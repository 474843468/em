package com.psi.easymanager.event;


import com.psi.easymanager.module.PxVipInfo;

/**
 * Created by psi on 2016/5/18.
 * MemberFragment发送给MemberDetailFragment
 */
public class MemberDetailEvent {

    private PxVipInfo mVipInfo;

    public MemberDetailEvent(PxVipInfo mRechargeRecord) {
        this.mVipInfo = mRechargeRecord;
    }

    public PxVipInfo getVipInfo() {
        return mVipInfo;
    }

    public void setVipInfo(PxVipInfo vipInfo) {
        mVipInfo = vipInfo;
    }
}
