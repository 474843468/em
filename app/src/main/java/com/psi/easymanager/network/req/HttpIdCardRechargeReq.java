package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * 会员卡充值
 * Created by Administrator on 2017-01-17.
 */
public class HttpIdCardRechargeReq extends HttpReq{
@Expose
  private String cardId;
  @Expose
  private Double money; //充值金额

  public String getCardId() {
    return cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public Double getMoney() {
    return money;
  }

  public void setMoney(Double money) {
    this.money = money;
  }
}
