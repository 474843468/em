package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

public class HttpWeiXinPayReq extends HttpReq {

  @Expose private ScanPayReqData scanPayReqData;
  @Expose private String orderNo; // 订单号
  @Expose private String selfTradeNo; //收银机产生的交易流水号

  public String getSelfTradeNo() {
    return selfTradeNo;
  }

  public void setSelfTradeNo(String selfTradeNo) {
    this.selfTradeNo = selfTradeNo;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public ScanPayReqData getScanPayReqData() {
    return scanPayReqData;
  }

  public void setScanPayReqData(ScanPayReqData scanPayReqData) {
    this.scanPayReqData = scanPayReqData;
  }
}
