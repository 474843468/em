package com.psi.easymanager.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.psi.easymanager.R;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.ui.fragment.QueryMemberFragment;
import com.psi.easymanager.ui.fragment.VipOperationFragment;
import com.psi.easymanager.widget.InterceptClickLayout;
import com.psi.easymanager.widget.SwipeBackLayout;

/**
 * Created by wanghzhen on 2016/6/2.
 * 会员中心
 */
public class MemberCentreActivity extends BaseActivity {
  @Bind(R.id.content_view) SwipeBackLayout mContentView;
  //内容view
  @Bind(R.id.content_view_intercept) InterceptClickLayout mRlContent;
  //刷新view
  @Bind(R.id.progress_view) RelativeLayout mRlProgress;
  //刷新view显示名称
  @Bind(R.id.tv_progress_name) TextView mTvProgressName;
  //Fragment管理器
  private FragmentManager mFm;
  @Override protected int provideContentViewId() {
    return R.layout.activity_member_centre;
  }
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    //Fragment管理器
    mFm = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = mFm.beginTransaction();
    //初始化Fragment
    initFragment(fragmentTransaction);
    //初始化滑动关闭
    initSwipeBack();
  }

  @Override protected void onStart() {
    super.onStart();
  }

  /**
   * 初始化Fragment，显示查询fragment
   */
  private void initFragment(FragmentTransaction transaction) {

    Fragment mQueryVipInfoFragment = mFm.findFragmentByTag(Constants.MEMBER_FUZZY_QUERY_TAG);
    if (mQueryVipInfoFragment == null) {
      mQueryVipInfoFragment = QueryMemberFragment.newInstance("param");
      transaction.add(R.id.fl_member_left, mQueryVipInfoFragment, Constants.MEMBER_FUZZY_QUERY_TAG);
    } else {
      transaction.show(mQueryVipInfoFragment);
    }
    Fragment mVipOperationFragment =
        mFm.findFragmentByTag(Constants.VIPINFOCHARGEANDRECHARGE);
    if (mVipOperationFragment == null) {
      mVipOperationFragment =
          VipOperationFragment.newInstance("param");
      transaction.add(R.id.fl_member_content, mVipOperationFragment,
          Constants.VIPINFOCHARGEANDRECHARGE);
    } else {
      transaction.show(mVipOperationFragment);
    }
    transaction.commit();
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


  /**
   * 是否显示蒙层
   */
  public void isShowProgress(boolean isShow){
    mRlProgress.setVisibility(isShow ? View.VISIBLE : View.GONE);
    mRlContent.setIntercept(isShow);
  }


}
