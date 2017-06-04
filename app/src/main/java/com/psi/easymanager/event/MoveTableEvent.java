package com.psi.easymanager.event;

import com.psi.easymanager.module.PxTableInfo;

/**
 * Created by zjq on 2016/4/11.
 * FindBillFragment向改单页面传递要移动的桌台信息
 */
public class MoveTableEvent {
  private PxTableInfo mTableInfo;

  public PxTableInfo getTableInfo() {
    return mTableInfo;
  }

  public MoveTableEvent setTableInfo(PxTableInfo tableInfo) {
    mTableInfo = tableInfo;
    return this;
  }
}
