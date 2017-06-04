package com.psi.easymanager.ui.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.psi.easymanager.adapter.OverBillSaleContentAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxProductCategoryDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.event.SaleContentEvent;
import com.psi.easymanager.module.AppSaleContent;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxProductInfo;
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
 * 已结账单 销售统计
 * Created by zjq on 2016/6/6.
 */
public class OverBillSaleContentFragment extends BaseFragment {
  //所属分类名称
  @Bind(R.id.tv_title) TextView mTvTitle;
  //Rcv
  @Bind(R.id.rcv_sale_content) RecyclerView mRcvSaleContent;

  //Activity
  private OverBillActivity mAct;
  //Fragment管理
  private FragmentManager mFm;

  //所属分类
  private PxProductCategory mCategory;
  //时间筛选条件
  private int mTimeFilter;
  //Data
  private List<AppSaleContent> mSaleContentList;
  //Adapter
  private OverBillSaleContentAdapter mSaleContentAdapter;

  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //单一线程用于打印
  private ExecutorService sDbEngine = null;

  public static OverBillSaleContentFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    OverBillSaleContentFragment fragment = new OverBillSaleContentFragment();
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
    View view = inflater.inflate(R.layout.fragment_over_bill_sale_content, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(SaleContentEvent.class);
    EventBus.getDefault().getStickyEvent(AppGpService.class);
  }

