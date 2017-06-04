package com.psi.easymanager.event;

/**
 * Created by dorado on 2016/8/12.
 */
public class UpdateProdInfoListStatusEvent {
  private boolean isFromWaiter;

  public boolean isFromWaiter() {
    return isFromWaiter;
  }

  public UpdateProdInfoListStatusEvent setFromWaiter(boolean fromWaiter) {
    isFromWaiter = fromWaiter;
    return this;
  }
}
