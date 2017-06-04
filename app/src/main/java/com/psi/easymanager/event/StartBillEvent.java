package com.psi.easymanager.event;

import com.psi.easymanager.module.PxTableInfo;

/**
 * Created by zjq on 2016/3/31.
 * 选桌开台时将数据传给开单页面
 */
public class StartBillEvent {
  private PxTableInfo mTableInfo;

  public PxTableInfo getTableInfo() {
    return mTableInfo;
  }

  public StartBillEvent setTableInfo(PxTableInfo tableInfo) {
    mTableInfo = tableInfo;
    return this;
  }
}
