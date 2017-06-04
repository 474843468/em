package com.psi.easymanager.ui.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.RefundReasonAdapter;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.PxOptReasonDao;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.SpeechEvent;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOperationLog;
import com.psi.easymanager.module.PxOptReason;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.print.MakePrintDetails;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.utils.ListViewHeightUtil;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.RegExpUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.SwipeBackLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

import static com.psi.easymanager.common.App.getContext;

/**
 * Created by zjq on 2016/5/30.
 * 已下单商品Activity
 */
public class OrderedProdActivity extends BaseActivity {
  @Bind(R.id.tv_name) TextView mTvName;
  @Bind(R.id.tv_num) TextView mTvNum;
  @Bind(R.id.tv_price) TextView mTvPrice;
  @Bind(R.id.content_view) SwipeBackLayout mContentView;
  @Bind(R.id.tv_multiple_num) TextView mTvMultNum;
  @Bind(R.id.tv_format) TextView mTvFormat;
  @Bind(R.id.tv_method) TextView mTvMethod;
  @Bind(R.id.tv_remarks) TextView mTvRemarks;
  @Bind(R.id.btn_take_food) Button mBtnTakeFood;

  //汇总信息
  private PxOrderDetails mDetails;
  //汇总信息id
  private long mDetailsId;
  //商品
  private PxProductInfo mProductInfo;
  //订单
  private PxOrderInfo mOrderInfo;
  //规格
  private PxFormatInfo mFormatInfo;
  //做法
  private PxMethodInfo mMethodInfo;
  //Intent Key
  public static final String INTENT_COLLECTION = "Collection";
  //数量
  private int mOriginalNum;
  //多单位数量
  private double mOriginMultNum;
  //备注
  private String mRemarks;

  //退菜 点菜单位输入框
  private EditText mEtRefundOrderNum;
  //退菜 多单位输入框
  private EditText mEtRefundMultNum;
  //所选退菜原因
  private PxOptReason mRefundReason;
  //所有退菜原因
  private List<PxOptReason> mReasonList;

