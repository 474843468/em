package com.psi.easymanager.common;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.IBinder;
import android.support.multidex.MultiDex;
import android_serialport_api.SerialParam;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortOperaion;
import com.facebook.stetho.Stetho;
import com.gprinter.aidl.GpService;
import com.gprinter.service.GpPrintService;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.BuildConfig;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.UserDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.dao.dbUtil.DbCore;
import com.psi.easymanager.module.User;
import com.psi.easymanager.print.usb.AppGpService;
import com.psi.easymanager.print.usb.AppUsbDeviceName;
import com.psi.easymanager.utils.SPUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import java.util.HashMap;
import java.util.Iterator;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by zjq on 2016/2/24.
 * Application
 */
public class App extends Application {
  public static final String JT = "1";
  public static final String HDX = "2";
  public static final String NOT_CUSTOMER_DISPLAY = "0";

  private static Context context = null;
  private static App instance;
  private static User mUser;
  private boolean mIsFromDataUpdate = false;
  private boolean mIsOpenLabel = false;
  //客显
  private SerialPortOperaion mSerialPortOperation = null;
  //佳田
  private SerialPort mSerialPortJt = null;
  //好德芯
  private SerialParam mSerialParamHdx = null;
  private RefWatcher refWatcher;
  private int baudrate = 2400;//传输速率
  private String path = "/dev/ttyS3";//输出路径

  //Usb打印Service
  private GpService mGpService = null;
  private PrinterServiceConnection conn = null;
  private String mDeviceName;//USB设备名
  private String mDevice;

  /**
   * 返回应用实例
   */
  public static App getInstance() {
    return instance;
  }

