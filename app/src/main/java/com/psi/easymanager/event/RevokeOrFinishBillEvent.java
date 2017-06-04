package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderInfo;

/**
 * Created by zjq on 2016/4/13.
 * 选择撤单时，通知CashMenuFragment更新OrderInfoList和页面
 */
public class RevokeOrFinishBillEvent {
  private PxOrderInfo mOrderInfo;
  private boolean mIsAuto;

  public boolean isAuto() {
    return mIsAuto;
  }

  public RevokeOrFinishBillEvent setAuto(boolean auto) {
    mIsAuto = auto;
    return this;
  }

  public RevokeOrFinishBillEvent setOrderInfo(PxOrderInfo orderInfo) {
    mOrderInfo = orderInfo;
    return this;
  }

  public PxOrderInfo getOrderInfo() {
    return mOrderInfo;
  }
}
