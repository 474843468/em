package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

import com.psi.easymanager.module.PxRechargeRecord;


/**
 * Created by wangzhen on 2016-10-08.
 */
public class HttpVipRechargeReverseReq extends HttpReq {
    @Expose
    private PxRechargeRecord record;

    public PxRechargeRecord getmRechargeRecord() {
        return record;
    }

    public void setmRechargeRecord(PxRechargeRecord mRechargeRecord) {
        this.record = mRechargeRecord;
    }
}
