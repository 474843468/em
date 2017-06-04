package com.psi.easymanager.ui.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CollectionContentEvent;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.module.AppBillCount;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.User;
import com.psi.easymanager.print.bt.BTPrintTask;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.print.net.PrinterTask;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.usb.AppGpService;
import com.psi.easymanager.print.usb.AppUsbDeviceName;
import com.psi.easymanager.print.usb.PrinterUsbData;
import com.psi.easymanager.ui.activity.OverBillActivity;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dorado on 2016/6/7.
 */
public class OverBillCollectionContentFragment extends BaseFragment {

  @Bind(R.id.tv_user_name) TextView mTvUserName;
  @Bind(R.id.tv_receivable) TextView mTvReceivable;
  @Bind(R.id.tv_received) TextView mTvReceived;
  @Bind(R.id.tv_change) TextView mTvChange;
  @Bind(R.id.tv_tail) TextView mTvTail;
  @Bind(R.id.tv_pay_privilege) TextView mTvPayPrivilege;

  public static final int TODAY = 0;
  public static final int YESTERDAY = 1;
  public static final int TWO_DAYS = 2;

  public static final String ALL = "2";
  public static final String RETAIL = "3";

  //Activity
  private OverBillActivity mAct;
  //Fragment管理
  private FragmentManager mFm;

  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //账单汇总
  private CollectionContentEvent mCollectionContent;
  //账单汇总实体
  private AppBillCount mAppBillCount;
  //单一线程用于打印
  private ExecutorService sDbEngine = null;

