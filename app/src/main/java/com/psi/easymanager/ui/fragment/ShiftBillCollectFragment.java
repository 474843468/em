package com.psi.easymanager.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.psi.easymanager.R;
import com.psi.easymanager.adapter.ShiftBillAdapter;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.dbUtil.DaoServiceUtil;
import com.psi.easymanager.event.ShiftChangeQueryInfoEvent;
import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.ui.activity.ShiftChangeFunctionsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by psi on 2016/7/30.
 * 交接班-所有账单信息
 */
public class ShiftBillCollectFragment extends BaseFragment {

  @Bind(R.id.rcv) RecyclerView mRcvShiftBill;

  private static final String SHIFT_BILL_COLLECT_PARAM = "param";
  private String mParam;
  private ShiftChangeFunctionsActivity mAct;

  //Data
  private List<PxOrderInfo> mOrderInfoList;
  //Adapter
  private ShiftBillAdapter mShiftBillAdapter;

  //每页数量
  private static final int PAGE_NUM = 4;
  //当前页码
  private int mCurrentPage = 1;
  //总页码
  private int mTotalPage;

  private int mArea;
  private long mUserId;
  private long mEndTime;

  public static ShiftBillCollectFragment newInstance(String mParam) {
    Bundle bundle = new Bundle();
    bundle.putString("param", mParam);
    ShiftBillCollectFragment fragment = new ShiftBillCollectFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam = getArguments().getString(SHIFT_BILL_COLLECT_PARAM);
    }
    mAct = (ShiftChangeFunctionsActivity) getActivity();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_shift_bill_collect, null);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initRcv();
    EventBus.getDefault().register(this);
    EventBus.getDefault().getStickyEvent(ShiftChangeQueryInfoEvent.class);
  }

  /**
   * 初始化Rcv
   */
  private void initRcv() {
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(mAct, LinearLayoutManager.VERTICAL, false);
    mShiftBillAdapter = new ShiftBillAdapter(mAct, mOrderInfoList);
    mRcvShiftBill.setHasFixedSize(true);
    mRcvShiftBill.setLayoutManager(layoutManager);
    mRcvShiftBill.setAdapter(mShiftBillAdapter);
  }

  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
  public void receiveAllBills(ShiftChangeQueryInfoEvent event) {
    if (event.getType() != ShiftChangeQueryInfoEvent.TYPE_ALL_ORDER) return;
    mArea = event.getArea();
    mUserId = event.getUserId();
    mEndTime = event.getEndTime();
    //查询数量
    queryNum(mArea, mUserId, mEndTime);
    //查询订单
    queryOrderInfo(mArea, mUserId, mEndTime);
  }

  /**
   * 查询数量
   */
  private void queryNum(int area, long userId, long endTime) {
    long count = 0;
    switch (area) {
      /**
       * 所有
       */
      case ShiftChangeQueryInfoEvent.AREA_ALL:
        if (userId != 0) {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .count();
        } else {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(
                  PxOrderInfoDao.Properties.IsReserveOrder.eq(PxOrderInfo.IS_REVERSE_ORDER_FALSE))
              .count();
        }
        break;
      /**
       * 桌台区域
       */
      case ShiftChangeQueryInfoEvent.AREA_TABLE:
        if (userId != 0) {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .count();
        } else {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .count();
        }
        break;
      /**
       * 零售单
       */
      case ShiftChangeQueryInfoEvent.AREA_RETAIL:
        if (userId != 0) {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_RETAIL))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .count();
        } else {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_RETAIL))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .count();
        }
        break;
      /**
       * 大厅
       */
      case ShiftChangeQueryInfoEvent.AREA_HALL:
        if (userId != 0) {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(PxOrderInfo.FINAL_AREA_HALL))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .count();
        } else {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(PxOrderInfo.FINAL_AREA_HALL))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .count();
        }
        break;

      /**
       * 包间
       */
      case ShiftChangeQueryInfoEvent.AREA_PARLOR:
        if (userId != 0) {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(PxOrderInfo.FINAL_AREA_PARLOR))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .count();
        } else {
          count = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(PxOrderInfo.FINAL_AREA_PARLOR))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .count();
        }
        break;
    }
    mTotalPage = (int) Math.ceil(count / (double) PAGE_NUM);
  }

  /**
   * 查询订单信息
   */
  private void queryOrderInfo(int area, long userId, long endTime) {
    switch (area) {
      /**
       * 所有
       */
      case ShiftChangeQueryInfoEvent.AREA_ALL:
        if (userId != 0) {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        } else {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(
                  PxOrderInfoDao.Properties.IsReserveOrder.eq(PxOrderInfo.IS_REVERSE_ORDER_FALSE))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        }
        break;
      /**
       * 桌台区域
       */
      case ShiftChangeQueryInfoEvent.AREA_TABLE:
        if (userId != 0) {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        } else {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        }
        break;
      /**
       * 零售单
       */
      case ShiftChangeQueryInfoEvent.AREA_RETAIL:
        if (userId != 0) {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_RETAIL))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        } else {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_RETAIL))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        }
        break;
      /**
       * 大厅
       */
      case ShiftChangeQueryInfoEvent.AREA_HALL:
        if (userId != 0) {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(PxOrderInfo.FINAL_AREA_HALL))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        } else {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(PxOrderInfo.FINAL_AREA_HALL))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        }
        break;
      /**
       * 包间
       */
      case ShiftChangeQueryInfoEvent.AREA_PARLOR:
        if (userId != 0) {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(PxOrderInfo.FINAL_AREA_PARLOR))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .where(PxOrderInfoDao.Properties.CheckOutUserId.eq(userId))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        } else {
          mOrderInfoList = DaoServiceUtil.getOrderInfoService()
              .queryBuilder()
              .where(PxOrderInfoDao.Properties.EndTime.between(0L, endTime))
              .where(PxOrderInfoDao.Properties.OrderInfoType.eq(PxOrderInfo.ORDER_INFO_TYPE_TABLE))
              .where(PxOrderInfoDao.Properties.FinalArea.eq(PxOrderInfo.FINAL_AREA_PARLOR))
              .where(PxOrderInfoDao.Properties.Status.eq(PxOrderInfo.STATUS_FINISH))
              .where(PxOrderInfoDao.Properties.ShiftChangeType.eq(PxOrderInfo.SHIFT_CHANGE_FREEZE))
              .orderDesc(PxOrderInfoDao.Properties.EndTime)
              .limit(PAGE_NUM)
              .offset(PAGE_NUM * (mCurrentPage - 1))
              .list();
        }
        break;
    }
    if (mOrderInfoList != null && mOrderInfoList.size() != 0) {
      mShiftBillAdapter.setData(mOrderInfoList);
    }
  }

  /**
   * 上页 下页
   */
  @OnClick({ R.id.btn_last_page, R.id.btn_next_page }) public void chagnePage(Button button) {
    switch (button.getId()) {
      case R.id.btn_last_page:
        if (mCurrentPage > 1) {
          mCurrentPage -= 1;
          //查询数据
          queryOrderInfo(mArea, mUserId, mEndTime);
        }
        break;
      case R.id.btn_next_page:
        if (mCurrentPage < mTotalPage) {
          mCurrentPage += 1;
          //查询数据
          queryOrderInfo(mArea, mUserId, mEndTime);
        }
        break;
    }
  }

  /**
   * 重置注入
   */
  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.unbind(this);
    EventBus.getDefault().unregister(this);
  }
}
