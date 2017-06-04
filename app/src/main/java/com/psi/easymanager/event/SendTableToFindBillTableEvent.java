package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxTableInfo;

/**
 * Created by zjq on 2016/4/11.
 * ModifyBillFragment将本桌台信息和订单信息发送给FindBillFragment
 * 用于RecyclerView显示标记以及合并操作
 */
public class SendTableToFindBillTableEvent {
  private PxTableInfo mTableInfo;
  private PxOrderInfo mOrderInfo;

  public PxTableInfo getTableInfo() {
    return mTableInfo;
  }

  public SendTableToFindBillTableEvent setTableInfo(PxTableInfo tableInfo) {
    mTableInfo = tableInfo;
    return this;
  }

  public PxOrderInfo getOrderInfo() {
    return mOrderInfo;
  }

  public SendTableToFindBillTableEvent setOrderInfo(PxOrderInfo orderInfo) {
    mOrderInfo = orderInfo;
    return this;
  }
}
