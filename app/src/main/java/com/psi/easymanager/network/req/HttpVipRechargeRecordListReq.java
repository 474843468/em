package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxVipInfo;

/**
 * Created by wangzhen on 2016-10-05.
 */
public class HttpVipRechargeRecordListReq extends HttpReq {
    @Expose
    private PxVipInfo vipInfo;

    public PxVipInfo getVipInfo() {
        return vipInfo;
    }

    public void setVipInfo(PxVipInfo vipInfo) {
        this.vipInfo = vipInfo;
    }
}
