package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * Created by wangzhen on 2016-10-27.
 * 会员查询请求
 */
public class HttpVipSearchReq extends HttpReq {
  @Expose
  private String mobile;

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
}
