package com.psi.easymanager.ui.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.DetailCollectionAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxOrderNumDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.dao.dbUtil.DbCore;
import com.psi.easymanager.event.AutoOrderEvent;
import com.psi.easymanager.event.CashBillAddItemEvent;
import com.psi.easymanager.event.CashBillUpdateOrderEvent;
import com.psi.easymanager.event.ConfirmStartBillEvent;
import com.psi.easymanager.event.FindBillRefreshStatusEvent;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.RevokeOrFinishBillEvent;
import com.psi.easymanager.event.SendOrderToModifyBillEvent;
import com.psi.easymanager.event.SpeechEvent;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxOrderNum;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxSetInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.print.MergePrintDetails;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.bt.BTPrintTask;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.print.net.PrintLabelTaskManager;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.print.net.PrinterTask;
import com.psi.easymanager.print.usb.AppGpService;
import com.psi.easymanager.print.usb.AppUsbDeviceName;
import com.psi.easymanager.print.usb.PrinterUsbData;
import com.psi.easymanager.ui.activity.AddComboActivity;
import com.psi.easymanager.ui.activity.MainActivity;
import com.psi.easymanager.ui.activity.OrderedProdActivity;
import com.psi.easymanager.ui.activity.UnOrderProdActivity;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.utils.UserUtils;
import com.psi.easymanager.widget.RecycleViewDivider;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dorado on 2016/5/26.
 */
public class CashBillFragment extends BaseFragment {
  //桌台名
  @Bind(R.id.tv_table_name) TextView mTvTableName;
  //人数
  @Bind(R.id.tv_people_number) TextView mTvPeopleNumber;
  //开单时间
  @Bind(R.id.tv_order_info_start_time) TextView mTvOrderInfoStartTime;
  //订单编号
  @Bind(R.id.tv_order_info_code) TextView mTvOrderInfoCode;
  //Rcv
  @Bind(R.id.rcv) RecyclerView mRcv;
  //服务生
  @Bind(R.id.tv_waiter) TextView mTvWaiter;
  //取餐
  @Bind(R.id.tv_take_food) TextView mTvTakeFood;
  //下单
  @Bind(R.id.ibtn_order_bill) ImageButton mIbtnOrderBill;
  //有付款
  @Bind(R.id.iv_has_pay_info) ImageView mIvHasPayInfo;

  private static final int QUERY_DETAILS = 0;//query list
  private static final int ORDER_BILL = 1;//order bill

  //MainActivity
  private MainActivity mAct;
  //Fragment管理器
  private FragmentManager mFm;
  //mRcv LayoutManager
  private LinearLayoutManager mRcvLm;
  //当前订单信息
  public static PxOrderInfo mOrderInfo;
  //汇总Adapter
  private DetailCollectionAdapter mCollectionAdapter;
  //汇总数据
  public static List<PxOrderDetails> mDetailsList;

  //开单
  private Fragment mStartBillFragment;
  //改单
  private Fragment mModifyBillFragment;
  //找单
  private Fragment mFindBillFragment;
  //点菜
  private CashMenuFragment mCashMenuFragment;
  //Fragment用于配置后自动切换
  private Fragment mCashBillFragment;
  private CheckOutFragment mCheckOutFragment;

  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //订单日期用 sdf
  private SimpleDateFormat mSdfDate = new SimpleDateFormat("yyyyMMdd");
  //序列号用 sdf
  private SimpleDateFormat mSdfOrderReq = new SimpleDateFormat("yyyyMMddHHmmss");
  //单一线程用于打印
  private ExecutorService sDbEngine = null;
  //异步下单Session
  private AsyncSession mOrderAsyncSession;
  //异步刷新Session
  private AsyncSession mRefreshAsyncSession;
  private UIHandler mUiHandler;

  public static CashBillFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    CashBillFragment fragment = new CashBillFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (MainActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    mUiHandler = new UIHandler();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cash_bill, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    //初始化AsyncSession
    mOrderAsyncSession = DbCore.getDaoSession().startAsyncSession();
    mRefreshAsyncSession = DbCore.getDaoSession().startAsyncSession();
    //初始化Rcv
    initRcv();
    //查询数据库，查询最后开单的订单
    queryLastOrderInfo();
    //初始化View
    initBtnVisible();
  }

