package com.psi.easymanager.print;

import com.psi.easymanager.event.PrintLabelQueueEvent;
import com.psi.easymanager.event.PrintQueueEvent;
import com.psi.easymanager.event.SwitchBTDeviceEvent;
import com.psi.easymanager.print.bt.BTPrintTask;
import com.psi.easymanager.print.bt.event.IBTPrintEvent;
import com.psi.easymanager.print.net.event.IPrintConnectStatus;
import com.psi.easymanager.print.net.event.IPrintEvent;
import java.util.Map;

/**
 * User: ylw
 * Date: 2016-12-12
 * Time: 17:59
 * 打印事件分发
 */
public class PrintEventManager {
  public static final int PRINT_TASK = 0;//普通打印任务
  public static final int PRINT_LABEL_TASK = 1;//标签打印任务
  public static final int SWITCH_LABEL = 3;//开关标签打印机
  private IPrintConnectStatus mConnectStatus;
  private IPrintEvent mIPrintEvent;
  private IBTPrintEvent mIBTPrintEvent;

  private PrintEventManager() {

  }

  public static PrintEventManager getManager() {
    return ManagerHolder.sInstance;
  }

  private static class ManagerHolder {
    private static final PrintEventManager sInstance = new PrintEventManager();
  }

  /**
   * 蓝牙打印 各项任务、标签打印、开钱箱
   */
  public void registBTPrintEvent(IBTPrintEvent printEvent) {
    this.mIBTPrintEvent = printEvent;
  }

  public void unRegistBTPrintEvent(IBTPrintEvent printEvent) {
    this.mIBTPrintEvent = null;
  }

  public void postBTPrintEvent(BTPrintTask task) {
    if (mIBTPrintEvent == null || task == null) return;
    mIBTPrintEvent.receiveBTPrintTask(task);
  }

  public void postSwitchEvent(SwitchBTDeviceEvent event) {
    if (mIBTPrintEvent != null) {
      mIBTPrintEvent.receiveSwitchBTDevice(event);
    }
  }

  public void postOpenCashBox() {
    if (mIBTPrintEvent != null) {
      mIBTPrintEvent.receiveOpenCashBox();
    }
  }

  /**
   * 打印 各项任务、标签打印、开钱箱
   */
  public void registPrintEvent(IPrintEvent printEvent) {
    this.mIPrintEvent = printEvent;
  }

  public void unRegistPrintEvent(IPrintEvent printEvent) {
    this.mIPrintEvent = null;
  }

  public void postPrintEvent(int mode, Object obj) {
    if (null == mIPrintEvent || null == obj) return;
    switch (mode) {
      case PRINT_TASK:
        mIPrintEvent.receivePrintTask((PrintQueueEvent) obj);
        break;
      case PRINT_LABEL_TASK:
        mIPrintEvent.receiveLabelPrintTask((PrintLabelQueueEvent) obj);
        break;
      case SWITCH_LABEL:
        mIPrintEvent.switchLabelPrint((boolean) obj);
    }
  }

  public void postOpenCash() {
    if (mIBTPrintEvent != null) {
      mIBTPrintEvent.receiveOpenCashBox();
    }
    if (mIPrintEvent != null) {
      mIPrintEvent.receiveOpenCashBoxEvent();
    }
  }

  /**
   * 打印机连接状态 regist
   */
  public void registPrinterConnectStatus(IPrintConnectStatus connectStatus) {
    this.mConnectStatus = connectStatus;
  }

  public void unRegistPrinterConnectStatus(IPrintConnectStatus connectStatus) {
    this.mConnectStatus = null;
  }

  public void postPrinterConnectStatus(Map<String, String> map, boolean isAllConnected) {
    if (mConnectStatus != null) {
      mConnectStatus.dispatchConnectStatus(map, isAllConnected);
    }
  }
}