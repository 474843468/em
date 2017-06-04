package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.VipConsumeRecordAdapter;
import com.psi.easymanager.adapter.VipRechargeRecordAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.config.URLConstants;
import com.psi.easymanager.dao.PxRechargePlanDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.event.VipInfoListEvent;
import com.psi.easymanager.event.VipLoginEvent;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxRechargePlan;
import com.psi.easymanager.module.PxRechargeRecord;
import com.psi.easymanager.module.PxSetInfo;
import com.psi.easymanager.module.PxVipCardInfo;
import com.psi.easymanager.module.PxVipInfo;
import com.psi.easymanager.module.User;
import com.psi.easymanager.network.RestClient;
import com.psi.easymanager.network.req.HttpIdCardRechargeReq;
import com.psi.easymanager.network.req.HttpSingleRechargeReq;
import com.psi.easymanager.network.req.HttpSingleVipCardReq;
import com.psi.easymanager.network.req.HttpSingleVipInfoReq;
import com.psi.easymanager.network.req.HttpVipRechargeRecordListReq;
import com.psi.easymanager.network.req.HttpVipRechargeReverseReq;
import com.psi.easymanager.network.resp.HttpRechargeRecordResp;
import com.psi.easymanager.network.resp.HttpResp;
import com.psi.easymanager.network.resp.HttpVipCardRechargeResp;
import com.psi.easymanager.network.resp.HttpVipRechargeRecordListResp;
import com.psi.easymanager.network.resp.HttpVipRechargeReverseResp;
import com.psi.easymanager.pay.vip.VipLogin;
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
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.RegExpUtils;
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
 * Created by wangzhen on 2016-09-28.
 */
public class VipOperationFragment extends BaseFragment {
  @Bind(R.id.tv_vip_charge) TextView mVipInfoCharge;//充值title
  @Bind(R.id.tv_vip_charge_again) TextView mVipInfoChargeAgain;//冲销title
  //@Bind(R.id.tv_vip_consume) TextView mVipConsume;
  //@Bind(R.id.btn_vip_operation_back) Button mVipInfoOperationBack;
  @Bind(R.id.btn_vip_operation_update) Button mVipInfoOperationUpdate;//修改
  @Bind(R.id.btn_vip_operation_charge) Button mVipInfoOperationCharge;//充值
  @Bind(R.id.rl_recharge_and_again_bottom) RelativeLayout mRlRechargeAndAgainBottom;//整个底部
  @Bind(R.id.tv_name) TextView tvMemberDetailName;//会员名字
  @Bind(R.id.tv_mobile) TextView tvMemberDetailMobile;//会员电话
  @Bind(R.id.tv_money) TextView tvMemberDetailMoney;//余额
  @Bind(R.id.tv_score) TextView tvMemberScore;//积分
  @Bind(R.id.tv_modify_name) TextView tvMemberModifyName;//名字修改显示提示
  @Bind(R.id.tv_modify_mobile) TextView tvMemberModifyMobile;//电话号码修改显示提示
  @Bind(R.id.tv_vip_info_name) TextView tvMemberRechargeDetailName;//名称
  @Bind(R.id.tv_vip_info_mobile) TextView tvMemberRechargeDetailMobile;//电话
  @Bind(R.id.tv_vip_info_balance) TextView tvMemberRechargeBalance;//余额
  @Bind(R.id.tv_vip_info_integral) TextView tvMemberRechargeIntegral;//积分
  //充值ll
  @Bind(R.id.ll_vip_info_charge) LinearLayout mVipChargeLinearLayout;
  //冲销ll
  @Bind(R.id.ll_vip_info_charge_again) LinearLayout mChargeAgainLinearLayout;
  //消费记录ll
  @Bind(R.id.ll_vip_info_consume) LinearLayout mVipConsumeLinearLayout;
  //充值记录rcv
  @Bind(R.id.rcv_vin_info_rcv) RecyclerView rcvRechargeRecord;
  //消费记录rcv
  //@Bind(R.id.rcv_vip_info_consume_rcv) RecyclerView rcvVipConsumeRecord;
  @Bind(R.id.rl_mobile_modify) RelativeLayout rl_mobile_modify;
  @Bind(R.id.rl_name_modify) RelativeLayout rl_name_modify;

  private String[] plans;//存储充值计划名字
  //存储满足条件的 充值计划
  private List<PxRechargePlan> planList;
  //private int position;//选择的vipinfo 在 会员列表的位置
  //MemberCentreActivity
  private MemberCentreActivity mAct;
  //Fragment管理器
  private FragmentManager mFm;
  private PxVipInfo mCurrentVipInfo;//当前会员信息
  private PxVipCardInfo mCurrentVipCardInfo;//当前会员信息
  //0:充值1:冲销2:消费记录
  private int choice = 0;//记录当前是选择 充值/冲销/消费记录

  //充值记录 适配器
  private VipRechargeRecordAdapter vipRechargeRecordAdapter;
  //会员消费记录 适配器
  private VipConsumeRecordAdapter vipConsumeAdapter;
  //充值记录列表
  private List<PxRechargeRecord> mRechargeRecordList;
  //充值方案列表
  private List<PxRechargePlan> mRechargePlanList;
  //会员消费记录列表
  private List<PxOrderInfo> mVipConsumeList;
  //充值记录点击监听
  private VipRechargeRecordAdapter.onRechargeRecordClickListener listener;
  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //单一线程用于打印
  private ExecutorService sDbEngine = null;
  //用于再打印
  private PxRechargeRecord recordPrint;
  //用于再打印
  private PxVipInfo vipInfoPrint;
  //会员电话
  private String mVipMobileCopy;
  //会员名称
  private String mVipNameCopy;

