package com.psi.easymanager.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.SysExceptionLogReq;
import com.psi.easymanager.network.resp.HttpResp;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.PackageUtils;
import com.psi.easymanager.utils.SPUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class UpLoadErrorService extends IntentService {
  public UpLoadErrorService() {
    super("UpLoadErrorService");
  }

  @Override protected void onHandleIntent(Intent intent) {
    User user = (User) intent.getSerializableExtra("user");
    final File crash = (File) intent.getSerializableExtra("crash");
    PackageUtils packageUtils = new PackageUtils(UpLoadErrorService.this);
    int localVersionCode = packageUtils.getLocalVersionCode();
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    //错误日志上传请求
    SysExceptionLogReq sysExceptionLogReq = new SysExceptionLogReq();
    sysExceptionLogReq.setAndroidVersion(Build.VERSION.SDK_INT);
    sysExceptionLogReq.setAppVersion(localVersionCode);
    sysExceptionLogReq.setDeviceName(Build.PRODUCT);
    sysExceptionLogReq.setCode(office.getCode());
    sysExceptionLogReq.setType(sysExceptionLogReq.EASYMANAGER_TYPE);
    sysExceptionLogReq.setName(office.getName());
    sysExceptionLogReq.setCompanyCode(user.getCompanyCode());
    sysExceptionLogReq.setUserId(user.getObjectId());
    //读取错误msg
    StringBuilder sb = new StringBuilder();
    BufferedReader br = null;
    FileReader fr = null;
    try {
      fr = new FileReader(crash);
      br = new BufferedReader(fr);
      String line = null;
      while ((line = br.readLine()) != null) {
        sb.append(line + "<br/>");
      }
    } catch (IOException e) {
      Logger.e(e.toString());
      e.printStackTrace();
    } finally {
      IOUtils.closeCloseables(br);
      IOUtils.closeCloseables(fr);
    }
    if (sb.length() < 20) {
      return;
    }
    final String errorTime = sb.substring(0, 19);
    //上传成功sp存的崩溃时间
    String OldErrorTime = (String) SPUtils.get(UpLoadErrorService.this, Constants.ERROR_TIME, "0");
    //日志已经上传成功过
    if (OldErrorTime.equals(errorTime)) {
      return;
    }
    sysExceptionLogReq.setCrashTime(errorTime);
    sysExceptionLogReq.setMsg(sb.toString());

    new RestClient(RestClient.SYNC_CLIENT,0, 1000, 10000, 10000) {
      @Override protected void start() {

      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
      }

      @Override protected void success(String responseString) {
       // Logger.e(responseString + "---------StatusCode应该是1-----");
        HttpResp httpResp = new Gson().fromJson(responseString, HttpResp.class);
        if (httpResp.getStatusCode() == HttpResp.SUCCESS) {
          //存储报错时间
          SPUtils.put(UpLoadErrorService.this, Constants.ERROR_TIME, errorTime);
          // crash.delete();
        }
      }
    }.postOther(UpLoadErrorService.this, URLConstants.UPLOAD_ERROR_LOG, sysExceptionLogReq);
  }
}