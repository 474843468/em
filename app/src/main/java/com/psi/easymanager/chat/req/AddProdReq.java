package com.psi.easymanager.chat.req;

import com.psi.easymanager.chat.AppConfirmOrderDetails;
import java.io.Serializable;
import java.util.List;

/**
 * Created by dorado on 2016/6/13.
 */
public class AddProdReq implements Serializable {
  //订单id
  private long mOrderId;
  //桌台objId
  private String mTableId;
  //下单商品信息
  private List<AppConfirmOrderDetails> mConfirmOrderDetailsList;
  //服务生id
  private String mWaiterId;

  public long getOrderId() {
    return mOrderId;
  }

  public void setOrderId(long orderId) {
    mOrderId = orderId;
  }

  public String getTableId() {
    return mTableId;
  }

  public void setTableId(String tableId) {
    mTableId = tableId;
  }

  public List<AppConfirmOrderDetails> getConfirmOrderDetailsList() {
    return mConfirmOrderDetailsList;
  }

  public void setConfirmOrderDetailsList(List<AppConfirmOrderDetails> confirmOrderDetailsList) {
    mConfirmOrderDetailsList = confirmOrderDetailsList;
  }

  public String getWaiterId() {
    return mWaiterId;
  }

  public void setWaiterId(String waiterId) {
    mWaiterId = waiterId;
  }
}
