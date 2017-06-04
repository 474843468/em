package com.psi.easymanager.chat;

/**
 * Created by psi on 2016/10/11.
 */

public class AppProdFormatStatus {
  public static final String APP_PRODUCT_FORMAT_NORMAL = "0";//正常
  public static final String APP_PRODUCT_FORMAT_STOCK = "1";//停售
  private String mProdFormatId;
  private String mProdFormatStatus;
  private Double mStock;

  public String getmProdFormatId() {
    return mProdFormatId;
  }

  public void setmProdFormatId(String mProdFormatId) {
    this.mProdFormatId = mProdFormatId;
  }

  public String getmProdFormatStatus() {
    return mProdFormatStatus;
  }

  public void setmProdFormatStatus(String mProdFormatStatus) {
    this.mProdFormatStatus = mProdFormatStatus;
  }

  public Double getmStock() {
    return mStock;
  }

  public void setmStock(Double mStock) {
    this.mStock = mStock;
  }
}
