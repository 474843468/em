package com.psi.easymanager.chat;

import java.io.Serializable;

/**
 * Created by zjq on 2016/5/9.
 */
public class AppMoveTableItem implements Serializable {
  private String mTableName;
  private String mTableId;
  private boolean isOccupy;

  public String getTableName() {
    return mTableName;
  }

  public void setTableName(String tableName) {
    mTableName = tableName;
  }

  public String getTableId() {
    return mTableId;
  }

  public void setTableId(String tableId) {
    mTableId = tableId;
  }

  public boolean isOccupy() {
    return isOccupy;
  }

  public void setIsOccupy(boolean isOccupy) {
    this.isOccupy = isOccupy;
  }
}
