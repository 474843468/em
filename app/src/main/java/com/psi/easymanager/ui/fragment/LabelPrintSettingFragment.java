package com.psi.easymanager.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.kyleduo.switchbutton.SwitchButton;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.service.PrintQueueService;
import com.psi.easymanager.ui.activity.MoreActivity;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.RegExpUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ServiceWorkUtil;
import com.psi.easymanager.utils.ToastUtils;

/**
 * User: ylw
 * Date: 2016-11-14
 * Time: 15:19
 * 标签打印机配置
 */
public class LabelPrintSettingFragment extends BaseFragment
    implements CompoundButton.OnCheckedChangeListener {
  @Bind(R.id.sb_is_open) SwitchButton mSbIsOpen;//是否开启标签打印
  @Bind(R.id.tv_ip) TextView mTvIp;
  @Bind(R.id.tv_paper_type) TextView mTvPaperType;

  private MoreActivity mAct;
  private String mParam;
  private static final String LABEL_PRINT_SETTING_FRAGMENT_PARAM = "param";

  public static LabelPrintSettingFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    LabelPrintSettingFragment fragment = new LabelPrintSettingFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam = getArguments().getString(LABEL_PRINT_SETTING_FRAGMENT_PARAM);
    }
    mAct = (MoreActivity) getActivity();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_label_print_setting, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initView();
  }

  //@formatter:off
  //init view
  private void initView() {
    //isOpen
    boolean isOpenLabelPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_LABEL_PRINT, false);
    mSbIsOpen.setChecked(isOpenLabelPrint);
    //label printer ip
    String labelPrinterIp = (String) SPUtils.get(mAct, Constants.LABEL_PRINTER_IP, "");
    if (!TextUtils.isEmpty(labelPrinterIp)) {
      mTvIp.setText(labelPrinterIp);
    }
    //paper type
    int paperWidth = (int) SPUtils.get(mAct, Constants.LABEL_PRINTER_PAPER_WIDTH, 0);
    int paperHeight = (int) SPUtils.get(mAct, Constants.LABEL_PRINTER_PAPER_HEIGHT, 0);
    int paperGap = (int) SPUtils.get(mAct, Constants.LABEL_PRINTER_PAPER_GAP, 2);
    mTvPaperType.setText("宽" + paperWidth + "mm高" + paperHeight +"mm间隔" + paperGap + "mm");
    mSbIsOpen.setOnCheckedChangeListener(this);
  }

  @OnClick({ R.id.rl_printer_ip, R.id.rl_paper_type }) public void onClick(View view) {
    boolean isOpenLabelPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_LABEL_PRINT, false);
    if (isOpenLabelPrint) {
      ToastUtils.showShort(mAct,"修改配置，请先关闭标签打印机!");
      return;
    }
    switch (view.getId()) {
      case R.id.rl_printer_ip:
        setIp();
        break;
      case R.id.rl_paper_type:
        selectPaperSetUp();
        break;
    }
  }
  /**
   * 开关标签打印
   */
  @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    App app = (App) App.getContext();
    if (!isChecked){
      //EventBus.getDefault().post(new SwitchLabelPrintEvent(false));
      PrintEventManager.getManager().postPrintEvent(PrintEventManager.SWITCH_LABEL,false);
      SPUtils.put(mAct, Constants.SWITCH_LABEL_PRINT, false);
      app.setOpenLabel(false);
      return;
    }
    //ip
    String ipAddress = mTvIp.getText().toString();
    if (!RegExpUtils.isBoolIp(ipAddress)) {
      ToastUtils.showShort(App.getContext(), "输入IP有误，请重新输入");
      mSbIsOpen.setChecked(false);
      SPUtils.put(mAct, Constants.SWITCH_LABEL_PRINT, false);
      app.setOpenLabel(false);
      return;
    }
    //width height
    int width = (int) SPUtils.get(mAct, Constants.LABEL_PRINTER_PAPER_WIDTH, 0);
    int height = (int) SPUtils.get(mAct, Constants.LABEL_PRINTER_PAPER_HEIGHT, 0);

    if (width <= 0 || height <= 0) {
      ToastUtils.showShort(App.getContext(), "标签纸张宽高有误");
      SPUtils.put(mAct, Constants.SWITCH_LABEL_PRINT, false);
      app.setOpenLabel(false);
      mSbIsOpen.setChecked(false);
      return;
    }

    String serviceName = mAct.getPackageName()+".service.PrintQueueService";
    if (!ServiceWorkUtil.isServiceWork(mAct,serviceName)) {
      mAct.startService(new Intent(mAct, PrintQueueService.class));
    }
    SPUtils.put(mAct, Constants.SWITCH_LABEL_PRINT, true);
    app.setOpenLabel(true);
    //EventBus.getDefault().post(new SwitchLabelPrintEvent(true,ipAddress,width,height));
    PrintEventManager.getManager().postPrintEvent(PrintEventManager.SWITCH_LABEL,true);
  }

  /**
   * select paper type
   */
  private void selectPaperSetUp() {
    final View dialogCustomView = View.inflate(mAct, R.layout.layout_dialog_label_set_up, null);
    final MaterialDialog selectPaperDialog = new MaterialDialog.Builder(mAct).title("标签纸型号")
        .customView(dialogCustomView, false)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .show();
    final EditText etWidth = (EditText) dialogCustomView.findViewById(R.id.et_width);
    final EditText etHeight = (EditText) dialogCustomView.findViewById(R.id.et_height);
    final EditText etGap = (EditText) dialogCustomView.findViewById(R.id.et_gap);
    //自动跳入下一EditText
    etWidth.addTextChangedListener(new AutoEditTextTextWatcher(etWidth, etHeight, 2));
    etHeight.addTextChangedListener(new AutoEditTextTextWatcher(etHeight,etGap,2));

    MDButton posBtn = selectPaperDialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = selectPaperDialog.getActionButton(DialogAction.NEGATIVE);
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(selectPaperDialog);
      }
    });
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        String paperWidth = etWidth.getText().toString().trim();
        String paperHeight = etHeight.getText().toString().trim();
        String paperGap = etGap.getText().toString().trim();

        if (paperWidth.trim().isEmpty() || paperHeight.trim().isEmpty() || paperGap.trim().isEmpty()){
          ToastUtils.showShort(mAct,"请填写有效值");
          return;
        }
        mTvPaperType.setText("宽" + paperWidth + "mm高" + paperHeight + "mm间隔" + paperGap + "mm");
        int width = Integer.valueOf(paperWidth);
        int height = Integer.valueOf(paperHeight);
        int gap = Integer.valueOf(paperGap);
        //save
        SPUtils.put(mAct, Constants.LABEL_PRINTER_PAPER_WIDTH, width);
        SPUtils.put(mAct, Constants.LABEL_PRINTER_PAPER_HEIGHT, height);
        SPUtils.put(mAct, Constants.LABEL_PRINTER_PAPER_GAP, gap);
        if (width <= 0 || height <= 0) {
          ToastUtils.showShort(App.getContext(), "标签纸张类型宽高有误");
        }
        DialogUtils.dismissDialog(selectPaperDialog);
      }
    });
  }

  /**
   * set ip
   */

  private void setIp() {
    final View dialogCustomView = View.inflate(mAct, R.layout.layout_dialog_label_printer_setting_ip, null);
    final MaterialDialog settingDialog = new MaterialDialog.Builder(mAct).title("配置标签打印机地址")
        .customView(dialogCustomView, true)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .show();
    final EditText et1 = (EditText) dialogCustomView.findViewById(R.id.et_label_printer_1);
    final EditText et2 = (EditText) dialogCustomView.findViewById(R.id.et_label_printer_2);
    final EditText et3 = (EditText) dialogCustomView.findViewById(R.id.et_label_printer_3);
    final EditText et4 = (EditText) dialogCustomView.findViewById(R.id.et_label_printer_4);
    //自动跳入下一EditText
    et1.addTextChangedListener(new AutoEditTextTextWatcher(et1, et2, 3));
    et2.addTextChangedListener(new AutoEditTextTextWatcher(et2, et3, 3));
    et3.addTextChangedListener(new AutoEditTextTextWatcher(et3, et4, 1));

    MDButton posBtn = settingDialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = settingDialog.getActionButton(DialogAction.NEGATIVE);
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        checkAndSaveIP(et1, et2, et3, et4);
        DialogUtils.dismissDialog(settingDialog);
      }
    });
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(settingDialog);
      }
    });
  }

  /**
   * 检查IP有效 保存
   */
  private void checkAndSaveIP(EditText et1, EditText et2, EditText et3, EditText et4) {
    String inputEt1 = et1.getText().toString().trim();
    String inputEt2 = et2.getText().toString().trim();
    String inputEt3 = et3.getText().toString().trim();
    String inputEt4 = et4.getText().toString().trim();
    if (inputEt1.isEmpty() || inputEt2.isEmpty() || inputEt3.isEmpty() || inputEt4.isEmpty()) {
      ToastUtils.showShort(mAct,"输入IP有误，请重新输入");
      return;
    }
    String ip = inputEt1 + "." + inputEt2 + "." + inputEt3 + "." + inputEt4;
    mTvIp.setText(ip);
    SPUtils.put(mAct, Constants.LABEL_PRINTER_IP, ip);
    if (!RegExpUtils.isBoolIp(ip)){
      ToastUtils.showShort(App.getContext(),"无效IP");
    }
  }

  //@formatter:on
  class AutoEditTextTextWatcher implements TextWatcher {
    private EditText nowEt, afterEt;
    private int length;

    public AutoEditTextTextWatcher(EditText et1, EditText et2, int length) {
      this.nowEt = et1;
      this.afterEt = et2;
      this.length = length;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (nowEt.hasFocus() && nowEt.getText().toString().trim().length() == length) {
        nowEt.clearFocus();
        afterEt.requestFocus();
      }
    }

    @Override public void afterTextChanged(Editable s) {

    }
  }

  //@OnClick(R.id.tv_title) public void test() {
  //  String address = IpAddressUtils.getLocalIpAddress();
  //  if (TextUtils.isEmpty(address)) return;
  //  int lastIndexOf = address.lastIndexOf(".");
  //  String host = address.substring(0, lastIndexOf);
  //  ScanIpUtils.getValidIpList(host, mHandler);
  //
  //}
  //
  //Handler mHandler = new Handler() {
  //  @Override public void handleMessage(Message msg) {
  //    super.handleMessage(msg);
  //    if (msg.what == ScanIpUtils.SCAN_IP_RESULT) {
  //
  //    }
  //  }
  //};

  @Override public void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}