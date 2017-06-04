package com.psi.easymanager.event;

/**
 * Created by dorado on 2016/5/30.
 * 刷新CashBill中ListView和对应数据
 */
public class RefreshCashBillListEvent {
  private int from;

  public int getFrom() {
    return from;
  }

  public void setFrom(int from) {
    this.from = from;
  }
}
