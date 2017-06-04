package com.psi.easymanager.ui.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.text.InputType;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxExtraChargeDao;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxPayInfoDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductRemarksDao;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.PxTableExtraRelDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.FindBillRefreshStatusEvent;
import com.psi.easymanager.event.MoveTableEvent;
import com.psi.easymanager.event.RefreshCashBillListEvent;
import com.psi.easymanager.event.RevokeOrFinishBillEvent;
import com.psi.easymanager.event.SendOrderToModifyBillEvent;
import com.psi.easymanager.event.SendTableToFindBillTableEvent;
import com.psi.easymanager.event.UpdateProdInfoListStatusEvent;
import com.psi.easymanager.module.Office;
import com.psi.easymanager.module.PrintDetailsCollect;
import com.psi.easymanager.module.PxExtraCharge;
import com.psi.easymanager.module.PxExtraDetails;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxOperationLog;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxPayInfo;
import com.psi.easymanager.module.PxProductFormatRel;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxProductRemarks;
import com.psi.easymanager.module.PxPromotioDetails;
import com.psi.easymanager.module.PxPromotioInfo;
import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.PxTableExtraRel;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.print.MergePrintDetails;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.ui.activity.MainActivity;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.NumberFormatUtils;
import com.psi.easymanager.utils.PromotioDetailsHelp;
import com.psi.easymanager.utils.ToastUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dorado on 2016/5/28.
 */
public class ModifyBillFragment extends BaseFragment {

  @Bind(R.id.tv_table_name) TextView mTvTableName;
  @Bind(R.id.tv_move_table) TextView mTvMoveTable;
  @Bind(R.id.tv_people_number) TextView mTvPeopleNumber;
  @Bind(R.id.rl_people_num) RelativeLayout mRlPeopleNum;
  @Bind(R.id.et_custom_remark) EditText mEtCustomRemark;
  @Bind(R.id.tv_promotio) TextView mTvPromotioInfo;

  //MainActivity
  private MainActivity mAct;
  //Fragment管理器
  private FragmentManager mFm;

  //移动桌台信息
  private PxTableInfo mMoveTableInfo;
  //桌台信息
  private PxTableInfo mTableInfo;
  //订单信息
  private PxOrderInfo mOrderInfo;
  //操作类型
  private String mOperateType;
  //移动桌台
  private static final String MOVE_TABLE = "1";

  //客单
  private Fragment mCashBillFragment;
  //菜单
  private Fragment mCashMenuFragment;

  //促销计划
  private PxPromotioInfo mPromotioInfo;

