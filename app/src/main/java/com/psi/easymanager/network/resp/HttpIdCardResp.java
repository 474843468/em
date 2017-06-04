package com.psi.easymanager.network.resp;

import com.google.gson.annotations.Expose;
import com.psi.easymanager.module.PxVipCardInfo;

/**
 * Created by Administrator on 2017-01-09.
 */
public class HttpIdCardResp extends HttpResp{
  @Expose
  private PxVipCardInfo cardInfo;
  @Expose
  private String type = "0";// 0:本店办卡 1：连锁店办卡

  public PxVipCardInfo getCardInfo() {
    return cardInfo;
  }

  public void setCardInfo(PxVipCardInfo cardInfo) {
    this.cardInfo = cardInfo;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
