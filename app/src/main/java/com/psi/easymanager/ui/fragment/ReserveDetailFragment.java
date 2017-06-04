package com.psi.easymanager.ui.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.orhanobut.logger.Logger;
import com.psi.easymanager.R;
import com.psi.easymanager.common.App;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxExtraChargeDao;
import com.psi.easymanager.dao.PxOrderNumDao;
import com.psi.easymanager.dao.PxTableExtraRelDao;
import com.psi.easymanager.dao.PxTableInfoDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CancelReserveEvent;
import com.psi.easymanager.event.ConfirmStartBillEvent;
import com.psi.easymanager.event.FindBillRefreshStatusEvent;
import com.psi.easymanager.event.ReserveDetailEvent;
import com.psi.easymanager.module.PxExtraCharge;
import com.psi.easymanager.module.PxExtraDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxOrderNum;
import com.psi.easymanager.module.PxTableExtraRel;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.module.User;
import com.psi.easymanager.ui.activity.ReserveManagerActivity;
import com.psi.easymanager.utils.DialogUtils;
import com.psi.easymanager.utils.ToastUtils;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * User: ylw
 * Date: 2016-09-12
 * Time: 17:17
 * 预订单详情
 */
public class ReserveDetailFragment extends BaseFragment {

  //tel
  @Bind(R.id.tv_contact_phone) TextView mTvContactPhone;
  //联系人
  @Bind(R.id.tv_link_man) TextView mTvLinkMan;
  //桌位
  @Bind(R.id.tv_table_arrange) TextView mTvTable;
  //就餐时间
  @Bind(R.id.tv_dining_time) TextView mTvDiningTime;
  //底部按钮
  @Bind(R.id.include_bottom_fabs) LinearLayout mLLBottomFabs;

