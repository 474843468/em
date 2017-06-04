package com.psi.easymanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.psi.easymanager.dao.PxPrinterInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.PrinterPingErrorEvent;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.utils.PingUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by psi on 2016/5/11.
 * 打印机检测是否连接
 */
public class PrinterService extends Service {
  private List<PxPrinterInfo> mDeviceList;
  private MyTask mMyTask;
 private Map<String, String> mStatusMap;
  private Timer mTimer;


  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    startPingStatus();
    return super.onStartCommand(intent, flags, startId);
  }

  /**
   * 执行任务ping
   */
  private void startPingStatus() {
    mDeviceList = DaoServiceUtil.getPrinterInfoService()
        .queryBuilder()
        .where(PxPrinterInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPrinterInfoDao.Properties.Status.eq(PxPrinterInfo.ENABLE))
        .list();
    if (!mDeviceList.isEmpty()) {
      mStatusMap = new HashMap<>();
      mMyTask = new MyTask();
      mTimer = new Timer();
      mTimer.schedule(mMyTask, 15 * 1000, 5 * 1000);
    }
  }

  //@formatter:on
 private class MyTask extends TimerTask {
    @Override public void run() {
      boolean isAllConnected = true;
      Boolean hasChange = null;//是否有状态变化
      Runtime runtime = Runtime.getRuntime();

      for (PxPrinterInfo printerInfo : mDeviceList) {
        String ip = printerInfo.getIpAddress();
        String currentStatus = PxPrinterInfo.CONNECTED;
        //2.
        boolean isConnect = PingUtils.isConnect(runtime, ip);
        Log.w("Print", ip + "---:" + isConnect);
        if (!isConnect) {
          currentStatus = PxPrinterInfo.UNCONNECTED;
          //只要有一个未连接
          isAllConnected = false;
        }
        //是否有变化 一旦有其中一变化 就不必在判断后面的
        if (hasChange == null || !hasChange) {
          hasChange = (!currentStatus.equals(mStatusMap.get(ip)));
        }
        mStatusMap.put(ip, currentStatus);
      }
      //有变化再发事件
      if (hasChange) {
        //发MainActivity
        EventBus.getDefault().post(new PrinterPingErrorEvent(isAllConnected));
      }
      //发厨房打印
      PrintEventManager.getManager().postPrinterConnectStatus(mStatusMap, hasChange);
    }
  }

  @Override public void onDestroy() {
    if (mTimer != null) {
      mTimer.cancel();
    }
    if (mMyTask != null) {
      mMyTask.cancel();
    }
    super.onDestroy();
  }
}
