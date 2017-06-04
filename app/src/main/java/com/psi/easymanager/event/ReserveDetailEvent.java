package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderInfo;

/**
 * User: ylw
 * Date: 2016-09-13
 * Time: 14:12
 * 预订单详情 可能是新增的 也可能是修改的
 */
public class ReserveDetailEvent {
  private PxOrderInfo mReserveOrder;
  private boolean isAdd;
  private boolean isModify;

  public ReserveDetailEvent(boolean isModify, PxOrderInfo reserveOrder) {
    this.isModify = isModify;
    mReserveOrder = reserveOrder;
  }

  public ReserveDetailEvent(PxOrderInfo reserveOrder, boolean isAdd) {
    mReserveOrder = reserveOrder;
    this.isAdd = isAdd;
  }

  public boolean isModify() {
    return isModify;
  }

  public boolean isAdd() {
    return isAdd;
  }

  public ReserveDetailEvent(PxOrderInfo reserveOrder) {
    mReserveOrder = reserveOrder;
  }

  public PxOrderInfo getReserveOrder() {
    return mReserveOrder;
  }

  public void setReserveOrder(PxOrderInfo reserveOrder) {
    mReserveOrder = reserveOrder;
  }
}