package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.upload.module.UpLoadVipInfo;
import java.util.ArrayList;
import java.util.List;

public class HttpVipInfoResp extends HttpResp {

  @Expose private List<UpLoadVipInfo> list = new ArrayList<UpLoadVipInfo>();

  public List<UpLoadVipInfo> getList() {
    return list;
  }

  public void setList(List<UpLoadVipInfo> list) {
    this.list = list;
  }
}
