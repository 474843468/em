package com.psi.easymanager.event;

/**
 * Created by dorado on 2016/6/20.
 */
public class ShowProgressEvent {
  private String mProdName;

  public ShowProgressEvent setProdName(String prodName) {
    mProdName = prodName;
    return this;
  }

  public String getProdName() {
    return mProdName;
  }
}