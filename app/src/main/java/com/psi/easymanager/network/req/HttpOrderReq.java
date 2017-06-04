package com.psi.easymanager.network.req;


import com.google.gson.annotations.Expose;
import com.psi.easymanager.upload.module.UpLoadPxOrderInfo;
import java.util.ArrayList;
import java.util.List;


public class HttpOrderReq extends HttpReq {

    @Expose
    private List<UpLoadPxOrderInfo> list = new ArrayList<>();


    public List<UpLoadPxOrderInfo> getList() {
        return list;
    }

    public void setList(List<UpLoadPxOrderInfo> list) {
        this.list = list;
    }

}
