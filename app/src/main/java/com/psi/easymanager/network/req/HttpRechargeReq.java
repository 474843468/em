package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.upload.module.UpLoadRechargeRecord;

public class HttpRechargeReq extends HttpReq {

  @Expose private UpLoadRechargeRecord record;

  public UpLoadRechargeRecord getRecord() {
    return record;
  }

  public void setRecord(UpLoadRechargeRecord record) {
    this.record = record;
  }
}
