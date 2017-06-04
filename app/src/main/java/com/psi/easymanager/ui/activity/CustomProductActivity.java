package com.psi.easymanager.ui.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kyleduo.switchbutton.SwitchButton;
import com.psi.easymanager.R;
import com.psi.easymanager.dao.PxProductCategoryDao;
import com.psi.easymanager.dao.PxProductConfigPlanDao;
import com.psi.easymanager.dao.PxProductInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.module.PxPrinterInfo;
import com.psi.easymanager.module.PxProductCategory;
import com.psi.easymanager.module.PxProductConfigPlan;
import com.psi.easymanager.module.PxProductConfigPlanRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.upload.UpLoadCustomProduct;
import com.psi.easymanager.utils.ChineseCharToEn;
import com.psi.easymanager.utils.RegExpUtils;
import com.psi.easymanager.utils.ToastUtils;
import com.psi.easymanager.widget.SwipeBackLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

/**
 * User: ylw
 * Date: 2016-06-10
 * Time: 19:22
 * 添加自定义商品
 */
public class CustomProductActivity extends BaseActivity {
  @Bind(R.id.tv_custom_product_name) TextView mTvName;//名字
  @Bind(R.id.tv_custom_product_category) TextView mTvCategory;//分类
  @Bind(R.id.tv_custom_product_unit) TextView mTvNumUnit;//数量单位
  @Bind(R.id.tv_custom_product_weight_unit) TextView mTvWeightUnit;//重量单位
  @Bind(R.id.tv_custom_product_unique_price) TextView mTvUnitPrice;//单价
  @Bind(R.id.ll_custom_product_weight_unit) LinearLayout mLlWeightUnit;//重量单位 item
  @Bind(R.id.sb_custom_product_allow_discount) SwitchButton mSbDiscount;//允许打折
  @Bind(R.id.sb_custom_product_pay_by_weight) SwitchButton mSbByWeight;//按重量结账
  @Bind(R.id.divide_6) View view_6;//分割线
  @Bind(R.id.content_view_custom) SwipeBackLayout mContentView;//滑动外容器
  @Bind(R.id.tv_custom_product_config_paln) TextView mTvConfigPlan;//配菜方案
  private PxProductCategory mCurrentCategory;//所选自定义商品的分类
  private String[] categories;//存储分类数组
  private List<PxProductCategory> mCategoryList;//所有分类
  private Integer[] mSelectedIndices;//配菜方案选择的条目
  private String[] mWeightUnits;

