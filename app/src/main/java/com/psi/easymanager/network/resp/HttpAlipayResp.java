package com.psi.easymanager.network.resp;

public class HttpAlipayResp extends HttpResp {
  private String status; // 支付状态(0:失败  1:成功)
  public static String SUCCESS = "1";
  public static String FAIL = "0";
  private String outTradeNo; // 订单号
  private String amount;    //订单金额

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getOutTradeNo() {
    return outTradeNo;
  }

  public void setOutTradeNo(String outTradeNo) {
    this.outTradeNo = outTradeNo;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
