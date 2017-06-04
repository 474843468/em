package com.psi.easymanager.chat.resp;

import java.io.Serializable;

/**
 * Created by zjq on 2016/5/7.
 * 加菜收银端回执
 */
public class SimpleResp implements Serializable {
  public static final int SUCCESS = 1;
  public static final int FAILURE = 2;
  private int result;
  private String des;

  public int getResult() {
    return result;
  }

  public void setResult(int result) {
    this.result = result;
  }

  public String getDes() {
    return des;
  }

  public void setDes(String des) {
    this.des = des;
  }
}