  @Override protected int provideContentViewId() {
    return R.layout.activity_custom_product;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    mContentView.setCallBack(new SwipeBackLayout.CallBack() {
      @Override public void onFinish() {
        finish();
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    ButterKnife.unbind(this);
  }

  /**
   * 所有布局的点击事件
   */
  @OnClick({
      R.id.ll_custom_product_name, R.id.ll_custom_product_category, R.id.ll_custom_product_unit,
      R.id.ll_custom_product_weight_unit, R.id.rl_custom_product_unique_price,
      R.id.rl_custom_product_config_plan
  }) public void layoutClickEvent(View view) {
    switch (view.getId()) {
      case R.id.ll_custom_product_name://品名
        new MaterialDialog.Builder(this).title("品名")
            .content("请输入正确名称")
            .inputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                InputType.TYPE_TEXT_FLAG_CAP_WORDS)
            .inputMaxLength(8)
            .positiveText("确认")
            .negativeText("取消")
            .negativeColor(this.getResources().getColor(R.color.primary_text))
            .alwaysCallInputCallback()
            .input("请输入正确名称", mTvName.getText(), false, new MaterialDialog.InputCallback() {
              @Override
              public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                materialDialog.getActionButton(DialogAction.POSITIVE)
                    .setEnabled(RegExpUtils.matchName(charSequence.toString().trim()));
              }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
                //检查重名
                checkRepeatName(dialog);
              }
            })
            .show();
        break;
      case R.id.ll_custom_product_category://所属分类
        //获取分类信息
        getAllCategory();
        new MaterialDialog.Builder(this).title("所属分类")
            .items(categories)
            .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
              @Override public boolean onSelection(MaterialDialog dialog, View view, int which,
                  CharSequence charSequence) {
                if (charSequence != null) {
                  mTvCategory.setText(charSequence.toString());
                  //得到所选分类
                  mCurrentCategory = mCategoryList.get(which);
                }
                return true;
              }
            })
            .negativeText("取消")
            .negativeColor(this.getResources().getColor(R.color.primary_text))
            .positiveText("确定")
            .show();
        break;
      case R.id.ll_custom_product_unit://数量单位
        new MaterialDialog.Builder(this).title("数量单位")
            .content("请输入正确单位")
            .inputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                InputType.TYPE_TEXT_FLAG_CAP_WORDS)
            .inputMaxLength(2)
            .negativeText("取消")
            .negativeColor(this.getResources().getColor(R.color.primary_text))
            .positiveText("确定")
            .alwaysCallInputCallback()
            .input("单位", "", false, new MaterialDialog.InputCallback() {
              @Override
              public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

              }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
                mTvNumUnit.setText(dialog.getInputEditText().getText().toString());
                mTvNumUnit.setTextColor(getResources().getColor(R.color.colorAccent));
              }
            })
            .show();
        break;
      case R.id.ll_custom_product_weight_unit://重量单位
        //获取重量单位
        mWeightUnits = getWeightUnit();
        new MaterialDialog.Builder(this).title("重量单位")
            .negativeText("取消")
            .negativeColor(this.getResources().getColor(R.color.primary_text))
            .positiveText("确定")
            .items(mWeightUnits)
            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
              @Override public boolean onSelection(MaterialDialog dialog, View itemView, int which,
                  CharSequence text) {
                return true;
              }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
                setWeightUnit(dialog);
              }
            })
            .onNegative(new MaterialDialog.SingleButtonCallback() {
              @Override
              public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mTvWeightUnit.setText("");
              }
            })
            .show();
        break;
      case R.id.rl_custom_product_unique_price://单价
        new MaterialDialog.Builder(this).title("单价")
            .content("请输入正确单价")
            .inputType(InputType.TYPE_CLASS_NUMBER)
            .negativeText("取消")
            .negativeColor(this.getResources().getColor(R.color.primary_text))
            .positiveText("确定")
            .alwaysCallInputCallback()
            .input("单价", "10", false, new MaterialDialog.InputCallback() {
              @Override
              public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                materialDialog.getActionButton(DialogAction.POSITIVE)
                    .setEnabled(RegExpUtils.matchMoney(charSequence.toString().trim()));
              }
            })
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override public void onClick(MaterialDialog dialog, DialogAction dialogAction) {
                checkRightPrice(dialog);
              }
            })
            .show();
        break;
      case R.id.rl_custom_product_config_plan://配菜方案
        showDialogConfigPlan();
        break;
    }
  }

  /**
   * 设置重量单位
   */
  private void setWeightUnit(MaterialDialog dialog) {
    int index = dialog.getSelectedIndex();
    if (index < 0) return;
    mTvWeightUnit.setText(mWeightUnits[index]);
    mTvWeightUnit.setTextColor(getResources().getColor(R.color.colorAccent));
  }

  /**
   * 弹出配菜方案多选选择框
   */
  private void showDialogConfigPlan() {
    List<PxProductConfigPlan> configPlanList = DaoServiceUtil.getProductConfigPlanService()
        .queryBuilder()
        .where(PxProductConfigPlanDao.Properties.DelFlag.eq("0"))
        .list();
    //减去 非后厨打印机的配菜方案
    for (int i = 0; i < configPlanList.size(); i++) {
      PxProductConfigPlan plan = configPlanList.get(i);
      PxPrinterInfo dbPrinter = plan.getDbPrinter();
      if (dbPrinter == null ) {
        configPlanList.remove(plan);
      }
    }
    final String[] configPlans = new String[configPlanList.size()];
    for (int i = 0; i < configPlanList.size(); i++) {
      configPlans[i] = configPlanList.get(i).getName();
    }
    new MaterialDialog.Builder(this).title("请至少选择一种配菜方案")
        .items(configPlans)
        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
          @Override
          public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
            return true;
          }
        })
        .positiveText("确定")
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            showConfigPlan(configPlans, dialog);
          }
        })
        .negativeText("取消")
        .show();
  }

  /**
   * 显示选择的配菜方案
   */
  private void showConfigPlan(String[] configPlans, MaterialDialog dialog) {
    mSelectedIndices = dialog.getSelectedIndices();
    if (mSelectedIndices != null && mSelectedIndices.length > 0) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < mSelectedIndices.length; i++) {
        if (i > 0) {
          sb.append("、" + configPlans[mSelectedIndices[i]]);
        } else {
          sb.append(configPlans[mSelectedIndices[i]]);
        }
      }
      mTvConfigPlan.setText(sb.toString());
    }
  }

  /**
   * 获取重量单位
   */
  private String[] getWeightUnit() {
    String[] strs = CustomProductActivity.this.getResources()
        .getStringArray(R.array.custom_product_weight_unit);
    return strs;
  }

  /**
   * 按重量结账SwitchButton
   */
  @OnCheckedChanged(R.id.sb_custom_product_pay_by_weight) public void isPayByWeight(
      SwitchButton sb) {
    if (sb.isChecked()) {
      view_6.setVisibility(View.VISIBLE);
      mLlWeightUnit.setVisibility(View.VISIBLE);
    } else {
      view_6.setVisibility(View.GONE);
      mLlWeightUnit.setVisibility(View.GONE);
    }
  }

  /**
   * 获取分类信息
   */
  private void getAllCategory() {
    //叶子节点的分类
    mCategoryList = DaoServiceUtil.getProductCategoryService()
        .queryBuilder()
        .where(PxProductCategoryDao.Properties.Leaf.eq(PxProductCategory.IS_LEAF))
        .where(PxProductCategoryDao.Properties.Type.eq(PxProductCategory.TYPE_ORDINARY))
        .where(PxProductCategoryDao.Properties.DelFlag.eq("0"))
        .list();
    categories = new String[mCategoryList.size()];
    for (int i = 0; i < mCategoryList.size(); i++) {
      categories[i] = mCategoryList.get(i).getName();
    }
  }

  /**
   * 取消 确定
   */
  @OnClick({ R.id.fab_custom_product_cancel, R.id.fab_custom_product_sure })
  public void closeCustomProduct(View view) {
    if (view.getId() == R.id.fab_custom_product_sure) {
      productSure();
    } else {
      productCancel();
    }
  }

  /**
   * 取消
   */
  private void productCancel() {
    this.finish();
  }

  /**
   * 确定
   */
  // @formatter:off
  private void productSure() {
    String name = mTvName.getText().toString().trim();//名
    String numUnit = mTvNumUnit.getText().toString().trim();//数量单位
    String weightUnit = mTvWeightUnit.getText().toString().trim();//重量单位
    String unitPrice = mTvUnitPrice.getText().toString().trim();//单价
    boolean weightChecked = mSbByWeight.isChecked();//重量结账
    boolean discountChecked = mSbDiscount.isChecked();//允许打折
    if (name.isEmpty()) {
      ToastUtils.showShort(this, "名字不能为空");
      return;
    }
    if (mCurrentCategory == null) {
      ToastUtils.showShort(this, "请选择分类");
      return;
    }
    if (numUnit.isEmpty()) {
      ToastUtils.showShort(this, "数量单位不能为空");
      return;
    }
    if (weightChecked && weightUnit.isEmpty()) {
      ToastUtils.showShort(this, "重量单位不能为空");
      return;
    }
    if (unitPrice.isEmpty()) {
      ToastUtils.showShort(this, "单价不能为空");
      return;
    }
    if (mTvConfigPlan.getText().toString().isEmpty()) {
      ToastUtils.showShort(this, "请至少选择一种配菜方案");
      return;
    }
     List<PxProductConfigPlan> configPlanList = DaoServiceUtil.getProductConfigPlanService()
        .queryBuilder()
        .where(PxProductConfigPlanDao.Properties.DelFlag.eq("0"))
        .list();
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
    //选择好的配菜方案
    List<PxProductConfigPlan> needConPlainList = new ArrayList<>();
    for (int i = 0;i<mSelectedIndices.length;i++){
      needConPlainList.add(configPlanList.get(mSelectedIndices[i]));
    }
      PxProductInfo customProductInfo = new PxProductInfo();
      //普通类型
      customProductInfo.setType(PxProductInfo.TYPE_ORIGINAL);
      //上架
      customProductInfo.setShelf(PxProductInfo.SHELF_PUT_AWAY);
      //微信点餐页面显示
      customProductInfo.setVisible(PxProductInfo.SHOW_ON_WX);
      //OBjId
      customProductInfo.setObjectId(UUID.randomUUID().toString().replaceAll("\\-", ""));
      //名字
      customProductInfo.setName(name);
      ChineseCharToEn charToEn = new ChineseCharToEn();
      String py = charToEn.getAllFirstLetter(name);
       //py
      if (py !=null){
        customProductInfo.setPy(py);
      }
      //分类
      customProductInfo.setDbCategory(mCurrentCategory);
      //默认未上传n
      customProductInfo.setIsUpLoad(false);
      //是自定义
      customProductInfo.setIsCustom(true);
      // 正常
      customProductInfo.setDelFlag("0");
      //点菜单位
      customProductInfo.setOrderUnit(numUnit);
      //赠品
      customProductInfo.setIsGift(PxProductInfo.IS_NOTGIFT);
      //商品发送后厨是否出单(0:出单 1：不出单)
      customProductInfo.setIsPrint(PxProductInfo.IS_PRINT);
      //是否允许收银改价（0：是 1：否）
      customProductInfo.setChangePrice(PxProductInfo.IS_NOT_ALLOW_CHANGE_PRICE);
      //单价
      customProductInfo.setPrice(Double.parseDouble(unitPrice));
      if (weightChecked) {//双单位
        customProductInfo.setMultipleUnit(PxProductInfo.IS_TWO_UNIT_TURE);
        customProductInfo.setUnit(weightUnit);
      } else {
        customProductInfo.setMultipleUnit(PxProductInfo.IS_TWO_UNIT_FALSE);
        customProductInfo.setUnit(numUnit);
      }
      //会员价默认 = 单价
      customProductInfo.setVipPrice(Double.parseDouble(unitPrice));
      //状态正常
      customProductInfo.setStatus(PxProductInfo.STATUS_ON_SALE);
      //不让打折
      customProductInfo.setIsDiscount(discountChecked ? PxProductInfo.IS_DISCOUNT_TRUE : PxProductInfo.IS_DISCOUNT_FALSE);
      //类型
      customProductInfo.setType(PxProductInfo.TYPE_ORIGINAL);
      //上架
      customProductInfo.setShelf(PxProductInfo.SHELF_PUT_AWAY);
      //微信显示
      customProductInfo.setVisible("1");
      //配菜方案
      DaoServiceUtil.getProductInfoService().save(customProductInfo);
      for (PxProductConfigPlan configPlan : needConPlainList) {
        PxProductConfigPlanRel configPlanRel = new PxProductConfigPlanRel();
        configPlanRel.setObjectId(UUID.randomUUID().toString().replaceAll("\\-", ""));
        configPlanRel.setDelFlag("0");
        configPlanRel.setDbProduct(customProductInfo);
        configPlanRel.setDbProductConfigPlan(configPlan);
        DaoServiceUtil.getProductConfigPlanRelService().save(configPlanRel);
      }
      DaoServiceUtil.getProductConfigPlanService().saveOrUpdate(needConPlainList);
      //向服务器上传自定义商品数据
      UpLoadCustomProduct upLoadCustomProduct = UpLoadCustomProduct.getInstance();
      upLoadCustomProduct.upLoadSingleProd(customProductInfo);
      upLoadCustomProduct.closePool();
      db.setTransactionSuccessful();
    } catch (Exception e) {

    } finally {
      db.endTransaction();
    }
    // 退出
    this.finish();
  }

  // @formatter:on

  /**
   * 检查重名
   */
  private void checkRepeatName(MaterialDialog dialog) {
    String customName = dialog.getInputEditText().getText().toString().trim();
    List<PxProductInfo> infoList = DaoServiceUtil.getProductInfoService()
        .queryBuilder()
        .where(PxProductInfoDao.Properties.Name.eq(customName))
        .list();
    if (infoList != null && infoList.size() != 0) { //重名
      ToastUtils.showShort(this, "已有该商品,请重新添加!");
      mTvName.setText("");
    } else { // 不重名
      mTvName.setText(customName);
      mTvName.setTextColor(getResources().getColor(R.color.colorAccent));
    }
  }

  /**
   * 检查价格输入是否正确
   */
  private void checkRightPrice(MaterialDialog dialog) {
    String priceText = dialog.getInputEditText().getText().toString().trim();
    if (priceText.isEmpty()) {
      ToastUtils.showShort(this, "请输入正确的价格");
      return;
    }
    Double price = Double.valueOf(priceText);
    if (price - 0 > 0) {
      mTvUnitPrice.setText(priceText);
      mTvUnitPrice.setTextColor(getResources().getColor(R.color.colorAccent));
    } else {
      mTvUnitPrice.setText("");
      ToastUtils.showShort(this, "请输入正确的价格");
    }
  }

}