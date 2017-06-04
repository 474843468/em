package com.psi.easymanager.chat.resp;

import com.psi.easymanager.chat.AppDetailsCollection;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zjq on 2016/5/4.
 */
public class OrderInfoResp implements Serializable {
  public static final int SUCCESS = 1;
  public static final int FAILURE = 2;
  private String tableName;//桌名
  private int peopleNum;//人数
  private long orderId;//订单id
  private String startTime;//开单时间
  private List<AppDetailsCollection> mDetailsList;//详情列表
  private String mOrderReqNum;//流水号
  private String mRemarks;//备注
  private String mPromotioInfoId;//促销计划
  private int result;
  private String des;
  private String mTableId;//

  public String getPromotioInfoId() {
    return mPromotioInfoId;
  }

  public void setPromotioInfoId(String promotioInfoId) {
    mPromotioInfoId = promotioInfoId;
  }

  public String getTableId() {
    return mTableId;
  }

  public void setTableId(String tableId) {
    mTableId = tableId;
  }
  /**
   * Get and Set
   */
  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public int getPeopleNum() {
    return peopleNum;
  }

  public void setPeopleNum(int peopleNum) {
    this.peopleNum = peopleNum;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public List<AppDetailsCollection> getDetailsList() {
    return mDetailsList;
  }

  public void setDetailsList(List<AppDetailsCollection> detailsList) {
    mDetailsList = detailsList;
  }

  public String getOrderReqNum() {
    return mOrderReqNum;
  }

  public void setOrderReqNum(String orderReqNum) {
    mOrderReqNum = orderReqNum;
  }

  public String getRemarks() {
    return mRemarks;
  }

  public void setRemarks(String remarks) {
    mRemarks = remarks;
  }

  public int getResult() {
    return result;
  }

  public void setResult(int result) {
    this.result = result;
  }

  public String getDes() {
    return des;
  }

  public void setDes(String des) {
    this.des = des;
  }
}
