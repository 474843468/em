package com.psi.easymanager.event;

import com.psi.easymanager.module.PxVipInfo;

/**
 * User: ylw
 * Date: 2016-08-31
 * Time: 09:34
 * 更新会员信息 通知
 */
public class UpdateVipInfoEvent {
  private PxVipInfo vipInfo;

  public UpdateVipInfoEvent(PxVipInfo vipInfo) {
    this.vipInfo = vipInfo;
  }

  public PxVipInfo getVipInfo() {
    return vipInfo;
  }

  public void setVipInfo(PxVipInfo vipInfo) {
    this.vipInfo = vipInfo;
  }
}