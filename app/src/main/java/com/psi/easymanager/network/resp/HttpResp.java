package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

public class HttpResp implements Serializable {

  public static final int SUCCESS = 1;
  public static final int FAILURE = 0;
  public static final String EMPTYMESSAGE = "请求数据为空";
  public static final String SUCCESSMESSAGE = "请求成功";
  public static final String ERRORMESSAGE = "服务器内部错误";

  private static final long serialVersionUID = 1L;

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
