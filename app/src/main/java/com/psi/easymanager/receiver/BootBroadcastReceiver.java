package com.psi.easymanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.psi.easymanager.ui.activity.SplashActivity;

/**
 * User: ylw
 * Date: 2016-07-08
 * Time: 14:47
 * FIXME
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
 private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
  @Override public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(ACTION)) {
      Intent splashIntent = new Intent(context, SplashActivity.class);  // 要启动的Activity
      splashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(splashIntent);
    }
  }
}