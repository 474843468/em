package com.psi.easymanager.ui.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
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
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.OverBillDetailsContentAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CashBillUpdateOrderEvent;
import com.psi.easymanager.event.DetailsContentEvent;
import com.psi.easymanager.event.FindBillRefreshStatusEvent;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.event.SpeechEvent;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.print.bt.BTPrintTask;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.print.net.PrinterTask;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.usb.AppGpService;
import com.psi.easymanager.print.usb.AppUsbDeviceName;
import com.psi.easymanager.print.usb.PrinterUsbData;
import com.psi.easymanager.ui.activity.OverBillActivity;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.FullyLinearLayoutManager;
import com.psi.easymanager.widget.RecyclerViewSpaceItemDecoration;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 账单详情
 * Created by dorado on 2016/6/7.
 */
public class OverBillDetailsContentFragment extends BaseFragment {

  @Bind(R.id.tv_table) TextView mTvTable;
  @Bind(R.id.tv_order_no) TextView mTvOrderNo;
  @Bind(R.id.tv_people_number) TextView mTvPeopleNumber;
  @Bind(R.id.tv_start_time) TextView mTvStartTime;
  @Bind(R.id.rcv) RecyclerView mRcv;
  @Bind(R.id.tv_total_num) TextView mTvTotalNum;
  @Bind(R.id.tv_receivable) TextView mTvReceivable;
  @Bind(R.id.tv_received) TextView mTvReceived;
  @Bind(R.id.tv_tail) TextView mTvTail;
  @Bind(R.id.tv_change) TextView mTvChange;
  @Bind(R.id.tv_extra) TextView mTvExtra;
  @Bind(R.id.tv_complement) TextView mTvComplement;
  @Bind(R.id.tv_cashier) TextView mTvCashier;
  @Bind(R.id.tv_waiter) TextView mTvWaiter;
  @Bind(R.id.tv_order_req_num) TextView mTvOrderReqNum;
  @Bind(R.id.tv_end_time) TextView mTvEndTime;
  @Bind(R.id.tv_product_count) TextView mTvProductCount;
  @Bind(R.id.tv_pay_privilege) TextView mTvPayPrivilege;
  //Activity
  private OverBillActivity mAct;
  //Fragment管理
  private FragmentManager mFm;
  //开单时间日期格式
  private SimpleDateFormat mSdf = new SimpleDateFormat("MM/dd HH:mm");
  //信息汇总数据
  private List<PxOrderDetails> mDetailsList;
  //适配器
  private OverBillDetailsContentAdapter mAdapter;

  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //账单明细
  private DetailsContentEvent mDetailsContent;
  //订单
  private PxOrderInfo mOrderInfo;
  //单一线程用于打印
  private ExecutorService sDbEngine = null;

  public static OverBillDetailsContentFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    OverBillDetailsContentFragment fragment = new OverBillDetailsContentFragment();
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
    View view = inflater.inflate(R.layout.fragment_over_bill_details_content, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(DetailsContentEvent.class);
    EventBus.getDefault().getStickyEvent(AppGpService.class);
  }

  /**
   * 接收订单信息
   */
  //@formatter:off
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void getFinishedOrderInfo(
      DetailsContentEvent event) {
    //接收event
    mDetailsContent = event;
    //清空显示
    mTvWaiter.setText("服务生:");
    //获取订单
    mOrderInfo = event.getOrderInfo();
    mOrderInfo = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.Id.eq(mOrderInfo.getId()))
        .unique();
    String orderNo = mOrderInfo.getOrderNo();
    mTvOrderNo.setText("No." + orderNo.substring(orderNo.length() - 6, orderNo.length()));
    if (mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
        .unique();
      PxTableInfo dbTable = unique.getDbTable();
      mTvTable.setText(dbTable.getName());
      mTvPeopleNumber.setText(mOrderInfo.getActualPeopleNumber() + "");
    } else {
      mTvTable.setText("零售单");
      mTvPeopleNumber.setText("无");
    }
    mTvStartTime.setText(mSdf.format(mOrderInfo.getStartTime()));
    mTvReceivable.setText(NumberFormatUtils.formatFloatNumber(mOrderInfo.getAccountReceivable()) + "");
    mTvTail.setText(NumberFormatUtils.formatFloatNumber(mOrderInfo.getTailMoney()) + "");
    mTvChange.setText(NumberFormatUtils.formatFloatNumber(mOrderInfo.getTotalChange()) + "");
    mTvExtra.setText(NumberFormatUtils.formatFloatNumber(mOrderInfo.getExtraMoney()) + "");
    mTvComplement.setText(NumberFormatUtils.formatFloatNumber(mOrderInfo.getComplementMoney()) + "");
    mTvPayPrivilege.setText(NumberFormatUtils.formatFloatNumber(mOrderInfo.getPayPrivilege()) + "");

