package com.psi.easymanager.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import com.psi.easymanager.R;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.ui.fragment.AboutFireFragment;
import com.psi.easymanager.ui.fragment.BTPrintSettingFragment;
import com.psi.easymanager.ui.fragment.BusinessModelFragment;
import com.psi.easymanager.ui.fragment.LabelPrintSettingFragment;
import com.psi.easymanager.ui.fragment.OrdinaryPrinterFragment;
import com.psi.easymanager.ui.fragment.PermissionSetFragment;
import com.psi.easymanager.ui.fragment.UploadSettingFragment;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.SwipeBackLayout;
import java.util.List;

/**
 * Created by zjq on 2016/3/16.
 * 主页面 更多
 */
public class MoreActivity extends BaseActivity {
  public static final int OPEN_BLUETOOTH_REQUEST = 15;//打开蓝牙请求


  @Bind(R.id.content_view) SwipeBackLayout mContentView;
  @Bind(R.id.rb_ordinary_printer) RadioButton mRbPrint;
  @Bind(R.id.view_ordinary_printer) View mViewPrint;
  //Fragment管理器
  private FragmentManager mFm;

  @Override protected int provideContentViewId() {
    return R.layout.activity_more;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    //Fragment管理器
    mFm = getSupportFragmentManager();
    //初始化滑动关闭
    initSwipeBack();
    initView();
  }

  //ptksai pos 不支持USB打印 不再显示配置普通打印机模块
  private void initView() {
    String isSupportUSBPrint = (String) SPUtils.get(this, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) {
      mRbPrint.setVisibility(View.GONE);
      mViewPrint.setVisibility(View.GONE);
    }else{
      mRbPrint.setVisibility(View.VISIBLE);
      mViewPrint.setVisibility(View.VISIBLE);
    }
  }

  @Override protected void onStart() {
    super.onStart();
    //初始化Fragment
    initFragment(mFm.beginTransaction());
  }

  /**
   * 初始化滑动关闭
   */
  private void initSwipeBack() {
    mContentView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
  }

  /**
   * 初始化Fragment
   */
  private void initFragment(FragmentTransaction transaction) {

  }

  /**
   * 切换Fragment
   */
  //@formatter:off
  @OnCheckedChanged({
      R.id.rb_business_model, R.id.rb_ordinary_printer, R.id.rb_permission_set,
      R.id.rb_upload_setting, R.id.rb_about_fire,R.id.rb_label_print,R.id.rb_bt_printer
  }) public void showFragment(RadioButton rb) {
    if (!rb.isChecked()) return;
    FragmentTransaction mTransaction = mFm.beginTransaction();
    //隐藏Fragment
    hideFragment(mFm, mTransaction);
    switch (rb.getId()) {
      case R.id.rb_business_model://商业模式
        Fragment businessModelFragment = mFm.findFragmentByTag(Constants.BUSINESS_MODEL_TAG);
        if (businessModelFragment == null) {
          businessModelFragment = BusinessModelFragment.newInstance("param");
          mTransaction.add(R.id.fl_more_content, businessModelFragment, Constants.BUSINESS_MODEL_TAG);
        } else {
          mTransaction.show(businessModelFragment);
        }
        break;
      case R.id.rb_ordinary_printer://配置普通打印机
        Fragment ordinaryPrinterFragment = mFm.findFragmentByTag(Constants.ORDINARY_PRINTER_TAG);
        if (ordinaryPrinterFragment == null) {
          ordinaryPrinterFragment = OrdinaryPrinterFragment.newInstance("param");
          mTransaction.add(R.id.fl_more_content, ordinaryPrinterFragment, Constants.ORDINARY_PRINTER_TAG);
        } else {
          mTransaction.show(ordinaryPrinterFragment);
        }
        break;
      case R.id.rb_permission_set://权限设置
        Fragment permissionSetFragment = mFm.findFragmentByTag(Constants.PERMISSION_SET_TAG);
        if (permissionSetFragment == null) {
          permissionSetFragment = PermissionSetFragment.newInstance("param");
          mTransaction.add(R.id.fl_more_content, permissionSetFragment, Constants.PERMISSION_SET_TAG);
        } else {
          mTransaction.show(permissionSetFragment);
        }
        break;
      case R.id.rb_upload_setting://上传设置数据
        Fragment uploadSettingFragment = mFm.findFragmentByTag(Constants.UPLOAD_SETTING_TAG);
        if (uploadSettingFragment == null) {
          uploadSettingFragment = UploadSettingFragment.newInstance("param");
          mTransaction.add(R.id.fl_more_content, uploadSettingFragment, Constants.UPLOAD_SETTING_TAG);
        } else {
          mTransaction.show(uploadSettingFragment);
        }
        break;
      case R.id.rb_about_fire://关于逸掌柜
        Fragment aboutFireFragment = mFm.findFragmentByTag(Constants.ABOUT_FIRE_TAG);
        if (aboutFireFragment == null) {
          aboutFireFragment = AboutFireFragment.newInstance("param");
          mTransaction.add(R.id.fl_more_content, aboutFireFragment, Constants.ABOUT_FIRE_TAG);
        } else {
          mTransaction.show(aboutFireFragment);
        }
        break;
      case R.id.rb_label_print://配置标签打印机
        Fragment labelPrintFragment = mFm.findFragmentByTag(Constants.LABEL_PRINT_TAG);
        if (labelPrintFragment == null) {
          labelPrintFragment = LabelPrintSettingFragment.newInstance("param");
          mTransaction.add(R.id.fl_more_content, labelPrintFragment, Constants.LABEL_PRINT_TAG);
        } else {
          mTransaction.show(labelPrintFragment);
        }
        break;
      case R.id.rb_bt_printer://配置蓝牙打印机
        Fragment btPrintFragment = mFm.findFragmentByTag(Constants.BT_PRINT_TAG);
        if (btPrintFragment == null) {
          btPrintFragment = BTPrintSettingFragment.newInstance();
          mTransaction.add(R.id.fl_more_content, btPrintFragment, Constants.BT_PRINT_TAG);
        }else{
          mTransaction.show(btPrintFragment);
        }
        break;
    }
    mTransaction.commit();
  }
  /**
   * 隐藏Fragment
   */
  private void hideFragment(FragmentManager fm, FragmentTransaction transaction) {
    List<Fragment> fragments = fm.getFragments();
    if (fragments != null) {
      for (Fragment fragment : fragments) {
        transaction.hide(fragment);
      }
    }
  }

  /**
   * 屏蔽返回键
   */
  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }


  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == OPEN_BLUETOOTH_REQUEST) {//打开蓝牙请求
      if (resultCode == Activity.RESULT_OK) {
        ToastUtils.showShort(this, "蓝牙已打开");
      } else {
        ToastUtils.showShort(this, "蓝牙打开失败!");
      }
    }
  }


  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}