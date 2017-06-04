package com.psi.easymanager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by zjq on 15/4/2.
 */
public abstract class BaseActivity extends AppCompatActivity {
  abstract protected int provideContentViewId();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(provideContentViewId());
    //initStrictMode();
  }

  private void initStrictMode() {
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()//虚拟机策略（VmPolicy）
        //.detectActivityLeaks()//最低版本API11 用户检查 Activity 的内存泄露情况
        //.detectCleartextNetwork()//最低版本为API23  检测明文的网络
        //.detectFileUriExposure()//最低版本为API18   检测file://或者是content://
        //.detectLeakedClosableObjects()//最低版本API11  资源没有正确关闭时触发
        //.detectLeakedRegistrationObjects()//最低版本API16  BroadcastReceiver、ServiceConnection是否被释放
        //.detectLeakedSqlLiteObjects()//最低版本API9   资源没有正确关闭时回触发
        //.setClassInstanceLimit(MyClass.class, 2)//设置某个类的同时处于内存中的实例上限，可以协助检查内存泄露
        .detectAll()
        .penaltyLog()//与上面的一致
        //.penaltyDeath()
        .penaltyDropBox()////一旦检测到将信息存到DropBox文件夹中 data/system/dropbox
        .build());
  }

  /**
   * 通过Id得到view的实例
   */
  protected <T> T findView(int viewId) {
    return (T) findViewById(viewId);
  }

  /**
   * 弹出对话框
   */
  protected void showDialog(String msg) {

  }

  /**
   * 关闭对话框
   */
  protected void dismissDialog() {

  }

  /**
   * 通过类名启动activity
   */
  protected void openActivity(Class<?> clazz) {
    openActivity(clazz, null);
  }

  /**
   * 通过类名启动activity
   */
  protected void openActivity(Context context, Class<?> clazz) {
    Intent intent = new Intent(context, clazz);
    openActivity(intent);
  }

  /**
   * 通过类名带参启动Activity
   */
  protected void openActivity(Class<?> clazz, Bundle bundle) {
    Intent intent = new Intent(this, clazz);
    if (bundle != null) {
      intent.putExtras(bundle);
    }
    openActivity(intent);
  }

  /**
   * 启动Activity
   */
  protected void openActivity(Intent intent) {
    startActivity(intent);
  }

  /**
   * 通过action名启动activity
   */
  protected void openActivity(String action) {
    openActivity(action, null);
  }

  /**
   * 通过action名带参启动activity
   */
  protected void openActivity(String action, Bundle bundle) {
    Intent intent = new Intent(action);
    if (bundle != null) {
      intent.putExtras(bundle);
    }
    openActivity(intent);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
