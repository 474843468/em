package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * 获取id卡充值记录idcard/rechargeRecordIdCardList
 * Created by Administrator on 2017-02-07.
 */
public class HttpSingleVipCardReq extends HttpReq{
  @Expose
  private String cardId;	// 会员卡内部卡号
  @Expose
  private String isBoss; 		// 是否是老板端 0:是 1:否

  public String getCardId() {
    return cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public String getIsBoss() {
    return isBoss;
  }

  public void setIsBoss(String isBoss) {
    this.isBoss = isBoss;
  }
}
