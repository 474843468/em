package com.psi.easymanager.ui.activity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.kyleduo.switchbutton.SwitchButton;
import com.psi.easymanager.R;
import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxMethodInfoDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.PxProductMethodRefDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.UpdateProdInfoListStatusEvent;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxProductMethodRef;
import com.psi.easymanager.utils.NumberFormatUtils;
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
import org.greenrobot.eventbus.EventBus;

/**
 * Created by dorado on 2016/8/12.
 */
public class ProdInfoActivity extends BaseActivity {
  @Bind(R.id.tv_name) TextView mTvName;
  @Bind(R.id.tv_price) TextView mTvPrice;
  @Bind(R.id.tv_privilege) TextView mTvPrivilege;
  @Bind(R.id.btn_stop_sale) Button mBtnStopSale;
  @Bind(R.id.content_view) SwipeBackLayout mContentView;
  //规格Tags
  @Bind(R.id.tags_format) TagFlowLayout mTagsFormat;
  //做法Tags
  @Bind(R.id.tags_method) TagFlowLayout mTagsMethod;

  @Bind(R.id.sb_open_label) Switch mSbSwitchPrintLabel;
  private String mProdId;//商品id
  private PxProductInfo mProductInfo;//商品信息
  //所有规格rel
  private List<PxProductFormatRel> mFormatRelList;
  //所选的规格
  private PxFormatInfo mFormatInfo;
  //所选规格rel
  private PxProductFormatRel productFormatRel;
  //规格Adapter
  private TagAdapter mFormatTagAdapter;

  public static final String PROD_OBJ_ID = "ProdObjId";

  @Override protected int provideContentViewId() {
    return R.layout.activity_prod_info;
  }

