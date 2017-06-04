package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

public class HttpConsumeReq extends HttpReq<HttpConsumeReq> {
  @Expose private String vipId; //会员ID
  @Expose private String mobile; //手机号
  @Expose private String orderId; //订单编号
  @Expose private Double amount;//消费金额
  @Expose private String tradeNo;//交易流水号
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

  public String getVipId() {
    return vipId;
  }

  public void setVipId(String vipId) {
    this.vipId = vipId;
  }

  //@NotEmpty(message = "订单编码不能为空", groups = PadGroup.class)
  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  //@NotNull(message = "消费金额不能为空", groups = PadGroup.class)
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  //@NotEmpty(message = "消费流水不能为空", groups = PadGroup.class)
  public String getTradeNo() {
    return tradeNo;
  }

  public void setTradeNo(String tradeNo) {
    this.tradeNo = tradeNo;
  }

  //@NotEmpty(message = "手机号不能为空", groups = PadGroup.class)
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
}
