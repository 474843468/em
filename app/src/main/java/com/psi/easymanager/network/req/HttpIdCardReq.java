package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * Created by Administrator on 2017-01-09.
 */
//ID卡登录接口
public class HttpIdCardReq extends HttpReq{
  @Expose
  private String idCardNum;

  public String getIdCardNum() {
    return idCardNum;
  }

  public void setIdCardNum(String idCardNum) {
    this.idCardNum = idCardNum;
  }
}