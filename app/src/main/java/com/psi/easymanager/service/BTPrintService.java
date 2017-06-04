package com.psi.easymanager.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.SwitchBTDeviceEvent;
import com.psi.easymanager.module.BTPrintDevice;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.bt.BTPrintTask;
import com.psi.easymanager.print.bt.event.IBTPrintEvent;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.print.constant.EpsonPosPrinterCommand;
import com.psi.easymanager.print.net.PutTask;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 蓝牙打印服务
 */
public class BTPrintService extends Service implements IBTPrintEvent {
  // Unique UUID for this application
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FA");
  private static final UUID OTHER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private ConcurrentHashMap<String, ExecutorService> mPrintMap = null;
  private ConcurrentHashMap<String, ExecutorService> mPutMap = null;
  private ConcurrentHashMap<String, LinkedBlockingQueue> mQueueMap = null;
  private ConcurrentHashMap<String, PrintRun> mPrintRunnableMap = null;

  private BluetoothAdapter mBTAdapter;
  private Vector<String> mOpenCashBoxVector = null;//开钱箱任务队列
  private boolean isDestroy;
  private BTBroadcastReceiver mBroadcastReceiver;
  //private LinkedBlockingQueue<BTPrintTask> mQueue;

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    isDestroy = false;
    mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    final List<BTPrintDevice> dbDeviceList = DaoServiceUtil.getBTDeviceService().queryAll();

    //Get a set of currently paired devices
    final Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
    // If there are paired devices, add each one to the ArrayAdapter
    if (pairedDevices == null || pairedDevices.isEmpty()) {
      stopSelf();
      if (!dbDeviceList.isEmpty()) {
        ToastUtils.showShort(null, "所有蓝牙打印机都已取消配对");
      }
      return;
    }

    //
    PrintEventManager.getManager().registBTPrintEvent(this);
    registReceiver();

