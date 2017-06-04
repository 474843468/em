package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

public class AlipayRefundReq extends HttpReq {
  @Expose private String outTradeNo; // 订单流水号
  @Expose private String totalAmount; // 订单总价
  @Expose private String refundReason; // 退款原因
  @Expose private String outRequestNo; // 商户退款请求号
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

  public String getOutRequestNo() {
    return outRequestNo;
  }

  public void setOutRequestNo(String outRequestNo) {
    this.outRequestNo = outRequestNo;
  }

  public String getOutTradeNo() {
    return outTradeNo;
  }

  public void setOutTradeNo(String outTradeNo) {
    this.outTradeNo = outTradeNo;
  }

  public String getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(String totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getRefundReason() {
    return refundReason;
  }

  public void setRefundReason(String refundReason) {
    this.refundReason = refundReason;
  }
}
