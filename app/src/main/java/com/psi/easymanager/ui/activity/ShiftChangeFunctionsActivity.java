package com.psi.easymanager.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.event.ShiftChangeQueryInfoEvent;
import com.psi.easymanager.module.AppShiftCateInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxRechargeRecord;
import com.psi.easymanager.module.User;
import com.psi.easymanager.print.bt.BTPrintTask;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.print.module.ShiftWork;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.print.net.PrinterTask;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.usb.AppGpService;
import com.psi.easymanager.print.usb.AppUsbDeviceName;
import com.psi.easymanager.print.usb.PrinterUsbData;
import com.psi.easymanager.ui.fragment.ShiftBillCollectFragment;
import com.psi.easymanager.ui.fragment.ShiftCateCollectFragment;
import com.psi.easymanager.ui.fragment.ShiftOrderCollectFragment;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.SwipeBackLayout;
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
 * Created by dorado on 2016/7/29.
 */
public class ShiftChangeFunctionsActivity extends BaseActivity {
  @Bind(R.id.rl_order_collect) RelativeLayout mRlOrderCollect;
  @Bind(R.id.rl_cate_collect) RelativeLayout mRlCateCollect;
  @Bind(R.id.rl_all_order) RelativeLayout mRlAllOrder;
  @Bind(R.id.tv_time) TextView mTvTime;//交接时间
  @Bind(R.id.tv_area) TextView mTvArea;//区域
  @Bind(R.id.tv_current_user) TextView mTvCurrentUser;//交班用户
  @Bind(R.id.tv_cashier) TextView mTvCashier;//收银员
  @Bind(R.id.swipe_back) SwipeBackLayout mSwipeBack;

  public static final String KEY_DATE = "KeyDate";
  public static final String KEY_USER = "KeyUser";
  public static final String KEY_AREA = "KeyArea";

  private Date mEndDate;//时间临界点
  private int mArea;//区域
  private int mCashier;//收银员

  //Fragment管理器
  private FragmentManager mFm;
  //ShiftOrderCollectFragment
  private ShiftOrderCollectFragment mShiftOrderCollectFragment;
  //ShiftCateCollectFragment
  private ShiftCateCollectFragment mShiftCateCollectFragment;
  //ShiftBillCollectFragment
  private Fragment mShiftBillCollectFragment;

  //UserId
  private long mUserId;

  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //单一线程用于打印
  private ExecutorService sDbEngine = null;
  //用于再次打印
  private ShiftWork mShiftPrint;

