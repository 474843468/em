package com.psi.easymanager.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.EPaymentAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.EPaymentInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.RefundAlipayEvent;
import com.psi.easymanager.module.EPaymentInfo;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.pay.alipay.RefundAlipay;
import com.psi.easymanager.pay.bestpay.RefundBestPay;
import com.psi.easymanager.pay.vip.RefundVipCard;
import com.psi.easymanager.pay.wxpay.WxRefundPay;
import com.psi.easymanager.utils.NetUtils;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.pay.vip.RefundVip;
import com.psi.easymanager.widget.SwipeBackLayout;
import java.text.SimpleDateFormat;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dorado on 2016/10/20.
 */
public class MessagesActivity extends BaseActivity
    implements EPaymentAdapter.ItemClickListener, View.OnClickListener {
  //滑动容器
  @Bind(R.id.content_view) SwipeBackLayout mContentView;
  //Rcv
  @Bind(R.id.rcv_messages) RecyclerView mRcvMessages;
  //左侧容器 临时替代
  @Bind(R.id.fl_left_container_temp) FrameLayout mFlLeftContainerTemp;
  //Data
  private List<EPaymentInfo> mEPaymentInfoList;

  //每页数量
  private static final int PAGE_NUM = 10;
  //当前页码
  private int mCurrentPage = 1;
  //总页码
  private int mTotalPage;
  private View mLeftContainer;//左侧容器
  //Adapter
  private EPaymentAdapter mEPaymentAdapter;//右侧
  private TextView mTvName;//交易人那么
  private TextView mTvPhone;//交易人电话
  private TextView mTvTable;//交易桌台号
  private TextView mTvOrder;//交易订单号
  private TextView mTvPayMoney;//交易金额
  private TextView mTvPayTradeNo;//交易流水号
  private TextView mTvPayStatus;//交易状态
  private TextView mTvPayTime;//交易时间
  private ImageButton mIbtnRefund;//退款按钮
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private int mCurrentPos;//当前pos

  @Override protected int provideContentViewId() {
    return R.layout.activity_messages;
  }

  //@formatter:off
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    EventBus .getDefault().register(this);
    mContentView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
    //初始化Rcv
    initRcv();
  }

  /**
   * 初始化Rcv
   */
  private void initRcv() {
    queryNum();
    queryData();

    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    mEPaymentAdapter = new EPaymentAdapter(this, mEPaymentInfoList);
    mRcvMessages.setLayoutManager(layoutManager);
    mRcvMessages.setAdapter(mEPaymentAdapter);
    mRcvMessages.setHasFixedSize(true);
    mEPaymentAdapter.setOnItemClickListener(this);
  }

  private void queryNum() {
    long count = DaoServiceUtil.getEPaymentInfoService().queryBuilder().count();
    mTotalPage = (int) Math.ceil(count / (double) PAGE_NUM);
  }

  private void queryData() {
    mEPaymentInfoList = DaoServiceUtil.getEPaymentInfoService()
        .queryBuilder()
        .orderDesc(EPaymentInfoDao.Properties.Id, EPaymentInfoDao.Properties.PayTime, EPaymentInfoDao.Properties.Status)
        .limit(PAGE_NUM)
        .offset(PAGE_NUM * (mCurrentPage - 1))
        .list();
  }

  /**
   * 上页 下页
   */
  @OnClick({ R.id.btn_last_page, R.id.btn_next_page }) public void chagnePage(Button button) {
    //查询总量
    queryNum();
    switch (button.getId()) {
      case R.id.btn_last_page:
        if (mCurrentPage > 1) {
          mCurrentPage -= 1;
        }
        break;
      case R.id.btn_next_page:
        if (mCurrentPage < mTotalPage) {
          mCurrentPage += 1;
          //查询数据
        }
        break;
    }
    //查询数据
    queryData();
    mEPaymentAdapter.setData(mEPaymentInfoList);
  }

  /**
   * Item Click
   */
  @Override public void onItemClick(int pos) {
    mCurrentPos = pos;
    initLeftContainer();
    EPaymentInfo paymentInfo = mEPaymentInfoList.get(pos);
    SpannableStringBuilder spSbStatus = new SpannableStringBuilder();
    //状态(0:已付款 1:已退款 2:付款过并已退款)
    switch (paymentInfo.getStatus()) {
      case EPaymentInfo.STATUS_PAYED:
        SpannableString paySuccSp = new SpannableString("状态: 支付成功");
        ForegroundColorSpan paySuccColor = new ForegroundColorSpan(Color.parseColor("#00bfa5"));
        paySuccSp.setSpan(paySuccColor, 3, paySuccSp.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spSbStatus.append(paySuccSp);
        mIbtnRefund.setVisibility(View.VISIBLE);
        break;
      case EPaymentInfo.STATUS_REFUND:
        SpannableString refundSuccSp = new SpannableString("状态: 退款成功");
        ForegroundColorSpan  refundSuccColor = new ForegroundColorSpan(Color.parseColor("#dd191d"));
        refundSuccSp.setSpan(refundSuccColor, 3, refundSuccSp.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spSbStatus.append(refundSuccSp);
        mIbtnRefund.setVisibility(View.INVISIBLE);
        break;
      case EPaymentInfo.STATUS_PAYED_AND_REFUND:
        SpannableString paySuccAndRefundSp = new SpannableString("状态: 支付成功");
        ForegroundColorSpan paySuccAndRefundColor = new ForegroundColorSpan(Color.parseColor("#0fffff"));
        paySuccAndRefundSp.setSpan(paySuccAndRefundColor, 3, paySuccAndRefundSp.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        SpannableString payAndRefundSuccSp = new SpannableString("(已退款)");
        ForegroundColorSpan payAndRefundSuccColor = new ForegroundColorSpan(Color.parseColor("#dd191d"));
        payAndRefundSuccSp.setSpan(payAndRefundSuccColor, 0, payAndRefundSuccSp.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        spSbStatus.append(paySuccAndRefundSp);
        spSbStatus.append(payAndRefundSuccSp);
        mIbtnRefund.setVisibility(View.INVISIBLE);
        break;
    }


    //类型(0:支付宝 1:微信 2:会员)
    String type = "";
    switch (paymentInfo.getType()) {
      case EPaymentInfo.TYPE_ALI_PAY:
        type = "支付宝支付: ";
        mTvPhone.setText("电话:***********");
        break;
      case EPaymentInfo.TYPE_WX_PAY:
        type = "微信支付: ";
        mTvPhone.setText("电话:***********");
        break;
      case EPaymentInfo.TYPE_BEST_PAY:
        type = "翼支付支付: ";
        mTvPhone.setText("电话:***********");
        break;
      case EPaymentInfo.TYPE_VIP_PAY:
        type = "会员卡支付: ";
        PxPayInfo payInfo = paymentInfo.getDbPayInfo();
        if (payInfo == null){
          mTvPhone.setText("电话:***********");
        }else{
          mTvPhone.setText(payInfo.getVipMobile());
        }
        break;
    }
    SpannableString spanOrder = new SpannableString("单号: " + paymentInfo.getOrderNo());
    ForegroundColorSpan orderColor = new ForegroundColorSpan(Color.parseColor("#dd191d"));
    spanOrder.setSpan(orderColor, 3, spanOrder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

    mTvTable.setText("桌号: " + paymentInfo.getTableName());
    mTvOrder.setText(spanOrder);
    mTvPayMoney.setText(type + paymentInfo.getPrice() + "元");
    mTvPayTime.setText("支付时间: " + sdf.format(paymentInfo.getPayTime()));
    mTvPayStatus.setText(spSbStatus);
    mTvPayTradeNo.setText("支付流水号:" + "\n" + paymentInfo.getTradeNo());
  }

  /**
   * 初始化左侧容器
   */
  private void initLeftContainer() {
    if (mLeftContainer == null) {
      mFlLeftContainerTemp.setVisibility(View.GONE);
      ViewStub vs = (ViewStub) findViewById(R.id.vs_left_container);
      mLeftContainer = vs.inflate();
      mTvName = (TextView) mLeftContainer.findViewById(R.id.tv_name);
      mTvPhone = (TextView) mLeftContainer.findViewById(R.id.tv_phone);
      mTvTable = (TextView) mLeftContainer.findViewById(R.id.tv_table);
      mTvOrder = (TextView) mLeftContainer.findViewById(R.id.tv_order);
      mTvPayMoney = (TextView) mLeftContainer.findViewById(R.id.tv_pay_money);
      mTvPayTradeNo = (TextView) mLeftContainer.findViewById(R.id.tv_pay_trade_no);
      mTvPayStatus = (TextView) mLeftContainer.findViewById(R.id.tv_pay_status);
      mTvPayTime = (TextView) mLeftContainer.findViewById(R.id.tv_pay_time);
      mIbtnRefund = (ImageButton) mLeftContainer.findViewById(R.id.ibtn_refund);
      mIbtnRefund.setOnClickListener(this);
    }
    if (mLeftContainer.getVisibility() != View.VISIBLE){
      mLeftContainer.setVisibility(View.VISIBLE);
    }
    if (mFlLeftContainerTemp.getVisibility() == View.VISIBLE){
      mFlLeftContainerTemp.setVisibility(View.GONE);
    }
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ibtn_refund:
        final MaterialDialog refundDialog = DialogUtils.showSimpleDialog(MessagesActivity.this, "退款", "确认退款?");
        MDButton posBtn = refundDialog.getActionButton(DialogAction.POSITIVE);
        posBtn.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            DialogUtils.dismissDialog(refundDialog);
            refundMoney();
          }
        });
        break;
    }
  }
  /**
   * 退款
   */
  private void refundMoney() {
    if (mCurrentPos < 0 || mCurrentPos > (mEPaymentInfoList.size() - 1)) return;
    EPaymentInfo ePaymentInfo = mEPaymentInfoList.get(mCurrentPos);
    PxPayInfo payInfo = ePaymentInfo.getDbPayInfo();
    if (payInfo == null) return;
    //判断订单状态
    PxOrderInfo dbOrder = payInfo.getDbOrder();
    if (PxOrderInfo.STATUS_FINISH.equals(dbOrder.getStatus())){
      ToastUtils.showShort(MessagesActivity.this,"订单已结账,无法退款!");
      return;
    }
    if (PxOrderInfo.STATUS_CANCEL.equals(dbOrder.getStatus())){
      ToastUtils.showShort(MessagesActivity.this,"订单已撤单,无法退款!");
      return;
    }
    //检查网络
    if (!NetUtils.isConnected(MessagesActivity.this)) {
      ToastUtils.showShort(App.getContext(), "请检查网络配置!");
      return;
    }
    switch (ePaymentInfo.getType()){
      case EPaymentInfo.TYPE_ALI_PAY://支付宝
        RefundAlipay.refundAlipay(RefundAlipay.FROM_MESSAGES,MessagesActivity.this,payInfo,null,0);
        break;
      case EPaymentInfo.TYPE_WX_PAY://微信
        WxRefundPay.refundWxPay(WxRefundPay.FROM_MESSAGES,MessagesActivity.this,payInfo,null,0);
        break;
      case EPaymentInfo.TYPE_BEST_PAY://翼支付
        RefundBestPay.refundBestPay(RefundBestPay.FROM_MESSAGES,MessagesActivity.this,payInfo,null,0);
        break;
      case EPaymentInfo.TYPE_VIP_PAY://会员
        if (!TextUtils.isEmpty(payInfo.getIdCardNum())){
          RefundVipCard.refundVipCard(RefundVipCard.FROM_MESSAGES,MessagesActivity.this,payInfo,null,0);//会员卡退款
        }else {
          RefundVip.refundVip(RefundVip.FROM_MESSAGES,MessagesActivity.this,payInfo,null,0);;//会员退款
        }
        break;
    }
  }

  /**
   * 接收退款结果 成功刷新页面
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void receiveRefundResultEvent(RefundAlipayEvent event){
    mCurrentPage = -1;
    mCurrentPos = -1;
    queryNum();
    queryData();
    mEPaymentAdapter.setData(mEPaymentInfoList);
    mFlLeftContainerTemp.setVisibility(View.VISIBLE);
    mLeftContainer.setVisibility(View.GONE);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
  }
}
