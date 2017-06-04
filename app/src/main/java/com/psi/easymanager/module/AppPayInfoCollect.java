package com.psi.easymanager.module;

/**
 * Created by dorado on 2016/10/21.
 * App使用,支付详情汇总,CheckOutFragment页面使用
 */
public class AppPayInfoCollect {
  //支付方式名称
  private String mName;
  //实收
  private double mReceived;
  //找零
  private double mChange;
  //支付类优惠
  private double mPayPrivilege;


  public String getName() {
    return mName;
  }

  public void setName(String name) {
    mName = name;
  }

  public double getReceived() {
    return mReceived;
  }

  public void setReceived(double received) {
    mReceived = received;
  }

  public double getChange() {
    return mChange;
  }

  public void setChange(double change) {
    mChange = change;
  }

  public double getPayPrivilege() {
    return mPayPrivilege;
  }

  public void setPayPrivilege(double payPrivilege) {
    mPayPrivilege = payPrivilege;
  }
}
