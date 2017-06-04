package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.event.VipInfoListEvent;
import com.psi.easymanager.pay.vip.VipLogin;
import com.psi.easymanager.ui.activity.MemberCentreActivity;
import com.psi.easymanager.utils.RegExpUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by psi on 2016/6/2.
 * 模糊查询会员
 */
public class QueryMemberFragment extends BaseFragment implements View.OnClickListener {
  @Bind(R.id.tv_fuzzy_query_number) EditText tvFuzzyQueryNumber;//电话号码搜索字眼
  @Bind(R.id.tv_query_member_title) TextView tvFuzzyQueryTitle;//标题
  @Bind(R.id.btn_card_login) Button btnCardLogin;//切换
  @Bind(R.id.btn_img_new) Button btnNew;//新建会员

  //MemberCentreActivity
  private MemberCentreActivity mAct;
  //Fragment管理器
  private FragmentManager mFm;
  private String likeName = "";
  StringBuilder sb = new StringBuilder();
  private Boolean isSelected = true;

  public static QueryMemberFragment newInstance(String param) {
    Bundle bundle = new Bundle();
    bundle.putString("param", param);
    QueryMemberFragment fragment = new QueryMemberFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (MemberCentreActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_query_member, null);
    ButterKnife.bind(this, view);
    Button btnTest = (Button) view.findViewById(R.id.btn_test);
    TextView tv1 = (TextView) view.findViewById(R.id.btn_1);
    TextView tv2 = (TextView) view.findViewById(R.id.btn_2);
    TextView tv3 = (TextView) view.findViewById(R.id.btn_3);
    TextView tv4 = (TextView) view.findViewById(R.id.btn_4);
    TextView tv5 = (TextView) view.findViewById(R.id.btn_5);
    TextView tv6 = (TextView) view.findViewById(R.id.btn_6);
    TextView tv7 = (TextView) view.findViewById(R.id.btn_7);
    TextView tv8 = (TextView) view.findViewById(R.id.btn_8);
    TextView tv9 = (TextView) view.findViewById(R.id.btn_9);
    TextView tv0 = (TextView) view.findViewById(R.id.btn_0);
    TextView tvBack = (TextView) view.findViewById(R.id.btn_back);
    TextView tvDel = (TextView) view.findViewById(R.id.btn_delete);

    btnTest.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

      }
    });
    tv1.setOnClickListener(this);
    tv2.setOnClickListener(this);
    tv3.setOnClickListener(this);
    tv4.setOnClickListener(this);
    tv5.setOnClickListener(this);
    tv6.setOnClickListener(this);
    tv7.setOnClickListener(this);
    tv8.setOnClickListener(this);
    tv9.setOnClickListener(this);
    tv0.setOnClickListener(this);
    tvBack.setOnClickListener(this);
    tvDel.setOnClickListener(this);
    tvFuzzyQueryNumber.setInputType(0);
    btnNew.setVisibility(View.GONE);
    return view;
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_1:
      case R.id.btn_2:
      case R.id.btn_3:
      case R.id.btn_0:
      case R.id.btn_4:
      case R.id.btn_5:
      case R.id.btn_6:
      case R.id.btn_7:
      case R.id.btn_8:
      case R.id.btn_9:
        TextView view = (TextView) v;
        showNumber(view.getText().toString());
        break;
      case R.id.btn_back:
        if (sb.length() >= 2) {
          sb.delete(sb.length() - 1, sb.length());
        } else if (sb.length() <= 1) {
          sb = new StringBuilder("");
        }
        tvFuzzyQueryNumber.setText(sb.toString());
        break;
      case R.id.btn_delete:
        sb = new StringBuilder("");
        tvFuzzyQueryNumber.setText(sb.toString());
        break;
      case R.id.btn_test:
        break;
    }
  }

  private void showNumber(String text) {
    if (isSelected == true) {
      if (!(sb.length() < 16)) {
        return;
      }
      return;
    } else if (!(sb.length() < 11)) {
      return;
    }
    sb.append(text);
    tvFuzzyQueryNumber.setText(sb.toString());
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override public void onStart() {
    super.onStart();
  }

  /**
   * 查询
   */
  @OnClick({ R.id.ic_member_search, R.id.btn_vip_login }) public void searchVip(View view) {
    if (isSelected) {
      String vipCardNumber = tvFuzzyQueryNumber.getText().toString().trim();
      Logger.v(vipCardNumber);
      if (TextUtils.isEmpty(vipCardNumber)) {
        ToastUtils.showShort(null, "请刷实体卡");
        return;
      }
      mAct.isShowProgress(true);
      VipLogin.vipCardLogin(vipCardNumber, null);
    } else {
      likeName = tvFuzzyQueryNumber.getText().toString().trim();
      Logger.v(likeName);
      if (!RegExpUtils.match11Number(likeName)) {
        ToastUtils.showShort(mAct, "您输入的手机号错误");
        return;
      } else {
        EventBus.getDefault().post(new VipInfoListEvent(likeName));
        switchoverFragment();
      }
    }
  }

  /**
   * 实体卡和手机 切换
   */
  @OnClick(R.id.btn_card_login) public void switchLogin(Button button) {
    btnCardLogin.setSelected(!isSelected);
    if (btnCardLogin.isSelected()) {
      isSelected = true;
      sb = new StringBuilder("");
      tvFuzzyQueryNumber.setText(sb.toString());
      btnCardLogin.setText("手机号");
      tvFuzzyQueryNumber.setHint("请刷实体卡");
      tvFuzzyQueryTitle.setText("刷实体卡搜索会员");
      btnNew.setVisibility(View.GONE);
    } else {
      isSelected = false;
      sb = new StringBuilder("");
      tvFuzzyQueryNumber.setText(sb.toString());
      btnCardLogin.setText("实体卡");
      tvFuzzyQueryNumber.setHint("请输入手机号");
      tvFuzzyQueryTitle.setText("手机号搜索会员");
      btnNew.setVisibility(View.VISIBLE);
    }
  }

  /**
   * 下面的新建按钮
   */
  @OnClick(R.id.btn_img_new) public void memberQueryClose(Button button) {
    echoFragment();
  }

  /**
   * 隐藏VipOperationFragment 显示 addMemberFragment
   */
  private void echoFragment() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideVipOperationFragment(transaction);
    Fragment mAddMemberFragment = mFm.findFragmentByTag(Constants.ADD_MEMBER_TAG);
    if (mAddMemberFragment == null) {
      mAddMemberFragment = AddMemberFragment.newInstance("param");
      transaction.add(R.id.fl_member_content, mAddMemberFragment, Constants.ADD_MEMBER_TAG);
    } else {
      transaction.show(mAddMemberFragment);
    }
    transaction.commit();
  }

  /**
   * 隐藏 addMemberFragment显示VipOperationFragment
   */
  private void switchoverFragment() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAddMemberFragment(transaction);
    Fragment mVipOperationFragment = mFm.findFragmentByTag(Constants.VIPINFOCHARGEANDRECHARGE);
    if (mVipOperationFragment == null) {
      mVipOperationFragment = VipOperationFragment.newInstance("param");
      transaction.add(R.id.fl_member_content, mVipOperationFragment,
          Constants.VIPINFOCHARGEANDRECHARGE);
    } else {
      transaction.show(mVipOperationFragment);
    }
    transaction.commit();
  }

  /**
   * 隐藏除了AddMemberFragment的fragment
   */
  private void hideExcludeAddMemberFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        if ((fragment instanceof AddMemberFragment)) continue;
        transaction.hide(fragment);
      }
    }
  }

  /**
   * 隐藏AddMemberFragment
   */
  private void hideAddMemberFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        if ((fragment instanceof AddMemberFragment)) {
          transaction.hide(fragment);
        }
      }
    }
  }

  /**
   * 隐藏VipOperationFragment
   */
  private void hideVipOperationFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        if (fragment instanceof VipOperationFragment) {
          transaction.hide(fragment);
        }
      }
    }
  }

  /**
   * 隐藏所有的fragment
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
   * 退出
   */
  @Override public void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}
