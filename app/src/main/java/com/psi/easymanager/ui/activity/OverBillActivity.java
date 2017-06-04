package com.psi.easymanager.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import com.psi.easymanager.R;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.ui.fragment.OverBillCollectionListFragment;
import com.psi.easymanager.ui.fragment.OverBillDetailsListFragment;
import com.psi.easymanager.ui.fragment.OverBillSaleListFragment;
import com.psi.easymanager.widget.SwipeBackLayout;
import java.util.List;

/**
 * Created by zjq on 2016/6/5.
 */
public class OverBillActivity extends BaseActivity {
  //滑动视图
  @Bind(R.id.swipe_view) SwipeBackLayout mSwipeView;
  //RadioGroup
  @Bind(R.id.rg_type) RadioGroup mRbType;
  //Fragment管理器
  private FragmentManager mFm;
  //账单明细Fragment
  private Fragment mDetailsListFragment;
  //账单汇总Fragment
  private Fragment mCollectionListFragment;
  //销售统计Fragment
  private Fragment mSaleListFragment;

  @Override protected int provideContentViewId() {
    return R.layout.activity_over_bill;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    mFm = getSupportFragmentManager();
    if (savedInstanceState != null) {
      mDetailsListFragment = mFm.findFragmentByTag(Constants.OVER_BILL_DETAIL_LIST);
      mCollectionListFragment = mFm.findFragmentByTag(Constants.OVER_BILL_COLLECTION_LIST);
      mSaleListFragment = mFm.findFragmentByTag(Constants.OVER_BILL_SALE_LIST);
    }
    mSwipeView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
    showDetailsListFragment();
  }

  /**
   * 显示账单明细页面
   */
  private void showDetailsListFragment() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    mDetailsListFragment = mFm.findFragmentByTag(Constants.OVER_BILL_DETAIL_LIST);
    if (mDetailsListFragment == null) {
      mDetailsListFragment = OverBillDetailsListFragment.newInstance("param");
      transaction.add(R.id.over_bill_content_right, mDetailsListFragment, Constants.OVER_BILL_DETAIL_LIST);
    } else {
      transaction.show(mDetailsListFragment);
    }
    transaction.commit();
  }

  /**
   * 显示账单汇总页面
   */
  private void showCollectionListFragment() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    mCollectionListFragment = mFm.findFragmentByTag(Constants.OVER_BILL_COLLECTION_LIST);
    if (mCollectionListFragment == null) {
      mCollectionListFragment = OverBillCollectionListFragment.newInstance("param");
      transaction.add(R.id.over_bill_content_right, mCollectionListFragment, Constants.OVER_BILL_COLLECTION_LIST);
    } else {
      transaction.show(mCollectionListFragment);
    }
    transaction.commit();
  }

  /**
   * 显示销售统计页面
   */
  private void showSaleListFragment() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    mSaleListFragment = mFm.findFragmentByTag(Constants.OVER_BILL_SALE_LIST);
    if (mSaleListFragment == null) {
      mSaleListFragment = OverBillSaleListFragment.newInstance("param");
      transaction.add(R.id.over_bill_content_right, mSaleListFragment, Constants.OVER_BILL_SALE_LIST);
    } else {
      transaction.show(mSaleListFragment);
    }
    transaction.commit();
  }


  /**
   * 切换Tab
   */
  @OnCheckedChanged({ R.id.rb_detail, R.id.rb_collect, R.id.rb_sale }) public void changeTab(RadioButton rb) {
    if (rb.isChecked()) {
      //开启事务
      FragmentTransaction transaction = mFm.beginTransaction();
      //隐藏Fragment
      hideAllFragment(transaction);
      switch (rb.getId()){
        case R.id.rb_detail:
          showDetailsListFragment();
          break;
        case R.id.rb_collect:
          showCollectionListFragment();
          break;
        case R.id.rb_sale:
          showSaleListFragment();
          break;
      }
    }
  }


  /**
   * 隐藏所有Fragment
   */
  private void hideAllFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
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

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}
