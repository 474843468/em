package com.psi.easymanager.event;

import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxVipCardInfo;
import com.psi.easymanager.module.PxVipInfo;

/**
 * Created by Administrator on 2016-10-22.
 */
public class VipLoginEvent {
  private boolean isSuccess;
  private PxVipInfo vipInfo;
  private PxVipCardInfo mVipCardInfo;
  private PxPaymentMode mPaymentMode;
  //private HttpIdCardResp.CardInfoBean mCardInfoBean;
  //
  //public HttpIdCardResp.CardInfoBean getCardInfoBean() {
  //  return mCardInfoBean;
  //}

  public VipLoginEvent(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public VipLoginEvent(boolean isSuccess, PxVipInfo vipInfo, PxVipCardInfo vipCardInfo,
      PxPaymentMode paymentMode) {
    this.isSuccess = isSuccess;
    this.vipInfo = vipInfo;
    mPaymentMode = paymentMode;
    mVipCardInfo = vipCardInfo;
  }
  //public VipLoginEvent(boolean isSuccess, PxVipInfo vipInfo, HttpIdCardResp.CardInfoBean cardInfoBean,
  //    PxPaymentMode paymentMode) {
  //  this.isSuccess = isSuccess;
  //  this.vipInfo = vipInfo;
  //  mPaymentMode = paymentMode;
  //  mCardInfoBean = cardInfoBean;
  //}

  public PxVipCardInfo getVipCardInfo() {
    return mVipCardInfo;
  }

  public PxPaymentMode getPaymentMode() {
    return mPaymentMode;
  }

  public PxVipInfo getVipInfo() {
    return vipInfo;
  }

  public boolean isSuccess() {
    return isSuccess;
  }
}
