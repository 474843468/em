package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

import com.psi.easymanager.module.PxVipInfo;

/**
 * Created by wangzhen on 2016-10-21.
 */
public class HttpSingleVipInfoReq extends HttpReq{

  @Expose
  private PxVipInfo vipInfo;
  @Expose
  private String planId;

  public PxVipInfo getVipInfo() {
    return vipInfo;
  }

  public void setVipInfo(PxVipInfo vipInfo) {
    this.vipInfo = vipInfo;
  }

  public String getPlanId() {
    return planId;
  }

  public void setPlanId(String planId) {
    this.planId = planId;
  }
}
