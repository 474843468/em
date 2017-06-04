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
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductMethodRefDao;
import com.psi.easymanager.dao.PxProductRemarksDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CashBillAddItemEvent;
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
import com.psi.easymanager.module.PxPromotioInfo;
import com.psi.easymanager.utils.KeyBoardUtils;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by dorado on 2016/5/31.
 */
public class AddProdActivity extends BaseActivity {

  //滑动容器
  @Bind(R.id.content_view) SwipeBackLayout mContentView;
  //名称
  @Bind(R.id.tv_prod_name) TextView mTvName;
  //价格
  @Bind(R.id.tv_price) TextView mTvPrice;
  //加
  @Bind(R.id.btn_add) Button mBtnAdd;
  //点菜数量
  @Bind(R.id.tv_num) TextView mTvNum;
  //减
  @Bind(R.id.btn_reduce) Button mBtnReduce;
  //双单位数量
  @Bind(R.id.tv_mult_num) TextView mTvMultNum;
  //双单位
  @Bind(R.id.rl_multiple_num) RelativeLayout mRlMultNum;
  //延迟上菜
  @Bind(R.id.sb_wait) SwitchButton mSbWait;
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

  //Intent key
  public static final String PROD = "Prod";
  //Intent key
  public static final String ORDER = "Order";
  //商品
  private PxProductInfo mProductInfo;
  //订单
  private PxOrderInfo mOrderInfo;
  //点菜数量
  private double mOrderNum = 1;
  //多单位数量
  private double mMultipleNum = 0;

  //所选的规格
  private PxFormatInfo mFormatInfo;
  //所选的做法
  private PxMethodInfo mMethodInfo;

  //规格Adapter
  private TagAdapter mFormatTagAdapter;

  //备注适配器
  private TagAdapter mRemarksAdapter;

  //所有规格rel
  private List<PxProductFormatRel> mFormatRelList;
  //所有备注list
  private List<PxProductRemarks> mRemarksList;
  //所选规格rel
  private PxProductFormatRel productFormatRel;
  //促销计划
  private PxPromotioInfo mDbPromotioInfo;
  private PxOrderDetails mDetails;

