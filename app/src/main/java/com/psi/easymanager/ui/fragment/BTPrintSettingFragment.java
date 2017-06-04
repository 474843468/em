package com.psi.easymanager.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.BTDevicesAdapter;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.BTPrintDeviceDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.SwitchBTDeviceEvent;
import com.psi.easymanager.module.BTPrintDevice;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.service.BTPrintService;
import com.psi.easymanager.ui.activity.MoreActivity;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ServiceWorkUtil;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.DetachableClickListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: ylw
 * Date: 2017-02-23
 * Time: 16:26
 * BTPrint
 */
public class BTPrintSettingFragment extends BaseFragment {

  @Bind(R.id.rcv_pair_devices) RecyclerView mRcvPairDevices;
  @Bind(R.id.rcv_new_devices) RecyclerView mRcvNewDevices;
  @Bind(R.id.ll_pair_devices) LinearLayout mLlPairDevices;
  @Bind(R.id.ll_new_devices) LinearLayout mLlNewDevices;

  private MoreActivity mAct;
  private List<BluetoothDevice> mPairDevicesList;
  private List<BluetoothDevice> mNewDevicesList;
  private BTDevicesAdapter mPairDevicesAdapter;
  private BTDevicesAdapter mNewDevicesAdapter;

  private BluetoothAdapter mBluetoothAdapter;
  private MaterialDialog mScanDialog;//scan Dialog

  public static BTPrintSettingFragment newInstance() {
    Bundle args = new Bundle();
    BTPrintSettingFragment fragment = new BTPrintSettingFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_bt_setting, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mAct = (MoreActivity) getActivity();
    //init
    initDevice();
    //initView
    initView();
  }

  /**
   * init device
   */
  private void initDevice() {
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null) {
      ToastUtils.showShort(mAct, "该设备不支持蓝牙!");
      return;
    }