  class PrinterServiceConnection implements ServiceConnection {

    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      String device_model = Build.MODEL; // 设备型号
      if (!(("86v_rgb_printer".equals(device_model) || "QBOSSI_printer".equals(device_model)
          || "QBOSSI_rgb_printer".equals(device_model)))) {
        return;
      }
      mGpService = GpService.Stub.asInterface(iBinder);
      //发送Gp服务
      EventBus.getDefault().postSticky(new AppGpService(mGpService));
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
      Logger.i("ServiceConnection", "onServiceDisconnected() called");
    }
  }

  public static Context getContext() {
    return context;
  }

  @Override public void onCreate() {
    super.onCreate();
    instance = this;
    context = getApplicationContext();
    initCrashHandler();
    initLogger();
    initStetho();
    initDb();
    initBugly();
    initJb();
    initLeak();
    initCustomerDisplay();
  }

  public static RefWatcher getRefWatcher(Context context) {
    App app = (App) context.getApplicationContext();
    return app.refWatcher;
  }

  /**
   * 初始化客显
   */
  private void initCustomerDisplay() {
    String device_model = Build.MODEL; // 设备型号
    mDevice = device_model.trim();
    //佳田
    for (String customer : Constants.CUSTOMERS_JT) {
      if (mDevice.equals(customer)) {
        SPUtils.put(this, Constants.CUSTOMER_DISPLAY_TYPE, JT);
        break;
      }
    }
    //好德芯
    for (String customer : Constants.CUSTOMERS_HDX) {
      if (mDevice.equals(customer)) {
        SPUtils.put(this, Constants.CUSTOMER_DISPLAY_TYPE, HDX);
        return;
      }
    }
  }

  /**
   * 好德芯
   */
  public SerialPortOperaion getSerialPortOperaion() {
    if (mSerialParamHdx == null) {
      mSerialParamHdx = new SerialParam(baudrate, path, 0);
      mSerialPortOperation = new SerialPortOperaion(null, mSerialParamHdx);
    }
    return mSerialPortOperation;
  }

  /**
   * 佳田
   */
  public SerialPort getSerialPort() throws Exception {
    if (mSerialPortJt == null) {
      if ("rk3288".equals(mDevice)) {
         path = "/dev/ttyS1";
      }
      /* Open the serial port */
      mSerialPortJt = new SerialPort(path, baudrate, 0);
    }
    return mSerialPortJt;
  }

  /**
   * 关闭客显
   */
  public void closeCustomerDisplay() {
    if (mSerialPortJt != null) {
      mSerialPortJt.close();
      mSerialPortJt = null;
    }
    if (mSerialPortOperation != null) {
      mSerialPortOperation.StopSerial();
      mSerialPortOperation = null;
    }
  }

  /**
   * Memory Leak
   */
  private void initLeak() {
    boolean debug = BuildConfig.DEBUG;
    if (debug) {
      refWatcher = LeakCanary.install(this);
    }
  }

  /**
   * User打印服务
   */
  //@formatter:off
  private void initJb() {
    String device = Build.MODEL; // 设备型号
    boolean isSupported = ("86v_rgb_printer".equals(device) || "QBOSSI_printer".equals(device) || "QBOSSI_rgb_printer".equals(device));
    //是否支持USB打印  0：支持  1：不支持
    SPUtils.put(this, Constants.SUPPORT_USB_PRINT, isSupported ? "0" : "1");
    if (isSupported) {
      //获取USB设备名
      UsbManager manager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
      HashMap<String, UsbDevice> devices = manager.getDeviceList();
      Iterator<UsbDevice> deviceIterator = devices.values().iterator();
      int count = devices.size();
      if (count > 0) {
        while (deviceIterator.hasNext()) {
          UsbDevice usbDevice = deviceIterator.next();
          if (Build.VERSION.SDK_INT < 21) {
            if (usbDevice.getProductId() == Constants.USB_PRODUCT_ID) {
              mDeviceName = usbDevice.getDeviceName();
              break;
            }
          } else {
            if (usbDevice.getManufacturerName().equals(Constants.USB_MANUFACTURER_NAME)) {
              mDeviceName = usbDevice.getDeviceName();
              break;
            }
          }
        }
      }
      EventBus.getDefault().postSticky(new AppUsbDeviceName(mDeviceName));
      //绑定Usb打印服务
      conn = new PrinterServiceConnection();
      Intent intent = new Intent(this, GpPrintService.class);
      bindService(intent, conn, BIND_AUTO_CREATE);
    }
  }

  /**
   * Logger
   */
  private void initLogger() {
    Logger.init("FireLog").setMethodCount(3).hideThreadInfo().setLogLevel(LogLevel.FULL);
  }

  /**
   * Stetho
   */
  private void initStetho() {
    Stetho.initializeWithDefaults(this);
  }

  /**
   * 数据库
   */
  private void initDb() {
    DbCore.init(App.getContext());
    //DbCore.enableQueryBuilderLog();
    DbCore.update(App.getContext());
  }

  /**
   * Bugly
   */
  private void initBugly() {
    CrashReport.initCrashReport(context, "900032643", true);
    String companyCode = (String) SPUtils.get(App.this, Constants.SAVE_STORE_NUM, "未知");
    CrashReport.setUserId(companyCode + "_debug:"+BuildConfig.DEBUG);
  }

  /**
   * Crash
   */
  private void initCrashHandler() {
    if (!BuildConfig.DEBUG) {
      AppCrashCatcher appCrashCatcher = AppCrashCatcher.newInstance();
      appCrashCatcher.setDefaultCrashCatcher();
    }
  }



  public void closeGpPort() {
    if (conn != null) {
      unbindService(conn);
    }
  }

  /**
   * MultiDex
   */
  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  @Override public void onTerminate() {
    super.onTerminate();
    closeGpPort();
    closeCustomerDisplay();
  }

  /**
   * Get and Set
   */
  public User getUser() {
    if (mUser == null) {
      String  userObjId = (String) SPUtils.get(this, Constants.LOGIN_USER_OBJID, "0");
      mUser = DaoServiceUtil.getUserService()
          .queryBuilder()
          .where(UserDao.Properties.ObjectId.eq(userObjId))
          .unique();
    }
    return mUser;
  }

  public void setUser(User user) {
    mUser = user;
  }

  public boolean isOpenLabel() {
    return mIsOpenLabel;
  }

  public void setOpenLabel(boolean openLabel) {
    mIsOpenLabel = openLabel;
  }

  public boolean isFromDataUpdate() {
    return mIsFromDataUpdate;
  }

  public void setFromDataUpdate(boolean fromDataUpdate) {
    mIsFromDataUpdate = fromDataUpdate;
  }
}