  private void initBtnVisible() {
    PxSetInfo setInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
    if (setInfo != null) {
      if (PxSetInfo.AUTO_ORDER_TRUE.equals(setInfo.getAutoOrder())) {
        mIbtnOrderBill.setVisibility(View.GONE);
      } else {
        mIbtnOrderBill.setVisibility(View.VISIBLE);
      }
    } else {
      mIbtnOrderBill.setVisibility(View.VISIBLE);
    }
  }

  /**
   * 呼叫取餐
   */
  //@formatter:on
  @OnClick(R.id.tv_take_food) public void takeFood() {
    if (mOrderInfo == null) {
      ToastUtils.showShort(mAct, "暂无订单");
      return;
    }
    long countOrder = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
        .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_ORDER))
        .count();
    if (countOrder == 0) {
      return;
    }
    String no = mOrderInfo.getOrderNo().substring(mOrderInfo.getOrderNo().length() - 6);
    String num = String.valueOf(Integer.parseInt(no));
    String content = "请" + num + "号到前台取餐";
    EventBus.getDefault().post(new SpeechEvent().setContent(content));
    //修改为已上菜
    for (PxOrderDetails details : mDetailsList) {
      if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)) {
        details.setIsServing(true);
      }
    }
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(mDetailsList);
    EventBus.getDefault().post(new RefreshCashBillListEvent());
  }

  /**
   * 初始化Rcv
   */
  //@formatter:off
  private void initRcv() {
    mRcvLm = new LinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false);
    mDetailsList = new ArrayList<>();
    mRcv.setHasFixedSize(true);
    mRcv.setLayoutManager(mRcvLm);
    mCollectionAdapter = new DetailCollectionAdapter(mAct, mDetailsList);
    mCollectionAdapter.setOnCollectionClickListener(new OnCollectionClickListener());
    mRcv.setAdapter(mCollectionAdapter);
    mRcv.addItemDecoration(new RecycleViewDivider(mAct, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.divider)));
  }
  /**
   * 查询数据库，获取全部未完成的OrderInfo
   */
  //@formatter:on
  private void queryLastOrderInfo() {
    //蒙层
    mAct.isShowProgress(true);
    mOrderInfo = DaoServiceUtil.getOrderInfoDao()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH))
        .orderDesc(PxOrderInfoDao.Properties.StartTime)
        .limit(1)
        .unique();
    //通知更新OrderInfo
    notifyUpdateOrderInfo();
    //显示订单基础数据
    refreshBasicInfo();
    //显示列表信息
    refreshList();
  }

  /**
   * 开单
   */
  //@formatter:off
  @OnClick(R.id.ibtn_start_bill) public void startBill() {
    //开启蒙层
    mAct.isShowProgress(true,null);
    //开单显示CashMenu界面
    showCashMenu();
    //查询商业模式
    PxSetInfo info = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
    if(info != null){
      if(info.getIsFastOpenOrder().equals("2")){//不是快速开单
        //隐藏按钮
        mAct.mCashFabs.setVisibility(View.GONE);
        //更新FindBill标签
        FindBillFragment.mCurrentLeftFragment = FindBillFragment.START_BILL;
        //更新Fragment
        FragmentTransaction transaction = mFm.beginTransaction();
        hideAllFragment(transaction);
        //开单
        mStartBillFragment = mFm.findFragmentByTag(Constants.START_BILL_TAG);
        if (mStartBillFragment == null) {
          mStartBillFragment = StartBillFragment.newInstance(null);
          transaction.add(R.id.cash_content_left, mStartBillFragment, Constants.START_BILL_TAG);
        } else {
          //重置StartBill显示
          ((StartBillFragment)mStartBillFragment).resetData();
          transaction.show(mStartBillFragment);
        }
        //找单
        mFindBillFragment = mFm.findFragmentByTag(Constants.FIND_BILL_TAG);
        if (mFindBillFragment == null) {
          mFindBillFragment = FindBillFragment.newInstance(false);
          transaction.add(R.id.cash_content_right, mFindBillFragment, Constants.FIND_BILL_TAG);
        } else {
          transaction.show( mFindBillFragment);
        }
        transaction.commitAllowingStateLoss();
      } else if(info.getIsFastOpenOrder().equals("1")){//是快速开单
        startRetailBill();
      }
    }
    //关闭蒙层
    mAct.isShowProgress(false);
    //回显MainActivity3个悬浮按钮
    mAct.mCashFabs.setVisibility(View.VISIBLE);
  }

  /**
   * 显示菜单页面
   */
  private void showCashMenu() {
    //更新Fragment
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    //客单
    mCashBillFragment = mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
    if (mCashBillFragment == null) {
      mCashBillFragment = CashBillFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mCashBillFragment, Constants.CASH_BILL_TAG);
    } else {
      transaction.show(mCashBillFragment);
    }
    //菜单
    mCashMenuFragment = (CashMenuFragment) mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    if (mCashMenuFragment == null) {
      mCashMenuFragment = CashMenuFragment.newInstance("param");
      transaction.add(R.id.cash_content_right, mCashMenuFragment, Constants.CASH_MENU_TAG);
    } else {
      transaction.show(mCashMenuFragment);
    }
    transaction.commitAllowingStateLoss();
  }



  /**
   * 生成reqNum
   */
  private String createOrderReqNum(int orderNum,User user) {
    //公司编码
    int companyHashCode = user.getCompanyCode().hashCode();
    //公司编码求绝对值
    int absHashCode = Math.abs(Integer.valueOf(companyHashCode));
    //公司编码绝对值的String表示
    String strHashCode = String.valueOf(absHashCode);
    //当前日期
    String strDate = mSdfOrderReq.format(new Date());
    //当前序号
    String strSer = String.format("%06d", orderNum);
    String strOrderNum = new StringBuilder().append(strHashCode).append(strDate).append(strSer).toString();
    return strOrderNum;
  }

  /**
   * 开单 零售单
   */
  private void startRetailBill() {
    //订单号
    PxOrderNum orderNum = DaoServiceUtil.getOrderNumService()
        .queryBuilder()
        .where(PxOrderNumDao.Properties.Date.eq(mSdfDate.format(new Date())))
        .unique();
    User loginUser = UserUtils.getLoginUser();
    if (loginUser == null) {
     ToastUtils.showShort(null,"请重启APP!");
      return;
    }

    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      mOrderInfo = new PxOrderInfo();
      //type
      mOrderInfo.setType(PxOrderInfo.TYPE_DEFAULT);
      //reverse
      mOrderInfo.setIsReserveOrder(PxOrderInfo.IS_REVERSE_ORDER_FALSE);
      //是否锁定状态 默认不锁定
      mOrderInfo.setIsLock(false);
      //开始时间
      mOrderInfo.setStartTime(new Date());
      //未完成
      mOrderInfo.setStatus(PxOrderInfo.STATUS_UNFINISH);
      //抹零
      mOrderInfo.setTailMoney((double) 0);
      //优惠金额
      mOrderInfo.setDiscountPrice((double) 0);
      //支付类优惠
      mOrderInfo.setPayPrivilege((double)0);
      //是否刷卡
      mOrderInfo.setUseVipCard(PxOrderInfo.USE_VIP_CARD_FALSE);
      //用户
      mOrderInfo.setDbUser(loginUser);
      //实收
      mOrderInfo.setRealPrice((double) 0);
      //应收
      mOrderInfo.setAccountReceivable((double) 0);
      //总的找零
      mOrderInfo.setTotalChange((double) 0);
      //订单类型
      mOrderInfo.setOrderInfoType(PxOrderInfo.ORDER_INFO_TYPE_RETAIL);

      if (orderNum == null) {
        PxOrderNum pxOrderNum = new PxOrderNum();
        pxOrderNum.setDate(mSdfDate.format(new Date()));
        pxOrderNum.setNum(1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(pxOrderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(pxOrderNum.getNum(),loginUser);
        mOrderInfo.setOrderNo(orderReqNum);
        mOrderInfo.setOrderReqNo(orderReqNum);
      } else {
        orderNum.setNum(orderNum.getNum() + 1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(orderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(orderNum.getNum(),loginUser);
        mOrderInfo.setOrderNo(orderReqNum);
        mOrderInfo.setOrderReqNo(orderReqNum);
      }
      //备注
      if (mOrderInfo.getRemarks() == null) {
        mOrderInfo.setRemarks("");
      }
      DaoServiceUtil.getOrderInfoService().save(mOrderInfo);
      db.setTransactionSuccessful();
      //找单页面更新
      EventBus.getDefault().post(new FindBillRefreshStatusEvent());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
    //通知更新OrderInfo
    notifyUpdateOrderInfo();
    //CashMenu更新角标
    notifyCashMenuRefreshProdNum();
    //显示订单基础信息
    refreshBasicInfo();
    //新开单，清空列表内容
    clearDetailsList();
  }

  /**
   * 改单
   */
  //@formatter:off
  @OnClick(R.id.ibtn_modify_bill) public void modifyBill() {
    //没有选择客单，则提示
    if (mOrderInfo == null) {
      ToastUtils.showShort(mAct, "请先选择客单");
      return;
    }
    //开启蒙层
    mAct.isShowProgress(true);
    //隐藏按钮
    mAct.mCashFabs.setVisibility(View.GONE);
    //向ModifyBill发送订单信息
    EventBus.getDefault().postSticky(new SendOrderToModifyBillEvent().setOrderInfo(mOrderInfo));
    //更新FindBill标签
    FindBillFragment.mCurrentLeftFragment = FindBillFragment.MODIFY_BILL;
    //更新Fragment
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    //改单
    mModifyBillFragment = mFm.findFragmentByTag(Constants.MODIFY_BILL_TAG);
    if (mModifyBillFragment == null) {
      mModifyBillFragment = ModifyBillFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mModifyBillFragment, Constants.MODIFY_BILL_TAG);
    } else {
      transaction.show(mModifyBillFragment);
    }
    if (mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
      //找单
      mFindBillFragment = mFm.findFragmentByTag(Constants.FIND_BILL_TAG);
      if (mFindBillFragment == null) {
        mFindBillFragment = FindBillFragment.newInstance(false);
        transaction.add(R.id.cash_content_right, mFindBillFragment, Constants.FIND_BILL_TAG);
      } else {
        transaction.show(mFindBillFragment);
      }
    }
    transaction.commitAllowingStateLoss();
    //关闭蒙层
    mAct.isShowProgress(false);
  }

  /**
   * 隐藏所有Fragment
   */
  //@formatter:off
  private void hideAllFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        transaction.hide(fragment);
      }
    }
  }

  /**
   * 显示基础信息
   */
  //@formatter:on
  private void refreshBasicInfo() {
    if (mOrderInfo != null) {//获取数据
      String orderNo = mOrderInfo.getOrderNo();
      mTvOrderInfoCode.setText("No." + orderNo.substring(orderNo.length() - 6, orderNo.length()));
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
      mTvOrderInfoStartTime.setText(sdf.format(mOrderInfo.getStartTime()));

      if (mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {//桌位单
        TableOrderRel tableOrderRel = DaoServiceUtil.getTableOrderRelService()
            .queryBuilder()
            .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
            .unique();
        mTvTableName.setText(tableOrderRel.getDbTable().getName());
        mTvPeopleNumber.setText(mOrderInfo.getActualPeopleNumber() + "");
      } else {//零售单
        mTvTableName.setText("零售单");
        mTvPeopleNumber.setText("无");
      }

      if (mOrderInfo.getDbWaiter() == null) {
        mTvWaiter.setText("服务生:无");
      } else {
        mTvWaiter.setText("服务生:" + mOrderInfo.getDbWaiter().getLoginName());
      }
      refreshHasPayInfo();
    } else {//全部撤单后，没有OrderInfo,则清空显示内容
      mTvOrderInfoCode.setText("");
      mTvTableName.setText("");
      mTvPeopleNumber.setText("");
      mTvOrderInfoStartTime.setText("");
      mTvWaiter.setText("");
      mDetailsList = new ArrayList<>();
      mCollectionAdapter.setData(mDetailsList);
      mIvHasPayInfo.setVisibility(View.GONE);
    }
  }

  /**
   * 刷新是否有付款信息
   */
  public void refreshHasPayInfo() {
    List<PxPayInfo> payInfoList = DaoServiceUtil.getPayInfoService()
        .queryBuilder()
        .where(PxPayInfoDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
        .list();
    mIvHasPayInfo.setVisibility(payInfoList.isEmpty() ? View.GONE : View.VISIBLE);
  }

  /**
   * 汇总点击
   */
  //@formatter:off
  private class OnCollectionClickListener implements DetailCollectionAdapter.OnCollectionClickListener {

    @Override public void onOrderedClick(int pos) {
      PxOrderDetails details = mDetailsList.get(pos);
      if (details.getDbProduct().getType().equals(PxProductInfo.TYPE_COMBO)) {
        Intent intent = new Intent(mAct, AddComboActivity.class);
        intent.putExtra(AddComboActivity.TYPE, AddComboActivity.TYPE_ORDERED);
        intent.putExtra(AddComboActivity.DETAILS_ID, details.getId());
        intent.putExtra(AddComboActivity.ORDER, mOrderInfo);
        startActivity(intent);
      } else {
        Intent intent = new Intent(mAct, OrderedProdActivity.class);
        intent.putExtra(OrderedProdActivity.INTENT_COLLECTION, details.getId());
        startActivity(intent);
      }
    }

    @Override public void onUnOrderClick(int pos) {
      PxOrderDetails details = mDetailsList.get(pos);
      if (details.getDbProduct().getType().equals(PxProductInfo.TYPE_COMBO)) {
        Intent intent = new Intent(mAct, AddComboActivity.class);
        intent.putExtra(AddComboActivity.TYPE, AddComboActivity.TYPE_EDIT);
        intent.putExtra(AddComboActivity.DETAILS_ID, details.getId());
        intent.putExtra(AddComboActivity.ORDER, mOrderInfo);
        startActivity(intent);
      } else {
        Intent intent = new Intent(mAct, UnOrderProdActivity.class);
        intent.putExtra(UnOrderProdActivity.INTENT_COLLECTION, details.getId());
        startActivity(intent);
      }
    }
  }

  /**
   * 新开单,清空列表内容,并恢复按钮状态
   */
  //@formatter:off
  private void clearDetailsList() {
    mDetailsList = new ArrayList<>();
    mCollectionAdapter.setData(mDetailsList);
    //通知CheckOut更新收款信息
    notifyUpdatePayInfo();
  }

  /**
   * CheckOut更新付款信息
   */
  private void notifyUpdatePayInfo(){
    if (mCheckOutFragment == null){
      mCheckOutFragment = (CheckOutFragment) mFm.findFragmentByTag(Constants.CHECK_OUT_TAG);
    }
    mCheckOutFragment.updatePayInfo();
  }

  /**
   * CashMenu和CheckOut更新OrderInfo
   */
  private void notifyUpdateOrderInfo(){
    if (mCheckOutFragment == null){
      mCheckOutFragment = (CheckOutFragment) mFm.findFragmentByTag(Constants.CHECK_OUT_TAG);
    }
    if (mCashMenuFragment == null){
      mCashMenuFragment = (CashMenuFragment) mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    }
    mCheckOutFragment.updateOrderInfo();
    mCashMenuFragment.updateOrderInfo();
  }

  /**
   * 退出
   */
  @Override public void onDestroy() {
    super.onDestroy();
    if (mUiHandler != null) {
      mUiHandler.removeCallbacksAndMessages(null);
    }
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
    closePool();
  }


  /**
   * 更新列表和数据
   */
  public void refreshList() {
    try {
      if (mOrderInfo == null) {
        mAct.isShowProgress(false);
      } else {
        getNewCollectionList();
      }
    } catch (Exception e) {
      mAct.isShowProgress(false);
    }
  }

  //@formatter:off
  private void getNewCollectionList() {
    //显示蒙层
    mAct.isShowProgress(true);
    //刷新订单
    DaoServiceUtil.getOrderInfoService().refresh(mOrderInfo);
    //
    mRefreshAsyncSession.runInTx(new Runnable() {
      @Override public void run() {
        //删除临时Details
        List<PxOrderDetails> temporaryDetailsList = DaoServiceUtil.getOrderDetailsService()
            .queryBuilder()
            .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
            .where(PxOrderDetailsDao.Properties.IsComboTemporaryDetails.eq(true))
            .list();
        DaoServiceUtil.getOrderDetailsService().delete(temporaryDetailsList);
        //查询当前订单Details
        Query<PxOrderDetails> detailsQuery = DaoServiceUtil.getOrderDetailsService()
            .queryBuilder()
            .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
            .orderAsc(PxOrderDetailsDao.Properties.OrderStatus, PxOrderDetailsDao.Properties.Status)
            .whereOr(PxOrderDetailsDao.Properties.InCombo.eq(PxOrderDetails.IN_COMBO_FALSE), PxOrderDetailsDao.Properties.InCombo.isNull())
            .build();
        List<PxOrderDetails> detailsList = detailsQuery.list();
        //handler msg
        sendHandlerMsg(QUERY_DETAILS,new ArrayList<>(detailsList));
      }
    });
  }

  /**
   * 商品添加后是否自动下单
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void setAutoOrde(AutoOrderEvent event) {
    boolean autoOrder = event.isAutoOrder();
    if (autoOrder) {
      mIbtnOrderBill.setVisibility(View.GONE);
    } else {
      mIbtnOrderBill.setVisibility(View.VISIBLE);
    }
  }

  /**
   * 下单
   */
  //@formatter:off
  @OnClick(R.id.ibtn_order_bill) public void orderBill() {
    if (mOrderInfo == null) {
      ToastUtils.showShort(mAct, "暂无订单，无法下单");
      return;
    }
    boolean allOrdered = true;
    for (PxOrderDetails orderDetails : mDetailsList) {
      if (PxOrderDetails.ORDER_STATUS_UNORDER.equals(orderDetails.getOrderStatus())) {
        allOrdered = false;
        break;
      }
    }
    if (allOrdered){
      return;
    }
    //Map 打印用
    final SparseArray<PrintDetailsCollect> collectArray = new SparseArray<>();
    //存放打印机IP
    final List<Long> printerIdList = new ArrayList<>();
    //存放标签打印机任务
    final List<PxOrderDetails> labelDetailsList = new ArrayList<>();

    //开启蒙层
    mAct.isShowProgress(true);
    //下单时间
    final Date orderTime = new Date();
    //下单
    mOrderAsyncSession.runInTx(new Runnable() {
      @Override public void run() {
        //合并打印内容
        MergePrintDetails.mergeByOrder(mOrderInfo,orderTime,collectArray,printerIdList,labelDetailsList);
        //未下单,非套餐相关
        List<PxOrderDetails> exceptComboList = DaoServiceUtil.getOrderDetailsService()
            .queryBuilder()
            .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
            .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_UNORDER))
            .whereOr(PxOrderDetailsDao.Properties.InCombo.eq(PxOrderDetails.IN_COMBO_FALSE), PxOrderDetailsDao.Properties.InCombo.isNull())
            .where(PxOrderDetailsDao.Properties.IsComboDetails.eq(PxOrderDetails.IS_COMBO_FALSE))
            .list();

        //遍历未下单列表
        for (PxOrderDetails details : exceptComboList) {
          PxProductInfo dbProduct = details.getDbProduct();
          details.setOrderStatus(PxOrderDetails.ORDER_STATUS_ORDER);
          //储存
          DaoServiceUtil.getOrderDetailsService().saveOrUpdate(details);

          //修改商品数量
          if (dbProduct.getSaleNum() == null) {
            dbProduct.setSaleNum(details.getNum().intValue());
          } else {
            dbProduct.setSaleNum(dbProduct.getSaleNum() + details.getNum().intValue());
          }
          DaoServiceUtil.getProductInfoService().saveOrUpdate(dbProduct);
        }

        //未下单,套餐
        List<PxOrderDetails> comboList = DaoServiceUtil.getOrderDetailsService()
            .queryBuilder()
            .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
            .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_UNORDER))
            .where(PxOrderDetailsDao.Properties.IsComboDetails.eq(PxOrderDetails.IS_COMBO_TRUE))
            .list();
        for (PxOrderDetails details : comboList) {
          if (details.getIsComboDetails().equals(PxOrderDetails.IS_COMBO_TRUE)) {
            details.setOrderStatus(PxOrderDetails.ORDER_STATUS_ORDER);
          }
          //修改商品数量
          PxProductInfo dbProduct = details.getDbProduct();
          if (dbProduct.getSaleNum() == null) {
            dbProduct.setSaleNum(details.getNum().intValue());
          } else {
            dbProduct.setSaleNum(dbProduct.getSaleNum() + details.getNum().intValue());
          }
          DaoServiceUtil.getProductInfoService().saveOrUpdate(dbProduct);
        }
        DaoServiceUtil.getOrderDetailsService().saveOrUpdate(comboList);

        //未下单,套餐内
        List<PxOrderDetails> inComboList = DaoServiceUtil.getOrderDetailsService()
            .queryBuilder()
            .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
            .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_UNORDER))
            .where(PxOrderDetailsDao.Properties.InCombo.eq(PxOrderDetails.IN_COMBO_TRUE))
            .list();
        for (PxOrderDetails details : inComboList) {
          details.setOrderStatus(PxOrderDetails.ORDER_STATUS_ORDER);
        }
        DaoServiceUtil.getOrderDetailsService().saveOrUpdate(inComboList);
        //后厨打印
        PrintTaskManager.printKitchenTask(collectArray, printerIdList, false);
        //标签打印
        PrintLabelTaskManager.printLabelTask(labelDetailsList);
        //下单成功
        mUiHandler.sendEmptyMessage(ORDER_BILL);
      }
    });
  }

  /**
   * 自动切换到结账页面
   */
  private void switchOverBill() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    //账单
    mCashBillFragment = mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
    if (mCashBillFragment == null) {
      mCashBillFragment = CashBillFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mCashBillFragment, Constants.CASH_BILL_TAG);
    } else {
      transaction.show(mCashBillFragment);
    }
    //结账
    mCheckOutFragment = (CheckOutFragment) mFm.findFragmentByTag(Constants.CHECK_OUT_TAG);
    if (mCheckOutFragment == null) {
      mCheckOutFragment = CheckOutFragment.newInstance("param");
      transaction.add(R.id.cash_content_right, mCheckOutFragment, Constants.CHECK_OUT_TAG);
    } else {
      transaction.show(mCheckOutFragment);
    }
    transaction.commitAllowingStateLoss();
  }

  /**
   * 接收Event,更新列表相关
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void refreshListAndData(RefreshCashBillListEvent event) {
    //更新列表
    refreshList();
    //更新基础信息
    refreshBasicInfo();
  }
  /**
   * 接收Event,更新列表相关
   */
 @Subscribe(threadMode = ThreadMode.MAIN) public void refreshListAndData(CashBillAddItemEvent event) {
   PxOrderDetails details = event.getDetails();
    if (null==details || mDetailsList == null) return;
   mDetailsList.add(0,details);
   mCollectionAdapter.notifyItemInserted(0);
   //更新基础信息
   //refreshBasicInfo();
   //通知CheckOut更新收款信息
   notifyUpdatePayInfo();
   //CashMenu页面更新角标和余量
   notifyCashMenuRefreshProdNum();
   //滑动到 0 位置 TODO
   //if (mRcvLm.findFirstCompletelyVisibleItemPosition() != 0){
     mRcv.smoothScrollToPosition(0);
   //}
   //关闭蒙层
   mAct.isShowProgress(false);
  }

  /**
   * 接收订单信息
   * 由StartBillFragment发送
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void onConfirmStartBillEvent(ConfirmStartBillEvent event) {
    boolean fromWaiter = event.isFromWaiter();
    if (fromWaiter) {
      if (mOrderInfo == null) {
        mOrderInfo = event.getOrderInfo();
        //通知更新OrderInfo
        notifyUpdateOrderInfo();
        //显示订单基础信息
        refreshBasicInfo();
        //刷新列表
        refreshList();
        //更新CashMenu角标
        notifyCashMenuRefreshProdNum();
      }
    } else {
      mOrderInfo = event.getOrderInfo();
      //通知更新OrderInfo
      notifyUpdateOrderInfo();
      //显示订单基础信息
      refreshBasicInfo();
      //新开单，清空列表内容
      clearDetailsList();
      //更新CashMenu角标
      notifyCashMenuRefreshProdNum();
    }
  }

  /**
   * CashMenu页面更新角标和余量
   */
  private void notifyCashMenuRefreshProdNum() {
    mCashMenuFragment = (CashMenuFragment) mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    if (mCashMenuFragment != null) {
      (mCashMenuFragment).updateProdNum(mOrderInfo);
    }
    Fragment fuzzyQueryFragment = mFm.findFragmentByTag(Constants.CASH_MENU_FUZZY_QUERY_TAG);
    if (fuzzyQueryFragment != null) {
      ((CashMenuFuzzyQueryFragment) fuzzyQueryFragment).updateProdNum(mOrderInfo);
    }
  }

  /**
   * 更新订单信息
   * 由FindBillFragment发送
   * 由OverBillDetailsContent发送
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void onReceiveTableInfoEvent(CashBillUpdateOrderEvent event) {
    mOrderInfo = event.getOrderInfo();
    //通知更新OrderInfo
    notifyUpdateOrderInfo();
    //更新列表
    refreshList();
    //更新基础信息
    refreshBasicInfo();
  }

  /**
   * 接收撤单或者结账完毕信息
   * 清空显示
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void revokeOrFinishBillEvent(RevokeOrFinishBillEvent event) {
    boolean autoStartBill = event.isAuto();
    //自动开单
    if (autoStartBill) {
      startRetailBill();
    } else {
      //查询最后一条
      queryLastOrderInfo();
      //更新脚标
      notifyCashMenuRefreshProdNum();
    }
  }

  /**
   * 接受由App发送的AppUsbDeviceName
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onDeviceNameEvent(
      AppUsbDeviceName appUsbDeviceName) {
    //ptksai pos 不支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;

    if (appUsbDeviceName == null) {
      ToastUtils.showShort(App.getContext(), "USB设备名为空");
      return;
    } else {
      mDeviceName = appUsbDeviceName.getDeviceName();
    }
  }

  /**
   * 接受由MainActivity发送的JbService
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onGpServiceEvent(
      AppGpService appGpService) {
    //ptksai pos 不支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;

    if (appGpService == null) {
      ToastUtils.showShort(mAct, "服务为空");
      return;
    } else {
      mAppGpService = appGpService;
      //检测USB并打开端口
      try {
        mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 打印
   */
  @OnClick(R.id.ibtn_print_bill) public void printBill() {
    if (mOrderInfo == null || mDetailsList == null || mDetailsList.size() == 0) return;
    boolean tempCollect = false;
    for (PxOrderDetails details : mDetailsList) {
      //存在下单或退单的 再打印
      if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)
          || details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_REFUND)) {
        tempCollect = true;
        break;
      }
    }
    if (!tempCollect) return;
    //网络打印
    printByNetAndBT();
    //USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    try {
      if (mAppGpService.getGpService().getPrinterConnectStatus(1) == Constants.USB_CONNECT_STATUS) {
        printByUSBPrinter();
      } else {
        mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
        printByUSBPrinter();
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 网络打印 收银
   */
  private void printByNetAndBT() {
    PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_DETAILS_COLLECTION, mOrderInfo, mDetailsList);
    PrintTaskManager.printCashTask(task);
    BTPrintTask btPrintTask = new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_DETAILS_COLLECTION)
        .orderInfo(mOrderInfo)
        .orderDetailsList(mDetailsList).build();
    PrintEventManager.getManager().postBTPrintEvent(btPrintTask);
  }

  /**
   * USB打印 收银
   */
  private void printByUSBPrinter() {
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          if (mOrderInfo != null) {
            PrinterUsbData.printOrderInfo(mAppGpService.getGpService(), mOrderInfo, mDetailsList);
          }
        }
      } catch (RemoteException e) {
        ToastUtils.showShort(mAct, "打印机异常:" + e.getMessage());
        e.printStackTrace();
      }
    } else {
      ToastUtils.showShort(mAct, "设备未连接,请在更多模块配置普通打印机!");
    }
  }

  /**
   * PrinterUsbData发送的未打开端口指令
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onOpenPort(
      final OpenPortEvent event) {
    if (sDbEngine == null) {
      sDbEngine = Executors.newSingleThreadExecutor();
    }
    sDbEngine.execute(new Runnable() {
      @Override public void run() {
        againPrintData(event);
      }
    });
  }

  /**
   * 重新打印数据
   */
  private void againPrintData(OpenPortEvent event) {
    if (OpenPortEvent.CASH_BILL_PORT.equals(event.getType())) {
      //Gp是否支持USB打印
      String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
      if (isSupportUSBPrint.equals("1")) return;
      //是否已配置开启USB打印
      boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
      if (isPrint) {
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            if (mOrderInfo != null) {
              PrinterUsbData.printOrderInfo(mAppGpService.getGpService(), mOrderInfo, mDetailsList);
            }
          }
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      } else {
        mAct.runOnUiThread(new Runnable() {
          @Override public void run() {
            ToastUtils.showShort(mAct, "设备未连接,请在更多模块配置普通打印机!");
          }
        });
      }
    }
  }
  //@formatter:on

  /**
   * 关闭线程
   */
  public void closePool() {
    if (sDbEngine != null) {
      sDbEngine.shutdown();
      sDbEngine = null;
    }
  }

  /**
   * send handler msg
   */
  private void sendHandlerMsg(int what, Object obj) {
    Message msg = Message.obtain();
    msg.what = what;
    msg.obj = obj;
    mUiHandler.sendMessage(msg);
  }

  /**
   * UI Handler
   */
  private class UIHandler extends Handler {
    @Override public void handleMessage(Message msg) {
      switch (msg.what) {
        case QUERY_DETAILS:
          //recyclerView整体刷新
          mDetailsList = (ArrayList<PxOrderDetails>) msg.obj;
          //设置给Adapter
          mCollectionAdapter.setData(mDetailsList);
          //通知CheckOut更新收款信息
          notifyUpdatePayInfo();
          //CashMenu页面更新角标和余量
          notifyCashMenuRefreshProdNum();
          //关闭蒙层
          mAct.isShowProgress(false);
          break;
        case ORDER_BILL:
          //刷新
          refreshList();
          //查询设置信息
          PxSetInfo pxSetInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
          //配置项启用是否自动切换到结账页面
          if (pxSetInfo != null && pxSetInfo.getIsAutoTurnCheckout().equals(PxSetInfo.AUTO_TURN_CHECKOUT_TRUE)) {
            switchOverBill();
            //回显MainActivity3个悬浮按钮
            mAct.mCashFabs.setVisibility(View.VISIBLE);
          }
          break;
      }
    }
  }
}