  @Override protected int provideContentViewId() {
    return R.layout.activity_add_prod;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    mContentView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        //隐藏软键盘
        if (mEtCustomRemark.hasFocus()) {
          KeyBoardUtils.hideSoftInput(mEtCustomRemark);
        }
        finish();
      }
    });
    //商品
    mProductInfo = (PxProductInfo) getIntent().getSerializableExtra(PROD);
    //订单
    mOrderInfo = (PxOrderInfo) getIntent().getSerializableExtra(ORDER);
    //促销计划
    mDbPromotioInfo = mOrderInfo.getDbPromotioById();
    //初始化视图
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
   * 关闭
   */
  @OnClick(R.id.btn_cancel) public void cancel() {
    finish();
  }

  /**
   * 查询备注
   */
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
          TextView tv = (TextView) LayoutInflater.from(AddProdActivity.this)
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
   * 查询规格
   */
  //@formatter:off
  private void queryFormat() {
    //查询可用的规格引用关系
    QueryBuilder<PxProductFormatRel> formatRelQb = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()));
    Join<PxProductFormatRel, PxFormatInfo> formatJoin = formatRelQb.join(PxProductFormatRelDao.Properties.PxFormatInfoId, PxFormatInfo.class);
    formatJoin.where(PxFormatInfoDao.Properties.DelFlag.eq("0"));
    final List<PxProductFormatRel> formatRelList = formatRelQb.list();
    mFormatRelList = formatRelList;
    if (formatRelList != null && formatRelList.size() != 0) {
      mFormatTagAdapter = new TagAdapter<PxProductFormatRel>(formatRelList) {
        @Override public View getView(FlowLayout parent, int position, PxProductFormatRel rel) {
          TextView tv = (TextView) LayoutInflater.from(AddProdActivity.this).inflate(R.layout.item_tags_format, mTagsFormat, false);
          PxFormatInfo format = rel.getDbFormat();
          //有效的促销计划
          PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(mDbPromotioInfo,format,mProductInfo);
          String realPrice = null;
          if (validPromotioDetails != null){
            realPrice = "促销价:"+String.valueOf(validPromotioDetails.getPromotionalPrice());
          }else{
           realPrice = String.valueOf(rel.getPrice());
          }

          if(PxProductFormatRel.STATUS_STOP_SALE.equals(rel.getStatus())){
            tv.setText(format.getName() + "(" + realPrice + "元)"+"(停售)");
            tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
          }else {
            if(rel.getStock() != null && rel.getStock() > 0.0){
              tv.setText(format.getName()+ "(" + realPrice + "元)(余" + NumberFormatUtils.formatFloatNumber(rel.getStock())+")");
            }else {
              tv.setText(format.getName() + "(" + realPrice + "元)");
            }
          }
          return tv;
        }
      };
      mTagsFormat.setAdapter(mFormatTagAdapter);
      //默认选中
      mFormatTagAdapter.setSelectedList(0);

      mFormatInfo = mFormatRelList.get(0).getDbFormat();

      //修改title 价格
      PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(mDbPromotioInfo,mFormatInfo,mProductInfo);
      if (validPromotioDetails != null){
        mTvPrice.setText(validPromotioDetails.getPromotionalPrice()+"/"+mProductInfo.getUnit());
      }
      //点击事件
      mTagsFormat.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
        @Override public void onSelected(Set<Integer> selectPosSet) {
          if (selectPosSet.size() == 1) {
            String posString = selectPosSet.toString();
            int pos = Integer.parseInt(posString.substring(1, 2));
            mFormatInfo = formatRelList.get(pos).getDbFormat();
            //促销计划
            PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(mDbPromotioInfo,mFormatInfo,mProductInfo);

            //价格显示
            PxProductFormatRel rel = mFormatRelList.get(pos);
            double price = validPromotioDetails != null ? validPromotioDetails.getPromotionalPrice() : rel.getPrice();
            mTvPrice.setText(price + "/" + mProductInfo.getUnit());
          } else {
            mFormatInfo = null;
            //价格显示
            mTvPrice.setText(mProductInfo.getPrice() + "/" + mProductInfo.getUnit());
          }
        }
      });
    }
  }


  /**
   * 查询做法
   */
  private void queryMethod() {
    //查询可用的规格引用关系
    QueryBuilder<PxProductMethodRef> methodRelQb = DaoServiceUtil.getProductMethodRelService()
        .queryBuilder()
        .where(PxProductMethodRefDao.Properties.DelFlag.eq("0"))
        .where(PxProductMethodRefDao.Properties.PxProductInfoId.eq(mProductInfo.getId()));
    Join<PxProductMethodRef, PxMethodInfo> methodJoin =
        methodRelQb.join(PxProductMethodRefDao.Properties.PxMethodInfoId, PxMethodInfo.class);
    methodJoin.where(PxMethodInfoDao.Properties.DelFlag.eq("0"));
    final List<PxProductMethodRef> methodRelList = methodRelQb.list();

    if (methodRelList != null && methodRelList.size() != 0) {
      mTagsMethod.setAdapter(new TagAdapter<PxProductMethodRef>(methodRelList) {
        @Override public View getView(FlowLayout parent, int position, PxProductMethodRef rel) {
          TextView tv = (TextView) LayoutInflater.from(AddProdActivity.this)
              .inflate(R.layout.item_tags_format, mTagsMethod, false);
          PxMethodInfo methodInfo = rel.getDbMethod();
          tv.setText(methodInfo.getName());
          return tv;
        }
      });
      mTagsMethod.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
        @Override public void onSelected(Set<Integer> selectPosSet) {
          if (selectPosSet.size() == 1) {
            String posString = selectPosSet.toString();
            int pos = Integer.parseInt(posString.substring(1, 2));
            mMethodInfo = methodRelList.get(pos).getDbMethod();
          } else {
            mMethodInfo = null;
          }
        }
      });
    }
  }

  /**
   * 初始化View
   */
  private void initView() {
    //名称
    mTvName.setText(mProductInfo.getName());
    //价格
    mTvPrice.setText(mProductInfo.getPrice() + "/" + mProductInfo.getUnit());
    //点菜数量
    mTvNum.setText(mOrderNum + "");
    //结账数量
    mTvMultNum.setText(mMultipleNum + "/" + mProductInfo.getUnit());
    //隐藏双单位
    if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_FALSE)) {
      mRlMultNum.setVisibility(View.GONE);
    }
  }

  /**
   * 加
   */
  //@formatter:on
  @OnClick(R.id.btn_add) public void addNum(Button button) {
    mOrderNum += 1;
    mTvNum.setText(mOrderNum + "");
  }

  /**
   * 减
   */
  @OnClick(R.id.btn_reduce) public void reduceNum(Button button) {
    if (mOrderNum > 1) {
      mOrderNum -= 1;
      mTvNum.setText(mOrderNum + "");
    }
  }

  /**
   * 多单位数量
   */
  //@formatter:off
  @OnClick(R.id.rl_multiple_num) public void setMultipleNum() {
    new MaterialDialog.Builder(this).title("提示")
        .content("重量")
        .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .input("重量", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence input) {
            if (input.toString().trim() != null && !input.toString().toString().equals("") && input.toString().length() > 6){
              ToastUtils.showShort(App.getContext(),"输入过长!");
              dialog.getInputEditText().setText("");
              return;
            }
            if (
                input.toString() == null
                || input.toString().trim().equals("")
                || !RegExpUtils.match2DecimalPlaces(input.toString())
                || Double.valueOf(input.toString().trim()).doubleValue() <= 0
                ) {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            } else {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            }
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            mMultipleNum = Double.valueOf(dialog.getInputEditText().getText().toString());
            mTvMultNum.setText(mMultipleNum + "/" + mProductInfo.getUnit());
          }
        })
        .show();
  }


  /**
   * 数量直接修改
   */
  @OnClick(R.id.tv_num)
  public void setNum(){
    new MaterialDialog.Builder(this).title("提示")
        .content("输入数量")
        .inputType(InputType.TYPE_CLASS_NUMBER)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .inputRange(1,4)
        .input("数量", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence input) {

          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            String s = dialog.getInputEditText().getText().toString();
            Integer num = Integer.valueOf(s);
            mOrderNum = num;
            mTvNum.setText(mOrderNum + mProductInfo.getOrderUnit());
          }
        })
        .show();
  }

  /**
   * 确定
   */
  //@formatter:on
  @OnClick(R.id.btn_confirm) public void confirm() {
    if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
      if (mMultipleNum == 0) {
        ToastUtils.showShort(App.getContext(), "请输入重量");
        return;
      }
    }
    //停售
    if (mProductInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE)) {
      ToastUtils.showShort(App.getContext(), "该商品处于停售状态,不能添加");
      return;
    }
    //剩余数量
    if (mProductInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)
        && mProductInfo.getOverPlus() != null && mProductInfo.getOverPlus() != 0) {
      Double overPlus = mProductInfo.getOverPlus();
      //多单位
      if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
        if (overPlus.doubleValue() < mMultipleNum) {
          ToastUtils.showShort(App.getContext(), "剩余数量不足");
          return;
        }
      }
      //非多单位
      else {
        if (overPlus.doubleValue() < mOrderNum) {
          ToastUtils.showShort(App.getContext(), "剩余数量不足");
          return;
        }
      }
    }

    //该商品有规格
    if (mFormatRelList != null && mFormatRelList.size() != 0) {
      if (mFormatInfo == null) {
        ToastUtils.showShort(App.getContext(), "请选择规格");
        return;
      }
      //判断该商品规格状态
      getProductFormat();
      if (PxProductFormatRel.STATUS_STOP_SALE.equals(productFormatRel.getStatus())) {
        ToastUtils.showShort(App.getContext(), "该规格处于停售状态,不能添加");
        return;
      }
    }
    //规格剩余数量
    if (mFormatRelList != null && mFormatRelList.size() != 0) {
      if (mFormatInfo == null) {
        ToastUtils.showShort(App.getContext(), "请选择规格");
        return;
      }
      //判断该商品规格余量状态
      getProductFormat();
      if (PxProductInfo.STATUS_ON_SALE.equals(mProductInfo.getStatus())
          && PxProductFormatRel.STATUS_ON_SALE.equals(productFormatRel.getStatus())
          && productFormatRel.getStock() != null && productFormatRel.getStock() != 0) {
        Double stock = productFormatRel.getStock();
        //多单位
        if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
          if (stock.doubleValue() < mMultipleNum) {
            ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
            return;
          }
        }
        //非多单位
        else {
          if (stock.doubleValue() < mOrderNum) {
            ToastUtils.showShort(App.getContext(), "该规格剩余数量不足");
            return;
          }
        }
      }
    }

    //@formatter:off
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //备注
      String remarks = mEtCustomRemark.getText().toString();
      //Condition 规格
      PxProductFormatRel formatRel = null;
      //规格
      if (mFormatInfo != null) {
        //规格价格
        formatRel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mFormatInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
            .unique();
      }

      //促销计划
      PxPromotioDetails validPromotioDetails = PromotioDetailsHelp.getValidPromotioDetails(mDbPromotioInfo, mFormatInfo, mProductInfo);
      //规格价
      double unitPrice = formatRel == null ? mProductInfo.getPrice() : formatRel.getPrice();
      double unitVipPrice = formatRel == null ? mProductInfo.getVipPrice() : formatRel.getVipPrice();
      //促销价
      if (validPromotioDetails != null) {
        unitPrice = validPromotioDetails.getPromotionalPrice();
        unitVipPrice = validPromotioDetails.getPromotionalPrice();
      }

      //新建Details
     mDetails =  new PxOrderDetails();
      //默认不清空
      mDetails.setIsClear(false);
      //数量
      mDetails.setNum(mOrderNum);
      //多单位数量
      mDetails.setMultipleUnitNumber(mMultipleNum);
      //订单
      mDetails.setDbOrder(mOrderInfo);
      //折扣率
      mDetails.setCurrentDiscRate();
      //单价
      mDetails.setUnitPrice(unitPrice);
      //会员单价
      mDetails.setUnitVipPrice(unitVipPrice);
      //价格
      if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
        mDetails.setPrice(mDetails.getUnitPrice() * mDetails.getMultipleUnitNumber());
        mDetails.setVipPrice(mDetails.getUnitVipPrice() * mDetails.getMultipleUnitNumber());
      } else {
        mDetails.setPrice(mDetails.getUnitPrice() * mDetails.getNum());
        mDetails.setVipPrice(mDetails.getUnitVipPrice() * mDetails.getNum());
      }
      //商品
      mDetails.setDbProduct(mProductInfo);
      //下单状态
      mDetails.setOrderStatus(PxOrderDetails.ORDER_STATUS_UNORDER);
      //商品状态
      if (mSbWait.isChecked()) {
        mDetails.setStatus(PxOrderDetails.STATUS_DELAY);
      } else {
        mDetails.setStatus(PxOrderDetails.STATUS_ORIDINARY);
      }
      //已上菜
      mDetails.setIsServing(false);
      //折扣率
      mDetails.setCurrentDiscRate();
      //规格信息
      mDetails.setDbFormatInfo(mFormatInfo);
      //做法
      mDetails.setDbMethodInfo(mMethodInfo);
      //是否为套餐Details
      mDetails.setIsComboDetails(PxOrderDetails.IS_COMBO_FALSE);
      //是否是套餐内Details
      mDetails.setInCombo(PxOrderDetails.IN_COMBO_FALSE);
      //是否为赠品
      if (mSbGift.isChecked()) {
        mDetails.setIsGift(PxOrderDetails.GIFT_TRUE);
        //覆盖价格
        mDetails.setPrice(0.0);
        mDetails.setVipPrice(0.0);
      } else {
        mDetails.setIsGift(PxOrderDetails.GIFT_FALSE);
      }
      //备注
      mDetails.setRemarks(remarks.toString());
      //objId
      mDetails.setObjectId(UUID.randomUUID().toString().replace("-","") + System.currentTimeMillis());
      //储存
      DaoServiceUtil.getOrderDetailsService().save(mDetails);
      //通知CashBill更新页面
      EventBus.getDefault().post(new CashBillAddItemEvent().setDetails(mDetails));
     // EventBus.getDefault().post(new RefreshCashBillListEvent());
      //剩余数量
      if (productFormatRel != null) {
        if (PxProductFormatRel.STATUS_ON_SALE.equals(productFormatRel.getStatus())) {
          if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
            if (productFormatRel.getStock() != null) {
              productFormatRel.setStock(Double.valueOf(
                  NumberFormatUtils.formatFloatNumber(productFormatRel.getStock() - mMultipleNum)));
            } else {
              productFormatRel.setStock(null);
            }
          } else {
            if (productFormatRel.getStock() != null) {
              productFormatRel.setStock(Double.valueOf(
                  NumberFormatUtils.formatFloatNumber(productFormatRel.getStock() - mOrderNum)));
            } else {
              productFormatRel.setStock(null);
            }
          }
          if (productFormatRel.getStock() != null && productFormatRel.getStock() == 0.0) {
            productFormatRel.setStatus(PxProductFormatRel.STATUS_STOP_SALE);
          }
          //储存
          DaoServiceUtil.getProductFormatRelService().saveOrUpdate(productFormatRel);
          //刷新余量页面
          EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
        }
      } else {
        if (mProductInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) {
          if (mProductInfo.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
            if (mProductInfo.getOverPlus() != null) {
              mProductInfo.setOverPlus(Double.valueOf(
                  NumberFormatUtils.formatFloatNumber(mProductInfo.getOverPlus() - mMultipleNum)));
            } else {
              mProductInfo.setOverPlus(null);
            }
          } else {
            if (mProductInfo.getOverPlus() != null) {
              mProductInfo.setOverPlus(Double.valueOf(
                  NumberFormatUtils.formatFloatNumber(mProductInfo.getOverPlus() - mOrderNum)));
            } else {
              mProductInfo.setOverPlus(null);
            }
          }
          if (mProductInfo.getOverPlus() != null && mProductInfo.getOverPlus() == 0.0) {
            mProductInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
          }
          //储存
          DaoServiceUtil.getProductInfoService().saveOrUpdate(mProductInfo);
          //刷新余量页面
          EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
        }
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
    //关闭
    finish();
  }

  /**
   * 获取该商品所选规格
   */
  private PxProductFormatRel getProductFormat() {
    QueryBuilder<PxProductFormatRel> formatRel = DaoServiceUtil.getProductFormatRelService()
        .queryBuilder()
        .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
        .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
        .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mFormatInfo.getId()));
    productFormatRel = formatRel.unique();
    return productFormatRel;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}
