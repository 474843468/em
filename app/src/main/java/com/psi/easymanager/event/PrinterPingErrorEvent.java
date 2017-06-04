package com.psi.easymanager.event;

/**
 * Created by dorado on 2016/8/28.
 */
public class PrinterPingErrorEvent {
  private boolean isAllConnect;

  public boolean isAllConnect() {
    return isAllConnect;
  }

  public PrinterPingErrorEvent(boolean isAllConnect) {
    this.isAllConnect = isAllConnect;
  }

  public PrinterPingErrorEvent setAllConnect(boolean allConnect) {
    isAllConnect = allConnect;
    return this;
  }
}
