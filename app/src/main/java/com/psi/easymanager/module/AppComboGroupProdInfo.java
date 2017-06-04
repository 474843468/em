package com.psi.easymanager.module;

import java.io.Serializable;

/**
 * Created by dorado on 2016/8/18.
 */
public class AppComboGroupProdInfo implements Serializable {

  public static final String TYPE_REQUIRED = "1";//必选商品
  public static final String TYPE_OPTIONAL = "0";//可选商品

  //商品
  private PxProductInfo mProductInfo;
  //数量
  private Integer mNum;
  //重量
  private Double mWeight;
  //规格
  private PxFormatInfo mFormatInfo;
  //类型
  private String type;
  //是否选择
  private boolean isSelected;


  public PxProductInfo getProductInfo() {
    return mProductInfo;
  }

  public void setProductInfo(PxProductInfo productInfo) {
    mProductInfo = productInfo;
  }

  public Integer getNum() {
    return mNum;
  }

  public void setNum(Integer num) {
    mNum = num;
  }

  public Double getWeight() {
    return mWeight;
  }

  public void setWeight(Double weight) {
    mWeight = weight;
  }

  public PxFormatInfo getFormatInfo() {
    return mFormatInfo;
  }

  public void setFormatInfo(PxFormatInfo formatInfo) {
    mFormatInfo = formatInfo;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

}
