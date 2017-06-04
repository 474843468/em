package com.psi.easymanager.module;

/**
 * Created by dorado on 2016/6/6.
 */
public class AppSaleContent {
  //商品名称
  private String mProdName;
  //数量
  private int mSaleNumber;
  //多单位数量
  private double mSaleMultNumber;
  //是否为多单位商品
  private boolean isMultUnitProd;
  //结账单位
  private String mUnit;

  public String getProdName() {
    return mProdName;
  }

  public void setProdName(String prodName) {
    mProdName = prodName;
  }

  public int getSaleNumber() {
    return mSaleNumber;
  }

  public void setSaleNumber(int saleNumber) {
    mSaleNumber = saleNumber;
  }

  public double getSaleMultNumber() {
    return mSaleMultNumber;
  }

  public void setSaleMultNumber(double saleMultNumber) {
    mSaleMultNumber = saleMultNumber;
  }

  public boolean isMultUnitProd() {
    return isMultUnitProd;
  }

  public void setMultUnitProd(boolean multUnitProd) {
    isMultUnitProd = multUnitProd;
  }

  public String getUnit() {
    return mUnit;
  }

  public void setUnit(String unit) {
    mUnit = unit;
  }
}
