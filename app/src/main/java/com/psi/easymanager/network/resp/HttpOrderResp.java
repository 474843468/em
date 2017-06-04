package com.psi.easymanager.network.resp;

import java.util.ArrayList;
import java.util.List;

public class HttpOrderResp extends HttpResp {


  private List<String> successOrderList = new ArrayList<>();

  public List<String> getSuccessOrderList() {
    return successOrderList;
  }

  public void setSuccessOrderList(List<String> successOrderList) {
    this.successOrderList = successOrderList;
  }
}