  @Override protected int provideContentViewId() {
    return R.layout.activity_shift_change_functions;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    //EventBus注册
    EventBus.getDefault().register(this);
    //Fragment管理器
    mFm = getSupportFragmentManager();
    mSwipeBack.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });

    mEndDate = (Date) getIntent().getSerializableExtra(KEY_DATE);
    mArea = getIntent().getIntExtra(KEY_AREA, 0);
    mCashier = getIntent().getIntExtra(KEY_USER, 0);
    //交接时间
    mTvTime.setText(" " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(mEndDate) + "");
    //交班用户
    if (App.getContext() != null) {
      User user = ((App) App.getContext()).getUser();
      if (user != null) {
        mTvCurrentUser.setText("" + user.getLoginName());
      }
    }
    //区域
    switch (mArea) {
      case ShiftChangeActivity.AREA_ALL:
        mTvArea.setText("全部");
        break;
      case ShiftChangeActivity.AREA_TABLE:
        mTvArea.setText("桌位单");
        break;
      case ShiftChangeActivity.AREA_RETAIL:
        mTvArea.setText("零售单");
        break;
    }
    //收银员
    App app = (App) App.getContext();
    if (app != null) {
      User user = app.getUser();
      if (user != null) {
        mUserId = user.getId();
        mTvCashier.setText(user.getName());
        queryOrderInfoData();
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
    closePool();
  }

  /**
   * 查询Data
   */
  //@formatter:off
  private void queryOrderInfoData() {
    SQLiteDatabase db = DaoServiceUtil.getOrderInfoDao().getDatabase();
    //订单cursor
    switch (mArea) {
      /**
       * 所有订单
       */
      case ShiftChangeActivity.AREA_ALL:
          //修改订单状态为冻结
            db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
                + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
                + " And CHECK_OUT_USER_ID = " + mUserId
                + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为冻结
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_FREEZE
             + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
             + " And USER_ID = " + mUserId
             + " And RECHARGE_TIME < " + mEndDate.getTime());
        break;
      /**
       * 桌台区域
       */
      case ShiftChangeActivity.AREA_TABLE:
            //修改订单状态为冻结
            db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
              + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
              + " And CHECK_OUT_USER_ID = " + mUserId
              + " And ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
              + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为冻结
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_FREEZE
              + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
              + " And USER_ID = " + mUserId
              + " And RECHARGE_TIME < " + mEndDate.getTime());
        break;
      /**
       * 零售单
       */
      case ShiftChangeActivity.AREA_RETAIL:
            //修改订单状态为冻结
            db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
              + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
              + " And CHECK_OUT_USER_ID = " + mUserId
              + " And ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
              + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为冻结
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_FREEZE
              + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
              + " And USER_ID = " + mUserId
              + " And RECHARGE_TIME < " + mEndDate.getTime());

        break;
      /**
       * 大厅
       */
      case ShiftChangeActivity.AREA_HALL:
            //修改订单状态为冻结
            db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
              + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
              + " And CHECK_OUT_USER_ID = " + mUserId
              + " And ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
              + " And FINAL_AREA = " + PxOrderInfo.FINAL_AREA_HALL
              + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为冻结
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_FREEZE
              + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
              + " And USER_ID = " + mUserId
              + " And RECHARGE_TIME < " + mEndDate.getTime());
        break;
      /**
       * 包间
       */
      case ShiftChangeActivity.AREA_PARLOR:
        //修改订单状态为冻结
          db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
              + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
              + " And CHECK_OUT_USER_ID = " + mUserId
              + " And ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
              + " And FINAL_AREA = " + PxOrderInfo.FINAL_AREA_PARLOR
              + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为冻结
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_FREEZE
              + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_UNHAND
              + " And USER_ID = " + mUserId
              + " And RECHARGE_TIME < " + mEndDate.getTime());
        break;
    }

    //默认选择第一项
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    orderCollect(transaction);
  }


  /**
   * 点击事件
   */
  @OnClick({ R.id.rl_order_collect, R.id.rl_cate_collect, R.id.rl_all_order })
  public void shiftInformation(RelativeLayout relativeLayout) {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    int id = relativeLayout.getId();
    switch (id) {
      //账单汇总
      case R.id.rl_order_collect:
        orderCollect(transaction);
        break;
      //分类汇总
      case R.id.rl_cate_collect:
        cateCollect(transaction);
        break;
      //所有账单
      case R.id.rl_all_order:
        billCollect(transaction);
        break;
    }
  }

  /**
   * 账单汇总
   */
  private void orderCollect(FragmentTransaction transaction) {
    if (mShiftOrderCollectFragment == null) {
      mShiftOrderCollectFragment = ShiftOrderCollectFragment.newInstance("param");
      transaction.add(R.id.fl_shift_content, mShiftOrderCollectFragment, Constants.SHIFT_ORDER_COLLECT_TAG);
    } else {
      transaction.show(mShiftOrderCollectFragment);
    }
    transaction.commit();

    //发 ShiftBillCollectFragment 所有账单信息
    ShiftWork shiftWork = getShiftWork();
    //发送查询信息
    ShiftChangeQueryInfoEvent shiftChangeQueryInfoEvent = new ShiftChangeQueryInfoEvent()
        .setArea(mArea)
        .setUserId(mUserId)
        .setEndTime(mEndDate.getTime())
        .setType(ShiftChangeQueryInfoEvent.TYPE_ORDER_COLLECT)
        .setShiftWork(shiftWork);
    EventBus.getDefault().postSticky(shiftChangeQueryInfoEvent);
  }

  /**
   * 获取打印用的shitwork
   */
  public ShiftWork getShiftWork() {
    ShiftWork shiftWork = new ShiftWork();
    shiftWork.setWorkZone(mTvArea.getText().toString());
    shiftWork.setShitTime(mEndDate);
    shiftWork.setShiftUserName(mTvCurrentUser.getText().toString());
    shiftWork.setCashierName(mTvCashier.getText().toString());
    return shiftWork;
  }

  /**
   * 分类汇总
   */
  private void cateCollect(FragmentTransaction transaction) {
    if (mShiftCateCollectFragment == null) {
      mShiftCateCollectFragment = ShiftCateCollectFragment.newInstance("param");
      transaction.add(R.id.fl_shift_content, mShiftCateCollectFragment, Constants.SHIFT_CATE_COLLECT_TAG);
    } else {
      transaction.show(mShiftCateCollectFragment);
    }
    transaction.commit();

  }

  /**
   * 所有账单
   */
  private void billCollect(FragmentTransaction transaction) {
    if (mShiftBillCollectFragment == null) {
      mShiftBillCollectFragment = ShiftBillCollectFragment.newInstance("param");
      transaction.add(R.id.fl_shift_content, mShiftBillCollectFragment, Constants.SHIFT_BILL_COLLECT_TAG);
    } else {
      transaction.show(mShiftBillCollectFragment);
    }
    transaction.commit();
    //发送查询信息
    EventBus.getDefault().postSticky(new ShiftChangeQueryInfoEvent().setArea(mArea).setUserId(mUserId).setEndTime(mEndDate.getTime()).setType(ShiftChangeQueryInfoEvent.TYPE_ALL_ORDER));

  }

  /**
   * 隐藏所有Fragment
   */
  private void hideAllFragment(FragmentTransaction transaction) {
    //获取栈内的所有fragment
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        transaction.hide(fragment);
      }
    }
  }

  /**
   * 交接
   */
  @OnClick(R.id.btn_confirm_shift_change) public void confirmShiftChange() {

    //对话框
    new MaterialDialog.Builder(this).title("是否交接")
        .content("点击确定将会完成交接，并跳转至登录界面")
        .positiveText("确认")
        .negativeText("取消")
        .negativeColor(getResources().getColor(R.color.primary_text))
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            //交接并跳转
            doShiftChangeAndTurnToLogin();
          }
        })
        .canceledOnTouchOutside(false)
        .show();
  }

  /**
   * 交接并跳转
   */
  private void doShiftChangeAndTurnToLogin() {
    //交接后统计数据
    shiftCollectData();

    SQLiteDatabase db = DaoServiceUtil.getOrderInfoDao().getDatabase();
    switch (mArea) {
      /**
       * 所有订单
       */
      case ShiftChangeActivity.AREA_ALL:
          //修改订单状态为交接
          db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_HANDED
              + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
              + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " And CHECK_OUT_USER_ID = " + mUserId
              + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为交接
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_HANDED
              + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " And USER_ID = " + mUserId
              + " And RECHARGE_TIME < " + mEndDate.getTime());
        break;
      /**
       * 桌台区域
       */
      case ShiftChangeActivity.AREA_TABLE:
          //修改订单状态为交接
          db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_HANDED
              + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
              + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " And CHECK_OUT_USER_ID = " + mUserId
              + " And ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
              + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为交接
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_HANDED
              + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " And USER_ID = " + mUserId
              + " And RECHARGE_TIME < " + mEndDate.getTime());
        break;
      /**
       * 零售单
       */
      case ShiftChangeActivity.AREA_RETAIL:
          //修改订单状态为交接
          db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_HANDED
              + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
              + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " And CHECK_OUT_USER_ID = " + mUserId
              + " And ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
              + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为交接
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_HANDED
              + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " And USER_ID = " + mUserId
              + " And RECHARGE_TIME < " + mEndDate.getTime());
        break;
      /**
       * 大厅
       */
      case ShiftChangeActivity.AREA_HALL:
          //修改订单状态为交接
          db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_HANDED
              + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
              + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " And CHECK_OUT_USER_ID = " + mUserId
              + " And ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
              + " And FINAL_AREA = " + PxOrderInfo.FINAL_AREA_HALL
              + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为交接
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_HANDED
              + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " And USER_ID = " + mUserId
              + " And RECHARGE_TIME < " + mEndDate.getTime());
        break;
      /**
       * 包间
       */
      case ShiftChangeActivity.AREA_PARLOR:
          //修改订单状态为交接
            db.execSQL("Update OrderInfo Set SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_HANDED
                + " Where STATUS = " + PxOrderInfo.STATUS_FINISH
                + " And SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                + " And CHECK_OUT_USER_ID = " + mUserId
                + " And ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                + " And FINAL_AREA = " + PxOrderInfo.FINAL_AREA_PARLOR
                + " And END_TIME < " + mEndDate.getTime());
          //修改充值状态为交接
          db.execSQL("Update RechargeRecord Set SHIFT_CHANGE_TYPE = " + PxRechargeRecord.SHIFT_CHANGE_HANDED
              + " Where SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
              + " And USER_ID = " + mUserId
              + " And RECHARGE_TIME < " + mEndDate.getTime());
        break;
    }

    //跳转
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
    ShiftChangeFunctionsActivity.this.finish();
  }

  /**
   * 交接后统计数据
   */
  private void shiftCollectData() {
    if (mShiftOrderCollectFragment == null){
      mShiftOrderCollectFragment = ShiftOrderCollectFragment.newInstance("param");
    }
    ShiftWork shiftWork = mShiftOrderCollectFragment.getShiftWork();
    if (shiftWork == null) return;
    if (mShiftCateCollectFragment != null) {
      List<AppShiftCateInfo> shiftCateInfoList = mShiftCateCollectFragment.getShiftCateInfoList();
      shiftWork.setCategoryCollectList(shiftCateInfoList);
    } else{
      List<AppShiftCateInfo> shiftCateInfoList = getCateInfoList();
      shiftWork.setCategoryCollectList(shiftCateInfoList);
    }
    //打印
    printShiftInfo(shiftWork);
    mShiftPrint = shiftWork;
  }

  /**
   * 获取所有分类汇总 可能会用到
   */
  public List<AppShiftCateInfo> getCateInfoList(){
    List<AppShiftCateInfo> cateInfoList = new ArrayList<>();
    //发送查询信息
    long endTime = mEndDate.getTime();
    SQLiteDatabase db = DaoServiceUtil.getOrderDetailsDao().getDatabase();
    Cursor categoryCursor = null;
    switch (mArea) {
      /**
       * 所有
       */

      case ShiftChangeQueryInfoEvent.AREA_ALL:
          categoryCursor = db.rawQuery(
              "Select sum(d.NUM),sum(d.FINAL_PRICE),sum(d.FINAL_PRICE * d.DISCOUNT_RATE / 100),c.NAME"
                  + " From OrderDetails d"
                  + " Join OrderInfo o On o._id = d.PX_ORDER_INFO_ID"
                  + " Join ProductInfo p On p._id = d.PX_PRODUCT_INFO_ID"
                  + " Join ProductCategory c On c._id = p.PX_PRODUCT_CATEGORY_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + mUserId
                  + " And o.END_TIME < " + endTime
                  + " And c.LEAF = " + PxProductCategory.IS_LEAF
                  + " And d.IN_COMBO = " + PxOrderDetails.IN_COMBO_FALSE
                  + " And d.ORDER_STATUS = " + PxOrderDetails.ORDER_STATUS_ORDER
                  + " Group By c._id", null);
        break;
      /**
       * 桌台区域
       */
      case ShiftChangeQueryInfoEvent.AREA_TABLE:
          categoryCursor = db.rawQuery(
              "Select sum(d.NUM),sum(d.FINAL_PRICE),sum(d.FINAL_PRICE * d.DISCOUNT_RATE / 100),c.NAME"
                  + " From OrderDetails d"
                  + " Join OrderInfo o On o._id = d.PX_ORDER_INFO_ID"
                  + " Join ProductInfo p On p._id = d.PX_PRODUCT_INFO_ID"
                  + " Join ProductCategory c On c._id = p.PX_PRODUCT_CATEGORY_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + mUserId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.END_TIME < " + endTime
                  + " And c.LEAF = " + PxProductCategory.IS_LEAF
                  + " And d.IN_COMBO = " + PxOrderDetails.IN_COMBO_FALSE
                  + " And d.ORDER_STATUS = " + PxOrderDetails.ORDER_STATUS_ORDER
                  + " Group By c._id", null);
        break;
      /**
       * 零售单
       */
      case ShiftChangeQueryInfoEvent.AREA_RETAIL:
          categoryCursor = db.rawQuery(
              "Select sum(d.NUM),sum(d.FINAL_PRICE),sum(d.FINAL_PRICE * d.DISCOUNT_RATE / 100),c.NAME"
                  + " From OrderDetails d"
                  + " Join OrderInfo o On o._id = d.PX_ORDER_INFO_ID"
                  + " Join ProductInfo p On p._id = d.PX_PRODUCT_INFO_ID"
                  + " Join ProductCategory c On c._id = p.PX_PRODUCT_CATEGORY_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + mUserId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
                  + " And o.END_TIME < " + endTime
                  + " And c.LEAF = " + PxProductCategory.IS_LEAF
                  + " And d.IN_COMBO = " + PxOrderDetails.IN_COMBO_FALSE
                  + " And d.ORDER_STATUS = " + PxOrderDetails.ORDER_STATUS_ORDER
                  + " Group By c._id", null);
        break;
      /**
       * 大厅
       */
      case ShiftChangeQueryInfoEvent.AREA_HALL:
          categoryCursor = db.rawQuery(
              "Select sum(d.NUM),sum(d.FINAL_PRICE),sum(d.FINAL_PRICE * d.DISCOUNT_RATE / 100),c.NAME"
                  + " From OrderDetails d"
                  + " Join OrderInfo o On o._id = d.PX_ORDER_INFO_ID"
                  + " Join ProductInfo p On p._id = d.PX_PRODUCT_INFO_ID"
                  + " Join ProductCategory c On c._id = p.PX_PRODUCT_CATEGORY_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + mUserId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_HALL
                  + " And o.END_TIME < " + endTime
                  + " And c.LEAF = " + PxProductCategory.IS_LEAF
                  + " And d.IN_COMBO = " + PxOrderDetails.IN_COMBO_FALSE
                  + " And d.ORDER_STATUS = " + PxOrderDetails.ORDER_STATUS_ORDER
                  + " Group By c._id",
              null);
        break;
      /**
       * 包间
       */
      case ShiftChangeQueryInfoEvent.AREA_PARLOR:
          categoryCursor = db.rawQuery(
              "Select sum(d.NUM),sum(d.FINAL_PRICE),sum(d.FINAL_PRICE * d.DISCOUNT_RATE / 100),c.NAME"
                  + " From OrderDetails d"
                  + " Join OrderInfo o On o._id = d.PX_ORDER_INFO_ID"
                  + " Join ProductInfo p On p._id = d.PX_PRODUCT_INFO_ID"
                  + " Join ProductCategory c On c._id = p.PX_PRODUCT_CATEGORY_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + mUserId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_PARLOR
                  + " And o.END_TIME < " + endTime
                  + " And c.LEAF = " + PxProductCategory.IS_LEAF
                  + " And d.IN_COMBO = " + PxOrderDetails.IN_COMBO_FALSE
                  + " And d.ORDER_STATUS = " + PxOrderDetails.ORDER_STATUS_ORDER
                  + " Group By c._id", null);
        break;
    }
    while (categoryCursor.moveToNext()) {
      AppShiftCateInfo shiftCateInfo = new AppShiftCateInfo();
      //设置数量
      shiftCateInfo.setCateNumber((int) categoryCursor.getDouble(0));
      //设置原价
      shiftCateInfo.setReceivableAmount(categoryCursor.getDouble(1));
      //设置实收
      shiftCateInfo.setActualAmount(categoryCursor.getDouble(2));
      //设置分类名
      shiftCateInfo.setCateName(categoryCursor.getString(3));
      //添加到list
      cateInfoList.add(shiftCateInfo);
    }
   return cateInfoList;

  }
  /**
   * 网络打印交接单
   */
  private void printByNet(ShiftWork mShiftWork) {
    if (mShiftWork == null) return;
    App app = (App) App.getContext();
    User user = app.getUser();
    mShiftWork.setCashierName((user == null) ? "admin" : user.getName());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    mShiftWork.setBusinessData(sdf.format(new Date()));
    mShiftWork.setTitle("收银员交接班-汇总信息");
    //收银机打印
    PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_SHIFT_WORK_DAILY_STATMENTS, mShiftWork);
    PrintTaskManager.printCashTask(task);
    //BT
    BTPrintTask btPrintTask = new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_SHIFT_WORK_DAILY_STATMENTS)
            .shiftWork(mShiftWork)
            .build();
    PrintEventManager.getManager().postBTPrintEvent(btPrintTask);
  }

  /**
   * USB打印 收银
   */
  private void printByUSBPrinter(ShiftWork mShiftWork){
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(ShiftChangeFunctionsActivity.this, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(ShiftChangeFunctionsActivity.this, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          if (mAppGpService.getGpService() != null && mShiftWork != null) {
            PrinterUsbData.printShiftCollect(mAppGpService.getGpService(), mShiftWork, this);
          }
        }
      } catch (RemoteException e) {
        ToastUtils.showShort(ShiftChangeFunctionsActivity.this, "打印机异常:" + e.getMessage());
        e.printStackTrace();
      }
    } else {
      ToastUtils.showShort(ShiftChangeFunctionsActivity.this, "设备未连接,请在更多模块配置普通打印机!");
    }
  }

  /**
   * 交接后打印统计数据
   */
  private void printShiftInfo(ShiftWork shiftWork) {
    //网络打印
    printByNet(shiftWork);
    //USB打印
    String isSupportUSBPrint = (String) SPUtils.get(this, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    try {
      if(mAppGpService.getGpService().getPrinterConnectStatus(1) == Constants.USB_CONNECT_STATUS){
        printByUSBPrinter(shiftWork);
      }else {
        mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
        printByUSBPrinter(shiftWork);
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 接受由App发送的AppUsbDeviceName
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onDeviceNameEvent(AppUsbDeviceName appUsbDeviceName) {
    //ptksai pos 不支持USB打印
    String  isSupportUSBPrint = (String) SPUtils.get(ShiftChangeFunctionsActivity.this, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;

    if(appUsbDeviceName == null){
      ToastUtils.showShort(App.getContext(),"USB设备名为空");
      return;
    }else {
      mDeviceName = appUsbDeviceName.getDeviceName();
    }
  }

  /**
   * 接受由MainActivity发送的JbService
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onGpServiceEvent(AppGpService appGpService) {
    //ptksai pos 不支持USB打印
    String  isSupportUSBPrint = (String) SPUtils.get(ShiftChangeFunctionsActivity.this, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;

    if(appGpService == null){
      ToastUtils.showShort(ShiftChangeFunctionsActivity.this,"服务为空");
      return;
    }else {
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
   * PrinterUsbData发送的未打开端口指令
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onOpenPort(final OpenPortEvent event) {
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
  private void againPrintData(OpenPortEvent event){
    if(OpenPortEvent.SHIFT_CONNECT_DATA_PORT.equals(event.getType())){
      //Gp是否支持USB打印
      String isSupportUSBPrint = (String) SPUtils.get(ShiftChangeFunctionsActivity.this, Constants.SUPPORT_USB_PRINT, "");
      if (isSupportUSBPrint.equals("1")) return;
      //是否已配置开启USB打印
      boolean isPrint = (boolean) SPUtils.get(ShiftChangeFunctionsActivity.this, Constants.SWITCH_ORDINARY_PRINT, true);
      if (isPrint) {
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            if (mAppGpService.getGpService() != null && mShiftPrint != null) {
              PrinterUsbData.printShiftCollect(mAppGpService.getGpService(), mShiftPrint, this);
            }
          }
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      } else {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            ToastUtils.showShort(ShiftChangeFunctionsActivity.this, "设备未连接,请在更多模块配置普通打印机!");
          }
        });
      }
    }
  }

  /**
   * 关闭线程
   */
  public void closePool(){
    if (sDbEngine != null){
      sDbEngine.shutdown();
      sDbEngine = null;
    }
  }
}
