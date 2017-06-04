package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.upload.module.UpLoadVipInfo;
import java.util.List;

public class HttpVipInfoReq extends HttpReq {
  @Expose private List<UpLoadVipInfo> vipList;

  public List<UpLoadVipInfo> getVipList() {
    return vipList;
  }

  public void setVipList(List<UpLoadVipInfo> vipList) {
    this.vipList = vipList;
  }
}
