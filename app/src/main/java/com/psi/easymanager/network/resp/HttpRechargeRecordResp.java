package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxRechargeRecord;
import java.util.ArrayList;
import java.util.List;

public class HttpRechargeRecordResp extends HttpResp {
  @Expose
  private List<PxRechargeRecord> list = new ArrayList<PxRechargeRecord>();
  @Expose private PxRechargeRecord pxRechargeRecord;

  public List<PxRechargeRecord> getList() {
    return list;
  }

  public void setList(List<PxRechargeRecord> list) {
    this.list = list;
  }

  public PxRechargeRecord getPxRechargeRecord() {
    return pxRechargeRecord;
  }

  public void setPxRechargeRecord(PxRechargeRecord pxRechargeRecord) {
    this.pxRechargeRecord = pxRechargeRecord;
  }
}
