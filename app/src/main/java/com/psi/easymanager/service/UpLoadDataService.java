package com.psi.easymanager.service;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.dao.SmackUUIDRecordDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.SmackUUIDRecord;
import com.psi.easymanager.upload.UpLoadOperationLog;
import com.psi.easymanager.upload.UpLoadOrder;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: ylw
 * Date: 2016-06-06
 * Time: 09:32
 * 上传自定义商品和订单和会员信息充值记录
 */
public class UpLoadDataService extends Service {

  private OrderInfoTask mOrderInfoTask;//订单任务
  private Timer mTimer;
  //private ScheduledExecutorService mScheduledThreadPool;

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    mOrderInfoTask = new OrderInfoTask();
    //mScheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
    //每次都要把任务执行完成后再延迟固定时间后再执行下一次
    //mScheduledThreadPool.scheduleWithFixedDelay(mOrderInfoTask, 5, 30, TimeUnit.MINUTES);
    mTimer = new Timer();
    mTimer.schedule(mOrderInfoTask, 5 * 60 * 1000, 30 * 60 * 1000);
    return super.onStartCommand(intent, flags, startId);
  }

  /**
   * 上传订单
   */
  class OrderInfoTask extends TimerTask {
    @Override public void run() {
      Logger.i("uploadOrder");
      //上传订单
      UpLoadOrder upLoadOrder = UpLoadOrder.getInstance();
      if (!upLoadOrder.isUploading()) {
        upLoadOrder.upLoadOrderInfo();
        //upLoadOrder.closePool();
      }

      //上传自定义商品
      //UpLoadCustomProduct upLoadCustomProduct = UpLoadCustomProduct.getInstance();
      //upLoadCustomProduct.upLoadProdList();
      //upLoadCustomProduct.closePool();
      //上传操作记录
      UpLoadOperationLog upLoadOperationLog = UpLoadOperationLog.getInstance();
      upLoadOperationLog.upload();
      //upLoadOperationLog.closePool();

      //删除 半小时前SmackUUIDRecord
      deletePreSmackUUIDRecord();
    }
  }

  /**
   * 删除半小时前 SmackUUIDRecord
   */
  private void deletePreSmackUUIDRecord() {
    Date preRecord = new Date(System.currentTimeMillis() - 30 * 60 * 1000);
    List<SmackUUIDRecord> preRecordList = DaoServiceUtil.getSmackUUIDRecordService()
        .queryBuilder()
        .where(SmackUUIDRecordDao.Properties.OperateTime.lt(preRecord))
        .list();
    if (!preRecordList.isEmpty()) {
      SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
      db.beginTransaction();
      try {
        DaoServiceUtil.getSmackUUIDRecordService().delete(preRecordList);
        db.setTransactionSuccessful();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        db.endTransaction();
      }
    }
  }

  @Override public void onDestroy() {
    if (mTimer != null) {
      mTimer.cancel();
    }
    if (mOrderInfoTask != null) {
      mOrderInfoTask.cancel();
    }
    super.onDestroy();
  }
}