package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;

/**
 * Created by Administrator on 2017-02-08.
 */
public class HttpVipCardRechargeResp extends HttpResp{
  @Expose
  private Integer score;

  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }
}
