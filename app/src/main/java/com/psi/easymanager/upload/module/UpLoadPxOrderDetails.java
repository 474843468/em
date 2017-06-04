package com.psi.easymanager.upload.module;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductMethodRef;
import java.io.Serializable;

/**
 * User: ylw
 * Date: 2016-05-23
 * Time: 11:53
 * 用于上传营业数据用的Details
 */
public class UpLoadPxOrderDetails implements Serializable {
  @Expose private PxOrderInfo order;        // 订单编号
  @Expose private UpLoadProduct product;        // 商品编号
  @Expose private Double num;        // 数量
  @Expose private Double multiNum;        // 多单位数量
  @Expose private String status;        // 商品状态(0:正常 1：延迟)
  @Expose private Integer discountRate;        // 折扣率(0-100)
  @Expose private String isDiscount;        // 是否打折（0：是 1：否  ）
  @Expose private PxProductFormatRel productFormatRel; // 商品规格
  @Expose private PxProductMethodRef productMethodRef; // 商品做法
  @Expose private String orderStatus;//下单状态(0:未下单,1:已下单,2:退货)
  @Expose private String isGift;//是否赠品(0 否 1 是)
  @Expose private String productName;//商品名
  @Expose private String formatName;//规格名

  @Expose private Double price;        // 价格
  @Expose private Double discPrice;//优惠金额
  @Expose private Double originPrice;//原价
  @Expose private Double finalPrice;//最终价格
  @Expose private Double finalMultiPrice;//最终多单位价格
  public Double getOriginPrice() {
    return originPrice;
  }

  public void setOriginPrice(Double originPrice) {
    this.originPrice = originPrice;
  }

  public Double getFinalMultiPrice() {
    return finalMultiPrice;
  }

  public void setFinalMultiPrice(Double finalMultiPrice) {
    this.finalMultiPrice = finalMultiPrice;
  }

  public Double getMultiNum() {
    return multiNum;
  }

  public void setMultiNum(Double multiNum) {
    this.multiNum = multiNum;
  }

  public Double getFinalPrice() {
    return finalPrice;
  }

  public void setFinalPrice(Double finalPrice) {
    this.finalPrice = finalPrice;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getFormatName() {
    return formatName;
  }

  public void setFormatName(String formatName) {
    this.formatName = formatName;
  }

  public String getIsGift() {
    return isGift;
  }

  public void setIsGift(String isGift) {
    this.isGift = isGift;
  }

  public Double getDiscPrice() {
    return discPrice;
  }

  public void setDiscPrice(Double discPrice) {
    this.discPrice = discPrice;
  }

  public PxProductFormatRel getProductFormatRel() {
    return productFormatRel;
  }

  public void setProductFormatRel(PxProductFormatRel productFormatRel) {
    this.productFormatRel = productFormatRel;
  }

  public PxProductMethodRef getProductMethodRef() {
    return productMethodRef;
  }

  public void setProductMethodRef(PxProductMethodRef productMethodRef) {
    this.productMethodRef = productMethodRef;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public PxOrderInfo getOrder() {
    return order;
  }

  public void setOrder(PxOrderInfo order) {
    this.order = order;
  }

  public UpLoadProduct getProduct() {
    return product;
  }

  public void setProduct(UpLoadProduct product) {
    this.product = product;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getNum() {
    return num;
  }

  public void setNum(Double num) {
    this.num = num;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getDiscountRate() {
    return discountRate;
  }

  public void setDiscountRate(Integer discountRate) {
    this.discountRate = discountRate;
  }

  public String getIsDiscount() {
    return isDiscount;
  }

  public void setIsDiscount(String isDiscount) {
    this.isDiscount = isDiscount;
  }
}