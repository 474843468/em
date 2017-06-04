package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

public class BestRefundReq extends HttpReq {

  private static final long serialVersionUID = 8192624987579659037L;
  @Expose private String merchantId; // 商户号
  @Expose private String subMerchantId; // 商户子代码，如没有则填空
  @Expose private String merchantPwd; // 商户执行时需填入相应密码 ， 又称：交易key
  @Expose private String oldOrderNo; // 原扣款成功的订单号
  @Expose private String oldOrderReqNo; // 原扣款成功的请求支付流水号
  @Expose private String refundReqNo; // 退款流水号 该流水在商户处必须唯一
  @Expose private String refundReqDate; // 退款日期 yyyyMMddhhmmss
  @Expose private String transAmt; // 退款金额 单位为分，小于等于原订单金额
  @Expose private String ledgerDetail; // 分账信息 商户需要在结算时进行分账情况，需填写此字段
  @Expose private String channel = "05"; // 默认填05
  @Expose private String mac; // MAC校验域
  @Expose private String key; // 商户key

  public String getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(String merchantId) {
    this.merchantId = merchantId;
  }

  public String getSubMerchantId() {
    return subMerchantId;
  }

  public void setSubMerchantId(String subMerchantId) {
    this.subMerchantId = subMerchantId;
  }

  public String getMerchantPwd() {
    return merchantPwd;
  }

  public void setMerchantPwd(String merchantPwd) {
    this.merchantPwd = merchantPwd;
  }

  public String getOldOrderNo() {
    return oldOrderNo;
  }

  public void setOldOrderNo(String oldOrderNo) {
    this.oldOrderNo = oldOrderNo;
  }

  public String getOldOrderReqNo() {
    return oldOrderReqNo;
  }

  public void setOldOrderReqNo(String oldOrderReqNo) {
    this.oldOrderReqNo = oldOrderReqNo;
  }

  public String getRefundReqNo() {
    return refundReqNo;
  }

  public void setRefundReqNo(String refundReqNo) {
    this.refundReqNo = refundReqNo;
  }

  public String getRefundReqDate() {
    return refundReqDate;
  }

  public void setRefundReqDate(String refundReqDate) {
    this.refundReqDate = refundReqDate;
  }

  public String getTransAmt() {
    return transAmt;
  }

  public void setTransAmt(String transAmt) {
    this.transAmt = transAmt;
  }

  public String getLedgerDetail() {
    return ledgerDetail;
  }

  public void setLedgerDetail(String ledgerDetail) {
    this.ledgerDetail = ledgerDetail;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
