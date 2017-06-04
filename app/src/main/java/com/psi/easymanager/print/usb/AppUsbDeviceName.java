package com.psi.easymanager.print.usb;

/**
 * Created by psi on 2016/11/17.
 * 获取USB设备名
 */

public class AppUsbDeviceName {

  private String mDeviceName;//USB设备名

  public AppUsbDeviceName(String mDeviceName) {
    this.mDeviceName = mDeviceName;
  }

  public String getDeviceName() {
    return mDeviceName;
  }

  public void setDeviceName(String mDeviceName) {
    this.mDeviceName = mDeviceName;
  }
}