  public static OverBillCollectionContentFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    OverBillCollectionContentFragment fragment = new OverBillCollectionContentFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (OverBillActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    if (savedInstanceState != null) {

    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_over_bill_collection_content, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(CollectionContentEvent.class);
    EventBus.getDefault().getStickyEvent(AppGpService.class);
  }

  /**
   * 接收信息
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void showCollectionContent(
      CollectionContentEvent event) {
    mCollectionContent = event;
    final User user = event.getUser();
    final int timeFilter = event.getTimeFilter();
    final String tableFilter = event.getTableFilter();
    mTvUserName.setText(user.getName());
    //清空显示
    //应收
    mTvReceivable.setText(0.0 + "");
    //找零
    mTvChange.setText(0.0 + "");
    //抹零
    mTvTail.setText(0.0 + "");
    //应收
    mTvReceived.setText(0.0 + "");
    //查询数据
    new Thread() {
      @Override public void run() {
        queryData(user, timeFilter, tableFilter);
      }
    }.start();
  }

  /**
   * 查询数据
   */
  //@formatter:off
  private void queryData(User user, int timeFilter, String tableFilter) {
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

    if (timeFilter == TODAY) {
      startDate = todayBegin;
      endDate = todayEnd;
    } else if (timeFilter == YESTERDAY) {
      startDate = yesterdayBegin;
      endDate = yesterdayEnd;
    } else if (timeFilter == TWO_DAYS) {
      startDate = yesterdayBegin;
      endDate = todayEnd;
    }

    SQLiteDatabase database = DaoServiceUtil.getPayInfoDao().getDatabase();
    Cursor cursorPayInfo = null;
    Cursor cursorOrder = null;
    switch (tableFilter) {
      case ALL:
        cursorPayInfo = database.rawQuery(
            "Select sum(pay.RECEIVED),pay.PAYMENT_NAME"
                + " From PxPayInfo pay"
                + " Join OrderInfo oi On oi._id = pay.PX_ORDER_INFO_ID"
                + " Where oi.STATUS = " + PxOrderInfo.STATUS_FINISH
                + " And oi.END_TIME between " + startDate.getTime()
                + " And " + endDate.getTime()
                + " And oi.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                + " And oi.USER_ID = " + user.getId()
                + " Group by pay.PAYMENT_NAME"
                + " Having pay.PAYMENT_TYPE != "
                + PxPaymentMode.TYPE_TAIL, null);
        cursorOrder = database.rawQuery(
            "Select sum(oi.ACCOUNT_RECEIVABLE),sum(oi.TOTAL_CHANGE),sum(oi.TAIL_MONEY),sum(oi.PAY_PRIVILEGE)"
                + " From OrderInfo oi" + " Where oi.STATUS = " + PxOrderInfo.STATUS_FINISH
                + " and oi.END_TIME between " + startDate.getTime() + " and " + endDate.getTime()
                + " and oi.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                + " and oi.USER_ID = " + user.getId(), null);
        break;
      case RETAIL:
        cursorPayInfo = database.rawQuery(
            "Select sum(pay.RECEIVED),pay.PAYMENT_NAME"
                + " From PxPayInfo pay"
                + " Join OrderInfo oi On oi._id = pay.PX_ORDER_INFO_ID"
                + " Where oi.STATUS = " + PxOrderInfo.STATUS_FINISH
                + " and oi.END_TIME between " + startDate.getTime()
                + " and " + endDate.getTime()
                + " and oi.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
                + " and oi.USER_ID = " + user.getId()
                + " Group by pay.PAYMENT_NAME"
                + " Having pay.PAYMENT_TYPE != "
                + PxPaymentMode.TYPE_TAIL, null);
        cursorOrder = database.rawQuery(
            "Select sum(oi.ACCOUNT_RECEIVABLE),sum(oi.TOTAL_CHANGE),sum(oi.TAIL_MONEY),sum(oi.PAY_PRIVILEGE)"
                + " From OrderInfo oi" + " Where oi.STATUS = " + PxOrderInfo.STATUS_FINISH
                + " and oi.END_TIME between " + startDate.getTime() + " and " + endDate.getTime()
                + " and oi.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_RETAIL
                + " and oi.USER_ID = " + user.getId(), null);
        break;
      default:
        String tableCondition = (!ALL.equals(tableFilter) && !RETAIL.equals(tableFilter)) ? "\"" + tableFilter +"\"" : tableFilter;
        cursorPayInfo = database.rawQuery(
            "Select sum(pay.RECEIVED),pay.PAYMENT_NAME" + " From PxPayInfo pay"
                + " Join OrderInfo oi On oi._id = pay.PX_ORDER_INFO_ID"
                + " Where oi.STATUS = " + PxOrderInfo.STATUS_FINISH
                + " And oi.END_TIME between " + startDate.getTime()
                + " And " + endDate.getTime()
                + " And oi.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                + " And oi.FINAL_AREA = " + tableCondition
                + " And oi.USER_ID = " + user.getId()
                + " Group by pay.PAYMENT_NAME"
                + " Having pay.PAYMENT_TYPE != " + PxPaymentMode.TYPE_TAIL, null);
        cursorOrder = database.rawQuery(
            "Select sum(oi.ACCOUNT_RECEIVABLE),sum(oi.TOTAL_CHANGE),sum(oi.TAIL_MONEY),sum(oi.PAY_PRIVILEGE)"
                + " From OrderInfo oi"
                + " Where oi.STATUS = " + PxOrderInfo.STATUS_FINISH
                + " And oi.END_TIME between " + startDate.getTime()
                + " And " + endDate.getTime()
                + " And oi.ORDER_INFO_TYPE = " + PxOrderInfo.ORDER_INFO_TYPE_TABLE
                + " And oi.FINAL_AREA = " + tableCondition
                + " And oi.USER_ID = " + user.getId(), null);
        break;
    }

    //@formatter:on
    final StringBuilder sb = new StringBuilder();
    //各项实收载体 用于打印
    final List<Pair<String, String>> everyReceivedList = new ArrayList<>();
    while (cursorPayInfo.moveToNext()) {
      //实收
      Double received = cursorPayInfo.getDouble(0);
      String name = cursorPayInfo.getString(1);
      String receivedMoney = NumberFormatUtils.formatFloatNumber(received);
      sb.append(name + ":\t" + receivedMoney + "\n");

      Pair<String, String> pair = new Pair<>(name, receivedMoney);
      everyReceivedList.add(pair);

      //各项实收
      mAct.runOnUiThread(new Runnable() {
        @Override public void run() {
          mTvReceived.setText(sb.toString());
        }
      });
    }

    if (cursorPayInfo != null && !cursorPayInfo.isClosed()) {
      cursorPayInfo.close();
      cursorPayInfo = null;
    }

    while (cursorOrder.moveToNext()) {
      //应收
      final double receivable = cursorOrder.getDouble(0);
      //找零
      final double change = cursorOrder.getDouble(1);
      //抹零
      final double tail = cursorOrder.getDouble(2);
      //支付类优惠
      final double payPrivilege = cursorOrder.getDouble(3);

      mAct.runOnUiThread(new Runnable() {
        @Override public void run() {
          //应收
          mTvReceivable.setText(NumberFormatUtils.formatFloatNumber(receivable) + "");
          //找零
          mTvChange.setText(NumberFormatUtils.formatFloatNumber(change) + "");
          //抹零
          mTvTail.setText(NumberFormatUtils.formatFloatNumber(tail) + "");
          //支付类优惠
          mTvPayPrivilege.setText(NumberFormatUtils.formatFloatNumber(payPrivilege));

          //数据加载到实体中
          mAppBillCount = new AppBillCount();
          mAppBillCount.setTotalReceivable(mTvReceivable.getText().toString());
          mAppBillCount.setTotalChange(mTvChange.getText().toString());
          mAppBillCount.setTotalTail(mTvTail.getText().toString());
          mAppBillCount.setEveryReceived(everyReceivedList);
          mAppBillCount.setPayPrivilege(mTvPayPrivilege.getText().toString());
        }
      });
    }

    if (cursorOrder != null && !cursorOrder.isClosed()) {
      cursorOrder.close();
      cursorOrder = null;
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

  @OnClick(R.id.ibtn_account_print) public void printAccount(View view) {
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
   * 网络打印 收银
   */
  //@formatter:off
  private void printByNetAndBT() {
    //收银打印
    PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_BILL_SUMMARY, mCollectionContent, mAppBillCount);
    PrintTaskManager.printCashTask(task);
    BTPrintTask btPrintTask = new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_BILL_SUMMARY)
        .contentevent(mCollectionContent)
        .appBillCount(mAppBillCount)
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
          if (mCollectionContent != null) {
            if (mAppBillCount != null) {
              PrinterUsbData.printBillCollectInfo(mAppGpService.getGpService(), mCollectionContent,
                  mAppBillCount);
            } else {
              ToastUtils.showShort(mAct, "暂无订单信息");
            }
          } else {
            ToastUtils.showShort(mAct, "暂无销售统计信息");
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
    if (OpenPortEvent.BILL_CONNECT_PORT.equals(event.getType())) {
      //Gp是否支持USB打印
      String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
      if (isSupportUSBPrint.equals("1")) return;
      //是否已配置开启USB打印
      boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
      if (isPrint) {
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            if (mCollectionContent != null) {
              if (mAppBillCount != null) {
                PrinterUsbData.printBillCollectInfo(mAppGpService.getGpService(), mCollectionContent, mAppBillCount);
              }
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

  /**
   * 关闭线程
   */
  public void closePool() {
    if (sDbEngine != null) {
      sDbEngine.shutdown();
      sDbEngine = null;
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
    closePool();
  }
}
