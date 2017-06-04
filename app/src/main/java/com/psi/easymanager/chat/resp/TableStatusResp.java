package com.psi.easymanager.chat.resp;

import com.psi.easymanager.chat.AppTableStatus;
import java.io.Serializable;
import java.util.List;

/**
 * Created by dorado on 2016/6/10.
 */
public class TableStatusResp implements Serializable {
  private List<AppTableStatus> mTableStatusList;

  public List<AppTableStatus> getTableStatusList() {
    return mTableStatusList;
  }

  public void setTableStatusList(List<AppTableStatus> tableStatusList) {
    mTableStatusList = tableStatusList;
  }
}
