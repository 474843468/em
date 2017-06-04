package com.psi.easymanager.ui.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.KitchenDocumentListAdapter;
import com.psi.easymanager.adapter.KitchenPrintDeviceAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.PdConfigRelDao;
import com.psi.easymanager.dao.PrintDetailsCollectDao;
import com.psi.easymanager.dao.PxPrinterInfoDao;
import com.psi.easymanager.dao.PxProductConfigPlanDao;
import com.psi.easymanager.dao.PxTableAlterationDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.dao.dbUtil.DbCore;
import com.psi.easymanager.module.AppPrinterDetails;
import com.psi.easymanager.module.PdConfigRel;
import com.psi.easymanager.module.PrintDetails;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.module.PxProductConfigPlan;
import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.print.net.PrinterTask;
import com.psi.easymanager.print.net.event.IPrintConnectStatus;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.InterceptClickFrameLayout;
import com.psi.easymanager.widget.SwipeBackLayout;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: ylw
 * Date: 2016-06-02
 * Time: 20:18
 * 厨房打印
 */

public class KitchenPrintActivity extends BaseActivity
    implements KitchenPrintDeviceAdapter.OnCallClickListener,
    KitchenDocumentListAdapter.OnCallBackClickListener, IPrintConnectStatus {
  //查找数据
  private static final int SEARCH_DATA = 0x0;
  //清空数据
  private static final int CLEAR_DATA = 0x1;
  //查找完毕
  private static final int SEARCH_OVER = 0x2;
  //清空完毕
  private static final int CLEAR_OVER = 0x3;
  //清空item
  private static final int CHANGE_STATUS = 0x4;
  //子线程Toast
  private static final int MSG_BOX = 0x6;
  //显示蒙层
  private static final int SHOW_INTERCEPT = 0x7;
  //关闭蒙层
  private static final int DISMISS_INTERCEPT = 0x8;
  //打印
  private static final int PRINT_DOCUMENT = 0x9;
  //查找数量
  private static final int LIMIT = 10;
  //LoadDevice
  private static final int LOAD_DEVICE = 0x11;
  //over
  private static final int LOAD_DEVICE_OVER = 0x12;

  //蒙层view
  @Bind(R.id.view) InterceptClickFrameLayout mView;
  //蒙层loading
  @Bind(R.id.progress_view) View mLoading;
  //已打印Rb
  @Bind(R.id.rb_collect) RadioButton mRbCollect;
  //已打印Rb
  @Bind(R.id.rb_table) RadioButton mRbTable;
  //内容view
  @Bind(R.id.content_view) SwipeBackLayout mContentView;
  //打印机列表
  @Bind(R.id.rcv_kitchen_print) RecyclerView mRcvDevice;
  //商品列表
  @Bind(R.id.rcv_document_list) RecyclerView mRcvDocument;
  //单据列表，当前位置
  @Bind(R.id.tv_kitchen_print_bills_page) TextView mTvPage;

  //配菜方案Adapter
  private KitchenPrintDeviceAdapter mDeviceAdapter;
  //配菜方案list
  private List mDeviceList;//配菜方案list + 收银打印机
  //需要打印的单据list
  private List<AppPrinterDetails> mDocumentNeedList;
  //单据列表Adapter
  private KitchenDocumentListAdapter mDocumentAdapter;
  //单据列表当前位置 默认 -1
  private int mCurrentDocumentPos = -1;
  //打印机当前位置 默认-1
  private int mCurrentDevicePos = -1;
  //一菜一切
  private boolean mIsOncePrint = false;
  //单个线程池
  //private ExecutorService mPool;
  //当前时间
  private Date mToday = new Date(System.currentTimeMillis());
  //昨天时间(当当前时间 - 24h)
  private Date mYesterday = new Date(mToday.getTime() - 1 * 24 * 60 * 60 * 1000);
  // 是否是订单信息  true 订单信息 false 桌台信息
  private boolean mIsCollect = true;
  //总页数
  private int mTotalPage = 0;
  //当前页
  private int mCurrentPage = 1;
  //当前配菜方案
  private PxProductConfigPlan mConfigPlan;
  private UIHandler mHandler;
  private AsyncSession mAsyncSession;

  //@formatter:off
  @Override protected int provideContentViewId() {
    return R.layout.activity_kitchen_print;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    //初始化滑动关闭
    initSwipeBack();
    //初始化View
    initView();
    //init Handler
    mHandler = new UIHandler();
    //GreenDao 异步
    mAsyncSession = DbCore.getDaoSession().startAsyncSession();
    isShowIntercept(true);
    //加载数据
    mAsyncSession.runInTx(new MyTask(LOAD_DEVICE));
    //new Thread(new MyTask(LOAD_DEVICE)).start();
  }

  /**
   * 初始化滑动关闭
   */
  private void initSwipeBack() {
    mContentView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
  }

  /**
   * 初始化View
   */
  private void initView() {
    //打印机列表
    LinearLayoutManager deviceManager = new LinearLayoutManager(this);
    deviceManager.setOrientation(LinearLayoutManager.VERTICAL);
    mDeviceList = new ArrayList<>();
    mDeviceAdapter = new KitchenPrintDeviceAdapter(this);
    mDeviceAdapter.setOnCallClickListener(this);
    mRcvDevice.setLayoutManager(deviceManager);
    mRcvDevice.setHasFixedSize(true);
    mRcvDevice.setAdapter(mDeviceAdapter);
    mDeviceAdapter.setData(mDeviceList);
    //单据列表
    mDocumentNeedList = new ArrayList<>();//单据列表 需要的数据
    LinearLayoutManager documentManager = new LinearLayoutManager(this);
    mDocumentAdapter = new KitchenDocumentListAdapter(this);
    mDocumentAdapter.setOnCallClickListener(this);
    mRcvDocument.setLayoutManager(documentManager);
    mRcvDocument.setHasFixedSize(true);
    mRcvDocument.setAdapter(mDocumentAdapter);
    mDocumentAdapter.setData(mDocumentNeedList,mIsOncePrint);
  }

  /**
   * 加载设备
   */
  private void loadDeviceList() {
    //查询配菜方案 启用、厨房
    QueryBuilder<PxProductConfigPlan> qb = DaoServiceUtil.getProductConfigPlanService()
        .queryBuilder()
        .where(PxProductConfigPlanDao.Properties.DelFlag.eq("0"))
        .orderDesc(PxProductConfigPlanDao.Properties.PxPrinterInfoId);
    Join<PxProductConfigPlan, PxPrinterInfo> join = qb.join(PxProductConfigPlanDao.Properties.PxPrinterInfoId, PxPrinterInfo.class);
    join.where(PxPrinterInfoDao.Properties.DelFlag.eq("0"));
    join.where(PxPrinterInfoDao.Properties.Status.eq(PxPrinterInfo.ENABLE));
    List<PxProductConfigPlan> planList = qb.build().forCurrentThread().list();

    //for (PxProductConfigPlan plan : planList) {
    //  mDeviceList.add(new KitchenDevice(KitchenDevice.TYPE_CONFIG, plan));
    //}
    if (!planList.isEmpty()){
      mDeviceList.addAll(planList);
    }

    //收银打印机
    List<PxPrinterInfo> cashPrinterList = DaoServiceUtil.getPrinterInfoService()
        .queryBuilder()
        .where(PxPrinterInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPrinterInfoDao.Properties.Type.eq(PxPrinterInfo.TYPE_CASH))
        .where(PxPrinterInfoDao.Properties.Status.eq(PxPrinterInfo.ENABLE))
        .build().forCurrentThread()
        .list();
    //for (PxPrinterInfo info : cashPrinterList) {
    //  mDeviceList.add(new KitchenDevice(KitchenDevice.TYPE_CASH,info));
    //}
    if (!cashPrinterList.isEmpty()){
      mDeviceList.addAll(cashPrinterList);
    }
  }
  /**
   * 打印机列表条目点击
   */
  @Override public void onCallClick(int pos) {
    if (mIsShow) return;
    //恢复选择状态
    regainPrintStatus();
    //页数
    mCurrentPage = 1;
    mTotalPage = 0;
    //状态恢复
    mCurrentDocumentPos = -1;
    //item状态重置
    mDeviceAdapter.setSelected(pos);
    //
    mCurrentDevicePos = pos;
    if (mDeviceList == null || mDeviceList.size() == 0) return;
    //当前ConfigPlan
    Object obj = mDeviceList.get(pos);
    //配菜方案
    if (obj instanceof  PxProductConfigPlan) {
      //加载数据 开启蒙层
      isShowIntercept(true);
      mConfigPlan = (PxProductConfigPlan) obj;
      //一菜一切
      mIsOncePrint = (mConfigPlan.getFlag().equals(PxProductConfigPlan.ONCE_PRINT) ? true : false);
      //mPool.execute(new MyTask(SEARCH_DATA));
      mAsyncSession.runInTx(new MyTask(SEARCH_DATA));
    } else {
      mConfigPlan = null;
      //收银打印机
      nullDocumentList();
    }

  }

  /**
   * 默认恢复 选择Collect
   */
  private void regainPrintStatus() {
    mRbCollect.setChecked(true);
    mRbTable.setChecked(false);
    mIsCollect = true;
  }

  /**
   * 选择状态信息 collect和 table
   */
  @OnClick({ R.id.rb_collect, R.id.rb_table }) public void selectStatus(RadioButton rb) {
    mIsCollect = (rb.getId() == R.id.rb_collect) ? true : false;
    mTotalPage = 0;
    mCurrentPage = 1;
    if (mConfigPlan == null) return;
    //加载数据 开启蒙层
    isShowIntercept(true);
    //mPool.execute(new MyTask(SEARCH_DATA));
    mAsyncSession.runInTx(new MyTask(SEARCH_DATA));
  }

  /**
   * 上下页
   */
  @OnClick({ R.id.tv_next_page, R.id.tv_last_page }) public void changePage(TextView tv) {
    if (mTotalPage == 0 || mIsShow) return;
    switch (tv.getId()) {
      case R.id.tv_next_page:
        if (mCurrentPage == mTotalPage) return;
        mCurrentPage += 1;
        //
        isShowIntercept(true);
        //mPool.execute(new MyTask(SEARCH_DATA));
        mAsyncSession.runInTx(new MyTask(SEARCH_DATA));
        break;
      case R.id.tv_last_page:
        if (mCurrentPage == 1 || mCurrentPage == 0) return;
        mCurrentPage -= 1;
        //
        isShowIntercept(true);
        //mPool.execute(new MyTask(SEARCH_DATA));
        mAsyncSession.runInTx(new MyTask(SEARCH_DATA));
        break;
    }
  }

  /**
   * 空的单据列表
   */
  private void nullDocumentList() {
    mDocumentNeedList.clear();
    mDocumentAdapter.setData(mDocumentNeedList, mIsOncePrint);
    mTvPage.setText("0/0");
    mCurrentDocumentPos = -1;
    mTotalPage = 0;
  }

  /**
   * 单据列表条目点击
   */
  @Override public void onCallBackClick(int pos) {
    //单据列表选择位置重置
    mCurrentDocumentPos = pos;
    mDocumentAdapter.setSelected(pos);
  }

  /**
   * 单据列表 底部按钮 清空、打印、测试
   */
  @OnClick({
      R.id.ibtn_document_list_clear, R.id.ibtn_document_list_print, R.id.ibtn_document_list_test
  }) public void bottomFabOnClick(ImageButton iBtn) {
    switch (iBtn.getId()) {
      case R.id.ibtn_document_list_clear://清空
        clearDocument();
        break;
      case R.id.ibtn_document_list_print://补打
        printDocument();
        break;
      case R.id.ibtn_document_list_test://测试页
        printTest();
        break;
    }
  }


  /**
   * 搜索 清空 加载 任务
   */
  class MyTask implements Runnable {
    private int mode;
    private PrinterTask mTask;
    private PxPrinterInfo printerInfo;

    public MyTask(int mode) {
      this.mode = mode;
    }

    public MyTask(int mode, PrinterTask task, PxPrinterInfo printerInfo) {
      this.mode = mode;
      this.mTask = task;
      this.printerInfo = printerInfo;
    }

    @Override public void run() {
      //显示蒙层
      // mHandler.sendEmptyMessage(SHOW_INTERCEPT);
      switch (mode) {
        case SEARCH_DATA://加载数据
          mDocumentNeedList = new ArrayList<>();
          if (mTotalPage == 0) {
            getTotalPage();
          }
          if (mTotalPage > 0) {
            loadData();
          }
          mHandler.sendEmptyMessage(SEARCH_OVER);
          break;
        case CLEAR_DATA://清空数据
          performClearData();
          mHandler.sendEmptyMessage(CLEAR_OVER);
          break;
        case PRINT_DOCUMENT://打印单据
          if (mTask != null) {
            performPrint(mTask, printerInfo);
          }
          mHandler.sendEmptyMessage(DISMISS_INTERCEPT);
          break;
        case LOAD_DEVICE://加载打印机配菜方案
          //奇怪
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          loadDeviceList();
          mHandler.sendEmptyMessage(LOAD_DEVICE_OVER);
          break;
      }
    }
  }
  //UIHandler
  //@formatter:off
 private class UIHandler extends Handler{
    @Override public void handleMessage(Message msg) {
      switch (msg.what) {
        case SEARCH_OVER://查询结束
          showPage();
          isShowIntercept(false);
          break;
        case CLEAR_OVER:
          //通知刷新 单据列表
          nullDocumentList();
          isShowIntercept(false);
          break;
        case CHANGE_STATUS:
          AppPrinterDetails printerDetails = (AppPrinterDetails) msg.obj;
          changeStatus(printerDetails);
          break;
        case MSG_BOX:
          String message = (String) msg.obj;
          msgBox(message);
          break;
        case SHOW_INTERCEPT://显示蒙层
          isShowIntercept(true);
          break;
        case DISMISS_INTERCEPT://关闭蒙层
          isShowIntercept(false);
          break;
        case LOAD_DEVICE_OVER://
          //Map<String,String> connectMap = (Map<String, String>)msg.obj;
          mDeviceAdapter.setData(mDeviceList);
          isShowIntercept(false);
          if (mDeviceList.isEmpty()) return;
          //初始化 默认初始配菜方案
          //onCallClick(0);
          PrintEventManager.getManager().registPrinterConnectStatus(KitchenPrintActivity.this);
          break;
      }
    }
  }
  /**
   * 获取总页数
   */
  private void getTotalPage() {
    //订单信息信息
    if (mIsCollect) {
      //非一菜一切
      if (!mIsOncePrint) {
        int count = (int) DaoServiceUtil.getPdCollectService()
            .queryBuilder()
            .where(PrintDetailsCollectDao.Properties.OperateTime.between(mYesterday, mToday))
            .where(PrintDetailsCollectDao.Properties.DbConfigId.eq(mConfigPlan.getId()))
            .buildCount().forCurrentThread()
            .count();
        mTotalPage = (count + LIMIT - 1) / LIMIT;
      }
      //一菜一切
      else {
        int count = (int) DaoServiceUtil.getPdConfigRelService()
            .queryBuilder()
            .where(PdConfigRelDao.Properties.DbConfigId.eq(mConfigPlan.getId()))
            .where(PdConfigRelDao.Properties.OperateTime.between(mYesterday, mToday))
            .buildCount().forCurrentThread()
            .count();
        mTotalPage = (count + LIMIT - 1) / LIMIT;
      }
    }
    //桌台信息
    else {
      int count = (int) DaoServiceUtil.getTableAlterationService()
          .queryBuilder()
          .where(PxTableAlterationDao.Properties.IsClear.eq(false))
          .where(PxTableAlterationDao.Properties.OperateTime.between(mYesterday, mToday))
          .buildCount().forCurrentThread()
          .count();
      //总页数
      mTotalPage = (count + LIMIT - 1) / LIMIT;
    }
    //数据过多,提示清理
    if (mTotalPage > 10) {
      Message msg = Message.obtain();
      msg.obj = "数据过多时,请及时清空数据!";
      msg.what = MSG_BOX;
      mHandler.sendMessage(msg);
    }
  }

  /**
   * 打印
   */
  private void performPrint(PrinterTask task, PxPrinterInfo printerInfo) {
    Socket socket = null;
    OutputStream outputStream = null;
    PrintWriter printWriter = null;
    try {
      String ip = printerInfo.getIpAddress();
      socket = new Socket();
      SocketAddress socketAddress = new InetSocketAddress(ip, 9100);
      socket.connect(socketAddress, 5000);
      outputStream = socket.getOutputStream();
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "GBK");
      printWriter = new PrintWriter(outputStreamWriter, true);
      task.run(outputStream, printWriter);
      if (task.getMode() != BTPrintConstants.PRINT_MODE_TEST) {
        savePrintOrderDetails();
      }
    } catch (Exception e) {
      Logger.e(e.toString());
      Message msg = Message.obtain();
      msg.obj = "打印失败，请检查网络连接";
      msg.what = MSG_BOX;
      mHandler.sendMessage(msg);
    } finally {
      IOUtils.closeCloseables(socket, outputStream, printWriter);
    }
  }

  /**
   * 加载数据
   */
  private void loadData() {
    //订单信息
    if (mIsCollect) {
      //一菜一切
      if (mIsOncePrint) {
        loadDetailsAndConfigRelData();
      }
      //非一菜一切
      else {
        loadCollectData();
      }
    }
    //桌台信息
    else {
      loadTableData();
    }
  }

  /**
   * 加载桌台信息
   */
  private void loadTableData() {
    //桌台信息
    List<PxTableAlteration> tableAlterationList = DaoServiceUtil.getTableAlterationService()
        .queryBuilder()
        .where(PxTableAlterationDao.Properties.IsClear.eq(false))
        .where(PxTableAlterationDao.Properties.OperateTime.between(mYesterday, mToday))
        .orderDesc(PxTableAlterationDao.Properties.OperateTime)
        .offset(LIMIT * (mCurrentPage - 1))
        .limit(LIMIT)
        .build().forCurrentThread()
        .list();
    //桌台信息转换为打印信息
    for (PxTableAlteration tableAlteration : tableAlterationList) {
      AppPrinterDetails printerDetails = new AppPrinterDetails();
      printerDetails.setType(AppPrinterDetails.TYPE_TABALTER);
      printerDetails.setAlteration(tableAlteration);
      mDocumentNeedList.add(printerDetails);
    }
  }

  /**
   * 加载Collect信息(非一菜一切)
   */
  private void loadCollectData() {
    //所有的下单汇总list 未清空的
    List<PrintDetailsCollect> collectList = DaoServiceUtil.getPdCollectService()
        .queryBuilder()
        .where(PrintDetailsCollectDao.Properties.OperateTime.between(mYesterday, mToday))
        .where(PrintDetailsCollectDao.Properties.DbConfigId.eq(mConfigPlan.getId()))
        .orderDesc(PrintDetailsCollectDao.Properties.PxOrderInfoId)
        .orderDesc(PrintDetailsCollectDao.Properties.OperateTime)
        .offset(LIMIT * (mCurrentPage - 1))
        .limit(LIMIT)
        .build().forCurrentThread()
        .list();

    //Collect
    for (PrintDetailsCollect collect : collectList) {
      List<PdConfigRel> dbPdConfigRelList = collect.getDbPdConfigRelList();
      if (dbPdConfigRelList.size() == 0) continue;
      //只添加属于配菜方案下商品
      List<PrintDetails> tempList = new ArrayList<PrintDetails>();
      for (PdConfigRel rel : dbPdConfigRelList) {
        PrintDetails dbPrintDetails = rel.getDbPrintDetails();
        if (dbPrintDetails == null) continue;
        tempList.add(dbPrintDetails);
      }
      if (tempList.size() == 0) continue;
      //构造数据
      AppPrinterDetails printerDetails = new AppPrinterDetails();
      printerDetails.setType(AppPrinterDetails.TYPE_DETAILS);
      printerDetails.setDetails(tempList);
      printerDetails.setCollect(collect);
      mDocumentNeedList.add(printerDetails);
    }
  }

  /**
   * 加载DetailAndConfigRel(一菜一切)
   */
  //@formatter:on
  private void loadDetailsAndConfigRelData() {
    //获取Details和配餐方案rel
    List<PdConfigRel> relList = DaoServiceUtil.getPdConfigRelService()
        .queryBuilder()
        .where(PdConfigRelDao.Properties.OperateTime.between(mYesterday, mToday))
        .where(PdConfigRelDao.Properties.DbConfigId.eq(mConfigPlan.getId()))
        .orderDesc(PdConfigRelDao.Properties.PxOrderInfoId)
        .orderDesc(PdConfigRelDao.Properties.OperateTime)
        .offset(LIMIT * (mCurrentPage - 1))
        .limit(LIMIT)
        .build()
        .forCurrentThread()
        .list();
    //转为为打印数据
    for (PdConfigRel rel : relList) {
      if (rel.getDbPrintDetails() == null) continue;
      //构造数据
      AppPrinterDetails printerDetails = new AppPrinterDetails();
      printerDetails.setType(AppPrinterDetails.TYPE_DETAILS);
      printerDetails.setRel(rel);
      mDocumentNeedList.add(printerDetails);
    }
  }

  /**
   * 更换item状态
   */
  private void changeStatus(AppPrinterDetails printerDetails) {
    mDocumentNeedList.remove(mCurrentDocumentPos);
    mDocumentNeedList.add(mCurrentDocumentPos, printerDetails);
    mDocumentAdapter.notifyItemChanged(mCurrentDocumentPos, printerDetails);
  }

  /**
   * 打印单据
   */
  private void printDocument() {
    if (mCurrentDocumentPos == -1) {
      ToastUtils.showShort(App.getContext(), "请选择您需要打印的单据!");
      return;
    }
    PxPrinterInfo dbPrinter = mConfigPlan.getDbPrinter();
    if (dbPrinter == null) {
      ToastUtils.showShort(App.getContext(), "打印信息有误");
      return;
    }
    //当前单据是详情
    if (mDocumentNeedList.get(mCurrentDocumentPos).getType() == AppPrinterDetails.TYPE_DETAILS) {
      //获取需要打印的OrderDetails
      AppPrinterDetails printerDetails = mDocumentNeedList.get(mCurrentDocumentPos);
      List<PrintDetails> printDetails;
      if (!mIsOncePrint) {
        printDetails = (List<PrintDetails>) printerDetails.getDetails();
      } else {
        printDetails = new ArrayList<>();
        PdConfigRel rel = printerDetails.getRel();
        printDetails.add(rel.getDbPrintDetails());
      }
      //打印份数
      int count = mConfigPlan.getCount();
      String planName = mConfigPlan.getName();
      PrinterTask task =
          new PrinterTask(BTPrintConstants.PRINT_KITCHEN_DETAILS, mIsOncePrint, printDetails, count,
              planName);
      task.setPrintInfo(dbPrinter);
      //
      isShowIntercept(true);
      //mPool.execute(new MyTask(PRINT_DOCUMENT,task,dbPrinter));
      mAsyncSession.runInTx(new MyTask(PRINT_DOCUMENT, task, dbPrinter));
    } else {//移并桌信息
      PxTableAlteration alteration = mDocumentNeedList.get(mCurrentDocumentPos).getAlteration();
      PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_TABLE, alteration);
      task.setPrintInfo(dbPrinter);
      //
      isShowIntercept(true);
      //mPool.execute(new MyTask(PRINT_DOCUMENT,task,dbPrinter));
      mAsyncSession.runInTx(new MyTask(PRINT_DOCUMENT, task, dbPrinter));
    }
  }

  /**
   * 保存打印的详情 为已打印
   */
  private void savePrintOrderDetails() {
    if (mDocumentNeedList == null || mDocumentNeedList.isEmpty()) return;
    AppPrinterDetails printerDetails = mDocumentNeedList.get(mCurrentDocumentPos);
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //订单信息
      if (printerDetails.getType() == AppPrinterDetails.TYPE_DETAILS) {
        //一菜一切
        if (mIsOncePrint) {
          PdConfigRel rel = printerDetails.getRel();
          rel.setIsPrinted(true);
          DaoServiceUtil.getPdConfigRelService().update(rel);
          printerDetails.setRel(rel);
        } else {
          //非一菜一切
          PrintDetailsCollect collect = printerDetails.getCollect();
          collect.setIsPrint(true);
          DaoServiceUtil.getPdCollectService().update(collect);
          printerDetails.setCollect(collect);
        }
      }
      //桌台信息
      else {
        PxTableAlteration tableAlteration = printerDetails.getAlteration();
        tableAlteration.setIsPrinted(true);
        DaoServiceUtil.getTableAlterationService().update(tableAlteration);
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Logger.e(e.toString());
    } finally {
      db.endTransaction();
    }
    //改变当前条目为已打印 - 通知单据列表刷新界面
    Message msg = Message.obtain();
    msg.obj = printerDetails;
    msg.what = CHANGE_STATUS;
    mHandler.sendMessage(msg);
  }

  /**
   * 打印测试
   */
  private void printTest() {
    if (mCurrentDevicePos < 0 || mCurrentDevicePos > mDeviceList.size()) {
      ToastUtils.showShort(null,"请选择打印机");
      return;
    }
    Object obj = mDeviceList.get(mCurrentDevicePos);
    PxPrinterInfo printerInfo = null;
    //配菜方案
    if (obj instanceof PxProductConfigPlan) {
      PxProductConfigPlan configPlan = (PxProductConfigPlan) obj;
      printerInfo = configPlan.getDbPrinter();
    } else {
      printerInfo = (PxPrinterInfo) obj;
    }
    if (printerInfo == null){
      ToastUtils.showShort(null,"打印数据有误!");
      return;
    }
    isShowIntercept(true);
    PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_TEST);
    PrinterTask clone = task.clone();
    clone.setPrintInfo(printerInfo);
    mAsyncSession.runInTx(new MyTask(PRINT_DOCUMENT, clone, printerInfo));
  }

  /**
   * 清空单据
   */
  private void clearDocument() {
    //对话框
    new MaterialDialog.Builder(this).
        title("提示")
        .content("是否要清空单据？")
        .positiveText("确定")
        .negativeText("取消")
        .titleGravity(GravityEnum.CENTER)
        .titleColorRes(R.color.primary_text)
        .contentColorRes(R.color.secondary_text)
        .backgroundColorRes(R.color.white)
        .negativeColor(getResources().getColor(R.color.secondary_text))
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            startClearDocument();
          }
        })
        .show();
  }

  /**
   * 清空单据列表
   */
  private void startClearDocument() {
    //
    isShowIntercept(true);
    //mPool.execute(new MyTask(CLEAR_DATA));
    mAsyncSession.runInTx(new MyTask(CLEAR_DATA));
  }

  /**
   * 清空数据
   */
  // @formatter:on
  private void performClearData() {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //PrintDetails
      DaoServiceUtil.getPrintDetailsService().deleteAll();
      //PdCollect
      DaoServiceUtil.getPdCollectService().deleteAll();
      //PdConfigRel
      DaoServiceUtil.getPdConfigRelService().deleteAll();
      //TableAlteration
      DaoServiceUtil.getTableAlterationService().deleteAll();

      db.setTransactionSuccessful();
    } catch (Exception e) {
    } finally {
      db.endTransaction();
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
    // 注销连接状态监听
    PrintEventManager.getManager().unRegistPrinterConnectStatus(KitchenPrintActivity.this);
    ButterKnife.unbind(this);
    if (mHandler != null) {
      mHandler.removeCallbacksAndMessages(null);
    }
  }

  /**
   * 蒙层状态
   */
  private boolean mIsShow = false;

  private void isShowIntercept(boolean isShow) {
    mIsShow = isShow;
    if (mView == null || isShow == mView.isIntercept()) return;
    mView.setIntercept(isShow);
    mLoading.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }

  /**
   * 初始化页数显示
   */
  private void showPage() {
    if (mDocumentNeedList.size() == 0) {
      nullDocumentList();
      return;
    }
    //先设置打印机名字
    mDocumentAdapter.setCategoryName(mConfigPlan.getName());
    mDocumentAdapter.setData(mDocumentNeedList, mIsOncePrint);
    mTvPage.setText(mCurrentPage + "/" + mTotalPage);
  }

  /**
   * 消息盒子
   */
  private void msgBox(String msg) {
    if (TextUtils.isEmpty(msg)) return;
    ToastUtils.showShort(App.getContext(), msg);
  }

  /**
   * 更新打印机连接状态
   * 由PrinterService发送
   */
  private Boolean mPreHasChange;

  @Override public void dispatchConnectStatus(final Map<String, String> map, boolean hasChange) {
    if (mPreHasChange == null || mPreHasChange != hasChange) {
      runOnUiThread(new Runnable() {
        @Override public void run() {
          if (mDeviceAdapter != null && !KitchenPrintActivity.this.isFinishing() && !mIsShow) {
            mDeviceAdapter.setRefreshData(map);
          }
        }
      });
    }
    mPreHasChange = hasChange;
  }
}