    //receiver
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    filter.addAction(BluetoothDevice.ACTION_FOUND);
    filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
    filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    // 注册广播接收器，接收并处理搜索结果
    mAct.registerReceiver(mReceiver, filter);
  }

  /**
   * init View
   */
  private void initView() {
    mPairDevicesList = new ArrayList();
    mRcvPairDevices.setLayoutManager(new LinearLayoutManager(mAct));
    mRcvPairDevices.setHasFixedSize(true);
    mPairDevicesAdapter = new BTDevicesAdapter(mAct);
    mRcvPairDevices.setAdapter(mPairDevicesAdapter);
    mPairDevicesAdapter.setItemClickListener(new PairDeviceItemClickListener());

    mNewDevicesList = new ArrayList();
    mRcvNewDevices.setLayoutManager(new LinearLayoutManager(mAct));
    mRcvNewDevices.setHasFixedSize(true);
    mNewDevicesAdapter = new BTDevicesAdapter(mAct);
    mRcvNewDevices.setAdapter(mNewDevicesAdapter);
    mNewDevicesAdapter.setItemClickListener(new NewDeviceItemClickListener());

    if (mBluetoothAdapter == null) return;
    //已绑定设备
    getBondedDevices();
  }

  /**
   * 开始扫描蓝牙设备
   */
  @OnClick(R.id.btn_start_scan_bt_devices) public void scanBTDevices() {
    if (mBluetoothAdapter == null) {
      ToastUtils.showShort(mAct, "该设备不支持蓝牙!");
      return;
    }
    //打开蓝牙
    if (!mBluetoothAdapter.isEnabled()) {
      Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableIntent, mAct.OPEN_BLUETOOTH_REQUEST);
      return;
    }
    //已经扫描 取消重新扫
    if (mBluetoothAdapter.isDiscovering()) {
      mBluetoothAdapter.cancelDiscovery();
    }

    //scan Dialog
    mScanDialog = DialogUtils.showDialog(mAct, "蓝牙打印", "正在扫描...", "停止扫描");
    MDButton posBtn = mScanDialog.getActionButton(DialogAction.POSITIVE);

    DetachableClickListener posListener = DetachableClickListener.wrap(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //已经扫描 取消重新扫
        if (mBluetoothAdapter.isDiscovering()) {
          mBluetoothAdapter.cancelDiscovery();
        }
        DialogUtils.dismissDialog(mScanDialog);
      }
    });
    posListener.clearOnDetach(mScanDialog);
    posBtn.setOnClickListener(posListener);
    mBluetoothAdapter.startDiscovery();
    mNewDevicesList = new ArrayList<>();
    mNewDevicesAdapter.setData(mNewDevicesList);
  }

  /**
   * 新设备 Item Click
   */
  class NewDeviceItemClickListener implements BTDevicesAdapter.ItemClickListener {
    @Override public void clickItem(int pos) {
      BluetoothDevice device = mNewDevicesList.get(pos);
      int deviceType = device.getBluetoothClass().getMajorDeviceClass();
      if (!(deviceType == BluetoothClass.Device.Major.IMAGING || deviceType == BluetoothClass.Device.Major.UNCATEGORIZED)) { //不是打印机
        ToastUtils.showShort(null, "只支持蓝牙打印机配对!");
        return;
      }
      boolean result = createBound(device);
      if (!result) {
        ToastUtils.showShort(null, "配对失败");
      } else {
        getBondedDevices();
      }
    }

    @Override public void longClick(int pos) {
    }

    /**
     * 配对
     */
    private boolean createBound(BluetoothDevice device) {
      boolean result = false;
      try {
        // 配对
        Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
        result = (boolean) createBondMethod.invoke(device);
      } catch (Exception e) {
        Logger.e(e.toString());
        result = false;
      }
      return result;
    }
  }

  /**
   * 已配对设备的Item click
   */
  class PairDeviceItemClickListener implements BTDevicesAdapter.ItemClickListener {

    @Override public void clickItem(final int pos) {
      final BluetoothDevice device = mPairDevicesList.get(pos);
      int deviceType = device.getBluetoothClass().getMajorDeviceClass();
      if (!(deviceType == BluetoothClass.Device.Major.IMAGING || deviceType == BluetoothClass.Device.Major.UNCATEGORIZED)) { //不是打印机
        ToastUtils.showShort(null, "只支持蓝牙打印机配对!");
        return;
      }

      if (Constants.BT_INNER_PRINTER.endsWith(device.getName().trim())) {
        ToastUtils.showShort(null, "内置打印机默认打印");
        return;
      }
      BTPrintDevice dbDevice = DaoServiceUtil.getBTDeviceService()
          .queryBuilder()
          .where(BTPrintDeviceDao.Properties.Address.eq(device.getAddress()))
          .unique();
      if (dbDevice != null) {
        ToastUtils.showShort(mAct, "已配置为收银打印机");
        return;
      }
      //加入收银打印机队列
      final MaterialDialog dialog = DialogUtils.showAddBTDeviceDialog(mAct);
      MDButton negBtn = dialog.getActionButton(DialogAction.NEGATIVE);
      MDButton posBtn = dialog.getActionButton(DialogAction.POSITIVE);

      DetachableClickListener negListener = DetachableClickListener.wrap(new View.OnClickListener() {
            @Override public void onClick(View v) {
              DialogUtils.dismissDialog(dialog);
            }
          });

      DetachableClickListener posListener =
          DetachableClickListener.wrap(new View.OnClickListener() {
            @Override public void onClick(View v) {
              int selectedIndex = dialog.getSelectedIndex();
              String format =
                  selectedIndex == 0 ? BTPrintDevice.FORMAT_58 : BTPrintDevice.FORMAT_80;
              BTPrintDevice printDevice = new BTPrintDevice();
              printDevice.setAddress(device.getAddress());
              printDevice.setFormat(format);
              DaoServiceUtil.getBTDeviceService().save(printDevice);
              //BT打印服务
              String serviceName = mAct.getPackageName() + ".service.BTPrintService";
              if (ServiceWorkUtil.isServiceWork(mAct, serviceName)) {
                //notify BTPrintService
                PrintEventManager.getManager()
                    .postSwitchEvent(new SwitchBTDeviceEvent(true, printDevice));
              } else {
                mAct.startService(new Intent(mAct, BTPrintService.class));
              }
              mPairDevicesAdapter.notifyItemChanged(pos);
              DialogUtils.dismissDialog(dialog);
            }
          });

      negListener.clearOnDetach(dialog);
      posListener.clearOnDetach(dialog);
      negBtn.setOnClickListener(negListener);
      posBtn.setOnClickListener(posListener);
    }

    @Override public void longClick(int pos) {
      BluetoothDevice device = mPairDevicesList.get(pos);
      int deviceType = device.getBluetoothClass().getMajorDeviceClass();
      if (!(deviceType == BluetoothClass.Device.Major.IMAGING || deviceType == BluetoothClass.Device.Major.UNCATEGORIZED)) { //不是打印机
        ToastUtils.showShort(null, "只支持蓝牙打印机配对!");
        return;
      }
      boolean removeBound = removeBound(device);
      if (!removeBound) {
        ToastUtils.showShort(null, "取消配对失败!");
      } else {
        getBondedDevices();
        //delete db
        BTPrintDevice dbDevice = DaoServiceUtil.getBTDeviceService()
            .queryBuilder()
            .where(BTPrintDeviceDao.Properties.Address.eq(device.getAddress()))
            .unique();
        if (dbDevice != null) {
          DaoServiceUtil.getBTDeviceService().delete(dbDevice);
          //notify BTPrintService
          PrintEventManager.getManager().postSwitchEvent(new SwitchBTDeviceEvent(false, dbDevice));
        }
      }
    }

    /**
     * 取消配对
     */
    private boolean removeBound(BluetoothDevice device) {
      boolean result = false;
      try {
        Method m = BluetoothDevice.class.getMethod("removeBond", (Class[]) null);
        result = (boolean) m.invoke(device, (Object[]) null);
      } catch (Exception e) {
        result = false;
        Logger.e(e.toString());
      }
      return result;
    }
  }

  /**
   * discover device
   */
  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      Logger.w(action);
      // 获得已经搜索到的蓝牙设备
      if (action.equals(BluetoothDevice.ACTION_FOUND)) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        // 搜索到的不是已经绑定的蓝牙设备
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
          // 防止重复添加
          if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            if (!mNewDevicesList.contains(device)) {
              mNewDevicesList.add(device);
              mNewDevicesAdapter.setData(mNewDevicesList);
              //mNewDevicesAdapter.notifyDataSetChanged();
            }
          }
        }
        // 搜索完成
      } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
        DialogUtils.dismissDialog(mScanDialog);
        ToastUtils.showShort(context, "搜索结束" + (mNewDevicesList.isEmpty() ? "没有发现可匹配的设备!" : "!"));
      } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
        // 状态改变的广播
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String name = device.getName();
        if (device.getName().equalsIgnoreCase(name)) {
          int connectState = device.getBondState();
          switch (connectState) {
            case BluetoothDevice.BOND_NONE:  //10 //TODO 通知 BTPrintService
              ToastUtils.showShort(null, "取消配对：" + device.getName());
              if (mPairDevicesList.contains(device)) {
                mPairDevicesList.remove(device);
                mPairDevicesAdapter.setData(mPairDevicesList);
                //mPairDevicesAdapter.notifyDataSetChanged();
              }
              break;
            case BluetoothDevice.BOND_BONDING:  //11
              ToastUtils.showShort(null, "正在配对：" + device.getName());
              break;
            case BluetoothDevice.BOND_BONDED:   //12  //TODO 通知 BTPrintService
              ToastUtils.showShort(null, "完成配对：" + device.getName());
              getBondedDevices();
              if (mNewDevicesList.contains(device)) {
                mNewDevicesList.remove(device);
                mNewDevicesAdapter.setData(mNewDevicesList);
                //mNewDevicesAdapter.notifyDataSetChanged();
              }
              break;
          }
        }
      }
    }
  };

  /**
   * 获取所有已绑定的设备
   */
  private void getBondedDevices() {
    //clear
    //if (!mPairDevicesList.isEmpty()) {
    //  mPairDevicesList.clear();
    //  mPairDevicesAdapter.setData(mPairDevicesList);
    //  //mPairDevicesAdapter.notifyDataSetChanged();
    //}

    //Get a set of currently paired devices
    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    // If there are paired devices, add each one to the ArrayAdapter
    if (pairedDevices != null && !pairedDevices.isEmpty()) {
      mPairDevicesList = new ArrayList<>(pairedDevices);
    }
    mPairDevicesAdapter.setData(mPairDevicesList);
  }

  @Override public void onResume() {
    super.onResume();
    getBondedDevices();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
    if (mReceiver != null) {
      mAct.unregisterReceiver(mReceiver);
    }
    if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
      mBluetoothAdapter.cancelDiscovery();
    }
  }
}