  public static ModifyBillFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    ModifyBillFragment fragment = new ModifyBillFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (MainActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
    if (savedInstanceState != null) {
      mCashBillFragment = mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
      mCashMenuFragment = mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_modify_bill, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(SendOrderToModifyBillEvent.class);
  }

  /**
   * 取消
   */
  @OnClick(R.id.ibtn_cancel) public void cancel() {
    showBillMenu();
  }

  /**
   * 促销计划 是否发生变化
   *
   * @return false 没发生变化
   */
  private boolean isChangePromotio(PxPromotioInfo prePromotioInfo) {
    if (prePromotioInfo != null) {
      if (mPromotioInfo == null) {
        return true;
      } else {
        return !prePromotioInfo.getObjectId().equals(mPromotioInfo.getObjectId());
      }
    } else {
      return mPromotioInfo != null;
    }
  }

  /**
   * 确认
   */
  //@formatter:off
  @OnClick(R.id.ibtn_confirm) public void confirm() {
    //促销计划发生变化
    // 通知CashMenuFragment 更新CashMenuProductAdapter、CheckOutFragment 更新订单账单统计 、CashBillFragment 更新DetailCollectionAdapter
    // + 更新订单关联的促销计划
    // + 订单详情关联的促销计划详情
    boolean changePromotio = isChangePromotio(mOrderInfo.getDbPromotioById());
    if (changePromotio) {
      changeOrderPromotioInfo(mOrderInfo,mPromotioInfo);
      //修改促销计划
      mOrderInfo.setDbPromotioInfo(mPromotioInfo);
    }
    if (mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_RETAIL)) {
      //合并备注
      mergeRemarks();
      if (changePromotio){
        DaoServiceUtil.getOrderInfoService().update(mOrderInfo);
      }
      //通知CashBill刷新
      EventBus.getDefault().post(new RefreshCashBillListEvent());
      //找单页面接收
      EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
      //显示页面
      showBillMenu();
      return;
    }
    //如果移动桌台为空，说明不移动桌台
    if (mMoveTableInfo == null && mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
      //开启对话框
      if (mTableInfo != null && mOrderInfo != null) {
        SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
        db.beginTransaction();
        try {
          mAct.isShowProgress(true);
          //人数
          Integer peopleNum = Integer.valueOf(mTvPeopleNumber.getText().toString());
          mOrderInfo.setActualPeopleNumber(peopleNum);
          DaoServiceUtil.getOrderInfoService().update(mOrderInfo);
          //备注合并
          mergeRemarks();
          //通知CashBill刷新
          EventBus.getDefault().post(new RefreshCashBillListEvent());
          //找单页面接收
          EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
          db.setTransactionSuccessful();
        } catch (Exception e) {
           e.printStackTrace();
          mAct.isShowProgress(false);
        } finally {
          db.endTransaction();
        }
        //更新页面
        showBillMenu();
      }
      return;
    }

    //移动桌台
    if (mOperateType.equals(MOVE_TABLE)) {
      SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
      db.beginTransaction();
      try {
        mAct.isShowProgress(true);
        //人数
        Integer peopleNum = Integer.valueOf(mTvPeopleNumber.getText().toString());
        mOrderInfo.setActualPeopleNumber(peopleNum);
        DaoServiceUtil.getOrderInfoService().update(mOrderInfo);
        //备注合并
        mergeRemarks();
        //恢复旧桌状态
        QueryBuilder<TableOrderRel> tableOrderRelQb = DaoServiceUtil.getTableOrderRelService().queryBuilder();
        tableOrderRelQb.where(TableOrderRelDao.Properties.PxTableInfoId.eq(mTableInfo.getId()));
        Join<TableOrderRel, PxOrderInfo> tableOrderRelJoin = tableOrderRelQb.join(TableOrderRelDao.Properties.PxOrderInfoId, PxOrderInfo.class);
        tableOrderRelJoin.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
        long count = tableOrderRelQb.count();
        if (count == 1) {
          mTableInfo.setStatus(PxTableInfo.STATUS_EMPTY);
          DaoServiceUtil.getTableInfoService().saveOrUpdate(mTableInfo);
        }
        //更新新桌状态
        mMoveTableInfo.setStatus(PxTableInfo.STATUS_OCCUPIED);
        DaoServiceUtil.getTableInfoService().saveOrUpdate(mMoveTableInfo);
        //改变桌台订单关联
        TableOrderRel orderRel = DaoServiceUtil.getTableOrderRelService()
            .queryBuilder()
            .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
            .unique();
        orderRel.setDbTable(mMoveTableInfo);
        DaoServiceUtil.getTableOrderRelService().saveOrUpdate(orderRel);
        //结束当前附加费计算
        if (mOrderInfo.getDbCurrentExtra() != null) {
          PxTableExtraRel tableExtraRel = DaoServiceUtil.getTableExtraRelService()
              .queryBuilder()
              .where(PxTableExtraRelDao.Properties.PxTableInfoId.eq(mTableInfo.getId()))
              .where(PxTableExtraRelDao.Properties.DelFlag.eq("0"))
              .unique();
          if (tableExtraRel != null) {
            PxExtraCharge dbExtraCharge = tableExtraRel.getDbExtraCharge();
            if (dbExtraCharge != null) {
              PxExtraDetails dbCurrentExtra = mOrderInfo.getDbCurrentExtra();
              long minutes = new Date().getTime() / 1000 / 60;
              long startMinutes = dbCurrentExtra.getStartTime().getTime() / 1000 / 60;
              long extraMinutes = minutes - startMinutes;
              //附加费价格
              double currentExtraPrice = Math.ceil((double) extraMinutes / dbExtraCharge.getMinutes() * dbExtraCharge.getServiceCharge().doubleValue());
              //更新价格
              dbCurrentExtra.setPrice(currentExtraPrice);
              //更新时间
              dbCurrentExtra.setStopTime(new Date());
              DaoServiceUtil.getExtraDetailsService().saveOrUpdate(dbCurrentExtra);
            }
          }
        }
        //查询新桌可用的附加费引用关系
        QueryBuilder<PxTableExtraRel> extraRelQb = DaoServiceUtil.getTableExtraRelService()
            .queryBuilder()
            .where(PxTableExtraRelDao.Properties.DelFlag.eq("0"))
            .where(PxTableExtraRelDao.Properties.PxTableInfoId.eq(mMoveTableInfo.getId()));
        Join<PxTableExtraRel, PxExtraCharge> extraJoin = extraRelQb.join(PxTableExtraRelDao.Properties.PxExtraChargeId, PxExtraCharge.class);
        extraJoin.where(PxExtraChargeDao.Properties.DelFlag.eq("0"));
        PxTableExtraRel rel = extraRelQb.unique();
        //如果新桌有可用附加费
        if (rel != null) {
          //附加费信息
          PxExtraCharge extraCharge = rel.getDbExtraCharge();
          if (extraCharge == null || extraCharge.getServiceStatus().equals(PxExtraCharge.ENABLE_FALSE)) {
            mOrderInfo.setDbCurrentExtra(null);
            //桌台变更
            tableAlteration();
            return;
          }
          //新建附加费详情
          PxExtraDetails pxExtraDetails = new PxExtraDetails();
          //开始时间
          pxExtraDetails.setStartTime(new Date());
          //价格
          pxExtraDetails.setPrice((double) 0);
          //订单
          pxExtraDetails.setDbOrder(mOrderInfo);
          //附加费名
          pxExtraDetails.setExtraName(extraCharge.getName());
          //桌台名
          pxExtraDetails.setTableName(mTableInfo.getName());
          //已付款
          pxExtraDetails.setPayPrice((double) 0);
          //已打印
          pxExtraDetails.setIsPrinted(false);
          //储存
          DaoServiceUtil.getExtraDetailsService().save(pxExtraDetails);
          //订单更新当前附加费详情
          mOrderInfo.setDbCurrentExtra(pxExtraDetails);
          //更新订单
          DaoServiceUtil.getOrderInfoService().saveOrUpdate(mOrderInfo);
        } else {
          mOrderInfo.setDbCurrentExtra(null);
          DaoServiceUtil.getOrderInfoService().saveOrUpdate(mOrderInfo);
        }
        //桌台变更
        tableAlteration();
        db.setTransactionSuccessful();
      } catch (Exception e) {
        e.printStackTrace();
        mAct.isShowProgress(false);
      } finally {
        db.endTransaction();
      }
    }
  }