  private PxOrderInfo mReserveOrder;
  private ReserveManagerActivity mAct;
  private FragmentManager mFm;
  //订单日期用 sdf
  private SimpleDateFormat mSdfDate = new SimpleDateFormat("yyyyMMdd");
  //序列号用 sdf
  private SimpleDateFormat mSdfOrderReq = new SimpleDateFormat("yyyyMMddHHmmss");
  private Long currentTime = null;//当前时间long

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAct = (ReserveManagerActivity) getActivity();
    mFm = mAct.getSupportFragmentManager();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_reserve_detail, null);
    //显示到达按钮
    view.findViewById(R.id.space_temp).setVisibility(View.VISIBLE);
    view.findViewById(R.id.space_temp1).setVisibility(View.VISIBLE);
    view.findViewById(R.id.ibtn_reach).setVisibility(View.VISIBLE);
    view.findViewById(R.id.ibtn_delete).setVisibility(View.VISIBLE);
    ButterKnife.bind(this, view);
    return view;
  }

  public static ReserveDetailFragment newInstance(String param) {
    Bundle bundle = new Bundle();
    bundle.putString("param", param);
    ReserveDetailFragment fragment = new ReserveDetailFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);
  }

  /**
   * 接收预订单的详情
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void receiveReserveOrder(ReserveDetailEvent event) {
    mReserveOrder = event.getReserveOrder();
    initView();
  }

  /**
   * init view
   */
  private void initView() {
    currentTime = new Date().getTime();
    mTvLinkMan.setText(mReserveOrder.getLinkMan());
    mTvContactPhone.setText(mReserveOrder.getContactPhone());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 预订单
    if (PxOrderInfo.RESERVE_STATE_RESERVE.equals(mReserveOrder.getReserveState())) {
      if (mReserveOrder.getDiningTime().getTime() < currentTime) { //过期
        mTvDiningTime.setTextColor(mAct.getResources().getColor(R.color.red));
      } else {
        mTvDiningTime.setTextColor(mAct.getResources().getColor(R.color.colorAccent));
      }
    }else{
      mTvDiningTime.setTextColor(mAct.getResources().getColor(R.color.colorAccent));
    }
    mTvDiningTime.setText(sdf.format(mReserveOrder.getDiningTime()));
    TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mReserveOrder.getId()))
        .unique();
    if (PxOrderInfo.RESERVE_STATE_RESERVE.equals(mReserveOrder.getReserveState())) {
      mTvTable.setText((unique != null) ? unique.getDbTable().getName() : "无");
    } else {
      mTvTable.setText((unique != null) ? unique.getDbTable().getName() : "零售单");
    }
    //已到达订单  不提供修改功能
    if (PxOrderInfo.RESERVE_STATE_RESERVE.equals(mReserveOrder.getReserveState())) {
      mLLBottomFabs.setVisibility(View.VISIBLE);
    } else {
      mLLBottomFabs.setVisibility(View.GONE);
    }
  }

  /**
   * 新增 预订单
   */
  @OnClick(R.id.ibtn_add_detail) public void addReserve(ImageButton iBtn) {
    Fragment addReserveFragment = mFm.findFragmentByTag(Constants.ADD_RESERVE);
    FragmentTransaction transaction = mFm.beginTransaction();
    mAct.hideLeftAllFragment(transaction);
    if (addReserveFragment == null) {
      addReserveFragment = AddReserveFragment.newInstance("param");
      transaction.add(R.id.fl_left, addReserveFragment, Constants.ADD_RESERVE);
    } else {
      transaction.show(addReserveFragment);
    }
    transaction.commit();
  }

  /**
   * 修改 预订单
   */
  @OnClick(R.id.ibtn_modify) public void modifyReserve(ImageButton iBtn) {
    Fragment modifyReserveFragment = mFm.findFragmentByTag(Constants.MODIFY_RESERVE);
    FragmentTransaction transaction = mFm.beginTransaction();
    mAct.hideLeftAllFragment(transaction);
    if (modifyReserveFragment == null) {
      modifyReserveFragment = ModifyReserveFragment.newInstance("param");
      transaction.add(R.id.fl_left, modifyReserveFragment, Constants.MODIFY_RESERVE);
    } else {
      transaction.show(modifyReserveFragment);
    }
    transaction.commit();
    EventBus.getDefault().postSticky(new ReserveDetailEvent(mReserveOrder));
  }

  /**
   * 删除预订单
   */
  @OnClick(R.id.ibtn_delete) public void deleteReserve(ImageButton iBtn) {
    new MaterialDialog.Builder(mAct).title("预订单")
        .content("确认删除该预订单?")
        .negativeText("取消")
        .positiveText("确认")
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            dismissDialog(dialog);
            deleteReserveOrder();
          }
        })
        .show();
  }

  /**
   * 删除 预订单
   */
  private void deleteReserveOrder() {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //有桌台关联的删除
      TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mReserveOrder.getId()))
          .unique();
      if (unique != null){
        DaoServiceUtil.getTableOrderRelService().delete(unique);
      }
      DaoServiceUtil.getOrderInfoService().delete(mReserveOrder);
      db.setTransactionSuccessful();
      exitThis();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 预订单 改为已到达
   */
  @OnClick(R.id.ibtn_reach) public void reach(ImageButton iBtn) {
    TableOrderRel unique = DaoServiceUtil.getTableOrderRelService()
        .queryBuilder()
        .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(mReserveOrder.getId()))
        .unique();
    if (unique == null) { //选桌台 或 为零售单
      final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("预订单")
          .content("该订单没有选择桌台!")
          .positiveText("选择桌台")
          .negativeText("取消")
          .neutralText("开零售单")
          .negativeColor(mAct.getResources().getColor(R.color.primary_text))
          .show();
      MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
      MDButton neutralBtn = dialog.getActionButton(DialogAction.NEUTRAL);
      //继续选择桌台
      positiveBtn.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          //dismiss Dialog
          dismissDialog(dialog);
          // 选择桌台
          selectTable();
        }
      });
      //生成零售单
      neutralBtn.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          ////dismiss Dialog
          //dismissDialog(dialog);
          //零售单
          changeRetailReach();

          DialogUtils.dismissDialog(dialog);
        }
      });
    } else { //生成订单
      final PxTableInfo dbTable = unique.getDbTable();
      //当前状态
      if (PxTableInfo.STATUS_OCCUPIED.equals(dbTable.getStatus())) { //占用
          ToastUtils.showShort(mAct,"该桌台已被占用,请修改其他桌台");
      } else {
        final MaterialDialog dialog = new MaterialDialog.Builder(mAct).title("预订单")
            .content("确认已到达?")
            .positiveText("确认")
            .negativeText("取消")
            .negativeColor(mAct.getResources().getColor(R.color.primary_text))
            .show();
        MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
        //生成订单
        positiveBtn.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            //dismiss Dialog
            dismissDialog(dialog);
            //已到达
            changeTableReach(dbTable);
          }
        });
      }
    }
  }

  /**
   * 保存零售单 该预订单为已到达
   */
  private void changeRetailReach() {
    App app = (App) App.getContext();
    if (app == null) return;
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(app,"请重启APP!");
      return;
    }

    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //是否锁定状态 默认不锁定
      mReserveOrder.setIsLock(false);
      //开始时间
      mReserveOrder.setStartTime(new Date());
      //未完成
      mReserveOrder.setStatus(PxOrderInfo.STATUS_UNFINISH);
      //抹零
      mReserveOrder.setTailMoney((double) 0);
      //优惠金额
      mReserveOrder.setDiscountPrice((double) 0);
      //支付类优惠
      mReserveOrder.setPayPrivilege((double)0);
      //是否刷卡
      mReserveOrder.setUseVipCard(PxOrderInfo.USE_VIP_CARD_FALSE);
      //用户
      mReserveOrder.setDbUser(user);
      //实收
      mReserveOrder.setRealPrice((double) 0);
      //应收
      mReserveOrder.setAccountReceivable((double) 0);
      //总的找零
      mReserveOrder.setTotalChange((double) 0);
      //订单类型
      mReserveOrder.setOrderInfoType(PxOrderInfo.ORDER_INFO_TYPE_RETAIL);
      //订单号
      PxOrderNum orderNum = DaoServiceUtil.getOrderNumService()
          .queryBuilder()
          .where(PxOrderNumDao.Properties.Date.eq(mSdfDate.format(new Date())))
          .unique();
      if (orderNum == null) {
        PxOrderNum pxOrderNum = new PxOrderNum();
        pxOrderNum.setDate(mSdfDate.format(new Date()));
        pxOrderNum.setNum(1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(pxOrderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(pxOrderNum.getNum(),user);
        mReserveOrder.setOrderNo(orderReqNum);
        mReserveOrder.setOrderReqNo(orderReqNum);
      } else {
        orderNum.setNum(orderNum.getNum() + 1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(orderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(orderNum.getNum(),user);
        mReserveOrder.setOrderNo(orderReqNum);
        mReserveOrder.setOrderReqNo(orderReqNum);
      }
      mReserveOrder.setRemarks("无");
      //修改 订单类型为正常类型
      mReserveOrder.setIsReserveOrder(PxOrderInfo.IS_REVERSE_ORDER_FALSE);
      //修改 预订单状态 为已到达
      mReserveOrder.setReserveState(PxOrderInfo.RESERVE_STATE_REACH);
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(mReserveOrder);
      db.setTransactionSuccessful();
      //由客单页面接收
      EventBus.getDefault().post(new ConfirmStartBillEvent().setOrderInfo(mReserveOrder));
      // reach
      exitThis();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 保存桌位单 该预订单为已到达
   */
  private void changeTableReach(PxTableInfo dbTable) {
    App app = (App) App.getContext();
    if (app == null) return;
    User user = app.getUser();
    if (user == null) {
      ToastUtils.showShort(app,"请重启APP!");
      return;
    }

    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //是否锁定状态 默认不锁定
      mReserveOrder.setIsLock(false);
      // 开始时间
      mReserveOrder.setStartTime(new Date());
      //未完成
      mReserveOrder.setStatus(PxOrderInfo.STATUS_UNFINISH);
      //抹零金额
      mReserveOrder.setTailMoney((double) 0);
      //优惠金额
      mReserveOrder.setDiscountPrice((double) 0);
      //支付类优惠
      mReserveOrder.setPayPrivilege((double) 0);
      //是否刷卡
      mReserveOrder.setUseVipCard(PxOrderInfo.USE_VIP_CARD_FALSE);
      //用户
      mReserveOrder.setDbUser(user);
      //桌台状态
      dbTable.setStatus(PxTableInfo.STATUS_OCCUPIED);
      DaoServiceUtil.getTableInfoService().saveOrUpdate(dbTable);

      //实收
      mReserveOrder.setRealPrice((double) 0);
      //应收
      mReserveOrder.setAccountReceivable((double) 0);
      //总的找零
      mReserveOrder.setTotalChange((double) 0);
      //订单类型
      mReserveOrder.setOrderInfoType(PxOrderInfo.ORDER_INFO_TYPE_TABLE);
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(mReserveOrder);
      ////建立桌台和订单连接
      //TableOrderRel tableOrderRel = new TableOrderRel();
      //tableOrderRel.setDbOrder(mOrderInfo);
      //tableOrderRel.setDbTable(mTableInfo);
      //DaoServiceUtil.getTableOrderRelService().saveOrUpdate(tableOrderRel);
      //查询可用的附加费引用关系
      QueryBuilder<PxTableExtraRel> extraRelQb = DaoServiceUtil.getTableExtraRelService()
          .queryBuilder()
          .where(PxTableExtraRelDao.Properties.DelFlag.eq("0"))
          .where(PxTableExtraRelDao.Properties.PxTableInfoId.eq(dbTable.getId()));
      Join<PxTableExtraRel, PxExtraCharge> extraJoin =
          extraRelQb.join(PxTableExtraRelDao.Properties.PxExtraChargeId, PxExtraCharge.class);
      extraJoin.where(PxExtraChargeDao.Properties.DelFlag.eq("0"));
      PxTableExtraRel rel = extraRelQb.unique();
      //如果不为空，表示有可用的附加费
      if (rel != null) {
        //附加费信息
        PxExtraCharge extraCharge = rel.getDbExtraCharge();
        if (extraCharge.getServiceStatus().equals(PxExtraCharge.ENABLE_TRUE)) {
          //新建附加费详情
          PxExtraDetails pxExtraDetails = new PxExtraDetails();
          //开始时间
          pxExtraDetails.setStartTime(new Date());
          //价格
          pxExtraDetails.setPrice((double) 0);
          //订单
          pxExtraDetails.setDbOrder(mReserveOrder);
          //附加费名
          pxExtraDetails.setExtraName(extraCharge.getName());
          //桌台名
          pxExtraDetails.setTableName(dbTable.getName());
          //已付款
          pxExtraDetails.setPayPrice((double) 0);
          //已打印
          pxExtraDetails.setIsPrinted(false);
          //储存
          DaoServiceUtil.getExtraDetailsService().save(pxExtraDetails);
          //订单更新当前附加费详情
          mReserveOrder.setDbCurrentExtra(pxExtraDetails);
        }
      }
      //订单号
      PxOrderNum orderNum = DaoServiceUtil.getOrderNumService()
          .queryBuilder()
          .where(PxOrderNumDao.Properties.Date.eq(mSdfDate.format(new Date())))
          .unique();
      if (orderNum == null) {
        PxOrderNum pxOrderNum = new PxOrderNum();
        pxOrderNum.setDate(mSdfDate.format(new Date()));
        pxOrderNum.setNum(1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(pxOrderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(pxOrderNum.getNum(),user);
        mReserveOrder.setOrderNo(orderReqNum);
        mReserveOrder.setOrderReqNo(orderReqNum);
      } else {
        orderNum.setNum(orderNum.getNum() + 1);
        DaoServiceUtil.getOrderNumService().saveOrUpdate(orderNum);
        //获取OrderReqNum
        String orderReqNum = createOrderReqNum(orderNum.getNum(),user);
        mReserveOrder.setOrderNo(orderReqNum);
        mReserveOrder.setOrderReqNo(orderReqNum);
      }
      //备注
      mReserveOrder.setRemarks("无");
      //修改 订单类型为正常类型
      mReserveOrder.setIsReserveOrder(PxOrderInfo.IS_REVERSE_ORDER_FALSE);
      //修改 预订单状态 为已到达
      mReserveOrder.setReserveState(PxOrderInfo.RESERVE_STATE_REACH);
      DaoServiceUtil.getOrderInfoService().update(mReserveOrder);
      db.setTransactionSuccessful();
      //由客单页面接收
      EventBus.getDefault().post(new ConfirmStartBillEvent().setOrderInfo(mReserveOrder));
      //找单页面接收
      EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
      // reach
      exitThis();
    } catch (Exception e) {
      e.printStackTrace();
      Logger.e(e.toString());
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 确认已到达  退出当前Fragment 通知ReserveManagerActivity刷新
   * 删除预订单  退出当前Fragment 通知ReserveManagerActivity刷新
   */
  private void exitThis() {
    //影藏 当前Fragment
    FragmentTransaction transaction = mFm.beginTransaction();
    mAct.hideLeftAllFragment(transaction);
    transaction.commit();
    //发 ReserveManagerActivity 更新
    EventBus.getDefault().post(new CancelReserveEvent());
    //mAct.receiveCancelEvent(null);
  }

  /**
   * 选择桌台
   */
  private void selectTable() {
    //显示所有桌台信息
    List<PxTableInfo> tableInfoList = DaoServiceUtil.getTableInfoService()
        .queryBuilder()
        .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
        .list();
    Map<String, PxTableInfo> tableInfoMap = new HashMap<>();
    final List<String> tableNameList = new ArrayList<>();
    for (PxTableInfo tableInfo : tableInfoList) {
      tableInfoMap.put(tableInfo.getName(), tableInfo);
      tableNameList.add(tableInfo.getName());
    }
    new MaterialDialog.Builder(mAct).title("选择桌位")
        .positiveText("确定")
        .negativeText("取消")
        .items(tableNameList)
        .negativeColor(mAct.getResources().getColor(R.color.primary_text))
        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog dialog, View itemView, int which,
              CharSequence text) {
            return true;
          }
        })
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            saveTableInfo(dialog, tableNameList);
          }
        })
        .show();
  }

  /**
   * 保存选择 的桌台
   */
  private void saveTableInfo(MaterialDialog dialog, List<String> tableNameList) {
    int selectedIndex = dialog.getSelectedIndex();
    // dismiss Dialog
    dismissDialog(dialog);
    if (selectedIndex == -1) {
      ToastUtils.showShort(App.getContext(), "请选择正确桌台");
    } else {
      String tableName = tableNameList.get(selectedIndex);
      PxTableInfo currentTable = DaoServiceUtil.getTableInfoService()
          .queryBuilder()
          .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
          .where(PxTableInfoDao.Properties.Name.eq(tableName))
          .unique();
      if (PxTableInfo.STATUS_OCCUPIED.equals(currentTable.getStatus())) { // 桌台占用
        ToastUtils.showShort(App.getContext(), "该桌台已被占用,请选择其他桌台");
      } else {
        //建立桌台和订单连接
        TableOrderRel tableOrderRel = new TableOrderRel();
        tableOrderRel.setDbOrder(mReserveOrder);
        tableOrderRel.setDbTable(currentTable);
        DaoServiceUtil.getTableOrderRelService().saveOrUpdate(tableOrderRel);
        //
        changeTableReach(currentTable);
      }
    }
  }

  /**
   * dismiss Dialog
   */
  private void dismissDialog(MaterialDialog dialog) {
    if (dialog != null && dialog.isShowing()) {
      dialog.dismiss();
    }
  }

  /**
   * 生成reqNum
   */
  private String createOrderReqNum(int orderNum,User user) {
    //公司编码
    int companyHashCode = user.getCompanyCode().hashCode();
    //公司编码求绝对值
    int absHashCode = Math.abs(Integer.valueOf(companyHashCode));
    //公司编码绝对值的String表示
    String strHashCode = String.valueOf(absHashCode);
    //当前日期
    String strDate = mSdfOrderReq.format(new Date());
    //当前序号
    String strSer = String.format("%06d", orderNum);
    String strOrderNum =
        new StringBuilder().append(strHashCode).append(strDate).append(strSer).toString();
    return strOrderNum;
  }

  /**
   * onDestroy
   */
  @Override public void onDestroy() {
    EventBus.getDefault().unregister(this);
    ButterKnife.unbind(this);
    super.onDestroy();
  }
}