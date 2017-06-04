package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BestPayReq extends HttpReq {

  private static final long serialVersionUID = 8192624987579659037L;
  @Expose private String merchantId; // 商户号
  @Expose private String subMerchantId; // 子商户号
  @Expose private String barcode; // 条形码，商户POS扫描用户客户端条形码
  @Expose private String orderNo; // 订单号  由商户平台提供，支持纯数字、纯字母、字母+数字组成，全局唯一（如果需要使用条码退款业务，订单号必须为偶数位）
  @Expose private String orderReqNo; // 订单请求交易流水号 同上
  @Expose private String channel; // 渠道
  @Expose private String busiType;// 业务类型
  @Expose private String orderDate; // 订单日期 yyyyMMddhhmmss
  @Expose private String orderAmt; // 订单总金额 单位：分订单总金额 = 产品金额+附加金额
  @Expose private String productAmt; // 产品金额 单位：分
  @Expose private String attachAmt; // 附加金额 单位：分
  @Expose private String goodsName; // 商品名称
  @Expose private String storeId; // 门店号
  @Expose private String backUrl; // 后台返回地址 商户提供的用于异步接收交易返回结果的后台url，若不需要后台返回，可不填，若需要后台返回，请保障地址可用
  @Expose private String ledgerDetail; // 分账信息 商户需要在结算时进行分账情况，需填写此字段，详情见接口说明分账明细
  @Expose private String attach; // 附加信息
  @Expose private String mac; // MAC校验域
  @Expose private String key; // 商户key;

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

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
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

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getBusiType() {
    return busiType;
  }

  public void setBusiType(String busiType) {
    this.busiType = busiType;
  }

  public String getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(String orderDate) {
    this.orderDate = orderDate;
  }

  public String getGoodsName() {
    return goodsName;
  }

  public void setGoodsName(String goodsName) {
    this.goodsName = goodsName;
  }

  public String getStoreId() {
    return storeId;
  }

  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }

  public String getBackUrl() {
    return backUrl;
  }

  public void setBackUrl(String backUrl) {
    this.backUrl = backUrl;
  }

  public String getLedgerDetail() {
    return ledgerDetail;
  }

  public void setLedgerDetail(String ledgerDetail) {
    this.ledgerDetail = ledgerDetail;
  }

  public String getAttach() {
    return attach;
  }

  public void setAttach(String attach) {
    this.attach = attach;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public String getOrderAmt() {
    return orderAmt;
  }

  public void setOrderAmt(String orderAmt) {
    this.orderAmt = orderAmt;
  }

  public String getProductAmt() {
    return productAmt;
  }

  public void setProductAmt(String productAmt) {
    this.productAmt = productAmt;
  }

  public String getAttachAmt() {
    return attachAmt;
  }

  public void setAttachAmt(String attachAmt) {
    this.attachAmt = attachAmt;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
