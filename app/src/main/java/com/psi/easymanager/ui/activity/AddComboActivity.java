package com.psi.easymanager.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.ui.fragment.AddComboFragment;
import com.psi.easymanager.ui.fragment.EditComboFragment;
import com.psi.easymanager.ui.fragment.ExistComboFragment;
import com.psi.easymanager.widget.SwipeBackLayout;
import java.util.List;

/**
 * Created by dorado on 2016/8/17.
 */
public class AddComboActivity extends BaseActivity {
  public static final String TYPE_ADD = "Add";
  public static final String TYPE_EDIT = "Edit";
  public static final String TYPE_ORDERED = "Ordered";

  //intent extra key
  public static final String TYPE = "Type";
  //intent extra key
  public static final String DETAILS_ID = "DetailsId";
  //intent extra key
  public static final String COMBO = "Combo";
  //intent extra key
  public static final String ORDER = "Order";

  //添加套餐Fragment
  private AddComboFragment mAddComboFragment;
  //编辑套餐Fragment
  private EditComboFragment mEditComboFragment;
  //已存在套餐Fragment
  private ExistComboFragment mExistComboFragment;
  //FragmentManager
  private FragmentManager mFm;

  //商品
  private PxProductInfo mProductInfo;
  //订单
  private PxOrderInfo mPxOrderInfo;
  //DetailsId
  private Long mPxOrderDetailsId;
  //type
  private String mType;

  //滑动关闭
  @Bind(R.id.swipe_back_view) SwipeBackLayout mSwipeBackView;

  @Override protected int provideContentViewId() {
    return R.layout.activity_add_combo;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    //获取类型
    mType = getIntent().getStringExtra(TYPE);
    if (mType.equals(TYPE_ADD)) {
      //获取商品
      mProductInfo = (PxProductInfo) getIntent().getSerializableExtra(COMBO);
    } else {
      //获取DetailsId
      mPxOrderDetailsId = getIntent().getLongExtra(DETAILS_ID, -1);
    }
    //获取订单
    mPxOrderInfo = (PxOrderInfo) getIntent().getSerializableExtra(ORDER);
    //滑动
    mSwipeBackView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
    //FragmentManager
    mFm = getSupportFragmentManager();
    if (savedInstanceState == null) {
      //初始化Fragment
      initFragment();
    } else {
      mAddComboFragment = (AddComboFragment) mFm.findFragmentByTag(Constants.ADD_COMBO);
      mEditComboFragment = (EditComboFragment) mFm.findFragmentByTag(Constants.EDIT_COMBO);
      mExistComboFragment = (ExistComboFragment) mFm.findFragmentByTag(Constants.EXIST_COMBO_TAG);
      //隐藏Fragment
      hideFragment(mFm.beginTransaction());
      //初始化Fragment
      initFragment();
    }
  }

  @Override protected void onDestroy() {
    ButterKnife.unbind(this);
    super.onDestroy();
  }

  /**
   * 隐藏Fragment
   */
  private void hideFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        transaction.hide(fragment);
      }
    }
  }

  /**
   * 初始化Fragment
   */
  //@formatter:off
  private void initFragment() {
    FragmentTransaction transaction = mFm.beginTransaction();
    //编辑套餐
    mEditComboFragment = (EditComboFragment) mFm.findFragmentByTag(Constants.EDIT_COMBO);
    if (mEditComboFragment == null) {
      mEditComboFragment = EditComboFragment.newInstance(mProductInfo, mPxOrderInfo, mPxOrderDetailsId, mType);
      transaction.add(R.id.fragment_container_left, mEditComboFragment, Constants.EDIT_COMBO);
    } else {
      transaction.show(mEditComboFragment);
    }
    //添加套餐
    mAddComboFragment = (AddComboFragment) mFm.findFragmentByTag(Constants.ADD_COMBO);
    if (mAddComboFragment == null) {
      mAddComboFragment = AddComboFragment.newInstance(mProductInfo, mPxOrderInfo, mPxOrderDetailsId, mType);
      transaction.add(R.id.fragment_container_right, mAddComboFragment, Constants.ADD_COMBO);
    } else {
      transaction.show(mAddComboFragment);
    }
    transaction.commit();
  }
}
