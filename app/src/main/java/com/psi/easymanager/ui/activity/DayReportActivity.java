package com.psi.easymanager.ui.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
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
import com.psi.easymanager.adapter.DayReportCateAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxBusinessHoursDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.module.AppCashCollect;
import com.psi.easymanager.module.AppShiftCateInfo;
import com.psi.easymanager.module.PxBusinessHours;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxProductCategory;
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
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.ListViewHeightUtil;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.SwipeBackLayout;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dorado on 2016/8/2.
 */
public class DayReportActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
  //滑动
  @Bind(R.id.swipe_back) SwipeBackLayout mSwipeBack;
  //日期显示
  @Bind(R.id.tv_date) TextView mTvDate;
  //当前设置的营业时间
  @Bind(R.id.tv_current_business_time) TextView mTvCurrentBusinessTime;

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
  //总价
  @Bind(R.id.tv_consume_statics_total_price) TextView mTvConsumeStaticsTotal;
  //应收
  @Bind(R.id.tv_consume_statics_receivable) TextView mTvConsumeStaticsReceivable;
  //优惠
  @Bind(R.id.tv_consume_statics_privilege) TextView mTvConsumeStaticsPrivilege;
  //支付类优惠
  @Bind(R.id.tv_consume_statics_pay_privilege) TextView mTvConsumeStaticsPayPrivilege;
  //实收
  @Bind(R.id.tv_consume_statics_received) TextView mTvConsumeStaticsReceived;
  //损益
  @Bind(R.id.tv_consume_statics_gain_lose) TextView mTvConsumeStaticsGainLose;
  //不计入统计金额
  @Bind(R.id.tv_consume_statics_exclusive) TextView mTvConsumeStaticsExclusive;

  /**
   * 分类统计
   */
  //ListView
  @Bind(R.id.lv_cate_collect) ListView mLvCateCollect;

  //年
  private int mYear;
  //月
  private int mMonthOfYear;
  //日
  private int mDayOfMonth;

  //当前营业时间
  private PxBusinessHours mBusinessHours;

  //Adapter
  private DayReportCateAdapter mDayReportCateAdapter;
  private ShiftWork mShiftWork;
  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //单一线程用于打印
  private ExecutorService sDbEngine = null;

  @Override protected int provideContentViewId() {
    return R.layout.activity_day_report;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    //EventBus注册
    EventBus.getDefault().register(this);
    mSwipeBack.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
    //初始化listView
    initLv();
    //获取该店铺的营业时间
    mBusinessHours = DaoServiceUtil.getBusinessHoursService()
        .queryBuilder()
        .where(PxBusinessHoursDao.Properties.DelFlag.eq("0"))
        .unique();
    if (mBusinessHours == null) {
      mBusinessHours = new PxBusinessHours();
      mBusinessHours.setCloseTime("23:59:59");
      mBusinessHours.setBusinessType(PxBusinessHours.TODAY);
      mTvCurrentBusinessTime.setText("营业结束时间:默认(当天全天)");
    } else {
      //显示当前营业时间
      if (mBusinessHours.getBusinessType().equals(PxBusinessHours.TODAY)) {
        mTvCurrentBusinessTime.setText("营业结束时间:当天" + mBusinessHours.getCloseTime());
      } else {
        mTvCurrentBusinessTime.setText("营业结束时间:次日" + mBusinessHours.getCloseTime());
      }
    }
    //显示默认时间
    Calendar calendar = Calendar.getInstance();
    mYear = calendar.get(Calendar.YEAR);
    mMonthOfYear = calendar.get(Calendar.MONTH);
    mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    mTvDate.setText(mYear + "-" + (mMonthOfYear + 1) + "-" + mDayOfMonth);
  }

  /**
   * 初始化ListView
   */
  private void initLv() {
    //mDayReportCateAdapter = new DayReportCateAdapter(this, null);
    //mLvCateCollect.setAdapter(mDayReportCateAdapter);
    //mLvCateCollect.setFocusable(false);
    //ListViewHeightUtil.setListViewHeightBasedOnChildren(mLvCateCollect);
  }

  /**
   * 选择营业日期
   */
  @OnClick(R.id.rl_business_date) public void selectBusinessDate() {
    Calendar now = Calendar.getInstance();
    DatePickerDialog datePickerDialog =
        DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH));
    datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
  }

  @Override
  public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
    mYear = year;
    mMonthOfYear = monthOfYear;
    mDayOfMonth = dayOfMonth;
    mTvDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
  }

  /**
   * 统计
   */
  @OnClick(R.id.btn_statistics) public void statistics() {
    if (mYear == 0 && mMonthOfYear == 0 && mDayOfMonth == 0) {
      ToastUtils.showShort(App.getContext(), "请选择日期");
      return;
    }

    if (mBusinessHours == null) {
      ToastUtils.showShort(App.getContext(), "后台未添加营业时间");
      return;
    }

    //获取Calendar
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, mYear);
    calendar.set(Calendar.MONTH, mMonthOfYear);
    calendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
    //闭店时间 example:"20:30:30"
    String closeTime = mBusinessHours.getCloseTime();
    String[] timeInfo = closeTime.split(":");
    //时
    int hourOfDay = Integer.valueOf(timeInfo[0]);
    //分
    int minute = Integer.valueOf(timeInfo[1]);
    //秒
    int second = Integer.valueOf(timeInfo[2]);
    //设置时分秒
    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);

    //所选日期
    Date selectedDate = calendar.getTime();

    //当天
    if (mBusinessHours.getBusinessType().equals(PxBusinessHours.TODAY)) {
      Date dateBeforeSelectedDate = new Date(selectedDate.getTime() - 24 * 60 * 60 * 1000);
      queryData(dateBeforeSelectedDate, selectedDate);
    }
    //次日早晨
    else {
      Date dateAfterSelectedDate = new Date(selectedDate.getTime() + 24 * 60 * 60 * 1000);
      queryData(selectedDate, dateAfterSelectedDate);
    }
  }

  /**
   * 查询所有的数据
   */
  //@formatter:off
  private void queryData(Date beforeDate, Date afterDate) {
    //用于打印
    mShiftWork = new ShiftWork();
    mShiftWork.setStartTime(beforeDate);
    mShiftWork.setEndTime(afterDate);
    mShiftWork.setShitTime(new Date());

    long beforeDateTime = beforeDate.getTime();
    long afterDateTime = afterDate.getTime();
    SQLiteDatabase db = DaoServiceUtil.getOrderDetailsDao().getDatabase();

    //收银统计
    Cursor cashCollectCursor = null;
    //消费统计
    Cursor consumeCollectCursor= null;
    //消费统计 损益
    Cursor consumeCollectGainLoseCursor = null;
    //消费统计 实际收入
    Cursor consumeCollectIncomeCursor = null;
    //消费统计 不计入统计金额
    Cursor consumeCollectExclusiveCursor = null;
    //分类统计
    Cursor categoryCursor = null;

    cashCollectCursor = db.rawQuery(
     "Select count(*),sum(pay.RECEIVED - pay.CHANGE),pay.PAYMENT_NAME"
            + " From PxPayInfo pay "
            + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
            + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
            + " And o.END_TIME between " + beforeDateTime + " and " + afterDateTime
            + " Group by pay.PAYMENT_ID"
            + " Having pay.PAYMENT_TYPE != " + PxPaymentMode.TYPE_TAIL
        , null);
    consumeCollectCursor = db.rawQuery(
        "Select count(o._id),sum(o.ACTUAL_PEOPLE_NUMBER),sum(o.TOTAL_PRICE),sum(o.ACCOUNT_RECEIVABLE),sum(o.DISCOUNT_PRICE),sum(o.PAY_PRIVILEGE)"
            + " From OrderInfo o"
            + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
            + " And o.END_TIME between " + beforeDateTime + " and " + afterDateTime
        , null);
    consumeCollectGainLoseCursor = db.rawQuery(
              "Select sum(o.REAL_PRICE - o.TOTAL_CHANGE + o.PAY_PRIVILEGE - o.ACCOUNT_RECEIVABLE)"
                  + " From OrderInfo o"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.END_TIME between " + beforeDateTime + " and " + afterDateTime
              ,null);
    consumeCollectIncomeCursor= db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.END_TIME between " + beforeDateTime + " and " + afterDateTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_TRUE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
    consumeCollectExclusiveCursor = db.rawQuery(
              "Select sum(pay.RECEIVED - pay.CHANGE)"
                  + " From PxPayInfo pay"
                  + " Join OrderInfo o On o._id = pay.PX_ORDER_INFO_ID"
                  + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
                  + " And o.END_TIME between " + beforeDateTime + " and " + afterDateTime
                  + " And pay.SALES_AMOUNT = " + PxPaymentMode.SALES_AMOUNT_FALSE
                  + " And pay.PAYMENT_TYPE !=" + PxPaymentMode.TYPE_TAIL
              ,null);
    categoryCursor = db.rawQuery(
        "Select sum(d.NUM),sum(d.FINAL_PRICE),sum(d.FINAL_PRICE * d.DISCOUNT_RATE / 100),c.NAME"
            + " From OrderDetails d"
            + " Join OrderInfo o On o._id = d.PX_ORDER_INFO_ID"
            + " Join ProductInfo p On p._id = d.PX_PRODUCT_INFO_ID"
            + " Join ProductCategory c On c._id = p.PX_PRODUCT_CATEGORY_ID"
            + " Where o.STATUS = " + PxOrderInfo.STATUS_FINISH
            + " And o.END_TIME between " + beforeDateTime + " and " + afterDateTime
            + " And c.LEAF = " + PxProductCategory.IS_LEAF
            + " And d.IN_COMBO = " + PxOrderDetails.IN_COMBO_FALSE
            + " And d.ORDER_STATUS = " + PxOrderDetails.ORDER_STATUS_ORDER
            + " Group By c._id", null);
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

      CashCollectAdapter cashCollectAdapter = new CashCollectAdapter(this, appCashCollectList);
      mLvCashCollect.setAdapter(cashCollectAdapter);
      ListViewHeightUtil.setListViewHeightBasedOnChildren(mLvCashCollect);
      IOUtils.closeCloseables(cashCollectCursor);
      //打印
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
        mTvConsumeStaticsPayPrivilege.setText(NumberFormatUtils.formatFloatNumber(payPrivilege) + "");

        // 打印 1.消费统计

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
    if (consumeCollectExclusiveCursor != null) {
      while (consumeCollectExclusiveCursor.moveToNext()) {
        double exclusive = consumeCollectExclusiveCursor.getDouble(0);
        mTvConsumeStaticsExclusive.setText(NumberFormatUtils.formatFloatNumber(exclusive) + "");
        mShiftWork.setStaticsExclusive(exclusive);
      }
      IOUtils.closeCloseables(consumeCollectExclusiveCursor);
    }

    //分类统计
    if (categoryCursor != null) {
      ArrayList<AppShiftCateInfo> appCashCollectList = new ArrayList<AppShiftCateInfo>();
      while (categoryCursor.moveToNext()) {
        AppShiftCateInfo cateInfo = new AppShiftCateInfo();
        cateInfo.setCateNumber(categoryCursor.getInt(0));
        cateInfo.setReceivableAmount(categoryCursor.getDouble(1));
        cateInfo.setActualAmount(categoryCursor.getDouble(2));
        cateInfo.setCateName(categoryCursor.getString(3));
        appCashCollectList.add(cateInfo);
      }
      DayReportCateAdapter cateAdapter = new DayReportCateAdapter(this, appCashCollectList);
      mLvCateCollect.setAdapter(cateAdapter);
      ListViewHeightUtil.setListViewHeightBasedOnChildren(mLvCateCollect);
      IOUtils.closeCloseables(categoryCursor);
      //打印 分类统计
      mShiftWork.setCategoryCollectList(appCashCollectList);
    }
  }

  /**
   * 打印日结单据
   */
  @OnClick(R.id.ibtn_shift_print) public void printDailyStatment(ImageButton iBtn) {
    if (mShiftWork == null) return;
    //默认全部
    mShiftWork.setWorkZone("全部");
    App app = (App) App.getContext();
    User user = app.getUser();
    mShiftWork.setCashierName((user == null) ? "admin" : user.getName());
    mShiftWork.setBusinessData(mTvDate.getText().toString());
    //1 - 网络打印
    printByNetAndBT();
    //2-USB打印
    String isSupportUSBPrint = (String) SPUtils.get(this, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    try {
      if(mAppGpService.getGpService().getPrinterConnectStatus(1) == Constants.USB_CONNECT_STATUS){
        printByUSBPrinter();
      }else {
        mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
        printByUSBPrinter();
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 网口或蓝牙打印
   */
  private void printByNetAndBT() {
    PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_SHIFT_WORK_DAILY_STATMENTS, mShiftWork);
    PrintTaskManager.printCashTask(task);
    BTPrintTask btPrintTask = new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_SHIFT_WORK_DAILY_STATMENTS)
        .shiftWork(mShiftWork)
        .build();
    PrintEventManager.getManager().postBTPrintEvent(btPrintTask);
  }

  /**
   * USB打印 收银
   */
  private void printByUSBPrinter(){
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(DayReportActivity.this, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(DayReportActivity.this, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          PrinterUsbData.printDayReportCollect(mAppGpService.getGpService(), mShiftWork, this);
        }
      } catch (RemoteException e) {
        ToastUtils.showShort(DayReportActivity.this, "打印机异常:" + e.getMessage());
        e.printStackTrace();
      }
    } else {
      ToastUtils.showShort(DayReportActivity.this, "设备未连接,请在更多模块配置普通打印机!");
    }
  }

  /**
   * 接受由App发送的AppUsbDeviceName
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onDeviceNameEvent(AppUsbDeviceName appUsbDeviceName) {
    //ptksai pos 不支持USB打印
    String  isSupportUSBPrint = (String) SPUtils.get(DayReportActivity.this, Constants.SUPPORT_USB_PRINT, "");
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
    String  isSupportUSBPrint = (String) SPUtils.get(DayReportActivity.this, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;

    if(appGpService == null){
      ToastUtils.showShort(DayReportActivity.this,"服务为空");
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
    if(OpenPortEvent.DAY_REPORT_PORT.equals(event.getType())){
      //Gp是否支持USB打印
      String isSupportUSBPrint = (String) SPUtils.get(DayReportActivity.this, Constants.SUPPORT_USB_PRINT, "");
      if (isSupportUSBPrint.equals("1")) return;
      //是否已配置开启USB打印
      boolean isPrint = (boolean) SPUtils.get(DayReportActivity.this, Constants.SWITCH_ORDINARY_PRINT, true);
      if (isPrint) {
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            PrinterUsbData.printDayReportCollect(mAppGpService.getGpService(), mShiftWork, this);
          }
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      } else {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            ToastUtils.showShort(DayReportActivity.this, "设备未连接,请在更多模块配置普通打印机!");
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

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
    closePool();
  }
}