    mOpenCashBoxVector = new Vector<>();
    mPrintMap = new ConcurrentHashMap<>();
    mPutMap = new ConcurrentHashMap<>();
    mQueueMap = new ConcurrentHashMap<>();
    mPrintRunnableMap = new ConcurrentHashMap<>();
    ExecutorService startService = Executors.newSingleThreadExecutor();
    startService.execute(new Runnable() {
      @Override public void run() {
        for (BluetoothDevice bt : pairedDevices) {
          //HDX065 内置打印机
          if (Constants.BT_INNER_PRINTER.equals(bt.getName().trim())) {
            String address = bt.getAddress();
            ExecutorService executor = getPrintThread(address);
            Runnable runnable = getPrintRun(bt, false);
            mPutMap.put(address, Executors.newSingleThreadExecutor());
            executor.execute(runnable);
            break;
          }
        }
        //数据库添加的
        for (BTPrintDevice device : dbDeviceList) {
          String address = device.getAddress();
          BluetoothDevice bluetoothDevice = isPair(address, pairedDevices);
          boolean is58 = BTPrintDevice.FORMAT_58.equals(device.getFormat());
          if (bluetoothDevice != null) {
            ExecutorService executor = getPrintThread(address);
            Runnable runnable = getPrintRun(bluetoothDevice, is58);
            getPutThread(address);
            executor.execute(runnable);
          } else {
            ToastUtils.showShortAsync(
                bluetoothDevice.getName() + "(" + bluetoothDevice.getAddress() + ")" + " 已被取消配对");
            DaoServiceUtil.getBTDeviceService().delete(device);
          }
        }
      }
    });
  }

  /**
   * 蓝牙是否在配对中
   */
  private BluetoothDevice isPair(String address, Set<BluetoothDevice> pairedDevices) {
    for (BluetoothDevice device : pairedDevices) {
      if (address.equals(device.getAddress())) return device;
    }
    return null;
  }

  private void registReceiver() {
    //receiver
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
    filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
    filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    // 注册广播接收器，接收并处理搜索结果
    mBroadcastReceiver = new BTBroadcastReceiver();
    registerReceiver(mBroadcastReceiver, filter);
  }

  private synchronized ExecutorService getPrintThread(String address) {
    ExecutorService service = mPrintMap.get(address);
    if (service == null) {
      service = Executors.newSingleThreadExecutor();
      mPrintMap.put(address, service);
    }
    return service;
  }

  private synchronized ExecutorService getPutThread(String address) {
    ExecutorService service = mPutMap.get(address);
    if (service == null) {
      service = Executors.newSingleThreadExecutor();
      mPutMap.put(address, service);
    }
    return service;
  }

  private synchronized LinkedBlockingQueue<BTPrintTask> getQueue(String address) {
    LinkedBlockingQueue<BTPrintTask> queue = mQueueMap.get(address);
    if (queue == null) {
      queue = new LinkedBlockingQueue<>(10);
      mQueueMap.put(address, queue);
    }
    return queue;
  }

  private synchronized Runnable getPrintRun(BluetoothDevice device, boolean is58) {
    String address = device.getAddress();
    LinkedBlockingQueue<BTPrintTask> queue = getQueue(address);
    PrintRun runnable = mPrintRunnableMap.get(address);
    if (runnable == null) {
      runnable = new PrintRun(queue, device, is58);
      mPrintRunnableMap.put(address, runnable);
    }
    return runnable;
  }

  /**
   * 打印 Runnable
   */
  //@formatter:on
  class PrintRun implements Runnable {
    private LinkedBlockingQueue<BTPrintTask> mQueue;
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private OutputStream mOs;
    private String mAddress;
    private boolean mStop = false;
    private boolean mIs58 = false;

    public void setStop(boolean stop) {
      mStop = stop;
      IOUtils.closeCloseables(mSocket, mOs);
    }

    public PrintRun(LinkedBlockingQueue<BTPrintTask> queue, BluetoothDevice device, boolean is58) {
      this.mQueue = queue;
      this.mDevice = device;
      this.mAddress = device.getAddress();
      this.mIs58 = is58;
      initOs();
    }

    private void initOs() {
      Pair<BluetoothSocket, OutputStream> pair = connectDevice(mDevice);
      this.mSocket = pair.first;
      this.mOs = pair.second;
    }

    //outputStream.write(EpsonPosPrinterCommand.OPEN_BOX1); 钱箱
    private BTPrintTask mTask;

    @Override public void run() {
      while (!isDestroy && !mStop) {
        try {
          if (mOs == null) {
            Thread.sleep(1000);
            initOs();
          } else {
            //优先开钱箱
            if (mOpenCashBoxVector != null && mOpenCashBoxVector.contains(mAddress)) {
              mOs.write(EpsonPosPrinterCommand.OPEN_BOX1);
              //开成功后 移除
              mOpenCashBoxVector.remove(mAddress);
              Log.w("Fire", mAddress + "开钱箱成功!");
            } else {
              mTask = mQueue.take();
              if (mTask.getMode() != BTPrintConstants.PRINT_MODE_FALSE_DATA) {//非假数据
                mTask.setIs58(mIs58);
                mTask.run(mOs);
              }
              mTask = null;
            }
          }
        } catch (IOException e) {
          Logger.e(e.toString());
          repeatTask();
          initOs();
        } catch (Exception e1) {
          Logger.e(e1.toString());
        }
      }
    }

    private void repeatTask() {
      if (!isDestroy || mTask != null || !mStop) {
        ExecutorService putThread = getPutThread(mAddress);
        putThread.execute(new PutTask(mTask, mQueue));
        IOUtils.closeCloseables(mSocket, mOs);
      }
    }
  }

  //@formatter:on

  /**
   * 获取蓝牙打印OutputStream
   */
  private Pair<BluetoothSocket, OutputStream> connectDevice(BluetoothDevice device) {
    BluetoothSocket socket = null;
    OutputStream os = null;
    try {
      socket = device.createRfcommSocketToServiceRecord(OTHER_UUID);
      socket.connect();
      os = socket.getOutputStream();
    } catch (IOException e) {
      Logger.e(e.toString());
      IOUtils.closeCloseables(socket, os);
    }
    return new Pair<>(socket, os);
  }

  /**
   * 收银接收蓝牙打印任务
   */
  @Override public void receiveBTPrintTask(BTPrintTask task) {
    if (isDestroy || task == null || mPrintRunnableMap == null) return;
    Set<Map.Entry<String, PrintRun>> entrySet = mPrintRunnableMap.entrySet();
    for (Map.Entry<String, PrintRun> entry : entrySet) {
      String address = entry.getKey();
      //收银任务 但是打印机停止打印收银任务
      ExecutorService putThread = getPutThread(address);
      LinkedBlockingQueue<BTPrintTask> queue = getQueue(address);
      putThread.execute(new PutTask(task, queue));
    }
  }

  /**
   * 蓝牙打印机开启任务
   */
  @Override public void receiveSwitchBTDevice(SwitchBTDeviceEvent event) {
    if (isDestroy) return;
    //添加打印队列中
    BTPrintDevice device = event.getDevice();
    String address = device.getAddress();
    Logger.w(event.toString());
    if (event.isSwitch()) {//open
      if (mPutMap.containsKey(address)) return;
      boolean is58 = BTPrintDevice.FORMAT_58.equals(device.getFormat());
      Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
      for (BluetoothDevice pairedDevice : pairedDevices) {
        if (pairedDevice.getAddress().equals(address)) {
          ExecutorService executor = getPrintThread(address);
          Runnable runnable = getPrintRun(pairedDevice, is58);
          getPutThread(address);
          executor.execute(runnable);
          break;
        }
        //TODO

      }
    } else {//close
      if (!mPutMap.containsKey(address)) return;
      closeDevice(address);
    }
  }

  /**
   * 开钱箱
   */
  @Override public void receiveOpenCashBox() {
    if (isDestroy || mPrintRunnableMap == null) return;
    Set<Map.Entry<String, PrintRun>> entrySet = mPrintRunnableMap.entrySet();
    for (Map.Entry<String, PrintRun> entry : entrySet) {
      String address = entry.getKey();
      //开钱箱
      mOpenCashBoxVector.add(address);
      //当前 可能阻塞在取打印任务 ，加空任务 跳一下
      LinkedBlockingQueue<BTPrintTask> queue = getQueue(address);
      if (queue != null && queue.size() == 0) {
        try {
          queue.put(new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_FALSE_DATA).build());
        } catch (InterruptedException e) {
          Logger.e(e.toString());
        }
      }
    }
  }

  //@formatter:on
  private void closeDevice(String address) {
    //1.stop put
    ExecutorService putService = mPutMap.get(address);
    if (putService != null) {
      mPutMap.remove(address);
      closeExecutor(putService);
    }

    //3.stop print runnable
    PrintRun runnable = mPrintRunnableMap.get(address);
    if (runnable != null) {
      runnable.setStop(true);
      mPrintRunnableMap.remove(address);
    }

    //2.clear queue
    LinkedBlockingQueue<BTPrintTask> queue = mQueueMap.get(address);
    if (queue != null) {
      if (queue.isEmpty()) {//假数据
        try {
          queue.put(new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_FALSE_DATA).build());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      mQueueMap.remove(address);
    }

    //4.stop print
    ExecutorService printService = mPrintMap.get(address);
    if (printService != null) {
      mPrintMap.remove(address);
      closeExecutor(printService);
    }
    //
    if (mPrintRunnableMap.isEmpty()) {
      stopSelf();
    }
  }

  //@formatter:off

  @Override public void onDestroy() {
    super.onDestroy();
    PrintEventManager.getManager().unRegistBTPrintEvent(this);
    if (mBroadcastReceiver!= null) {
      unregisterReceiver(mBroadcastReceiver);
    }
    isDestroy = true;
    //1.put map
    if (mPutMap != null) {
      for (Map.Entry<String, ExecutorService> entry : mPutMap.entrySet()) {
        String address = entry.getKey();
        ExecutorService service = mPutMap.get(address);
        closeExecutor(service);
      }
    }
    //2.stop print runnable
    if (mPrintRunnableMap!= null) {
      for (Map.Entry<String,PrintRun> entry : mPrintRunnableMap.entrySet()) {
        String address = entry.getKey();
        PrintRun printRun = mPrintRunnableMap.get(address);
        printRun.setStop(true);
      }
    }
    //3.stop print
    if (mPrintMap != null) {
      for (Map.Entry<String, ExecutorService> entry : mPrintMap.entrySet()) {
        String address = entry.getKey();
        ExecutorService service = mPrintMap.get(address);
        closeExecutor(service);
      }
    }
      //4.cleat queue
    if (mQueueMap != null) {
      for (Map.Entry<String, LinkedBlockingQueue> entry : mQueueMap.entrySet()) {
        String address = entry.getKey();
        LinkedBlockingQueue queue = mQueueMap.get(address);
        queue.clear();
        queue = null;
      }
    }
  }

  //close ExecutorService
  private void closeExecutor(ExecutorService executor) {
    if (executor != null) {
      executor.shutdownNow();
      executor = null;
    }
  }

  /**
   * bound state changed
   */
  //@formatter:on
  private class BTBroadcastReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
      if (device == null) return;
      String name = device.getName();
      if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
        // 状态改变的广播
        int connectState = device.getBondState();
        switch (connectState) {
          case BluetoothDevice.BOND_NONE:  //10
            ToastUtils.showShort(null, "取消配对：" + name);
            break;
          case BluetoothDevice.BOND_BONDING:  //11
            ToastUtils.showShort(null, "正在配对：" + name);
            break;
          case BluetoothDevice.BOND_BONDED:   //12
            ToastUtils.showShort(null, "完成配对：" + name);
            break;
        }
      } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) { //连接
        ToastUtils.showShort(null, name + "蓝牙-连接成功");
        Log.d("Fire", name + " ACTION_ACL_CONNECTED");
      } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) { //断开连接
        Log.d("Fire", name + " ACTION_ACL_DISCONNECTED");
        ToastUtils.showShort(null, name + "蓝牙-断开连接");
      }
      //else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) { //蓝牙关闭
      //  int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
      //  switch (state) {
      //    case BluetoothAdapter.STATE_OFF:
      //      ToastUtils.showShort(context, "蓝牙已关闭，无法使用蓝牙打印!");
      //      stopSelf();
      //      break;
      //    case BluetoothAdapter.STATE_TURNING_OFF:
      //      ToastUtils.showShort(context, "蓝牙正在关闭，无法使用蓝牙打印!");
      //      break;
      //    case BluetoothAdapter.STATE_ON:
      //      ToastUtils.showShort(context, "蓝牙开启!");
      //      startService(new Intent(context, BTPrintService.class));
      //      break;
      //    case BluetoothAdapter.STATE_TURNING_ON:
      //      ToastUtils.showShort(context, "蓝牙正在开启!");
      //      break;
      //  }
      //}
    }
  }

  ;
}
