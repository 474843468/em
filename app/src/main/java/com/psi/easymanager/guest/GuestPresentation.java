package com.psi.easymanager.guest;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.psi.easymanager.R;
import com.psi.easymanager.adapter.GuestShowAdapter;
import com.psi.easymanager.module.PxOrderDetails;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dorado on 2016/11/22.
 */
public class GuestPresentation extends Presentation {
  private List<PxOrderDetails> mDetailsList;
  private GuestShowAdapter mGuestShowAdapter;
  private Context mContext;

  private ListView mLvGuestShowInfo;
  private TextView mTvTable;
  private TextView mTvPeopleNum;
  private TextView mTvTotalAmount;
  private TextView mTvReceivableAmount;
  private TextView mTvReceivedAmount;
  private TextView mTvChangeAmount;
  private TextView mTvPrivilegeAmount;
  private TextView mTvWaitPayAmount;

  public GuestPresentation(Context outerContext, Display display) {
    super(outerContext, display);
    mContext = outerContext;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.layout_customer_show);
    initView();
  }

  private void initView() {
    mLvGuestShowInfo = (ListView) findViewById(R.id.lv_guest_show);
    mTvTable = (TextView) findViewById(R.id.tv_table);
    mTvPeopleNum = (TextView) findViewById(R.id.tv_people_num);
    mTvTotalAmount = (TextView) findViewById(R.id.tv_total_amount);
    mTvReceivableAmount = (TextView) findViewById(R.id.tv_receivable_amount);
    mTvReceivedAmount = (TextView) findViewById(R.id.tv_received_amount);
    mTvChangeAmount = (TextView) findViewById(R.id.tv_change_amount);
    mTvPrivilegeAmount = (TextView) findViewById(R.id.tv_privilege_amount);
    mTvWaitPayAmount = (TextView) findViewById(R.id.tv_wait_pay_amount);

    mDetailsList = new ArrayList<>();
    mGuestShowAdapter = new GuestShowAdapter(mContext, mDetailsList);
    mLvGuestShowInfo.setAdapter(mGuestShowAdapter);
  }

  public void setData(GuestShowInfo guestShowInfo) {
    boolean tableOrder = guestShowInfo.isTableOrder();
    if (tableOrder) {
      mTvTable.setText("桌台:" + guestShowInfo.getTableName());
      mTvPeopleNum.setText("人数:" + guestShowInfo.getPeopleNum());
    } else {
      mTvTable.setText("零售单");
      mTvPeopleNum.setVisibility(View.GONE);
    }
    mTvTotalAmount.setText("合计:" + guestShowInfo.getTotalAmount());
    mTvReceivableAmount.setText("应收:" + guestShowInfo.getReceivableAmount());
    mTvReceivedAmount.setText("实收:" + guestShowInfo.getReceivedAmount());
    mTvChangeAmount.setText("找零:" + guestShowInfo.getChangeAmount());
    mTvPrivilegeAmount.setText("优惠:" + guestShowInfo.getPrivilegeAmount());
    mTvWaitPayAmount.setText("待支付:" + guestShowInfo.getWaitPayAmount());

    List<PxOrderDetails> data = guestShowInfo.getData();
    //订单信息
    if (data == null) {
      mDetailsList = new ArrayList<>();
    } else {
      mDetailsList = data;
    }
    mGuestShowAdapter.setData(mDetailsList);
  }

  public void clearData() {
    mTvTable.setText("桌台:");
    mTvPeopleNum.setText("人数:");
    mDetailsList = new ArrayList<>();
    mGuestShowAdapter.setData(mDetailsList);
    mTvTotalAmount.setText("合计:");
    mTvReceivableAmount.setText("应收:");
    mTvReceivedAmount.setText("实收:");
    mTvChangeAmount.setText("找零:" );
    mTvPrivilegeAmount.setText("优惠:" );
    mTvWaitPayAmount.setText("待支付:" );
  }
}
