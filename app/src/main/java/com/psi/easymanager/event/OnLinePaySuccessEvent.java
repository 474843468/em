package com.psi.easymanager.event;

/**
 * Created by psi on 2016/8/11.
 * WxPayActivity发CheckOutFragment接（客户付款成功后清空该订单信息并保存数据库）
 * AliPayActivity发CheckOutFragment接（客户付款成功后清空该订单信息并保存数据库）
 * 在线支付成功事件
 */
public class OnLinePaySuccessEvent {
  private int type;
  public static final int WX_PAY = 2;
  public static final int ALI_PAY = 3;
  public static final int BEST_PAY = 4;
  private String tradeNo;//在线支付流水号
  private double payMoney;//

  public double getPayMoney() {
    return payMoney;
  }

  public void setPayMoney(double payMoney) {
    this.payMoney = payMoney;
  }

  public String getTradeNo() {
    return tradeNo;
  }

  public void setTradeNo(String tradeNo) {
    this.tradeNo = tradeNo;
  }

  public OnLinePaySuccessEvent(int type, String tradeNo, double payMoney) {
    this.type = type;
    this.tradeNo = tradeNo;
    this.payMoney = payMoney;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