  /**
   * 合并备注
   */
  private void mergeRemarks() {
    //备注合并
    String remarks = mEtCustomRemark.getText().toString().trim();
    mOrderInfo.setRemarks(remarks);
    DaoServiceUtil.getOrderInfoService().saveOrUpdate(mOrderInfo);
  }

  /**
   * 桌台变更
   */
  private void tableAlteration() {
    //上次换桌时间
    mOrderInfo.setLastMoveTableTime(new Date());
    //添加桌台变更信息
    PxTableAlteration pxTableAlteration = new PxTableAlteration();
    //操作时间
    pxTableAlteration.setOperateTime(new Date());
    //移动
    pxTableAlteration.setType(PxTableAlteration.TYPE_MOVE);
    //未打印
    pxTableAlteration.setIsPrinted(false);
    //未清空
    pxTableAlteration.setIsClear(false);
    //订单
    pxTableAlteration.setDbOrder(mOrderInfo);
    //原桌台
    pxTableAlteration.setDbOriginalTable(mTableInfo);
    //目标桌台
    pxTableAlteration.setDbTargetTable(mMoveTableInfo);
    DaoServiceUtil.getTableAlterationService().saveOrUpdate(pxTableAlteration);
    //通知CashBill刷新
    EventBus.getDefault().post(new RefreshCashBillListEvent());
    //找单页面接收
    EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
    //更新页面
    showBillMenu();
    //打印移并桌信息
    //printTableAlteration(pxTableAlteration);
    PrintTaskManager.printTableAlteration(pxTableAlteration);
  }

