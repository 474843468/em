package com.psi.easymanager.event;

/**
 * User: ylw
 * Date: 2016-10-23
 * Time: 18:05
 * FIXME
 */
public class VipRefundEvent {
  private boolean isSuccess;//

  public boolean isSuccess() {
    return isSuccess;
  }

  public VipRefundEvent(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }
}