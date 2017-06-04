package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * Created by lj on 2017-01-09.
 */
public class HttpIdCardConsumeReq extends HttpReq{
  @Expose
  private String cardId; //会员ID
  @Expose
  private String mobile; //手机号
  @Expose
  private String orderId; //订单编号
  @Expose
  private Double amount;//消费金额
  @Expose
  private String tradeNo;//交易流水号

  public String getCardId() {
    return cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getTradeNo() {
    return tradeNo;
  }

  public void setTradeNo(String tradeNo) {
    this.tradeNo = tradeNo;
  }
}
