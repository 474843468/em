package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

public class HttpWeiXinReverseReq extends HttpReq {
  private static final long serialVersionUID = -8027784659884126950L;
  @Expose private String selfTradeNo;

  public String getSelfTradeNo() {
    return selfTradeNo;
  }

  public void setSelfTradeNo(String selfTradeNo) {
    this.selfTradeNo = selfTradeNo;
  }
}
