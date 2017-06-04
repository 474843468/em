package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderInfo;

/**
 * Created by zjq on 2016/5/31.
 * 通知CashBill更新OrderInfo
 */
public class CashBillUpdateOrderEvent {
  private PxOrderInfo mOrderInfo;

  public PxOrderInfo getOrderInfo() {
    return mOrderInfo;
  }

  public CashBillUpdateOrderEvent setOrderInfo(PxOrderInfo orderInfo) {
    mOrderInfo = orderInfo;
    return this;
  }
}
