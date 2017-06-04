package com.psi.easymanager.ui.activity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kyleduo.switchbutton.SwitchButton;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.PxProductMethodRefDao;
import com.psi.easymanager.dao.PxProductRemarksDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.UpdateProdInfoListStatusEvent;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxProductMethodRef;
import com.psi.easymanager.module.PxProductRemarks;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.PromotioDetailsHelp;
import com.psi.easymanager.utils.RegExpUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.SwipeBackLayout;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by zjq on 2016/5/30.
 * 未下单商品Activity
 */
public class UnOrderProdActivity extends BaseActivity {
  //名称
  @Bind(R.id.tv_prod_name) TextView mTvProdName;
  //价格
  @Bind(R.id.tv_price) TextView mTvPrice;
  //数量
  @Bind(R.id.tv_num) TextView mTvNum;
  //是否等待
  @Bind(R.id.sb_wait) SwitchButton mSbWait;
  //滑动容器
  @Bind(R.id.content_view) SwipeBackLayout mContentView;
  //多单位布局
  @Bind(R.id.rl_multiple_num) RelativeLayout mRlMultipleNum;
  //多单位数量
  @Bind(R.id.tv_mult_num) TextView mTvMultNum;
  //多单位分割线
  @Bind(R.id.divider_2) View mDivider2;
  //规格Tags
  @Bind(R.id.tags_format) TagFlowLayout mTagsFormat;
  //做法Tags
  @Bind(R.id.tags_method) TagFlowLayout mTagsMethod;
  //备注Tags
  @Bind(R.id.tags_remarks) TagFlowLayout mTagsRemarks;
  //设为赠品
  @Bind(R.id.tv_gift_title) TextView mTvGiftTitle;
  //设为赠品
  @Bind(R.id.sb_gift) SwitchButton mSbGift;
  //自定义备注
  @Bind(R.id.et_custom_remark) EditText mEtCustomRemark;
  //Intent Key
  public static final String INTENT_COLLECTION = "Collection";
  //汇总信息
  private PxOrderDetails mDetails;
  //汇总信息id
  private long mDetailsId;
  //修改的数量
  private Double mNum;
  //修改的多单位数量
  private Double mMultNum;
  //原始数量
  private Double mOriginalNum;
  //原始多单位
  private Double mOriginalMultNum;

  //规格Adapter
  private TagAdapter mFormatTagAdapter;
  //做法Adapter
  private TagAdapter mMethodTagAdapter;
  //备注Adapter
  private TagAdapter mRemarksAdapter;

  //商品
  private PxProductInfo mProductInfo;
  //订单
  private PxOrderInfo mOrderInfo;
  //原始规格
  private PxFormatInfo mFormatInfo;
  //原始做法
  private PxMethodInfo mMethodInfo;

  //所选的规格
  private PxFormatInfo mNewFormatInfo;
  //所选的做法
  private PxMethodInfo mNewMethodInfo;

  //所有规格rel
  private List<PxProductFormatRel> mFormatRelList;
  //所有备注rel
  private List<PxProductRemarks> mRemarksList;

  @Override protected int provideContentViewId() {
    return R.layout.activity_unorder_prod;
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
    //数量
    mNum = mDetails.getNum();
    //多单位数量
    mMultNum = mDetails.getMultipleUnitNumber();
    //原始数量
    mOriginalNum = mDetails.getNum();
    //原始多单位
    mOriginalMultNum = mDetails.getMultipleUnitNumber();
    //Info
    mProductInfo = mDetails.getDbProduct();
    mFormatInfo = mDetails.getDbFormatInfo();
    mMethodInfo = mDetails.getDbMethodInfo();
    mOrderInfo = mDetails.getDbOrder();

    //初始化View
    initView();
    //查询规格
    queryFormat();
    //查询做法
    queryMethod();
    //可否设为赠品
    queryCanBeGift();
    //查询备注
    queryRemarks();
  }

