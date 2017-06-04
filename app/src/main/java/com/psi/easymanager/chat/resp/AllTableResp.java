package com.psi.easymanager.chat.resp;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxTableInfo;
import java.io.Serializable;
import java.util.List;

/**
 * User: ylw
 * Date: 2017-02-14
 * Time: 15:41
 * FIXME
 */
public class AllTableResp implements Serializable{
  @Expose
  private List<PxTableInfo> mTableInfoList;

  public AllTableResp(List<PxTableInfo> tableInfoList) {
    mTableInfoList = tableInfoList;
  }

  public List<PxTableInfo> getTableInfoList() {
    return mTableInfoList;
  }

  public void setTableInfoList(List<PxTableInfo> tableInfoList) {
    mTableInfoList = tableInfoList;
  }
}