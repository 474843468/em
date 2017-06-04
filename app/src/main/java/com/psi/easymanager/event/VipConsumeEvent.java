package com.psi.easymanager.event;

import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxVipCardInfo;
import com.psi.easymanager.module.PxVipInfo;

/**
 * User: ylw
 * Date: 2016-10-23
 * Time: 11:04
 * 会员消费请求事件
 */
public class VipConsumeEvent {
  private boolean isSuccess;
  private PxPaymentMode mPaymentMode;
  private double received;//会员实收
  private PxVipInfo mVipInfo;//会员信息
  private PxVipCardInfo mVipCardInfo;
  private String tradeNo;//交易流水号

  public VipConsumeEvent(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public VipConsumeEvent(boolean isSuccess, PxPaymentMode paymentMode, double received,
      PxVipInfo vipInfo, PxVipCardInfo vipCardInfo, String tradeNo) {
    this.isSuccess = isSuccess;
    this.mPaymentMode = paymentMode;
    this.received = received;
    this.mVipInfo = vipInfo;
    this.tradeNo = tradeNo;
    mVipCardInfo = vipCardInfo;
  }

  public PxVipCardInfo getVipCardInfo() {
    return mVipCardInfo;
  }

  public String getTradeNo() {
    return tradeNo;
  }

  public PxVipInfo getVipInfo() {
    return mVipInfo;
  }

  public double getReceived() {
    return received;
  }

  public PxPaymentMode getPaymentMode() {
    return mPaymentMode;
  }

  public void setPaymentMode(PxPaymentMode paymentMode) {
    mPaymentMode = paymentMode;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setSuccess(boolean success) {
    isSuccess = success;
  }
}