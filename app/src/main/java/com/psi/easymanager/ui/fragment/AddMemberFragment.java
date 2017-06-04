package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.PxRechargePlanDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.VipInfoListEvent;
import com.psi.easymanager.module.PxRechargePlan;
import com.psi.easymanager.module.PxRechargeRecord;
import com.psi.easymanager.module.PxVipInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpSingleVipInfoReq;
import com.psi.easymanager.network.resp.HttpResp;
import com.psi.easymanager.print.bt.BTPrintTask;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.print.net.PrinterTask;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.usb.AppGpService;
import com.psi.easymanager.print.usb.AppUsbDeviceName;
import com.psi.easymanager.print.usb.PrinterUsbData;
import com.psi.easymanager.ui.activity.MemberCentreActivity;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.NetUtils;
import com.psi.easymanager.utils.RegExpUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by wangzhen on 2016/10/12
 * 新建会员
 */
public class AddMemberFragment extends BaseFragment {
  @Bind(R.id.tv_name) TextView tvName;
  @Bind(R.id.tv_mobile) TextView tvMobile;
  @Bind(R.id.tv_money) TextView tvMoney;
  @Bind(R.id.tv_plan) TextView tvPlan;
  //MemberCentreActivity
  private MemberCentreActivity mAct;
  //Fragment管理器
  private FragmentManager mFm;
  //充值计划list
  private List<PxRechargePlan> mRechargePlanList;
  //所选的充值计划
  private PxRechargePlan pxRechargePlan;
  private String[] plans;//存储充值计划名称

  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;

