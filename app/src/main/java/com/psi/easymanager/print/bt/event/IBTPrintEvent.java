package com.psi.easymanager.print.bt.event;

import com.psi.easymanager.event.SwitchBTDeviceEvent;
import com.psi.easymanager.print.bt.BTPrintTask;

/**
 * 作者：${ylw} on 2017-01-06 16:41
 */
public interface IBTPrintEvent {
  void receiveBTPrintTask(BTPrintTask task);

  void receiveSwitchBTDevice(SwitchBTDeviceEvent event);

  void receiveOpenCashBox();
}