  /**
   * 查询备注
   */
  //@formatter:on
  private void queryRemarks() {
    //备注验证
    mRemarksList = DaoServiceUtil.getProdRemarksService()
        .queryBuilder()
        .where(PxProductRemarksDao.Properties.DelFlag.eq("0"))
        .list();
    if (mRemarksList != null && mRemarksList.size() != 0) {
      //TagAdapter
      mRemarksAdapter = new TagAdapter<PxProductRemarks>(mRemarksList) {
        @Override public View getView(FlowLayout parent, int position, PxProductRemarks remarks) {
          TextView tv = (TextView) LayoutInflater.from(UnOrderProdActivity.this)
              .inflate(R.layout.item_tags_remark, mTagsRemarks, false);
          tv.setText(remarks.getRemarks());
          return tv;
        }
      };
      mTagsRemarks.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
        @Override public boolean onTagClick(View view, int position, FlowLayout parent) {
          PxProductRemarks pxProductRemarks = mRemarksList.get(position);
          if (mEtCustomRemark.getText() != null && mEtCustomRemark.getText().toString().trim().equals("") == false) {
            mEtCustomRemark.append(" ," + pxProductRemarks.getRemarks());
          } else {
            mEtCustomRemark.append(pxProductRemarks.getRemarks());
          }
          return true;
        }
      });
      mTagsRemarks.setAdapter(mRemarksAdapter);
    }
    //默认
    mEtCustomRemark.setText(mDetails.getRemarks()+"");
  }

  /**
   * 可否为赠品
   */
  private void queryCanBeGift() {
    if (mProductInfo.getIsGift().equals(PxProductInfo.IS_GIFT)) {
      mTvGiftTitle.setVisibility(View.VISIBLE);
      mSbGift.setVisibility(View.VISIBLE);
    } else {
      mTvGiftTitle.setVisibility(View.GONE);
      mSbGift.setVisibility(View.GONE);
    }
  }

  /**
   * 初始化View
   */
  //@formatter:off
  private void initView() {
    mTvProdName.setText(mDetails.getDbProduct().getName());
    mTvPrice.setText(mDetails.getUnitPrice() + "/" + mDetails.getDbProduct().getUnit());
    //是否等待
    if (mDetails.getStatus().equals(PxOrderDetails.STATUS_DELAY)) {
      mSbWait.setChecked(true);
    }
    //多单位
    if (mDetails.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      mTvMultNum.setText(NumberFormatUtils.formatFloatNumber(mDetails.getMultipleUnitNumber()) + mDetails.getDbProduct().getUnit());
    } else {
      mRlMultipleNum.setVisibility(View.GONE);
      mDivider2.setVisibility(View.GONE);
    }
    mTvNum.setText(mDetails.getNum() + "");
    //是否为赠品
    if (mDetails.getIsGift().equals(PxOrderDetails.GIFT_TRUE)) {
      mSbGift.setChecked(true);
    }
  }

  /**
   * 查询规格
   */
  //@formatter:on
  private void queryFormat() {
    //查询可用的规格引用关系
    QueryBuilder<PxProductFormatRel> formatRelQb = DaoServiceUtil.getProductFormatRelService()
        .queryBuilder()
        .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
        .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()));
    Join<PxProductFormatRel, PxFormatInfo> formatJoin =
        formatRelQb.join(PxProductFormatRelDao.Properties.PxFormatInfoId, PxFormatInfo.class);
    formatJoin.where(PxFormatInfoDao.Properties.DelFlag.eq("0"));
    final List<PxProductFormatRel> formatRelList = formatRelQb.list();
    mFormatRelList = formatRelList;

    if (formatRelList != null && formatRelList.size() != 0) {
      mFormatTagAdapter = new TagAdapter<PxProductFormatRel>(formatRelList) {
        @Override public View getView(FlowLayout parent, int position, PxProductFormatRel rel) {
          TextView tv = (TextView) LayoutInflater.from(UnOrderProdActivity.this)
              .inflate(R.layout.item_tags_format, mTagsFormat, false);
          PxFormatInfo format = rel.getDbFormat();

          //有效的促销计划
          PxPromotioDetails validPromotioDetails =
              PromotioDetailsHelp.getValidPromotioDetails(mOrderInfo.getDbPromotioById(), format,
                  mProductInfo);
          String realPrice = null;
          if (validPromotioDetails != null) {
            realPrice = "促销价:" + String.valueOf(validPromotioDetails.getPromotionalPrice());
          } else {
            realPrice = String.valueOf(rel.getPrice());
          }

          if (PxProductFormatRel.STATUS_STOP_SALE.equals(rel.getStatus())) {
            tv.setText(format.getName() + "(" + realPrice + "元)" + "(停售)");
            tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
          } else {
            if (rel.getStock() != null && rel.getStock() > 0.0) {
              tv.setText(
                  format.getName() + "(" + realPrice + "元)(余" + NumberFormatUtils.formatFloatNumber(
                      rel.getStock()) + ")");
            } else {
              tv.setText(format.getName() + "(" + realPrice + "元)");
            }
          }

          return tv;
        }
      };
      mTagsFormat.setAdapter(mFormatTagAdapter);
      mTagsFormat.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
        @Override public void onSelected(Set<Integer> selectPosSet) {
          if (selectPosSet.size() == 1) {
            String posString = selectPosSet.toString();
            int pos = Integer.parseInt(posString.substring(1, 2));
            mNewFormatInfo = formatRelList.get(pos).getDbFormat();
            //促销计划
            PxPromotioDetails validPromotioDetails =
                PromotioDetailsHelp.getValidPromotioDetails(mOrderInfo.getDbPromotioById(),
                    mNewFormatInfo, mProductInfo);
            //价格显示
            PxProductFormatRel rel = mFormatRelList.get(pos);
            double price = validPromotioDetails != null ? validPromotioDetails.getPromotionalPrice()
                : rel.getPrice();
            mTvPrice.setText(price + "/" + mProductInfo.getUnit());
          } else {
            mNewFormatInfo = null;
            //价格显示
            mTvPrice.setText(mProductInfo.getPrice() + "/" + mProductInfo.getUnit());
          }
        }
      });
    }
    //默认选中
    if (mFormatInfo != null && mFormatTagAdapter != null) {
      PxProductFormatRel currentRel = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mFormatInfo.getId()))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
          .unique();
      if (currentRel == null) return;
      for (int i = 0; i < formatRelList.size(); i++) {
        PxProductFormatRel rel = formatRelList.get(i);
        if (currentRel.getObjectId().equals(rel.getObjectId())) {
          mNewFormatInfo = rel.getDbFormat();
          mFormatTagAdapter.setSelectedList(i);
          //促销计划
          PxPromotioDetails validPromotioDetails =
              PromotioDetailsHelp.getValidPromotioDetails(mOrderInfo.getDbPromotioById(),
                  mNewFormatInfo, mProductInfo);
          double realPrice = validPromotioDetails == null ? rel.getPrice()
              : validPromotioDetails.getPromotionalPrice();
          //价格显示
          mTvPrice.setText(realPrice + "/" + mProductInfo.getUnit());
          break;
        }
      }
    }
  }

  /**
   * 查询做法
   */
  private void queryMethod() {
    //查询可用的做法引用关系
    QueryBuilder<PxProductMethodRef> methodRelQb = DaoServiceUtil.getProductMethodRelService()
        .queryBuilder()
        .where(PxProductMethodRefDao.Properties.DelFlag.eq("0"))
        .where(PxProductMethodRefDao.Properties.PxProductInfoId.eq(mProductInfo.getId()));
    Join<PxProductMethodRef, PxMethodInfo> methodJoin =
        methodRelQb.join(PxProductMethodRefDao.Properties.PxMethodInfoId, PxMethodInfo.class);
    methodJoin.where(PxMethodInfoDao.Properties.DelFlag.eq("0"));
    final List<PxProductMethodRef> methodRelList = methodRelQb.list();

    if (methodRelList != null && methodRelList.size() != 0) {
      mMethodTagAdapter = new TagAdapter<PxProductMethodRef>(methodRelList) {
        @Override public View getView(FlowLayout parent, int position, PxProductMethodRef rel) {
          TextView tv = (TextView) LayoutInflater.from(UnOrderProdActivity.this)
              .inflate(R.layout.item_tags_format, mTagsMethod, false);
          PxMethodInfo method = rel.getDbMethod();
          tv.setText(method.getName());
          return tv;
        }
      };
      mTagsMethod.setAdapter(mMethodTagAdapter);
      mTagsMethod.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
        @Override public void onSelected(Set<Integer> selectPosSet) {
          if (selectPosSet.size() == 1) {
            String posString = selectPosSet.toString();
            int pos = Integer.parseInt(posString.substring(1, 2));
            mNewMethodInfo = methodRelList.get(pos).getDbMethod();
          } else {
            mNewMethodInfo = null;
          }
        }
      });
    }
    //默认选中
    if (mMethodInfo != null && mMethodTagAdapter != null) {
      PxProductMethodRef currentRel = DaoServiceUtil.getProductMethodRelService()
          .queryBuilder()
          .where(PxProductMethodRefDao.Properties.DelFlag.eq("0"))
          .where(PxProductMethodRefDao.Properties.PxMethodInfoId.eq(mMethodInfo.getId()))
          .where(PxProductMethodRefDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
          .unique();
      if (currentRel == null) return;
      for (int i = 0; i < methodRelList.size(); i++) {
        PxProductMethodRef rel = methodRelList.get(i);
        if (currentRel.getObjectId().equals(rel.getObjectId())) {
          mNewMethodInfo = rel.getDbMethod();
          mMethodTagAdapter.setSelectedList(i);
        }
      }
    }
  }

  /**
   * 加
   */
  //@formatter:on
  @OnClick(R.id.btn_add) public void addNum(Button button) {
    mNum += 1;
    mTvNum.setText(mNum + "");
  }

  /**
   * 减
   */
  @OnClick(R.id.btn_reduce) public void reduceNum(Button button) {
    if (mNum.doubleValue() > 1) {
      mNum -= 1;
      mTvNum.setText(mNum + "");
    }
  }

  /**
   * 数量直接修改
   */
  @OnClick(R.id.tv_num) public void setNum() {
    new MaterialDialog.Builder(this).title("警告")
        .content("输入数量")
        .inputType(InputType.TYPE_CLASS_NUMBER)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .inputRange(1, 4)
        .input("数量", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence input) {
            dialog.getActionButton(DialogAction.POSITIVE)
                .setEnabled(RegExpUtils.matchinpuNum(
                    dialog.getInputEditText().getText().toString().trim()));
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            String s = dialog.getInputEditText().getText().toString();
            Integer num = Integer.valueOf(s);
            mNum = num.doubleValue();
            if ((mDetails.getDbProduct()
                .getMultipleUnit()
                .equals(PxProductInfo.IS_TWO_UNIT_TURE))) {
              mTvNum.setText(mNum + mDetails.getDbProduct().getOrderUnit());
            } else {
              mTvNum.setText(mNum + mDetails.getDbProduct().getUnit());
            }
          }
        })
        .show();
  }

  /**
   * 多单位数量
   */
  //@formatter:off
  @OnClick(R.id.tv_mult_num) public void setMultipleNum() {
    new MaterialDialog.Builder(this).title("警告")
        .content("重量")
        .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .input("数量", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence input) {
            if (input.toString().trim() !=null && !input.toString().toString().equals("") && input.toString().length() > 6){
              ToastUtils.showShort(App.getContext(),"输入过长!");
              dialog.getInputEditText().setText("");
              return;
            }

            if (input.toString() == null
                || input.toString().trim().equals("")
                || !RegExpUtils.match2DecimalPlaces(input.toString())
                || Double.valueOf(input.toString().trim()).doubleValue() <= 0
                || input.toString().trim().length() > 4) {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            } else {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            }
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            mMultNum = Double.valueOf(dialog.getInputEditText().getText().toString());
            mTvMultNum.setText(mMultNum + "/" + mProductInfo.getUnit());
          }
        })
        .show();
  }

  /**
   * 确定
   */
  @OnClick(R.id.btn_confirm) public void confirmModify() {
    //规格
    if (mFormatRelList != null && mFormatRelList.size() != 0) {
      if (mNewFormatInfo == null) {
        ToastUtils.showShort(App.getContext(), "请选择规格");
        return;
      }
    }
    //校验
    PxProductInfo unique = DaoServiceUtil.getProductInfoDao()
        .queryBuilder()
        .where(PxProductInfoDao.Properties.ObjectId.eq(mProductInfo.getObjectId()))
        .unique();
    if (unique == null) return;
    //有规格
    if (mNewFormatInfo != null) {
      QueryBuilder<PxProductFormatRel> formatRelQb = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
          .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mNewFormatInfo.getId()));
      PxProductFormatRel productFormatRel = formatRelQb.unique();
      //沽清
      if (productFormatRel != null && PxProductFormatRel.STATUS_STOP_SALE.equals(productFormatRel.getStatus())) {
        //双单位
        if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)){
          //规格未变
          if (mFormatInfo != null && mFormatInfo.getId() == mNewFormatInfo.getId()){
            if (mMultNum > mOriginalMultNum){
              ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
              return;
            }
          }
          //规格有变
          if (mFormatInfo == null || mFormatInfo.getId() != mNewFormatInfo.getId()){
            if (mMultNum >  productFormatRel.getStock()){
              ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
              return;
            }
          }
        }
        //非双单位
        else {
          //规格未变
          if (mFormatInfo != null && mFormatInfo.getId() == mNewFormatInfo.getId()){
            if (mNum > mOriginalNum){
              ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
              return;
            }
          }
          //规格有变
          if (mFormatInfo == null || mFormatInfo.getId() != mNewFormatInfo.getId()){
            if (mNum >  productFormatRel.getStock()){
              ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
              return;
            }
          }
        }
      }
      //余量
      if (PxProductFormatRel.STATUS_ON_SALE.equals(productFormatRel.getStatus()) && productFormatRel.getStock() != null && productFormatRel.getStock() != 0) {
        Double stock = productFormatRel.getStock();
        //双单位
        if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)){
          //规格未变
          if (mFormatInfo != null && mFormatInfo.getId() == mNewFormatInfo.getId()){
            if (stock.doubleValue() < (mMultNum - mOriginalMultNum)) {
              ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
              return;
            }
          }
          //规格有变
          if (mFormatInfo == null || mFormatInfo.getId() != mNewFormatInfo.getId()){
            if (mMultNum >  productFormatRel.getStock()){
              ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
              return;
            }
          }
        }
        //非双单位
        else {
          //规格未变
          if (mFormatInfo != null && mFormatInfo.getId() == mNewFormatInfo.getId()){
            if (stock.doubleValue() < (mNum  - mOriginalNum)) {
              ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
              return;
            }
          }
          //规格有变
          if (mFormatInfo == null || mFormatInfo.getId() != mNewFormatInfo.getId()){
            if (mNum >  productFormatRel.getStock()){
              ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
              return;
            }
          }
        }
      }
    }
    //无规格
    else {
      //沽清
      if (unique.getStatus().equals(PxProductInfo.STATUS_STOP_SALE)) {
        //双单位
        if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)){
          if (mMultNum > mOriginalMultNum){
            ToastUtils.showShort(App.getContext(), "剩余数量不足");
            return;
          }
        }
        //非双单位
        else {
          if (mNum > mOriginalNum){
            ToastUtils.showShort(App.getContext(), "剩余数量不足");
            return;
          }
        }
      }
      //余量
      if (unique.getStatus().equals(PxProductInfo.STATUS_ON_SALE) && unique.getOverPlus() != null && unique.getOverPlus() != 0) {
        Double overPlus = unique.getOverPlus();
        //双单位
        if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)){
          if (overPlus.doubleValue() < (mMultNum - mOriginalMultNum)) {
            ToastUtils.showShort(App.getContext(), "剩余数量不足");
            return;
          }
        }
        //非双单位
        else {
          if (overPlus.doubleValue() < (mNum - mOriginalNum)) {
            ToastUtils.showShort(App.getContext(), "剩余数量不足");
            return;
          }
        }
      }
    }
    //@formatter:off
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //删除
      deleteProd();
      //新加
      addProd(mSbGift.isChecked(),mSbWait.isChecked(), mNum, mMultNum);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
    //刷新数据
    EventBus.getDefault().post(new RefreshCashBillListEvent());
    //关闭
    finish();
  }

  /**
   * 删除商品
   */
  //@formatter:off
  private void deleteProd() {
    mProductInfo = DaoServiceUtil.getProductInfoService()
                  .queryBuilder()
                  .where(PxProductInfoDao.Properties.ObjectId.eq(mProductInfo.getObjectId()))
                  .unique();
    DaoServiceUtil.getOrderDetailsService().delete(mDetails);
    //沽清 有规格
    if (mFormatInfo != null) {
      QueryBuilder<PxProductFormatRel> formatRel = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
          .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mFormatInfo.getId()));
      PxProductFormatRel productFormatRel = formatRel.unique();
      if (productFormatRel.getStatus().equals(PxProductFormatRel.STATUS_STOP_SALE) || (productFormatRel.getStatus().equals(PxProductFormatRel.STATUS_ON_SALE) && productFormatRel.getStock() != null && productFormatRel.getStock() != 0)) {
        if (PxProductInfo.IS_TWO_UNIT_TURE.equals(mProductInfo.getMultipleUnit())) {
          productFormatRel.setStock(Double.valueOf(NumberFormatUtils.formatFloatNumber(productFormatRel.getStock() + mOriginalMultNum)));
        } else {
          productFormatRel.setStock(Double.valueOf(NumberFormatUtils.formatFloatNumber(productFormatRel.getStock() + mOriginalNum)));
        }
        productFormatRel.setStatus(PxProductFormatRel.STATUS_ON_SALE);
      }
      //储存
      DaoServiceUtil.getProductFormatRelService().saveOrUpdate(productFormatRel);
    }
    //沽清 无规格
    else {
      if (mProductInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE) || ((mProductInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) && mProductInfo.getOverPlus() != null && mProductInfo.getOverPlus() != 0)) {
        mProductInfo = DaoServiceUtil.getProductInfoService()
            .queryBuilder()
            .where(PxProductInfoDao.Properties.ObjectId.eq(mProductInfo.getObjectId()))
            .unique();
        if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          mProductInfo.setOverPlus(Double.valueOf(NumberFormatUtils.formatFloatNumber(mProductInfo.getOverPlus() + mOriginalMultNum)));
        } else {
          mProductInfo.setOverPlus(Double.valueOf(NumberFormatUtils.formatFloatNumber(mProductInfo.getOverPlus() + mOriginalNum)));
        }
        mProductInfo.setStatus(PxProductInfo.STATUS_ON_SALE);
        DaoServiceUtil.getProductInfoService().saveOrUpdate(mProductInfo);
        EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
      }
    }
  }

  /**
   * 添加商品
   */
  //@formatter:on
  private void addProd(boolean isGift, boolean isDelay, Double num, Double multNum) {
    DaoServiceUtil.getProductInfoService().refresh(mProductInfo);
    //备注
    String remarks = mEtCustomRemark.getText().toString().trim();
    //新规格下 促销计划
    PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(mOrderInfo.getDbPromotioById(), mNewFormatInfo, mProductInfo);
    //新建Details
    PxOrderDetails details = new PxOrderDetails();
    //默认不清空
    details.setIsClear(false);
    //数量
    details.setNum(num);
    //多单位数量
    details.setMultipleUnitNumber(multNum);
    //订单
    details.setDbOrder(mOrderInfo);
    //折扣率
    details.setCurrentDiscRate();
    //价格
    if (mNewFormatInfo != null) {
      PxProductFormatRel rel = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mNewFormatInfo.getId()))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
          .unique();
      double unitPrice = rel == null ? mProductInfo.getPrice() : rel.getPrice();
      double unitVipPrice = rel == null ? mProductInfo.getVipPrice() : rel.getVipPrice();
      if (validPromotioDetails != null) {
        unitPrice = validPromotioDetails.getPromotionalPrice();
        unitVipPrice = validPromotioDetails.getPromotionalPrice();
      }
      //单价
      details.setUnitPrice(unitPrice);
      details.setUnitVipPrice(unitVipPrice);
      //总价
      if (mDetails.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
        details.setPrice(details.getUnitPrice() * details.getMultipleUnitNumber());
        details.setVipPrice(details.getUnitVipPrice() * details.getMultipleUnitNumber());
      } else {
        details.setPrice(details.getUnitPrice() * details.getNum());
        details.setVipPrice(details.getUnitVipPrice() * details.getNum());
      }
    } else {
      //单价
      details.setUnitPrice(validPromotioDetails == null ? mProductInfo.getPrice()
          : validPromotioDetails.getPromotionalPrice());
      details.setUnitVipPrice(validPromotioDetails == null ? mProductInfo.getVipPrice()
          : validPromotioDetails.getPromotionalPrice());
      //总价
      if (mDetails.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
        details.setPrice(details.getUnitPrice() * details.getMultipleUnitNumber());
        details.setVipPrice(details.getUnitVipPrice() * details.getMultipleUnitNumber());
      } else {
        details.setPrice(details.getUnitPrice() * details.getNum());
        details.setVipPrice(details.getUnitVipPrice() * details.getNum());
      }
    }
    //商品
    details.setDbProduct(mProductInfo);
    //下单状态
    details.setOrderStatus(PxOrderDetails.ORDER_STATUS_UNORDER);
    //商品状态
    if (isDelay) {
      details.setStatus(PxOrderDetails.STATUS_DELAY);
    } else {
      details.setStatus(PxOrderDetails.STATUS_ORIDINARY);
    }
    //已上菜
    details.setIsServing(false);
    //规格
    details.setDbFormatInfo(mNewFormatInfo);
    //做法
    details.setDbMethodInfo(mNewMethodInfo);
    //是否为套餐Details
    details.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
    //是否是套餐内Details
    details.setInCombo(PxOrderDetails.IN_COMBO_FALSE);
    //是否为赠品
    if (isGift) {
      details.setIsGift(PxOrderDetails.GIFT_TRUE);
      //覆盖价格
      details.setUnitPrice(0.0);
      details.setUnitVipPrice(0.0);
      details.setPrice(0.0);
      details.setVipPrice(0.0);
    } else {
      details.setIsGift(PxOrderDetails.GIFT_FALSE);
    }
    //备注
    details.setRemarks(remarks);
    //objId
    details.setObjectId(UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis());
    //储存
    DaoServiceUtil.getOrderDetailsService().save(details);

    //@formatter:off
    //剩余数量 有规格
    if (mNewFormatInfo != null) {
      QueryBuilder<PxProductFormatRel> formatRel = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
          .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mNewFormatInfo.getId()));
      PxProductFormatRel productFormatRel = formatRel.unique();
      if (productFormatRel.getStock() != null) {
        if (PxProductInfo.IS_TWO_UNIT_TURE.equals(mProductInfo.getMultipleUnit())) {
          productFormatRel.setStock(Double.valueOf(NumberFormatUtils.formatFloatNumber(productFormatRel.getStock() - multNum)));
        } else {
          productFormatRel.setStock(Double.valueOf(NumberFormatUtils.formatFloatNumber(productFormatRel.getStock() - num)));
        }
        if (productFormatRel.getStock() == 0) {
          productFormatRel.setStatus(PxProductFormatRel.STATUS_STOP_SALE);
        }
        //储存
        DaoServiceUtil.getProductFormatRelService().saveOrUpdate(productFormatRel);
      }
    }
    //剩余数量 无规格
    else {
      if (mProductInfo.getOverPlus() != null) {
        if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          mProductInfo.setOverPlus(Double.valueOf(NumberFormatUtils.formatFloatNumber(mProductInfo.getOverPlus() - multNum)));
        } else {
          mProductInfo.setOverPlus(Double.valueOf(NumberFormatUtils.formatFloatNumber(mProductInfo.getOverPlus() - num)));
        }
        if (mProductInfo.getOverPlus() == 0) {
          mProductInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
        }
        //储存
        DaoServiceUtil.getProductInfoService().saveOrUpdate(mProductInfo);
      }
      //刷新商品列表状态
      EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
    }
  }


  /**
   * 删除商品
   */
  //@formatter:on
  @OnClick(R.id.btn_delete) public void delete() {
    new MaterialDialog.Builder(this).title("警告")
        .content("是否删除该商品?")
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
            db.beginTransaction();
            try {
              //删除
              deleteProd();
              db.setTransactionSuccessful();
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              db.endTransaction();
            }
            //刷新数据
            EventBus.getDefault().post(new RefreshCashBillListEvent());
            //关闭
            finish();
          }
        })
        .show();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}
