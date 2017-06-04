package com.psi.easymanager.upload.module;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: ylw
 * Date: 2016-10-18
 * Time: 13:43
 * 上传支付信息
 */
public class UploadPayInfo implements Serializable {
  @Expose private long payTime;//支付时间
  @Expose private BigDecimal received;//实收
  @Expose private String voucherCode;//凭证吗
  @Expose private String paymentId;//支付方式类型
  @Expose private BigDecimal change;//找零
  @Expose private String tradeNo;    // 账单流水号
  @Expose private String paymentType;    // 支付方式类型
  @Expose private String paymentName;    // 支付方式名称
  @Expose private String salesAmount;    // 是否计算入销售额(0:是 1：否)
  @Expose private Double payPrivilege;//支付类优惠
  @Expose private String ticketCode;//验券码

  public Double getPayPrivilege() {
    return payPrivilege;
  }

  public void setPayPrivilege(Double payPrivilege) {
    this.payPrivilege = payPrivilege;
  }

  public String getTicketCode() {
    return ticketCode;
  }

  public void setTicketCode(String ticketCode) {
    this.ticketCode = ticketCode;
  }

  public String getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(String paymentType) {
    this.paymentType = paymentType;
  }

  public String getPaymentName() {
    return paymentName;
  }

  public void setPaymentName(String paymentName) {
    this.paymentName = paymentName;
  }

  public String getSalesAmount() {
    return salesAmount;
  }

  public void setSalesAmount(String salesAmount) {
    this.salesAmount = salesAmount;
  }

  public String getTradeNo() {
    return tradeNo;
  }

  public void setTradeNo(String tradeNo) {
    this.tradeNo = tradeNo;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public long getPayTime() {
    return payTime;
  }

  public void setPayTime(long payTime) {
    this.payTime = payTime;
  }

  public BigDecimal getReceived() {
    return received;
  }

  public void setReceived(BigDecimal received) {
    this.received = received;
  }

  public String getVoucherCode() {
    return voucherCode;
  }

  public void setVoucherCode(String voucherCode) {
    this.voucherCode = voucherCode;
  }

  public BigDecimal getChange() {
    return change;
  }

  public void setChange(BigDecimal change) {
    this.change = change;
  }
}