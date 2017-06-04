package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

public class AlipaySearchReq extends HttpReq {
  @Expose
  private String outTradeNo; // 订单号 流水号
  @Expose private String selfTradeNo; //收银机产生的交易流水号

  public String getSelfTradeNo() {
    return selfTradeNo;
  }

  public void setSelfTradeNo(String selfTradeNo) {
    this.selfTradeNo = selfTradeNo;
  }

  public String getOutTradeNo() {
    return outTradeNo;
  }

  public void setOutTradeNo(String outTradeNo) {
    this.outTradeNo = outTradeNo;
  }
}
