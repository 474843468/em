package com.psi.easymanager.print.module;

import java.io.Serializable;

/**
 * Created by psi on 2016/6/2.
 * App客户联打印总价,应收等等
 */
public class AppCustomAmount implements Serializable {
  //总金额
  private double countMoney = 0;
  //应收金额
  private double mReceivableAmount = 0;
  //实收金额
  private double mActualAmount = 0;
  //找零金额
  private double mChangeAmount = 0;
  //消费金额
  private double mConsumeAmount = 0;
  //优惠金额
  private double mDiscAmount = 0;
  //附加费
  private double mSurchargeAmount = 0;

  public AppCustomAmount() {
  }

  public double getCountMoney() {
    return countMoney;
  }

  public void setCountMoney(double countMoney) {
    this.countMoney = countMoney;
  }

  public double getReceivableAmount() {
    return mReceivableAmount;
  }

  public void setReceivableAmount(double mReceivableAmount) {
    this.mReceivableAmount = mReceivableAmount;
  }

  public double getActualAmount() {
    return mActualAmount;
  }

  public void setActualAmount(double mActualAmount) {
    this.mActualAmount = mActualAmount;
  }

  public double getChangeAmount() {
    return mChangeAmount;
  }

  public void setChangeAmount(double mChangeAmount) {
    this.mChangeAmount = mChangeAmount;
  }

  public double getConsumeAmount() {
    return mConsumeAmount;
  }

  public void setConsumeAmount(double mConsumeAmount) {
    this.mConsumeAmount = mConsumeAmount;
  }

  public double getDiscAmount() {
    return mDiscAmount;
  }

  public void setDiscAmount(double mDiscAmount) {
    this.mDiscAmount = mDiscAmount;
  }

  public double getSurchargeAmount() {
    return mSurchargeAmount;
  }

  public void setSurchargeAmount(double mSurchargeAmount) {
    this.mSurchargeAmount = mSurchargeAmount;
  }

  /**
   * 设置信息
   */
  public void setInfoList(double totalAmount, double receivableAmount, double totalReceived, double totalChange, double consumeAmount, double discAmount, double extraAmount) {
    //总金额
    this.setCountMoney(totalAmount);
    //应收金额
    this.setReceivableAmount(receivableAmount);
    //实收金额
    this.setActualAmount(totalReceived);
    //找零金额
    this.setChangeAmount(totalChange);
    //消费金额
    this.setConsumeAmount(consumeAmount);
    //优惠金额
    this.setDiscAmount(discAmount);
    //附加费
    this.setSurchargeAmount(extraAmount);
  }
}
