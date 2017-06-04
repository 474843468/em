package com.psi.easymanager.event;

import com.psi.easymanager.module.PxVipCardInfo;
import com.psi.easymanager.module.PxVipInfo;

/**
 * Created by wangzhen on 2016-09-29.
 *
 * 由 MemberCenter 发送给 VipOperationFragment
 */
public class VipInfoChargeAndChargeAgainEvent {

    private PxVipInfo mVipInfo;
    private PxVipCardInfo mVipCardInfo;
    private int position;

    public VipInfoChargeAndChargeAgainEvent(int position,PxVipInfo mVipInfo) {
        this.position = position;
        this.mVipInfo = mVipInfo;
    }
  public VipInfoChargeAndChargeAgainEvent(PxVipInfo mVipInfo) {
        this.mVipInfo = mVipInfo;
    }
  public VipInfoChargeAndChargeAgainEvent(PxVipCardInfo mVipCardInfo) {
        this.mVipCardInfo = mVipCardInfo;
    }

    public PxVipInfo getVipInfo() {
        return mVipInfo;
    }

    public void setVipInfo(PxVipInfo vipInfo) {
        mVipInfo = vipInfo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
