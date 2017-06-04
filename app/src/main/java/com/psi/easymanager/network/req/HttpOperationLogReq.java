package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxOperationLog;
import java.util.List;

public class HttpOperationLogReq extends HttpReq {

  @Expose private List<PxOperationLog> dataList;

  public void setDataList(List<PxOperationLog> dataList) {
    this.dataList = dataList;
  }
}
