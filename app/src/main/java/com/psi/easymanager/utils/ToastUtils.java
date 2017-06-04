package com.psi.easymanager.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.psi.easymanager.common.App;

/**
 * Toast统一管理类
 */
public class ToastUtils {

  private static Toast toast;

  public static void show(CharSequence msg) {
    showInner(msg, Toast.LENGTH_SHORT);
  }

  public static void showLong(CharSequence msg) {
    showInner(msg, Toast.LENGTH_LONG);
  }

  private static void showInner(CharSequence msg, int time) {
    if (toast != null) {
      toast.cancel();
    }
    toast = Toast.makeText(App.getInstance(), msg, time);
    toast.show();
  }

  private ToastUtils() {
    /* cannot be instantiated */
    throw new UnsupportedOperationException("cannot be instantiated");
  }

  /**
   * 短时间显示Toast
   */
  public static void showShort(Context context, CharSequence message) {
    if (App.getContext() != null) {
      Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Async toast
   */
  public static void showShortAsync(final CharSequence message) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override public void run() {
        showShort(null, message);
      }
    });
  }

  /**
   * 短时间显示Toast
   */
  public static void showShort(Context context, int message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  /**
   * 长时间显示Toast
   */
  public static void showLong(Context context, CharSequence message) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
  }

  /**
   * 长时间显示Toast
   */
  public static void showLong(Context context, int message) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
  }

  /**
   * 自定义显示Toast时间
   */
  public static void show(Context context, CharSequence message, int duration) {
    Toast.makeText(context, message, duration).show();
  }

  /**
   * 自定义显示Toast时间
   */
  public static void show(Context context, int message, int duration) {
    Toast.makeText(context, message, duration).show();
  }
}