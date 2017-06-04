package com.psi.easymanager.module;

import java.io.Serializable;

/**
 * Created by dorado on 2017/3/8.
 */

public class TemporaryMergeDetails implements Serializable {
  private PxOrderInfo mOrderInfo;
  private PxProductInfo mPxProductInfo;
  private PxFormatInfo mFormatInfo;
  private PxMethodInfo mPxMethodInfo;
  private PxOptReason mReason;
  private String mStatus;
  private String mInCombo;
  private String mRemarks;

  public PxOrderInfo getOrderInfo() {
    return mOrderInfo;
  }

  public void setOrderInfo(PxOrderInfo orderInfo) {
    mOrderInfo = orderInfo;
  }

  public PxProductInfo getPxProductInfo() {
    return mPxProductInfo;
  }

  public void setPxProductInfo(PxProductInfo pxProductInfo) {
    mPxProductInfo = pxProductInfo;
  }

  public PxFormatInfo getFormatInfo() {
    return mFormatInfo;
  }

  public void setFormatInfo(PxFormatInfo formatInfo) {
    mFormatInfo = formatInfo;
  }

  public PxMethodInfo getPxMethodInfo() {
    return mPxMethodInfo;
  }

  public void setPxMethodInfo(PxMethodInfo pxMethodInfo) {
    mPxMethodInfo = pxMethodInfo;
  }

  public PxOptReason getReason() {
    return mReason;
  }

  public void setReason(PxOptReason reason) {
    mReason = reason;
  }

  public String getStatus() {
    return mStatus;
  }

  public void setStatus(String status) {
    mStatus = status;
  }

  public String getInCombo() {
    return mInCombo;
  }

  public void setInCombo(String inCombo) {
    mInCombo = inCombo;
  }

  public String getRemarks() {
    return mRemarks;
  }

  public void setRemarks(String remarks) {
    mRemarks = remarks;
  }
}
