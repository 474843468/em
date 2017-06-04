package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderInfo;

/**
 * Created by zjq on 2016/4/7.
 * 确认开单
 * 由开单页面向客单页面传递
 */
public class ConfirmStartBillEvent {
  private PxOrderInfo mOrderInfo;
  private boolean isFromWaiter;

  public PxOrderInfo getOrderInfo() {
    return mOrderInfo;
  }

  public ConfirmStartBillEvent setOrderInfo(PxOrderInfo orderInfo) {
    mOrderInfo = orderInfo;
    return this;
  }

  public boolean isFromWaiter() {
    return isFromWaiter;
  }

  public ConfirmStartBillEvent setFromWaiter(boolean fromWaiter) {
    isFromWaiter = fromWaiter;
    return this;
  }
}