  //@formatter:off
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    //滑动关闭
    mContentView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
    mProdId = getIntent().getStringExtra(PROD_OBJ_ID);
    mProductInfo = DaoServiceUtil.getProductInfoService()
        .queryBuilder()
        .where(PxProductInfoDao.Properties.ObjectId.eq(mProdId))
        .unique();
    initView();
    //初始化规格数据
    initFormat();
    //初始化做法数据
    initMethod();
    //沽清状态
    if (mProductInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) {
      mBtnStopSale.setText("沽清");
    } else {
      mBtnStopSale.setText("取消沽清");
    }
  }

  /**
   * init view
   */
  private void initView() {
    //商品名
    mTvName.setText(mProductInfo.getName());
    //价格
    mTvPrice.setText(mProductInfo.getPrice() + "/" + mProductInfo.getUnit());
    //优惠
    if (mProductInfo.getIsDiscount().equals(PxProductInfo.IS_DISCOUNT_TRUE)) {
      mTvPrivilege.setText("允许打折\t" + "会员价:" + mProductInfo.getVipPrice() + "/" + mProductInfo.getUnit());
    } else {
      mTvPrivilege.setText("不允许打折\t" + "会员价:" + mProductInfo.getVipPrice() + "/" + mProductInfo.getUnit());
    }
    //标签打印
    boolean isPrintLabel = (mProductInfo.getIsLabel() == null || PxProductInfo.PRINT_LABEL_TRUE.equals(mProductInfo.getIsLabel()));
    mSbSwitchPrintLabel.setChecked(isPrintLabel);
    mSbSwitchPrintLabel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switchPrintLabel(isChecked);
      }
    });
  }

  /**
   * 关闭
   */
  @OnClick(R.id.btn_cancel) public void cancel() {
    finish();
  }

  /**
   * 初始化规格数据
   */
  private void initFormat(){
    QueryBuilder<PxProductFormatRel> formatRelQb = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()));
    Join<PxProductFormatRel, PxFormatInfo> formatJoin = formatRelQb.join(PxProductFormatRelDao.Properties.PxFormatInfoId, PxFormatInfo.class);
    formatJoin.where(PxFormatInfoDao.Properties.DelFlag.eq("0"));
    final List<PxProductFormatRel> formatRelList = formatRelQb.list();
    mFormatRelList = formatRelList;
    if (formatRelList != null && formatRelList.size() != 0) {
      //tagAdapter
      mTagsFormat.setAdapter(new TagAdapter<PxProductFormatRel>(formatRelList) {
        @Override public View getView(FlowLayout parent, int position, PxProductFormatRel rel) {
          TextView tv = (TextView) LayoutInflater.from(ProdInfoActivity.this).inflate(R.layout.item_tags_format, mTagsFormat, false);
          PxFormatInfo format = rel.getDbFormat();
          if(PxProductFormatRel.STATUS_STOP_SALE.equals(rel.getStatus())){
            tv.setText(format.getName()+"(停售)");
            tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
          } else {
            if(rel.getStock()!=null&&rel.getStock()>0.0){
              tv.setText(format.getName()+"(余" + NumberFormatUtils.formatFloatNumber(rel.getStock()) + ")");
            }else {
              tv.setText(format.getName());
            }
          }
          return tv;
        }
      });
      mTagsFormat.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
        @Override public void onSelected(Set<Integer> selectPosSet) {
          if (selectPosSet.size() == 1) {
            String posString = selectPosSet.toString();
            int pos = Integer.parseInt(posString.substring(1, 2));
            mFormatInfo = formatRelList.get(pos).getDbFormat();
            //获取当前规格
            getProductFormat();
            //价格显示
            PxProductFormatRel rel = mFormatRelList.get(pos);
            Double price = rel.getPrice();
            mTvPrice.setText(price + "/" + mProductInfo.getUnit());
            //优惠显示
            Double vipPrice = rel.getVipPrice();
            if (mProductInfo.getIsDiscount().equals(PxProductInfo.IS_DISCOUNT_TRUE)) {
              mTvPrivilege.setText("允许打折\t" + "会员价:" + vipPrice + "/" + mProductInfo.getUnit());
            } else {
              mTvPrivilege.setText("不允许打折\t" + "会员价:" + vipPrice + "/" + mProductInfo.getUnit());
            }
            if(PxProductFormatRel.STATUS_STOP_SALE.equals(productFormatRel.getStatus())){
              mBtnStopSale.setText("取消沽清");
            }else {
              mBtnStopSale.setText("沽清");
            }
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
   * 初始化做法数据
   */
  private void initMethod() {
    QueryBuilder<PxProductMethodRef> methodRefQb = DaoServiceUtil.getProductMethodRelService()
        .queryBuilder()
        .where(PxProductMethodRefDao.Properties.DelFlag.eq("0"))
        .where(PxProductMethodRefDao.Properties.PxProductInfoId.eq(mProductInfo.getId()));
    Join<PxProductMethodRef, PxMethodInfo> methodJoin = methodRefQb.join(PxProductMethodRefDao.Properties.PxMethodInfoId, PxMethodInfo.class);
    methodJoin.where(PxMethodInfoDao.Properties.DelFlag.eq("0"));
    final List<PxProductMethodRef> methodRefList = methodRefQb.list();
    if (methodRefList != null && methodRefList.size() != 0) {
      //tagAdapter
      mTagsMethod.setAdapter(new TagAdapter<PxProductMethodRef>(methodRefList) {
        @Override public View getView(FlowLayout parent, int position, PxProductMethodRef ref) {
          TextView tv = (TextView) LayoutInflater.from(ProdInfoActivity.this).inflate(R.layout.item_tags_format, mTagsMethod,false);
          PxMethodInfo methodInfo = ref.getDbMethod();
          tv.setText(methodInfo.getName());
          return tv;
        }
      });
    }
  }

  /**
   * 沽清
   */
  @OnClick(R.id.btn_stop_sale) public void stopSale() {
    if (mFormatRelList != null && mFormatRelList.size() != 0) {
      if (mFormatInfo == null) {
        ToastUtils.showShort(this, "请选择规格");
        return;
      }
      getProductFormat();
      if(PxProductFormatRel.STATUS_STOP_SALE.equals(productFormatRel.getStatus())){
        mBtnStopSale.setText("取消沽清");
        productFormatRel.setStock(null);//设置规格剩余数量为空
        productFormatRel.setStatus(PxProductFormatRel.STATUS_ON_SALE);//正常
      }else {
        mBtnStopSale.setText("沽清");
        productFormatRel.setStock(0.0);//设置规格剩余数量为0
        productFormatRel.setStatus(PxProductFormatRel.STATUS_STOP_SALE);//停售
      }
      DaoServiceUtil.getProductFormatRelService().saveOrUpdate(productFormatRel);
      //规格和剩余数量设置后刷新
      refreshSetData();
    } else {
      if (mProductInfo.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) {
        mProductInfo.setStatus(PxProductInfo.STATUS_STOP_SALE);
        mProductInfo.setOverPlus(0.0);
        mBtnStopSale.setText("取消沽清");
      } else {
        mProductInfo.setStatus(PxProductInfo.STATUS_ON_SALE);
        mProductInfo.setOverPlus(null);
        mBtnStopSale.setText("沽清");
      }
      DaoServiceUtil.getProductInfoService().saveOrUpdate(mProductInfo);
      EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
    }
  }

  /**
   * 设置剩余数量
   */
  @OnClick(R.id.btn_set_stock) public void setSurplusNum() {
    //有规格
    if (mFormatRelList != null && mFormatRelList.size() != 0) {
      if (mFormatInfo == null) {
        ToastUtils.showShort(this, "请选择规格,再设置规格余量");
        return;
      }
      //获取所选规格
      getProductFormat();
      if (PxProductFormatRel.STATUS_STOP_SALE.equals(productFormatRel.getStatus())) {
        ToastUtils.showShort(this, "已沽请的规格不能设置剩余数量");
        return;
      }
      //设置规格余量
      showSurplusDialog(true);
    }
    //无规格
    else {
      if (mProductInfo.getStatus().equals(PxProductInfo.STATUS_STOP_SALE)) {
        ToastUtils.showShort(this, "已沽清的商品不能设置剩余数量");
        return;
      }
      //设置商品余量
      showSurplusDialog(false);
    }
  }

  private void showSurplusDialog(final boolean hasFormat){
    new MaterialDialog.Builder(this).title("警告")
        .content("输入余量")
        .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .input("数量", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence input) {
            if (input.toString().trim() != null && !input.toString().toString().equals("") && input.toString().length() > 6) {
              ToastUtils.showShort(ProdInfoActivity.this, "输入过长!");
              dialog.getInputEditText().setText("");
              return;
            }
            //校验
            if (input.toString() == null || input.toString().trim().equals("")
                || !RegExpUtils.match2DecimalPlaces(input.toString())
                || Double.valueOf(input.toString().trim()).doubleValue() <= 0) {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            } else {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
              //单单位
              if (PxProductInfo.IS_TWO_UNIT_FALSE.equals(mProductInfo.getMultipleUnit())) {
                if (RegExpUtils.matchNumber(input.toString()) == false){
                  dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }
              }
            }
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            Double surplusNum = Double.valueOf(dialog.getInputEditText().getText().toString());
            if (hasFormat){
              productFormatRel.setStock(surplusNum);
              DaoServiceUtil.getProductFormatRelService().saveOrUpdate(productFormatRel);
              //规格和剩余数量设置后刷新
              refreshSetData();
            } else {
              mProductInfo.setOverPlus(surplusNum);
              DaoServiceUtil.getProductInfoService().saveOrUpdate(mProductInfo);
              EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
            }
          }
        })
        .show();
  }

  /**
   * 修改价格
   */
  //@formatter:on
  @OnClick(R.id.btn_modify_price) public void modifyPrice() {
    if (mFormatRelList != null && mFormatRelList.size() != 0 && mFormatInfo == null) {
      ToastUtils.showShort(this, "请选择规格,再修改价格");
      return;
    }
    final MaterialDialog dialog = new MaterialDialog.Builder(this).title("修改价格")
        .customView(R.layout.layout_dialog_modify_price, true)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .cancelable(false)
        .build();
    //确定按钮
    final MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
    //价格
    final EditText etPrice = (EditText) dialog.getCustomView().findViewById(R.id.et_price);
    //会员价
    final EditText etVipPrice = (EditText) dialog.getCustomView().findViewById(R.id.et_vip_price);

    if (mFormatInfo != null) {
      PxProductFormatRel formatRel = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mFormatInfo.getId()))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .unique();
      etPrice.setText(formatRel.getPrice() + "");
      etVipPrice.setText(formatRel.getVipPrice() + "");
    } else {
      etPrice.setText(mProductInfo.getPrice() + "");
      etVipPrice.setText(mProductInfo.getVipPrice() + "");
    }

    dialog.show();
    positiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String price = etPrice.getText().toString();
        String vipPrice = etVipPrice.getText().toString();
        if (price == null || price.trim().equals("") || RegExpUtils.match2DecimalPlaces(price) == false){
          ToastUtils.showShort(ProdInfoActivity.this, "请设置正确的价格");
          return;
        }
        if (vipPrice == null || vipPrice.trim().equals("") || RegExpUtils.match2DecimalPlaces(vipPrice) == false){
          ToastUtils.showShort(ProdInfoActivity.this, "请设置正确的会员价");
          return;
        }
        if (mFormatInfo != null) {
          PxProductFormatRel formatRel = DaoServiceUtil.getProductFormatRelService()
              .queryBuilder()
              .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mFormatInfo.getId()))
              .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
              .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
              .unique();
          formatRel.setPrice(Double.valueOf(price));
          formatRel.setVipPrice(Double.valueOf(vipPrice));
          DaoServiceUtil.getProductFormatRelService().saveOrUpdate(formatRel);
          DaoServiceUtil.getProductFormatRelService().refresh(formatRel);
        } else {
          mProductInfo.setPrice(Double.valueOf(price));
          mProductInfo.setVipPrice(Double.valueOf(vipPrice));
          DaoServiceUtil.getProductInfoService().saveOrUpdate(mProductInfo);
          DaoServiceUtil.getProductInfoService().refresh(mProductInfo);
        }
        //价格
        mTvPrice.setText(price + "/" + mProductInfo.getUnit());
        //优惠
        if (mProductInfo.getIsDiscount().equals(PxProductInfo.IS_DISCOUNT_TRUE)) {
          mTvPrivilege.setText("允许打折\t" + "会员价:" + vipPrice + "/" + mProductInfo.getUnit());
        } else {
          mTvPrivilege.setText("不允许打折\t" + "会员价:" + vipPrice + "/" + mProductInfo.getUnit());
        }
        //刷新余量页面
        EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
        dialog.dismiss();
      }
    });
  }

  /**
   * 获取该商品所选规格
   */
  //@formatter:off
  private PxProductFormatRel getProductFormat(){
    QueryBuilder<PxProductFormatRel> formatRel = DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
            .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mFormatInfo.getId()));
    productFormatRel = formatRel.unique();
    return productFormatRel;
  }

  /**
   * 规格和剩余数量设置后刷新
   */
  private void refreshSetData(){
    QueryBuilder<PxProductFormatRel> formatRelAgainQb =
        DaoServiceUtil.getProductFormatRelService()
            .queryBuilder()
            .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
            .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()));
    Join<PxProductFormatRel, PxFormatInfo> formatJoin = formatRelAgainQb.join(PxProductFormatRelDao.Properties.PxFormatInfoId, PxFormatInfo.class);
    formatJoin.where(PxFormatInfoDao.Properties.DelFlag.eq("0"));
    if (formatRelAgainQb.list() != null && formatRelAgainQb.list().size() != 0) {
      mFormatTagAdapter = new TagAdapter<PxProductFormatRel>(formatRelAgainQb.list()) {
        @Override public View getView(FlowLayout parent, int position, PxProductFormatRel rel) {
          TextView tv = (TextView) LayoutInflater.from(ProdInfoActivity.this).inflate(R.layout.item_tags_format, mTagsFormat, false);
          PxFormatInfo format = rel.getDbFormat();
          if(PxProductFormatRel.STATUS_STOP_SALE.equals(rel.getStatus())){
            tv.setText(format.getName()+"(停售)");
            tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
            mBtnStopSale.setText("取消沽清");
          }else {
            if(rel.getStock()!=null&&rel.getStock()>0.0){
              tv.setText(format.getName()+"(余" + NumberFormatUtils.formatFloatNumber(rel.getStock()) + ")");
            }else {
              tv.setText(format.getName());
            }
            mBtnStopSale.setText("沽清");
          }
          return tv;
        }
      };
      mTagsFormat.setAdapter(mFormatTagAdapter);
    }
    //默认选中状态设置
    if (mFormatInfo != null && mFormatTagAdapter != null) {
      PxProductFormatRel currentRel = DaoServiceUtil.getProductFormatRelService()
          .queryBuilder()
          .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
          .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(mFormatInfo.getId()))
          .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(mProductInfo.getId()))
          .unique();
      if (currentRel == null) return;
      for (int i = 0; i < formatRelAgainQb.list().size(); i++) {
        PxProductFormatRel rel = formatRelAgainQb.list().get(i);
        if (currentRel.getObjectId().equals(rel.getObjectId())) {
          mFormatTagAdapter.setSelectedList(i);
        }
      }
    }
  }
  /**
   * 标签打印设置
   */
  //@formatter:on
  private void switchPrintLabel(boolean isChecked) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      String isLabel = isChecked ? "0" : "1";
      mProductInfo.setIsLabel(isLabel);
      DaoServiceUtil.getProductInfoService().update(mProductInfo);
      db.setTransactionSuccessful();
    } catch (Exception e) {
    } finally {
      db.endTransaction();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }
}
