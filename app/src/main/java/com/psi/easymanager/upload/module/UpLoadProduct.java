package com.psi.easymanager.upload.module;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxProductCategory;
import java.io.Serializable;

/**
 * User: ylw
 * Date: 2016-06-09
 * Time: 17:36
 * FIXME
 */
public class UpLoadProduct implements Serializable{
  public static final String SINGLE_UNIT = "1";    //单单位
  public static final String MULTI_UNIT = "0";    //多单位

  public static final String ALLOW_DISCOUNT = "0";        //允许打折
  public static final String NOT_ALLOW_DISCOUNT = "1";    //不允许打折

  public static final String COMMODITY = "1";        // 不设为赠品
  public static final String GIFT = "0";            // 设为赠品

  public static final String ALLOW_PRINT = "0";            //出单
  public static final String NOT_ALLOW_PRINT = "1";        //不出单

  public static final String ALLOW_CHANGEPRICE = "0";        //允许改价
  public static final String NOT_ALLOW_CHANGEPRICE = "1";    //不允许改价

  public static final String NORMAL = "0";        //正常状态
  public static final String HALT_SALES = "1";    //停售状态
  @Expose private String name;        // 商品名称
  @Expose private PxProductCategory category;        // 分类编号
  @Expose private String py;        // 中文拼音首字母缩写（由程序生成）
  @Expose private String code;        // 商品编码
  @Expose private Double price;        // 商品单价
  @Expose private Double vipPrice;        // 会员特价（默认与商品价格一致）
  @Expose private String unit;        // 结账单位
  @Expose private String multipleUnit;        // 是否多单位菜（0：是 1：否）
  @Expose private String orderUnit;        // 点菜单位
  @Expose private String isDiscount;        // 允许打折（0：允许 1：不允许）
  @Expose private String isGift;        // 是否为赠品(0：是  1 ：否)
  @Expose private String isPrint;        // 商品发送后厨是否出单(0:出单 1：不出单)
  @Expose private String changePrice;        // 是否允许收银改价（0：是 1：否）
  @Expose private String status;        // 商品状态 (0:正常 1：停售)
  @Expose String id;
  @Expose private String[] plan;//传菜方案ObjId

  public String[] getPlan() {
    return plan;
  }

  public void setPlan(String[] plan) {
    this.plan = plan;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PxProductCategory getCategory() {
    return category;
  }

  public void setCategory(PxProductCategory category) {
    this.category = category;
  }

  public String getPy() {
    return py;
  }

  public void setPy(String py) {
    this.py = py;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getVipPrice() {
    return vipPrice;
  }

  public void setVipPrice(Double vipPrice) {
    this.vipPrice = vipPrice;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getMultipleUnit() {
    return multipleUnit;
  }

  public void setMultipleUnit(String multipleUnit) {
    this.multipleUnit = multipleUnit;
  }

  public String getOrderUnit() {
    return orderUnit;
  }

  public void setOrderUnit(String orderUnit) {
    this.orderUnit = orderUnit;
  }

  public String getIsDiscount() {
    return isDiscount;
  }

  public void setIsDiscount(String isDiscount) {
    this.isDiscount = isDiscount;
  }

  public String getIsGift() {
    return isGift;
  }

  public void setIsGift(String isGift) {
    this.isGift = isGift;
  }

  public String getIsPrint() {
    return isPrint;
  }

  public void setIsPrint(String isPrint) {
    this.isPrint = isPrint;
  }

  public String getChangePrice() {
    return changePrice;
  }

  public void setChangePrice(String changePrice) {
    this.changePrice = changePrice;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}  