  @Override protected int provideContentViewId() {
    return R.layout.activity_ordered_prod;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    mContentView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
    //汇总信息
    mDetailsId = (long) getIntent().getSerializableExtra(INTENT_COLLECTION);
    mDetails = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.Id.eq(mDetailsId))
        .unique();
    //Info
    mProductInfo = mDetails.getDbProduct();
    mOrderInfo = mDetails.getDbOrder();
    mFormatInfo = mDetails.getDbFormatInfo();
    mMethodInfo = mDetails.getDbMethodInfo();
    mRemarks = mDetails.getRemarks();
    //原始数量
    mOriginalNum = mDetails.getNum().intValue();
    //原始多单位数量
    mOriginMultNum = mDetails.getMultipleUnitNumber();
    //初始化View
    initView();
  }

  /**
   * 呼叫取餐
   */
  //@formatter:on
  @OnClick(R.id.btn_take_food) public void takeFood() {
    if (mOrderInfo == null) return;
    String no = mOrderInfo.getOrderNo().substring(mOrderInfo.getOrderNo().length() - 6);
    String num = String.valueOf(Integer.parseInt(no));
    String content = "请" + num + "号到前台取餐";
    EventBus.getDefault().post(new SpeechEvent().setContent(content));
    //修改为已上菜
    mDetails.setIsServing(true);
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(mDetails);
    EventBus.getDefault().post(new RefreshCashBillListEvent());
  }

  /**
   * 初始化View
   */
  private void initView() {
    mTvName.setText(mProductInfo.getName());
    if (mDetails.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      mTvNum.setText(mDetails.getNum() + mProductInfo.getOrderUnit());
      mTvMultNum.setText(NumberFormatUtils.formatFloatNumber(mDetails.getMultipleUnitNumber())
          + mProductInfo.getUnit());
    } else {
      mTvNum.setText(mDetails.getNum() + mProductInfo.getUnit());
      mTvMultNum.setText("无");
    }
    //规格
    if (mFormatInfo != null) {
      PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mFormatInfo.getId()))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
          .unique();
      if (rel != null) {
        mTvFormat.setText(mFormatInfo.getName());
      }
    }
    mTvPrice.setText(mDetails.getUnitPrice() + "/" + mProductInfo.getUnit());
    //做法
    if (mMethodInfo != null) {
      mTvMethod.setText(mMethodInfo.getName());
    }
    //备注
    if (mRemarks != null) {
      mTvRemarks.setText(mRemarks);
    }
  }

  /**
   * 退菜点击
   */
  //@formatter:on
  @OnClick(R.id.btn_refund) public void refundClick() {
    if (mDetails.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      showMultUnitRefundDialog();
    } else {
      showRefundDialog();
    }
  }

  /**
   * 多单位商品退菜
   */
  //@formatter:off
  private void showMultUnitRefundDialog() {
    final MaterialDialog dialog = new MaterialDialog.Builder(this).title("退菜")
        .customView(R.layout.layout_dialog_modify_two_unit_prod, true)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .build();
    //确定按钮
    final MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
    //数量
    mEtRefundOrderNum = (EditText) dialog.getCustomView().findViewById(R.id.et_order_num);
    mEtRefundOrderNum.requestFocus();
    mEtRefundOrderNum.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().trim() != null && !s.toString().toString().equals("") && s.toString().length() > 6) {
          ToastUtils.showShort(OrderedProdActivity.this, "输入过长!");
          mEtRefundOrderNum.setText("");
          return;
        }

        boolean orderNumEtEnable =
            (s.toString().trim() != null
                && !s.toString().trim().equals("")
                && Integer.valueOf(s.toString()) >= 1
                && s.toString().trim().length() <= 4);
        boolean multNumEtEnable =
            (mEtRefundMultNum.getText().toString().trim() != null
                && !mEtRefundMultNum.getText().toString().trim().equals("")
                && ((RegExpUtils.match2DecimalPlaces(mEtRefundMultNum.getText().toString().trim())) && (Double.valueOf(mEtRefundMultNum.getText().toString()) > 0))
                && mEtRefundMultNum.getText().toString().trim().length() < 4);
        if (orderNumEtEnable && multNumEtEnable) {
          positiveBtn.setEnabled(true);
        } else {
          positiveBtn.setEnabled(false);
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });

    //重量数量
    mEtRefundMultNum = (EditText) dialog.getCustomView().findViewById(R.id.et_mult_num);
    mEtRefundMultNum.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().trim() != null && !s.toString().toString().equals("") && s.toString().length() > 4) {
          ToastUtils.showShort(OrderedProdActivity.this, "输入过长!");
          mEtRefundMultNum.setText("");
          return;
        }

        boolean multNumEtEnable =
            (s.toString().trim() != null
                && !s.toString().trim().equals("")
                && ((RegExpUtils.matchFloatNum(s.toString().trim())) && (Double.valueOf(s.toString()) > 0))
                && s.toString().trim().length() <= 4);
        boolean orderNumEtEnable =
            (mEtRefundOrderNum.getText().toString().trim() != null
                && !mEtRefundOrderNum.getText().toString().trim().equals("")
                && Integer.valueOf(mEtRefundOrderNum.getText().toString()) >= 1
                && mEtRefundOrderNum.getText().toString().trim().length() < 4);
        if (orderNumEtEnable && multNumEtEnable) {
          positiveBtn.setEnabled(true);
        } else {
          positiveBtn.setEnabled(false);
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });

    //@formatter:on
    ListView lv = (ListView) dialog.getCustomView().findViewById(R.id.lv_refund_reasons);
    //查询可用的退货原因
    mReasonList = DaoServiceUtil.getOptReasonService()
        .queryBuilder()
        .where(PxOptReasonDao.Properties.DelFlag.eq("0"))
        .where(PxOptReasonDao.Properties.Type.eq(PxOptReason.REFUND_REASON))
        .list();
    RefundReasonAdapter reasonAdapter =
        new RefundReasonAdapter(OrderedProdActivity.this, mReasonList);
    lv.setAdapter(reasonAdapter);
    ListViewHeightUtil.setListViewHeightBasedOnChildren(lv);
    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mRefundReason = mReasonList.get(position);
      }
    });

    mRefundReason = null;
    //显示
    dialog.show();
    positiveBtn.setEnabled(false);

    positiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
        int num = Integer.valueOf(mEtRefundOrderNum.getText().toString());
        double multNum = Double.valueOf(mEtRefundMultNum.getText().toString());
        //数量超出
        if (num > mOriginalNum || multNum > mOriginMultNum) {
          ToastUtils.showShort(OrderedProdActivity.this, "数量不能大于原始数量");
          return;
        }
        //数量错误
        if ((num == mOriginalNum && multNum != mOriginMultNum) || (num != mOriginalNum
            && multNum == mOriginMultNum)) {
          ToastUtils.showShort(OrderedProdActivity.this, "数量填写错误");
          return;
        }
        dialog.dismiss();
        refundProd(num, multNum);
      }
    });
  }

  /**
   * 普通商品退菜
   */
  //@formatter:on
  private void showRefundDialog() {
    final MaterialDialog dialog = new MaterialDialog.Builder(this).title("退菜")
        .customView(R.layout.layout_dialog_refund_prod, true)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .build();
    //确定按钮
    final MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
    //数量et
    mEtRefundOrderNum = (EditText) dialog.getCustomView().findViewById(R.id.et_num);
    mEtRefundOrderNum.requestFocus();
    mEtRefundOrderNum.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null && s.toString().trim().length() != 0 && s.toString().trim().length() < 4
            && Integer.valueOf(s.toString().trim()) <= mOriginalNum) {
          positiveBtn.setEnabled(true);
        } else {
          positiveBtn.setEnabled(false);
        }
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
    //退菜原因lv
    //@formatter:on
    ListView lv = (ListView) dialog.getCustomView().findViewById(R.id.lv_refund_reasons);
    //查询可用的退货原因
    mReasonList = DaoServiceUtil.getOptReasonService()
        .queryBuilder()
        .where(PxOptReasonDao.Properties.DelFlag.eq("0"))
        .where(PxOptReasonDao.Properties.Type.eq(PxOptReason.REFUND_REASON))
        .list();
    RefundReasonAdapter reasonAdapter =
        new RefundReasonAdapter(OrderedProdActivity.this, mReasonList);
    lv.setAdapter(reasonAdapter);
    ListViewHeightUtil.setListViewHeightBasedOnChildren(lv);
    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mRefundReason = mReasonList.get(position);
      }
    });
    mRefundReason = null;
    //显示
    dialog.show();
    positiveBtn.setEnabled(true);

    positiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String input = mEtRefundOrderNum.getText().toString();
        Integer num = Integer.valueOf(input);
        dialog.dismiss();
        refundProd(num, (double) 0);
      }
    });
  }

  /**
   * 退菜
   */
  //@formatter:off
  private void refundProd(Integer num, Double multNum) {
    SparseArray<PrintDetailsCollect> collectArray = new SparseArray<>();
    List<Long> printIdList = new ArrayList<>();
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    boolean isFail = false;
    try {
      //退菜时间
      Date refundDate = new Date();
      MakePrintDetails.makeNotMergePrintDetails(mDetails,refundDate,collectArray,(double)num,multNum,printIdList);
      //数量
      mDetails.setNum(mDetails.getNum() - num);
      mDetails.setMultipleUnitNumber(mDetails.getMultipleUnitNumber() - multNum);
      if (mDetails.getNum().doubleValue() == 0 && mDetails.getMultipleUnitNumber().doubleValue() == 0){
         mDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_REFUND);
      }
      //退货数量
      if (mDetails.getRefundNum() == null){
        mDetails.setRefundNum(num.doubleValue());
      } else {
        mDetails.setRefundNum(mDetails.getRefundNum() + num);
      }
      if (mDetails.getRefundMultNum() == null){
        mDetails.setRefundMultNum(multNum);
      } else {
        mDetails.setRefundMultNum(mDetails.getRefundMultNum() + multNum);
      }
      //价格
      if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
        mDetails.setPrice(mDetails.getUnitPrice() * mDetails.getMultipleUnitNumber());
        mDetails.setVipPrice(mDetails.getUnitVipPrice() * mDetails.getMultipleUnitNumber());
      } else {
        mDetails.setPrice(mDetails.getUnitPrice() * mDetails.getNum());
        mDetails.setVipPrice(mDetails.getUnitVipPrice() * mDetails.getNum());
      }
      DaoServiceUtil.getOrderDetailsService().saveOrUpdate(mDetails);
      //生成OperationRecord
      makeOperationRecord(mDetails,refundDate,num,multNum);
      //结束事务
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Logger.e(e.toString());
      isFail = true;
    } finally {
      db.endTransaction();
    }
    if (!isFail) {
      //后厨打印
      PrintTaskManager.printKitchenTask(collectArray, printIdList, true);
      //刷新数据
      EventBus.getDefault().post(new RefreshCashBillListEvent());
    }
    //关闭
    finish();
  }


  /**
   * 生成操作记录
   */
  //@formatter:on
  private void makeOperationRecord(PxOrderDetails details, Date refundDate, Integer num,
      Double multNum) {
    App app = (App) getContext();
    if (app == null) return;
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    PxOperationLog operationRecord = new PxOperationLog();
    operationRecord.setCid(office.getObjectId());
    //操作日期
    operationRecord.setOperaterDate(refundDate.getTime());
    //操作人员
    operationRecord.setOperater(app.getUser().getName());
    //订单序列号
    operationRecord.setOrderNo(details.getDbOrder().getOrderNo());
    //类型
    operationRecord.setType(PxOperationLog.TYPE_REFUND);
    //名称
    StringBuilder sb = new StringBuilder();
    sb.append(details.getDbProduct().getName());
    if (PxProductInfo.IS_TWO_UNIT_TURE.equals(details.getDbProduct().getMultipleUnit())) {
      sb.append("[" + multNum + details.getDbProduct().getUnit() + "]");
    } else {
      sb.append("[" + num + details.getDbProduct().getUnit() + "]");
    }
    if (details.getDbFormatInfo() != null) {
      sb.append(details.getDbFormatInfo().getName());
    }
    operationRecord.setProductName(sb.toString());
    //操作缘由
    if (mRefundReason != null) {
      operationRecord.setRemarks(mRefundReason.getName());
    }
    //双单位 价格
    if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      if (PxOrderInfo.USE_VIP_CARD_TRUE.equals(details.getDbOrder().getUseVipCard())) {
        operationRecord.setTotalPrice(details.getUnitVipPrice() * multNum);
      } else {
        operationRecord.setTotalPrice(details.getUnitPrice() * multNum);
      }
    }
    //非双单位 价格
    else {
      if (PxOrderInfo.USE_VIP_CARD_TRUE.equals(details.getDbOrder().getUseVipCard())) {
        operationRecord.setTotalPrice(details.getUnitVipPrice() * num);
      } else {
        operationRecord.setTotalPrice(details.getUnitPrice() * num);
      }
    }
    DaoServiceUtil.getOperationRecordService().saveOrUpdate(operationRecord);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}
