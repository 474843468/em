package com.psi.easymanager.ui.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.RetailOrderAdapter;
import com.psi.easymanager.adapter.TableAdapter;
import com.psi.easymanager.adapter.TableTypeAdapter;
import com.psi.easymanager.config.Constants;
import com.psi.easymanager.dao.PxOrderDetailsDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxTableAreaDao;
import com.psi.easymanager.dao.PxTableInfoDao;
import com.psi.easymanager.dao.TableOrderRelDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.CashBillUpdateOrderEvent;
import com.psi.easymanager.event.FindBillRefreshStatusEvent;
import com.psi.easymanager.event.MoveTableEvent;
import com.psi.easymanager.event.SendTableToFindBillTableEvent;
import com.psi.easymanager.event.StartBillEvent;
import com.psi.easymanager.module.AppTableType;
import com.psi.easymanager.module.PxExtraDetails;
import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxMethodInfo;
import com.psi.easymanager.module.PxOrderDetails;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductInfo;
import com.psi.easymanager.module.PxTableAlteration;
import com.psi.easymanager.module.PxTableArea;
import com.psi.easymanager.module.PxTableInfo;
import com.psi.easymanager.module.TableOrderRel;
import com.psi.easymanager.print.net.PrintTaskManager;
import com.psi.easymanager.ui.activity.MainActivity;
import com.psi.easymanager.widget.RecyclerViewSpaceItemDecoration;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by dorado on 2016/5/27.
 */
public class FindBillFragment extends BaseFragment {
  //桌台Rcv
  @Bind(R.id.rcv_table_retail) RecyclerView mRcvTableRetail;
  //桌台类型lv
  @Bind(R.id.lv_table_type) ListView mLvTableType;
  //类型Rg
  @Bind(R.id.rg_type) RadioGroup mRgType;

  //MainActivity
  private MainActivity mAct;
  //Fragment管理器
  private FragmentManager mFm;
  //桌台分类列表
  private List<AppTableType> mTableTypeList;
  //桌台分类Adapter
  private TableTypeAdapter mTableTypeAdapter;
  //桌台列表
  private List<PxTableInfo> mTableList;
  //桌台Adapter
  private TableAdapter mTableAdapter;
  //零售单Adapter
  private RetailOrderAdapter mRetailOrderAdapter;
  //零售单列表
  private List<PxOrderInfo> mRetailOrderList;

  //标记当前左侧Fragment
  public static int mCurrentLeftFragment;
  //客单
  public static final int BILL = 0;
  //开单
  public static final int START_BILL = 1;
  //改单
  public static final int MODIFY_BILL = 2;

  //要改单的桌台信息
  private PxTableInfo mModifyTableInfo;
  //要改单的订单信息
  private PxOrderInfo mModifyOrderInfo;

  //客单页面
  private Fragment mCashBillFragment;
  //开单页面
  private Fragment mStartBillFragment;
  //菜单页面
  private Fragment mCashMenuFragment;

  public static FindBillFragment newInstance(boolean tabVisiable) {
    Bundle bundle = new Bundle();
    bundle.putBoolean("param", tabVisiable);
    FindBillFragment fragment = new FindBillFragment();
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
      mStartBillFragment = mFm.findFragmentByTag(Constants.START_BILL_TAG);
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_find_bill, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initView();
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(SendTableToFindBillTableEvent.class);
    EventBus.getDefault().getStickyEvent(FindBillRefreshStatusEvent.class);
  }

  private void initView() {
    //读取所有分类
    loadAllCate();
    //初始化Rcv
    initRcv();
    //默认显示大厅数据
    mLvTableType.setItemChecked(0, true);
    changeTableDataByType(0);
  }

  /**
   * 选择Tab
   */
  @OnCheckedChanged({ R.id.rb_table, R.id.rb_retail }) public void changeTab(RadioButton rb) {
    if (rb.isChecked()) {
      switch (rb.getId()) {
        case R.id.rb_table:
          mLvTableType.setVisibility(View.VISIBLE);
          mRcvTableRetail.setAdapter(mTableAdapter);
          if (mLvTableType != null) {
            changeTableDataByType(mLvTableType.getCheckedItemPosition());
          } else {
            changeTableDataByType(0);
          }
          break;
        case R.id.rb_retail:
          mLvTableType.setVisibility(View.INVISIBLE);
          loadAllRetailOrder();
          mRcvTableRetail.setAdapter(mRetailOrderAdapter);
          mRetailOrderAdapter.setData(mRetailOrderList);
          break;
      }
    }
  }

