package com.psi.easymanager.event;

import com.psi.easymanager.module.PxRechargeRecord;
import com.psi.easymanager.module.PxVipInfo;

/**
 * Created by wangzhen on 2016/10/22.
 * AddMemberFragment发送给MemberFragment
 */
public class AddMemberEvent {

  /**
   * 新添加会员的信息
   */
  private PxVipInfo vipInfo;

  public AddMemberEvent(PxVipInfo vipInfo) {
    this.vipInfo = vipInfo;
  }

  public PxVipInfo getVipInfo() {
    return vipInfo;
  }

  public void setVipInfo(PxVipInfo vipInfo) {
    this.vipInfo = vipInfo;
  }
}
