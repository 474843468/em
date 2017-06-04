package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxVipInfo;
import java.io.Serializable;

/**
 * Created by wangzhen on 2016-10-21.
 * 会员支付 会员登录获取会员余额等信息 resp
 */
public class HttpVipPayLoginResp implements Serializable{
    @Expose
    private String msg;
    @Expose
    private int statusCode;
    @Expose
    private PxVipInfo vipInfo;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public PxVipInfo getVipInfo() {
        return vipInfo;
    }

    public void setVipInfo(PxVipInfo vipInfo) {
        this.vipInfo = vipInfo;
    }

}
