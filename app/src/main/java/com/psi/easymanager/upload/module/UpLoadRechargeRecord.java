package com.psi.easymanager.upload.module;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxRechargePlan;
import com.psi.easymanager.module.PxVipInfo;
import java.io.Serializable;

/**
 * User: ylw
 * Date: 2016-06-13
 * Time: 18:02
 * FIXME
 */
public class UpLoadRechargeRecord implements Serializable {
  @Expose private PxVipInfo vip; // 会员编号
  @Expose private PxRechargePlan plan; // 充值方案
  @Expose private Double money; // 充值金额
  @Expose private Double giving; // 赠送金额
  public PxVipInfo getVip() {
    return vip;
  }

  public void setVip(PxVipInfo vip) {
    this.vip = vip;
  }

  public PxRechargePlan getPlan() {
    return plan;
  }

  public void setPlan(PxRechargePlan plan) {
    this.plan = plan;
  }

  public Double getMoney() {
    return money;
  }

  public void setMoney(Double money) {
    this.money = money;
  }

  public Double getGiving() {
    return giving;
  }

  public void setGiving(Double giving) {
    this.giving = giving;
  }
}