package com.psi.easymanager.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.gprinter.command.GpCom;
import com.gprinter.io.PortParameters;
import com.kyleduo.switchbutton.SwitchButton;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.BuyCouponAdapter;
import com.psi.easymanager.adapter.CouponAdapter;
import com.psi.easymanager.adapter.PaymentModeAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxBuyCouponsDao;
import com.psi.easymanager.dao.PxDiscounSchemeDao;
import com.psi.easymanager.dao.PxExtraDetailsDao;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.PxPaymentModeDao;
import com.psi.easymanager.dao.PxTableExtraRelDao;
import com.psi.easymanager.dao.PxVoucherDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.FindBillRefreshStatusEvent;
import com.psi.easymanager.event.OnLinePaySuccessEvent;
import com.psi.easymanager.event.OpenPortEvent;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.ResetAutoOrderEvent;
import com.psi.easymanager.event.RevokeOrFinishBillEvent;
import com.psi.easymanager.event.ScanCodeEvent;
import com.psi.easymanager.event.SpeechEvent;
import com.psi.easymanager.event.VipConsumeEvent;
import com.psi.easymanager.event.VipLoginEvent;
import com.psi.easymanager.module.AppPayInfoCollect;
import com.psi.easymanager.module.EPaymentInfo;
import com.psi.easymanager.module.PxBuyCoupons;
import com.psi.easymanager.module.PxDiscounScheme;
import com.psi.easymanager.module.PxExtraCharge;
import com.psi.easymanager.module.PxExtraDetails;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxPaymentMode;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxSetInfo;
import com.psi.easymanager.module.PxTableExtraRel;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.PxVipCardInfo;
import com.psi.easymanager.module.PxVipCardType;
import com.psi.easymanager.module.PxVipInfo;
import com.psi.easymanager.module.PxVoucher;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.operatedialog.ClearDiscAndPayInfoDialog;
import com.psi.easymanager.operatedialog.EntiretyDiscDialog;
import com.psi.easymanager.operatedialog.PartDiscDialog;
import com.psi.easymanager.operatedialog.SchemeDiscDialog;
import com.psi.easymanager.operatedialog.VipLoginDialog;
import com.psi.easymanager.pay.alipay.Alipay;
import com.psi.easymanager.pay.bestpay.BestPay;
import com.psi.easymanager.pay.help.OptOrderLock;
import com.psi.easymanager.pay.query.QueryNetPayRecord;
import com.psi.easymanager.pay.vip.VipCardConsume;
import com.psi.easymanager.pay.vip.VipConsume;
import com.psi.easymanager.pay.wxpay.WxPay;
import com.psi.easymanager.print.PrintEventManager;
import com.psi.easymanager.print.bt.BTPrintTask;
import com.psi.easymanager.print.constant.BTPrintConstants;
import com.psi.easymanager.print.module.AppCustomAmount;
import com.psi.easymanager.print.module.AppFinanceAmount;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.print.net.PrinterTask;
import com.psi.easymanager.print.usb.AppGpService;
import com.psi.easymanager.print.usb.AppUsbDeviceName;
import com.psi.easymanager.print.usb.PrinterUsbData;
import com.psi.easymanager.ui.activity.MainActivity;
import com.psi.easymanager.ui.activity.ScanPayActivity;
import com.psi.easymanager.upload.UpLoadOrder;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.IOUtils;
import com.psi.easymanager.utils.NetUtils;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.SPUtils;
import com.psi.easymanager.utils.ToastUtils;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import hdx.HdxUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.view.View.OnClickListener;

/**
 * Created by dorado on 2016/5/28.
 */
public class CheckOutFragment extends BaseFragment {
  //MainActivity
  private MainActivity mAct;
  //接续收款页面
  @Bind(R.id.layout_check_out_continue_pay) FrameLayout mFlContinuePay;
  //支付方式rcv
  @Bind(R.id.lv_payment_mode) ListView mLvPaymentMode;
  //功能页面
  @Bind(R.id.rl_functions) RelativeLayout mRlFunctions;
  //账单概述页面
  @Bind(R.id.ll_overview) LinearLayout mLlOverview;

  //现金结账页面
  @Bind(R.id.layout_check_out_cash) ViewStub mCashViewStub;
  //延迟加载的现金结账视图
  private View mCashView;
  //现金付款 取消
  private TextView mTvPayCashCancel;
  //现金付款 确认
  private TextView mTvPayCashConfirm;
  //现金付款 应收
  private TextView mTvPayCashReceivable;
  //现金付款 实收
  private TextView mTvPayCashReceived;
  //现金付款 找零
  private TextView mTvPayCashChange;
  //现金付款 标题
  private TextView mTvPayCashTitle;

  //银行卡结账页面
  @Bind(R.id.layout_check_out_pos) ViewStub mPosViewStub;
  //延迟加载的银行卡结账视图
  private View mPosView;
  //银行付款 取消
  private TextView mTvPayPosCancel;
  //银行付款 确认
  private TextView mTvPayPosConfirm;
  //银行付款 实收
  private TextView mTvPayPosReceived;
  //银行付款 凭证
  private EditText mEtPayPosVoucher;
  //银行付款 标题
  private TextView mTvPayPosTitle;

  //支付宝、微信结账
  @Bind(R.id.layout_check_out_third) ViewStub mThirdViewStub;
  //延迟加载的第三方结账视图
  private View mThirdView;
  //第三方付款 实收
  private TextView mTvPayThirdReceived;
  //第三方付款 付款码
  private EditText mEtPayThirdPaycode;
  //第三方付款 标题
  private TextView mTvPayThirdTitle;

  //免单
  @Bind(R.id.layout_check_out_free) ViewStub mFreeViewStub;
  //延迟加载的免单结账视图
  private View mFreeView;
  //免单付款 取消
  private TextView mTvPayFreeCancel;
  //免单付款 确认
  private TextView mTvPayFreeConfirm;
  //免单付款 实收
  private TextView mTvPayFreeReceived;
  //免单付款 原因
  private EditText mEtPayFreeReason;
  //免单付款 标题
  private TextView mTvPayFreeTitle;

  //其它
  @Bind(R.id.layout_check_out_other) ViewStub mOtherViewStub;
  //延迟加载的其它结账视图
  private View mOtherView;
  //其它付款 取消
  private TextView mTvPayOtherCancel;
  //其它付款 确认
  private TextView mTvPayOtherConfirm;
  //其它付款 应收
  private TextView mTvPayOtherReceivable;
  //其它付款 实收
  private TextView mTvPayOtherReceived;
  //其它付款 标题
  private TextView mTvPayOtherTitle;

  //优惠券
  @Bind(R.id.layout_check_out_coupon) ViewStub mCouponViewStub;
  //延迟加载的优惠券结账视图
  private View mCouponView;
  //优惠券 列表
  private ListView mLvCoupon;
  //优惠券款 取消
  private TextView mTvPayCouponCancel;
  //优惠券付款 确认
  private TextView mTvPayCouponConfirm;
  //优惠券付款 标题
  private TextView mTvPayCouponTitle;
  //优惠券列表
  private List<PxVoucher> mVoucherList;

  //团购券
  @Bind(R.id.layout_check_out_group_coupon) ViewStub mGroupCouponViewStub;
  //延迟加载的团购券视图
  private View mGroupCouponView;
  //团购券列表
  private ListView mLvGroupCoupon;
  //团购券付款 取消
  private TextView mTvPayGroupCouponCancel;
  //团购券付款 确认
  private TextView mTvPayGroupCouponConfirm;
  //团购券付款 标题
  private TextView mTvPayGroupCouponTitle;
  //团购券付款 门市价
  private TextView mTvPayGroupCouponStoreAmount;
  //团购券付款 实收
  private TextView mTvPayGroupCouponReceivedAmount;
  //团购券付款 验券码
  private EditText mEtPayGroupCouponCode;
  //团购券列表
  private List<PxBuyCoupons> mBuyCouponsList;

  //优惠买单
  @Bind(R.id.layout_check_out_privilege) ViewStub mPrivilegeViewStub;
  //延迟加载的优惠买单视图
  private View mPrivilegeView;
  //优惠买单 标题
  private TextView mTvPayPrivilegeTitle;
  //优惠买单 应收
  private TextView mTvPayPrivilegeReceivable;
  //优惠买单 实收
  private TextView mTvPayPrivilegeReceived;
  //优惠买单 优惠
  private TextView mTvPayPrivilegeAmount;
  //优惠买单 取消
  private TextView mTvPayPrivilegeCancel;
  //优惠买单 确认
  private TextView mTvPayPrivilegeConfirm;

  /**
   * 金额统计界面
   */
  //合计数量
  @Bind(R.id.tv_nums) TextView mTvNums;
  //合计金额
  @Bind(R.id.tv_total_amount) TextView mTvTotalAmount;
  //应收金额
  @Bind(R.id.tv_total_receivable_amount) TextView mTvTotalReceivable;
  //消费金额
  @Bind(R.id.tv_total_consume_amount) TextView mTvTotalConsume;
  //优惠金额
  @Bind(R.id.tv_total_disc_amount) TextView mTvTotalDisc;
  //附加费金额
  @Bind(R.id.tv_total_extra_amount) TextView mTvTotalExtra;
  //总实收金额
  @Bind(R.id.tv_total_received_amount) TextView mTvTotalReceived;
  //总找零金额
  @Bind(R.id.tv_total_change_amount) TextView mTvTotalChange;
  //支付类优惠
  @Bind(R.id.tv_total_pay_privilege_amount) TextView mTvTotalPayPrivilege;

  //还需付款
  @Bind(R.id.tv_wait_pay) TextView mTvWaitPay;

  //合计数量
  private int mTotalNum = 0;
  //合计金额
  private double mTotalAmount = 0;
  //应收金额
  private double mReceivableAmount = 0;
  //总实收
  private double mTotalReceived = 0;
  //总找零
  private double mTotalChange = 0;
  //消费金额
  private double mConsumeAmount = 0;
  //优惠金额
  private double mDiscAmount = 0;
  //附加费金额
  private double mExtraAmount = 0;
  //还需付款
  private double mWaitPayAmount = 0;
  //当前实收
  private double mCurrentReceived = 0;
  //当前找零
  private double mCurrentChange = 0;
  //总支付类优惠
  private double mTotalPayPrivilege = 0;

  //现金实收StringBuilder
  private StrBuilder mSbCashReceived = new StrBuilder();
  //Fragment管理
  private FragmentManager mFm;
  //当前OrderInfo
  private PxOrderInfo mCurrentOrderInfo;
  //汇总信息
  private List<PxOrderDetails> mDetailsList;

  //客单页面
  private CashBillFragment mCashBillFragment;
  //菜单页面
  private Fragment mCashMenuFragment;

  //Gp服务
  private AppGpService mAppGpService;
  //USB设备名
  private String mDeviceName;
  //总价,应收等等(供客户联打印使用)
  private AppCustomAmount mCustomAmount;
  //总价,应收等等(供财务联打印使用)
  private AppFinanceAmount mFinanceAmount;
  //支付方式 列表
  private List<PxPaymentMode> mPaymentModeList;
  //当前支付方式
  private PxPaymentMode mCurrentPaymentMode;
  //共用缓存线程池  用于usb补打、HDX设备开钱箱、结账完毕订单及时上传
  private ExecutorService mCommonService = null;
  //用于打印会员消费实收
  private double receivePrint = 0;
  //用于打印会员消费vip
  private PxVipInfo vipInfoPrint;
  private PxPaymentMode mPaymentMode;
  //会员登录对话框
  private VipLoginDialog mVipLoginDialog;
  private String mType;
  private TextInputEditText mEtInput;

  public static CheckOutFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    CheckOutFragment fragment = new CheckOutFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (MainActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    mCashBillFragment = (CashBillFragment) mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
    if (savedInstanceState != null) {
      mCashMenuFragment = mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_check_out, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(AppGpService.class);
    //获取支付方式
    loadPayment();
    //HDX065开钱箱用、USB补打、及上传结账完毕订单
    mCommonService = Executors.newCachedThreadPool();
  }

  /**
   * 退出
   */

  @Override public void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
    if (mCommonService != null) {
      mCommonService.shutdown();
    }
  }

