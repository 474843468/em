package com.psi.easymanager.ui.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.CashCollectAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.event.ShiftChangeQueryInfoEvent;
import com.psi.easymanager.module.AppCashCollect;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.print.bt.BTPrintTask;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.print.module.ShiftWork;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.print.net.PrinterTask;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.usb.AppGpService;
import com.psi.easymanager.print.usb.AppUsbDeviceName;
import com.psi.easymanager.print.usb.PrinterUsbData;
import com.psi.easymanager.ui.activity.ShiftChangeFunctionsActivity;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.ListViewHeightUtil;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by psi on 2016/7/30.
 * 交接班-账单汇总信息
 */
public class ShiftOrderCollectFragment extends BaseFragment {

  private static final String SHIFT_ORDER_COLLECT_PARAM = "param";
  /**
   * 收银汇总
   */
  @Bind(R.id.lv_cash_collect) ListView mLvCashCollect;

  /**
   * 消费统计
   */
  //单数
  @Bind(R.id.tv_consume_statics_number) TextView mTvConsumeStaticsNumber;
  //人数
  @Bind(R.id.tv_consume_statics_people_number) TextView mTvConsumeStaticsPeopleNumber;
  //应收
  @Bind(R.id.tv_consume_statics_receivable) TextView mTvConsumeStaticsReceivable;
  //优惠
  @Bind(R.id.tv_consume_statics_privilege) TextView mTvConsumeStaticsPrivilege;
  //支付类优惠
  @Bind(R.id.tv_consume_statics_pay_privilege) TextView mTvConsumeStaticsPayPrivilege;
  //总价
  @Bind(R.id.tv_consume_statics_total_price) TextView mTvConsumeStaticsTotal;
  //损益
  @Bind(R.id.tv_consume_statics_gain_lose) TextView mTvConsumeStaticsGainLose;
  //实收
  @Bind(R.id.tv_consume_statics_received) TextView mTvConsumeStaticsReceived;
  //不计入
  @Bind(R.id.tv_consume_statics_exclusive) TextView mTvConsumeStaticsExclusive;

  private String mParam;
  private ShiftChangeFunctionsActivity mAct;

  private ShiftWork mShiftWork;//打印用
  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //单一线程用于打印
  private ExecutorService sDbEngine = null;

