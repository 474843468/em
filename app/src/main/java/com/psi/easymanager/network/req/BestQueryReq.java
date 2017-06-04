package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

public class BestQueryReq extends HttpReq {

  private static final long serialVersionUID = 8192624987579659037L;
  @Expose private String merchantId;    // 商户号
  @Expose private String orderNo;      // 订单号
  @Expose private String orderReqNo;    // 订单请求交易流水号
  @Expose private String orderDate;    // 订单日期 yyyyMMddhhmmss
  @Expose private String key;        // 商户key;
  @Expose private String mac;        // MAC校验域

  public BestQueryReq() {
  }

  public String getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(String merchantId) {
    this.merchantId = merchantId;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getOrderReqNo() {
    return orderReqNo;
  }

  public void setOrderReqNo(String orderReqNo) {
    this.orderReqNo = orderReqNo;
  }

  public String getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(String orderDate) {
    this.orderDate = orderDate;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }
}
