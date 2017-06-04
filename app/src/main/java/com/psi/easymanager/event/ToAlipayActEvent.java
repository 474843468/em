package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPaymentMode;

/**
 * User: ylw
 * Date: 2016-08-12
 * Time: 18:25
 * 结账页面 跳支付宝event
 */
public class ToAlipayActEvent {
  private PxOrderInfo mOrderInfo;
  private double mWaitPayAmount;
  private PxPaymentMode mPaymentMode;

  public ToAlipayActEvent(PxOrderInfo orderInfo, double waitPayAmount, PxPaymentMode paymentMode) {
    mOrderInfo = orderInfo;
    mWaitPayAmount = waitPayAmount;
    mPaymentMode = paymentMode;
  }

  public PxPaymentMode getPaymentMode() {
    return mPaymentMode;
  }

  public void setPaymentMode(PxPaymentMode paymentMode) {
    mPaymentMode = paymentMode;
  }

  public double getWaitPayAmount() {
    return mWaitPayAmount;
  }

  public void setWaitPayAmount(double waitPayAmount) {
    mWaitPayAmount = waitPayAmount;
  }

  public PxOrderInfo getOrderInfo() {
    return mOrderInfo;
  }

  public void setOrderInfo(PxOrderInfo orderInfo) {
    mOrderInfo = orderInfo;
  }
}