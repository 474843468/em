package com.psi.easymanager.chat;

import java.io.Serializable;

/**
 * Created by dorado on 2016/6/12.
 */
public class AppDetailsCollection implements Serializable {
  //双单位
  public static final String IS_TWO_UNIT_TURE = "0";
  //非双单位
  public static final String IS_TWO_UNIT_FALSE = "1";

  public static final String STATUS_ORIDINARY = "0";//正常
  public static final String STATUS_DELAY = "1";//延迟

  public static final String ORDER_STATUS_UNORDER = "0";//未下单
  public static final String ORDER_STATUS_ORDER = "1";//已下下单
  public static final String ORDER_STATUS_REFUND = "2";//退货

  public static final String GIFT_FALSE = "0";
  public static final String GIFT_TRUE = "1";

  //商品名
  private String mProdName;
  //商品id
  private String mProdObjId;
  //商品数量
  private int mNum;
  //是否为多单位
  private String isMultipleUnit;
  //多单位数量
  private double mMultipleNum;
  //状态
  private String mStatus;
  //下单状态
  private String mOrderStatus;
  //规格名
  private String mFormatName;
  //规格id
  private String mFormatObjId;
  //做法名
  private String mMethodName;
  //做法id
  private String mMethodObjId;
  //点餐单位
  private String mOrderUnit;
  //结账单位
  private String mUnit;
  //已上菜
  private boolean isServing;
  //折扣率
  private double mDiscRate;
  //是否为赠品
  private String mIsGift;
  //备注
  private String mRemarks;
  //是否为套餐
  private String mIsComboDetails;
  //objId
  private String mObjectId;

  public String getProdName() {
    return mProdName;
  }

  public void setProdName(String prodName) {
    mProdName = prodName;
  }

  public int getNum() {
    return mNum;
  }

  public void setNum(int num) {
    mNum = num;
  }


  public String getIsMultipleUnit() {
    return isMultipleUnit;
  }

  public void setIsMultipleUnit(String isMultipleUnit) {
    this.isMultipleUnit = isMultipleUnit;
  }

  public double getMultipleNum() {
    return mMultipleNum;
  }

  public void setMultipleNum(double multipleNum) {
    mMultipleNum = multipleNum;
  }

  public String getStatus() {
    return mStatus;
  }

  public void setStatus(String status) {
    mStatus = status;
  }

  public String getOrderStatus() {
    return mOrderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    mOrderStatus = orderStatus;
  }

  public String getFormatName() {
    return mFormatName;
  }

  public void setFormatName(String formatName) {
    mFormatName = formatName;
  }

  public String getMethodName() {
    return mMethodName;
  }

  public void setMethodName(String methodName) {
    mMethodName = methodName;
  }

  public String getOrderUnit() {
    return mOrderUnit;
  }

  public void setOrderUnit(String orderUnit) {
    mOrderUnit = orderUnit;
  }

  public String getUnit() {
    return mUnit;
  }

  public void setUnit(String unit) {
    mUnit = unit;
  }

  public boolean isServing() {
    return isServing;
  }

  public void setIsServing(boolean isServing) {
    this.isServing = isServing;
  }

  public double getDiscRate() {
    return mDiscRate;
  }

  public void setDiscRate(double discRate) {
    mDiscRate = discRate;
  }

  public String getProdObjId() {
    return mProdObjId;
  }

  public void setProdObjId(String prodObjId) {
    mProdObjId = prodObjId;
  }

  public String getFormatObjId() {
    return mFormatObjId;
  }

  public void setFormatObjId(String formatObjId) {
    mFormatObjId = formatObjId;
  }

  public String getMethodObjId() {
    return mMethodObjId;
  }

  public void setMethodObjId(String methodObjId) {
    mMethodObjId = methodObjId;
  }

  public String getIsGift() {
    return mIsGift;
  }

  public void setIsGift(String isGift) {
    mIsGift = isGift;
  }

  public String getRemarks() {
    return mRemarks;
  }

  public void setRemarks(String remarks) {
    mRemarks = remarks;
  }

  public String getIsComboDetails() {
    return mIsComboDetails;
  }

  public void setIsComboDetails(String isComboDetails) {
    mIsComboDetails = isComboDetails;
  }

  public void setServing(boolean serving) {
    isServing = serving;
  }

  public String getObjectId() {
    return mObjectId;
  }

  public void setObjectId(String objectId) {
    mObjectId = objectId;
  }
}
