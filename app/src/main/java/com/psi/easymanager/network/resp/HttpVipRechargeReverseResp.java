package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

/**
 * Created by Administrator on 2016-10-08.
 */
public class HttpVipRechargeReverseResp implements Serializable{
    @Expose
    private String msg;
    @Expose
    private int statusCode;

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
}
