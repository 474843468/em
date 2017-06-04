package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.OverBillSaleListAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxProductCategoryDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.SaleContentEvent;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.ui.activity.OverBillActivity;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.DetachableClickListener;
import com.psi.easymanager.widget.RecyclerViewSpaceItemDecoration;
import java.util.Comparator;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by zjq on 2016/6/6.
 * 已结账单 销售统计 列表页面
 */
public class OverBillSaleListFragment extends BaseFragment
    implements OverBillSaleListAdapter.OnSaleClickListener {
  //检索日期
  @Bind(R.id.tv_date) TextView mTvDate;
  //Rcv
  @Bind(R.id.rcv_sale_list) RecyclerView mRcvSaleList;
  //Activity
  private OverBillActivity mAct;
  //Fragment管理
  private FragmentManager mFm;
  //分类数据
  private List<PxProductCategory> mCategoryList;
  //适配器
  private OverBillSaleListAdapter mSaleListAdapter;
  //左侧销售详情Fragment
  private Fragment mSaleContentFragment;
  //当前Category
  private PxProductCategory mCurrentCategory;

  private int mSearchDate = TODAY;//要检索的日期范围
  public static final int TODAY = 0;
  public static final int YESTERDAY = 1;
  public static final int TWO_DAYS = 2;

  public static OverBillSaleListFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    OverBillSaleListFragment fragment = new OverBillSaleListFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (OverBillActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    if (savedInstanceState != null) {
      mSaleContentFragment = mFm.findFragmentByTag(Constants.OVER_BILL_SALE_CONTENT);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_over_bill_sale_list, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initRcv();
  }

  /**
   * 初始化Rcv
   */
  //@formatter:off
  private void initRcv() {
    GridLayoutManager layoutManager = new GridLayoutManager(mAct, 5, LinearLayoutManager.VERTICAL, false);
    mRcvSaleList.setHasFixedSize(true);
    mRcvSaleList.setLayoutManager(layoutManager);

    mCategoryList = DaoServiceUtil.getProductCategoryService()
        .queryBuilder()
        .where(PxProductCategoryDao.Properties.DelFlag.eq("0"))
        .list();

    mSaleListAdapter = new OverBillSaleListAdapter(mAct, mCategoryList);
    mSaleListAdapter.setOnSaleClickListener(this);
    mRcvSaleList.setAdapter(mSaleListAdapter);

    //间距设置
    int spaceWidth = getResources().getDimensionPixelSize(R.dimen.over_bill_sale_list_rcv_item_horizontal_space_width);
    int spaceHeight = getResources().getDimensionPixelSize(R.dimen.over_bill_sale_list_rcv_item_vertical_space_height);
    mRcvSaleList.addItemDecoration(new RecyclerViewSpaceItemDecoration(spaceWidth, spaceHeight));
  }

  /**
   * 选择日期
   */

  @OnClick(R.id.tv_date) public void chooseDate(View view) {
    final String[] date = { "今日", "昨日", "两日内" };

    final MaterialDialog selectDateDialog = DialogUtils.showListDialog(mAct, "日期", date);
    MDButton posBtn = selectDateDialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = selectDateDialog.getActionButton(DialogAction.NEGATIVE);

    final DetachableClickListener posListener = DetachableClickListener.wrap(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int which = selectDateDialog.getSelectedIndex();
        mTvDate.setText(date[which]);
        switch (which) {
          case 0:
            mSearchDate = TODAY;
            break;
          case 1:
            mSearchDate = YESTERDAY;
            break;
          case 2:
            mSearchDate = TWO_DAYS;
            break;
        }
        if (mCurrentCategory == null) {
          ToastUtils.showShort(App.getContext(), "请选择分类");
        } else {
          //传递数据
          EventBus.getDefault().postSticky(new SaleContentEvent().setCategory(mCurrentCategory).setTimeFilter(mSearchDate));
        }
        DialogUtils.dismissDialog(selectDateDialog);
      }
    });

    DetachableClickListener negListener = DetachableClickListener.wrap(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(selectDateDialog);
      }
    });

    posListener.clearOnDetach(selectDateDialog);
    negListener.clearOnDetach(selectDateDialog);
    posBtn.setOnClickListener(posListener);
    negBtn.setOnClickListener(negListener);
  }

  /**
   * Item点击
   */
  @Override public void onSaleClick(int pos) {
    mCurrentCategory = mCategoryList.get(pos);
    mSaleListAdapter.setSelected(pos);
    showSaleContent();
    //传递数据
    EventBus.getDefault().postSticky(new SaleContentEvent().setCategory(mCurrentCategory).setTimeFilter(mSearchDate));
  }

  /**
   * 显示左侧销售详情
   */
  private void showSaleContent(){
    FragmentTransaction transaction = mFm.beginTransaction();
    hideLeftFragment(transaction);
    mSaleContentFragment = mFm.findFragmentByTag(Constants.OVER_BILL_SALE_CONTENT);
    if (mSaleContentFragment == null) {
      mSaleContentFragment = OverBillSaleContentFragment.newInstance("param");
      transaction.add(R.id.over_bill_content_left, mSaleContentFragment, Constants.OVER_BILL_SALE_CONTENT);
    } else {
      transaction.show(mSaleContentFragment);
    }
    transaction.commit();
  }


  /**
   * 隐藏Fragment
   */
  private void hideLeftFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        if (!(fragment instanceof OverBillSaleListFragment)){
          transaction.hide(fragment);
        }
      }
    }
  }

  /**
   * 退出
   */
  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }

}