  /**
   * 设置人数
   */
  @OnClick(R.id.rl_people_num) public void setPeopleNumber() {
    new MaterialDialog.Builder(mAct).title("警告")
        .content("输入人数")
        .inputType(InputType.TYPE_CLASS_NUMBER)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .alwaysCallInputCallback()
        .input("人数", "", false, new MaterialDialog.InputCallback() {
          @Override public void onInput(MaterialDialog dialog, CharSequence input) {
            if (input.toString().trim() != null && !input.toString().toString().equals("")
                && input.toString().length() > 4) {
              ToastUtils.showShort(mAct, "输入过长!");
              dialog.getInputEditText().setText("");
              return;
            }
            if (input.toString() == null || input.toString().trim().equals("")
                || Integer.valueOf(input.toString().trim()).intValue() <= 0) {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            } else {
              dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            }
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            mTvPeopleNumber.setText(dialog.getInputEditText().getText().toString());
          }
        })
        .show();
  }

  /**
   * 设置备注
   */
  //@formatter:on
  @OnClick(R.id.rl_remarks) public void setRemarks() {
    //备注验证
    final List<PxProductRemarks> list = DaoServiceUtil.getProdRemarksService()
        .queryBuilder()
        .where(PxProductRemarksDao.Properties.DelFlag.eq("0"))
        .list();
    if (list == null || list.size() == 0) {
      ToastUtils.showShort(mAct, "请在后台添加备注信息");
      return;
    }
    //对话框
    final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("备注")
        .customView(R.layout.layout_dialog_choose_remarks, true)
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(this.getResources().getColor(R.color.primary_text))
        .build();
    final TextView tvRemarks = (TextView) dialog.getCustomView().findViewById(R.id.tv_remarks);
    //Tags
    final TagFlowLayout remarkTags =
        (TagFlowLayout) dialog.getCustomView().findViewById(R.id.tags_remarks);
    //TagAdapter
    TagAdapter tagRemarksAdapter = new TagAdapter<PxProductRemarks>(list) {
      @Override public View getView(FlowLayout parent, int position, PxProductRemarks remarks) {
        TextView tv = (TextView) LayoutInflater.from(mAct).inflate(R.layout.item_tags_remark, remarkTags, false);
        tv.setText(remarks.getRemarks());
        return tv;
      }
    };
    remarkTags.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
      @Override public boolean onTagClick(View view, int position, FlowLayout parent) {
        PxProductRemarks remarks = list.get(position);
        if (tvRemarks.getText() != null && tvRemarks.getText().toString().trim().equals("") == false) {
          tvRemarks.append(" ," + remarks.getRemarks());
        } else {
          tvRemarks.append(remarks.getRemarks());
        }
        return false;
      }
    });
    remarkTags.setAdapter(tagRemarksAdapter);
    //确定按钮
    MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);

    //显示
    dialog.show();

    //确认点击
    positiveBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //
        mEtCustomRemark.append(" " + tvRemarks.getText().toString().trim());
        //关闭dialog
        dialog.dismiss();
      }
    });
  }

  /**
   * 撤单
   */
  @OnClick(R.id.ibtn_revoke) public void revokeBill() {
    new MaterialDialog.Builder(mAct).title("警告")
        .content("确定要撤单么?")
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            if (mOrderInfo == null) {
              ToastUtils.showShort(mAct, "暂无订单");
              return;
            }
            List<PxPayInfo> payInfoList = DaoServiceUtil.getPayInfoService()
                .queryBuilder()
                .where(PxPayInfoDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
                .list();
            if (payInfoList != null && payInfoList.size() != 0) {
              ToastUtils.showShort(mAct, "该单有付款信息，不能撤单");
              return;
            }
            mAct.isShowProgress(true);
            //生成操作记录
            makeOperationRecord();
            //撤销桌位单
            if (mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_TABLE)) {
              revokeTableBill();
            }
            //撤销零售单
            if (mOrderInfo.getOrderInfoType().equals(PxOrderInfo.ORDER_INFO_TYPE_RETAIL)) {
              revokeRetail();
            }
            //显示菜单
            showBillMenu();
            //通知CashMenuFragment和CashBillFragment清空数据
            EventBus.getDefault().post(new RevokeOrFinishBillEvent().setOrderInfo(mOrderInfo));
            //找单页面接收
            EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
            mAct.isShowProgress(false);
          }
        })
        .show();
  }

  /**
   * 生成操作记录
   */
  private void makeOperationRecord() {
    App app = (App) App.getContext();
    if (app == null || app.getUser() == null) return;
    Office office = DaoServiceUtil.getOfficeService().queryBuilder().unique();
    PxOperationLog operationRecord = new PxOperationLog();
    operationRecord.setCid(office.getObjectId());
    //订单序列号
    operationRecord.setOrderNo(mOrderInfo.getOrderNo());
    //类型
    operationRecord.setType(PxOperationLog.TYPE_REVOKE);
    //操作人
    operationRecord.setOperater(app.getUser().getName());
    //操作时间
    operationRecord.setOperaterDate(new Date().getTime());
    //商品名
    StringBuilder stringBuilder = new StringBuilder();
    //价格
    double price = 0.0;
    List<PxOrderDetails> revokeList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
        .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_ORDER))
        .where(PxOrderDetailsDao.Properties.InCombo.eq(PxOrderDetails.IN_COMBO_FALSE))
        .list();
    if (revokeList == null || revokeList.size() == 0) return;
    for (PxOrderDetails details : revokeList) {
      StringBuilder sbDetails = new StringBuilder();
      sbDetails.append(details.getDbProduct().getName());
      if (PxProductInfo.IS_TWO_UNIT_TURE.equals(details.getDbProduct().getMultipleUnit())) {
        sbDetails.append(
            "[" + details.getMultipleUnitNumber() + details.getDbProduct().getUnit() + "]");
      } else {
        sbDetails.append("[" + details.getNum() + details.getDbProduct().getUnit() + "]");
      }
      if (details.getDbFormatInfo() != null) {
        sbDetails.append(details.getDbFormatInfo().getName());
      }
      stringBuilder.append(sbDetails.toString() + " ");
      price += details.getReceivablePrice();
    }
    operationRecord.setProductName(stringBuilder.toString());
    operationRecord.setTotalPrice(price);
    DaoServiceUtil.getOperationRecordService().saveOrUpdate(operationRecord);
  }

  /**
   * 撤销零售单
   */
  private void revokeRetail() {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //更改订单信息为撤单
      mOrderInfo.setStatus(PxOrderInfo.STATUS_CANCEL);
      mOrderInfo.setEndTime(new Date());
      DaoServiceUtil.getOrderInfoService().update(mOrderInfo);
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
    //撤销Details
    revokeDetails();
  }

  /**
   * 撤销桌位单
   */
  private void revokeTableBill() {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //更改订单信息为撤单
      mOrderInfo.setStatus(PxOrderInfo.STATUS_CANCEL);
      mOrderInfo.setEndTime(new Date());
      DaoServiceUtil.getOrderInfoService().update(mOrderInfo);
      //更改桌台状态
      QueryBuilder<TableOrderRel> tableOrderRelQb =
          DaoServiceUtil.getTableOrderRelService().queryBuilder();
      tableOrderRelQb.where(TableOrderRelDao.Properties.PxTableInfoId.eq(mTableInfo.getId()));
      Join<TableOrderRel, PxOrderInfo> tableOrderRelJoin =
          tableOrderRelQb.join(TableOrderRelDao.Properties.PxOrderInfoId, PxOrderInfo.class);
      tableOrderRelJoin.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
      long count = tableOrderRelQb.count();
      if (count == 0) {
        mTableInfo.setStatus(PxTableInfo.STATUS_EMPTY);
        DaoServiceUtil.getTableInfoService().update(mTableInfo);
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
    //撤销Details
    revokeDetails();
  }

  /**
   * 撤销Details
   */
  //@formatter:off
  private void revokeDetails() {
    //删除未下单的
    List<PxOrderDetails> delList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
        .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_UNORDER))
        .list();
    for (PxOrderDetails details : delList) {
      PxProductInfo dbProduct = details.getDbProduct();
      DaoServiceUtil.getProductInfoService().refresh(dbProduct);
      //沽清状态
      //带规格
      if(details.getDbFormatInfo() != null){
        QueryBuilder<PxProductFormatRel> formatRel = DaoServiceUtil.getProductFormatRelService()
                .queryBuilder()
                .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
                .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(details.getDbProduct().getId()))
                .where(PxProductFormatRelDao.Properties.PxFormatInfoId.eq(details.getDbFormatInfo().getId()));
        if(formatRel !=null){
          PxProductFormatRel productFormatRel = formatRel.unique();
          if (dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
            if (productFormatRel.getStock() != null) {
              productFormatRel.setStock(Double.valueOf(NumberFormatUtils.formatFloatNumber(productFormatRel.getStock() + details.getMultipleUnitNumber())));
              productFormatRel.setStatus(PxProductFormatRel.STATUS_ON_SALE);
            } else {
              productFormatRel.setStock(null);
            }
          } else {
            if (productFormatRel.getStock() != null) {
              productFormatRel.setStock(Double.valueOf(NumberFormatUtils.formatFloatNumber(productFormatRel.getStock() + details.getNum())));
              productFormatRel.setStatus(PxProductFormatRel.STATUS_ON_SALE);
            } else {
              productFormatRel.setStock(null);
            }
          }
          //储存
          DaoServiceUtil.getProductFormatRelService().saveOrUpdate(productFormatRel);
        }
      } else {
        //不带规格
        if (dbProduct.getStatus().equals(PxProductInfo.STATUS_STOP_SALE) || ((dbProduct.getStatus().equals(PxProductInfo.STATUS_ON_SALE)) && dbProduct.getOverPlus() != null && dbProduct.getOverPlus() != 0)) {
          if (dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
            dbProduct.setOverPlus(Double.valueOf(NumberFormatUtils.formatFloatNumber(dbProduct.getOverPlus() + details.getMultipleUnitNumber())));
          } else {
            dbProduct.setOverPlus(Double.valueOf(NumberFormatUtils.formatFloatNumber(dbProduct.getOverPlus() + details.getNum())));
          }
          dbProduct.setStatus(PxProductInfo.STATUS_ON_SALE);
          DaoServiceUtil.getProductInfoService().saveOrUpdate(dbProduct);
        }
      }

    }
    if (delList != null && delList.size() != 0) {
      EventBus.getDefault().post(new UpdateProdInfoListStatusEvent());
      DaoServiceUtil.getOrderDetailsService().delete(delList);
    }
    //退菜时间
    Date refundDate = new Date();
    //Map
    SparseArray<PrintDetailsCollect> collectArray = new SparseArray<>();
    List<Long> printIdList = new ArrayList<>();
    //合并打印内容
    MergePrintDetails.mergeByRevoke(mOrderInfo,refundDate,collectArray,printIdList);
    //变更已下单状态
    List<PxOrderDetails> revokeList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
        .where(PxOrderDetailsDao.Properties.OrderStatus.eq(PxOrderDetails.ORDER_STATUS_ORDER))
        .list();
    for (PxOrderDetails details : revokeList) {
      details.setOrderStatus(PxOrderDetails.ORDER_STATUS_REFUND);
    }
    DaoServiceUtil.getOrderDetailsService().saveOrUpdate(revokeList);
    //后厨打印
    PrintTaskManager.printKitchenTask(collectArray,printIdList,true);
  }

  /**
   * 显示客单和菜单
   */
  private void showBillMenu() {
    FragmentTransaction transaction = mFm.beginTransaction();
    hideAllFragment(transaction);
    mCashBillFragment = mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
    mCashMenuFragment = mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    //客单
    if (mCashBillFragment == null) {
      mCashBillFragment = CashBillFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mCashBillFragment, Constants.CASH_BILL_TAG);
    } else {
      transaction.show(mCashBillFragment);
    }
    //菜单
    if (mCashMenuFragment == null) {
      mCashMenuFragment = CashMenuFragment.newInstance("param");
      transaction.add(R.id.cash_content_right, mCashMenuFragment, Constants.CASH_MENU_TAG);
    } else {
      transaction.show(mCashMenuFragment);
    }
    transaction.commit();
    //显示MainActivity3个悬浮按钮
    ((MainActivity) mAct).mCashFabs.setVisibility(View.VISIBLE);
    //更改FindBillFragment标记
    FindBillFragment.mCurrentLeftFragment = FindBillFragment.BILL;
    //重置信息
    mMoveTableInfo = null;
    mTvMoveTable.setText("");
    mOperateType = "";
  }

  /**
   * 隐藏所有Fragment
   */

  private void hideAllFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments == null) return;
    for (Fragment fragment : allAddedFragments) {
      transaction.hide(fragment);
    }
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
   * 接收FindBill传来的桌台信息，移动桌台
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void moveTable(MoveTableEvent event) {
    mMoveTableInfo = event.getTableInfo();
    //显示移动桌台名称
    String type = mMoveTableInfo.getType();
    String tableType = null;
    if (type != null) {
      PxTableArea area = DaoServiceUtil.getTableAreaService()
          .queryBuilder()
          .where(PxTableAreaDao.Properties.Type.eq(type))
          .unique();
      tableType = area == null ? "大厅" : area.getName();

    }else{
      tableType = "大厅";
    }
    mTvMoveTable.setText("移动至(" + tableType + ")" + mMoveTableInfo.getName());
    //更改操作标识符
    mOperateType = MOVE_TABLE;
  }

  /**
   * 接收CashBill传来的订单信息
   */
  //@formatter:on
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void receiveOrderInfo(
      SendOrderToModifyBillEvent event) {
    mOrderInfo = event.getOrderInfo();
    TableOrderRel orderRel = DaoServiceUtil.getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mOrderInfo.getId()))
        .unique();
    //零售单
    if (mOrderInfo != null && orderRel == null) {
      mTvPeopleNumber.setText("无");
      mRlPeopleNum.setClickable(false);
      mTvTableName.setText("无");
    }
    //桌位单
    else if (mOrderInfo != null && orderRel != null) {
      mTableInfo = orderRel.getDbTable();
      mTvPeopleNumber.setText(mOrderInfo.getActualPeopleNumber() + "");
      mRlPeopleNum.setClickable(true);
      mTvTableName.setText(mTableInfo.getName());
    }
    if (mOrderInfo != null && mOrderInfo.getRemarks() != null && mOrderInfo.getRemarks().equals("") == false) {
      mEtCustomRemark.setText(mOrderInfo.getRemarks());
    } else {
      mEtCustomRemark.setText("");
    }
    //促销计划
    mPromotioInfo = mOrderInfo.getDbPromotioById();
    if (mPromotioInfo == null) {
      mTvPromotioInfo.setVisibility(View.INVISIBLE);
    } else {
      mTvPromotioInfo.setText(mPromotioInfo.getName());
      mTvPromotioInfo.setVisibility(View.VISIBLE);
    }
    //接收到CashBill发送的信息后，将本桌台信息发送给FindBillFragment
    EventBus.getDefault()
        .postSticky(
            new SendTableToFindBillTableEvent().setTableInfo(mTableInfo).setOrderInfo(mOrderInfo));
  }

  //促销计划
  //@formatter:on
  @OnClick(R.id.rl_promotio_info) public void selectedPromotioInfo() {
    //获取有效促销计划
    final List<PxPromotioInfo> promotioInfoList = PromotioDetailsHelp.getPromotioInfoList();
    if (promotioInfoList.isEmpty()) {
      ToastUtils.showShort(null, "请在后台添加促销计划!");
      return;
    }
    String[] items = new String[promotioInfoList.size()];
    for (int i = 0; i < promotioInfoList.size(); i++) {
      PxPromotioInfo promotioInfo = promotioInfoList.get(i);
      items[i] = promotioInfo.getName();
    }
    final MaterialDialog selectPromotioDialog = DialogUtils.showListDialog(mAct, "促销计划", items);
    MDButton posBtn = selectPromotioDialog.getActionButton(DialogAction.POSITIVE);
    MDButton negBtn = selectPromotioDialog.getActionButton(DialogAction.NEGATIVE);
    negBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DialogUtils.dismissDialog(selectPromotioDialog);
      }
    });
    posBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int selectedIndex = selectPromotioDialog.getSelectedIndex();
        mPromotioInfo = promotioInfoList.get(selectedIndex);
        mTvPromotioInfo.setText(mPromotioInfo.getName());
        mTvPromotioInfo.setVisibility(View.VISIBLE);
        DialogUtils.dismissDialog(selectPromotioDialog);
      }
    });
  }

  /**
   * 修改订单的促销计划信息
   */
  private void changeOrderPromotioInfo(PxOrderInfo orderInfo, PxPromotioInfo promotioInfo) {
    List<PxOrderDetails> dbOrderDetailsList = DaoServiceUtil.getOrderDetailsService()
        .queryBuilder()
        .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(orderInfo.getId()))
        .list();
    for (PxOrderDetails details : dbOrderDetailsList) {
      PxProductInfo dbProduct = details.getDbProduct();
      //双单位
      boolean isTwoUnit = dbProduct.getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE);
      PxPromotioDetails validPromotioDetails =
          PromotioDetailsHelp.getValidPromotioDetails(promotioInfo, details.getDbFormatInfo(),
              details.getDbProduct());
      Pair<Double, Double> pricePair = getOrderDetailsUnitPrice(details, validPromotioDetails);
      //
      details.setUnitPrice(pricePair.first);
      details.setUnitVipPrice(pricePair.second);
      //
      details.setPrice(details.getUnitPrice() * (isTwoUnit ? details.getMultipleUnitNumber() : details.getNum()));
      details.setVipPrice(details.getUnitVipPrice() * (isTwoUnit ? details.getMultipleUnitNumber() : details.getNum()));
    }
    DaoServiceUtil.getOrderDetailsService().update(dbOrderDetailsList);
  }

  /**
   * 获取该订单详情 UnitPrice + UnitVipPrice
   */
  private Pair<Double, Double> getOrderDetailsUnitPrice(PxOrderDetails orderDetails, PxPromotioDetails promotioDetails) {
    if (promotioDetails != null) {
      return new Pair<>(promotioDetails.getPromotionalPrice(), promotioDetails.getPromotionalPrice());
    }
    PxFormatInfo formatInfo = orderDetails.getDbFormatInfo();
    PxProductInfo dbProduct = orderDetails.getDbProduct();
    //没规格
    if (formatInfo == null)  return new Pair<>(dbProduct.getPrice(), dbProduct.getVipPrice());

    WhereCondition formatCondition = PxProductFormatRelDao.Properties.PxFormatInfoId.isNull();
    if (formatInfo != null){
      formatCondition = PxProductFormatRelDao.Properties.PxFormatInfoId.eq(formatInfo.getId());
    }
    //规格价格
    PxProductFormatRel formatRel = DaoServiceUtil.getProductFormatRelService()
        .queryBuilder()
        .where(PxProductFormatRelDao.Properties.DelFlag.eq("0"))
        .where(formatCondition)
        .where(PxProductFormatRelDao.Properties.PxProductInfoId.eq(dbProduct.getId()))
        .unique();
    if (formatRel != null) return new Pair<>(formatRel.getPrice(), formatRel.getVipPrice());
    return new Pair<>(dbProduct.getPrice(), dbProduct.getVipPrice());
  }

  //删除促销计划
  @OnClick(R.id.tv_promotio) public void deletePromotioDetails() {
    mPromotioInfo = null;
    mTvPromotioInfo.setVisibility(View.INVISIBLE);
  }
}
