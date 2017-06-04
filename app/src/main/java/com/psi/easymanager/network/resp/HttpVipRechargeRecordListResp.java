package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxRechargeRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhen on 2016-10-22.
 */
public class HttpVipRechargeRecordListResp extends HttpResp{

  /**
   * 充值记录列表
   */
  @Expose
  private List<PxRechargeRecord> list = new ArrayList<>();

  public List<PxRechargeRecord> getList() {
    return list;
  }

  public void setList(List<PxRechargeRecord> list) {
    this.list = list;
  }
}
