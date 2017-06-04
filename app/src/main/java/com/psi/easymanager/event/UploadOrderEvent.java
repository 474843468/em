package com.psi.easymanager.event;

/**
 * User: ylw
 * Date: 2016-10-13
 * Time: 15:49
 * 上传订单 event
 */
public class UploadOrderEvent {
  private int result = 0;
  private String msg;

  public UploadOrderEvent(String msg) {
    this.msg = msg;
  }

  public int getResult() {
    return result;
  }

  public UploadOrderEvent setResult(int result) {
    this.result = result;
    return this;
  }

  public String getMsg() {
    return msg;
  }
}