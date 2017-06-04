package com.psi.easymanager.chat;

import java.io.Serializable;

/**
 * Created by dorado on 2016/6/11.
 */
public class AppConfirmOrderDetails implements Serializable {
  //商品id
  private String mProdId;
  //规格id
  private String mFormatId;
  //做法id
  private String mMethodId;
  //数量
  private int mNum;
  //多单位数量
  private double mMultNum;
  //是否延迟(0:不延迟,1:延迟)
  private int isDelay;
  //备注
  private String mRemarks;

  public String getProdId() {
    return mProdId;
  }

  public void setProdId(String prodId) {
    mProdId = prodId;
  }

  public String getFormatId() {
    return mFormatId;
  }

  public void setFormatId(String formatId) {
    mFormatId = formatId;
  }

  public String getMethodId() {
    return mMethodId;
  }

  public void setMethodId(String methodId) {
    mMethodId = methodId;
  }

  public int getNum() {
    return mNum;
  }

  public void setNum(int num) {
    mNum = num;
  }

  public double getMultNum() {
    return mMultNum;
  }

  public void setMultNum(double multNum) {
    mMultNum = multNum;
  }

  public int getIsDelay() {
    return isDelay;
  }

  public void setIsDelay(int isDelay) {
    this.isDelay = isDelay;
  }

  public String getRemarks() {
    return mRemarks;
  }

  public void setRemarks(String remarks) {
    mRemarks = remarks;
  }
}
