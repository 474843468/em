package com.psi.easymanager.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.ReserveAdapter;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CancelReserveEvent;
import com.psi.easymanager.event.ReserveDetailEvent;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.ui.fragment.AddReserveFragment;
import com.psi.easymanager.ui.fragment.ReserveDetailFragment;
import com.psi.easymanager.utils.KeyBoardUtils;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.widget.RecyclerViewSpaceItemDecoration;
import com.psi.easymanager.widget.SwipeBackLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ReserveManagerActivity extends BaseActivity
    implements ReserveAdapter.OnItemClickListener {
  @Bind(R.id.content_view) SwipeBackLayout mContentView;//父布局
  @Bind(R.id.fl_left) FrameLayout mFlLeft;//left 容器
  @Bind(R.id.rcv_reserve_orders) RecyclerView mRcvReserveOrders;//预订单List
  @Bind(R.id.rl_add_reserve) RelativeLayout mRlLayout;//无预订单时界面
  @Bind(R.id.tv_reserve) TextView mTvReserve;//已约定
  @Bind(R.id.tv_ont_reach) TextView mTvReach;//已到达
  @Bind(R.id.tv_date) TextView mTvDate;//检索日期
  @Bind(R.id.et_search) EditText mEtSearch;//快速搜索框
  private FragmentManager mFm;
  private List<PxOrderInfo> mReserveOrderList;//预订单
  private ReserveAdapter mReserveAdapter;
  private static final String TODAY = "今日";//默认
  private static final String TODAY_BEFORE = "今日前";
  private static final String TODAY_AFTER = "今日后";
  private String nowCheckState = PxOrderInfo.RESERVE_STATE_RESERVE;//当前查询的状态  默认预定

  @Override protected int provideContentViewId() {
    return R.layout.activity_reserve_manager;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    //初始化滑动关闭
    initSwipeBack();
    EventBus.getDefault().register(this);
    //FragmentManager
    mFm = getSupportFragmentManager();
    //删除两天前过期的预定
    deleteOverTimeReserveOrder();
    initView();
    //监听快速搜素
    mEtSearch.addTextChangedListener(new SearchTextWatcher());
  }

  /**
   * 删除 两天前过期的预订单
   */
  private void deleteOverTimeReserveOrder() {
    //1年内
    Date dateBefore = new Date(System.currentTimeMillis() - 365 * 24 * 60 * 60 * 1000);
    //2天前
    Date dateAfter = new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
    //条件:预订单、预定类型、预定时间、订单状态
    List<PxOrderInfo> infoList = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.IsReserveOrder.eq(PxOrderInfo.IS_REVERSE_ORDER_TRUE))
        .where(PxOrderInfoDao.Properties.ReserveState.eq(PxOrderInfo.RESERVE_STATE_RESERVE))
        .where(PxOrderInfoDao.Properties.DiningTime.between(dateBefore, dateAfter))
        .where(PxOrderInfoDao.Properties.Status.isNull())
        .list();
    if (infoList == null || infoList.size() == 0) return;
    DaoServiceUtil.getOrderInfoService().delete(infoList);
  }

  /**
   * 初始化滑动关闭
   */
  private void initSwipeBack() {
    mContentView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        //隐藏软键盘
        if (mEtSearch.hasFocus()) {
          KeyBoardUtils.hideSoftInput(mEtSearch);
        }
        ReserveManagerActivity.this.finish();
      }
    });
  }

  /**
   * initView
   */
  private void initView() {
    //初始化Tab背景
    initTabBg();
    //默认加载已预订
    loadReserve();
    initRcv();
    mTvReserve.setBackgroundResource(R.drawable.bg_over_bill_tab_sel);
    //2.左侧
    if (mReserveOrderList == null || mReserveOrderList.size() == 0) {
      //无预订单时显示
      mRlLayout.setVisibility(View.VISIBLE);
    } else {
      mRlLayout.setVisibility(View.GONE);
      onItemClick(0);
    }
  }

  /**
   * initRcv
   */
  private void initRcv() {
    GridLayoutManager layoutManager =
        new GridLayoutManager(this, 6, LinearLayoutManager.VERTICAL, false);
    mReserveAdapter = new ReserveAdapter(this, mReserveOrderList);
    mReserveAdapter.setOnItemClick(this);
    mRcvReserveOrders.setHasFixedSize(true);
    mRcvReserveOrders.setLayoutManager(layoutManager);
    //间距设置
    int spaceWidth =
        getResources().getDimensionPixelSize(R.dimen.over_bill_rcv_item_horizontal_space_width);
    int spaceHeight =
        getResources().getDimensionPixelSize(R.dimen.over_bill_rcv_item_vertical_space_height);
    mRcvReserveOrders.setAdapter(mReserveAdapter);
    mRcvReserveOrders.addItemDecoration(
        new RecyclerViewSpaceItemDecoration(spaceWidth, spaceHeight));
  }

  /**
   * initTabBg
   */
  private void initTabBg() {
    mTvReserve.setBackgroundResource(R.drawable.bg_over_bill_tab_unsel);
    mTvReach.setBackgroundResource(R.drawable.bg_over_bill_tab_unsel);
  }

  /**
   * 快速搜索框
   */
  class SearchTextWatcher implements TextWatcher {

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      String input = s.toString().trim();
      //获取时间过滤
      Pair<Date, Date> datePair = getTimeFilter();
      mReserveOrderList = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .where(PxOrderInfoDao.Properties.ReserveState.eq(nowCheckState))
          .where(PxOrderInfoDao.Properties.DiningTime.between(datePair.first, datePair.second))
          .where(PxOrderInfoDao.Properties.ContactPhone.like("%" + input + "%"))
          .orderDesc(PxOrderInfoDao.Properties.DiningTime)
          .list();
      if (mReserveOrderList == null) {
        mReserveOrderList = new ArrayList<>();
      }
      resetLeftShow();
    }

    @Override public void afterTextChanged(Editable s) {

    }
  }

  /**
   * 显示预订单详情
   */
  private void showReserveDetailFragment(FragmentTransaction transaction) {
    Fragment fragment = mFm.findFragmentByTag(Constants.RESERVE_DETAIL);
    if (fragment == null) {
      fragment = ReserveDetailFragment.newInstance("param");
      transaction.add(R.id.fl_left, fragment, Constants.RESERVE_DETAIL);
    } else {
      transaction.show(fragment);
    }
    transaction.commit();
  }

  /**
   * 选择日期
   */
  //@formatter:off
  @OnClick(R.id.tv_date) public void chooseDate(View view) {
    final String[] date = { "今日", "今日前", "今日后" };
    final MaterialDialog selectDialog = DialogUtils.showListDialog(ReserveManagerActivity.this, "日期", date);
    MDButton posBtn = selectDialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = selectDialog.getActionButton(DialogAction.NEGATIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int selectedIndex = selectDialog.getSelectedIndex();
        DialogUtils.dismissDialog(selectDialog);
        selectDate(date[selectedIndex]);

      }
    });
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
      DialogUtils.dismissDialog(selectDialog);
      }
    });
  }

  /**
   * 选日期
   */
  private void selectDate(String selectDate) {
    mTvDate.setText(selectDate);
    loadReserve();
    resetLeftShow();
    mEtSearch.setText(null);
  }

  @OnClick({ R.id.tv_reserve, R.id.tv_ont_reach }) public void reserveStateSelect(TextView tv) {
    initTabBg();
    tv.setBackgroundResource(R.drawable.bg_over_bill_tab_sel);
    switch (tv.getId()) {
      case R.id.tv_reserve://已预订
        nowCheckState = PxOrderInfo.RESERVE_STATE_RESERVE;
        break;
      case R.id.tv_ont_reach://已到达
        nowCheckState = PxOrderInfo.RESERVE_STATE_REACH;
        break;
    }
    //恢复
    selectDate(TODAY);
  }

  /**
   * 重置左侧显示
   */
  private void resetLeftShow() {
    //左侧
    FragmentTransaction transaction = mFm.beginTransaction();
    hideLeftAllFragment(transaction);
    transaction.commit();
    //右侧内容
    mReserveAdapter.setData(mReserveOrderList);
    //默认点击第一个
    if (mReserveOrderList == null || mReserveOrderList.size() == 0) {
      //有没有其他预定单
      mRlLayout.setVisibility(View.VISIBLE);
    } else {
      mRlLayout.setVisibility(View.GONE);
      onItemClick(0);
    }
  }

  /**
   * 获取时间 过滤条件
   */
  private Pair<Date, Date> getTimeFilter() {
    ////今日开始
    //Date todayBegin = new Date();
    //todayBegin.setHours(0);
    //todayBegin.setMinutes(0);
    //todayBegin.setSeconds(0);
    ////今日开始
    //long todayBeginTime = todayBegin.getTime();
    ////无止境
    //Date todayEnd = new Date(Long.MAX_VALUE);
    ////今日结束
    //long todayEndTime = todayEnd.getTime();
    ////昨日开始
    //Date yesterdayBegin = new Date(todayBeginTime - 86400000);
    ////昨日结束
    //Date yesTodyEnd = new Date();
    //yesTodyEnd.setHours(23);
    //yesTodyEnd.setMinutes(59);
    //yesTodyEnd.setSeconds(59);
    //Date yesterdayEnd = new Date(yesTodyEnd.getTime() - 86400000);
    //今日
    Date todayBegin = new Date();
    todayBegin.setHours(0);
    todayBegin.setMinutes(0);
    todayBegin.setSeconds(0);
    Date todayEnd = new Date();
    todayEnd.setHours(23);
    todayEnd.setMinutes(59);
    todayEnd.setSeconds(59);
    //今日前
    Date before = new Date(0);
    //今日后
    Date after = new Date(Long.MAX_VALUE);
    Pair<Date, Date> datePair = null;
    switch (mTvDate.getText().toString()) {
      case TODAY:
        datePair = new Pair<>(todayBegin, todayEnd);
        break;
      case TODAY_BEFORE:
        datePair = new Pair<>(before, todayBegin);
        break;
      case TODAY_AFTER:
        datePair = new Pair<>(todayEnd, after);
        break;
    }
    return datePair;
  }

  /**
   * 加载数据
   */
  private void loadReserve() {
    //获取时间过滤
    Pair<Date, Date> datePair = getTimeFilter();
    mReserveOrderList = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.ReserveState.eq(nowCheckState))
        .where(PxOrderInfoDao.Properties.DiningTime.between(datePair.first, datePair.second))
        .orderDesc(PxOrderInfoDao.Properties.DiningTime)
        .list();
    if (mReserveOrderList == null) {
      mReserveOrderList = new ArrayList<>();
    }
  }

  /**
   * item点击
   */
  @Override public void onItemClick(int pos) {
    if (mReserveOrderList == null || mReserveOrderList.size() < (pos - 1)) return;
    FragmentTransaction transaction = mFm.beginTransaction();
    hideLeftAllFragment(transaction);
    showReserveDetailFragment(transaction);

    PxOrderInfo reserveOrder = mReserveOrderList.get(pos);
    //发送ReserveOrder
    EventBus.getDefault().postSticky(new ReserveDetailEvent(reserveOrder));
    mReserveAdapter.setSelected(pos);
  }

  /**
   * 新加预定
   */
  @OnClick(R.id.ibtn_add_no) public void addReserve(ImageButton iBtn) {
    //关闭 ViewStub
    mRlLayout.setVisibility(View.GONE);
    Fragment fragment = mFm.findFragmentByTag(Constants.ADD_RESERVE);
    //显示 预订单开单界面
    FragmentTransaction transaction = mFm.beginTransaction();
    if (fragment == null) {
      fragment = AddReserveFragment.newInstance("param");
      transaction.add(R.id.fl_left, fragment, Constants.ADD_RESERVE);
    } else {
      transaction.show(fragment);
    }
    transaction.commit();
  }

  /**
   * 接收新增预订单 取消event
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveCancelEvent(
      CancelReserveEvent event) {
    //加载
    loadReserve();
    mReserveAdapter.setData(mReserveOrderList);
    if (mReserveOrderList.size() == 0) { //有没有其他预定单
      mRlLayout.setVisibility(View.VISIBLE);
    } else {//有 默认选中第一个
      mRlLayout.setVisibility(View.GONE);
      onItemClick(0);
      mReserveAdapter.setSelected(0);
    }
  }

  /**
   * 接收新增预订单 确认Event
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveAddEvent(ReserveDetailEvent event) {
    boolean add = event.isAdd();
    if (!add) return;
    //加载已预订的
    loadReserve();
    mRlLayout.setVisibility(View.GONE);
    mReserveAdapter.setData(mReserveOrderList);
    PxOrderInfo reserveOrder = event.getReserveOrder();
    int pos = mReserveOrderList.indexOf(reserveOrder);
    if (pos < 0 || pos > (mReserveOrderList.size() - 1)) return;
    mReserveAdapter.setSelected(pos);
  }

  /**
   * 接收修改的预订单详情 更新 ReserveAdapter
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveModifyReserve(
      ReserveDetailEvent event) {
    boolean modify = event.isModify();
    if (!modify) return;
    int currentPos = mReserveAdapter.getCurrentPos();
    PxOrderInfo reserveOrder = event.getReserveOrder();
    mReserveAdapter.insteadItem(currentPos, reserveOrder);
  }

  /**
   * 隐藏左侧所有
   */
  public void hideLeftAllFragment(FragmentTransaction transaction) {
    List<Fragment> fragments = mFm.getFragments();
    if (fragments != null) {
      for (Fragment fragment : fragments) {
        if (fragment.getId() == R.id.fl_left) {
          transaction.hide(fragment);
        }
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
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
  }
}
