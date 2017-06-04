package com.psi.easymanager.pay.bestpay;

import java.io.Serializable;

public class BestPaySuccessResult implements Serializable {

  private static final long serialVersionUID = 9126872823515171789L;

  /**
   * 支付中
   */
  public static final String UNDERWAY = "A";
  /**
   * 支付成功
   */
  public static final String SUCCESS = "B";
  /**
   * 支付失败
   */
  public static final String DEFEATED = "C";
  /**
   * 支付超时
   */
  public static final String TIMEOUT = "D";
  /**
   * 订单作废
   */
  public static final String CANCEL = "G"; //

  /**
   * 无退款
   */
  public static final String RETURN_EMPTY = "0";
  /**
   * 已退款
   */
  public static final String RETURN_SUCCESS = "1";
  /**
   * 部分退款
   */
  public static final String RETURN_PART = "2";
  /**
   * 已冲正
   */
  public static final String REVERSE = "3";

  private String merchantId;
  private String orderNo;// 查询到的商户订单号
  private String orderReqNo;// 查询到的商户请求流水号
  private String orderDate;// 商户下单时间
  private String ourTransNo;// 翼支付生成的内部流水号
  private Double transAmt;// 金额分
  private String transStatus;// A：请求（支付中） B：成功（支付成功） C：失败 G:订单作废
  private String encodeType;// 签名方式 1代表MD5 3代表RSA 9代表CA 默认为1
  private String sign;

  private String refundReqNo;// 退款请求流水号
  private String oldOrderNo;// 原订单号
  private String refundFlag; // 退款标识 0 代表没有退款，1 已退款 2 部分退款 3 已冲正
  private String customerID; // 支付手机号

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

  public String getOurTransNo() {
    return ourTransNo;
  }

  public void setOurTransNo(String ourTransNo) {
    this.ourTransNo = ourTransNo;
  }

  public Double getTransAmt() {
    return transAmt;
  }

  public void setTransAmt(Double transAmt) {
    this.transAmt = transAmt;
  }

  public String getTransStatus() {
    return transStatus;
  }

  public void setTransStatus(String transStatus) {
    this.transStatus = transStatus;
  }

  public String getEncodeType() {
    return encodeType;
  }

  public void setEncodeType(String encodeType) {
    this.encodeType = encodeType;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getRefundFlag() {
    return refundFlag;
  }

  public void setRefundFlag(String refundFlag) {
    this.refundFlag = refundFlag;
  }

  public String getCustomerID() {
    return customerID;
  }

  public void setCustomerID(String customerID) {
    this.customerID = customerID;
  }

  public String getRefundReqNo() {
    return refundReqNo;
  }

  public void setRefundReqNo(String refundReqNo) {
    this.refundReqNo = refundReqNo;
  }

  public String getOldOrderNo() {
    return oldOrderNo;
  }

  public void setOldOrderNo(String oldOrderNo) {
    this.oldOrderNo = oldOrderNo;
  }
}
