package com.psi.easymanager.utils;

import android.content.Context;
import android.os.Build;

import android.view.InputDevice;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.Office;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DeviceUtils {
  private static  final String mDeviceName = "USBKey Chip USBKey Module";
  public static List<String> getDeviceMsg(Context mContext) {
    List<String> deviceInfo = new ArrayList<>();
    PackageUtils packageUtils = new PackageUtils(mContext);
    int localVersionCode = packageUtils.getLocalVersionCode();
    String localVersionName = packageUtils.getLocalVersionName();
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long currentTimeMillis = System.currentTimeMillis();
    deviceInfo.add("=====\tDevice Info\t=====");
    deviceInfo.add("manufacture:" + Build.MANUFACTURER);
    deviceInfo.add("model:" + Build.MODEL);
    deviceInfo.add("versionRelease:" + Build.VERSION.RELEASE);
    deviceInfo.add("display:" + Build.DISPLAY);
    deviceInfo.add("fingerPrint:" +Build.FINGERPRINT);
    deviceInfo.add("product:" + Build.PRODUCT);
    deviceInfo.add("versionSdkIni:" +Build.VERSION.SDK_INT);
    deviceInfo.add("=====\tBB Info\t=====");
    deviceInfo.add("versionCode:" +localVersionCode);
    deviceInfo.add("versionName:" + localVersionName);
    deviceInfo.add("type:" + 0);
    deviceInfo.add("name:" + office.getName());
    deviceInfo.add("code:" + office.getCode());
    deviceInfo.add("crashTime:" + simpleDateFormat.format(currentTimeMillis));
  return deviceInfo;
  }
  /**
   * 输入设备是否存在
   */
  public static boolean isInputDeviceExist(String deviceName) {
    int[] deviceIds = InputDevice.getDeviceIds();
    for (int id : deviceIds) {
      Logger.v(id+"-------");
      //if (InputDevice.getDevice(id).getName().equals(deviceName)) {
      return true;
      // }
    }
    return false;
  }
}