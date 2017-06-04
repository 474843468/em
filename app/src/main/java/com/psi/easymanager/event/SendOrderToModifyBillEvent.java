package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderInfo;

/**
 * Created by zjq don 2016/4/11.
 * 客单页面向改单页面传递订单信息
 */
public class SendOrderToModifyBillEvent {
  private PxOrderInfo mOrderInfo;

  public PxOrderInfo getOrderInfo() {
    return mOrderInfo;
  }

  public SendOrderToModifyBillEvent setOrderInfo(PxOrderInfo orderInfo) {
    mOrderInfo = orderInfo;
    return this;
  }
}