  /**
   * 获取所有餐桌分类
   */
  private void loadAllCate() {
    mTableTypeList = new ArrayList<AppTableType>();
    //桌台区域
    List<PxTableArea> tableAreaList = DaoServiceUtil.getTableAreaService()
        .queryBuilder()
        .where(PxTableAreaDao.Properties.DelFlag.eq("0"))
        .orderAsc(PxTableAreaDao.Properties.Name)
        .list();
    if (!tableAreaList.isEmpty()) {
      for (PxTableArea tableArea : tableAreaList) {
        AppTableType type = new AppTableType();
        type.setType(tableArea.getType());
        type.setTableArea(tableArea);
        mTableTypeList.add(type);
      }
    }
    AppTableType typeAll = new AppTableType();
    typeAll.setType(AppTableType.ALL);
    typeAll.setName("全部");

    mTableTypeList.add(typeAll);
  }

  /**
   * 获取所有零售单
   */
  private void loadAllRetailOrder() {
    mRetailOrderList = DaoServiceUtil.getOrderInfoService()
        .queryBuilder()
        .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_RETAIL))
        .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH))
        .list();
  }

  /**
   * 初始化基础视图
   */
  //@formatter:off
  private void initRcv() {
    //桌台类型Adapter
    mTableTypeAdapter = new TableTypeAdapter(mAct, mTableTypeList);
    mLvTableType.setAdapter(mTableTypeAdapter);
    mLvTableType.setOnItemClickListener(new OnTypeClickListener());
    //桌台Adapter
    GridLayoutManager tableLayoutManager = new GridLayoutManager(mAct, 5, LinearLayoutManager.VERTICAL, false);
    mTableList = new ArrayList<>();
    mTableAdapter = new TableAdapter(mAct, mTableList);
    mTableAdapter.setOnTableClickListener(new OnTableClickListener());
    mRcvTableRetail.setHasFixedSize(true);
    mRcvTableRetail.setLayoutManager(tableLayoutManager);
    mRcvTableRetail.setAdapter(mTableAdapter);
    int spaceWidth = getResources().getDimensionPixelSize(R.dimen.find_bill_rcv_item_horizontal_space_width);
    int spaceHeight = getResources().getDimensionPixelSize(R.dimen.find_bill_rcv_item_vertical_space_height);
    mRcvTableRetail.addItemDecoration(new RecyclerViewSpaceItemDecoration(spaceWidth, spaceHeight));
    //零售Adapter
    mRetailOrderList = new ArrayList<PxOrderInfo>();
    mRetailOrderAdapter = new RetailOrderAdapter(mAct,mRetailOrderList);
    mRetailOrderAdapter.setOnRetailOrderClickListener(new OnRetailOrderClickListener());
  }

  /**
   * 桌台类型点击
   */
  //@formatter:on
  class OnTypeClickListener implements AdapterView.OnItemClickListener {

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      changeTableDataByType(position);
    }
  }

  /**
   * 桌台点击
   */
  class OnTableClickListener implements TableAdapter.OnTableClickListener {
    //占用桌台点击
    @Override public void onOccupiedTableClick(int pos) {
      switch (mCurrentLeftFragment) {
        case BILL://客单
          mTableAdapter.setSelected(pos);
          //当前订单
          PxTableInfo pxTableInfo = mTableList.get(pos);
          QueryBuilder<TableOrderRel> queryBuilder = DaoServiceUtil.getTableOrderRelService()
              .queryBuilder()
              .where(TableOrderRelDao.Properties.PxTableInfoId.eq(pxTableInfo.getId()));
          Join<TableOrderRel, PxOrderInfo> join =
              queryBuilder.join(TableOrderRelDao.Properties.PxOrderInfoId, PxOrderInfo.class);
          join.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
          final List<TableOrderRel> relList = queryBuilder.list();
          if (relList == null || relList.size() == 0) return;
          if (relList.size() == 1) {
            //开启蒙层
            mAct.isShowProgress(true);
            //订单
            PxOrderInfo currentOrder = relList.get(0).getDbOrder();
            //向CashBillFragment传递订单信息
            EventBus.getDefault().post(new CashBillUpdateOrderEvent().setOrderInfo(currentOrder));
          } else {
            String[] orderNoList = new String[relList.size()];
            for (int i = 0; i < relList.size(); i++) {
              orderNoList[i] = relList.get(i).getDbOrder().getOrderNumCutOut();
            }
            new MaterialDialog.Builder(mAct).title("选择订单")
                .items(orderNoList)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                  @Override public boolean onSelection(MaterialDialog dialog, View view, int which,
                      CharSequence text) {
                    return true;
                  }
                })
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    int selectedIndex = dialog.getSelectedIndex();
                    PxOrderInfo dbOrder = relList.get(selectedIndex).getDbOrder();
                    //向CashBillFragment传递订单信息
                    EventBus.getDefault()
                        .post(new CashBillUpdateOrderEvent().setOrderInfo(dbOrder));
                  }
                })
                .show();
          }
          break;
        case START_BILL://开单
          //ToastUtils.showShort(mAct, "该桌已被占用，不能开单!");
          PxTableInfo tableInfo = mTableList.get(pos);
          EventBus.getDefault().postSticky(new StartBillEvent().setTableInfo(tableInfo));
          break;
        case MODIFY_BILL://改单(合并订单)
          mTableAdapter.setSelected(pos);
          //不能合并自身
          PxTableInfo targetTableInfo = mTableList.get(pos);
          if (targetTableInfo.getObjectId().equals(mModifyTableInfo.getObjectId())) return;
          //查找订单
          QueryBuilder<TableOrderRel> queryBuilderModify = DaoServiceUtil.getTableOrderRelService()
              .queryBuilder()
              .where(TableOrderRelDao.Properties.PxTableInfoId.eq(targetTableInfo.getId()));
          Join<TableOrderRel, PxOrderInfo> joinModify =
              queryBuilderModify.join(TableOrderRelDao.Properties.PxOrderInfoId, PxOrderInfo.class);
          joinModify.where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_UNFINISH));
          final List<TableOrderRel> relListModify = queryBuilderModify.list();
          if (relListModify == null || relListModify.size() == 0) return;
          if (relListModify.size() == 1) {
            showModifyDialog(relListModify.get(0).getDbOrder());
          } else {
            showModifyChooseDialog(relListModify);
          }
      }
    }

    //闲置桌台点击
    @Override public void onEmptyTableClick(int pos) {
      //重置点击状态
      mTableAdapter.setSelected(pos);
      //当前桌台
      PxTableInfo tableInfo = mTableList.get(pos);
      switch (mCurrentLeftFragment) {
        case BILL://客单
          showStartBillDialog(tableInfo);
          break;
        case START_BILL://开单
          EventBus.getDefault().postSticky(new StartBillEvent().setTableInfo(tableInfo));
          break;
        case MODIFY_BILL://改单(移动桌台)
          //向改单页面传递对应的桌台信息 如果不为空，说明为桌位单
          if (mModifyTableInfo != null) {
            EventBus.getDefault().post(new MoveTableEvent().setTableInfo(tableInfo));
          }
          break;
      }
    }
  }

  /**
   * 零售单点击
   */
  class OnRetailOrderClickListener implements RetailOrderAdapter.OnRetailOrderClickListener {

    @Override public void onRetailOrderClick(int pos) {
      mRetailOrderAdapter.setSelected(pos);
      PxOrderInfo orderInfo = mRetailOrderList.get(pos);
      switch (mCurrentLeftFragment) {
        case BILL:
          //向CashBillFragment传递订单信息
          EventBus.getDefault().post(new CashBillUpdateOrderEvent().setOrderInfo(orderInfo));
          break;
        case START_BILL:

          break;
        case MODIFY_BILL:

          break;
      }
    }
  }

  /**
   * 确认开单对话框
   */
  private void showStartBillDialog(final PxTableInfo tableInfo) {
    //对话框
    new MaterialDialog.Builder(mAct).title("警告")
        .content("是否开单?")
        .positiveText("确认")
        .negativeText("取消")
        .negativeColor(getResources().getColor(R.color.primary_text))
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            showStartBillFragment(tableInfo);
          }
        })
        .show();
  }

  /**
   * 合并桌台信息 提示框
   */
  private void showModifyDialog(final PxOrderInfo orderInfo) {
    new MaterialDialog.Builder(mAct).title("警告")
        .content("是否合并订单?")
        .positiveText("确定")
        .negativeText("取消")
        .negativeColor(getResources().getColor(R.color.primary_text))
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(MaterialDialog dialog, DialogAction which) {
            mergeTableInfo(orderInfo);
          }
        })
        .show();
  }

  /**
   * 选择改单的订单
   */
  private void showModifyChooseDialog(final List<TableOrderRel> relList) {
    String[] orderNoList = new String[relList.size()];
    for (int i = 0; i < relList.size(); i++) {
      orderNoList[i] = relList.get(i).getDbOrder().getOrderNumCutOut();
    }
    new MaterialDialog.Builder(mAct).title("选择订单")
        .items(orderNoList)
        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
          @Override public boolean onSelection(MaterialDialog dialog, View view, int which,
              CharSequence text) {
            return true;
          }
        })
        .positiveText("确定")
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            int selectedIndex = dialog.getSelectedIndex();
            PxOrderInfo dbOrder = relList.get(selectedIndex).getDbOrder();
            mergeTableInfo(dbOrder);
          }
        })
        .show();
  }

  /**
   * 合并桌台信息
   */
  //@formatter:off
  private void mergeTableInfo(PxOrderInfo targetOrder) {
    SQLiteDatabase db = DaoServiceUtil.getUserDao().getDatabase();
    db.beginTransaction();
    try {
      //开启蒙层
       mAct.isShowProgress(true);
      //合计人数
      targetOrder.setActualPeopleNumber(mModifyOrderInfo.getActualPeopleNumber() + targetOrder.getActualPeopleNumber());
      //如果合并订单有正在计算的附加费，关闭
      if (mModifyOrderInfo.getDbCurrentExtra() != null) {
        mModifyOrderInfo.getDbCurrentExtra().setStopTime(new Date());
        mModifyOrderInfo.setDbCurrentExtra(null);
      }
      //要合并的订单详情
      List<PxOrderDetails> modifyOrderDetailsList = mModifyOrderInfo.getDbOrderDetailsList();
      for (PxOrderDetails details : modifyOrderDetailsList) {
        //如果是套餐或套餐内商品,直接更换订单
        if (details.getInCombo().equals(PxOrderDetails.IN_COMBO_TRUE) || details.getIsComboDetails().equals(PxOrderDetails.IS_COMBO_TRUE)){
          details.setDbOrder(targetOrder);
          DaoServiceUtil.getOrderDetailsService().saveOrUpdate(details);
          continue;
        }
        //做法
        PxMethodInfo dbMethodInfo = details.getDbMethodInfo();
        //规格
        PxFormatInfo dbFormatInfo = details.getDbFormatInfo();

        //查询对应商品的已下单信息
          WhereCondition formatCondition = PxOrderDetailsDao.Properties.PxFormatInfoId.isNull();
          WhereCondition methodCondition = PxOrderDetailsDao.Properties.PxMethodInfoId.isNull();
          if (dbFormatInfo != null){
            formatCondition = PxOrderDetailsDao.Properties.PxFormatInfoId.eq(dbFormatInfo.getId());
          }
          if (dbMethodInfo != null){
            methodCondition = PxOrderDetailsDao.Properties.PxMethodInfoId.eq(dbMethodInfo.getId());
          }

        //已存在的Details
       PxOrderDetails existDetails= DaoServiceUtil.getOrderDetailsService()
              .queryBuilder()
              .where(PxOrderDetailsDao.Properties.PxOrderInfoId.eq(targetOrder.getId()))
              .where(PxOrderDetailsDao.Properties.Status.eq(details.getStatus()))
              .where(PxOrderDetailsDao.Properties.OrderStatus.eq(details.getOrderStatus()))
              .where(PxOrderDetailsDao.Properties.DiscountRate.eq(details.getDiscountRate()))
              .where(PxOrderDetailsDao.Properties.IsGift.eq(details.getIsGift()))
              .where(PxOrderDetailsDao.Properties.InCombo.eq(PxOrderDetails.IN_COMBO_FALSE))
              .where(PxOrderDetailsDao.Properties.IsComboDetails.eq(PxOrderDetails.IS_COMBO_FALSE))
              .where(PxOrderDetailsDao.Properties.Remarks.eq(details.getRemarks()))
              .where(PxOrderDetailsDao.Properties.PxProductInfoId.eq(details.getDbProduct().getId()))
              .where(methodCondition)
              .where(formatCondition)
              .unique();
        //如果没有，直接更改订单
        if (existDetails == null) {
          details.setDbOrder(targetOrder);
          DaoServiceUtil.getOrderDetailsService().saveOrUpdate(details);
        }
        //如果有,变更已有的数量和价格
        else {
          if (details.getDbProduct().getMultipleUnit().equals(PxProductInfo.IS_TWO_UNIT_TURE)) {
            existDetails.setNum(existDetails.getNum() + details.getNum());
            existDetails.setMultipleUnitNumber(existDetails.getMultipleUnitNumber() + details.getMultipleUnitNumber());
            existDetails.setPrice(existDetails.getUnitPrice() * existDetails.getMultipleUnitNumber());
            existDetails.setVipPrice(existDetails.getUnitVipPrice() * existDetails.getMultipleUnitNumber());
          } else {
            existDetails.setNum(existDetails.getNum() + details.getNum());
            existDetails.setPrice(existDetails.getUnitPrice() * existDetails.getNum());
            existDetails.setVipPrice(existDetails.getUnitVipPrice() * existDetails.getNum());
          }
          DaoServiceUtil.getOrderDetailsService().delete(details);
          DaoServiceUtil.getOrderDetailsService().saveOrUpdate(existDetails);
        }
      }
      //要合并的附加费详情
      List<PxExtraDetails> modifyExtraDetailsList = mModifyOrderInfo.getDbExtraDetailsList();
      for (PxExtraDetails details : modifyExtraDetailsList) {
        details.setDbOrder(targetOrder);
      }
      DaoServiceUtil.getExtraDetailsService().saveOrUpdate(modifyExtraDetailsList);
      //恢复桌台状态
      mModifyTableInfo.setStatus(PxTableInfo.STATUS_EMPTY);
      DaoServiceUtil.getTableInfoService().update(mModifyTableInfo);
      //变更订单状态
      mModifyOrderInfo.setStatus(PxOrderInfo.STATUS_CANCEL);
      DaoServiceUtil.getOrderInfoService().saveOrUpdate(mModifyOrderInfo);
      //添加桌台变更信息
      PxTableAlteration pxTableAlteration = new PxTableAlteration();
      //操作时间
      pxTableAlteration.setOperateTime(new Date());
      //移动
      pxTableAlteration.setType(PxTableAlteration.TYPE_MERGE);
      //未打印
      pxTableAlteration.setIsPrinted(false);
      //未清空
      pxTableAlteration.setIsClear(false);
      //订单
      pxTableAlteration.setDbOrder(targetOrder);
      //原桌台
      pxTableAlteration.setDbOriginalTable(mModifyTableInfo);
      //目标桌台
      TableOrderRel orderRel = DaoServiceUtil.getTableOrderRelService()
          .queryBuilder()
          .where(TableOrderRelDao.Properties.PxOrderInfoId.eq(targetOrder.getId()))
          .unique();
      PxTableInfo dbTable = orderRel.getDbTable();
      pxTableAlteration.setDbTargetTable(dbTable);
      DaoServiceUtil.getTableAlterationService().saveOrUpdate(pxTableAlteration);
      //更新FindBillFragment页面
      int checkedItemPosition = mLvTableType.getCheckedItemPosition();
      changeTableDataByType(checkedItemPosition);
      mTableAdapter.setSelected(-1);
      //通知CashBill更新订单信息
      EventBus.getDefault().post(new CashBillUpdateOrderEvent().setOrderInfo(targetOrder));
      //找单页面接收
      //EventBus.getDefault().postSticky(new FindBillRefreshStatusEvent());
      refreshTableStaus(new FindBillRefreshStatusEvent());
      //显示BillMenu页面
      showBillMenuFragment();
      db.setTransactionSuccessful();
      //打印移并桌信息
      //printTableAlteration(pxTableAlteration);
      PrintTaskManager.printTableAlteration(pxTableAlteration);
    } catch (Exception e) {
      e.printStackTrace();
      //关闭
      mAct.isShowProgress(false);
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 显示左侧客单页面
   */
  private void showBillMenuFragment() {
    //显示按钮
    mAct.mCashFabs.setVisibility(View.VISIBLE);
    //更新FindBill标签
    FindBillFragment.mCurrentLeftFragment = FindBillFragment.BILL;
    FragmentTransaction transaction = mFm.beginTransaction();
    //隐藏所有Fragment
    hideAllFragment(transaction);
    //客单页面
    mCashBillFragment = mFm.findFragmentByTag(Constants.CASH_BILL_TAG);
    if (mCashBillFragment == null) {
      mCashBillFragment = CashBillFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mCashBillFragment, Constants.CASH_BILL_TAG);
    } else {
      transaction.show(mCashBillFragment);
    }
    //菜单页面
    mCashMenuFragment = mFm.findFragmentByTag(Constants.CASH_MENU_TAG);
    if (mCashMenuFragment == null) {
      mCashMenuFragment = CashMenuFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mCashMenuFragment, Constants.CASH_MENU_TAG);
    } else {
      transaction.show(mCashMenuFragment);
    }
    transaction.commit();
  }

  /**
   * 显示左侧开单页面
   */
  private void showStartBillFragment(PxTableInfo tableInfo) {
    //隐藏按钮
    mAct.mCashFabs.setVisibility(View.GONE);
    //更新FindBill标签
    FindBillFragment.mCurrentLeftFragment = FindBillFragment.START_BILL;
    //更新页面
    FragmentTransaction transaction = mFm.beginTransaction();
    hideLeftFragment(transaction);
    mStartBillFragment = mFm.findFragmentByTag(Constants.START_BILL_TAG);
    if (mStartBillFragment == null) {
      mStartBillFragment = StartBillFragment.newInstance("param");
      transaction.add(R.id.cash_content_left, mStartBillFragment, Constants.START_BILL_TAG);
    } else {
      //重置StartBill显示
      ((StartBillFragment) mStartBillFragment).resetData();
      transaction.show(mStartBillFragment);
    }
    EventBus.getDefault().postSticky(new StartBillEvent().setTableInfo(tableInfo));
    transaction.commit();
  }

  /**
   * 修改桌台类型数据
   */
  private void changeTableDataByType(int position) {
    AppTableType appTableType = mTableTypeList.get(position);
    switch (appTableType.getType()) {
      case AppTableType.ALL:
        mTableList = DaoServiceUtil.getTableInfoService()
            .queryBuilder()
            .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
            .orderAsc(PxTableInfoDao.Properties.Type)
            .orderAsc(PxTableInfoDao.Properties.SortNo)
            .list();
        break;
      default:
        PxTableArea tableArea = appTableType.getTableArea();
        mTableList = DaoServiceUtil.getTableInfoService()
            .queryBuilder()
            .where(PxTableInfoDao.Properties.Type.eq(tableArea.getType()))
            .where(PxTableInfoDao.Properties.DelFlag.eq("0"))
            .orderAsc(PxTableInfoDao.Properties.SortNo)
            .list();
        break;
    }
    mTableAdapter.setData(mTableList);
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
   * 隐藏左侧Fragment
   */
  private void hideLeftFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        if (!(fragment instanceof FindBillFragment)) transaction.hide(fragment);
      }
    }
  }

  /**
   * 隐藏所有Fragment
   */
  private void hideAllFragment(FragmentTransaction transaction) {
    List<Fragment> allAddedFragments = mFm.getFragments();
    if (allAddedFragments != null) {
      for (Fragment fragment : allAddedFragments) {
        transaction.hide(fragment);
      }
    }
  }

  /**
   * 改单时 接收ModifyBill传来的桌台信息
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void receiveTableInfo(SendTableToFindBillTableEvent event) {
    mModifyTableInfo = event.getTableInfo();
    mModifyOrderInfo = event.getOrderInfo();
    //轮询，设置选中位置
    for (int i = 0; i < mTableList.size(); i++) {
      PxTableInfo tableInfo = mTableList.get(i);
      if (mModifyTableInfo == null) continue;
      if (mModifyTableInfo.getObjectId().equals(tableInfo.getObjectId())) {
        mTableAdapter.setSelected(i);
      }
    }
  }

  /**
   * 刷新桌台状态
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN) public void refreshTableStaus(FindBillRefreshStatusEvent event) {
    doRefreshBill();
  }

  /**
   * 具体刷新操作
   */
  private void doRefreshBill() {
    int checkedRadioButtonId = mRgType.getCheckedRadioButtonId();
    if (checkedRadioButtonId == R.id.rb_table){
      if (mLvTableType != null) {
        int checkedItemPosition = mLvTableType.getCheckedItemPosition();
        changeTableDataByType(checkedItemPosition);
      }
    } else {
      loadAllRetailOrder();
      mRetailOrderAdapter.setData(mRetailOrderList);
    }
  }
  /**
   * 刷新桌台信息
   */
  public void refreshOnDataUpdate(){
    //刷洗桌台区域数据
    loadAllCate();
    mTableTypeAdapter.setData(mTableTypeList);
    //初始化tab，默认选择桌位单
    mRgType.check(R.id.rb_table);
    //默认显示大厅数据
    mLvTableType.setItemChecked(0, true);
    changeTableDataByType(0);
  }
}
