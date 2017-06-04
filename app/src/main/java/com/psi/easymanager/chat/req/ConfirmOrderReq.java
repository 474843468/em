package com.psi.easymanager.chat.req;

import com.psi.easymanager.chat.AppConfirmOrderDetails;
import java.io.Serializable;
import java.util.List;

/**
 * Created by dorado on 2016/6/11.
 */
public class ConfirmOrderReq implements Serializable {
  //桌台id
  private String mTableId;
  //桌台人数
  private int mPeopleNum;
  //用户id
  private String mWaiterId;
  //备注
  private String mRemarks;
  //促销计划
  private String mPromotioInfoId;


  //下单商品信息
  private List<AppConfirmOrderDetails> mConfirmOrderDetailsList;

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

  public int getPeopleNum() {
    return mPeopleNum;
  }

  public void setPeopleNum(int peopleNum) {
    mPeopleNum = peopleNum;
  }

  public String getWaiterId() {
    return mWaiterId;
  }

  public void setWaiterId(String waiterId) {
    mWaiterId = waiterId;
  }

  public List<AppConfirmOrderDetails> getConfirmOrderDetailsList() {
    return mConfirmOrderDetailsList;
  }

  public void setConfirmOrderDetailsList(List<AppConfirmOrderDetails> confirmOrderDetailsList) {
    mConfirmOrderDetailsList = confirmOrderDetailsList;
  }

  public String getRemarks() {
    return mRemarks;
  }

  public void setRemarks(String remarks) {
    mRemarks = remarks;
  }
}
