package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxOrderInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhen on 2016-10-11.
 */
public class HttpVipConsumeResp implements Serializable{
    @Expose
    private String msg;
    @Expose
    private int statusCode;
    @Expose
    private List<PxOrderInfo> list = new ArrayList<>();

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

    public List<PxOrderInfo> getList() {
        return list;
    }

    public void setList(List<PxOrderInfo> list) {
        this.list = list;
    }
}
