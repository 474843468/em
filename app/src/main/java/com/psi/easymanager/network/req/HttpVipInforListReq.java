package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * Created by wangzhen on 2016-09-28.
 */
public class HttpVipInforListReq extends HttpReq{

  @Expose
  private String mobile; //手机号

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
}
