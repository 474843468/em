package com.psi.easymanager.upload;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxOperationLog;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpOperationLogReq;
import com.psi.easymanager.network.resp.HttpResp;
import com.psi.easymanager.utils.NetUtils;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: ylw
 * Date: 2016-11-17
 * Time: 15:45
 * 上传操作日志
 */
public class UpLoadOperationLog {

  private ExecutorService mThread = null;//单线程上传
  private boolean isUploading = false;//是否有任务正在上传

  /**
   * 提供一个实例
   */
  public static UpLoadOperationLog getInstance() {
   return UploadOperationLogHolder.sInstance;
  }
  private static class UploadOperationLogHolder {
    private static final UpLoadOperationLog sInstance = new UpLoadOperationLog();
  }
  private void getThread() {
    if (mThread == null) {
      mThread = Executors.newSingleThreadExecutor();
    }
  }

  /**
   * 对外上传
   */
  public void upload() {
    if (isUploading) return;
    getThread();
    mThread.execute(new Runnable() {
      @Override public void run() {
        performUploadRecord();
      }
    });
  }

  private void performUploadRecord() {
    App app = (App) App.getContext();
    //没网
    if (!NetUtils.isConnected(app)) {
      return;
    }

    List<PxOperationLog> recordList = null;
    try {
      recordList = DaoServiceUtil.getOperationRecordService().queryAll();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (recordList == null || recordList.isEmpty()) return;

    User user = app.getUser();
    if (user == null) return;

    HttpOperationLogReq req = new HttpOperationLogReq();
    req.setUserId(user.getObjectId());
    req.setCompanyCode(user.getCompanyCode());
    req.setDataList(recordList);
    final List<PxOperationLog> logList = recordList;

    isUploading = true;
    new RestClient(RestClient.SYNC_CLIENT, 0, 1000, 15000, 5000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        Logger.e(responseString + "---" + throwable.toString());
        isUploading = false;
      }

      @Override protected void success(String responseString) {
        Gson gson = new Gson();
        HttpResp resp = gson.fromJson(responseString, HttpResp.class);
        if (resp.getStatusCode() == 1) { //真删除
          DaoServiceUtil.getOperationRecordService().delete(logList);
        }
        isUploading = false;
      }
    }.postOther(app, URLConstants.REFUND_OPERATE_RECORD, req);
  }

  /**
   * 关闭线程
   */
  public void closePool() {
    if (mThread != null) {
      mThread.shutdown();
      mThread = null;
    }
  }
}