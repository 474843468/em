package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

public class HttpWeiXinReturnQueryReq extends HttpReq {
  @Expose private RefundQueryReqData refundQueryReqData;
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

  public RefundQueryReqData getRefundQueryReqData() {
    return refundQueryReqData;
  }

  public void setRefundQueryReqData(RefundQueryReqData refundQueryReqData) {
    this.refundQueryReqData = refundQueryReqData;
  }
}