  public static VipOperationFragment newInstance(String param) {
    Bundle bundle = new Bundle();
    bundle.putString("param", param);
    VipOperationFragment fragment = new VipOperationFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (MemberCentreActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    mRechargePlanList = new ArrayList<>();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_recharge_and_charge_again, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(AppGpService.class);
    listener = new VipRechargeRecordAdapter.onRechargeRecordClickListener() {
      @Override public void onRechargeRecordItemClick(final int pos) {
        final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("提示")
            .content("您确定要对此条记录进行冲销？")
            .negativeText("取消")
            .negativeColor(mAct.getResources().getColor(R.color.primary_text))
            .positiveText("确定")
            .show();
        MDButton posBtn = dialog.getActionButton(DialogAction.POSITIVE);
        final PxRechargeRecord mRechargeRecord = mRechargeRecordList.get(pos);
        addDialog(dialog);
        posBtn.setOnClickListener(new View.OnClickListener() {

          @Override public void onClick(View v) {
            if (mCurrentVipInfo != null) {
              //电话会员冲销
              if (mCurrentVipInfo.getAccountBalance() <= 0) {
                ToastUtils.show(mAct, "此会员已无可用金额,不能冲正！", Toast.LENGTH_SHORT);
                DialogUtils.dismissDialog(dialog);
                return;
              }
              //发送请求 进行 冲正
              App app = (App) App.getContext();
              User user = app.getUser();
              if (user == null) {
                ToastUtils.show("APP发生异常，请重新启动!");
                DialogUtils.dismissDialog(dialog);
                return;
              }
              String companyCode = user.getCompanyCode();
              String userId = user.getObjectId();
              HttpVipRechargeReverseReq req = new HttpVipRechargeReverseReq();
              req.setUserId(userId);
              req.setCompanyCode(companyCode);
              req.setmRechargeRecord(mRechargeRecord);
              new RestClient() {
                @Override protected void start() {
                  //开启蒙层
                  mAct.isShowProgress(true);
                }

                @Override protected void success(String responseString) {
                  Logger.json(responseString);
                  HttpVipRechargeReverseResp resp =
                      getGson().fromJson(responseString, HttpVipRechargeReverseResp.class);
                  if (resp.getStatusCode() == HttpResp.SUCCESS) {
                    //修改充值记录列表 记录显示页面删除相应的得充值记录
                    mRechargeRecordList.remove(pos);
                    vipRechargeRecordAdapter.setData(mRechargeRecordList);
                    // 发送 事件 修改 会员列表相应的选择项的 会员信息
                    // 刷新会员信息显示
                    Double subtract =
                        NumberFormatUtils.subtract(mCurrentVipInfo.getAccountBalance(),
                            mRechargeRecord.getMoney());
                    if (mRechargeRecord.getGiving() != 0) {
                      Double st = NumberFormatUtils.subtract(subtract, mRechargeRecord.getGiving());
                      mCurrentVipInfo.setAccountBalance(
                          Double.valueOf(NumberFormatUtils.formatFloatNumber(st)));
                    } else {
                      mCurrentVipInfo.setAccountBalance(Double.valueOf(subtract));
                    }
                    Logger.v(mCurrentVipInfo.getAccountBalance() + "");
                    ToastUtils.show( "冲销成功");
                    initReverseVipInfo(mCurrentVipInfo, mCurrentVipCardInfo, false);
                  } else if (resp.getStatusCode() == 1001) {
                    ToastUtils.show(resp.getMsg());
                  } else if (resp.getStatusCode() == 1002) {
                    ToastUtils.show(resp.getMsg());
                  } else {
                    ToastUtils.show("操作失败");
                  }
                  //蒙层
                  mAct.isShowProgress(false);
                }

                @Override protected void finish() {

                }

                @Override protected void failure(String responseString, Throwable throwable) {
                  //蒙层
                  mAct.isShowProgress(false);
                  ToastUtils.show(mAct, "冲正失败,请稍后重试", Toast.LENGTH_SHORT);
                }
              }.postOther(mAct, URLConstants.VIP_RECHARGE_RECORD_REVERSE, req);
              //dismiss dialog
              DialogUtils.dismissDialog(dialog);
            } else {
              //实体卡会员冲销
              if (mCurrentVipCardInfo.getAccountBalance() <= 0) {
                ToastUtils.show(mAct, "此会员已无可用金额,不能冲正！", Toast.LENGTH_SHORT);
                DialogUtils.dismissDialog(dialog);
                return;
              }
              App app = (App) App.getContext();
              User user = app.getUser();
              if (user == null) {
                ToastUtils.show("APP发生异常，请重新启动!");
                DialogUtils.dismissDialog(dialog);
                return;
              }
              String companyCode = user.getCompanyCode();
              String userId = user.getObjectId();
              HttpVipRechargeReverseReq req = new HttpVipRechargeReverseReq();
              req.setUserId(userId);
              req.setCompanyCode(companyCode);
              req.setmRechargeRecord(mRechargeRecord);
              new RestClient() {
                @Override protected void start() {
                  //开启蒙层
                  mAct.isShowProgress(true);
                }

                @Override protected void success(String responseString) {
                  Logger.json(responseString);
                  HttpVipRechargeReverseResp resp =
                      getGson().fromJson(responseString, HttpVipRechargeReverseResp.class);
                  if (resp.getStatusCode() == HttpResp.SUCCESS) {
                    //修改充值记录列表 记录显示页面删除相应的得充值记录
                    mRechargeRecordList.remove(pos);
                    vipRechargeRecordAdapter.setData(mRechargeRecordList);
                    // 发送 事件 修改 会员列表相应的选择项的 会员信息
                    // 刷新会员信息显示
                    Double subtract =
                        NumberFormatUtils.subtract(mCurrentVipCardInfo.getAccountBalance(),
                            mRechargeRecord.getMoney());
                    if (mRechargeRecord.getGiving() != 0) {
                      Double st = NumberFormatUtils.subtract(subtract, mRechargeRecord.getGiving());
                      mCurrentVipCardInfo.setAccountBalance(
                          Double.valueOf(NumberFormatUtils.formatFloatNumber(st)));
                    } else {
                      mCurrentVipCardInfo.setAccountBalance(Double.valueOf(subtract));
                    }
                    Integer score = mRechargeRecord.getScore();
                    mCurrentVipCardInfo.setScore(mCurrentVipCardInfo.getScore() - score);
                    ToastUtils.show("冲销成功");
                    initReverseVipInfo(mCurrentVipInfo, mCurrentVipCardInfo, false);
                  } else {
                    ToastUtils.show(resp.getMsg());
                  }
                  //蒙层
                  mAct.isShowProgress(false);
                }

                @Override protected void finish() {

                }

                @Override protected void failure(String responseString, Throwable throwable) {
                  //蒙层
                  mAct.isShowProgress(false);
                  ToastUtils.show(mAct, "冲正失败,请稍后重试", Toast.LENGTH_SHORT);
                }
              }.postOther(mAct, URLConstants.VIP_CARD_RECHARGE_RECORD_REVERSE, req);
              //dismiss dialog
              DialogUtils.dismissDialog(dialog);
            }
          }
        });
      }
    };

    //初始化View
    initView();
    mVipInfoCharge.setSelected(true);
    mVipChargeLinearLayout.setVisibility(View.VISIBLE);
    //会员修改编辑项最初设置为不可点击
    rl_name_modify.setClickable(false);
    rl_mobile_modify.setClickable(false);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
    closePool();
  }

  /**
   * 初始化View
   */
  private void initView() {
    mRechargeRecordList = new ArrayList<>();
    mVipConsumeList = new ArrayList<>();
    //充值记录
    LinearLayoutManager deviceManager = new LinearLayoutManager(mAct);

    vipRechargeRecordAdapter = new VipRechargeRecordAdapter(mAct, mRechargeRecordList);
    vipRechargeRecordAdapter.setListener(listener);
    rcvRechargeRecord.setLayoutManager(deviceManager);
    rcvRechargeRecord.setHasFixedSize(true);
    rcvRechargeRecord.setAdapter(vipRechargeRecordAdapter);

    //消费记录
    //LinearLayoutManager device1Manager = new LinearLayoutManager(mAct);
    //vipConsumeAdapter = new VipConsumeRecordAdapter(mAct, mVipConsumeList);
    //rcvVipConsumeRecord.setLayoutManager(device1Manager);
    //rcvVipConsumeRecord.setHasFixedSize(true);
    //rcvVipConsumeRecord.setAdapter(vipConsumeAdapter);
  }

  /**
   * 按钮点击事件处理
   */
  @OnClick({
      R.id.tv_vip_charge, R.id.tv_vip_charge_again, R.id.btn_vip_operation_back,
      R.id.btn_vip_operation_update, R.id.btn_vip_operation_charge
  }) public void setClickButton(View view) {
    switch (view.getId()) {
      case R.id.tv_vip_charge:
        choice = 0;//充值页
        mVipInfoCharge.setSelected(true);
        mVipInfoChargeAgain.setSelected(false);
        mVipInfoOperationUpdate.setVisibility(View.VISIBLE);
        mVipInfoOperationCharge.setVisibility(View.VISIBLE);
        mVipChargeLinearLayout.setVisibility(View.VISIBLE);
        mChargeAgainLinearLayout.setVisibility(View.GONE);
        mVipConsumeLinearLayout.setVisibility(View.GONE);
        mRlRechargeAndAgainBottom.setVisibility(View.VISIBLE);
        if (mCurrentVipCardInfo != null) {
          mVipInfoOperationUpdate.setVisibility(View.GONE);
        } else {
          mVipInfoOperationUpdate.setVisibility(View.VISIBLE);
        }
        break;
      case R.id.tv_vip_charge_again:
        choice = 1;//冲销页
        mVipInfoCharge.setSelected(false);
        mVipInfoChargeAgain.setSelected(true);
        mVipInfoOperationUpdate.setVisibility(View.GONE);
        mVipInfoOperationCharge.setVisibility(View.GONE);
        mVipChargeLinearLayout.setVisibility(View.GONE);
        mChargeAgainLinearLayout.setVisibility(View.VISIBLE);
        mVipConsumeLinearLayout.setVisibility(View.GONE);
        //初始化vip显示信息
        initReverseVipInfo(mCurrentVipInfo, mCurrentVipCardInfo, false);
        //获取充值记录
        queryVipRechargeRecord(mCurrentVipInfo, mCurrentVipCardInfo);
        break;
      //暂时不删除 保留
      //case R.id.tv_vip_consume:
      //  choice = 2;//消费中心
      //  mVipInfoCharge.setSelected(false);
      //  mVipInfoChargeAgain.setSelected(false);
      //  mVipConsume.setSelected(true);
      //  mVipInfoOperationUpdate.setVisibility(View.GONE);
      //  mVipInfoOperationCharge.setVisibility(View.GONE);
      //  mVipChargeLinearLayout.setVisibility(View.GONE);
      //  mChargeAgainLinearLayout.setVisibility(View.GONE);
      //  mVipConsumeLinearLayout.setVisibility(View.VISIBLE);
      //  //获取会员消费记录
      //  getVipConsumeList(mCurrentVipInfo);
      //  break;
      //case R.id.btn_vip_operation_back:
      //  echoFragment();
      // rushVipInfoList();
      //break;
      case R.id.btn_vip_operation_update://修改按钮
        if (mCurrentVipInfo == null) return;
        showUpdateVipInfoPrompt();
        break;
      case R.id.btn_vip_operation_charge://充值按钮
        //未登录不处理
        if (mCurrentVipInfo == null && mCurrentVipCardInfo == null) {
          Logger.v("未登录不处理");
          return;
        }
        modifyMemberMoney(mCurrentVipInfo != null);
        break;
    }
  }

  /**
   * 刷新 VipInfo 列表
   */
  //private void rushVipInfoList() {
  //  //通知会员列表 恢复未选中
  //  mAct.RecoverVipInfoListStatus();
  //  //刷新会员列表
  //  mAct.refreshVipList();
  //}

  /**
   * 初始显示 修改会员 提示
   */
  private void showUpdateVipInfoPrompt() {
    rl_name_modify.setClickable(true);
    rl_mobile_modify.setClickable(true);
    tvMemberModifyName.setVisibility(View.VISIBLE);
    tvMemberModifyMobile.setVisibility(View.VISIBLE);
  }

  /**
   * 不显示 修改会员提示
   */
  private void HideUpdateVipInfoPrompt() {
    rl_name_modify.setClickable(false);
    rl_mobile_modify.setClickable(false);
    tvMemberModifyName.setVisibility(View.GONE);
    tvMemberModifyMobile.setVisibility(View.GONE);
  }

  /**
   * 初始化  VipInfo和vipCardInfo
   */
  private void initReverseVipInfo(PxVipInfo vipInfo, PxVipCardInfo vipCardInfo,
      boolean isRefreshAdapter) {
    //冲销页隐藏下方按钮
    if (choice == 1) {
      mRlRechargeAndAgainBottom.setVisibility(View.GONE);
    } else {
      mRlRechargeAndAgainBottom.setVisibility(View.VISIBLE);
    }
    if (vipInfo != null) {
      //电话会员信息显示
      if (choice == 0) {
        mVipInfoOperationUpdate.setVisibility(View.VISIBLE);//修改
      }
      //显示 会员名
      tvMemberDetailName.setText(vipInfo.getName());
      //显示会员电话
      tvMemberDetailMobile.setText(vipInfo.getMobile());
      //会员 钱数
      tvMemberDetailMoney.setText(NumberFormatUtils.formatFloatNumber(vipInfo.getAccountBalance()));
      tvMemberScore.setText(0 + "");
      tvMemberRechargeDetailName.setText(vipInfo.getName());
      tvMemberRechargeDetailMobile.setText(vipInfo.getMobile());
      tvMemberRechargeBalance.setText(
          NumberFormatUtils.formatFloatNumber(vipInfo.getAccountBalance()));
      //会员积分 暂时不用
      tvMemberRechargeIntegral.setText(0 + "");
      if (isRefreshAdapter) {
        mRechargeRecordList = new ArrayList<>();
        if (vipRechargeRecordAdapter == null) {
          vipRechargeRecordAdapter = new VipRechargeRecordAdapter(mAct, mRechargeRecordList);
          vipRechargeRecordAdapter.notifyDataSetChanged();
        } else {
          vipRechargeRecordAdapter.setData(mRechargeRecordList);
        }
      }
    } else if (vipCardInfo != null) {
      //实体卡会员信息显示
      if (choice == 0) {
        mVipInfoOperationUpdate.setVisibility(View.GONE);//实体卡信息不可修改,隐藏修改按钮
      }
      //显示 会员名
      tvMemberDetailName.setText(vipCardInfo.getIdcardNum());
      //显示会员电话
      tvMemberDetailMobile.setText(vipCardInfo.getMobile());
      //会员 钱数
      tvMemberDetailMoney.setText(
          NumberFormatUtils.formatFloatNumber(vipCardInfo.getAccountBalance()));
      tvMemberScore.setText(vipCardInfo.getScore() + "");
      tvMemberRechargeDetailName.setText(vipCardInfo.getIdcardNum());
      tvMemberRechargeDetailMobile.setText(vipCardInfo.getMobile());
      tvMemberRechargeBalance.setText(
          NumberFormatUtils.formatFloatNumber(vipCardInfo.getAccountBalance()));
      //会员积分 暂时不用
      tvMemberRechargeIntegral.setText(vipCardInfo.getScore() + "");
      //是否刷新充值记录
      if (isRefreshAdapter) {
        mRechargeRecordList = new ArrayList<>();
        if (vipRechargeRecordAdapter == null) {
          vipRechargeRecordAdapter = new VipRechargeRecordAdapter(mAct, mRechargeRecordList);
          vipRechargeRecordAdapter.notifyDataSetChanged();
        } else {
          vipRechargeRecordAdapter.setData(mRechargeRecordList);
        }
      }
    }
  }

  /**
   * 可修改(名字,电话,等级)
   */
  @OnClick({ R.id.rl_name_modify, R.id.rl_mobile_modify }) public void memberModifyWhich(
      View view) {
    switch (view.getId()) {
      case R.id.rl_name_modify:
        modifyMemberName();
        break;
      case R.id.rl_mobile_modify:
        modifyMemberMobile();
        break;
    }
  }

  /**
   * 修改会员名字
   */
  private void modifyMemberName() {
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("会员名称")
        .customView(R.layout.layout_dialog_global, true)
        .build();
    View view = dialog.getCustomView();
    final TextInputEditText mEtInput = (TextInputEditText) view.findViewById(R.id.et_input);
    mEtInput.setInputType(InputType.TYPE_CLASS_TEXT |
        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
        InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    TextInputLayout til_input = (TextInputLayout) view.findViewById(R.id.text_input_layout);
    InputFilter[] filters = { new InputFilter.LengthFilter(6) };
    mEtInput.setFilters(filters);
    til_input.setHint("请修改会员电话");
    til_input.setCounterMaxLength(6);
    final TextView tvOk = (TextView) view.findViewById(R.id.tv_ok);
    TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
    tvOk.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        modifyVipName(dialog, mEtInput);
      }
    });
    tvCancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });
    dialog.show();
    addDialog(dialog);
  }

  /**
   * 更新会员名字
   */
  private void modifyVipName(MaterialDialog materialDialog, TextInputEditText mEtInput) {
    if (!checkCurrentVip()) return;
    String inputName = mEtInput.getText().toString().trim();
    if (TextUtils.isEmpty(inputName)) {
      ToastUtils.show("会员名称不能为空");
      return;
    } else if (!(inputName.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length()
        == 0)) {
      ToastUtils.show("会员名称不合法");
      return;
    }
    if (!NetUtils.isConnected(mAct)) {
      ToastUtils.show(mAct, "没有网络，请检查网络设置!", Toast.LENGTH_SHORT);
      return;
    }
    materialDialog.dismiss();
    updateSingleVipInfo(mCurrentVipInfo, inputName, mCurrentVipInfo.getMobile(), materialDialog);
  }

  /**
   * 修改会员 电话号码
   */
  private void modifyMemberMobile() {
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("会员电话")
        .customView(R.layout.layout_dialog_global, true)
        .build();
    View view = dialog.getCustomView();
    final TextInputEditText mEtInput = (TextInputEditText) view.findViewById(R.id.et_input);
    mEtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    InputFilter[] filters = { new InputFilter.LengthFilter(11) };
    mEtInput.setFilters(filters);
    TextInputLayout til_input = (TextInputLayout) view.findViewById(R.id.text_input_layout);
    til_input.setHint("请修改会员电话");
    til_input.setCounterMaxLength(11);
    final TextView tvOk = (TextView) view.findViewById(R.id.tv_ok);
    TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
    tvOk.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        modifyVipMobile(dialog, mEtInput);
      }
    });
    tvCancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });
    dialog.show();
    addDialog(dialog);
  }

  /**
   * 更改会员 电话号码
   */
  private void modifyVipMobile(MaterialDialog dialog, TextInputEditText mEtInput) {
    if (!checkCurrentVip()) return;
    String inputMobile = mEtInput.getText().toString().trim();
    if (TextUtils.isEmpty(inputMobile) || !RegExpUtils.match11Number(inputMobile)) {
      ToastUtils.show( "请输入有效的号码");
      return;
    }
    if (!NetUtils.isConnected(mAct)) {
      ToastUtils.show(mAct, "没有网络，请检查网络设置!", Toast.LENGTH_SHORT);
      return;
    }
    updateSingleVipInfo(mCurrentVipInfo, mCurrentVipInfo.getName(), inputMobile, dialog);
  }

  /**
   * 会员余额充值
   */
  private void modifyMemberMoney(final Boolean isPhoneVip) {
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("充值钱数")
        .customView(R.layout.layout_dialog_global, true)
        .canceledOnTouchOutside(false)
        .build();
    View view = dialog.getCustomView();
    final TextInputEditText mEtInput = (TextInputEditText) view.findViewById(R.id.et_input);
    mEtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    InputFilter[] filters = { new InputFilter.LengthFilter(6) };
    mEtInput.setFilters(filters);
    TextInputLayout til_input = (TextInputLayout) view.findViewById(R.id.text_input_layout);
    til_input.setHint("请填写充值金额");
    til_input.setCounterMaxLength(6);
    final TextView tvOk = (TextView) view.findViewById(R.id.tv_ok);
    TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
    tvOk.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        vipRecharge(dialog, mEtInput, isPhoneVip);
      }
    });
    tvCancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });
    dialog.show();
    addDialog(dialog);
  }

  /**
   * 充值
   */
  private void vipRecharge(MaterialDialog materialDialog, TextInputEditText mEtInput,
      Boolean isPhoneVip) {
    String inputMoney = mEtInput.getText().toString().trim();
    if (TextUtils.isEmpty(inputMoney)) {
      ToastUtils.show( "金额不能为空");
      return;
    } else if (!RegExpUtils.matchMoney(inputMoney)) {
      ToastUtils.show( "金额不正确");
      return;
    }
    materialDialog.dismiss();
    //获取充值计划信息
    getAllRechargePlan();
    if (isPhoneVip) {
      if (mRechargePlanList.size() > 0) {
        planList = new ArrayList<>();
        for (int i = 0; i < mRechargePlanList.size(); i++) {
          if (Double.parseDouble(inputMoney) >= mRechargePlanList.get(i).getMoney()) {
            planList.add(mRechargePlanList.get(i));
          }
        }
        if (planList != null) {
          plans = new String[planList.size()];
          for (int i = 0; i < planList.size(); i++) {
            String plan = planList.get(i).getName();
            plans[i] = plan;
          }
        }
        showVipPlanDialog(inputMoney);
      } else {
        showVipPlanDialog(inputMoney);
      }
    } else {
      checkVipRecharge(mCurrentVipInfo, mCurrentVipCardInfo, inputMoney, null);
    }
  }

  /**
   * 充值方案
   */
  private void showVipPlanDialog(final String inputMoney) {
    if (!checkCurrentVip()) return;
    if (inputMoney == null || inputMoney.isEmpty() || Double.valueOf(inputMoney) <= 0) {
      ToastUtils.show("请输入有效金额!");
      return;
    }
    if (plans != null && plans.length > 0) {
      MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("充值方案")
          .items(plans)
          .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
            @Override public boolean onSelection(MaterialDialog dialog, View view, int which,
                CharSequence charSequence) {
              return true;
            }
          })
          .onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
              //得到所选充值计划
              PxRechargePlan rechargePlan = getRechargePlan(dialog);
              checkVipRecharge(mCurrentVipInfo, mCurrentVipCardInfo, inputMoney, rechargePlan);
              //singleVipRecharge(mCurrentVipInfo, mCurrentVipCardInfo, inputMoney, rechargePlan);
            }
          })
          .positiveText("确认")
          .negativeText("取消")
          .negativeColor(mAct.getResources().getColor(R.color.primary_text))
          .neutralText("不选择充值方案")
          .onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
              checkVipRecharge(mCurrentVipInfo, mCurrentVipCardInfo, inputMoney, null);
            }
          })
          .canceledOnTouchOutside(false)
          .show();
      addDialog(dialog);
    } else {
      checkVipRecharge(mCurrentVipInfo, mCurrentVipCardInfo, inputMoney, null);
    }
  }

  /**
   * 核对充值钱数
   */
  private void checkVipRecharge(final PxVipInfo currentVipInfo,
      final PxVipCardInfo currentVipCardInfo, final String inputMoney,
      final PxRechargePlan rechargePlan) {
    String str = "   ";
    MaterialDialog.Builder dialog = new MaterialDialog.Builder(mAct).title("请核对充值钱数")
        .positiveText("确认")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            singleVipRecharge(currentVipInfo, currentVipCardInfo, inputMoney, rechargePlan);
            MaterialDialog mCheckVipRedialog = dialog;
          }
        })
        .canceledOnTouchOutside(false);
    //实体卡
    if (currentVipCardInfo != null) {
      dialog = dialog.content(
          "充值金额:" + inputMoney + str + "会员名称:" + currentVipCardInfo.getIdcardNum() + str + "会员电话:"
              + currentVipCardInfo.getMobile());
    } else {
      if (rechargePlan != null) {
        Double largess = rechargePlan.getLargess();
        if (largess > 0) {
          //有赠送金额
          dialog = dialog.content("充值金额:" + inputMoney + str + "赠送金额:" + largess + str + "会员名称:"
              + currentVipInfo.getName() + str + "会员电话:" + currentVipInfo.getMobile());
        } else {
          //无赠送金额
          dialog = dialog.content(
              "充值金额:" + inputMoney + str + "会员名称:" + currentVipInfo.getName() + str + "会员电话:"
                  + currentVipInfo.getMobile());
        }
      } else {
        //无充值方案
        dialog = dialog.content(
            "充值金额:" + inputMoney + str + "会员名称:" + currentVipInfo.getName() + str + "会员电话:"
                + currentVipInfo.getMobile());
      }
    }
    if (dialog != null) {
      MaterialDialog show = dialog.show();
      addDialog(show);
    }
  }

  /**
   * 获取选择的充值方案
   */
  private PxRechargePlan getRechargePlan(MaterialDialog dialog) {
    int selectedIndex = dialog.getSelectedIndex();
    return planList.get(selectedIndex);
  }

  /**
   * 会员和实体卡充值
   */
  private void singleVipRecharge(final PxVipInfo vipInfo, final PxVipCardInfo vipCardInfo,
      final String inputMoney, final PxRechargePlan pxRechargePlan) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.show( "操作有误，请重启App!");
      return;
    }
    //检查网络
    if (!NetUtils.isConnected(app)) {
      ToastUtils.show(mAct, "没有网络，请检查网络!", Toast.LENGTH_SHORT);
      return;
    }
    String companyCode = user.getCompanyCode();
    String userId = user.getObjectId();
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    String cid = office.getObjectId();
    if (vipCardInfo != null) {
      //实体卡充值
      HttpIdCardRechargeReq req = new HttpIdCardRechargeReq();
      req.setUserId(userId);
      req.setCompanyCode(companyCode);
      req.setCid(cid);
      req.setCardId(vipCardInfo.getObjectId());
      req.setMoney(Double.parseDouble(inputMoney));
      final MaterialDialog redialog = DialogUtils.showDialog(mAct, "充值", "充值中,请耐心等待!");
      new RestClient() {
        @Override protected void start() {

        }

        @Override protected void finish() {

        }

        @Override protected void failure(String responseString, Throwable throwable) {
          ToastUtils.show("会员充值失败");
          DialogUtils.dismissDialog(redialog);
        }

        @Override protected void success(String responseString) {
          Logger.json(responseString);
          HttpVipCardRechargeResp resp =
              getGson().fromJson(responseString, HttpVipCardRechargeResp.class);
          if (resp.getStatusCode() == 1) {
            String str = "会员充值成功!\n";
            str = str + "充值金额为:" + inputMoney;
            double newRechargeBalance =
                NumberFormatUtils.add(mCurrentVipCardInfo.getAccountBalance(),
                    Double.valueOf(inputMoney));
            mCurrentVipCardInfo.setAccountBalance(
                Double.valueOf(NumberFormatUtils.formatFloatNumber(newRechargeBalance)));
            //  }
            mCurrentVipCardInfo.setScore(mCurrentVipCardInfo.getScore() + resp.getScore());
            tvMemberDetailMoney.setText(
                NumberFormatUtils.formatFloatNumber(mCurrentVipCardInfo.getAccountBalance()));
            tvMemberScore.setText(mCurrentVipCardInfo.getScore() + "");
            ToastUtils.show(str);
            //设置是否打印会员消费信息
            PxSetInfo info = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
            if (info == null) {
              ToastUtils.show("未初始化设置信息");
              return;
            }
            if (PxSetInfo.AUTO_PRINT_RECHARGE_TRUE.equals(info.getIsAutoPrintRechargeVoucher())) {
              //新建vipInfo用于打印
              PxVipInfo vipInfo = new PxVipInfo();
              vipInfo.setMobile(vipCardInfo.getMobile());
              vipInfo.setObjectId(vipCardInfo.getObjectId());
              vipInfo.setName(vipCardInfo.getIdcardNum() + "(实体卡会员)");
              vipInfo.setAccountBalance(vipCardInfo.getAccountBalance());

              printVipRechargeRecord(vipInfo, inputMoney, pxRechargePlan);
            }
          }
          DialogUtils.dismissDialog(redialog);
        }
      }.postOther(mAct, URLConstants.VIP_CARD_RECHARGE, req);
    } else {
      //电话会员充值
      HttpSingleRechargeReq req = new HttpSingleRechargeReq();
      req.setUserId(userId);
      req.setCompanyCode(companyCode);
      req.setVipId(vipInfo.getObjectId());
      req.setMoney(Double.parseDouble(inputMoney));
      if (pxRechargePlan != null) {
        req.setPlanId(pxRechargePlan.getObjectId());
      }
      final MaterialDialog redialog = DialogUtils.showDialog(mAct, "充值", "充值中,请耐心等待!");
      addDialog(redialog);
      new RestClient() {
        @Override protected void start() {

        }

        @Override protected void finish() {

        }

        @Override protected void failure(String responseString, Throwable throwable) {
          ToastUtils.show("会员充值失败");
          DialogUtils.dismissDialog(redialog);
        }

        @Override protected void success(String responseString) {
          Logger.json(responseString);
          HttpResp resp = getGson().fromJson(responseString, HttpResp.class);
          if (resp.getStatusCode() == 1) {
            String str = "会员充值成功!\n";
            if (pxRechargePlan != null) {
              //Double money = pxRechargePlan.getMoney();
              Double largess = pxRechargePlan.getLargess();
              str = str + "充值金额为:" + inputMoney + "赠送金额:" + largess;
              double inputMoney1 = Double.parseDouble(inputMoney);
              double addMoney = NumberFormatUtils.add(inputMoney1, largess);
              double newRechargeBalance =
                  NumberFormatUtils.add(mCurrentVipInfo.getAccountBalance(), addMoney);
              mCurrentVipInfo.setAccountBalance(
                  Double.valueOf(NumberFormatUtils.formatFloatNumber(newRechargeBalance)));
            } else {
              str = str + "充值金额为:" + inputMoney;
              double newRechargeBalance = NumberFormatUtils.add(mCurrentVipInfo.getAccountBalance(),
                  Double.valueOf(inputMoney));
              mCurrentVipInfo.setAccountBalance(
                  Double.valueOf(NumberFormatUtils.formatFloatNumber(newRechargeBalance)));
            }
            tvMemberDetailMoney.setText(
                NumberFormatUtils.formatFloatNumber(mCurrentVipInfo.getAccountBalance()));
            tvMemberScore.setText("0");
            ToastUtils.show(str);
            //设置是否打印会员消费信息
            PxSetInfo info = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
            if (info == null) {
              ToastUtils.show("未初始化设置信息");
              return;
            }
            if (PxSetInfo.AUTO_PRINT_RECHARGE_TRUE.equals(info.getIsAutoPrintRechargeVoucher())) {
              printVipRechargeRecord(vipInfo, inputMoney, pxRechargePlan);
            }
          }
          DialogUtils.dismissDialog(redialog);
        }
      }.postOther(mAct, URLConstants.VIP_RECHARGE, req);
    }
  }

  /**
   * 打印会员充值记录
   *
   * @param vipInfo 要充值的会员
   * @param inputMoney 充值输入的钱数
   * @param pxRechargePlan 选择的充值方案 （可以为空）
   */
  private void printVipRechargeRecord(PxVipInfo vipInfo, String inputMoney,
      PxRechargePlan pxRechargePlan) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.show("操作有误，请重启App!");
      return;
    }
    //构造PxRechargeRecord
    PxRechargeRecord record = new PxRechargeRecord();
    record.setDelFlag("0");
    record.setRechargeTime(new Date());
    if (pxRechargePlan != null) {
      if (Double.parseDouble(inputMoney) >= pxRechargePlan.getMoney()) {
        record.setMoney(Double.parseDouble(inputMoney));
        record.setGiving(pxRechargePlan.getLargess());
      }
    } else {
      record.setMoney(Double.parseDouble(inputMoney));
      record.setGiving(0.0);
    }
    //用于再次打印
    recordPrint = record;
    vipInfoPrint = vipInfo;
    //网络打印 收银
    printByNetAndBT(vipInfo, record);
    //2-USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    try {
      if (mAppGpService.getGpService().getPrinterConnectStatus(1) == Constants.USB_CONNECT_STATUS) {
        printByUSBPrinter(record, vipInfo);
      } else {
        mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
        printByUSBPrinter(record, vipInfo);
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  private void printByNetAndBT(PxVipInfo vipInfo, PxRechargeRecord record) {
    PrinterTask task =
        new PrinterTask(BTPrintConstants.PRINT_MODE_VIP_RECHARGE_RECORD, record, vipInfo);
    PrintTaskManager.printCashTask(task);

    BTPrintTask btPrintTask = new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_VIP_RECHARGE_RECORD)
        .vipRechargeRecord(record)
        .vipInfo(vipInfo)
        .build();
    PrintEventManager.getManager().postBTPrintEvent(btPrintTask);
  }

  /**
   * USB打印 收银
   */
  private void printByUSBPrinter(PxRechargeRecord record, PxVipInfo vipInfo) {
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          if (vipInfo != null) {
            PrinterUsbData.printVipRechargeRecord(mAppGpService.getGpService(), vipInfo, record);
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
   * 修改会员 信息 (名字，电话)
   */
  private void updateSingleVipInfo(final PxVipInfo pxVipInfo, final String inputName,
      final String inputMobile, final MaterialDialog dialog) {
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.show("APP发生异常，请重新启动!");
      return;
    }
    String companyCode = user.getCompanyCode();
    String userId = user.getObjectId();
    HttpSingleVipInfoReq req = new HttpSingleVipInfoReq();
    req.setUserId(userId);
    req.setCompanyCode(companyCode);
    mVipNameCopy = pxVipInfo.getName();
    mVipMobileCopy = pxVipInfo.getMobile();
    pxVipInfo.setName(inputName);
    pxVipInfo.setMobile(inputMobile);
    req.setVipInfo(pxVipInfo);
    new RestClient() {
      @Override protected void start() {
        mAct.isShowProgress(true);
      }

      @Override protected void finish() {

      }

      @Override protected void failure(String responseString, Throwable throwable) {
        ToastUtils.show("用户信息修改失败");
        //mCurrentVipInfo.setName(mVipNameCopy);
        //tvMemberRechargeDetailName.setText(mVipNameCopy);
        //tvMemberDetailName.setText(mVipNameCopy);
        if (dialog.isShowing()) {
          dialog.dismiss();
        }
        mAct.isShowProgress(false);
      }

      @Override protected void success(String responseString) {
        HttpResp resp = getGson().fromJson(responseString, HttpResp.class);
        Logger.json(responseString);
        if (resp.getStatusCode() == 1) {
          ToastUtils.show("用户信息修改成功！");
          //电话
          mCurrentVipInfo.setMobile(inputMobile);
          tvMemberDetailMobile.setText(inputMobile);
          tvMemberRechargeDetailMobile.setText(inputMobile);
          //名字
          mCurrentVipInfo.setName(inputName);
          tvMemberRechargeDetailName.setText(inputName);
          tvMemberDetailName.setText(inputName);
          HideUpdateVipInfoPrompt();
          //保存起来
          mVipNameCopy = inputName;
          mVipMobileCopy = inputMobile;
        } else {
          //号码重复修改失败,恢复
          mCurrentVipInfo.setMobile(mVipMobileCopy);
          Logger.v(resp.getMsg() + "---" + mCurrentVipInfo.getName() + "---"
              + mCurrentVipInfo.getMobile());
          ToastUtils.show(resp.getMsg());
          //不显示 修改会员提示
          HideUpdateVipInfoPrompt();
        }
        if (dialog.isShowing()) {
          dialog.dismiss();
        }
        mAct.isShowProgress(false);
      }
    }.postOther(mAct, URLConstants.VIP_UPDATEINFO, req);
  }

  /**
   * 获取所有充值计划
   */
  private void getAllRechargePlan() {
    mRechargePlanList = new ArrayList<PxRechargePlan>();
    mRechargePlanList = DaoServiceUtil.getRechargePlanService()
        .queryBuilder()
        .where(PxRechargePlanDao.Properties.DelFlag.eq("0"))
        .list();
  }

  /**
   * 检测当前会员的有效性
   */
  private boolean checkCurrentVip() {
    if (mCurrentVipInfo == null) {
      return false;
    }
    if (mCurrentVipInfo.getObjectId() == null || mCurrentVipInfo.getObjectId().isEmpty()) {
      ToastUtils.show("当前会员未上传成功，暂不支持修改!");
      return false;
    }
    return true;
  }

  /**
   * 获取实体卡会员 的充值记录
   */
  private void getVipRechargeList(PxVipCardInfo pxVipCardInfo) {
    Logger.v("getVipRechargeList");
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.show("APP发生异常，请重新启动!");
      return;
    }
    String companyCode = user.getCompanyCode();
    String userId = user.getObjectId();
    HttpSingleVipCardReq req = new HttpSingleVipCardReq();
    req.setCompanyCode(companyCode);
    req.setUserId(userId);
    req.setCardId(pxVipCardInfo.getObjectId());
    req.setIsBoss("1");

    new RestClient(0, 1000, 15000, 5000) {

      @Override protected void start() {
        mAct.isShowProgress(true);
      }

      @Override protected void finish() {
      }

      @Override protected void failure(String responseString, Throwable throwable) {
        mAct.isShowProgress(false);
      }

      @Override protected void success(String responseString) {

        HttpRechargeRecordResp resp =
            getGson().fromJson(responseString, HttpRechargeRecordResp.class);
        if (resp.getStatusCode() == 1) {
          mRechargeRecordList = resp.getList();
          if (mRechargeRecordList == null) {
            mRechargeRecordList = new ArrayList<>();
          }
          vipRechargeRecordAdapter.setData(mRechargeRecordList);
        }
        mAct.isShowProgress(false);
      }
    }.postOther(mAct, URLConstants.VIP_CARD_RECHARGE_RECORD_LIST, req);
  }

  /**
   * 获取电话会员 的充值记录
   */
  private void getVipRechargeList(PxVipInfo pxVipInfo) {
    Logger.v("getVipRechargeList");
    App app = (App) App.getContext();
    User user = app.getUser();
    if (user == null) {
      ToastUtils.show("APP发生异常，请重新启动!");
      return;
    }
    String companyCode = user.getCompanyCode();
    String userId = user.getObjectId();
    HttpVipRechargeRecordListReq req = new HttpVipRechargeRecordListReq();
    req.setCompanyCode(companyCode);
    req.setUserId(userId);
    req.setVipInfo(pxVipInfo);

    new RestClient(0, 1000, 15000, 5000) {

      @Override protected void start() {
        mAct.isShowProgress(true);
      }

      @Override protected void finish() {
      }

      @Override protected void failure(String responseString, Throwable throwable) {
        mAct.isShowProgress(false);
      }

      @Override protected void success(String responseString) {

        HttpVipRechargeRecordListResp resp =
            getGson().fromJson(responseString, HttpVipRechargeRecordListResp.class);
        if (resp.getStatusCode() == 1) {
          mRechargeRecordList = resp.getList();
          if (mRechargeRecordList == null) {
            mRechargeRecordList = new ArrayList<>();
          }
          vipRechargeRecordAdapter.setData(mRechargeRecordList);
        }
        mAct.isShowProgress(false);
      }
    }.postOther(mAct, URLConstants.VIP_RECHARGE_RECORD_LIST, req);
  }

  /**
   * 查询充值记录
   */
  private void queryVipRechargeRecord(PxVipInfo vipInfo, PxVipCardInfo vipCardInfo) {
    if (vipInfo != null) {
      getVipRechargeList(vipInfo);
    } else if (vipCardInfo != null) {
      getVipRechargeList(vipCardInfo);
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
    if (OpenPortEvent.VIP_RECHARGE_PORT.equals(event.getType())) {
      //Gp是否支持USB打印
      String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
      if (isSupportUSBPrint.equals("1")) return;
      //是否已配置开启USB打印
      boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
      if (isPrint) {
        try {
          int type = mAppGpService.getGpService().getPrinterCommandType(1);
          if (type == GpCom.ESC_COMMAND) {
            if (vipInfoPrint != null) {
              PrinterUsbData.printVipRechargeRecord(mAppGpService.getGpService(), vipInfoPrint,
                  recordPrint);
            }
          }
        } catch (RemoteException e) {
          e.printStackTrace();
        }
      } else {
        mAct.runOnUiThread(new Runnable() {
          @Override public void run() {
            ToastUtils.show("设备未连接,请在更多模块配置普通打印机!");
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
   * 查询会员信息和显示
   * 由QueryMemberFragment发送
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void onReceiveMemberFuzzyQueryEvent(
      VipInfoListEvent event) {
    //登录
    String likeName = event.getLikeName();
    mAct.isShowProgress(true);
    VipLogin.vipLogin(likeName, null);
  }

  /**
   * 接收会员登录结果
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveVipLogin(VipLoginEvent event) {
    //关闭蒙层
    mAct.isShowProgress(false);

    if (event.isSuccess()) {
      PxPaymentMode paymentMode = event.getPaymentMode();
      if (paymentMode != null) {
        return;
      }
      Logger.v("success");
      PxVipInfo mVipInfo = event.getVipInfo();
      PxVipCardInfo mVipCardInfo = event.getVipCardInfo();
      mCurrentVipInfo = mVipInfo;
      mCurrentVipCardInfo = mVipCardInfo;
      initReverseVipInfo(mCurrentVipInfo, mCurrentVipCardInfo, true);
      //不显示 修改会员提示
      HideUpdateVipInfoPrompt();
      switch (choice) {
        case 0:
          break;
        case 1:
          //获取充值记录
          queryVipRechargeRecord(mCurrentVipInfo, mCurrentVipCardInfo);
          break;
      }
    }
  }
}
