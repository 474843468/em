package com.psi.easymanager.chat.req;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dorado on 2016/6/10.
 */
public class TableStatusReq implements Serializable {
  private List<String> mTableIdList;

  public List<String> getTableIdList() {
    return mTableIdList;
  }

  public void setTableIdList(List<String> tableIdList) {
    mTableIdList = tableIdList;
  }
}
