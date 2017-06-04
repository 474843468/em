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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.OverBillCollectionListAdapter;
import com.psi.easymanager.adapter.TableTypeAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CollectionContentEvent;
import com.psi.easymanager.event.SaleContentEvent;
import com.psi.easymanager.module.AppTableType;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.User;
import com.psi.easymanager.ui.activity.OverBillActivity;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.DetachableClickListener;
import com.psi.easymanager.widget.RecyclerViewSpaceItemDecoration;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by zjq on 2016/6/6.
 * 已结账单 账单汇总列表页面
 */
public class OverBillCollectionListFragment extends BaseFragment
    implements OverBillCollectionListAdapter.OnUserClickListener {

  @Bind(R.id.tv_date) TextView mTvDate;
  @Bind(R.id.rcv) RecyclerView mRcv;
  @Bind(R.id.lv_cate) ListView mLvCate;

  public static final int TODAY = 0;
  public static final int YESTERDAY = 1;
  public static final int TWO_DAYS = 2;

  public static final String ALL = "2";
  public static final String RETAIL = "3";

  //桌台类型列表
  private List<AppTableType> mTableTypeList;
  //桌台类型适配器
  private TableTypeAdapter mTableTypeAdapter;

  //汇总列表
  private List<User> mUserList;
  //汇总适配器
  private OverBillCollectionListAdapter mAdapter;

  //Activity
  private OverBillActivity mAct;
  //Fragment管理
  private FragmentManager mFm;

  //要检索的日期范围
  private int mSearchDate = TODAY;
  //要检索的桌台类型
  private String mSearchTableType = ALL;
  //要搜索的用户
  private User mSearchUser;

  //左侧订单汇总Fragment
  private Fragment mCollectionContentFragment;

  public static OverBillCollectionListFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    OverBillCollectionListFragment fragment = new OverBillCollectionListFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (OverBillActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    if (savedInstanceState != null) {
      mCollectionContentFragment = mFm.findFragmentByTag(Constants.OVER_BILL_COLLECTION_CONTENT);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_over_bill_collection_list, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    //初始化ListView
    initLv();
    //初始化Rcv
    initRcv();
  }

  /**
   * 初始化ListView
   */
  //@formatter:off
  private void initLv() {
    mTableTypeList = new ArrayList<>();
    //桌台区域
     List<PxTableArea> tableAreaList = DaoServiceUtil.getTableAreaService()
        .queryBuilder()
        .where(PxTableAreaDao.Properties.DelFlag.eq("0"))
         .orderAsc(PxTableAreaDao.Properties.Name)
        .list();
    if (!tableAreaList.isEmpty()) {
      for (int i = 0; i < tableAreaList.size(); i++) {
        PxTableArea tableArea = tableAreaList.get(i);
        AppTableType type = new AppTableType();
        type.setType(tableArea.getType());
        type.setTableArea(tableArea);
        mTableTypeList.add(type);
        //默认第一个桌台区域
        if (i == 0){
          mSearchTableType = tableArea.getType();
        }
      }
    }

    AppTableType typeAll = new AppTableType();
    typeAll.setType(AppTableType.ALL);
    typeAll.setName("全部桌台");

    AppTableType typeRetail = new AppTableType();
    typeRetail.setType(AppTableType.RETAIL);
    typeRetail.setName("零售单");

    mTableTypeList.add(typeAll);
    mTableTypeList.add(typeRetail);

    mTableTypeAdapter = new TableTypeAdapter(mAct, mTableTypeList);
    mLvCate.setAdapter(mTableTypeAdapter);

    mLvCate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        AppTableType appTableType = mTableTypeList.get(position);
        String tableType = appTableType.getType();
        switch (tableType){
          case AppTableType.ALL:
            mSearchTableType = ALL;
            break;
          case AppTableType.RETAIL:
            mSearchTableType = RETAIL;
            break;
          default:
            mSearchTableType = tableType;
            break;
        }
        if (mSearchUser != null) {
          showCollectionContent();
          EventBus.getDefault().postSticky(new CollectionContentEvent().setUser(mSearchUser).setTimeFilter(mSearchDate).setTableFilter(mSearchTableType));
        } else {
          ToastUtils.showShort(App.getContext(), "请选择用户");
        }
      }
    });
    mLvCate.setItemChecked(0, true);
  }

  /**
   * 初始化User列表
   */
  private void initRcv() {
    mUserList = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.DelFlag.eq("0"))
        .list();
    GridLayoutManager layoutManager = new GridLayoutManager(mAct, 5, LinearLayoutManager.VERTICAL, false);
    mRcv.setHasFixedSize(true);
    mRcv.setLayoutManager(layoutManager);
    mAdapter = new OverBillCollectionListAdapter(mAct, mUserList);
    mAdapter.setOnUserClickListener(this);
    mRcv.setAdapter(mAdapter);
    //间距设置
    int spaceWidth = getResources().getDimensionPixelSize(R.dimen.over_bill_rcv_item_horizontal_space_width);
    int spaceHeight = getResources().getDimensionPixelSize(R.dimen.over_bill_rcv_item_vertical_space_height);
    mRcv.addItemDecoration(new RecyclerViewSpaceItemDecoration(spaceWidth, spaceHeight));
  }

  /**
   * 选择日期
   */
  //@formatter:off
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
        if (mSearchUser != null) {
          showCollectionContent();
          CollectionContentEvent event = new CollectionContentEvent()
              .setUser(mSearchUser)
              .setTimeFilter(mSearchDate)
              .setTableFilter(mSearchTableType);
          EventBus.getDefault().postSticky(event);
        } else {
          ToastUtils.showShort(App.getContext(), "请选择用户");
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
   * 退出
   */
  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
  }

  /**
   * 隐藏Fragment
   */
  private void hideLeftFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        if (!(fragment instanceof OverBillCollectionListFragment)) {
          transaction.hide(fragment);
        }
      }
    }
  }

  /**
   * 显示左侧销售详情
   */
  //@formatter:off
  private void showCollectionContent() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideLeftFragment(transaction);
    mCollectionContentFragment = mFm.findFragmentByTag(Constants.OVER_BILL_COLLECTION_CONTENT);
    if (mCollectionContentFragment == null) {
      mCollectionContentFragment = OverBillCollectionContentFragment.newInstance("param");
      transaction.add(R.id.over_bill_content_left, mCollectionContentFragment, Constants.OVER_BILL_COLLECTION_CONTENT);
    } else {
      transaction.show(mCollectionContentFragment);
    }
    transaction.commit();
  }

  /**
   * Item点击
   */
  //@formatter:off
  @Override public void onUserClick(int pos) {
    mAdapter.setSelected(pos);
    User user = mUserList.get(pos);
    mSearchUser = user;
    showCollectionContent();
    EventBus.getDefault().postSticky(new CollectionContentEvent().setUser(mSearchUser).setTimeFilter(mSearchDate).setTableFilter(mSearchTableType));
  }
}

