package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.kyleduo.switchbutton.SwitchButton;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.print.usb.AppGpService;
import com.psi.easymanager.print.usb.AppUsbDeviceName;
import com.psi.easymanager.ui.activity.MoreActivity;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zjq on 2016/3/17.
 * 更多-配置普通打印机
 */
public class OrdinaryPrinterFragment extends BaseFragment {
    private static final String ORDINARY_PRINTER_FRAGMENT_PARAM = "param";
    @Bind(R.id.sb_ordinary_printer)
    SwitchButton mOrdinaryPrinter;
    private String mParam;//MainActivity参数
    private MoreActivity mAct;

    private AppGpService mAppGpService;//GP服务
    private String mDeviceName;//USB设备名

    public static OrdinaryPrinterFragment newInstance(String mParam) {
        Bundle bundle = new Bundle();
        bundle.putString("param", mParam);
        OrdinaryPrinterFragment fragment = new OrdinaryPrinterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ORDINARY_PRINTER_FRAGMENT_PARAM);
        }
        mAct = (MoreActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordinary_printer, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        EventBus.getDefault().getStickyEvent(AppGpService.class);
        EventBus.getDefault().getStickyEvent(AppUsbDeviceName.class);
        //连接SB状态
        initViewStatus();
    }

    /**
     * 初始化控件显示状态
     */
    private void initViewStatus() {
        boolean sbOrdinaryPrint = (boolean) SPUtils.get(App.getContext(), Constants.SWITCH_ORDINARY_PRINT, true);
        mOrdinaryPrinter.setChecked(sbOrdinaryPrint);
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
     * 接受由MainActivity发送的GpService
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onGpServiceEvent(AppGpService appGpService) {
        //ptksai pos 不支持USB打印
        String  isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
        if (isSupportUSBPrint.equals("1")) return;

        if(appGpService == null){
            ToastUtils.showShort(App.getContext(),"服务为空");
            return;
        }else {
            mAppGpService = appGpService;
        }
    }


    /**
     * 普通打印
     */
    @OnCheckedChanged(R.id.sb_ordinary_printer)
    public void ordinaryPrintSwitch(SwitchButton sb) {
        try {
            mAppGpService.getGpService().closePort(1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (sb.isChecked()) {
            SPUtils.put(App.getContext(), Constants.SWITCH_ORDINARY_PRINT, true);
            //检测USB并打开端口（参数说明：参数一:连接打印机索引ID参数二:连接类型参数三:设备地址,USB设备地址参数四:默认端口一般设置为0）
            try {
                mAppGpService.getGpService().openPort(1, PortParameters.USB,mDeviceName,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            SPUtils.put(App.getContext(), Constants.SWITCH_ORDINARY_PRINT, false);
            //关闭端口
            try {
                mAppGpService.getGpService().closePort(1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印测试页
     */
    @OnClick(R.id.btn_print_test)
    public void printTest(Button button) {
        //ptksai pos 不支持USB打印
        String  isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
        if (isSupportUSBPrint.equals("1")) return;
        try {
            int status = mAppGpService.getGpService().printeTestPage(1);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[status];
            if(r != GpCom.ERROR_CODE.SUCCESS){
                ToastUtils.showShort(mAct,"请配置是否使用USB打印");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置注入
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