  /**
   * 加载支付方式
   */
  private void loadPayment() {
    ////支付方式list
    //mPaymentModeList = DaoServiceUtil.getPaymentModeService()
    //    .queryBuilder()
    //    .where(PxPaymentModeDao.Properties.DelFlag.eq("0"))
    //    .orderAsc(PxPaymentModeDao.Properties.OrderNo)
    //    .where(PxPaymentModeDao.Properties.Type.notEq(PxPaymentMode.TYPE_WINGPAY), PxPaymentModeDao.Properties.Type.notEq(PxPaymentMode.TYPE_TAIL))
    //    .list();

    //支付方式list
    mPaymentModeList = new ArrayList<>();
    //默认支付方式
    mCurrentPaymentMode = DaoServiceUtil.getPaymentModeService()
        .queryBuilder()
        .where(PxPaymentModeDao.Properties.Type.eq(PxPaymentMode.TYPE_CASH))
        .where(PxPaymentModeDao.Properties.Edit.eq(PxPaymentMode.EDIT_FALSE))
        .unique();
    mPaymentModeList.add(mCurrentPaymentMode);
    //不可编辑
    List<PxPaymentMode> couldNotEditPaymentModeList = DaoServiceUtil.getPaymentModeService()
        .queryBuilder()
        .where(PxPaymentModeDao.Properties.DelFlag.eq("0"))
        .where(PxPaymentModeDao.Properties.Edit.eq(PxPaymentMode.EDIT_FALSE))
        .where(PxPaymentModeDao.Properties.Type.notEq(PxPaymentMode.TYPE_CASH),
            PxPaymentModeDao.Properties.Type.notEq(PxPaymentMode.TYPE_WINGPAY),
            PxPaymentModeDao.Properties.Type.notEq(PxPaymentMode.TYPE_TAIL))
        .orderAsc(PxPaymentModeDao.Properties.OrderNo)
        .list();
    mPaymentModeList.addAll(couldNotEditPaymentModeList);
    //可编辑的
    List<PxPaymentMode> couldEditPaymentModeList = DaoServiceUtil.getPaymentModeService()
        .queryBuilder()
        .where(PxPaymentModeDao.Properties.DelFlag.eq("0"))
        .where(PxPaymentModeDao.Properties.Edit.eq(PxPaymentMode.EDIT_TRUE))
        .where(
            //PxPaymentModeDao.Properties.Type.notEq(PxPaymentMode.TYPE_WINGPAY),
            PxPaymentModeDao.Properties.Type.notEq(PxPaymentMode.TYPE_TAIL))
        .orderAsc(PxPaymentModeDao.Properties.OrderNo)
        .list();
    mPaymentModeList.addAll(couldEditPaymentModeList);

    PaymentModeAdapter paymentModeAdapter = new PaymentModeAdapter(mAct, mPaymentModeList);
    mLvPaymentMode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPaymentMode = mPaymentModeList.get(position);
        if (mPaymentMode.getType() == null) return;
        //当前支付方式
        mCurrentPaymentMode = mPaymentMode;
        //校验
        if (mCurrentOrderInfo == null) {
          ToastUtils.show("暂无订单");
          closePayView();
          return;
        }
        if (mDetailsList == null || mDetailsList.size() == 0) {
          ToastUtils.show("暂无商品信息");
          closePayView();
          return;
        }
        switch (mPaymentMode.getType()) {
          case PxPaymentMode.TYPE_VIP://会员
            mVipLoginDialog = new VipLoginDialog();
            mVipLoginDialog.showVipLoginDialog(CheckOutFragment.this, mAct, mPaymentMode);
            break;
          case PxPaymentMode.TYPE_ALIPAY://支付宝
            payThird();
            onKeyDown();
            break;
          case PxPaymentMode.TYPE_WEIXIN://微信
            payThird();
            onKeyDown();
            break;
          case PxPaymentMode.TYPE_WINGPAY://翼支付
            payThird();
            onKeyDown();
            break;
          case PxPaymentMode.TYPE_POS://银行卡
            payPos();
            break;
          case PxPaymentMode.TYPE_VOUCHER://代金券
            payCoupon();
            break;
          case PxPaymentMode.TYPE_CASH://现金
            payCash();
            break;
          case PxPaymentMode.TYPE_OTHER://其它
            payOther();
            break;
          case PxPaymentMode.TYPE_FREE://免单
            payFree();
            break;
          case PxPaymentMode.TYPE_ON_ACCOUNT://挂账

            break;
          case PxPaymentMode.TYPE_GROUP_COUPON://团购券
            payGroupCoupon();
            break;
          case PxPaymentMode.TYPE_PRIVILEGE://优惠买单
            payPrivilege();
            break;
        }
      }
    });
    mLvPaymentMode.setAdapter(paymentModeAdapter);
    mLvPaymentMode.setItemChecked(0, true);
  }

  /**
   * 打开账单统计
   */
  @OnClick(R.id.btn_open_overview) public void openOverview() {
    performOverviewAnim(mRlFunctions, mLlOverview);
  }

  private void performOverviewAnim(final View outView, final View inView) {
    final int width = outView.getWidth();
    ObjectAnimator outAnim =
        ObjectAnimator.ofFloat(outView, "translationX", 0, -width).setDuration(300);
    final ObjectAnimator inAnim =
        ObjectAnimator.ofFloat(inView, "translationX", -width, 0).setDuration(300);
    outAnim.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        outView.setVisibility(View.GONE);
        inView.setVisibility(View.VISIBLE);
        inAnim.start();
      }
    });
    outAnim.start();
  }

  /**
   * 关闭账单概述
   */
  @OnClick(R.id.tv_close_overview) public void closeOverview() {
    performOverviewAnim(mLlOverview, mRlFunctions);
  }

  /**
   * 更新支付信息
   */
  //@formatter:off
  public void updatePayInfo() {
    //统计信息
    mDetailsList = CashBillFragment.mDetailsList;
    if (mDetailsList == null || mCurrentOrderInfo == null) return;
    //重置 基础信息
    if (mDetailsList.size() == 0) {
      resetPayInfo();
      return;
    }
    //更新附加费信息
    if (mCurrentOrderInfo != null && mCurrentOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE) && mCurrentOrderInfo.getDbCurrentExtra() != null) {
      updateExtraInfo();
    }
    //更新支付信息
    updatePayInfoData();
  }

  /**
   * 更新附加费信息
   */
  //@formatter:off
  private void updateExtraInfo() {
    //桌台
    TableOrderRel rel = DaoServiceUtil.getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
        .unique();
    PxTableInfo dbTable = rel.getDbTable();
    //附加费关联
    PxTableExtraRel tableExtraRel = DaoServiceUtil.getTableExtraRelService()
        .queryBuilder()
        .where(PxTableExtraRelDao.Properties.PxTableInfoId.eq(dbTable.getId()))
        .where(PxTableExtraRelDao.Properties.DelFlag.eq("0"))
        .unique();
    if (tableExtraRel == null) return;
    //当前所用附加费
    PxExtraCharge dbExtraCharge = tableExtraRel.getDbExtraCharge();
    if (dbExtraCharge == null || PxExtraCharge.ENABLE_FALSE.equals(dbExtraCharge.getServiceStatus())) return;
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //当前订单的附加费详情
      PxExtraDetails currentExtra = mCurrentOrderInfo.getDbCurrentExtra();
      //当前时间转为分钟
      long minutes = new Date().getTime() / 1000 / 60;
      //需要计算附加费的时间
      long extraMinutes;
      //如果之前没换过桌
      if (mCurrentOrderInfo.getLastMoveTableTime() == null) {
        long startOrderMinutes = mCurrentOrderInfo.getStartTime().getTime() / 1000 / 60;
        extraMinutes = minutes - startOrderMinutes;
      } else {
        long lastMoveMinutes = mCurrentOrderInfo.getLastMoveTableTime().getTime() / 1000 / 60;
        extraMinutes = minutes - lastMoveMinutes;
      }
      //附加费价格
      double currentExtraPrice = Math.ceil((double) extraMinutes / dbExtraCharge.getMinutes() * dbExtraCharge.getServiceCharge().doubleValue());
      //设置给当前附加费
      currentExtra.setPrice(currentExtraPrice);
      //储存
      DaoServiceUtil.getExtraDetailsService().saveOrUpdate(currentExtra);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  private void updatePayInfoData() {
    //客户联打印
    mCustomAmount = new AppCustomAmount();
    //财务联打印
    mFinanceAmount = new AppFinanceAmount();

    //合计数量
    mTotalNum = 0;
    //合计金额
    mTotalAmount = 0;
    //应收金额
    mReceivableAmount = 0;
    //消费金额
    mConsumeAmount = 0;
    //优惠金额
    mDiscAmount = 0;
    //附加费金额
    mExtraAmount = 0;
    //还需付款金额
    mWaitPayAmount = 0;
    //实收
    mTotalReceived = 0;
    //找零
    mTotalChange = 0;
    //支付类优惠
    mTotalPayPrivilege = 0;
    //如果为没有统计信息，清空显示
    if (mDetailsList == null || mDetailsList.size() == 0) {
      resetPayInfo();
      return;
    }
    //配置信息
    PxSetInfo pxSetInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
    //自动快速开单
    if (pxSetInfo != null && PxSetInfo.AUTO_ORDER_TRUE.equals(pxSetInfo.getAutoOrder())) {
      for (PxOrderDetails details : mDetailsList) {
        //已下单或未下单
        if (PxOrderDetails.ORDER_STATUS_ORDER.equals(details.getOrderStatus()) || PxOrderDetails.ORDER_STATUS_UNORDER.equals(details.getOrderStatus())){
          //合计数量
          mTotalNum += 1;
          //合计金额
          mTotalAmount += details.getPrice();
          //应收金额
          mReceivableAmount += details.getReceivablePrice();
          //消费金额
          mConsumeAmount += details.getPrice();
          //优惠金额
          mDiscAmount += details.getDiscPrice();
        }
      }
    } else {
      for (PxOrderDetails details : mDetailsList) {
        //已下单
        if (PxOrderDetails.ORDER_STATUS_ORDER.equals(details.getOrderStatus())) {
          //合计数量
          mTotalNum += 1;
          //合计金额
          mTotalAmount += details.getPrice();
          //应收金额
          mReceivableAmount += details.getReceivablePrice();
          //消费金额
          mConsumeAmount += details.getPrice();
          //优惠金额
          mDiscAmount += details.getDiscPrice();
        }
      }
    }
    //附加费详情列表
    List<PxExtraDetails> dbExtraDetailsList = DaoServiceUtil.getExtraDetailsService()
        .queryBuilder()
        .where(PxExtraDetailsDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
        .list();
    for (PxExtraDetails extraDetails : dbExtraDetailsList) {
      //附加费金额
      mExtraAmount += extraDetails.getPrice();
      //应收金额
      mReceivableAmount += extraDetails.getPrice();
    }

    mTotalAmount = Double.parseDouble(NumberFormatUtils.formatFloatNumber(mTotalAmount));
    mReceivableAmount = Double.parseDouble(NumberFormatUtils.formatFloatNumber(mReceivableAmount));
    mConsumeAmount = Double.parseDouble(NumberFormatUtils.formatFloatNumber(mConsumeAmount));
    mDiscAmount = Double.parseDouble(NumberFormatUtils.formatFloatNumber(mDiscAmount));
    mExtraAmount = Double.parseDouble(NumberFormatUtils.formatFloatNumber(mExtraAmount));

    //合计数量
    mTvNums.setText(mTotalNum + "项");

    //合计金额
    mTvTotalAmount.setText("￥:" + mTotalAmount);
    //应收金额
    mTvTotalReceivable.setText("￥:" + mReceivableAmount);
    //消费金额
    mTvTotalConsume.setText("￥:" + mConsumeAmount);
    //优惠金额
    mTvTotalDisc.setText("￥:" + mDiscAmount);
    //附加费金额
    mTvTotalExtra.setText("￥:" + mExtraAmount);

    //支付信息
    List<PxPayInfo> payInfoList = DaoServiceUtil.getPayInfoService()
        .queryBuilder()
        .where(PxPayInfoDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
        .list();
    //计算金额
    for (PxPayInfo payInfo :  payInfoList) {
      //实收
      mTotalReceived += payInfo.getReceived();
      //找零
      mTotalChange += payInfo.getChange();
      //支付类优惠
      mTotalPayPrivilege += payInfo.getPayPrivilege();
    }
    //实收
    mTvTotalReceived.setText("￥:" + NumberFormatUtils.formatFloatNumber(mTotalReceived));
    //找零
    mTvTotalChange.setText("￥:" + NumberFormatUtils.formatFloatNumber(mTotalChange));
    //支付类优惠
    mTvTotalPayPrivilege.setText("￥:" + NumberFormatUtils.formatFloatNumber(mTotalPayPrivilege));
    //还需付款
    mWaitPayAmount = Double.parseDouble(NumberFormatUtils.formatFloatNumber(mReceivableAmount - (mTotalReceived - mTotalChange)- mTotalPayPrivilege));
    if (mWaitPayAmount < 0) {
      mWaitPayAmount = 0;
    }
    mTvWaitPay.setText("还需付款:" + NumberFormatUtils.formatFloatNumber(mWaitPayAmount));
    //客显
    showCustomerDisplay();
    //仅供客户联打印使用
    mCustomAmount.setInfoList(mTotalAmount, mReceivableAmount, mTotalReceived, mTotalChange, mConsumeAmount, mDiscAmount, mExtraAmount);
    //仅供财务联打印使用
    mFinanceAmount.setmPayCashTotal(mTotalAmount);
    //关闭界面
    closePayView();
  }

  /**
   * 客显
   */
  public void showCustomerDisplay(){
    if (mAct != null){
      mAct.showCustomerDisplay(mWaitPayAmount);
    }
  }

  /**
   * 查询支付详情
   */
  @OnClick(R.id.btn_query_received_details)
  public void queryReceivedDetails(){
    if (mCurrentOrderInfo == null) return;
    SQLiteDatabase database = DaoServiceUtil.getPayInfoDao().getDatabase();
    Cursor cursor = database.rawQuery(
        "Select sum(pay.RECEIVED),pay.PAYMENT_NAME,sum(pay.CHANGE),sum(pay.PAY_PRIVILEGE)"
            + " From PxPayInfo pay"
            + " Where pay.PX_ORDER_INFO_ID = " + mCurrentOrderInfo.getId()
            + " Group by pay.PAYMENT_NAME", null);
    List<AppPayInfoCollect> payinfoCollectList = new ArrayList<AppPayInfoCollect>();
    while (cursor.moveToNext()) {
      AppPayInfoCollect appPayInfoCollect = new AppPayInfoCollect();
      appPayInfoCollect.setReceived(cursor.getDouble(0) - cursor.getDouble(2));
      appPayInfoCollect.setName(cursor.getString(1));
      appPayInfoCollect.setPayPrivilege(cursor.getDouble(3));
      payinfoCollectList.add(appPayInfoCollect);
    }
    IOUtils.closeCloseables(cursor);
    if (payinfoCollectList.size() == 0) return;
    String[] strings = new String[payinfoCollectList.size()];
    for (int i = 0; i < payinfoCollectList.size(); i++){
      AppPayInfoCollect appPayInfoCollect = payinfoCollectList.get(i);
      strings[i] = appPayInfoCollect.getName() + ":实收 " + NumberFormatUtils.formatFloatNumber(appPayInfoCollect.getReceived()) + ", 支付优惠 " + appPayInfoCollect.getPayPrivilege();
    }
    MaterialDialog dialog =
        new MaterialDialog.Builder(mAct).title("实收明细").items(strings).negativeText("确定").show();
    addDialog(dialog);
  }

  /**
   * 重置商品点单后,直接结账
   * BusinessModelFragment发送
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void resetAutoOrder(ResetAutoOrderEvent evnet) {
    updatePayInfo();
  }

  /**
   * 更新OrderInfo
   */
   public void updateOrderInfo() {
    mCurrentOrderInfo = CashBillFragment.mOrderInfo;
    //清空支付信息
    if (mCurrentOrderInfo == null) {
      resetPayInfo();
    } else {
      //APP非正常退出 订单可能是锁定状态 恢复
      OptOrderLock.optOrderLock(mCurrentOrderInfo,false);
    }
  }

  /**
   * 清空支付显示
   */
  private void resetPayInfo() {
    //合计数量
    mTotalNum = 0;
    mTvNums.setText(0 + "项");
    //合计金额
    mTotalAmount = 0;
    mTvTotalAmount.setText("￥:" + 0);
    //应收金额
    mReceivableAmount = 0;
    mTvTotalReceivable.setText("￥:" + 0);
    //总实收
    mTotalReceived = 0;
    mTvTotalReceived.setText("￥:" + 0);
    //总找零
    mTotalChange = 0;
    mTvTotalChange.setText("￥:" + 0);
    //消费金额
    mConsumeAmount = 0;
    mTvTotalConsume.setText("￥:" + 0);
    //优惠金额
    mDiscAmount = 0;
    mTvTotalDisc.setText("￥:" + 0);
    //附加费金额
    mExtraAmount = 0;
    mTvTotalExtra.setText("￥:" + 0);
    //还需付款
    mWaitPayAmount = 0;
    mTvWaitPay.setText("￥:" + 0);
    //当前实收
    mCurrentReceived = 0;
    if (mTvPayCashReceivable != null) {
      mTvPayCashReceived.setText("0.0");
    }
    //当前找零
    mCurrentChange = 0;
    if (mTvPayCashChange != null) {
      mTvPayCashChange.setText("0.0");
    }
    //支付类优惠
    mTotalPayPrivilege = 0;
    mTvTotalPayPrivilege.setText("￥:" + 0);
    //清空汇总信息
    mDetailsList = null;
  }

  /**
   * 继续收款
   */
  @OnClick({ R.id.btn_continue_pay }) public void continuePay() {
    //默认支付方式
    mCurrentPaymentMode = DaoServiceUtil.getPaymentModeService()
        .queryBuilder()
        .where(PxPaymentModeDao.Properties.Type.eq(PxPaymentMode.TYPE_CASH))
        .where(PxPaymentModeDao.Properties.Edit.eq(PxPaymentMode.EDIT_FALSE))
        .unique();
    //现金支付
    payCash();
  }

  /**
   * 现金付款
   */
  public void payCash() {
    //加载ViewStub
    if (mCashView == null) {
      mCashView = mCashViewStub.inflate();
      //取消按钮
      mTvPayCashCancel = (TextView) mCashView.findViewById(R.id.tv_pay_cash_cancel);
      mTvPayCashCancel.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          closePayView();
          updatePayInfo();
        }
      });
      //确认按钮
      mTvPayCashConfirm = (TextView) mCashView.findViewById(R.id.tv_pay_cash_confirm);
      mTvPayCashConfirm.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          if(mWaitPayAmount <= 0){
            ToastUtils.show("款已付清,无需支付");
            return;
          }
          if (!(mCurrentReceived > 0)) {
            ToastUtils.show("请输入大于零的金额!");
            return;
          }
          //开钱箱
          openCashBox();
          //添加支付信息
          addPayInfoToOrderInfo(mCurrentReceived, mCurrentChange, null, "", "", "", mCurrentPaymentMode,0.0,"");
          //是否收款同时下单
          if (isReceiveMoneyAndOrder()){
           receiveMoneyAndOrder();
         }
        }
      });
      //应收
      mTvPayCashReceivable = (TextView) mCashView.findViewById(R.id.tv_pay_cash_receivable);
      //实收
      mTvPayCashReceived = (TextView) mCashView.findViewById(R.id.tv_pay_cash_received);
      //找零
      mTvPayCashChange = (TextView) mCashView.findViewById(R.id.tv_pay_cash_change);
      //标题
      mTvPayCashTitle = (TextView) mCashView.findViewById(R.id.tv_pay_cash_title);
      mTvPayCashTitle.setText(mCurrentPaymentMode.getName());
    } else {
      mTvPayCashTitle.setText(mCurrentPaymentMode.getName());
    }
    //显示页面
    showPayView();
  }

  /**
   * 银行卡支付
   */
  private void payPos() {
    //加载ViewStub
    if (mPosView == null) {
      mPosView = mPosViewStub.inflate();
      //取消按钮
      mTvPayPosCancel = (TextView) mPosView.findViewById(R.id.tv_pay_pos_cancel);
      mTvPayPosCancel.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          closePayView();
          updatePayInfo();
        }
      });
      //确认按钮
      mTvPayPosConfirm = (TextView) mPosView.findViewById(R.id.tv_pay_pos_confirm);
      mTvPayPosConfirm.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          String money = mTvPayPosReceived.getText().toString().trim();
          String voucher = mEtPayPosVoucher.getText().toString().trim();
          if (TextUtils.isEmpty(money) || TextUtils.isEmpty(voucher)) {
            ToastUtils.show("请填写正确的金额和凭证码");
            return;
          }
          double received = Double.parseDouble(money);
          if (!(received > 0)) {
            ToastUtils.show( "请输入大于零的金额!");
            return;
          }
          //添加付款信息
          addPayInfoToOrderInfo(received, 0, null, voucher, "", "", mCurrentPaymentMode,0.0,"");
          //是否收款同时下单
          if (isReceiveMoneyAndOrder()){
            receiveMoneyAndOrder();
          }
        }
      });
      //实收
      mTvPayPosReceived = (TextView) mPosView.findViewById(R.id.tv_pay_pos_received);
      //凭证
      mEtPayPosVoucher = (EditText) mPosView.findViewById(R.id.et_pay_pos_voucher);
      //标题
      mTvPayPosTitle = (TextView) mPosView.findViewById(R.id.tv_pay_pos_title);
      mTvPayPosTitle.setText(mCurrentPaymentMode.getName());
    } else {
      mTvPayPosTitle.setText(mCurrentPaymentMode.getName());
    }
    //显示页面
    showPayView();
  }

  /**
   * 第三方支付
   */
  private void payThird() {
    //加载ViewStub
    if (mThirdView == null) {
      mThirdView = mThirdViewStub.inflate();
      //实收
      mTvPayThirdReceived = (TextView) mThirdView.findViewById(R.id.tv_pay_third_received);
      //付款码
      mEtPayThirdPaycode = (EditText) mThirdView.findViewById(R.id.et_pay_third_paycode);
      //标题
      mTvPayThirdTitle = (TextView) mThirdView.findViewById(R.id.tv_pay_third_title);
      //title
      mTvPayThirdTitle.setText(mCurrentPaymentMode.getName());
      //点击软键盘上的回车键才会触发
      mEtPayThirdPaycode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
          if (mThirdView.getVisibility() == View.VISIBLE) {
            //支付金额
            String payMoney = mTvPayThirdReceived.getText().toString().trim();
            //支付条码
            String payCode = mEtPayThirdPaycode.getText().toString().trim();
            if (payCode.length() > 10) {
              goPay(payMoney, payCode);
            }
            mEtPayThirdPaycode.setText("");
          }
          return true;
        }
      });
      //判断支持扫码支付
      supportScanPay();
    } else {
      mTvPayThirdTitle.setText(mCurrentPaymentMode.getName());
    }
    mEtPayThirdPaycode.setText("");
    //显示页面
    showPayView();
  }

  /**
   * 监听键盘输入事件  扫码枪用
   * 扫码枪支付请求焦点
   */
  public void onKeyDown(){
    if (mThirdView != null && mThirdView.getVisibility() == View.VISIBLE && !mEtPayThirdPaycode.hasFocus() ){
      mEtPayThirdPaycode.requestFocus();
    }
  }


  /**
   * 第三方支付发起支附
   */
  private void goPay(String payMoney ,String payCode){
    if (!(Double.valueOf(payMoney) > 0.0)) {
      ToastUtils.show( "请输入大于零的金额!");
      return;
    }
    if (!NetUtils.isConnected(mAct)) {
      ToastUtils.show( "无网络,请检查网络配置!");
      return;
    }
    //支付类型
    String type = mCurrentPaymentMode.getType();
    if (PxPaymentMode.TYPE_ALIPAY.equals(type)) {//支付宝
      Alipay.reqAlipay(mAct, mCurrentOrderInfo, payMoney, payCode);
    } else if (PxPaymentMode.TYPE_WEIXIN.equals(type)) {//微信
      WxPay.reqWxPay(mAct, mCurrentOrderInfo, payMoney, payCode);
    } else if (PxPaymentMode.TYPE_WINGPAY.equals(type)){//翼支付
      BestPay.reqBestPay(mAct,mCurrentOrderInfo,payMoney,payCode);
    }
  }

  /**
   * 免单
   */
  private void payFree() {
    //加载ViewStub
    if (mFreeView == null) {
      mFreeView = mFreeViewStub.inflate();
      //取消按钮
      mTvPayFreeCancel = (TextView) mFreeView.findViewById(R.id.tv_pay_free_cancel);
      mTvPayFreeCancel.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          closePayView();
          updatePayInfo();
        }
      });
      //确认按钮
      mTvPayFreeConfirm = (TextView) mFreeView.findViewById(R.id.tv_pay_free_confirm);
      mTvPayFreeConfirm.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          String money = mTvPayFreeReceived.getText().toString().trim();
          String reason = mEtPayFreeReason.getText().toString().trim();
          if (TextUtils.isEmpty(money) || TextUtils.isEmpty(reason)) {
            ToastUtils.show("请填写正确的金额和免单原因");
            return;
          }
          double received = Double.parseDouble(money);
          if (!(received > 0)) {
            ToastUtils.show( "请输入大于零的金额!");
            return;
          }
          //添加付款信息
          addPayInfoToOrderInfo(received, 0, null, "", "", reason, mCurrentPaymentMode,0.0,"");
          //是否收款同时下单
          if (isReceiveMoneyAndOrder()){
            receiveMoneyAndOrder();
          }
        }
      });
      //实收
      mTvPayFreeReceived = (TextView) mFreeView.findViewById(R.id.tv_pay_free_received);
      //原因
      mEtPayFreeReason = (EditText) mFreeView.findViewById(R.id.et_pay_free_reason);
      //标题
      mTvPayFreeTitle = (TextView) mFreeView.findViewById(R.id.tv_pay_free_title);
      mTvPayFreeTitle.setText(mCurrentPaymentMode.getName());
    } else {
      mTvPayFreeTitle.setText(mCurrentPaymentMode.getName());
    }
    //显示页面
    showPayView();
  }

  /**
   * 其它支付
   */
  private void payOther() {
    //加载ViewStub
    if (mOtherView == null) {
      mOtherView = mOtherViewStub.inflate();
      //取消按钮
      mTvPayOtherCancel = (TextView) mOtherView.findViewById(R.id.tv_pay_other_cancel);
      mTvPayOtherCancel.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          closePayView();
          updatePayInfo();
        }
      });
      //确认按钮
      mTvPayOtherConfirm = (TextView) mOtherView.findViewById(R.id.tv_pay_other_confirm);
      mTvPayOtherConfirm.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          String money = mTvPayOtherReceived.getText().toString().trim();
          if (TextUtils.isEmpty(money)) {
            ToastUtils.show( "请填写正确的金额");
            return;
          }
          double received = Double.parseDouble(money);
          if (!(received > 0)) {
            ToastUtils.show("请输入大于零的金额!");
            return;
          }
          //开钱箱
          openCashBox();
          //添加付款信息
          addPayInfoToOrderInfo(received, 0, null, "", "", "", mCurrentPaymentMode,0.0,"");
          //是否收款同时下单
          if (isReceiveMoneyAndOrder()){
            receiveMoneyAndOrder();
          }
        }
      });
      //应收
      mTvPayOtherReceivable = (TextView) mOtherView.findViewById(R.id.tv_pay_other_receivable);
      //实收
      mTvPayOtherReceived = (TextView) mOtherView.findViewById(R.id.tv_pay_other_received);
      //标题
      mTvPayOtherTitle = (TextView) mOtherView.findViewById(R.id.tv_pay_other_title);
      mTvPayOtherTitle.setText(mCurrentPaymentMode.getName());
    } else {
      mTvPayOtherTitle.setText(mCurrentPaymentMode.getName());
    }
    //显示页面
    showPayView();
  }

  /**
   * 优惠券
   */
  private void payCoupon() {
    //加载ViewStub
    if (mCouponView == null) {
      mCouponView = mCouponViewStub.inflate();
      //取消按钮
      mTvPayCouponCancel = (TextView) mCouponView.findViewById(R.id.tv_pay_coupon_cancel);
      mTvPayCouponCancel.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          closePayView();
          updatePayInfo();
        }
      });
      //确认按钮
      mTvPayCouponConfirm = (TextView) mCouponView.findViewById(R.id.tv_pay_coupon_confirm);
      mTvPayCouponConfirm.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (mVoucherList== null || mVoucherList.size() ==0){
            ToastUtils.show("请添加优惠券");
            return;
          }
          int pos = mLvCoupon.getCheckedItemPosition();
          PxVoucher pxVoucher = mVoucherList.get(pos);
          //添加支付信息
          addPayInfoToOrderInfo(pxVoucher.getPrice(), 0, null, "", "", "", mCurrentPaymentMode,0.0,"");
          //是否收款同时下单
          if (isReceiveMoneyAndOrder()){
            receiveMoneyAndOrder();
          }
        }
      });
      //标题
      mTvPayCouponTitle = (TextView) mCouponView.findViewById(R.id.tv_pay_coupon_title);
      mTvPayCouponTitle.setText(mCurrentPaymentMode.getName());
      //ListView
      mLvCoupon = (ListView) mCouponView.findViewById(R.id.lv_coupon);
    } else {
      mTvPayCouponTitle.setText(mCurrentPaymentMode.getName());
    }
    //显示页面
    showPayView();
  }


  /**
   * 团购券
   */
  private void payGroupCoupon(){
    //加载ViewStub
    if (mGroupCouponView == null) {
      mGroupCouponView = mGroupCouponViewStub.inflate();
      //取消按钮
      mTvPayGroupCouponCancel = (TextView) mGroupCouponView.findViewById(R.id.tv_pay_group_coupon_cancel);
      mTvPayGroupCouponCancel.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          closePayView();
          updatePayInfo();
        }
      });
      //确认按钮
      mTvPayGroupCouponConfirm = (TextView) mGroupCouponView.findViewById(R.id.tv_pay_group_coupon_confirm);
      mTvPayGroupCouponConfirm.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (mWaitPayAmount == 0){
            ToastUtils.show("款已付清");
            return;
          }
          if (StringUtils.isEmpty(mEtPayGroupCouponCode.getText())){
            ToastUtils.show("请填写验券码");
            return;
          }
          if (mEtPayGroupCouponCode.getText().toString().length() < 4){
            ToastUtils.show("验券码长度过短");
            return;
          }
          if (StringUtils.isEmpty(mTvPayGroupCouponStoreAmount.getText()) || StringUtils.isEmpty(mTvPayGroupCouponReceivedAmount.getText())){
            ToastUtils.show("请选择团购券");
            return;
          }
          //门市价
          Double storeAmount = Double.valueOf(mTvPayGroupCouponStoreAmount.getText().toString());
          //实收
          Double receivedAmount =Double.valueOf(mTvPayGroupCouponReceivedAmount.getText().toString());
          //支付类优惠
          Double payPrivilege = storeAmount - receivedAmount;
          //验券码
          String ticketCode = mEtPayGroupCouponCode.getText().toString();
          //添加支付信息
          addPayInfoToOrderInfo(receivedAmount,0.0,null,"","","",mCurrentPaymentMode,payPrivilege,ticketCode);
          //是否收款同时下单
          if (isReceiveMoneyAndOrder()){
            receiveMoneyAndOrder();
          }
        }
      });
      //标题
      mTvPayGroupCouponTitle = (TextView) mGroupCouponView.findViewById(R.id.tv_pay_group_coupon_title);
      mTvPayGroupCouponTitle.setText(mCurrentPaymentMode.getName());
      //ListView
      mLvGroupCoupon = (ListView) mGroupCouponView.findViewById(R.id.lv_group_coupon);
      //门市价
      mTvPayGroupCouponStoreAmount = (TextView) mGroupCouponView.findViewById(R.id.tv_pay_group_coupon_store_amount);
      //实收
      mTvPayGroupCouponReceivedAmount = (TextView) mGroupCouponView.findViewById(R.id.tv_pay_group_coupon_received_amount);
      //验券码
      mEtPayGroupCouponCode = (EditText) mGroupCouponView.findViewById(R.id.et_pay_group_coupon_code);
    } else {
      mTvPayGroupCouponTitle.setText(mCurrentPaymentMode.getName());
      mTvPayGroupCouponStoreAmount.setText("");
      mTvPayGroupCouponReceivedAmount.setText("");
      mEtPayGroupCouponCode.setText("");
    }
    //显示页面
    showPayView();
  }

  /**
   * 优惠买单
   */
  private void payPrivilege() {
    //加载ViewStub
    if (mPrivilegeView == null) {
      mPrivilegeView = mPrivilegeViewStub.inflate();
      //取消按钮
      mTvPayPrivilegeCancel = (TextView) mPrivilegeView.findViewById(R.id.tv_pay_privilege_cancel);
      mTvPayPrivilegeCancel.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          closePayView();
          updatePayInfo();
        }
      });
      //确认按钮
      mTvPayPrivilegeConfirm = (TextView) mPrivilegeView.findViewById(R.id.tv_pay_privilege_confirm);
      mTvPayPrivilegeConfirm.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (mCurrentReceived <= 0){
            ToastUtils.show("实收金额不能为0");
            return;
          }
          String s = mTvPayPrivilegeAmount.getText().toString();
          double payPrivilege = s == null ? 0.0:Double.valueOf(s);
          //添加支付信息
          addPayInfoToOrderInfo(mCurrentReceived,0.0,null,"","","",mCurrentPaymentMode,payPrivilege,"");
          //是否收款同时下单
          if (isReceiveMoneyAndOrder()){
            receiveMoneyAndOrder();
          }
        }
      });
      //标题
      mTvPayPrivilegeTitle = (TextView) mPrivilegeView.findViewById(R.id.tv_pay_privilege_title);
      mTvPayPrivilegeTitle.setText(mCurrentPaymentMode.getName());
      //应收
      mTvPayPrivilegeReceivable = (TextView) mPrivilegeView.findViewById(R.id.tv_pay_privilege_receivable);
      //实收
      mTvPayPrivilegeReceived = (TextView) mPrivilegeView.findViewById(R.id.tv_pay_privilege_received);
      //优惠
      mTvPayPrivilegeAmount = (TextView) mPrivilegeView.findViewById(R.id.tv_pay_privilege_amount);
    } else {
      mTvPayPrivilegeTitle.setText(mCurrentPaymentMode.getName());
    }
    //显示页面
    showPayView();
  }

  /**
   * 显示结账页面
   */
  private void showPayView() {
    //切换页面
    mFlContinuePay.setVisibility(View.GONE);
    //现金
    if (mCashView != null) {
      mCashView.setVisibility(View.GONE);
    }
    //银行卡
    if (mPosView != null) {
      mPosView.setVisibility(View.GONE);
    }
    //第三方
    if (mThirdView != null) {
      mThirdView.setVisibility(View.GONE);
    }
    //免单
    if (mFreeView != null) {
      mFreeView.setVisibility(View.GONE);
    }
    //其它
    if (mOtherView != null) {
      mOtherView.setVisibility(View.GONE);
    }
    //优惠券
    if (mCouponView != null) {
      mCouponView.setVisibility(View.GONE);
    }
    //团购券
    if (mGroupCouponView != null){
      mGroupCouponView.setVisibility(View.GONE);
    }
    //优惠买单
    if (mPrivilegeView != null){
      mPrivilegeView.setVisibility(View.GONE);
    }

    //初始化StringBuilder
    mSbCashReceived = new StrBuilder();
    switch (mCurrentPaymentMode.getType()) {
      case PxPaymentMode.TYPE_CASH://现金
        mTvPayCashReceivable.setText(NumberFormatUtils.formatFloatNumber(mWaitPayAmount));
        mTvPayCashReceived.setText(NumberFormatUtils.formatFloatNumber(mWaitPayAmount ));
        mTvPayCashChange.setText(0 + "");
        if (mCashView != null) {
          mCashView.setVisibility(View.VISIBLE);
        }
        break;
      case PxPaymentMode.TYPE_POS://银行卡
        mTvPayPosReceived.setText(NumberFormatUtils.formatFloatNumber(mWaitPayAmount));
        if (mPosView != null) {
          mPosView.setVisibility(View.VISIBLE);
        }
        break;
      case PxPaymentMode.TYPE_ALIPAY://支付宝
      case PxPaymentMode.TYPE_WEIXIN://微信
      case PxPaymentMode.TYPE_WINGPAY://翼支付
        mTvPayThirdReceived.setText(NumberFormatUtils.formatFloatNumber(mWaitPayAmount ));
        if (mThirdView != null) {
          mThirdView.setVisibility(View.VISIBLE);
          if (!mEtPayThirdPaycode.hasFocus()) {
            mEtPayThirdPaycode.requestFocus();
          }
        }
        break;
      case PxPaymentMode.TYPE_FREE://免单
        mTvPayFreeReceived.setText(NumberFormatUtils.formatFloatNumber(mWaitPayAmount));
        if (mFreeView != null) {
          mFreeView.setVisibility(View.VISIBLE);
        }
        break;
      case PxPaymentMode.TYPE_OTHER://其它
        mTvPayOtherReceivable.setText(NumberFormatUtils.formatFloatNumber(mWaitPayAmount));
        mTvPayOtherReceived.setText(NumberFormatUtils.formatFloatNumber(mWaitPayAmount));
        if (mOtherView != null) {
          mOtherView.setVisibility(View.VISIBLE);
        }
        break;
      case PxPaymentMode.TYPE_VOUCHER://优惠券
        //结束时间 大于现在前一天
        Date startDate = new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000);
        mVoucherList = DaoServiceUtil.getVoucherService()
            .queryBuilder()
            .where(PxVoucherDao.Properties.DelFlag.eq("0"))
            .whereOr(PxVoucherDao.Properties.EndDate.isNull(), PxVoucherDao.Properties.EndDate.gt(startDate))
            .whereOr(PxVoucherDao.Properties.StartDate.isNull(), PxVoucherDao.Properties.StartDate.lt(new Date()))
            .list();
        CouponAdapter couponAdapter = new CouponAdapter(mAct, mVoucherList);
        mLvCoupon.setAdapter(couponAdapter);
        if (mLvCoupon.getCount() != 0) {
          mLvCoupon.setItemChecked(0, true);
        }
        if (mCouponView != null) {
          mCouponView.setVisibility(View.VISIBLE);
        }
        break;
      case PxPaymentMode.TYPE_GROUP_COUPON://团购券
        mBuyCouponsList = DaoServiceUtil.getPxBuyCouponsService()
            .queryBuilder()
            .where(PxBuyCouponsDao.Properties.DelFlag.eq("0"))
            .where(PxBuyCouponsDao.Properties.PaymentModeId.eq(mCurrentPaymentMode.getId()))
            .list();
        BuyCouponAdapter buyCouponAdapter = new BuyCouponAdapter(mAct,mBuyCouponsList);
        mLvGroupCoupon.setAdapter(buyCouponAdapter);
        mLvGroupCoupon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PxBuyCoupons buyCoupons = mBuyCouponsList.get(position);
            mTvPayGroupCouponStoreAmount.setText(NumberFormatUtils.formatFloatNumber(buyCoupons.getOffsetAmount()));
            mTvPayGroupCouponReceivedAmount.setText(NumberFormatUtils.formatFloatNumber(buyCoupons.getAmount()));
          }
        });
        if (mGroupCouponView != null){
          mGroupCouponView.setVisibility(View.VISIBLE);
        }
        break;
      case PxPaymentMode.TYPE_PRIVILEGE://优惠买单
        mTvPayPrivilegeReceivable.setText(NumberFormatUtils.formatFloatNumber(mWaitPayAmount));
        mTvPayPrivilegeReceived.setText(NumberFormatUtils.formatFloatNumber(mWaitPayAmount));
        mTvPayPrivilegeAmount.setText(0 + "");
        if (mPrivilegeView != null) {
          mPrivilegeView.setVisibility(View.VISIBLE);
        }
        break;
    }
    mCurrentReceived = mWaitPayAmount;
    mCurrentChange = 0;
  }

  /**
   * 关闭现金页面
   */
  private void closePayView() {
    mLvPaymentMode.setItemChecked(0,true);
    //切换页面
    if (mCashView != null) {
      mCashView.setVisibility(View.GONE);
    }
    if (mPosView != null) {
      mPosView.setVisibility(View.GONE);
      mEtPayPosVoucher.setText("");
    }
    if (mThirdView != null) {
      mThirdView.setVisibility(View.GONE);
    }
    if (mFreeView != null) {
      mFreeView.setVisibility(View.GONE);
      mEtPayFreeReason.setText("");
    }
    if (mOtherView != null) {
      mOtherView.setVisibility(View.GONE);
    }
    if (mCouponView != null) {
      mCouponView.setVisibility(View.GONE);
    }
    if (mGroupCouponView != null){
      mGroupCouponView.setVisibility(View.GONE);
    }
    if (mPrivilegeView != null){
      mPrivilegeView.setVisibility(View.GONE);
    }
    mFlContinuePay.setVisibility(View.VISIBLE);
  }

  /**
   * 键盘点击
   * 1、2、3、4、5、6、7、8、9、0、00、.、del
   */
  //@formatter:off
  @OnClick({
      R.id.btn_check_out_1, R.id.btn_check_out_2, R.id.btn_check_out_3, R.id.btn_check_out_4,
      R.id.btn_check_out_5, R.id.btn_check_out_6, R.id.btn_check_out_7, R.id.btn_check_out_8,
      R.id.btn_check_out_9, R.id.btn_check_out_0, R.id.btn_check_out_point, R.id.btn_check_out_del
  })
  public void keyBoardClick(TextView tv) {
    //如果页面关闭,禁止输入
    switch (mCurrentPaymentMode.getType()) {
      case PxPaymentMode.TYPE_CASH:
        //如果现金页面为隐藏状态，返回
        if (mCashView == null || mCashView.getVisibility() != View.VISIBLE) {
          return;
        }
        break;
      case PxPaymentMode.TYPE_POS:
        //如果现金页面为隐藏状态，返回
        if (mPosView == null || mPosView.getVisibility() != View.VISIBLE) {
          return;
        }
        break;
      case PxPaymentMode.TYPE_ALIPAY://支付宝
      case PxPaymentMode.TYPE_WEIXIN://微信
      case PxPaymentMode.TYPE_WINGPAY://翼支付
        if (mThirdView == null || mThirdView.getVisibility() != View.VISIBLE) {
          return;
        }
        break;
      case PxPaymentMode.TYPE_FREE://免单
        if (mFreeView == null || mFreeView.getVisibility() != View.VISIBLE) {
          return;
        }
        break;
      case PxPaymentMode.TYPE_OTHER://其它
        if (mOtherView == null || mOtherView.getVisibility() != View.VISIBLE) {
          return;
        }
        break;
      case PxPaymentMode.TYPE_PRIVILEGE://优惠买单
        if (mPrivilegeView == null || mPrivilegeView.getVisibility() != View.VISIBLE){
          return;
        }
        break;
    }
    //长度限制
    if (mSbCashReceived != null && mSbCashReceived.length() > 6 && tv.getId() != R.id.btn_check_out_del) {
      return;
    }
    //浮点
    if (tv.getId() == R.id.btn_check_out_point) {
      if (!mSbCashReceived.contains(".") && (mSbCashReceived != null && mSbCashReceived.toString().trim().length() != 0)) {
        mSbCashReceived.append(tv.getText());
      } else {
        return;
      }
    }
    //删除
    else if (tv.getId() == R.id.btn_check_out_del && mSbCashReceived.length() >= 2) {
      mSbCashReceived.delete(mSbCashReceived.length() - 1, mSbCashReceived.length());
    }
    //删除
    else if (tv.getId() == R.id.btn_check_out_del && mSbCashReceived.length() <= 1) {
      mSbCashReceived = new StrBuilder();
      mSbCashReceived.append("0");
    }
    //数字
    else {
      if (mSbCashReceived.indexOf("0") == 0) {
        mSbCashReceived.deleteFirst("0");
      }
      mSbCashReceived.append(tv.getText());
    }
    //实收金额
    if (mSbCashReceived.toString().trim().equals("") || mSbCashReceived.toString().trim().equals(".")) {
      return;
    }
    mCurrentReceived = new Double(mSbCashReceived.toString());
    switch (mCurrentPaymentMode.getType()) {
      case PxPaymentMode.TYPE_CASH://现金
        //找零金额
        mCurrentChange = 0;
        if (mCurrentReceived > mWaitPayAmount) {
          mCurrentChange = Double.parseDouble(NumberFormatUtils.formatFloatNumber(mCurrentReceived - mWaitPayAmount));
        } else {
          mCurrentChange = 0;
        }
        mTvPayCashReceived.setText(NumberFormatUtils.formatFloatNumber(mCurrentReceived));
        mTvPayCashChange.setText(mCurrentChange + "");
        break;
      case PxPaymentMode.TYPE_POS://银行卡
        if (mCurrentReceived > mWaitPayAmount) {
          mCurrentReceived = mWaitPayAmount;
          mSbCashReceived = new StrBuilder();
          mSbCashReceived.append(mCurrentReceived + "");
        }
        mTvPayPosReceived.setText(NumberFormatUtils.formatFloatNumber(mCurrentReceived));
        break;
      case PxPaymentMode.TYPE_ALIPAY://支付宝
      case PxPaymentMode.TYPE_WEIXIN://微信
      case PxPaymentMode.TYPE_WINGPAY://翼支付
        if (mCurrentReceived > mWaitPayAmount) {
          mCurrentReceived = mWaitPayAmount;
          mSbCashReceived = new StrBuilder();
          mSbCashReceived.append(mCurrentReceived + "");
        }
        mTvPayThirdReceived.setText(NumberFormatUtils.formatFloatNumber(mCurrentReceived));
        break;
      case PxPaymentMode.TYPE_FREE://免单
        if (mCurrentReceived > mWaitPayAmount) {
          mCurrentReceived = mWaitPayAmount;
          mSbCashReceived = new StrBuilder();
          mSbCashReceived.append(mCurrentReceived + "");
        }
        mTvPayFreeReceived.setText(NumberFormatUtils.formatFloatNumber(mCurrentReceived));
        break;
      case PxPaymentMode.TYPE_OTHER://其它
        if (mCurrentReceived > mWaitPayAmount) {
          mCurrentReceived = mWaitPayAmount;
          mSbCashReceived = new StrBuilder();
          mSbCashReceived.append(mCurrentReceived + "");
        }
        mTvPayOtherReceived.setText(NumberFormatUtils.formatFloatNumber(mCurrentReceived));
        break;
      case PxPaymentMode.TYPE_PRIVILEGE://优惠买单
        if (mCurrentReceived > mWaitPayAmount){
          mCurrentReceived = mWaitPayAmount;
          mSbCashReceived = new StrBuilder();
          mSbCashReceived.append(mCurrentReceived + "");
        }
        mTvPayPrivilegeReceived.setText(NumberFormatUtils.formatFloatNumber(mCurrentReceived));
        double privilege = mWaitPayAmount - mCurrentReceived;
        mTvPayPrivilegeAmount.setText(NumberFormatUtils.formatFloatNumber(privilege));
        break;
    }
  }

  /**
   * 抹零并结账完毕
   */
  //@formatter:off
  @OnClick(R.id.btn_tail_pay) public void tailAndContinuePay() {
    if (mCurrentOrderInfo == null) {
      ToastUtils.show( "暂无订单");
      return;
    }
    if (mDetailsList == null || mDetailsList.size() == 0) {
      ToastUtils.show( "暂无商品信息");
      return;
    }
    Double maxTail = ((App) App.getContext()).getUser().getMaxTail();
    if (mWaitPayAmount > maxTail) {
      ToastUtils.show( "当前金额" + mWaitPayAmount + "高于抹零限制" + maxTail);
      return;
    }
    if (mWaitPayAmount == 0) {
      ToastUtils.show( "账已付清,不需要抹零");
      return;
    }
    //检查空Details并结账完毕
    checkEmptyDetailsAndOver(mWaitPayAmount);
  }

  /**
   * 清空支付
   */
  //@formatter:on
  @OnClick(R.id.btn_clear_pay_info) public void cleanPayInfo() {
    if (mCurrentOrderInfo == null) {
      ToastUtils.show("暂无订单信息");
      return;
    }
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("清空支付")
        .customView(R.layout.layout_dialog_clear_disc_pay_info, true)
        .positiveText("确定")
        .positiveColor(mAct.getResources().getColor(R.color.primary_text))
        .build();
    //对话框操作
    ClearDiscAndPayInfoDialog.dialogOperate(dialog, mCurrentOrderInfo, mAct);
    //显示对话框
    dialog.show();
    addDialog(dialog);
  }

  /**
   * 清空折扣
   */
  @OnClick(R.id.btn_clear_discount) public void cleanDiscount() {
    if (mCurrentOrderInfo == null) {
      ToastUtils.show("暂无订单信息");
      return;
    }

    MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("选择清空内容")
        .positiveText("确定")
        .negativeText("取消")
        .items(R.array.clear_disc_selections)
        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog dialog, View itemView, int which,
              CharSequence text) {
            return true;
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            int selectedIndex = dialog.getSelectedIndex();
            SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
            db.beginTransaction();
            try {
              switch (selectedIndex) {
                case 0://商品折扣
                  cleanProductDiscount();
                  break;
                case 1://会员价
                  cleanVipPrice();
                  break;
              }
              db.setTransactionSuccessful();
              //页面更新
              EventBus.getDefault().post(new RefreshCashBillListEvent());
              //关闭对话框
              dialog.dismiss();
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              db.endTransaction();
            }
          }
        })
        .show();
    addDialog(dialog);
  }

  /**
   * 恢复商品折扣率
   */
  private void cleanProductDiscount() {
    Logger.v("mCurrentOrderInfo.getId()");
    //恢复商品详情状态
    List<PxOrderDetails> orderDetailsList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
        .list();
    for (PxOrderDetails details : orderDetailsList) {
      details.setDiscountRate(100);
    }
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(orderDetailsList);
    //恢复附加费详情状态
    List<PxExtraDetails> extraDetailsList = DaoServiceUtil.getExtraDetailsService()
        .queryBuilder()
        .where(PxExtraDetailsDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
        .list();
    for (PxExtraDetails details : extraDetailsList) {
      details.setPayPrice((double) 0);
    }
    DaoServiceUtil.getExtraDetailsService().saveOrUpdate(extraDetailsList);
  }

  /**
   * 恢复原价
   */
  private void cleanVipPrice() {
    Logger.v("cleanVipPrice");
    //判断该订单是否继续使用会员价
    QueryBuilder<PxPayInfo> queryBuilder = DaoServiceUtil.getPayInfoService().queryBuilder();
    queryBuilder.where(PxPayInfoDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()));
    queryBuilder.where(PxPayInfoDao.Properties.PaymentType.eq(PxPaymentMode.TYPE_VIP));
    List<PxPayInfo> vipPayList = queryBuilder.list();
    if (vipPayList == null || vipPayList.size() == 0) {
      mCurrentOrderInfo.setUseVipCard(PxOrderInfo.USE_VIP_CARD_FALSE);
      DaoServiceUtil.getOrderInfoService().update(mCurrentOrderInfo);
    } else {
      ToastUtils.show("有会员支付信息，不能清除折扣");
    }
  }

  /**
   * 结账完毕
   */
  @OnClick(R.id.btn_check_out_over) public void checkOutOver() {
    if (mCurrentOrderInfo == null) {
      ToastUtils.show("暂无订单");
      return;
    }
    if (mDetailsList == null || mDetailsList.size() == 0) {
      ToastUtils.show("暂无商品信息");
      return;
    }
    if (mWaitPayAmount > 0) {
      ToastUtils.show("有未付清的金额");
      return;
    }

    Double receivable = Double.valueOf(NumberFormatUtils.formatFloatNumber(mReceivableAmount));
    Double received = Double.valueOf(NumberFormatUtils.formatFloatNumber(mTotalReceived));
    Double change = Double.valueOf(NumberFormatUtils.formatFloatNumber(mTotalChange));
    if (receivable < received - change) {
      MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("警告")
          .content("当前订单'应收'与'实收 - 找零'不相等,是否结账完毕?")
          .positiveText("确认")
          .negativeText("取消")
          .negativeColor(getResources().getColor(R.color.primary_text))
          .onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override public void onClick(MaterialDialog dialog, DialogAction which) {
              checkEmptyDetailsAndOver(0);
            }
          })
          .canceledOnTouchOutside(false)
          .show();
      addDialog(dialog);
    } else {
      checkEmptyDetailsAndOver(0);
    }
  }

  /**
   * 结账时检查是否为空订单(即为无下单商品的订单)
   */
  private void checkEmptyDetailsAndOver(final double tail) {
    //先检查是否正在刷新订单详情信息
    if (mAct.isShowingProgress()) {
      ToastUtils.showShort(null, "订单信息正在刷新中...");
      return;
    }
    //再检查
    boolean hasDetails = false;
    for (PxOrderDetails details : mDetailsList) {
      if (details.getOrderStatus().equals(PxOrderDetails.ORDER_STATUS_ORDER)) {
        hasDetails = true;
        break;
      }
    }
    if (!hasDetails) {
      ToastUtils.show("没有下单的商品，无法结账，请下单或进行撤单操作");
    } else {
      //查询附加费最低消费
      queryMinConsume(tail);
    }
  }

  /**
   * 处理完结订单
   */
  private void operateOverBill(Double tail) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();

    Cursor cursorExtra = null;//附加费
    Cursor cursorComplement = null;//补足金额
    try {
      //开启蒙层
      mAct.isShowProgress(true);
      //未下单Details
      List<PxOrderDetails> unOrderDetailsList = DaoServiceUtil.getOrderDetailsService()
          .queryBuilder()
          .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
          .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_UNORDER))
          .list();
      if (unOrderDetailsList != null && unOrderDetailsList.size() != 0) {
        ToastUtils.show("请先删除未下单的商品,再结账完毕");
        return;
      }
      //查询所有OrderDetails
      List<PxOrderDetails> orderDetailsList = DaoServiceUtil.getOrderDetailsService()
          .queryBuilder()
          .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
          .list();
      for (PxOrderDetails details : orderDetailsList) {
        //设置最终价格
        if (mCurrentOrderInfo.getUseVipCard().equals(PxOrderInfo.USE_VIP_CARD_TRUE)) {
          details.setFinalPrice(details.getVipPrice());
        } else {
          details.setFinalPrice(details.getPrice());
        }
      }
      DaoServiceUtil.getOrderDetailsService().saveOrUpdate(orderDetailsList);
      //附加费
      if (mCurrentOrderInfo.getDbCurrentExtra() != null) {
        mCurrentOrderInfo.getDbCurrentExtra().setStopTime(new Date());
        DaoServiceUtil.getExtraDetailsService().saveOrUpdate(mCurrentOrderInfo.getDbCurrentExtra());
      }
      //更改订单状态
      mCurrentOrderInfo.setStatus(PxOrderInfo.STATUS_FINISH);
      //刷实体卡
      if (mCurrentOrderInfo.getUseVipCard() == null) {
        mCurrentOrderInfo.setUseVipCard(PxOrderInfo.USE_VIP_CARD_FALSE);
      }
      //结束时间
      mCurrentOrderInfo.setEndTime(new Date());
      //取消当前附加费
      mCurrentOrderInfo.setDbCurrentExtra(null);
      //应收金额
      mCurrentOrderInfo.setAccountReceivable(mReceivableAmount);
      //实收金额
      mCurrentOrderInfo.setRealPrice(mTotalReceived);
      //找零金额
      mCurrentOrderInfo.setTotalChange(mTotalChange);
      //是否抹零
      if (tail != 0) {
        //抹零金额
        PxPaymentMode paymentMode = DaoServiceUtil.getPaymentModeService()
            .queryBuilder()
            .where(PxPaymentModeDao.Properties.Type.eq(PxPaymentMode.TYPE_TAIL))
            .unique();
        //添加支付方式
        addPayInfoToOrderInfo(tail, 0, null, "", "", "", paymentMode, 0.0, "");
        mCurrentOrderInfo.setTailMoney(tail);
      } else {
        mCurrentOrderInfo.setTailMoney(0.0);
      }
      //优惠金额
      mCurrentOrderInfo.setDiscountPrice(mDiscAmount);
      //支付类优惠
      mCurrentOrderInfo.setPayPrivilege(mTotalPayPrivilege);
      //订单总价
      mCurrentOrderInfo.setTotalPrice(mTotalAmount);

      SQLiteDatabase database = DaoServiceUtil.getPayInfoDao().getDatabase();
      //附加费金额
      cursorExtra = database.rawQuery(
          "Select sum(PRICE) from ExtraDetails where PX_ORDER_INFO_ID = "
              + mCurrentOrderInfo.getId() + " and IS_COMPLEMENT is null", null);
      while (cursorExtra.moveToNext()) {
        mCurrentOrderInfo.setExtraMoney(cursorExtra.getDouble(0));
      }
      //补足金额
      cursorComplement = database.rawQuery(
          "Select sum(PRICE) from ExtraDetails where PX_ORDER_INFO_ID = "
              + mCurrentOrderInfo.getId() + " and IS_COMPLEMENT = 1", null);
      while (cursorComplement.moveToNext()) {
        mCurrentOrderInfo.setComplementMoney(cursorComplement.getDouble(0));
      }
      //用户
      mCurrentOrderInfo.setDbUser(((App) App.getContext()).getUser());
      //已经上传的不在修改 置定上传状态false
      if (mCurrentOrderInfo.getIsUpload() == null) {
        mCurrentOrderInfo.setIsUpload(false);
      }
      //默认反结账未上传
      mCurrentOrderInfo.setIsUploadReverse(false);
      //交接班状态
      mCurrentOrderInfo.setShiftChangeType(PxOrderInfo.SHIFT_CHANGE_UNHAND);
      //完结用户
      mCurrentOrderInfo.setDbCheckOutUser(((App) App.getContext()).getUser());
      //储存
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(mCurrentOrderInfo);
      //恢复桌台状态
      if (mCurrentOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
        TableOrderRel tableOrderRel = DaoServiceUtil.getTableOrderRelService()
            .queryBuilder()
            .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
            .unique();
        //结账时间
        tableOrderRel.setOrderEndTime(new Date());
        DaoServiceUtil.getTableOrderRelService().saveOrUpdate(tableOrderRel);
        //最终区域
        String type = tableOrderRel.getDbTable().getType();
        if (type.equals(PxTableInfo.TYPE_HALL)) {
          mCurrentOrderInfo.setFinalArea(PxOrderInfo.FINAL_AREA_HALL);
        } else if (PxTableInfo.TYPE_PARLOR.equals(type)) {
          mCurrentOrderInfo.setFinalArea(PxOrderInfo.FINAL_AREA_PARLOR);
        } else {
          mCurrentOrderInfo.setFinalArea(type);
        }
        DaoServiceUtil.getOrderInfoService().saveOrUpdate(mCurrentOrderInfo);
        //查询桌台订单rel,如果该桌没有订单,则置为空桌
        PxTableInfo dbTable = tableOrderRel.getDbTable();
        QueryBuilder<TableOrderRel> tableOrderRelQb =
            DaoServiceUtil.getTableOrderRelService().queryBuilder();
        tableOrderRelQb.where(TableOrderRelDao.Properties.PxTableInfoId.eq(dbTable.getId()));
        Join<TableOrderRel, PxOrderInfo> tableOrderRelJoin =
            tableOrderRelQb.join(TableOrderRelDao.Properties.PxOrderInfoId, PxOrderInfo.class);
        tableOrderRelJoin.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
        long count = tableOrderRelQb.count();
        if (count == 0) {
          dbTable.setStatus(PxTableInfo.STATUS_EMPTY);
          DaoServiceUtil.getTableInfoService().saveOrUpdate(dbTable);
        }
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      //关闭cursor
      IOUtils.closeCloseables(cursorExtra, cursorComplement);
      //结束事务
      db.endTransaction();
      //关闭
      mAct.isShowProgress(false);
    }
    //及时上传订单
    uploadSingleOrder(mCurrentOrderInfo);
    //打印结账单(财务联)
    printBillWithFinance(mCurrentOrderInfo);
    //重置显示信息
    resetPayInfo();
    //关闭账单概述
    if (mRlFunctions.getVisibility() != View.VISIBLE) {
      performOverviewAnim(mLlOverview, mRlFunctions);
    }
    //找单页面接收
    EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
    //显示客单、菜单页面
    showBillMenu();
    //配置信息
    PxSetInfo setInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
    //自动开单
    boolean isAutoStartBill = (setInfo != null && PxSetInfo.OVER_AUTO_START_BILL_TRUE.equals(
        setInfo.getOverAutoStartBill()));
    //通知CashBill结账完毕 自动开单刷新最新数据
    EventBus.getDefault()
        .post(
            new RevokeOrFinishBillEvent().setOrderInfo(mCurrentOrderInfo).setAuto(isAutoStartBill));
  }

  /**
   * 打印结账单(财务联)
   */
  private void printBillWithFinance(PxOrderInfo info) {
    if (mCurrentOrderInfo == null || mDetailsList == null || mDetailsList.size() == 0) return;
    //网络打印
    printByNetAndBT(BTPrintConstants.PRINT_MODE_FINANCE_INFO);
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //USB打印财务联
    try {
      if (mAppGpService.getGpService().getPrinterConnectStatus(1) == Constants.USB_CONNECT_STATUS) {
        printByUsbWithFinancePrinter(info);
      } else {
        mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
        printByUsbWithFinancePrinter(info);
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 打印USB结账单(财务联)
   */
  private void printByUsbWithFinancePrinter(PxOrderInfo info) {
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          if (info != null) {
            PrinterUsbData.printBillWithFinanceInfo(mAppGpService.getGpService(), info,
                mDetailsList, mFinanceAmount);
          } else {
            ToastUtils.show("暂无订单信息");
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
   * 显示客单、菜单页面
   */
  private void showBillMenu() {
    //更新Fragment
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    //客单
    mCashBillFragment = (CashBillFragment) mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
    if (mCashBillFragment == null) {
      mCashBillFragment = CashBillFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mCashBillFragment, Constants.CASH_BILL_TAG);
    } else {
      transaction.show(mCashBillFragment);
    }
    //菜单
    mCashMenuFragment = mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    if (mCashMenuFragment == null) {
      mCashMenuFragment = CashMenuFragment.newInstance("param");
      transaction.add(R.id.cash_content_right, mCashMenuFragment, Constants.CASH_MENU_TAG);
    } else {
      transaction.show(mCashMenuFragment);
    }
    transaction.commit();
  }

  /**
   * 隐藏所有Fragment
   */
  //@formatter:off
  private void hideAllFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        transaction.hide(fragment);
      }
    }
  }

  /**
   * 整单打折
   */
  @OnClick(R.id.btn_entirety_discount) public void entiretyDisc() {
    if (mCurrentOrderInfo == null) {
      ToastUtils.show( "暂无订单");
      return;
    }
    if (mDetailsList == null || mDetailsList.size() == 0) {
      ToastUtils.show( "暂无商品");
      return;
    }
    showCustomEntiretyDiscDialog();
  }

  /**
   * 显示自定义处理整单打折的对话框
   */
  private void showCustomEntiretyDiscDialog() {
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("整单打折")
        .customView(R.layout.layout_dialog_all_discount, true)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .build();
    //对话框操作
    EntiretyDiscDialog.dialogOperate(dialog, mCurrentOrderInfo);
    addDialog(dialog);
  }

  /**
   * 部分打折
   */
  @OnClick(R.id.btn_part_discount) public void partDiscount() {
    //没有订单
    if (mCurrentOrderInfo == null) {
      ToastUtils.show( "暂无订单");
      return;
    }
    //没有商品
    if (mDetailsList == null || mDetailsList.size() == 0) {
      ToastUtils.show( "暂无商品");
      return;
    }
    showCustomPartDiscDialog();
  }

  /**
   * 显示自定义处理部分打折的对话框
   */
  //@formatter:off
  private void showCustomPartDiscDialog() {
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("部分打折")
        .customView(R.layout.layout_dialog_part_discount, true)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .build();
    //对话框操作
    PartDiscDialog.dialogOperate(dialog, mDetailsList, mAct);
    addDialog(dialog);
  }

  /**
   * 方案打折
   */
  @OnClick(R.id.btn_scheme_discount) public void schemaDiscount() {
    //没有订单
    if (mCurrentOrderInfo == null) {
      ToastUtils.show( "暂无订单");
      return;
    }
    //没有商品
    if (mDetailsList == null || mDetailsList.size() == 0) {
      ToastUtils.show( "暂无商品");
      return;
    }
    //查询打折方案
    final List<PxDiscounScheme> schemeList = DaoServiceUtil.getDiscounSchemeService()
        .queryBuilder()
        .where(PxDiscounSchemeDao.Properties.DelFlag.eq("0"))
        .list();
    if (schemeList == null || schemeList.size() == 0) {
      ToastUtils.show("暂无打折方案");
      return;
    }
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("选择折扣方案")
            .customView(R.layout.layout_dialog_scheme_discount, true)
            .positiveText("确定")
            .negativeText("取消")
            .negativeColor(mAct.getResources().getColor(R.color.primary_text))
            .build();
    //对话框操作
    SchemeDiscDialog.dialogOperate(dialog,schemeList,mDetailsList, mAct);
    addDialog(dialog);
  }


  /**
   * 恢复默认现金支付方式
   */
  private void resetCashPaymentMode() {
    //默认支付方式
    mCurrentPaymentMode = DaoServiceUtil.getPaymentModeService()
        .queryBuilder()
        .where(PxPaymentModeDao.Properties.Type.eq(PxPaymentMode.TYPE_CASH))
        .where(PxPaymentModeDao.Properties.Edit.eq(PxPaymentMode.EDIT_FALSE))
        .unique();
    //关闭支付页面
    closePayView();
  }

  /**
   * 接收会员登录结果
   */
  //@formatter:on
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveVipLogin(VipLoginEvent event) {
    //关闭蒙层
    mAct.isShowProgress(false);
    if (event.isSuccess()) {
      final PxPaymentMode paymentMode = event.getPaymentMode();
      if (paymentMode == null) {
        return;
      }
      Logger.v("VipLoginsuccess");
      final PxVipInfo mVipInfo = event.getVipInfo();
      final PxVipCardInfo mVipCardInfo = event.getVipCardInfo();
      if (mVipInfo == null) {
        Logger.v("mVipInfo==null");
        final PxVipCardType cardType = mVipCardInfo.getCardType();
        if (cardType.getRequirePassword().equals(PxVipCardType.VALIDATE_PASSWORD)) {
          final MaterialDialog mDialog = new MaterialDialog.Builder(mAct).title("密码验证")
              .customView(R.layout.layout_dialog_global, true)
              .canceledOnTouchOutside(false)
              .build();
          View view = mDialog.getCustomView();
          mEtInput = (TextInputEditText) view.findViewById(R.id.et_input);
          mEtInput.setInputType(
              InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
          InputFilter[] filters = { new InputFilter.LengthFilter(6) };
          mEtInput.setFilters(filters);
          TextInputLayout til_input = (TextInputLayout) view.findViewById(R.id.text_input_layout);
          til_input.setHint("请输入实体卡密码");
          til_input.setCounterMaxLength(6);
          final TextView tvOk = (TextView) view.findViewById(R.id.tv_ok);
          TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
          tvOk.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              passwordVerify(mDialog, mVipCardInfo, mVipInfo, cardType);
            }
          });
          tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              mDialog.dismiss();
            }
          });
          mDialog.show();
          addDialog(mDialog);
        } else {
          privilegeMethod(mVipInfo, mVipCardInfo, mPaymentMode, cardType);
        }
      } else {
        useVipPrice(mVipInfo, mVipCardInfo, paymentMode);
      }
    } else {
      resetCashPaymentMode();
    }
  }

  /**
   * 验证密码
   */
  private void passwordVerify(MaterialDialog dialog, PxVipCardInfo mVipCardInfo, PxVipInfo mVipInfo,
      PxVipCardType cardType) {
    String passWord = mEtInput.getText().toString().trim();
    String pwd = mVipCardInfo.getPassword();
    if (TextUtils.isEmpty(passWord)) {
      ToastUtils.show("密码不能为空");
      return;
    } else if (passWord.length() != 6) {
      ToastUtils.show("密码必须为六位");
      return;
    }
    if (pwd.equals(passWord)) {
      ToastUtils.show("密码正确");
      dialog.dismiss();
      privilegeMethod(mVipInfo, mVipCardInfo, mPaymentMode, cardType);
    } else {
      ToastUtils.show("密码错误");
    }
  }

  /**
   * 使用会员价
   */
  private void useVipPrice(PxVipInfo mVipInfo, PxVipCardInfo mVipCardInfo,
      PxPaymentMode paymentMode) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      mCurrentOrderInfo.setUseVipCard(PxOrderInfo.USE_VIP_CARD_TRUE);
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(mCurrentOrderInfo);
      //计算支付信息
      updatePayInfo();
      db.setTransactionSuccessful();
      //显示会员信息 对话框
      Logger.v("应收" + mReceivableAmount + "   -实收" + mTotalReceived + "    +找零" + mTotalChange
          + "   -支付类优惠" + mTotalPayPrivilege + " =" + mWaitPayAmount);
      showVipDetailsDialog(mVipInfo, mVipCardInfo, paymentMode);
      EventBus.getDefault().post(new RefreshCashBillListEvent());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 优惠方式（0：使用会员价 1：打折 2：使用折扣方案）
   */
  private void privilegeMethod(PxVipInfo mVipInfo, PxVipCardInfo mVipCardInfo,
      PxPaymentMode paymentMode, PxVipCardType cardType) {
    mType = cardType.getType();
    Logger.v(mType);
    if (mType.equals(PxVipCardType.USE_VIP_PRICE)) {
      useVipPrice(mVipInfo, mVipCardInfo, paymentMode);
    } else if (mType.equals(PxVipCardType.DISCOUNT)) {
      Integer rechargeScore = cardType.getDiscountRate();
      orderDiscount(mVipInfo, mVipCardInfo, mPaymentMode, rechargeScore);
    } else if (mType.equals(PxVipCardType.DISCOUNT_SCHEME)) {
      PxDiscounScheme discounScheme = cardType.getPxDiscounScheme();
      String objectId = discounScheme.getObjectId();
      //查询打折方案
      PxDiscounScheme unique = DaoServiceUtil.getDiscounSchemeService()
          .queryBuilder()
          .where(PxDiscounSchemeDao.Properties.ObjectId.eq(objectId))
          .unique();
      //找不到折扣方案
      if (unique == null) {
        mAct.isShowProgress(false);
        ToastUtils.show("请同步数据");
        return;
      }
      Integer rechargeScore = unique.getRate();
      Logger.v("" + rechargeScore);
      orderDiscount(mVipInfo, mVipCardInfo, mPaymentMode, rechargeScore);
    }
  }

  /**
   * 订单打折
   */
  private void orderDiscount(PxVipInfo mVipInfo, PxVipCardInfo mVipCardInfo,
      PxPaymentMode paymentMode, Integer rechargeScore) {
    if (mDetailsList != null && mDetailsList.size() != 0) {
      //开启蒙层
      mAct.isShowProgress(true);
    }
    Logger.v("应收" + mReceivableAmount + "   -实收" + mTotalReceived + "    +找零" + mTotalChange
        + "   -支付类优惠" + mTotalPayPrivilege + " =" + mWaitPayAmount);
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      for (PxOrderDetails details : mDetailsList) {
        //商品是否允许打折
        boolean canNotDisc =
            details.getDbProduct().getIsDiscount().equals(PxProductInfo.IS_DISCOUNT_FALSE);
        //如果不允许，处理下一条数据
        if (canNotDisc) continue;
        details.setDiscountRate(rechargeScore);
        DaoServiceUtil.getOrderDetailsService().saveOrUpdate(details);
      }
      //计算支付信息
      updatePayInfo();
      db.setTransactionSuccessful();
      //显示会员信息 对话框
      Logger.v("应收" + mReceivableAmount + "   -实收" + mTotalReceived + "    +找零" + mTotalChange
          + "   -支付类优惠" + mTotalPayPrivilege + " =" + mWaitPayAmount);
      showVipDetailsDialog(mVipInfo, mVipCardInfo, paymentMode);
      EventBus.getDefault().post(new RefreshCashBillListEvent());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 显示会员详情对话框
   */
  //@formatter:off
  private void showVipDetailsDialog(final PxVipInfo vipInfo, final PxVipCardInfo vipCardInfo,final PxPaymentMode paymentMode) {
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("会员详情")
        .customView(R.layout.layout_dialog_vip_card_details, true)
        .positiveText("确定")
        .negativeText("取消")
        .canceledOnTouchOutside(false)
        .cancelable(false)
        .build();
    View customView = dialog.getCustomView();
    //确定按钮
    MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = dialog.getActionButton(DialogAction.NEGATIVE);
   //实体卡号
    TextView tvNum = (TextView) customView.findViewById(R.id.tv_vip_card_num);
    //会员名称
    TextView tvName = (TextView) customView.findViewById(R.id.tv_vip_card_name);
    //会员余额
    TextView tvBalance = (TextView) customView.findViewById(R.id.tv_vip_card_balance);
    //会员等级
  //  TextView tvLevel = (TextView) customView.findViewById(R.id.tv_vip_card_level);
    //使用余额
    final SwitchButton sbUseBalance = (SwitchButton) customView.findViewById(R.id.sb_switch_use_balance);//使用余额
    final RelativeLayout RlVipUseBalance = (RelativeLayout) customView.findViewById(R.id.rl_use_balance);
    //应收Rl
    final RelativeLayout rlReceivable = (RelativeLayout) customView.findViewById(R.id.rl_receivable);
    //应收Tv
    TextView tvReceivable = (TextView) customView.findViewById(R.id.tv_receivable);
    //实收Rl
    final RelativeLayout rlReceived = (RelativeLayout) customView.findViewById(R.id.rl_received);
    //实收Tv
    final EditText etReceived = (EditText) customView.findViewById(R.id.et_received);
    Logger.v("应收"+mReceivableAmount+"   -实收"+mTotalReceived+"    +找零"+mTotalChange+ "   -支付类优惠"+mTotalPayPrivilege+" ="+mWaitPayAmount);
    tvReceivable.setText(mWaitPayAmount + "");
    etReceived.setText(mWaitPayAmount + "");
    if (vipCardInfo!=null){
      RlVipUseBalance.setVisibility(View.GONE);
      etReceived.setEnabled(false);
    }else {
      RlVipUseBalance.setVisibility(View.VISIBLE);
      etReceived.setEnabled(true);
    }
    if (vipInfo==null){
       tvNum.setText(vipCardInfo.getMobile());
      tvName.setText(vipCardInfo.getIdcardNum());//实体卡内部卡号
      String balance= NumberFormatUtils.formatFloatNumber(vipCardInfo.getAccountBalance());//余额
       tvBalance.setText(balance);
    }else {
      dialog.setTitle("会员详情");
      tvNum.setText(vipInfo.getMobile());
     // tvLevel.setText(vipInfo.getLevel());//等级
      tvName.setText(vipInfo.getName());
      String balance= NumberFormatUtils.formatFloatNumber(vipInfo.getAccountBalance());
      tvBalance.setText(balance);
    }

    sbUseBalance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          rlReceivable.setVisibility(View.VISIBLE);
          rlReceived.setVisibility(View.VISIBLE);
        } else {
          rlReceivable.setVisibility(View.GONE);
          rlReceived.setVisibility(View.GONE);
        }
      }
    });
    //显示对话框
    dialog.show();
    addDialog(dialog);
    //确认按钮点击
    positiveBtn.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        //实收
        String inputReceived = etReceived.getText().toString().trim();
        DialogUtils.dismissDialog(dialog);
        if (sbUseBalance.isChecked()) {
          //使用实体卡消费
          useVipBalance(inputReceived, vipInfo,vipCardInfo, paymentMode);
        } else {
          //开启蒙层
           mAct.isShowProgress(true);
          justUseVipPrice();
        }
      }
    });
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
      cleanDis();
      resetCashPaymentMode();
      dialog.dismiss();
      }
    });
  }

  /**
   * 清空折扣 恢复原价
   */
  public void cleanDis() {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      cleanProductDiscount();
      cleanVipPrice();
      db.setTransactionSuccessful();
      //页面更新
      EventBus.getDefault().post(new RefreshCashBillListEvent());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 使用会员余额
   */
  private void useVipBalance(String etReceived, PxVipInfo vipInfo,PxVipCardInfo vipCardInfo, PxPaymentMode paymentMode) {
    //1.检测不为空输入金额
    if (TextUtils.isEmpty(etReceived)) {
      ToastUtils.show( "请填写实收金额");
      return;
    }
    //2.检测有效金额
    Double received = Double.valueOf(etReceived);
    if (received <= 0 || received > mWaitPayAmount) {
      ToastUtils.show( "实收金额填写错误");
      return;
    }
    //发起会员消费请求
    //开启蒙层
    mAct.isShowProgress(true);
    if (vipInfo==null){
      VipCardConsume.vipConsume(mAct,vipCardInfo,received,paymentMode,mCurrentOrderInfo);
    }else{
      VipConsume.vipConsume(mAct,vipInfo,received,paymentMode,mCurrentOrderInfo);
    }
  }
  /**
   * 是否使用会员价
   */
  private void isUseVipPrice(String s){
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      Logger.v("USE_VIP_CARD"+s);
      mCurrentOrderInfo.setUseVipCard(s);
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(mCurrentOrderInfo);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 接收会员消费请求事件
   */
  //@formatter:on
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveVipConsumeEvent(
      VipConsumeEvent event) {
    //开启蒙层
    mAct.isShowProgress(false);
    //会员成功消费
    if (event.isSuccess()) {
      double received = event.getReceived();
      PxVipInfo vipInfo = event.getVipInfo();
      PxVipCardInfo vipCardInfo = event.getVipCardInfo();
      PxPaymentMode paymentMode = event.getPaymentMode();
      String tradeNo = event.getTradeNo();
      //用于会员消费打印
      receivePrint = event.getReceived();
      vipInfoPrint = event.getVipInfo();

      mCurrentOrderInfo = DaoServiceUtil.getOrderInfoService()
          .queryBuilder()
          .where(PxOrderInfoDao.Properties.Id.eq(mCurrentOrderInfo.getId()))
          .unique();
      //是否使用会员价
      if (PxOrderInfo.USE_VIP_CARD_FALSE.equals(mCurrentOrderInfo.getUseVipCard())) {
        if (vipInfo != null) {
          isUseVipPrice(PxOrderInfo.USE_VIP_CARD_TRUE);
        }
      } else {
        if (vipCardInfo != null && !mType.equals(PxVipCardType.USE_VIP_PRICE)) {
          isUseVipPrice(PxOrderInfo.USE_VIP_CARD_FALSE);
        }
      }
      //生成支付信息
      SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
      db.beginTransaction();
      PxPayInfo vipPayInfo = null;
      try {
        //开启蒙层
        mAct.isShowProgress(true);
        //新建支付信息
        vipPayInfo = new PxPayInfo();
        //实收
        vipPayInfo.setReceived(received);
        //找零
        vipPayInfo.setChange(0D);
        //支付类优惠
        vipPayInfo.setPayPrivilege(0.0);
        //支付时间
        vipPayInfo.setPayTime(new Date());
        //所属订单
        vipPayInfo.setDbOrder(mCurrentOrderInfo);
        //支付方式 相关
        vipPayInfo.setPaymentId(paymentMode.getObjectId());
        vipPayInfo.setPaymentType(paymentMode.getType());
        vipPayInfo.setPaymentName(paymentMode.getName());
        vipPayInfo.setSalesAmount(paymentMode.getSalesAmount());
        //支付方式
        String type = paymentMode.getType();
        //实体卡
        if (PxPaymentMode.TYPE_VIP.equals(type)) {
          if (vipInfo != null) {
            vipPayInfo.setVipMobile(vipInfo.getMobile());
            vipPayInfo.setVipId(vipInfo.getObjectId());
            vipPayInfo.setTradeNo(tradeNo);
          } else {
            vipPayInfo.setVipMobile(vipCardInfo.getMobile());
            vipPayInfo.setVipId(vipCardInfo.getObjectId());
            vipPayInfo.setTradeNo(tradeNo);
            vipPayInfo.setIdCardNum(vipCardInfo.getIdcardNum());
            //新建vipInfo用于打印
            vipInfo = new PxVipInfo();
            vipInfo.setMobile(vipCardInfo.getMobile());
            vipInfo.setObjectId(vipCardInfo.getObjectId());
            vipInfo.setName(vipCardInfo.getIdcardNum() + "(实体卡会员)");
            vipInfo.setAccountBalance(vipCardInfo.getAccountBalance());
          }
        }
        DaoServiceUtil.getPayInfoService().saveOrUpdate(vipPayInfo);
        //更新页面
        updatePayInfo();
        mAct.isShowProgress(false);
        db.setTransactionSuccessful();
      } catch (Exception e) {
        e.printStackTrace();
        mAct.isShowProgress(false);
        vipPayInfo = null;
      } finally {
        db.endTransaction();
      }
      //PxPayInfo vipPayInfo = addPayInfoToOrderInfo(received, 0.0, vipInfo, null, tradeNo, null, paymentMode,0.0,"");
      //是否收款同时下单
      if (isReceiveMoneyAndOrder()) {
        receiveMoneyAndOrder();
      }
      //生成电子支付信息
      createEPaymentInfoPaySuccess(received, tradeNo, vipPayInfo, EPaymentInfo.TYPE_VIP_PAY);
      //配置信息
      PxSetInfo pxSetInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
      if (pxSetInfo != null) {
        //打印会员消费消息
        if (PxSetInfo.AUTO_PRINT_RECHARGE_TRUE.equals(pxSetInfo.getIsAutoPrintRechargeVoucher())) {
          //net
          PrinterTask task =
              new PrinterTask(BTPrintConstants.PRINT_MODE_VIP_CONSUME_RECORD, vipInfo, received);
          PrintTaskManager.printCashTask(task);
          //BT
          BTPrintTask btPrintTask =
              new BTPrintTask.Builder(BTPrintConstants.PRINT_MODE_VIP_CONSUME_RECORD).vipInfo(
                  vipInfo).consume(received).build();
          PrintEventManager.getManager().postBTPrintEvent(btPrintTask);
          //USB
          String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
          if (isSupportUSBPrint.equals("1")) return;
          try {
            if (mAppGpService.getGpService().getPrinterConnectStatus(1)
                == Constants.USB_CONNECT_STATUS) {
              printByUSBWithVipConsumePrinter(received, vipInfo);
            } else {
              mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
              printByUSBWithVipConsumePrinter(received, vipInfo);
            }
          } catch (RemoteException e) {
            e.printStackTrace();
          }
        }
      }
    } else {
      //会员消费余额不足的时候,清空折扣和会员价
      cleanDis();
      resetCashPaymentMode();
    }
  }

  /**
   * USB打印会员消费
   */
  // @formatter:off
  private void printByUSBWithVipConsumePrinter(double received, PxVipInfo vipInfo) {
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
            PrinterUsbData.printVipConsumeRecord(mAppGpService.getGpService(), received, vipInfo);
          }
        }
      } catch (RemoteException e) {
        ToastUtils.show( "打印机异常:" + e.getMessage());
        e.printStackTrace();
      }
    } else {
      ToastUtils.show( "设备未连接,请在更多模块配置普通打印机!");
    }
  }

  /**
   * 使用会员价
   */
  private void justUseVipPrice() {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      mCurrentOrderInfo.setUseVipCard(PxOrderInfo.USE_VIP_CARD_TRUE);
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(mCurrentOrderInfo);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
    EventBus.getDefault().post(new RefreshCashBillListEvent());
  }

  /**
   * 添加支付信息
   *
   * @param received 实收
   * @param change 找零
   * @param vipInfo 会员信息
   * @param voucherCode 凭证码
   * @param tradeNum 本地生成的流水号
   * @param freeReason 免单原因
   * @param paymentMode 支付方式
   * @param payPrivilege 支付类优惠
   */
  //@formatter:off
  private PxPayInfo addPayInfoToOrderInfo(
      double received, double change, PxVipInfo vipInfo, String voucherCode,
      String tradeNum, String freeReason, PxPaymentMode paymentMode,
      double payPrivilege, String ticketCode) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    PxPayInfo payInfo = null;
    try {
      //开启蒙层
      mAct.isShowProgress(true);
      //新建支付信息
      payInfo = new PxPayInfo();
      //实收
      payInfo.setReceived(received);
      //找零
      payInfo.setChange(change);
      //支付类优惠
      payInfo.setPayPrivilege(payPrivilege);
      //支付时间
      payInfo.setPayTime(new Date());
      //所属订单
      payInfo.setDbOrder(mCurrentOrderInfo);
      //支付方式 相关
      payInfo.setPaymentId(paymentMode.getObjectId());
      payInfo.setPaymentType(paymentMode.getType());
      payInfo.setPaymentName(paymentMode.getName());
      payInfo.setSalesAmount(paymentMode.getSalesAmount());
      //支付方式
      String type = paymentMode.getType();
      //实体卡
      if (PxPaymentMode.TYPE_VIP.equals(type)) {
        payInfo.setVipMobile(vipInfo.getMobile());
        payInfo.setVipId(vipInfo.getObjectId());
        payInfo.setTradeNo(tradeNum);
      }
      //现金
      if (PxPaymentMode.TYPE_CASH.equals(type)) {
        //关闭页面
        closePayView();
      }
      //pos机
      if (PxPaymentMode.TYPE_POS.equals(type)) {
        payInfo.setVoucherCode(voucherCode);
        //关闭页面
        closePayView();
      }
      //第三方
      if (PxPaymentMode.TYPE_ALIPAY.equals(type) || PxPaymentMode.TYPE_WEIXIN.equals(type) || PxPaymentMode.TYPE_WINGPAY.equals(type)) {
        payInfo.setTradeNo(tradeNum);
        //关闭页面
        closePayView();
      }
      //免单
      if (PxPaymentMode.TYPE_FREE.equals(type)) {
        payInfo.setRemarks(freeReason);
        //关闭页面
        closePayView();
      }
      //优惠券
      if (PxPaymentMode.TYPE_VOUCHER.equals(type)) {
        closePayView();
      }
      //优惠买单
      if (PxPaymentMode.TYPE_PRIVILEGE.equals(type)) {
        closePayView();
      }
      //团购券
      if (PxPaymentMode.TYPE_GROUP_COUPON.equals(type)) {
        payInfo.setTicketCode(ticketCode);
        closePayView();
      }
      //储存
      DaoServiceUtil.getPayInfoService().saveOrUpdate(payInfo);
      //更新页面,抹零不用更新
      if (PxPaymentMode.TYPE_TAIL.equals(type) == false){
        updatePayInfo();
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      payInfo = null;
    } finally {
      db.endTransaction();
      //关闭蒙层
      mAct.isShowProgress(false);
    }
    return payInfo;
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
      ToastUtils.show( "USB设备名为空");
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
      ToastUtils.show( "服务为空");
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
   * 打印客户联
   */
  @OnClick(R.id.btn_print_customer) public void printCustomer() {
    if (mCurrentOrderInfo == null || mDetailsList == null || mDetailsList.size() == 0) return;
    //网络打印
    printByNetAndBT(BTPrintConstants.PRINT_MODE_CUSTOMERS_AL);
    //USB打印客户联
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    try {
      if (mAppGpService.getGpService().getPrinterConnectStatus(1) == Constants.USB_CONNECT_STATUS) {
        printByUsbWithCustomerPrinter();
      } else {
        mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
        printByUsbWithCustomerPrinter();
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 打印USB客户联
   */
  private void printByUsbWithCustomerPrinter() {
    //Gp是否支持USB打印
    String isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      try {
        int type = mAppGpService.getGpService().getPrinterCommandType(1);
        if (type == GpCom.ESC_COMMAND) {
          PrinterUsbData.printBillWithCustomInfo(mAppGpService.getGpService(), mCurrentOrderInfo, mDetailsList, mCustomAmount);
        }
      } catch (RemoteException e) {
        ToastUtils.show("打印机异常:" + e.getMessage());
        e.printStackTrace();
      }
    } else {
      ToastUtils.show( "设备未连接,请在更多模块配置普通打印机!");
    }
  }

  /**
   * 网络、BT 打印收银
   */

  private void printByNetAndBT(int mode) {
    PrinterTask task = null;
    BTPrintTask.Builder builder = new BTPrintTask.Builder(mode);
    if (mode == BTPrintConstants.PRINT_MODE_CUSTOMERS_AL) {
      task = new PrinterTask(mode, mCurrentOrderInfo, mDetailsList, mCustomAmount);
      builder = builder.orderInfo(mCurrentOrderInfo)
          .orderDetailsList(mDetailsList)
          .customAmount(mCustomAmount);
    } else {
      task = new PrinterTask(mode, mCurrentOrderInfo, mDetailsList, mCustomAmount, mFinanceAmount);
      builder = builder.orderInfo(mCurrentOrderInfo)
          .orderDetailsList(mDetailsList)
          .customAmount(mCustomAmount)
          .financeAmount(mFinanceAmount);
    }
    PrintTaskManager.printCashTask(task);
    PrintEventManager.getManager().postBTPrintEvent(builder.build());
  }

  /**
   * 查询最低消费
   */
  //@formatter:off
  private void queryMinConsume(Double tail) {
    //查询附加费最低消费
    if (mCurrentOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
      TableOrderRel tableOrderRel = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
          .unique();
      final PxTableInfo dbTable = tableOrderRel.getDbTable();
      PxTableExtraRel tableExtraRel = DaoServiceUtil.getTableExtraRelService()
          .queryBuilder()
          .where(PxTableExtraRelDao.Properties.PxTableInfoId.eq(dbTable.getId()))
          .where(PxTableExtraRelDao.Properties.DelFlag.eq("0"))
          .unique();
      if (tableExtraRel == null) {
        operateOverBill(tail);
        return;
      }
      final PxExtraCharge dbExtraCharge = tableExtraRel.getDbExtraCharge();
      if (dbExtraCharge == null) {
        operateOverBill(tail);
        return;
      }
      //如果启用最低消费
      if (dbExtraCharge.getConsumeStatus().equals(PxExtraCharge.MINIMUM_CHARGE_TRUE)) {
        //如果不够最低消费
        if (mReceivableAmount < dbExtraCharge.getMinConsume()) {
          MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("警告")
              .content("当前累计消费为" + mReceivableAmount + "，少于当前桌台要求的最低额度" + dbExtraCharge.getMinConsume() + "，需要补上!")
              .positiveText("确定")
              .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(MaterialDialog dialog, DialogAction which) {
                  SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
                  db.beginTransaction();
                  try {
                    //新建附加费详情
                    PxExtraDetails extraDetails = new PxExtraDetails();
                    extraDetails.setIsComplement(true);
                    extraDetails.setPrice(dbExtraCharge.getMinConsume() - mReceivableAmount);
                    extraDetails.setDbOrder(mCurrentOrderInfo);
                    extraDetails.setPayPrice((double) 0);
                    extraDetails.setIsPrinted(false);
                    extraDetails.setTableName(dbTable.getName());
                    DaoServiceUtil.getExtraDetailsService().saveOrUpdate(extraDetails);
                    db.setTransactionSuccessful();
                     //更新页面
                    EventBus.getDefault().post(new RefreshCashBillListEvent());
                  } catch (Exception e) {
                    e.printStackTrace();
                  } finally {
                    db.endTransaction();
                  }
                }
              })
              .canceledOnTouchOutside(false)
              .show();
          addDialog(dialog);
        } else {
         operateOverBill(tail);
        }
      } else {
         operateOverBill(tail);
      }
    } else {
      operateOverBill(tail);
    }
  }


  /**
   * 接受在线支付(微信、支付宝)结账完成后页面刷新b 付款成功
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveOnLinePaySuccessEvent(OnLinePaySuccessEvent event) {
      int eventType = event.getType();
      String tradeNo = event.getTradeNo();
      double payMoney = event.getPayMoney();
      //配置信息,收款同时下单
      //receiveMoneyAndOrder();
      //恢复
      if (mEtPayThirdPaycode != null) {
        mEtPayThirdPaycode.setText("");
      }
      PxPayInfo payInfo = addPayInfoToOrderInfo(payMoney, 0.0, null, null, tradeNo, null, mCurrentPaymentMode,0.0,"");
     //是否收款同时下单
     if (isReceiveMoneyAndOrder()){
       receiveMoneyAndOrder();
     }
      //生成电子支付信息 成功付款
      String type = null;
      switch (eventType) {
        case OnLinePaySuccessEvent.ALI_PAY:
          type = EPaymentInfo.TYPE_ALI_PAY;
          //支付成功后发语音
          String contentAliPay = "已收到支付宝付款" + String.valueOf(payMoney) + "元";
          EventBus.getDefault().post(new SpeechEvent().setContent(contentAliPay));
          break;
        case OnLinePaySuccessEvent.WX_PAY:
          type = EPaymentInfo.TYPE_WX_PAY;
          //支付成功后发语音
          String contentWxPay = "已收到微信付款" + String.valueOf(payMoney) + "元";
          EventBus.getDefault().post(new SpeechEvent().setContent(contentWxPay));
          break;
        case OnLinePaySuccessEvent.BEST_PAY:
          type = EPaymentInfo.TYPE_BEST_PAY;
          //支付成功后发语音
          String contentBestPay = "已收到翼支付付款" + String.valueOf(payMoney) + "元";
          EventBus.getDefault().post(new SpeechEvent().setContent(contentBestPay));
          break;
      }
      //生成成功付款的电子支付信息
      if (payInfo != null) {
        createEPaymentInfoPaySuccess(payMoney, tradeNo, payInfo, type);
      }
  }

  /**
   * 生成成功付款 EPaymentInfo
   */
  private void createEPaymentInfoPaySuccess(double price,String tradeNo,PxPayInfo payInfo,String type){
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      String orderNo = mCurrentOrderInfo.getOrderNo();
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mCurrentOrderInfo.getId()))
          .unique();
      EPaymentInfo ePaymentInfo = new EPaymentInfo();
      ePaymentInfo.setDbOrder(mCurrentOrderInfo);
      ePaymentInfo.setOrderNo("No." + orderNo.substring(orderNo.length() - 4, orderNo.length()));
      ePaymentInfo.setPrice(price);
      ePaymentInfo.setTradeNo(tradeNo);
      ePaymentInfo.setDbPayInfo(payInfo);
      ePaymentInfo.setPayTime(new Date());
      ePaymentInfo.setStatus(EPaymentInfo.STATUS_PAYED);
      ePaymentInfo.setTableName((unique == null) ? "零售单" : unique.getDbTable().getName());
      ePaymentInfo.setIsHandled(EPaymentInfo.HAS_HANDLED);
      ePaymentInfo.setType(type);
      DaoServiceUtil.getEPaymentInfoService().saveOrUpdate(ePaymentInfo);
      db.setTransactionSuccessful();
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      db.endTransaction();
    }
  }

  /**
   * 查询网络支付详情
   */
  @OnClick(R.id.btn_query_net_pay_record)
  public void queryNetPayRecord(Button btn){
    if (mCurrentOrderInfo == null) {
      ToastUtils.show("当前无订单可查!");
      return;
    }
    QueryNetPayRecord.queryNetPayRecord(mAct,mCurrentOrderInfo);
  }

  /**
   * 数据更新时刷新页面
   */
  public void refreshOnDataUpdate(){
    loadPayment();
    closePayView();
  }

  /**
   * PrinterUsbData发送的未打开端口指令
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void onOpenPort(final OpenPortEvent event) {
    mCommonService.execute(new Runnable() {
      @Override public void run() {
        againPrintData(event);
      }
    });
  }

  /**
   * 重新打印数据
   */
  private void againPrintData(OpenPortEvent event){
    //ptksai pos 不支持USB打印
    String  isSupportUSBPrint = (String) SPUtils.get(mAct, Constants.SUPPORT_USB_PRINT, "");
    if (isSupportUSBPrint.equals("1")) return;
    //是否已配置开启USB打印
    boolean isPrint = (boolean) SPUtils.get(mAct, Constants.SWITCH_ORDINARY_PRINT, true);
    if (isPrint) {
      if(OpenPortEvent.OPEN_DRAWER_PORT.equals(event.getType())){
        PrinterUsbData.openDrawer(mAppGpService.getGpService());
      }else if(OpenPortEvent.FINANCE_BILL_PORT.equals(event.getType())){
        if(mCurrentOrderInfo != null && mDetailsList != null){
          PrinterUsbData.printBillWithFinanceInfo(mAppGpService.getGpService(), mCurrentOrderInfo, mDetailsList, mFinanceAmount);
        }
      }else if(OpenPortEvent.CUSTOM_BILL_PORT.equals(event.getType())){
        if(mCurrentOrderInfo != null && mDetailsList != null){
          PrinterUsbData.printBillWithCustomInfo(mAppGpService.getGpService(), mCurrentOrderInfo, mDetailsList, mCustomAmount);
        }
      }else if(OpenPortEvent.VIP_CONSUME_PORT.equals(event.getType())){
        if(vipInfoPrint != null){
          PrinterUsbData.printVipConsumeRecord(mAppGpService.getGpService(), receivePrint, vipInfoPrint);
        }
      }
    } else {
      mAct.runOnUiThread(new Runnable() {
        @Override public void run() {
          ToastUtils.show( "设备未连接,请在更多模块配置普通打印机!");
        }
      });
    }
  }


  /**
   * 是否收款同时下单
   * @return
   */
  private boolean isReceiveMoneyAndOrder(){
    PxSetInfo pxSetInfo = DaoServiceUtil.getSetInfoService().queryBuilder().unique();
    return  (pxSetInfo != null && PxSetInfo.AUTO_ORDER_TRUE.equals(pxSetInfo.getAutoOrder())) ;
  }

  /**
   * 收款同时下单
   */
  private void receiveMoneyAndOrder(){
      CashBillFragment cashBillFragment = (CashBillFragment) mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
      if (cashBillFragment != null){
        cashBillFragment.orderBill();
        cashBillFragment.refreshHasPayInfo();
      }
  }
  /**
   * 是否支持扫码支付
   */
  //@formatter:on
  private void supportScanPay() {
    Button btnScanPay = (Button) mThirdView.findViewById(R.id.btn_scan_pay);
    if (mAct.mIsHDX065) {
      btnScanPay.setVisibility(View.VISIBLE);
      btnScanPay.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          //进入扫码Activity
          Intent intent = new Intent(mAct, ScanPayActivity.class);
          startActivity(intent);
        }
      });
    }
  }

  /**
   * 接收扫码支付结果
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void receiveScanCode(ScanCodeEvent event) {
    //支付金额
    String payMoney = mTvPayThirdReceived.getText().toString().trim();
    //支付条码
    String payCode = event.getPayCode();
    if (payCode.length() > 10) {
      goPay(payMoney, payCode);
    }
  }

  /**
   * 开钱箱
   */
  private void openCashBox() {
    //开钱箱
    if (PxPaymentMode.OPEN_CASH_BOX_TRUE.equals(mCurrentPaymentMode.getOpenBox())) {
      //usb
      if (mAppGpService != null) {
        try {
          if (mAppGpService.getGpService().getPrinterConnectStatus(1)
              == Constants.USB_CONNECT_STATUS) {
            PrinterUsbData.openDrawer(mAppGpService.getGpService());
          } else {
            mAppGpService.getGpService().openPort(1, PortParameters.USB, mDeviceName, 0);
            PrinterUsbData.openDrawer(mAppGpService.getGpService());
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      //开钱箱
      mCommonService.execute(new Runnable() {
        @Override public void run() {
          //net and BT
          PrintEventManager.getManager().postOpenCash();
          if (mAct.mIsHDX065) {
            HdxUtil.SetV12Power(1);
          }
        }
      });
    }
  }

  /**
   * 上传结账完毕订单
   */
  private void uploadSingleOrder(final PxOrderInfo orderInfo) {
    mCommonService.execute(new Runnable() {
      @Override public void run() {
        if (!NetUtils.isConnected(mAct)) return;
        UpLoadOrder instance = UpLoadOrder.getInstance();
        instance.uploadSingleOrder(orderInfo);
      }
    });
  }
}