  public static ShiftOrderCollectFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    ShiftOrderCollectFragment fragment = new ShiftOrderCollectFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam = getArguments().getString(SHIFT_ORDER_COLLECT_PARAM);
    }
    mAct = (ShiftChangeFunctionsActivity) getActivity();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_shift_order_collect, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(ShiftChangeQueryInfoEvent.class);
  }

  /**
   * 打印账单汇总
   */
  @OnClick(R.id.ibtn_shift_print) public void printBillCollect(ImageButton iBtn) {
    if (mShiftWork == null) {
      ToastUtils.showShort(mAct.getApplicationContext(), "没有可打印的订单信息");
      return;
    }
    //网络打印
    printByNetAndBT();
    //2-USB打印
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
   * s网络收银打印
   */
  private void printByNetAndBT() {
    PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_SHIFT_WORK, mShiftWork);
    PrintTaskManager.printCashTask(task);
    BTPrintTask btPrintTask = new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_SHIFT_WORK)
        .shiftWork(mShiftWork)
        .build();
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
          PrinterUsbData.printAllOrderCollect(mAppGpService.getGpService(), mShiftWork);
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
   * 返回ShiftWork 交接班打印用
   */
  public ShiftWork getShiftWork() {
    return mShiftWork;
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
   * 重置注入
   */
  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
    closePool();
  }

  /**
   * 接收订单信息
   */
  //@formatter:off
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void getOrderInfoList(ShiftChangeQueryInfoEvent event) {
    int type = event.getType();
    if (type != ShiftChangeQueryInfoEvent.TYPE_ORDER_COLLECT) return;
    SQLiteDatabase db = DaoServiceUtil.getOrderDetailsDao().getDatabase();
    int area = event.getArea();
    long userId = event.getUserId();
    long endTime = event.getEndTime();
    mShiftWork = event.getShiftWork();
    //收银统计
    Cursor cashCollectCursor = null;
    //消费统计 Details表
    Cursor consumeCollectCursor = null;
    //消费统计 损益
    Cursor consumeCollectGainLoseCursor = null;
    //消费统计 实际收入
    Cursor consumeCollectIncomeCursor = null;
    //消费统计 不计入收益
    Cursor consumeCollectExclusive = null;
    switch (area) {
      /**
       * 所有
       */
      case ShiftChangeQueryInfoEvent.AREA_ALL:
          cashCollectCursor = db.rawQuery(
              "Select count(*),sum(pay.RECEIVED - pay.CHANGE),pay.PAYMENT_NAME"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.END_TIME < " + endTime
                  + " Group by pay.PAYMENT_NAME"
                  + " Having pay.PAYMENT_TYPE != " + PxPaymentMode.TYPE_TAIL
              ,null);
          consumeCollectCursor = db.rawQuery(
              "Select count(o._id),sum(o.ACTUAL_PEOPLE_NUMBER),sum(o.TOTAL_PRICE),sum(o.ACCOUNT_RECEIVABLE),sum(o.DISCOUNT_PRICE),sum(o.PAY_PRIVILEGE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.END_TIME < " + endTime
              , null);
          consumeCollectGainLoseCursor = db.rawQuery(
              "Select sum(o.REAL_PRICE - o.TOTAL_CHANGE + o.PAY_PRIVILEGE - o.ACCOUNT_RECEIVABLE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.END_TIME < " + endTime
              ,null);
          consumeCollectIncomeCursor= db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_TRUE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
        consumeCollectExclusive = db.rawQuery(
            "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_FALSE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
            ,null);
        break;
      /**
       * 桌台区域
       */
      case ShiftChangeQueryInfoEvent.AREA_TABLE:
          cashCollectCursor = db.rawQuery(
           "Select count(*),sum(pay.RECEIVED - pay.CHANGE),pay.PAYMENT_NAME"
                  + " From PxPayInfo pay "
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.END_TIME < " + endTime
                  + " Group by pay.PAYMENT_NAME"
                  + " Having pay.PAYMENT_TYPE != " + PxPaymentMode.TYPE_TAIL
              ,null);
          consumeCollectCursor = db.rawQuery(
              "Select count(o._id),sum(o.ACTUAL_PEOPLE_NUMBER),sum(o.TOTAL_PRICE),sum(o.ACCOUNT_RECEIVABLE),sum(o.DISCOUNT_PRICE),sum(o.PAY_PRIVILEGE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.END_TIME < " + endTime
              , null);
          consumeCollectGainLoseCursor = db.rawQuery(
              "Select sum(o.REAL_PRICE - o.TOTAL_CHANGE + o.PAY_PRIVILEGE- o.ACCOUNT_RECEIVABLE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.END_TIME < " + endTime
              ,null);
          consumeCollectIncomeCursor= db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_TRUE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
        consumeCollectExclusive = db.rawQuery(
            "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_FALSE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
            ,null);

        break;
      /**
       * 零售单
       */
      case ShiftChangeQueryInfoEvent.AREA_RETAIL:
          cashCollectCursor = db.rawQuery(
           "Select count(*),sum(pay.RECEIVED - pay.CHANGE),pay.PAYMENT_NAME"
                  + " From PxPayInfo pay "
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
                  + " And o.END_TIME < " + endTime
                  + " Group by pay.PAYMENT_NAME"
                  + " Having pay.PAYMENT_TYPE != " + PxPaymentMode.TYPE_TAIL
              ,null);
          consumeCollectCursor = db.rawQuery(
              "Select count(o._id),sum(o.ACTUAL_PEOPLE_NUMBER),sum(o.TOTAL_PRICE),sum(o.ACCOUNT_RECEIVABLE),sum(o.DISCOUNT_PRICE),sum(o.PAY_PRIVILEGE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
                  + " And o.END_TIME < " + endTime
              , null);
          consumeCollectGainLoseCursor = db.rawQuery(
              "Select sum(o.REAL_PRICE - o.TOTAL_CHANGE + o.PAY_PRIVILEGE- o.ACCOUNT_RECEIVABLE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
                  + " And o.END_TIME < " + endTime
              ,null);
          consumeCollectIncomeCursor = db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_TRUE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
        consumeCollectExclusive = db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_FALSE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
        break;
      /**
       * 大厅
       */
      case ShiftChangeQueryInfoEvent.AREA_HALL:
          cashCollectCursor = db.rawQuery(
           "Select count(*),sum(pay.RECEIVED - pay.CHANGE),pay.PAYMENT_NAME"
                  + " From PxPayInfo pay "
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_HALL
                  + " And o.END_TIME < " + endTime
                  + " Group by pay.PAYMENT_NAME"
                  + " Having pay.PAYMENT_TYPE != " + PxPaymentMode.TYPE_TAIL
              ,null);
          consumeCollectCursor = db.rawQuery(
              "Select count(o._id),sum(o.ACTUAL_PEOPLE_NUMBER),sum(o.TOTAL_PRICE),sum(o.ACCOUNT_RECEIVABLE),sum(o.DISCOUNT_PRICE),sum(o.PAY_PRIVILEGE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_HALL
                  + " And o.END_TIME < " + endTime
              , null);
          consumeCollectGainLoseCursor = db.rawQuery(
              "Select sum(o.REAL_PRICE - o.TOTAL_CHANGE + o.PAY_PRIVILEGE- o.ACCOUNT_RECEIVABLE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_HALL
                  + " And o.END_TIME < " + endTime
              ,null);
          consumeCollectIncomeCursor= db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_HALL
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_TRUE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
        consumeCollectExclusive = db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_HALL
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_FALSE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
        break;
      /**
       * 包间
       */
      case ShiftChangeQueryInfoEvent.AREA_PARLOR:
          cashCollectCursor = db.rawQuery(
           "Select count(*),sum(pay.RECEIVED - pay.CHANGE),pay.PAYMENT_NAME"
                  + " From PxPayInfo pay "
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_PARLOR
                  + " And o.END_TIME < " + endTime
                  + " Group by pay.PAYMENT_NAME"
                  + " Having pay.PAYMENT_TYPE != " + PxPaymentMode.TYPE_TAIL
              ,null);
          consumeCollectCursor = db.rawQuery(
              "Select count(o._id),sum(o.ACTUAL_PEOPLE_NUMBER),sum(o.TOTAL_PRICE),sum(o.ACCOUNT_RECEIVABLE),sum(o.DISCOUNT_PRICE),sum(o.PAY_PRIVILEGE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_PARLOR
                  + " And o.END_TIME < " + endTime
              , null);
          consumeCollectGainLoseCursor = db.rawQuery(
              "Select sum(o.REAL_PRICE - o.TOTAL_CHANGE + o.PAY_PRIVILEGE- o.ACCOUNT_RECEIVABLE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_PARLOR
                  + " And o.END_TIME < " + endTime
              ,null);
          consumeCollectIncomeCursor = db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_PARLOR
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_TRUE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
        consumeCollectExclusive = db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.SHIFT_CHANGE_TYPE = " + PxOrderInfo.SHIFT_CHANGE_FREEZE
                  + " And o.CHECK_OUT_USER_ID = " + userId
                  + " And o.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                  + " And o.FINAL_AREA = " + PxOrderInfo.FINAL_AREA_PARLOR
                  + " And o.END_TIME < " + endTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_FALSE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
        break;
    }

    //@formatter:on
    //收银汇总
    if (cashCollectCursor != null) {
      ArrayList<AppCashCollect> appCashCollectList = new ArrayList<AppCashCollect>();
      while (cashCollectCursor.moveToNext()) {
        AppCashCollect appCashCollect = new AppCashCollect();
        appCashCollect.setNum(cashCollectCursor.getInt(0));
        appCashCollect.setMoney(cashCollectCursor.getDouble(1));
        appCashCollect.setName(cashCollectCursor.getString(2));
        appCashCollectList.add(appCashCollect);
      }
      CashCollectAdapter cashCollectAdapter = new CashCollectAdapter(mAct, appCashCollectList);
      mLvCashCollect.setAdapter(cashCollectAdapter);
      ListViewHeightUtil.setListViewHeightBasedOnChildren(mLvCashCollect);
      IOUtils.closeCloseables(cashCollectCursor);
      //打印用
      mShiftWork.setCashCollectList(appCashCollectList);
    }

    //消费统计
    if (consumeCollectCursor != null) {
      while (consumeCollectCursor.moveToNext()) {
        //单数
        int billsCount = consumeCollectCursor.getInt(0);
        mTvConsumeStaticsNumber.setText(billsCount + "");
        //人数
        int peopleNum = consumeCollectCursor.getInt(1);
        mTvConsumeStaticsPeopleNumber.setText(peopleNum + "");
        //总价
        double totalPrice = consumeCollectCursor.getDouble(2);
        mTvConsumeStaticsTotal.setText(NumberFormatUtils.formatFloatNumber(totalPrice) + "");
        //应收
        double receivable = consumeCollectCursor.getDouble(3);
        mTvConsumeStaticsReceivable.setText(NumberFormatUtils.formatFloatNumber(receivable) + "");
        //优惠
        double privilege = consumeCollectCursor.getDouble(4);
        mTvConsumeStaticsPrivilege.setText(NumberFormatUtils.formatFloatNumber(privilege) + "");
        //支付类优惠
        double payPrivilege = consumeCollectCursor.getDouble(5);
        mTvConsumeStaticsPayPrivilege.setText(
            NumberFormatUtils.formatFloatNumber(payPrivilege) + "");

        mShiftWork.setBillsCount(billsCount);
        mShiftWork.setPeopleNum(peopleNum);
        mShiftWork.setAcceptAmount(receivable);
        mShiftWork.setDiscountAmount(privilege);
        mShiftWork.setTotalPrice(totalPrice);
        mShiftWork.setPayPrivilege(payPrivilege);
      }
      IOUtils.closeCloseables(consumeCollectCursor);
    }

    //消费统计 损益
    if (consumeCollectGainLoseCursor != null) {
      while (consumeCollectGainLoseCursor.moveToNext()) {
        double gainLose = consumeCollectGainLoseCursor.getDouble(0);
        mTvConsumeStaticsGainLose.setText(NumberFormatUtils.formatFloatNumber(gainLose) + "");
        mShiftWork.setGainLoseAmount(gainLose);
      }
      IOUtils.closeCloseables(consumeCollectGainLoseCursor);
    }

    //消费统计 实际收入
    if (consumeCollectIncomeCursor != null) {
      while (consumeCollectIncomeCursor.moveToNext()) {
        double income = consumeCollectIncomeCursor.getDouble(0);
        mTvConsumeStaticsReceived.setText(NumberFormatUtils.formatFloatNumber(income) + "");
        mShiftWork.setActualAmount(income);
      }
      IOUtils.closeCloseables(consumeCollectIncomeCursor);
    }

    //消费统计 不计入金额
    if (consumeCollectExclusive != null) {
      while (consumeCollectExclusive.moveToNext()) {
        double exclusive = consumeCollectExclusive.getDouble(0);
        mTvConsumeStaticsExclusive.setText(NumberFormatUtils.formatFloatNumber(exclusive) + "");
        mShiftWork.setStaticsExclusive(exclusive);
      }
      consumeCollectExclusive.close();
      consumeCollectExclusive = null;
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
    if (OpenPortEvent.SHIFT_BILL_CONNECT_PORT.equals(event.getType())) {
      //Gp是否支持USB打印
      String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
      if (isSupportUSBPrint.equals("1")) return;
      //是否已配置开启USB打印
      boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
      if (isPrint) {
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            PrinterUsbData.printAllOrderCollect(mAppGpService.getGpService(), mShiftWork);
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

  /**
   * 关闭线程
   */
  public void closePool() {
    if (sDbEngine != null) {
      sDbEngine.shutdown();
      sDbEngine = null;
    }
  }
}
