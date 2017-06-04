package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.ShiftCateAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.module.AppShiftCateInfo;
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
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by psi on 2016/7/30.
 * 交接班-分类汇总
 */
public class ShiftCateCollectFragment extends BaseFragment {

  @Bind(R.id.rcv) RecyclerView mRcvShiftCate;

  private static final String SHIFT_CATE_COLLECT_PARAM = "param";
  private String mParam;
  private ShiftChangeFunctionsActivity mAct;

  //Data
  private List<AppShiftCateInfo> mShiftCateInfoList;
  //Adapter
  private ShiftCateAdapter mShiftCateAdapter;
  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //打印用
  private ShiftWork mShiftWork;
  //单一线程用于打印
  private ExecutorService sDbEngine = null;

  public static ShiftCateCollectFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    ShiftCateCollectFragment fragment = new ShiftCateCollectFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam = getArguments().getString(SHIFT_CATE_COLLECT_PARAM);
    }
    mAct = (ShiftChangeFunctionsActivity) getActivity();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_shift_cate_collect, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initRcv();
    EventBus.getDefault().register(this);
    //加载数据
    mShiftCateInfoList = mAct.getCateInfoList();
    mShiftCateAdapter.setData(mShiftCateInfoList);

    //set分类汇总
    mShiftWork = mAct.getShiftWork();
    mShiftWork.setCategoryCollectList(mShiftCateInfoList);
  }

  /**
   * 初始化Rcv
   */
  private void initRcv() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false);
    mShiftCateInfoList = new ArrayList<AppShiftCateInfo>();
    mShiftCateAdapter = new ShiftCateAdapter(mAct, mShiftCateInfoList);
    mRcvShiftCate.setHasFixedSize(true);
    mRcvShiftCate.setLayoutManager(layoutManager);
    mRcvShiftCate.setAdapter(mShiftCateAdapter);
  }

  /**
   * 打印分类汇总信息
   */
  @OnClick(R.id.ibtn_shift_print) public void printCategoryCollect(ImageButton iBtn) {
    if (mShiftWork == null) {
      ToastUtils.showShort(App.getContext(), "没有可打印的订单信息");
      return;
    }
    //网络打印 收银
    printByNetAndBT();
    //2-USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
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
  private void printByNetAndBT() {
    PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_CATEGORY_COLLECT, mShiftWork);
    PrintTaskManager.printCashTask(task);
    BTPrintTask btPrintTask = new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_CATEGORY_COLLECT)
        .shiftWork(mShiftWork)
        .build();
    PrintEventManager.getManager().postBTPrintEvent(btPrintTask);
  }
  /**
   * USB打印 收银
   */
  private void printByUSBPrinter(){
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          PrinterUsbData.printAllCateCollect(mAppGpService.getGpService(), mShiftCateInfoList, mShiftWork);
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
   * 分类汇总 用于交接班打印
   */
  public List<AppShiftCateInfo> getShiftCateInfoList(){
    return mShiftCateInfoList;
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
    if(OpenPortEvent.SHIFT_BILL_CATE_PORT.equals(event.getType())){
      //Gp是否支持USB打印
      String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
      if (isSupportUSBPrint.equals("1")) return;
      //是否已配置开启USB打印
      boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
      if (isPrint) {
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            PrinterUsbData.printAllCateCollect(mAppGpService.getGpService(), mShiftCateInfoList, mShiftWork);
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
  public void closePool(){
    if (sDbEngine != null){
      sDbEngine.shutdown();
      sDbEngine = null;
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

}