  public static AddMemberFragment newInstance(String param) {
    Bundle bundle = new Bundle();
    bundle.putString("param", param);
    AddMemberFragment fragment = new AddMemberFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (MemberCentreActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_add_member, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(AppGpService.class);
  }

  /**
   * TextView点击事件
   */
  @OnClick({ R.id.rl_name, R.id.rl_mobile, R.id.rl_money, R.id.rl_plan })
  public void setClickTextView(View view) {
    switch (view.getId()) {
      case R.id.rl_name:
        MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("会员名称")
            .content("请输入会员名称")
            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
            .positiveText("确认")
            .negativeText("取消")
            .inputMaxLength(6)
            .negativeColor(mAct.getResources().getColor(R.color.primary_text))
            .alwaysCallInputCallback()
            .input("请输入会员名称", "", false, new MaterialDialog.InputCallback() {
              @Override public void onInput(MaterialDialog dialog, CharSequence charSequence) {
                dialog.getActionButton(DialogAction.POSITIVE)
                    .setEnabled(RegExpUtils.matchName(charSequence.toString().trim()));
              }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
                checkNameAndSave(dialog);
              }
            })
            .show();
        addDialog(dialog);
        break;
      case R.id.rl_mobile:
        MaterialDialog dialog1 = new MaterialDialog.Builder(mAct).title("会员电话")
            .content("请输入会员电话")
            .inputType(InputType.TYPE_CLASS_NUMBER)
            .positiveText("确认")
            .negativeText("取消")
            .inputMaxLength(11)
            .negativeColor(mAct.getResources().getColor(R.color.primary_text))
            .alwaysCallInputCallback()
            .input("请输入会员电话", "", false, new MaterialDialog.InputCallback() {
              @Override public void onInput(MaterialDialog dialog, CharSequence charSequence) {
                dialog.getActionButton(DialogAction.POSITIVE)
                    .setEnabled(RegExpUtils.match11Number(charSequence.toString().trim()));
              }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
                //checkNumAndSave(dialog);
                tvMobile.setText(dialog.getInputEditText().getText().toString().trim());
                tvMobile.setTextColor(getResources().getColor(R.color.colorAccent));
              }
            })
            .show();
        addDialog(dialog1);
        break;
      case R.id.rl_money:
        MaterialDialog dialog2 = new MaterialDialog.Builder(mAct).title("充值钱数")
            .content("请输入充值钱数")
            .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER)
            .negativeText("取消")
            .negativeColor(mAct.getResources().getColor(R.color.primary_text))
            .positiveText("确定")
            .alwaysCallInputCallback()
            .input("请输入充值钱数", "", false, new MaterialDialog.InputCallback() {
              @Override public void onInput(MaterialDialog dialog, CharSequence charSequence) {
                dialog.getActionButton(DialogAction.POSITIVE)
                    .setEnabled(RegExpUtils.matchMoney(charSequence.toString().trim()));
              }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
                checkMoneyAndSave(dialog);
              }
            })
            .show();
        addDialog(dialog2);
        break;
      case R.id.rl_plan:
        //判断是否能输入了金额
        if (!tvMoney.getText().toString().trim().isEmpty()) {
          //获取充值计划
          getAllRechargePlan();
          if (mRechargePlanList.size() > 0) {
            final List<PxRechargePlan> planss = new ArrayList<>();
            for (int i = 0; i < mRechargePlanList.size(); i++) {
              if (Double.parseDouble(tvMoney.getText().toString().trim()) >= mRechargePlanList.get(
                  i).getMoney()) {
                planss.add(mRechargePlanList.get(i));
              }
            }

            if (planss != null) {
              plans = new String[planss.size()];
              for (int i = 0; i < planss.size(); i++) {
                String plan = planss.get(i).getName();
                plans[i] = plan;
              }
            }

            if (planss.size() > 0) {
              MaterialDialog dialog3 = new MaterialDialog.Builder(mAct).title("充值方案")
                  .items(plans)
                  .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which,
                        CharSequence charSequence) {
                      if (charSequence != null) {
                        //得到所选充值计划
                        pxRechargePlan = planss.get(which);
                        tvPlan.setText(charSequence.toString());
                        tvPlan.setTextColor(getResources().getColor(R.color.colorAccent));
                      }
                      return true;
                    }
                  })
                  .negativeText("取消")
                  .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override public void onClick(@NonNull MaterialDialog dialog,
                        @NonNull DialogAction which) {
                      pxRechargePlan = null;
                      tvPlan.setText("");
                    }
                  })
                  .negativeColor(mAct.getResources().getColor(R.color.primary_text))
                  .positiveText("确定")
                  .neutralText("不选择充值方案")
                  .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override public void onClick(@NonNull MaterialDialog dialog,
                        @NonNull DialogAction which) {
                      tvPlan.setText("没有选择充值方案");
                      tvPlan.setTextColor(getResources().getColor(R.color.colorAccent));
                      pxRechargePlan = null;
                    }
                  })
                  .show();
              addDialog(dialog3);
            } else {

              MaterialDialog dialog4 = new MaterialDialog.Builder(mAct).title("提示")
                  .content("没有符合的充值方案")
                  .positiveText("确定")
                  .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
                      pxRechargePlan = null;
                      tvPlan.setText("无充值方案");
                      tvPlan.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                  })
                  .show();
              addDialog(dialog4);
            }
          } else {
            MaterialDialog dialog5 = new MaterialDialog.Builder(mAct).title("提示")
                .content("没有相关的充值方案")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                  @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
                    pxRechargePlan = null;
                    tvPlan.setText("没有选择充值方案");
                    tvPlan.setTextColor(getResources().getColor(R.color.colorAccent));
                  }
                })
                .show();
            addDialog(dialog5);
          }
        } else {
          ToastUtils.show("请先输入充值金额");
        }
        break;
    }
  }

  /**
   * 检查名字的有效性
   */
  private void checkNameAndSave(MaterialDialog dialog) {
    String inputName = dialog.getInputEditText().getText().toString().trim();
    tvName.setText(inputName);
    tvName.setTextColor(getResources().getColor(R.color.colorAccent));
  }

  /**
   * 检查保存充值金额
   */
  private void checkMoneyAndSave(MaterialDialog dialog) {
    String money = dialog.getInputEditText().getText().toString().trim();
    tvMoney.setText(money);
    tvMoney.setTextColor(getResources().getColor(R.color.colorAccent));
  }

  /**
   * 返回和确定按钮的点击事件
   */
  @OnClick({ R.id.btn_add_back, R.id.btn_sure }) public void btnClickButton(Button button) {
    switch (button.getId()) {
      case R.id.btn_add_back:
        echoFragment(mFm.beginTransaction());
        break;
      case R.id.btn_sure:
        onclickSure();
        break;
    }
  }

  /**
   * 确定
   */
  private void onclickSure() {
    String name = tvName.getText().toString().trim();
    String mobile = tvMobile.getText().toString().trim();
    String money = tvMoney.getText().toString().trim();
    //信息做不为空校验
    if (name.isEmpty()) {
      ToastUtils.show("请添加会员名字");
      return;
    }
    if (mobile.isEmpty()) {
      ToastUtils.show("请添加会员电话");
      return;
    }
    if (money.isEmpty()) {
      ToastUtils.show("请填写充值金额");
      return;
    }

    if (!NetUtils.isConnected(mAct)) {
      ToastUtils.show(mAct, "没有网络，请检查网络设置!", Toast.LENGTH_SHORT);
      return;
    }

    //构造上传的VipINfo
    final PxVipInfo pxVipInfo = new PxVipInfo();
    //正常
    pxVipInfo.setDelFlag("0");
    //默认没上传
    pxVipInfo.setIsUpLoad(false);
    //默认没修改
    pxVipInfo.setIsModify(false);
    //名字
    pxVipInfo.setName(name);
    //手机号
    pxVipInfo.setMobile(mobile);
    //等级 恒定1
    pxVipInfo.setLevel("1");
    final double balance = Double.parseDouble(money);
    if (balance > 0) {
      pxVipInfo.setAccountBalance(balance);
    }

    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.show("APP发生异常，请重新启动!");
      return;
    }
    String companyCode = user.getCompanyCode();
    String userId = user.getObjectId();
    HttpSingleVipInfoReq req = new HttpSingleVipInfoReq();
    req.setCompanyCode(companyCode);
    req.setUserId(userId);
    req.setVipInfo(pxVipInfo);
    if (pxRechargePlan != null) {
      req.setPlanId(pxRechargePlan.getObjectId());
    }

    final MaterialDialog addVipDialog = DialogUtils.showDialog(mAct, "添加会员", "添加中,请耐心等待!");
    addDialog(addVipDialog);
    new RestClient(0, 1000, 15000, 5000) {

      @Override protected void start() {
      }

      @Override protected void finish() {
      }

      @Override protected void failure(String responseString, Throwable throwable) {
        DialogUtils.dismissDialog(addVipDialog);
        Logger.e(responseString + "---" + throwable.toString());
        ToastUtils.show("会员添加失败");
        resetData();
      }

      @Override protected void success(String responseString) {
        Logger.i(":" + responseString);
        Logger.json(responseString);
        //需要做状态码为1001 做判断 ---此会员已经存在
        HttpResp resp = getGson().fromJson(responseString, HttpResp.class);

        if (resp.getStatusCode() == 1001) {

          ToastUtils.show("该会员已经存在！");
        } else if (resp.getStatusCode() == 1) {
          //打印会员充值
          if (balance > 0) {
            PxRechargeRecord record = new PxRechargeRecord();
            record.setDelFlag("0");
            record.setRechargeTime(new Date());
            record.setMoney(balance);
            record.setGiving((pxRechargePlan == null) ? 0.0 : pxRechargePlan.getLargess());
            //打印充值记录
            printRecord(record, pxVipInfo);
          }
          ToastUtils.show("会员添加成功");
          echoFragment(mFm.beginTransaction());
          // EventBus.getDefault().post(new AddMemberEvent(pxVipInfo));
          EventBus.getDefault().post(new VipInfoListEvent(pxVipInfo.getMobile()));
          resetData();
        }
        DialogUtils.dismissDialog(addVipDialog);
      }
    }.postOther(mAct, URLConstants.VIP_ADD_NEW, req);
  }

  /**
   * 打印充值记录
   */
  private void printRecord(PxRechargeRecord record, PxVipInfo pxVipInfo) {
    if (pxRechargePlan != null) {
      pxVipInfo.setAccountBalance(pxVipInfo.getAccountBalance() + pxRechargePlan.getLargess());
    }
    //网络打印 收银
    printByNetAndBT(record, pxVipInfo);
    //2-USB打印
    printByUSBPrinter(pxVipInfo, record);
  }

  private void printByNetAndBT(PxRechargeRecord record, PxVipInfo pxVipInfo) {
    PrinterTask task = new PrinterTask(BTPrintConstants.PRINT_MODE_VIP_RECHARGE_RECORD, record, pxVipInfo);
    PrintTaskManager.printCashTask(task);

    BTPrintTask btPrintTask =
        new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_VIP_RECHARGE_RECORD)
            .vipRechargeRecord(record)
            .vipInfo(pxVipInfo)
            .build();
    PrintEventManager.getManager().postBTPrintEvent(btPrintTask);
  }

  /**
   * USB打印 收银
   */
  private void printByUSBPrinter(PxVipInfo pxVipInfo, PxRechargeRecord record) {
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          if (pxVipInfo != null) {
            PrinterUsbData.printVipRechargeRecord(mAppGpService.getGpService(), pxVipInfo, record);
          }
        }
      } catch (RemoteException e) {
        ToastUtils.show("打印机异常:" + e.getMessage());
        e.printStackTrace();
      }
    } else {
      ToastUtils.show("设备未连接,请在更多模块配置普通打印机!");
    }
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
      ToastUtils.show("USB设备名为空");
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
      ToastUtils.show("服务为空");
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

  /**
   * 重置数据
   */
  private void resetData() {
    tvName.setHint("请输入会员名称");
    tvName.setText("");
    tvMobile.setHint("请输入会员电话");
    tvMobile.setText("");
    tvMoney.setHint("请输入充值钱数");
    tvMoney.setText("");
    tvPlan.setHint("请选择充值方案");
    tvPlan.setText("");
    pxRechargePlan = null;
  }

  /**
   * 隐藏所有Fragment
   */
  private void hideAllFragment(FragmentTransaction transaction) {
    //获取栈内的所有fragment
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        transaction.hide(fragment);
      }
    }
  }

  /**
   * 获取所有充计划
   */
  private void getAllRechargePlan() {

    /**
     *从数据库获取
     */
    mRechargePlanList = new ArrayList<PxRechargePlan>();
    mRechargePlanList = DaoServiceUtil.getRechargePlanService()
        .queryBuilder()
        .where(PxRechargePlanDao.Properties.DelFlag.eq("0"))
        .list();
  }

  /**
   * 退出
   */
  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
  }

  /**
   * 回显mVipOperationFragment
   */
  private void echoFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        if ((fragment instanceof AddMemberFragment)) {
          transaction.hide(fragment);
        }
      }
    }
    Fragment mVipOperationFragment = mFm.findFragmentByTag(Constants.VIPINFOCHARGEANDRECHARGE);
    if (mVipOperationFragment == null) {
      mVipOperationFragment = VipOperationFragment.newInstance("param");
      transaction.add(R.id.fl_member_content, mVipOperationFragment,
          Constants.VIPINFOCHARGEANDRECHARGE);
    } else {
      transaction.show(mVipOperationFragment);
    }
    transaction.commit();
  }

  /**
   * 隐藏除了新建会员Fragment
   */
  private void hideExcludeQueryMember(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();

    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        if ((fragment instanceof QueryMemberFragment)) continue;
        transaction.hide(fragment);
      }
    }
  }
}
