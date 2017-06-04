package com.psi.easymanager.event;

import com.psi.easymanager.module.PxOrderDetails;

/**
 * CashBill中ListView末尾添加条目并刷新
 */
public class CashBillAddItemEvent {
  private PxOrderDetails details;

  public PxOrderDetails getDetails() {
    return details;
  }

  public CashBillAddItemEvent setDetails(PxOrderDetails details) {
    this.details = details;
    return this;
  }
}
