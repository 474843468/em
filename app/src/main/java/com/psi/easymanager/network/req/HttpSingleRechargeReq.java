package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * Created by wangzhen on 2016-10-21.
 *
 * 单个会员充值 请求
 */
public class HttpSingleRechargeReq extends HttpReq {
  @Expose
  private String vipId;
  @Expose
  private Double money;
  @Expose
  private String planId;

  public String getVipId() {
    return vipId;
  }

  public void setVipId(String vipId) {
    this.vipId = vipId;
  }

  public Double getMoney() {
    return money;
  }

  public void setMoney(Double money) {
    this.money = money;
  }

  public String getPlanId() {
    return planId;
  }

  public void setPlanId(String planId) {
    this.planId = planId;
  }
}
