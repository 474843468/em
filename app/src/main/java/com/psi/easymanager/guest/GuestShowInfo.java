package com.psi.easymanager.guest;

import com.psi.easymanager.module.PxOrderDetails;
import java.io.Serializable;
import java.util.List;

/**
 * Created by dorado on 2016/11/22.
 */
public class GuestShowInfo implements Serializable {
  //桌位单
  private boolean mIsTableOrder;
  //桌位名称
  private String mTableName;
  //人数
  private int mPeopleNum;
  //订单数据
  private List<PxOrderDetails> mData;
  //合计
  private double mTotalAmount;
  //应收
  private double mReceivableAmount;
  //实收
  private double mReceivedAmount;
  //找零
  private double mChangeAmount;
  //优惠
  private double mPrivilegeAmount;
  //待支付金额
  private double mWaitPayAmount;

  public boolean isTableOrder() {
    return mIsTableOrder;
  }

  public void setTableOrder(boolean tableOrder) {
    mIsTableOrder = tableOrder;
  }

  public String getTableName() {
    return mTableName;
  }

  public void setTableName(String tableName) {
    mTableName = tableName;
  }

  public int getPeopleNum() {
    return mPeopleNum;
  }

  public void setPeopleNum(int peopleNum) {
    mPeopleNum = peopleNum;
  }

  public List<PxOrderDetails> getData() {
    return mData;
  }

  public void setData(List<PxOrderDetails> data) {
    mData = data;
  }

  public double getTotalAmount() {
    return mTotalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    mTotalAmount = totalAmount;
  }

  public double getReceivableAmount() {
    return mReceivableAmount;
  }

  public void setReceivableAmount(double receivableAmount) {
    mReceivableAmount = receivableAmount;
  }

  public double getReceivedAmount() {
    return mReceivedAmount;
  }

  public void setReceivedAmount(double receivedAmount) {
    mReceivedAmount = receivedAmount;
  }

  public double getChangeAmount() {
    return mChangeAmount;
  }

  public void setChangeAmount(double changeAmount) {
    mChangeAmount = changeAmount;
  }

  public double getPrivilegeAmount() {
    return mPrivilegeAmount;
  }

  public void setPrivilegeAmount(double privilegeAmount) {
    mPrivilegeAmount = privilegeAmount;
  }

  public double getWaitPayAmount() {
    return mWaitPayAmount;
  }

  public void setWaitPayAmount(double waitPayAmount) {
    mWaitPayAmount = waitPayAmount;
  }
}
