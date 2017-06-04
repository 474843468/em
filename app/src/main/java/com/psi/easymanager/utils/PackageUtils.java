package com.psi.easymanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import java.io.File;

/**
 * Created by zjq on 2016/4/8.
 * App包辅助类
 */

public class PackageUtils {

  private PackageInfo info;
  private PackageManager pm;

  public PackageUtils(Context context) {
    pm = context.getPackageManager();
    try {
      info = pm.getPackageInfo(context.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  public int getLocalVersionCode() {
    return info != null ? info.versionCode : Integer.MAX_VALUE;
  }

  public String getLocalVersionName() {
    return info != null ? info.versionName : "";
  }

  public String getAppName() {
    return info != null ? (String) info.applicationInfo.loadLabel(pm) : "";
  }

  public String getPackageName() {
    return info != null ? info.packageName : "";
  }

  public int getAppIcon() {
    return info != null ? info.applicationInfo.icon : android.R.drawable.ic_dialog_info;
  }

  /**
   * APK 存放地址
   */
  public static File getDownloadFile(Context context) {
    String cachePath;
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
      cachePath = context.getExternalCacheDir().getPath();
    } else {
      cachePath = context.getCacheDir().getPath();
    }
    File file = new File(cachePath + File.separator + "逸掌柜.apk");
    return file;
  }
  /**
   * 错误日志存放地址
   */
  public static File getErrorLogDir(Context context) {
    if (context==null){
    return null;
    }
    String cachePath = null;
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
        || !Environment.isExternalStorageRemovable()) {
      if (context.getExternalCacheDir()!=null){
        cachePath = context.getExternalCacheDir().getPath();
      }
    } else {
      if (context.getCacheDir()!=null){
        cachePath = context.getCacheDir().getPath();
      }
    }
    if (!TextUtils.isEmpty(cachePath)){
      File file = new File(cachePath + File.separator + "log");
      return file;
    }else {
      return null;
    }
  }
  /**
   * 安装App
   */
  public static void installApk(Activity act ,File file) {
    // 打开安装界面安装apk
    Intent intent = new Intent();
    intent.setAction("android.intent.action.VIEW");
    intent.addCategory("android.intent.category.DEFAULT");
    intent.setDataAndType(Uri.parse("file://" + file), "application/vnd.android.package-archive");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // 当安装界面点击取消按钮时调用onActivityResult方法，
    act.startActivityForResult(intent, 0);
    //更新完成自启动
    android.os.Process.killProcess(android.os.Process.myPid());
  }
}
