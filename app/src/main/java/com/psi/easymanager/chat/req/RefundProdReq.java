package com.psi.easymanager.chat.req;

import java.io.Serializable;

/**
 * Created by zjq on 2016/5/10.
 */
public class RefundProdReq implements Serializable {
  private long mOrderId;//订单id
  private long mRefundNum;//退货数量
  private double mRefundMultipleNum;//多单位退货数量
  private String mTableId;//桌台id
  private String mRefundReasonObjId;//退菜原因id
  private String mWaiterId;//服务生id
  private String mObjId;//DetailsObjId


  public long getOrderId() {
    return mOrderId;
  }

  public void setOrderId(long orderId) {
    mOrderId = orderId;
  }

  public long getRefundNum() {
    return mRefundNum;
  }

  public void setRefundNum(long refundNum) {
    mRefundNum = refundNum;
  }

  public double getRefundMultipleNum() {
    return mRefundMultipleNum;
  }

  public void setRefundMultipleNum(double refundMultipleNum) {
    mRefundMultipleNum = refundMultipleNum;
  }

  public String getTableId() {
    return mTableId;
  }

  public void setTableId(String tableId) {
    mTableId = tableId;
  }

  public String getRefundReasonObjId() {
    return mRefundReasonObjId;
  }

  public void setRefundReasonObjId(String refundReasonObjId) {
    mRefundReasonObjId = refundReasonObjId;
  }

  public String getWaiterId() {
    return mWaiterId;
  }

  public void setWaiterId(String waiterId) {
    mWaiterId = waiterId;
  }

  public String getObjId() {
    return mObjId;
  }

  public void setObjId(String objId) {
    mObjId = objId;
  }
}