    SQLiteDatabase database = DaoServiceUtil.getPayInfoDao().getDatabase();
    Cursor cursor = database.rawQuery("Select sum(pay.RECEIVED),pay.PAYMENT_NAME"
        + " From PxPayInfo pay "
        + " Where pay.PX_ORDER_INFO_ID = " + mOrderInfo.getId()
        + " Group by pay.PAYMENT_NAME"
        + " Having pay.PAYMENT_TYPE != " + PxPaymentMode.TYPE_TAIL
        ,null);
    StringBuilder sb = new StringBuilder();
    //用于打印
     List<Pair<String, Double>> receivedS = new ArrayList<>();
    while (cursor.moveToNext()){
      double received = cursor.getDouble(0);
      String name = cursor.getString(1);
      sb.append(name + ":\t" + received + "\n");
      receivedS.add(new Pair<>(name,received));
    }
    IOUtils.closeCloseables(cursor);
    mTvReceived.setText(sb.toString());
    mTvOrderReqNum.setText("No." + mOrderInfo.getOrderReqNo());
    mTvEndTime.setText(mSdf.format(mOrderInfo.getEndTime()));
    mTvCashier.setText("收银员:" + mOrderInfo.getDbUser().getLoginName());
    if (mOrderInfo.getDbWaiter() != null) {
      mTvWaiter.setText("服务生:" + mOrderInfo.getDbWaiter().getLoginName());
    } else {
      mTvWaiter.setText("服务生:无");
    }
    //应收 = 点单总额 + 附加费 + 补足金额
    mTvProductCount.setText(NumberFormatUtils.formatFloatNumber(mOrderInfo.getAccountReceivable() - mOrderInfo.getExtraMoney() - mOrderInfo.getComplementMoney()) + "");
    queryData(mOrderInfo);
  }

  /**
   * 查询数据
   */
  private void queryData(PxOrderInfo mOrderInfo) {
  mDetailsList = DaoServiceUtil.getOrderDetailsService()
      .queryBuilder()
      .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
      .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_ORDER))
      .where(PxOrderDetailsDao.Properties.InCombo.eq(PxOrderDetails.IN_COMBO_FALSE))
      .list();
    if (mAdapter == null) {
      initRcv();
    } else {
      mAdapter.setData(mDetailsList);
      mTvTotalNum.setText(mDetailsList.size() + "项");
    }
  }

  /**
   * 初始化Rcv
   */
  private void initRcv() {
    FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false);
    mRcv.setHasFixedSize(true);
    mRcv.setLayoutManager(layoutManager);
    mAdapter = new OverBillDetailsContentAdapter(mAct, mDetailsList);
    if (mDetailsList != null && mDetailsList.size() != 0) {
      mTvTotalNum.setText(mDetailsList.size() + "项");
    } else {
      mTvTotalNum.setText("0项");
    }
    mRcv.setAdapter(mAdapter);
    int spaceWidth =
        getResources().getDimensionPixelSize(R.dimen.over_bill_rcv_content_horizontal_space_width);
    int spaceHeight =
        getResources().getDimensionPixelSize(R.dimen.over_bill_rcv_content_vertical_space_height);
    mRcv.addItemDecoration(new RecyclerViewSpaceItemDecoration(spaceWidth, spaceHeight));
  }

  @OnClick(R.id.ibtn_detail_print) public void printAccount(View view) {
    String[] choosePrint = { "客户联", "财务联" };
    new MaterialDialog.Builder(mAct).title("结账单")
        .items(choosePrint)
        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog dialog, View view, int which,
              CharSequence text) {
            switch (which) {
              case 0:
                printCustomer();
                break;
              case 1:
                printFinancing();
                break;
            }
            return true;
          }
        })
        .positiveText("确定")
        .negativeText("取消")
        .show();
  }

  /**
   * 接受由App发送的AppUsbDeviceName
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onDeviceNameEvent(AppUsbDeviceName appUsbDeviceName) {
    //ptksai pos 不支持USB打印
    String  isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
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
    String  isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;

    if(appGpService == null){
      ToastUtils.showShort(mAct,"服务为空");
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
   * 打印财务联
   */
  private void printFinancing() {
    //网络打印
    printByNetAndBT(BTPrintConstants.PRINT_MODE_BILL_DETAIL_FINANCE);
    //USB打印财务联
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    try {
      if(mAppGpService.getGpService().getPrinterConnectStatus(1) == Constants.USB_CONNECT_STATUS){
        printUsbByWithFinancingPrinter();
      }else {
        mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
        printUsbByWithFinancingPrinter();
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
  /**
   * 网络打印 收银
   */
  // @formatter:on
  private void printByNetAndBT(int mode) {
    PrinterTask task = new PrinterTask( mDetailsList,mode);
    PrintTaskManager.printCashTask(task);
    BTPrintTask btPrintTask = new BTPrintTask.Builder(mode)
        .orderDetailsList(mDetailsList)
        .build();
    PrintEventManager.getManager().postBTPrintEvent(btPrintTask);
  }

  /**
   * USB打印财务联
   */
  private void printUsbByWithFinancingPrinter(){
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          if (mDetailsList != null) {
            PrinterUsbData.printBillDetailInfoWithFinancing(mAppGpService.getGpService(), mDetailsContent,mDetailsList );
          } else {
            ToastUtils.showShort(mAct, "暂无账单明细信息");
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
   * 打印客户联
   */
  private void printCustomer() {
    //网络打印 客户联
    printByNetAndBT(BTPrintConstants.PRINT_MODE_BILL_DETAIL_CUSTOMER);
    //USB打印客户联
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    try {
      if(mAppGpService.getGpService().getPrinterConnectStatus(1) == Constants.USB_CONNECT_STATUS){
        printUsbByWithCustomerPrinter();
      }else {
        mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
        printUsbByWithCustomerPrinter();
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * USB打印客户联
   */
  private void printUsbByWithCustomerPrinter(){
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          if (mDetailsList  != null) {
            PrinterUsbData.printBillDetailInfoWithCustomer(mAppGpService.getGpService(), mDetailsContent, mDetailsList);
          } else {
            ToastUtils.showShort(mAct, "暂无账单明细信息");
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
   * 反结账
   */
  @OnClick(R.id.ibtn_reverse) public void reverse() {
    //对话框
    new MaterialDialog.Builder(mAct).title("警告")
        .content("是否反结账?")
        .positiveText("确认")
        .negativeText("取消")
        .negativeColor(getResources().getColor(R.color.primary_text))
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            if (PxOrderInfo.SHIFT_CHANGE_HANDED.equals(mOrderInfo.getShiftChangeType())) {
              ToastUtils.showShort(mAct, "已交接的订单不能反结账");
              return;
            }
            //如果有抹零，删除
            PxPayInfo tail = DaoServiceUtil.getPayInfoService()
                .queryBuilder()
                .where(PxPayInfoDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
                .where(PxPayInfoDao.Properties.PaymentType.eq(PxPaymentMode.TYPE_TAIL))
                .unique();
            if (tail != null) {
              mOrderInfo.setTailMoney(0.0);
              DaoServiceUtil.getPayInfoService().delete(tail);
            }
            //修改订单状态
            mOrderInfo.setStatus(PxOrderInfo.STATUS_UNFINISH);
            //指定反结账未上传
            mOrderInfo.setIsUploadReverse(false);
            //反结账
            mOrderInfo.setIsReversed(PxOrderInfo.REVERSE_TRUE);
            DaoServiceUtil.getOrderInfoService().saveOrUpdate(mOrderInfo);
            //重置桌台状态
            TableOrderRel rel = DaoServiceUtil.getTableOrderRelService()
                .queryBuilder()
                .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
                .unique();
            if (rel != null) { //桌位单
              PxTableInfo dbTable = rel.getDbTable();
              if (dbTable.getStatus().equals(PxTableInfo.STATUS_EMPTY)) {
                dbTable.setStatus(PxTableInfo.STATUS_OCCUPIED);
              }
              DaoServiceUtil.getTableInfoService().saveOrUpdate(dbTable);
            }
            EventBus.getDefault().post(new CashBillUpdateOrderEvent().setOrderInfo(mOrderInfo));
            EventBus.getDefault().post(new FindBillRefreshStatusEvent());
            mAct.finish();
          }
        })
        .canceledOnTouchOutside(false)
        .show();
  }

  /**
   * 取餐
   */
  @OnClick(R.id.ibtn_detail_take_food) public void takeFood() {
    if (mOrderInfo == null) return;
    String no = mOrderInfo.getOrderNo().substring(mOrderInfo.getOrderNo().length() - 6);
    String num = String.valueOf(Integer.parseInt(no));
    String content = "请" + num + "号到前台取餐";
    EventBus.getDefault().post(new SpeechEvent().setContent(content));
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
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if(isPrint){
      if(OpenPortEvent.BILL_DETAIL_WITH_FINANCE_PORT.equals(event.getType())){
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            if (mDetailsList != null) {
              PrinterUsbData.printBillDetailInfoWithFinancing(mAppGpService.getGpService(), mDetailsContent,mDetailsList );
            }
          }
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      }else if(OpenPortEvent.BILL_DETAIL_WITH_CUSTOM_PORT.equals(event.getType())){
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            if (mDetailsList  != null) {
              PrinterUsbData.printBillDetailInfoWithCustomer(mAppGpService.getGpService(), mDetailsContent, mDetailsList);
            }
          }
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      }
    }else {
      mAct.runOnUiThread(new Runnable() {
        @Override public void run() {
          ToastUtils.showShort(mAct, "设备未连接,请在更多模块配置普通打印机!");
        }
      });
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

  /**
   * 退出
   */
  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
    closePool();
  }
}