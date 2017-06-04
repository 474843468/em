package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxProductCategory;

/**
 * Created by dorado on 2016/6/6.
 * 点击商品，向OverBillSaleContentFragment传递商品和筛选条件
 */
public class DetailsContentEvent {
  private PxOrderInfo mOrderInfo;

  public PxOrderInfo getOrderInfo() {
    return mOrderInfo;
  }

  public DetailsContentEvent setOrderInfo(PxOrderInfo orderInfo) {
    mOrderInfo = orderInfo;
    return this;
  }
}
