package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.kyleduo.switchbutton.SwitchButton;
import com.psi.easymanager.R;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxProductRemarksDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.EditComboProdEvent;
import com.psi.easymanager.event.ExistComboEvent;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxProductRemarks;
import com.psi.easymanager.ui.activity.AddComboActivity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dorado on 2016/9/26.
 */
public class ExistComboFragment extends BaseFragment {

  @Bind(R.id.tv_prod_name) TextView mTvProdName;
  @Bind(R.id.tv_format_name) TextView mTvFormatName;
  @Bind(R.id.tags_remarks) TagFlowLayout mTagsRemarks;
  @Bind(R.id.et_custom_remark) EditText mEtCustomRemark;
  @Bind(R.id.sb_wait) SwitchButton mSbWait;

  private String mRemarks;
  private List<PxProductRemarks> mRemarksList;
  private TagAdapter mRemarksAdapter;
  private AddComboActivity mAct;
  private PxOrderDetails mDetails;
  private FragmentManager mFm;
  private AddComboFragment mAddComboFragment;
  private String mType;

  public static ExistComboFragment newInstance(String param) {
    ExistComboFragment existComboFragment = new ExistComboFragment();
    Bundle bundle = new Bundle();

    existComboFragment.setArguments(bundle);
    return existComboFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (AddComboActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_exist_combo, null);
    ButterKnife.bind(this, view);

    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(ExistComboEvent.class);
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void getDetails(ExistComboEvent event) {
    mDetails = event.getDetails();
    mType = event.getType();
    if (mType.equals(EditComboProdEvent.TYPE_ORDERED)) {
      mTagsRemarks.setEnabled(false);
      mSbWait.setEnabled(false);
      mEtCustomRemark.setEnabled(false);
    }
    mTvProdName.setText(mDetails.getDbProduct().getName());
    if (mDetails.getDbFormatInfo() != null) {
      mTvFormatName.setVisibility(View.VISIBLE);
      mTvFormatName.setText(mDetails.getDbFormatInfo().getName());
    } else {
      mTvFormatName.setVisibility(View.GONE);
    }
    mRemarks = mDetails.getRemarks();
    //查询备注
    queryRemarks();
    //是否延迟
    if (mDetails.getStatus().equals(PxOrderDetails.STATUS_ORIDINARY)) {
      mSbWait.setChecked(false);
    } else {
      mSbWait.setChecked(true);
    }
  }

  /**
   * 查询备注
   */
  private void queryRemarks() {
    //备注验证
    mRemarksList = DaoServiceUtil.getProdRemarksService()
        .queryBuilder()
        .where(PxProductRemarksDao.Properties.DelFlag.eq("0"))
        .list();
    if (mRemarksList != null && mRemarksList.size() != 0) {
      //TagAdapter
      mRemarksAdapter = new TagAdapter<PxProductRemarks>(mRemarksList) {
        @Override public View getView(FlowLayout parent, int position, PxProductRemarks remarks) {
          TextView tv = (TextView) LayoutInflater.from(mAct)
              .inflate(R.layout.item_tags_remark, mTagsRemarks, false);
          tv.setText(remarks.getRemarks());
          return tv;
        }
      };
      mTagsRemarks.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
        @Override public boolean onTagClick(View view, int position, FlowLayout parent) {
          PxProductRemarks pxProductRemarks = mRemarksList.get(position);
          if (mEtCustomRemark.getText() != null && mEtCustomRemark.getText().toString().trim().equals("") == false) {
            mEtCustomRemark.append(" ," + pxProductRemarks.getRemarks());
          } else {
            mEtCustomRemark.append(pxProductRemarks.getRemarks());
          }
          return true;
        }
      });
      mTagsRemarks.setAdapter(mRemarksAdapter);
      //默认选择
      mEtCustomRemark.setText(mDetails.getRemarks()+"");
    }
  }

  /**
   * 确定
   */
  @OnClick(R.id.btn_confirm) public void confirm() {
    //备注
    String remarks = mEtCustomRemark.getText().toString();
    mDetails.setRemarks(remarks.toString());

    if (mSbWait.isChecked()) {
      mDetails.setStatus(PxOrderDetails.STATUS_DELAY);
    } else {
      mDetails.setStatus(PxOrderDetails.STATUS_ORIDINARY);
    }
    //储存
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(mDetails);
    //显示AddComboFragment
    showAddComboFragment();
  }

  /**
   * 取消
   */
  @OnClick(R.id.btn_cancel) public void cancel() {
    //显示AddComboFragment
    showAddComboFragment();
  }

  /**
   * 显示AddComboFragment
   */
  private void showAddComboFragment() {
    //切换Fragment
    mAddComboFragment = (AddComboFragment) mFm.findFragmentByTag(Constants.ADD_COMBO);
    FragmentTransaction transaction = mFm.beginTransaction();
    hideFragment(transaction);
    if (mAddComboFragment == null) {

    } else {
      transaction.show(mAddComboFragment);
    }
    transaction.commit();
  }

  /**
   * 隐藏Fragment
   */
  public void hideFragment(FragmentTransaction transaction) {
    List<Fragment> fragments = mFm.getFragments();
    for (Fragment fragment : fragments) {
      if (fragment instanceof ExistComboFragment) {
        transaction.hide(fragment);
      }
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
  }
}