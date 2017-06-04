/**
 * Copyright &copy; 2012-2014
 */
package com.psi.easymanager.pay.query.module;

import java.io.Serializable;

/**
 * 网络支付交易流水Entity
 *
 * @author zh
 * @version 2016-10-17
 */
public class PxNetPayRecord implements Serializable {
  public static final String TYPE_ALIPAY = "1";
  public static final String TYPE_WEIXIN = "0";
  public static final String TYPE_BESTPAY = "2";

  public static final String STATUS_PAY = "0";
  public static final String STATUS_REFUND = "1";

  private String tradeNo;    // 账单流水号
  private Double fee;    // 支付金额
  private String type;    // 支付类型  (0:微信 1：支付宝 2：翼支付)
  private String status;    // 交易流水状态(0: 支付 1：退款)
  private String orderNo;    // 订单编号
  private String selfTradeNo;//本地生成的流水号
  private String updateDate;//交易时间
  public String getSelfTradeNo() {
    return selfTradeNo;
  }

  public void setSelfTradeNo(String selfTradeNo) {
    this.selfTradeNo = selfTradeNo;
  }

  public String getTradeNo() {
    return tradeNo;
  }

  public void setTradeNo(String tradeNo) {
    this.tradeNo = tradeNo;
  }

  public Double getFee() {
    return fee;
  }

  public void setFee(Double fee) {
    this.fee = fee;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(String updateDate) {
    this.updateDate = updateDate;
  }
}