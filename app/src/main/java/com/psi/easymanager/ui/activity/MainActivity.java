package com.psi.easymanager.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortOperaion;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.chat.chatutils.XMPPConnectUtils;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxPrinterInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.ChatLoginEvent;
import com.psi.easymanager.event.HideProgressEvent;
import com.psi.easymanager.event.PrinterPingErrorEvent;
import com.psi.easymanager.event.ShowProgressEvent;
import com.psi.easymanager.event.SpeechEvent;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.module.PxSetInfo;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.service.BTPrintService;
import com.psi.easymanager.service.ChatLoginService;
import com.psi.easymanager.service.ChatSmackService;
import com.psi.easymanager.service.ConnectService;
import com.psi.easymanager.service.PingService;
import com.psi.easymanager.service.PrintQueueService;
import com.psi.easymanager.service.PrinterService;
import com.psi.easymanager.service.UpLoadDataService;
import com.psi.easymanager.ui.fragment.CashBillFragment;
import com.psi.easymanager.ui.fragment.CashMenuFragment;
import com.psi.easymanager.ui.fragment.CashMenuFuzzyQueryFragment;
import com.psi.easymanager.ui.fragment.CheckOutFragment;
import com.psi.easymanager.ui.fragment.FindBillFragment;
import com.psi.easymanager.ui.fragment.ModifyBillFragment;
import com.psi.easymanager.ui.fragment.StartBillFragment;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ScanGunKeyEventUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.InterceptClickLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class MainActivity extends BaseActivity
    implements SpeechSynthesizerListener, ScanGunKeyEventUtils.OnScanSuccessListener {
  @Bind(R.id.rb_tab_kitchen_print) RadioButton mRbTabKitchenPrint;
  //Fab
  @Bind(R.id.rl_cash_btns) public RelativeLayout mCashFabs;
  //网络错误描述
  @Bind(R.id.iv_network_error) ImageView mIvNetworkError;
  //网络错误提示按钮
  private ImageView mIvMessageClick;

  //内容view
  @Bind(R.id.content_view) InterceptClickLayout mRlContent;
  //刷新view
  @Bind(R.id.progress_view) RelativeLayout mRlProgress;
  //刷新view显示名称
  @Bind(R.id.tv_progress_name) TextView mTvProgressName;

  private ScanGunKeyEventUtils mScanGunKeyEventUtils;
  //Fragment管理器
  private FragmentManager mFm;
  //Fragment
  private CashBillFragment mCashBillFragment;
  private CashMenuFragment mCashMenuFragment;
  private ModifyBillFragment mModifyBillFragment;
  private StartBillFragment mStartBillFragment;
  private CheckOutFragment mCheckOutFragment;
  private FindBillFragment mFindBillFragment;
  private CashMenuFuzzyQueryFragment mFuzzyQueryFragment;

  //连接配置
  private XmppConnectionListener mXmppConnectionListener;

  //客显相关
  byte[] mDisplayBuffer, mDisplayBuffer1, mDisplayBuffer2, mDisplayBuffer3;
  //佳田
  protected SerialPort mSerialPort = null;
  protected OutputStream mOutputStream = null;
  //好德芯
  protected SerialPortOperaion mSerialPortOperaionHdx = null;

  //语音合成客户端
  private SpeechSynthesizer mSpeechSynthesizer;
  //TTS文件路径
  private String mSampleDirPath;
  private static final String SAMPLE_DIR_NAME = "EasyManagerTTS";
  private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
  private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";

  //BT
  private static final int OPEN_BLUETOOTH_REQUEST = 15;//打开蓝牙请求
  private BTStateReceiver mBtStateReceiver;

  public boolean mIsHDX065 = false;//是否是HDX065设备(用于自带开钱箱，蓝牙打印)

  @Override protected int provideContentViewId() {
    return R.layout.activity_main;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //
    mIvMessageClick = (ImageView) findViewById(R.id.iv_message_click);
    ButterKnife.bind(this);
    EventBus.getDefault().register(this);
    //BT Print
    initBluePrint();
    //初始化设置信息
    initSetInfo();
    //Fragment管理器
    mFm = getSupportFragmentManager();
    if (savedInstanceState == null) {
      //初始化Fragment
      initFragment(mFm.beginTransaction());
    } else {
      mCashBillFragment = (CashBillFragment) mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
      mCashMenuFragment = (CashMenuFragment) mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
      mStartBillFragment = (StartBillFragment) mFm.findFragmentByTag(Constants.START_BILL_TAG);
      mModifyBillFragment = (ModifyBillFragment) mFm.findFragmentByTag(Constants.MODIFY_BILL_TAG);
      mCheckOutFragment = (CheckOutFragment) mFm.findFragmentByTag(Constants.CHECK_OUT_TAG);
      mFindBillFragment = (FindBillFragment) mFm.findFragmentByTag(Constants.FIND_BILL_TAG);
      mFuzzyQueryFragment =
          (CashMenuFuzzyQueryFragment) mFm.findFragmentByTag(Constants.CASH_MENU_FUZZY_QUERY_TAG);
      //初始化Fragment
      FragmentTransaction transaction = mFm.beginTransaction();
      hideAllFragment(transaction);
      initFragment(transaction);
    }
    //开启上传营业数据service
    startService(new Intent(this, UpLoadDataService.class));
    //可用的打印机 开启检测打印机服务
    checkPrinterIsConnectService(true);
    //初始化客显
    initCustomerDisplay();
    //初始化TTS
    new Thread() {
      @Override public void run() {
        super.run();
        initTTS();
      }
    }.start();
    //ChatLogin
    startService(new Intent(this, ChatLoginService.class));
    //扫码枪
    mScanGunKeyEventUtils = new ScanGunKeyEventUtils(this);
  }

  /**
   * 支持蓝牙打印
   */
  private void initBluePrint() {
    //检测蓝牙开启
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //不支持蓝牙
    if (bluetoothAdapter == null) return;
    //支持HDX065
    String device_model = Build.MODEL; // 设备型号
    for (String device : Constants.BLUE_PRINT) {
      if (device.equals(device_model)) {
        mIsHDX065 = true;
        break;
      }
    }
    long count = DaoServiceUtil.getBTDeviceService().count();

    if (mIsHDX065 || count > 0) {
      //打开蓝牙
      if (!bluetoothAdapter.isEnabled()) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, OPEN_BLUETOOTH_REQUEST);
      } else {
        startService(new Intent(this, BTPrintService.class));
      }
      //注册蓝牙状态变化广播
      registBTStateReceiver();
    }
  }

  /**
   * 注册蓝牙状态监听广播
   */
  private void registBTStateReceiver() {
    mBtStateReceiver = new BTStateReceiver();
    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    registerReceiver(mBtStateReceiver, filter);
  }

  /**
   * 蓝牙状态广播
   */
  class BTStateReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
      Logger.e(intent.getAction());
      if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch (state) {
          case BluetoothAdapter.STATE_OFF:
            ToastUtils.showShort(context, "蓝牙已关闭，无法使用蓝牙打印!");
            stopService(new Intent(context, BTPrintService.class));
            break;
          case BluetoothAdapter.STATE_TURNING_OFF:
            ToastUtils.showShort(context, "蓝牙正在关闭，无法使用蓝牙打印!");
            break;
          case BluetoothAdapter.STATE_ON:
            ToastUtils.showShort(context, "蓝牙开启!");
            startService(new Intent(context, BTPrintService.class));
            break;
          case BluetoothAdapter.STATE_TURNING_ON:
            ToastUtils.showShort(context, "蓝牙正在开启!");
            break;
        }
      }
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == OPEN_BLUETOOTH_REQUEST) {//打开蓝牙请求
      if (resultCode == Activity.RESULT_OK) {
        ToastUtils.showShort(this, "蓝牙已打开");
        //start
        startService(new Intent(this, BTPrintService.class));
      } else {
        ToastUtils.showShort(this, "蓝牙没有打开，无法使用蓝牙打印功能");
      }
    }
  }

  /**
   * 初始化TTS
   */
  //@formatter:off
  private void initTTS() {
    if (mSampleDirPath == null) {
      String sdcardPath = Environment.getExternalStorageDirectory().toString();
      mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
    }
    File file = new File(mSampleDirPath);
    if (!file.exists()) {
      file.mkdirs();
    }
    copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
    copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);

    //初始化语音合成客户端并启动
    //获取语音合成对象实例
    mSpeechSynthesizer = SpeechSynthesizer.getInstance();
    //设置context
    mSpeechSynthesizer.setContext(this);
    //设置语音合成状态监听器
    mSpeechSynthesizer.setSpeechSynthesizerListener(this);
    //设置在线语音合成授权，需要填入从百度语音官网申请的api_key和secret_key
    mSpeechSynthesizer.setApiKey("Q169TBuyYYehatNXbis1ozKj", "de7cf8dc56bed9eef94d99035e036399");
    //设置离线语音合成授权，需要填入从百度语音官网申请的app_id
    mSpeechSynthesizer.setAppId("8747181");
    //设置语音合成文本模型文件
    mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/" + TEXT_MODEL_NAME);
    //设置语音合成声音模型文件
    mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
    //音量
    mSpeechSynthesizer.setStereoVolume(1.0f, 1.0f);
    //获取语音合成授权信息
    AuthInfo authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);
    //判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
    if (authInfo != null && authInfo.isSuccess()) {
      try{
        mSpeechSynthesizer.initTts(TtsMode.MIX);
      }catch (Throwable e){
        mSpeechSynthesizer = null;
      }
    } else {
      //授权失败
    }
  }

  /**
   * 将资源文件拷贝到SD卡中使用
   *
   * @param isCover 是否覆盖已存在的目标文件
   */
  private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
    File file = new File(dest);
    if (isCover || (!isCover && !file.exists())) {
      InputStream is = null;
      FileOutputStream fos = null;
      try {
        is = getResources().getAssets().open(source);
        String path = dest;
        fos = new FileOutputStream(path);
        byte[] buffer = new byte[1024];
        int size = 0;
        while ((size = is.read(buffer, 0, 1024)) >= 0) {
          fos.write(buffer, 0, size);
        }
      } catch (FileNotFoundException e) {
        Logger.i(e.getMessage());
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        IOUtils.closeCloseables(fos,is);
      }
    }
  }

  @Override public void onSynthesizeStart(String s) {

  }

  @Override public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

  }

  @Override public void onSynthesizeFinish(String s) {

  }

  @Override public void onSpeechStart(String s) {

  }

  @Override public void onSpeechProgressChanged(String s, int i) {

  }

  @Override public void onSpeechFinish(String s) {

  }

  @Override public void onError(String s, SpeechError speechError) {
    Logger.i(speechError.toString());
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void speech(SpeechEvent event) {
    String content = event.getContent();
    if (mSpeechSynthesizer != null && content != null) {
      mSpeechSynthesizer.speak(content);
    }
  }

  /**
   * 初始化客显
   */
  private void initCustomerDisplay() {
    try {
      //好德芯
      if (App.HDX.equals(SPUtils.get(this, Constants.CUSTOMER_DISPLAY_TYPE, App.NOT_CUSTOMER_DISPLAY))){
        mSerialPortOperaionHdx = ((App) getApplication()).getSerialPortOperaion();
        mSerialPortOperaionHdx.StartSerial();
      }//佳田
      else if (App.JT.equals(SPUtils.get(this, Constants.CUSTOMER_DISPLAY_TYPE, App.NOT_CUSTOMER_DISPLAY))){
        mSerialPort = ((App) getApplication()).getSerialPort();
        mOutputStream = mSerialPort.getOutputStream();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  /**
   * 客显
   */
  public void showCustomerDisplay(double price) {
      if (mSerialPort != null||mSerialPortOperaionHdx!=null) {
        mDisplayBuffer = new byte[1];
        mDisplayBuffer[0] = 0x33;
        DisplayData(price + "", mDisplayBuffer);
      }
  }

  /**
   * 客显相关
   */
  public void DisplayData(String data, byte[] _displayType) {
    ////清屏: CLR 格式：0C
    //mDisplayBuffer = new byte[1];
    //mDisplayBuffer[0] = 0x0C;
    //SendStr(mDisplayBuffer);

    //mDisplayBuffer = new byte[3];
    //mDisplayBuffer[0] = 0x1B;
    //mDisplayBuffer[1] = 0x73;
    //mDisplayBuffer[2] = _displayType[0];
    //SendStr(mDisplayBuffer);
    //
    //mDisplayBuffer = new byte[3];
    //mDisplayBuffer[0] = 0x1B;
    //mDisplayBuffer[1] = 0x51;
    //mDisplayBuffer[2] = 0x41;
    //mDisplayBuffer1 = new byte[20];
    //try {
    //  mDisplayBuffer1 = data.getBytes("GB2312");
    //} catch (UnsupportedEncodingException e) {
    //  e.printStackTrace();
    //}
    //mDisplayBuffer2 = new byte[20];
    //mDisplayBuffer2 = byteMerger(mDisplayBuffer, mDisplayBuffer1);
    //mDisplayBuffer = new byte[1];
    //mDisplayBuffer[0] = 0x0D;
    //mDisplayBuffer3 = new byte[20];
    //mDisplayBuffer3 = byteMerger(mDisplayBuffer2, mDisplayBuffer);
    //SendStr(mDisplayBuffer3);

    mDisplayBuffer = new byte[6];
    mDisplayBuffer[0] = 0x1B;
    mDisplayBuffer[1] = 0x73;
    mDisplayBuffer[2] =  _displayType[0];
    mDisplayBuffer[3] = 0x1B;
    mDisplayBuffer[4] = 0x51;
    mDisplayBuffer[5] = 0x41;
    mDisplayBuffer1 = new byte[15];
    //小灯控制: ESC s(0-1) CR 格式: 1B 73 （30—34）
    //小灯控制类型
    /*
       *当n=48(0x30),  状态灯全灭。
      *当n=49(0x31), “单价”字符亮，其它三种暗
      *当n=50(0x32), “合计”字符亮，其它三种暗
      *当n=51(0x33), “收款”字符亮，其它三种暗
      *当n=52(0x34), “找零”字符亮，其它三种暗
      */
    try {
      mDisplayBuffer1 = data.getBytes("GB2312");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    mDisplayBuffer2 = new byte[21];
    mDisplayBuffer2 = byteMerger(mDisplayBuffer, mDisplayBuffer1);
    mDisplayBuffer = new byte[1];
    mDisplayBuffer[0] = 0x0D;
    mDisplayBuffer3 = new byte[22];
    mDisplayBuffer3 = byteMerger(mDisplayBuffer2, mDisplayBuffer);
    SendStr(mDisplayBuffer3);

  }

  //向串口发送数据
  //@formatter:off
  public void SendStr(byte[] buffer) {
    try {
      if (mOutputStream != null) {
       mOutputStream.write(buffer);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
     if (mSerialPortOperaionHdx!=null){
       new WriteThread(buffer).start();

    }
  }
  //写数据到客显
  private class WriteThread extends Thread {
    byte[] mSerialPortHdxBuffer;
    WriteThread(byte[] buffer){
      mSerialPortHdxBuffer=buffer;
    }
    public void run() {
      super.run();
      try {
          mSerialPortOperaionHdx.WriteData(mSerialPortHdxBuffer);
        } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  //java 合并两个byte数组
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
    byte[] byte_3 = new byte[byte_1.length + byte_2.length];
    System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
    return byte_3;
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    //刷新页面
    CashMenuFragment cashMenuFragment = (CashMenuFragment) mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    if (cashMenuFragment != null) {
      cashMenuFragment.refreshOnDataUpdate();
    }
    CheckOutFragment checkOutFragment = (CheckOutFragment) mFm.findFragmentByTag(Constants.CHECK_OUT_TAG);
    if (checkOutFragment != null) {
      checkOutFragment.refreshOnDataUpdate();
    }
    FindBillFragment findBillFragment = (FindBillFragment) mFm.findFragmentByTag(Constants.FIND_BILL_TAG);
    if (findBillFragment != null) {
      findBillFragment.refreshOnDataUpdate();
    }
    //重新检测打印service
    checkPrinterIsConnectService(true);
  }

  /**
   * 初始化设置信息(默认模式)
   */
  //@formatter:off
    private void initSetInfo() {
      PxSetInfo p = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
      //初始化
      if (p == null) {
        PxSetInfo setInfo = new PxSetInfo();
        setInfo.setDelFlag("0");
        if (setInfo.getModel() == null) {
          setInfo.setModel(PxSetInfo.MODEL_DEFAULT);
        }
        if (setInfo.getOverAutoStartBill() == null) {
          setInfo.setOverAutoStartBill(PxSetInfo.OVER_AUTO_START_BILL_FALSE);
        }
        if (setInfo.getAutoOrder() == null) {
          setInfo.setAutoOrder(PxSetInfo.AUTO_ORDER_FALSE);
        }
        if (setInfo.getIsAutoTurnCheckout() == null) {
          setInfo.setIsAutoTurnCheckout(PxSetInfo.AUTO_TURN_CHECKOUT_FALSE);
        }
        if (setInfo.getIsFastOpenOrder() == null) {
          setInfo.setIsFastOpenOrder(PxSetInfo.FAST_START_ORDER_FALSE);
        }
        if (setInfo.getIsAutoPrintRechargeVoucher() == null) {
          setInfo.setIsAutoPrintRechargeVoucher(PxSetInfo.AUTO_PRINT_RECHARGE_TRUE);
        }
        if (setInfo.getIsFinancePrintCategory() == null) {
          setInfo.setIsFinancePrintCategory(PxSetInfo.FINANCE_PRINT_CATEGORY_FALSE);
        }
        DaoServiceUtil.getSetInfoService().saveOrUpdate(setInfo);
      }
      //添加新的
      else {
        if (p.getModel() == null) {
          p.setModel(PxSetInfo.MODEL_DEFAULT);
        }
        if (p.getOverAutoStartBill() == null) {
          p.setOverAutoStartBill(PxSetInfo.OVER_AUTO_START_BILL_FALSE);
        }
        if (p.getAutoOrder() == null) {
          p.setAutoOrder(PxSetInfo.AUTO_ORDER_FALSE);
        }
        if (p.getIsAutoTurnCheckout() == null) {
          p.setIsAutoTurnCheckout(PxSetInfo.AUTO_TURN_CHECKOUT_FALSE);
        }
        if (p.getIsFastOpenOrder() == null) {
          p.setIsFastOpenOrder(PxSetInfo.FAST_START_ORDER_FALSE);
        }
        if (p.getIsAutoPrintRechargeVoucher() == null) {
          p.setIsAutoPrintRechargeVoucher(PxSetInfo.AUTO_PRINT_RECHARGE_TRUE);
        }
        if (p.getIsFinancePrintCategory() == null) {
          p.setIsFinancePrintCategory(PxSetInfo.FINANCE_PRINT_CATEGORY_FALSE);
        }
        DaoServiceUtil.getSetInfoService().saveOrUpdate(p);
      }
    }

  /**
   * ChatLogin Success
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveChatLoginEvent(ChatLoginEvent event) {
    mIvMessageClick.setVisibility(View.GONE);
    XMPPTCPConnection connection = XMPPConnectUtils.getConnection();
    mXmppConnectionListener = new XmppConnectionListener();
    connection.addConnectionListener(mXmppConnectionListener);
  }

  /**
   * Xmpp 连接监听
   */
  class XmppConnectionListener implements ConnectionListener {
    @Override public void connected(XMPPConnection connection) {
      //更新网络状态图
      runOnUiThread(new Runnable() {
        @Override public void run() {
          mIvMessageClick.setVisibility(View.GONE);
        }
      });
    }

    @Override public void authenticated(XMPPConnection connection, boolean resumed) {

    }

    @Override public void connectionClosed() {
      //更新网络状态图
      runOnUiThread(new Runnable() {
        @Override public void run() {
          mIvMessageClick.setVisibility(View.VISIBLE);
        }
      });
    }

    @Override public void connectionClosedOnError(final Exception e) {
      //更新网络状态图
      runOnUiThread(new Runnable() {
        @Override public void run() {
          mIvMessageClick.setVisibility(View.VISIBLE);
        }
      });
    }

    @Override public void reconnectionSuccessful() {
      //更新网络状态图
      runOnUiThread(new Runnable() {
        @Override public void run() {
          mIvMessageClick.setVisibility(View.GONE);
        }
      });
    }

    @Override public void reconnectingIn(int seconds) {
    }

    @Override public void reconnectionFailed(final Exception e) {
      //更新网络状态图
      runOnUiThread(new Runnable() {
        @Override public void run() {
          mIvMessageClick.setVisibility(View.VISIBLE);
        }
      });
    }
  }

  /**
   * 错误按钮点击
   */
  @OnClick(R.id.iv_message_click) public void networkErrorClick() {
    mIvNetworkError.setVisibility(View.VISIBLE);
    mIvNetworkError.postDelayed(new Runnable() {
      @Override public void run() {
        mIvNetworkError.setVisibility(View.INVISIBLE);
      }
    }, 2000);
  }

  /**
   * 初始化Fragment，显示客单和点菜页面
   */
  private void initFragment(FragmentTransaction transaction) {
    //结账
    mCheckOutFragment = (CheckOutFragment) mFm.findFragmentByTag(Constants.CHECK_OUT_TAG);
    if (mCheckOutFragment == null){
      mCheckOutFragment = CheckOutFragment.newInstance("");
      transaction.add(R.id.cash_content_right,mCheckOutFragment,Constants.CHECK_OUT_TAG);
      transaction.show(mCheckOutFragment);
    }
    transaction.hide(mCheckOutFragment);
    transaction.commit();

    FragmentTransaction fragmentTransaction = mFm.beginTransaction();
    //账单
    mCashBillFragment = (CashBillFragment) mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
    if (mCashBillFragment == null) {
      mCashBillFragment = CashBillFragment.newInstance("");
      fragmentTransaction.add(R.id.cash_content_left, mCashBillFragment, Constants.CASH_BILL_TAG);
    } else {
      fragmentTransaction.show(mCashBillFragment);
    }
    //菜单
    mCashMenuFragment = (CashMenuFragment) mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    if (mCashMenuFragment == null) {
      mCashMenuFragment = CashMenuFragment.newInstance("");
      fragmentTransaction.add(R.id.cash_content_right, mCashMenuFragment, Constants.CASH_MENU_TAG);
    } else {
      fragmentTransaction.show(mCashMenuFragment);
    }
    fragmentTransaction.commit();
  }

  /**
   * 找单
   */
  @OnClick(R.id.ibtn_find_bill) public void findBill() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    //账单
    mCashBillFragment = (CashBillFragment) mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
    if (mCashBillFragment == null) {
      mCashBillFragment = CashBillFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mCashBillFragment, Constants.CASH_BILL_TAG);
    } else {
      transaction.show(mCashBillFragment);
    }
    //桌单
    mFindBillFragment = (FindBillFragment) mFm.findFragmentByTag(Constants.FIND_BILL_TAG);
    if (mFindBillFragment == null) {
      mFindBillFragment = FindBillFragment.newInstance(true);
      transaction.add(R.id.cash_content_right, mFindBillFragment, Constants.FIND_BILL_TAG);
    } else {
      //显示tab
      mCashFabs.setVisibility(View.VISIBLE);
      transaction.show(mFindBillFragment);
    }
    transaction.commit();
  }

  /**
   * 点菜
   */
  @OnClick(R.id.ibtn_take_order) public void takeOrder() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    //账单
    mCashBillFragment = (CashBillFragment) mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
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
    transaction.commit();
  }

  /**
   * 结账
   */
  @OnClick(R.id.ibtn_check_out) public void checkOut() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    //账单
    mCashBillFragment = (CashBillFragment) mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
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
    transaction.commit();
  }

  /**
   * 注销用户
   */
  @OnClick(R.id.rb_tab_admin) public void logOff(RadioButton rb) {
    if (rb.isChecked()) {
      //对话框
      new MaterialDialog.Builder(this).title("警告")
          .content("是否注销用户?")
          .positiveText("确认")
          .negativeText("取消")
          .negativeColor(getResources().getColor(R.color.primary_text))
          .onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override public void onClick(MaterialDialog dialog, DialogAction which) {
              openActivity(LoginActivity.class);
              finish();
            }
          })
          .canceledOnTouchOutside(true)
          .show();
    }
  }

  /**
   * 已结账单
   */
  @OnClick(R.id.rb_tab_over_bill) public void toOverBill() {
    Intent intent = new Intent(MainActivity.this, OverBillActivity.class);
    startActivity(intent);
  }

  /**
   * 数据更新
   */
  @OnClick(R.id.rb_tab_data_update) public void toDataUpdate() {
    Intent intent = new Intent(MainActivity.this, DataUpdateActivity.class);
    startActivity(intent);
  }

  /**
   * 会员中心
   */
  @OnClick(R.id.rb_tab_member_centre) public void toMemberCentre() {
    Intent intent = new Intent(MainActivity.this, MemberCentreActivity.class);
    startActivity(intent);
  }

  /**
   * 厨房打印
   */
  @OnClick(R.id.rb_tab_kitchen_print) public void toKitchenPrint() {
    Intent intent = new Intent(MainActivity.this, KitchenPrintActivity.class);
    startActivity(intent);
  }

  /**
   * 更多
   */
  @OnClick(R.id.rb_tab_more) public void toMore() {
    Intent intent = new Intent(MainActivity.this, MoreActivity.class);
    startActivity(intent);
  }

  /**
   * 交接班
   */
  @OnClick(R.id.rb_tab_shift_change) public void toShiftChange() {
    Intent intent = new Intent(MainActivity.this, ShiftChangeActivity.class);
    startActivity(intent);
  }

  /**
   * 预定管理
   */
  @OnClick(R.id.rb_tab_reserve_manager) public void toReserveManager() {
    Intent intent = new Intent(MainActivity.this, ReserveManagerActivity.class);
    startActivity(intent);
  }

  /**
   * 消息列表
   */
  @OnClick(R.id.rb_tab_messages) public void toMessages() {
    Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
    startActivity(intent);
  }


  /**
   * 隐藏所有Fragment
   */
  private void hideAllFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        transaction.hide(fragment);
      }
    }
  }

  ///**
  // * 隐藏所有Fragment
  // */
  //private void hideAllFragment(FragmentTransaction transaction) {
  //  hideFragment(transaction,mCashBillFragment);
  //  hideFragment(transaction,mCashMenuFragment);
  //  hideFragment(transaction,mStartBillFragment);
  //  hideFragment(transaction,mModifyBillFragment);
  //  hideFragment(transaction,mCheckOutFragment);
  //  hideFragment(transaction,mFindBillFragment);
  //  hideFragment(transaction,mFuzzyQueryFragment);
  //  transaction.commit();
  //
  //}
  //
  //private void hideFragment(FragmentTransaction transaction,Fragment fragment) {
  //  if (fragment != null){
  //    transaction.hide(fragment);
  //  }
  //}

  /**
   * 退出
   */
  @Override protected void onDestroy() {
    super.onDestroy();
    //关闭ChatLoginService
    stopService(new Intent(MainActivity.this, ChatLoginService.class));
    //关闭Smack Service
    stopService(new Intent(MainActivity.this, ChatSmackService.class));
    //关闭上传订单service
    stopService(new Intent(MainActivity.this, UpLoadDataService.class));
    //关闭ping service
    stopService(new Intent(MainActivity.this, PingService.class));
    //关闭connect service
    stopService(new Intent(MainActivity.this, ConnectService.class));
    //关闭 检测打印机连接状态服务
    checkPrinterIsConnectService(false);
    //BTPrintService
    if (mBtStateReceiver != null) {
      unregisterReceiver(mBtStateReceiver);
    }
    stopService(new Intent(MainActivity.this, BTPrintService.class));

    //Smack断开连接
    if (XMPPConnectUtils.getConnection() != null) {
      XMPPTCPConnection connection = XMPPConnectUtils.getConnection();
      if (mXmppConnectionListener != null) {
        connection.removeConnectionListener(mXmppConnectionListener);
        mXmppConnectionListener = null;
      }
      if (connection.isConnected()) {
        connection.disconnect();
        connection.instantShutdown();
        connection = null;
      }
    }
    //反注册EventBus
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
    //客显收款
    mDisplayBuffer = new byte[1];
    mDisplayBuffer[0] = 0x33;
    if (mSerialPort != null||mSerialPortOperaionHdx!=null) {
      DisplayData(String.valueOf("0.0"), mDisplayBuffer);
    }
    //TTS
    if (mSpeechSynthesizer != null) {
      try {
        mSpeechSynthesizer.release();
      }catch (Throwable e){

      }
    }
  }

  /**
   * 显示对话框
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void showProgress(ShowProgressEvent event) {
    performShowProgress(true, event.getProdName());
  }

  /**
   * 关闭对话框
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void hideProgress(HideProgressEvent event) {
    performShowProgress(false, null);
  }

  /**
   * 蒙层 是否显示
   */
  public void isShowProgress(boolean isShow) {
    isShowProgress(isShow, null);
  }
  /**
   * 蒙层状态
   * @return
   */
  public boolean isShowingProgress(){
    return mRlContent.isIntercept();
  }

  /**
   * 蒙层 是否显示
   */
  public void isShowProgress(final boolean isShow, final String productName) {
    //线程切换
    if (Looper.myLooper() == Looper.getMainLooper()) {
      performShowProgress(isShow, productName);
    } else {
      runOnUiThread(new Runnable() {
        @Override public void run() {
          performShowProgress(isShow, productName);
        }
      });
    }
  }

  /**
   * 执行 蒙层
   */
  private void performShowProgress(boolean isShow, String productName) {
    if (mRlContent.isIntercept() == isShow) return;
    mRlContent.setIntercept(isShow);
    mRlProgress.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    mTvProgressName.setText(productName);
  }

  /**
   * 打印机是否全部连接,更改tab状态
   */
  //@formatter:off
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void receivePrinterPingResult(PrinterPingErrorEvent event) {
    boolean allConnect = event.isAllConnect();
    mRbTabKitchenPrint.setCompoundDrawablesWithIntrinsicBounds(null, getKitchenDrawable(allConnect), null, null);
  }

  /**
   * 连接状态 选择图片
   */
  private Pair<Drawable,Drawable> mDrawablePair;//软引用 缓存图片
  private Drawable getKitchenDrawable(boolean isConnect){
    if (mDrawablePair == null) {
      Drawable selectorError = getResources().getDrawable(R.drawable.selector_tab_kitchenprint_error);
      Drawable selectorOk = getResources().getDrawable(R.drawable.selector_tab_print_manager);
      mDrawablePair = new Pair<>(selectorOk,selectorError);
    }
    return isConnect ? mDrawablePair.first : mDrawablePair.second;
  }
  /**
   * 开启或关闭 检测打印机连接服务
   */
  private void checkPrinterIsConnectService(boolean isOpen) {
    List<PxPrinterInfo> enablePrinterList = DaoServiceUtil.getPrinterInfoService()
        .queryBuilder()
        .where(PxPrinterInfoDao.Properties.DelFlag.eq("0"))
        .where(PxPrinterInfoDao.Properties.Status.eq(PxPrinterInfo.ENABLE))
        .list();

    //是否开启标签打印机
    boolean isOpenLabel = (boolean) SPUtils.get(MainActivity.this, Constants.SWITCH_LABEL_PRINT, false);
    if (isOpenLabel) {
      App app = (App) App.getContext();
      app.setOpenLabel(true);
    }
    //没有可用打印机 不开启
    boolean isOpenEsc = enablePrinterList.size() > 0;
    if (!isOpenEsc && !isOpenLabel) return;//没有启用的普通打印机 也没有标签打印机
    if (isOpen) {
      //重新初始化打印机
      PrintTaskManager.initPrinterList();
      //开启检测打印机 连接服务
      if (isOpenEsc) {
        startService(new Intent(this, PrinterService.class));
      }
      startService(new Intent(this, PrintQueueService.class));
    } else {
      if (isOpenEsc) {
        stopService(new Intent(this, PrinterService.class));
      }
      stopService(new Intent(this, PrintQueueService.class));
    }
  }

  /**
   * 屏蔽返回键,
   */
  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  /**
   * Activity截获按键事件.发给ScanGunKeyEventUtils 监听CheckOutFragment条码输入事件
   */
  //@formatter:on
  @Override public boolean dispatchKeyEvent(KeyEvent event) {
    //输入事件数字 交由 扫码框处理
    if ((mCheckOutFragment != null && !mCheckOutFragment.isHidden())) {
      mCheckOutFragment.onKeyDown();
    } else if (mScanGunKeyEventUtils.isScanGunEvent(event)
        && !mCashMenuFragment.isHidden()) { //如果是点菜页面,拦截分发事件
      mScanGunKeyEventUtils.analysisKeyEvent(event);
      return true;
    }
    return super.dispatchKeyEvent(event);
  }

  @Override public void onScanSuccess(String barcode) {
    if (barcode != null && barcode.length() != 0) {
      mCashMenuFragment.searchProdByCode(barcode);
    }
  }

  //@Subscribe(threadMode = ThreadMode.MAIN) public void showProgress(SwipingVipCardEvent event) {
  //  mCanSwiping =  event.isCanSwiping();
  //  Logger.v(mCanSwiping+"");
  //}
}