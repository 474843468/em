package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import com.kyleduo.switchbutton.SwitchButton;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.AutoOrderEvent;
import com.psi.easymanager.event.ResetAutoOrderEvent;
import com.psi.easymanager.module.PxSetInfo;
import com.psi.easymanager.ui.activity.MoreActivity;
import com.psi.easymanager.utils.ToastUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by zjq on 2016/3/17.
 * 更多-商业模式
 */
public class BusinessModelFragment extends BaseFragment
    implements CompoundButton.OnCheckedChangeListener {
  SwitchButton sbFastOpenBill;//是否快速开单
  SwitchButton sbAutoSwitchOverBill;//是否自动切换到结账页面
  SwitchButton sbAutoOrder;//商品添加后自动下单
  SwitchButton sbOverAutoStartBill;//结账完毕自动开单
  SwitchButton sbVipRechargeConsumePrintVoucher;//会员充值消费是否打印凭证
  SwitchButton sbFinancePrintCategory;//财务联是否打印分类统计信息

  private static final String BUSINESS_MODEL_FRAGMENT_PARAM = "param";
  private String mParam;//MainActivity参数
  private MoreActivity mAct;

  private PxSetInfo mSetInfo;//数据库查询出的设置信息

  public static BusinessModelFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    BusinessModelFragment fragment = new BusinessModelFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam = getArguments().getString(BUSINESS_MODEL_FRAGMENT_PARAM);
    }
    mAct = (MoreActivity) getActivity();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_business_model, null);
    initSwitchBtns(view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    //连接RB状态
    initViewStatus();
    //监听按钮SwitchButton选择事件
    setSwitchBtnsListener();
  }

  //@formatter:off
  /**
   * set checkChangeListener
   */
  private void setSwitchBtnsListener() {
    sbFastOpenBill.setOnCheckedChangeListener(this);
    sbAutoSwitchOverBill.setOnCheckedChangeListener(this);
    sbAutoOrder.setOnCheckedChangeListener(this);
    sbOverAutoStartBill.setOnCheckedChangeListener(this);
    sbVipRechargeConsumePrintVoucher.setOnCheckedChangeListener(this);
    sbFinancePrintCategory.setOnCheckedChangeListener(this);
  }
  /**
  * 初始化开关
   */
  private void initSwitchBtns(View view) {
    sbFastOpenBill = (SwitchButton) view.findViewById(R.id.sb_fast_open_bill);
    sbAutoSwitchOverBill = (SwitchButton) view.findViewById(R.id.sb_auto_switch_over_bill);
    sbAutoOrder = (SwitchButton) view.findViewById(R.id.sb_auto_order);
    sbOverAutoStartBill = (SwitchButton) view.findViewById(R.id.sb_over_auto_start_bill);
    sbVipRechargeConsumePrintVoucher = (SwitchButton) view.findViewById(R.id.sb_vip_recharge_consume_print_voucher);
    sbFinancePrintCategory = (SwitchButton) view.findViewById(R.id.sb_finance_print_category);
  }
  //@formatter:ff

  /**
   * 初始化控件显示状态
   */
  private void initViewStatus() {
    PxSetInfo info = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
    if (info == null) {
      ToastUtils.showShort(App.getContext(), "未初始化设置信息");
      return;
    }
    //是否快速开单
    if (PxSetInfo.FAST_START_ORDER_TRUE.equals(info.getIsFastOpenOrder())) {
      sbFastOpenBill.setChecked(true);
    } else {
      sbFastOpenBill.setChecked(false);
    }
    //是否自动切换到结账页面
    if (PxSetInfo.AUTO_TURN_CHECKOUT_TRUE.equals(info.getIsAutoTurnCheckout())) {
      sbAutoSwitchOverBill.setChecked(true);
    } else {
      sbAutoSwitchOverBill.setChecked(false);
    }
    //商品添加后自动下单
    if (PxSetInfo.AUTO_ORDER_TRUE.equals(info.getAutoOrder())) {
      sbAutoOrder.setChecked(true);
    } else {
      sbAutoOrder.setChecked(false);
    }
    //结账完毕自动开单
    if (PxSetInfo.OVER_AUTO_START_BILL_TRUE.equals(info.getOverAutoStartBill())) {
      sbOverAutoStartBill.setChecked(true);
    } else {
      sbOverAutoStartBill.setChecked(false);
    }
    //结账完毕自动开单
    if (PxSetInfo.OVER_AUTO_START_BILL_TRUE.equals(info.getOverAutoStartBill())) {
      sbOverAutoStartBill.setChecked(true);
    } else {
      sbOverAutoStartBill.setChecked(false);
    }
    //会员充值自动打印
    if (PxSetInfo.AUTO_PRINT_RECHARGE_TRUE.equals(info.getIsAutoPrintRechargeVoucher())) {
      sbVipRechargeConsumePrintVoucher.setChecked(true);
    } else {
      sbVipRechargeConsumePrintVoucher.setChecked(false);
    }
    //财务联打印分类统计信息
    if (PxSetInfo.FINANCE_PRINT_CATEGORY_TRUE.equals(info.getIsFinancePrintCategory())) {
      sbFinancePrintCategory.setChecked(true);
    } else {
      sbFinancePrintCategory.setChecked(false);
    }
  }

  /**
   * 配置参数
   */
  @Override public void onCheckedChanged(CompoundButton sb, boolean isChecked) {
    mSetInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
    if (mSetInfo == null) return;
    switch (sb.getId()) {
      case R.id.sb_fast_open_bill:
        if (isChecked) {
          mSetInfo.setIsFastOpenOrder(PxSetInfo.FAST_START_ORDER_TRUE);
        } else {
          mSetInfo.setIsFastOpenOrder(PxSetInfo.FAST_START_ORDER_FALSE);
        }
        DaoServiceUtil.getSetInfoService().saveOrUpdate(mSetInfo);
        break;
      case R.id.sb_auto_switch_over_bill:
        if (isChecked) {
          mSetInfo.setIsAutoTurnCheckout(PxSetInfo.AUTO_TURN_CHECKOUT_TRUE);
        } else {
          mSetInfo.setIsAutoTurnCheckout(PxSetInfo.AUTO_TURN_CHECKOUT_FALSE);
        }
        DaoServiceUtil.getSetInfoService().saveOrUpdate(mSetInfo);
        break;
      case R.id.sb_auto_order:
        if (isChecked) {
          mSetInfo.setAutoOrder(PxSetInfo.AUTO_ORDER_TRUE);
          EventBus.getDefault().post(new AutoOrderEvent().setAutoOrder(true));
        } else {
          mSetInfo.setAutoOrder(PxSetInfo.AUTO_ORDER_FALSE);
          EventBus.getDefault().post(new AutoOrderEvent().setAutoOrder(false));
        }
        DaoServiceUtil.getSetInfoService().saveOrUpdate(mSetInfo);
        EventBus.getDefault().post(new ResetAutoOrderEvent());
        break;
      case R.id.sb_over_auto_start_bill:
        if (isChecked) {
          mSetInfo.setOverAutoStartBill(PxSetInfo.OVER_AUTO_START_BILL_TRUE);
          sbFastOpenBill.setChecked(true);
          mSetInfo.setIsFastOpenOrder(PxSetInfo.FAST_START_ORDER_TRUE);
        } else {
          mSetInfo.setOverAutoStartBill(PxSetInfo.OVER_AUTO_START_BILL_FALSE);
        }
        DaoServiceUtil.getSetInfoService().saveOrUpdate(mSetInfo);
        break;
      case R.id.sb_vip_recharge_consume_print_voucher:
        if (isChecked) {
          mSetInfo.setIsAutoPrintRechargeVoucher(PxSetInfo.AUTO_PRINT_RECHARGE_TRUE);
        } else {
          mSetInfo.setIsAutoPrintRechargeVoucher(PxSetInfo.AUTO_PRINT_RECHARGE_FALSE);
        }
        DaoServiceUtil.getSetInfoService().saveOrUpdate(mSetInfo);
        break;
      case R.id.sb_finance_print_category:
        if (isChecked) {
          mSetInfo.setIsFinancePrintCategory(PxSetInfo.FINANCE_PRINT_CATEGORY_TRUE);
        } else {
          mSetInfo.setIsFinancePrintCategory(PxSetInfo.FINANCE_PRINT_CATEGORY_FALSE);
        }
        DaoServiceUtil.getSetInfoService().saveOrUpdate(mSetInfo);
    }
  }
}