  /**
   * 初始化Rcv
   */
  //@formatter:off
  private void initRcv() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false);
    mSaleContentAdapter = new OverBillSaleContentAdapter(mAct,mSaleContentList);
    mRcvSaleContent.setHasFixedSize(true);
    mRcvSaleContent.setLayoutManager(layoutManager);
    mRcvSaleContent.setAdapter(mSaleContentAdapter);
  }

  /**
   * 接收商品分类信息和筛选条件
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void getCategoryAndFilter(SaleContentEvent event) {
    mCategory = event.getCategory();
    mTimeFilter = event.getTimeFilter();
    //标题
    mTvTitle.setText(mCategory.getName());
    //查询数据
    queryData(mTimeFilter);
  }

  /**
   * 查询数据
   * @param timeFilter
   */
  //@formatter:off
  private void queryData(final int timeFilter) {
    //今日开始
    final Date todayBegin = new Date();
    todayBegin.setHours(0);
    todayBegin.setMinutes(0);
    todayBegin.setSeconds(0);
    //今日结束
    final Date todayEnd = new Date();
    todayEnd.setHours(23);
    todayEnd.setMinutes(59);
    todayEnd.setSeconds(59);
    //今日开始
    long todayBeginTime = todayBegin.getTime();
    //今日结束
    long todayEndTime = todayEnd.getTime();
    //昨日开始
    final Date yesterdayBegin = new Date(todayBeginTime - 86400000);
    //昨日结束
    final Date yesterdayEnd = new Date(todayEndTime - 86400000);
    //更新列表
    mSaleContentList = new ArrayList<AppSaleContent>();
    new Thread() {
      @Override public void run() {
        switch (timeFilter) {
          case OverBillSaleListFragment.TODAY:
            mSaleContentList = getAllProdList(todayBegin, todayEnd, mCategory);
            break;
          case OverBillSaleListFragment.YESTERDAY:
            mSaleContentList = getAllProdList(yesterdayBegin, yesterdayEnd, mCategory);
            break;
          case OverBillSaleListFragment.TWO_DAYS:
            mSaleContentList = getAllProdList(yesterdayBegin, todayEnd, mCategory);
            break;
        }
        mAct.runOnUiThread(new Runnable() {
          @Override public void run() {
            if (mSaleContentAdapter == null) {
              initRcv();
            } else {
              mSaleContentAdapter.setData(mSaleContentList);
            }
          }
        });
      }
    }.start();
  }

  //递归获取所有销售信息
  private List<AppSaleContent> getAllProdList(Date start, Date end, PxProductCategory category) {
    List<AppSaleContent> prodList = new ArrayList<AppSaleContent>();
    if (category.getLeaf().equals(PxProductCategory.IS_LEAF)) {
      List<AppSaleContent> list = getAllProductSaleNum(start, end, category);
      prodList.addAll(list);
    } else {
      List<PxProductCategory> childCateList = DaoServiceUtil.getProductCategoryService()
          .queryBuilder()
          .where(PxProductCategoryDao.Properties.ParentId.eq(category.getObjectId()))
          .where(PxProductCategoryDao.Properties.DelFlag.eq("0"))
          .list();
      for (PxProductCategory childCate : childCateList) {
        List<AppSaleContent> childProdList = getAllProdList(start, end, childCate);
        if (childProdList != null || childCateList.size() != 0) {
          prodList.addAll(childProdList);
        }
      }
    }
    return prodList;
  }

  //获取销售信息
  private List<AppSaleContent> getAllProductSaleNum(Date start, Date end,
      PxProductCategory category) {
    SQLiteDatabase db = DaoServiceUtil.getOrderDetailsDao().getDatabase();
    Cursor cursor = null;
    cursor = db.rawQuery(
        "Select sum(d.NUM),sum(d.MULTIPLE_UNIT_NUMBER),p.NAME,p.MULTIPLE_UNIT,p.UNIT"
            + " From OrderDetails d" + " Join OrderInfo o On o._id = d.PX_ORDER_INFO_ID"
            + " Join ProductInfo p On p._id = d.PX_PRODUCT_INFO_ID" + " Where o.STATUS = "
            + PxOrderInfo.STATUS_FINISH + " And p.PX_PRODUCT_CATEGORY_ID = " + category.getId()
            + " And o.END_TIME BETWEEN " + start.getTime() + " AND " + end.getTime()
            + " And d.ORDER_STATUS = " + PxOrderDetails.ORDER_STATUS_ORDER + " And d.IN_COMBO = "
            + PxOrderDetails.IN_COMBO_FALSE + " Or d.IN_COMBO = NULL" + " Group By p._id", null);
    mSaleContentList = new ArrayList<AppSaleContent>();
    while (cursor.moveToNext()) {
      AppSaleContent appSaleContent = new AppSaleContent();
      appSaleContent.setSaleNumber((int) cursor.getDouble(0));
      appSaleContent.setSaleMultNumber(cursor.getDouble(1));
      appSaleContent.setProdName(cursor.getString(2));
      appSaleContent.setMultUnitProd(PxProductInfo.IS_TWO_UNIT_TURE.equals(cursor.getString(3)));
      appSaleContent.setUnit(cursor.getString(4));
      mSaleContentList.add(appSaleContent);
    }
    IOUtils.closeCloseables(cursor);
    return mSaleContentList;
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

  @OnClick(R.id.ibtn_sale_print) public void printSale(View view) {
    //网络打印
    printByNetAndBT();
    //2-USB打印
    //Gp是否支持USB打印
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
    PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_SALE_COUNT, mCategory, mSaleContentList);
    PrintTaskManager.printCashTask(task);
    BTPrintTask btPrintTask = new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_SALE_COUNT)
        .category(mCategory)
        .saleContentList(mSaleContentList)
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
          if (mCategory != null && mSaleContentList != null) {
            PrinterUsbData.printOverBillSaleListInfo(mAppGpService.getGpService(), mCategory,
                mSaleContentList);
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
    if (OpenPortEvent.SALE_CONNECT_PORT.equals(event.getType())) {
      //Gp是否支持USB打印
      String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
      if (isSupportUSBPrint.equals("1")) return;
      //是否已配置开启USB打印
      boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
      if (isPrint) {
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            if (mCategory != null && mSaleContentList != null) {
              PrinterUsbData.printOverBillSaleListInfo(mAppGpService.getGpService(), mCategory,
                  mSaleContentList);
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
