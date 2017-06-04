package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxVipInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhen on 2016-09-28.
 */
public class VipInfoListResult extends HttpResp{

    @Expose
    public List<PxVipInfo> list = new ArrayList<>();

    public List<PxVipInfo> getList() {
        return list;
    }

    public void setList(List<PxVipInfo> list) {
        this.list = list;
    }

}
