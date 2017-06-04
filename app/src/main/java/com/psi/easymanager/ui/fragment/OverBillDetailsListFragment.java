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
import com.psi.easymanager.adapter.OverBillDetailsListAdapter;
import com.psi.easymanager.adapter.TableTypeAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.dao.dbUtil.DbCore;
import com.psi.easymanager.event.DetailsContentEvent;
import com.psi.easymanager.event.SaleContentEvent;
import com.psi.easymanager.module.AppTableType;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.User;
import com.psi.easymanager.ui.activity.OverBillActivity;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.DetachableClickListener;
import com.psi.easymanager.widget.RecyclerViewSpaceItemDecoration;
import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by zjq on 2016/6/6.
 * 已结账单 账单明细 列表页面
 */
public class OverBillDetailsListFragment extends BaseFragment
    implements OverBillDetailsListAdapter.OnDetailClickListener {

  public static final int TODAY = 0;
  public static final int YESTERDAY = 1;
  public static final int TWO_DAYS = 2;

  public static final String ALL = "2";
  public static final String RETAIL = "3";
  //收银员
  @Bind(R.id.tv_cashier) TextView mTvCashier;
  //日期
  @Bind(R.id.tv_date) TextView mTvDate;
  //账单rcv
  @Bind(R.id.rcv_bill) RecyclerView mRcvBill;
  //分类lv
  @Bind(R.id.lv_cate) ListView mLvCate;

  //每页数量
  private static final int PAGE_NUM = 20;
  //当前页码
  private int mCurrentPage = 1;
  //总页码
  private int mTotalPage;

  //桌台类型列表
  private List<AppTableType> mTableTypeList;
  //桌台类型适配器
  private TableTypeAdapter mTableTypeAdapter;

  //订单列表
  private List<PxOrderInfo> mOrderInfoList;
  //订单适配器
  private OverBillDetailsListAdapter mAdapter;

  //Activity
  private OverBillActivity mAct;
  //Fragment管理
  private FragmentManager mFm;

  //所有收银员
  List<User> mUserList;
  //所有收银员姓名
  private String[] mUserNames;
  //要搜索的User
  private User mSearchUser;
  //要检索的日期范围
  private int mSearchDate = TODAY;
  //要检索的桌台类型
  private String mSearchTableType = ALL;

  //左侧订单明细详情Fragment
  private Fragment mDetailsContentFragment;

  //异步AsyncSession
  private AsyncSession mQueryAsyncSession;

  public static OverBillDetailsListFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    OverBillDetailsListFragment fragment = new OverBillDetailsListFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (OverBillActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    //初始化AsyncSession
    mQueryAsyncSession = DbCore.getDaoSession().startAsyncSession();
    if (savedInstanceState != null) {
      mDetailsContentFragment = mFm.findFragmentByTag(Constants.OVER_BILL_DETAILS_CONTENT);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_over_bill_details_list, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    //初始化ListView
    initLv();
    //获取User列表
    initUserList();
    //初始化Rcv
    initRcv();
    //查询数量
    queryNum();
    //查询数据
    queryData();
  }

  /**
   * 上页 下页
   */
  @OnClick({ R.id.tv_last_page, R.id.tv_next_page }) public void changePage(TextView tv) {
    //查询总量
    queryNum();
    switch (tv.getId()) {
      case R.id.tv_last_page:
        if (mCurrentPage > 1) {
          mCurrentPage -= 1;
          //查询数据
          queryData();
        }
        break;
      case R.id.tv_next_page:
        if (mCurrentPage < mTotalPage) {
          mCurrentPage += 1;
          //查询数据
          queryData();
        }
        break;
    }
  }

  /**
   * 查询数量
   */
  private void queryNum() {
    //今日开始
    Date todayBegin = new Date();
    todayBegin.setHours(0);
    todayBegin.setMinutes(0);
    todayBegin.setSeconds(0);
    //今日结束
    Date todayEnd = new Date();
    todayEnd.setHours(23);
    todayEnd.setMinutes(59);
    todayEnd.setSeconds(59);
    //今日开始
    long todayBeginTime = todayBegin.getTime();
    //今日结束
    long todayEndTime = todayEnd.getTime();
    //昨日开始
    Date yesterdayBegin = new Date(todayBeginTime - 86400000);
    //昨日结束
    Date yesterdayEnd = new Date(todayEndTime - 86400000);

    //开始时间
    Date startDate = null;
    //结束时间
    Date endDate = null;

    if (mSearchDate == TODAY) {
      startDate = todayBegin;
      endDate = todayEnd;
    } else if (mSearchDate == YESTERDAY) {
      startDate = yesterdayBegin;
      endDate = yesterdayEnd;
    } else if (mSearchDate == TWO_DAYS) {
      startDate = yesterdayBegin;
      endDate = todayEnd;
    }

    long count = 0;
    if (mSearchUser == null) {
      switch (mSearchTableType) {
        case ALL:
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(
                  PxOrderInfoDao.Properties.IsReserveOrder.eq(PxOrderInfo.IS_REVERSE_ORDER_FALSE))
              .count();
          break;
        case RETAIL:
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_RETAIL))
              .count();
          break;
        default:
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(mSearchTableType))
              .count();
          break;
      }
    } else {
      switch (mSearchTableType) {
        case ALL:
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.UserId.eq(mSearchUser.getId()))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .count();
          break;
        case RETAIL:
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_RETAIL))
              .where(PxOrderInfoDao.Properties.UserId.eq(mSearchUser.getId()))
              .count();
          break;
        default:
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(mSearchTableType))
              .where(PxOrderInfoDao.Properties.UserId.eq(mSearchUser.getId()))
              .count();
          break;
      }
    }
    mTotalPage = (int) Math.ceil(count / (double) PAGE_NUM);
  }

  /**
   * 查询数据
   */
  private void queryData() {
    mAdapter.setData(new ArrayList<PxOrderInfo>());
    //今日开始
    Date todayBegin = new Date();
    todayBegin.setHours(0);
    todayBegin.setMinutes(0);
    todayBegin.setSeconds(0);
    //今日结束
    Date todayEnd = new Date();
    todayEnd.setHours(23);
    todayEnd.setMinutes(59);
    todayEnd.setSeconds(59);
    //今日开始
    long todayBeginTime = todayBegin.getTime();
    //今日结束
    long todayEndTime = todayEnd.getTime();
    //昨日开始
    Date yesterdayBegin = new Date(todayBeginTime - 86400000);
    //昨日结束
    Date yesterdayEnd = new Date(todayEndTime - 86400000);

    //开始时间
    Date startDate = null;
    //结束时间
    Date endDate = null;

    if (mSearchDate == TODAY) {
      startDate = todayBegin;
      endDate = todayEnd;
    } else if (mSearchDate == YESTERDAY) {
      startDate = yesterdayBegin;
      endDate = yesterdayEnd;
    } else if (mSearchDate == TWO_DAYS) {
      startDate = yesterdayBegin;
      endDate = todayEnd;
    }
    Query<PxOrderInfo> orderInfoQuery = null;
    if (mSearchUser == null) {
      switch (mSearchTableType) {
        case ALL:
          orderInfoQuery = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .build();
          break;
        case RETAIL:
          orderInfoQuery = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_RETAIL))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .build();
          break;
        default:
          orderInfoQuery = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(mSearchTableType))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .build();
          break;
      }
    } else {
      switch (mSearchTableType) {
        case ALL:
          orderInfoQuery = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.UserId.eq(mSearchUser.getId()))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .build();
          break;
        case RETAIL:
          orderInfoQuery = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_RETAIL))
              .where(PxOrderInfoDao.Properties.UserId.eq(mSearchUser.getId()))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .build();
          break;
        default:
          orderInfoQuery = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.EndTime.between(startDate, endDate))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(mSearchTableType))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .where(PxOrderInfoDao.Properties.UserId.eq(mSearchUser.getId()))
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .build();
          break;
      }
    }
    if (mQueryAsyncSession.getListener() == null) {
      mQueryAsyncSession.setListenerMainThread(new AsyncOperationListener() {
        @Override public void onAsyncOperationCompleted(AsyncOperation operation) {
          if (operation.isFailed()) ToastUtils.showShort(mAct, "查询失败,请重新查询");
          mOrderInfoList = (List<PxOrderInfo>) operation.getResult();
          mAdapter.setData(mOrderInfoList);
        }
      });
    }
    mQueryAsyncSession.queryList(orderInfoQuery);
  }

  /**
   * 初始化User列表
   */
  private void initUserList() {
    mUserList = DaoServiceUtil.getUserService()
        .queryBuilder()
        .where(UserDao.Properties.DelFlag.eq("0"))
        .list();
    //收银员姓名
    mUserNames = new String[mUserList.size() + 1];
    mUserNames[0] = "全部";
    for (int i = 0; i < mUserList.size(); i++) {
      mUserNames[i + 1] = mUserList.get(i).getName();
    }
  }

  /**
   * 初始化ListView
   */
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
        if (i == 0) {
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
        switch (tableType) {
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
        //重置位置
        mCurrentPage = 1;
        //查询数量
        queryNum();
        //查询数据
        queryData();
      }
    });
    mLvCate.setItemChecked(0, true);
  }

  /**
   * 初始化Rcv
   */
  private void initRcv() {
    GridLayoutManager layoutManager =
        new GridLayoutManager(mAct, 5, LinearLayoutManager.VERTICAL, false);
    mRcvBill.setHasFixedSize(true);
    mRcvBill.setLayoutManager(layoutManager);
    mOrderInfoList = new ArrayList<PxOrderInfo>();
    mAdapter = new OverBillDetailsListAdapter(mAct, mOrderInfoList);
    mAdapter.setOnDetailClickListener(this);

    //间距设置
    int spaceWidth =
        getResources().getDimensionPixelSize(R.dimen.over_bill_rcv_item_horizontal_space_width);
    int spaceHeight =
        getResources().getDimensionPixelSize(R.dimen.over_bill_rcv_item_vertical_space_height);
    mRcvBill.setAdapter(mAdapter);
    mRcvBill.addItemDecoration(new RecyclerViewSpaceItemDecoration(spaceWidth, spaceHeight));
  }

  /**
   * 选择收银员
   */
  @OnClick(R.id.tv_cashier) public void chooseCashier(View view) {
    final MaterialDialog selectUserDialog = DialogUtils.showListDialog(mAct, "收银员", mUserNames);
    MDButton posBtn = selectUserDialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = selectUserDialog.getActionButton(DialogAction.NEGATIVE);

    final DetachableClickListener posListener =
        DetachableClickListener.wrap(new View.OnClickListener() {
          @Override public void onClick(View v) {
            int which = selectUserDialog.getSelectedIndex();
            mTvCashier.setText(mUserNames[which]);
            if (which == 0) {
              mSearchUser = null;
            } else {
              mSearchUser = mUserList.get(which - 1);
            }
            //重置页码
            mCurrentPage = 1;
            //查询页码
            queryNum();
            //查询数据
            queryData();
            DialogUtils.dismissDialog(selectUserDialog);
          }
        });

    DetachableClickListener negListener = DetachableClickListener.wrap(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(selectUserDialog);
      }
    });

    posListener.clearOnDetach(selectUserDialog);
    negListener.clearOnDetach(selectUserDialog);
    posBtn.setOnClickListener(posListener);
    negBtn.setOnClickListener(negListener);
  }

  /**
   * 选择日期
   */
  @OnClick(R.id.tv_date) public void chooseDate(View view) {
    final String[] date = { "今日", "昨日", "两日内" };
    final MaterialDialog selectDateDialog = DialogUtils.showListDialog(mAct, "日期", date);
    MDButton negBtn = selectDateDialog.getActionButton(DialogAction.NEGATIVE);
    MDButton posBtn = selectDateDialog.getActionButton(DialogAction.POSITIVE);

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
        //重置页码
        mCurrentPage = 1;
        //查询页码
        queryNum();
        //查询数据
        queryData();
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
        if (!(fragment instanceof OverBillDetailsListFragment)) {
          transaction.hide(fragment);
        }
      }
    }
  }

  /**
   * Item Click
   */
  @Override public void onDetailClick(int pos) {
    mAdapter.setSelected(mAdapter.getCurrentSelected(), pos);
    PxOrderInfo orderInfo = mOrderInfoList.get(pos);
    showDetailsContent();
    EventBus.getDefault().postSticky(new DetailsContentEvent().setOrderInfo(orderInfo));
  }

  /**
   * 显示左侧销售详情
   */
  //@formatter:off
  private void showDetailsContent() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideLeftFragment(transaction);
    mDetailsContentFragment = mFm.findFragmentByTag(Constants.OVER_BILL_DETAILS_CONTENT);
    if (mDetailsContentFragment == null) {
      mDetailsContentFragment = OverBillDetailsContentFragment.newInstance("param");
      transaction.add(R.id.over_bill_content_left, mDetailsContentFragment, Constants.OVER_BILL_DETAILS_CONTENT);
    } else {
      transaction.show(mDetailsContentFragment);
    }
    transaction.commit();
  }
}

