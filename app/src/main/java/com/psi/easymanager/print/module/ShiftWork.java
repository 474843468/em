package com.psi.easymanager.print.module;

import com.psi.easymanager.module.AppCashCollect;
import com.psi.easymanager.module.AppShiftCateInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: ylw
 * Date: 2016-07-29
 * Time: 09:53
 * 交接班
 */
public class ShiftWork {

  private String shiftUserName;// 交班用户
  private String cashierName;//收银用户名
  private Date shitTime = new Date();//交接时间
  private Date startTime = null;//开始时间 用于时间段
  private Date endTime = new Date();//结束时间 用于时间段
  private String workZone = " "; //区域
  private String businessData;//营业日期
  // 1.消费统计
  private int billsCount;//单数
  private int peopleNum;//人数
  private Double consumeAverage;//人均消费
  private Double acceptAmount;//应收金额
  private Double discountAmount;//优惠金额
  private Double gainLoseAmount;//损益金额
  private Double actualAmount;//实收金额
  private Double changeAmount;//找零金额
  private Double totalPrice;//总价
  private Double StaticsExclusive;//不计入统计金额
  private Double payPrivilege;//支付类优惠
  //2.  收银统计
  private int bankCardCount;//银行卡单数
  private int wingPayCount;//翼支付单数
  private int vipCount;//会员卡单数
  private int aliPayCount;//支付宝单数
  private int weiXinCount;//微信单数
  private int cashCount;//现金数量
  private int subTotalCount;//小计数量
  private Double cashMoney;//现金金额
  private Double bankCardMoney;//银行卡金额
  private Double wingPayMoney;//翼支付金额
  private Double vipMoney;//会员金额
  private Double aliPayMoney;//支付宝金额
  private Double weixinMoney;//微信金额
  private Double subTotalMoney;//小计金额
  //3.会员充值情况
  private int rechargePeopleNum;//充值人数
  private Double rechargeMoney;//充值金额
  private Double presentMoney;//赠送金额
  private Double actualMoney;//会员充值情况   实收款数
  //4.会员充值收银
  private int vipCashRechargeCount;//现金充值次数
  private Double vipCashRechargeMoney;//现金充值总额
  private Double vipRechargeAmountTotal;//会员充值总计金额
  private int vipRechargeCountTotal;//总计笔数
  //6.收银汇总
  private int bankCardCountCashCollect;// 银行卡单数
  private int wingPayCountCashCollect;//翼支付单数
  private int vipCountCashCollect;//会员卡单数
  private int aliPayCountCashCollect;//支付宝单数
  private int cashCountCashCollect;//现金单数
  private int weixinCountCollect;//微信单数
  private int subTotalCountCollect;//
  private Double weixinMoneyCollect;//微信金额
  private Double bankCardMoneyCashCollect;//银行卡金额
  private Double wingPayMoneyCashCollect;//翼支付金额
  private Double vipMoneyCashCollect;//会员金额
  private Double aliPayMoneyCashCollect;//支付宝金额
  private Double cashMoneyCashCollect;//现金
  private Double subTotalMoneyCashCollect;//总计

  // 7.-------------
  private int cashCountVipRecharge;//现金充值次数
  private Double cashVipRechargeMoney;//现金充值金额
  private Double subTotalVipRecharge;//小计
  private Double totalVipRecharge;//总计
  //8.分类统计
  private List<AppShiftCateInfo> categoryCollectList;// 分类List
  private int categoryCollectTotal = 0;//数量总数
  private Double categoryCollectReceivableMoneyTotal = 0.0;//应收总额
  private Double categoryCollectRealMoneyTotal = 0.0;//实收总额
  //-- 收银员汇总
  private int cashCashierCollectNum;// 现金汇总数
  private int bankCashierCollectNum;//银行卡
  private int alipayCashierCollectNum;//支付宝
  private int weixinCashierCollectNum;//微信
  private int wingPayCashierCollectNum;//翼支付
  private int vipCashierCollectNum;//会员
  private Double cashCashierCollectMoney;//money
  private Double bankCashierCollectMoney;//
  private Double alipayCashierCollectMoney;//
  private Double weixinCashierCollectMoney;//
  private Double wingPayCashierCollectMoney;//
  private Double vipCashierCollectMoney;//
  private Double cashierCollectTotalMoney;//小计money


  private String title;//标题 用于交接班按钮打印

  //收银汇总 新改
  private List<AppCashCollect> mCashCollectList;

  public List<AppCashCollect> getCashCollectList() {
    return mCashCollectList;
  }

  public void setCashCollectList(List<AppCashCollect> cashCollectList) {
    if (cashCollectList == null) {
      mCashCollectList = new ArrayList<>();
    }
    mCashCollectList = cashCollectList;
  }

  public Double getPayPrivilege() {
    return payPrivilege;
  }

  public void setPayPrivilege(Double payPrivilege) {
    this.payPrivilege = payPrivilege;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBusinessData() {
    return businessData;
  }

  public void setBusinessData(String businessData) {
    this.businessData = businessData;
  }

  public int getVipRechargeCountTotal() {
    return vipRechargeCountTotal;
  }

  public void setVipRechargeCountTotal(int vipRechargeCountTotal) {
    this.vipRechargeCountTotal = vipRechargeCountTotal;
  }

  public Double getCashierCollectTotalMoney() {
    return cashierCollectTotalMoney;
  }

  public void setCashierCollectTotalMoney(Double cashierCollectTotalMoney) {
    this.cashierCollectTotalMoney = cashierCollectTotalMoney;
  }

  public int getCashCashierCollectNum() {
    return cashCashierCollectNum;
  }

  public void setCashCashierCollectNum(int cashCashierCollectNum) {
    this.cashCashierCollectNum = cashCashierCollectNum;
  }

  public int getBankCashierCollectNum() {
    return bankCashierCollectNum;
  }

  public void setBankCashierCollectNum(int bankCashierCollectNum) {
    this.bankCashierCollectNum = bankCashierCollectNum;
  }

  public int getAlipayCashierCollectNum() {
    return alipayCashierCollectNum;
  }

  public void setAlipayCashierCollectNum(int alipayCashierCollectNum) {
    this.alipayCashierCollectNum = alipayCashierCollectNum;
  }

  public int getWeixinCashierCollectNum() {
    return weixinCashierCollectNum;
  }

  public void setWeixinCashierCollectNum(int weixinCashierCollectNum) {
    this.weixinCashierCollectNum = weixinCashierCollectNum;
  }

  public int getWingPayCashierCollectNum() {
    return wingPayCashierCollectNum;
  }

  public void setWingPayCashierCollectNum(int wingPayCashierCollectNum) {
    this.wingPayCashierCollectNum = wingPayCashierCollectNum;
  }

  public int getVipCashierCollectNum() {
    return vipCashierCollectNum;
  }

  public void setVipCashierCollectNum(int vipCashierCollectNum) {
    this.vipCashierCollectNum = vipCashierCollectNum;
  }

  public Double getCashCashierCollectMoney() {
    return cashCashierCollectMoney;
  }

  public void setCashCashierCollectMoney(Double cashCashierCollectMoney) {
    this.cashCashierCollectMoney = cashCashierCollectMoney;
  }

  public Double getBankCashierCollectMoney() {
    return bankCashierCollectMoney;
  }

  public void setBankCashierCollectMoney(Double bankCashierCollectMoney) {
    this.bankCashierCollectMoney = bankCashierCollectMoney;
  }

  public Double getAlipayCashierCollectMoney() {
    return alipayCashierCollectMoney;
  }

  public void setAlipayCashierCollectMoney(Double alipayCashierCollectMoney) {
    this.alipayCashierCollectMoney = alipayCashierCollectMoney;
  }

  public Double getWeixinCashierCollectMoney() {
    return weixinCashierCollectMoney;
  }

  public void setWeixinCashierCollectMoney(Double weixinCashierCollectMoney) {
    this.weixinCashierCollectMoney = weixinCashierCollectMoney;
  }

  public Double getWingPayCashierCollectMoney() {
    return wingPayCashierCollectMoney;
  }

  public void setWingPayCashierCollectMoney(Double wingPayCashierCollectMoney) {
    this.wingPayCashierCollectMoney = wingPayCashierCollectMoney;
  }

  public Double getVipCashierCollectMoney() {
    return vipCashierCollectMoney;
  }

  public void setVipCashierCollectMoney(Double vipCashierCollectMoney) {
    this.vipCashierCollectMoney = vipCashierCollectMoney;
  }

  public int getCashCount() {
    return cashCount;
  }

  public void setCashCount(int cashCount) {
    this.cashCount = cashCount;
  }

  public Double getCashMoney() {
    return cashMoney;
  }

  public void setCashMoney(Double cashMoney) {
    this.cashMoney = cashMoney;
  }

  public int getSubTotalCountCollect() {
    return subTotalCountCollect;
  }

  public void setSubTotalCountCollect(int subTotalCountCollect) {
    this.subTotalCountCollect = subTotalCountCollect;
  }

  public int getSubTotalCount() {
    return subTotalCount;
  }

  public void setSubTotalCount(int subTotalCount) {
    this.subTotalCount = subTotalCount;
  }

  public int getWeixinCountCollect() {
    return weixinCountCollect;
  }

  public void setWeixinCountCollect(int weixinCountCollect) {
    this.weixinCountCollect = weixinCountCollect;
  }

  public Double getWeixinMoneyCollect() {
    return weixinMoneyCollect;
  }

  public void setWeixinMoneyCollect(Double weixinMoneyCollect) {
    this.weixinMoneyCollect = weixinMoneyCollect;
  }

  public int getVipCashRechargeCount() {
    return vipCashRechargeCount;
  }

  public void setVipCashRechargeCount(int vipCashRechargeCount) {
    this.vipCashRechargeCount = vipCashRechargeCount;
  }

  public Double getVipCashRechargeMoney() {
    return vipCashRechargeMoney;
  }

  public void setVipCashRechargeMoney(Double vipCashRechargeMoney) {
    this.vipCashRechargeMoney = vipCashRechargeMoney;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getCashierName() {
    return cashierName;
  }

  public void setCashierName(String cashierName) {
    this.cashierName = cashierName;
  }

  public String getShiftUserName() {
    return shiftUserName;
  }

  public void setShiftUserName(String shiftUserName) {
    this.shiftUserName = shiftUserName;
  }

  public int getCategoryCollectTotal() {
    return categoryCollectTotal;
  }

  public Double getCategoryCollectReceivableMoneyTotal() {
    return categoryCollectReceivableMoneyTotal;
  }

  public Double getCategoryCollectRealMoneyTotal() {
    return categoryCollectRealMoneyTotal;
  }

  public List<AppShiftCateInfo> getCategoryCollectList() {
    return categoryCollectList;
  }

  public void setCategoryCollectList(List<AppShiftCateInfo> categoryCollectList) {
    if (categoryCollectList == null) {
      this.categoryCollectList = new ArrayList<>();
    } else {
      this.categoryCollectList = categoryCollectList;
    }
    //计算分类统计
    for (AppShiftCateInfo collect : categoryCollectList) {
      categoryCollectTotal += collect.getCateNumber();
      categoryCollectReceivableMoneyTotal += collect.getReceivableAmount();
      categoryCollectRealMoneyTotal += collect.getActualAmount();
    }
  }

  public Double getTotalVipRecharge() {
    return totalVipRecharge;
  }

  public void setTotalVipRecharge(Double totalVipRecharge) {
    this.totalVipRecharge = totalVipRecharge;
  }

  public int getCashCountVipRecharge() {
    return cashCountVipRecharge;
  }

  public void setCashCountVipRecharge(int cashCountVipRecharge) {
    this.cashCountVipRecharge = cashCountVipRecharge;
  }

  public Double getCashVipRechargeMoney() {
    return cashVipRechargeMoney;
  }

  public void setCashVipRechargeMoney(Double cashVipRechargeMoney) {
    this.cashVipRechargeMoney = cashVipRechargeMoney;
  }

  public Double getSubTotalVipRecharge() {
    return subTotalVipRecharge;
  }

  public void setSubTotalVipRecharge(Double subTotalVipRecharge) {
    this.subTotalVipRecharge = subTotalVipRecharge;
  }

  public int getBankCardCountCashCollect() {
    return bankCardCountCashCollect;
  }

  public void setBankCardCountCashCollect(int bankCardCountCashCollect) {
    this.bankCardCountCashCollect = bankCardCountCashCollect;
  }

  public int getWingPayCountCashCollect() {
    return wingPayCountCashCollect;
  }

  public void setWingPayCountCashCollect(int wingPayCountCashCollect) {
    this.wingPayCountCashCollect = wingPayCountCashCollect;
  }

  public int getVipCountCashCollect() {
    return vipCountCashCollect;
  }

  public void setVipCountCashCollect(int vipCountCashCollect) {
    this.vipCountCashCollect = vipCountCashCollect;
  }

  public int getAliPayCountCashCollect() {
    return aliPayCountCashCollect;
  }

  public void setAliPayCountCashCollect(int aliPayCountCashCollect) {
    this.aliPayCountCashCollect = aliPayCountCashCollect;
  }

  public int getCashCountCashCollect() {
    return cashCountCashCollect;
  }

  public void setCashCountCashCollect(int cashCountCashCollect) {
    this.cashCountCashCollect = cashCountCashCollect;
  }

  public Double getBankCardMoneyCashCollect() {
    return bankCardMoneyCashCollect;
  }

  public void setBankCardMoneyCashCollect(Double bankCardMoneyCashCollect) {
    this.bankCardMoneyCashCollect = bankCardMoneyCashCollect;
  }

  public Double getWingPayMoneyCashCollect() {
    return wingPayMoneyCashCollect;
  }

  public void setWingPayMoneyCashCollect(Double wingPayMoneyCashCollect) {
    this.wingPayMoneyCashCollect = wingPayMoneyCashCollect;
  }

  public Double getVipMoneyCashCollect() {
    return vipMoneyCashCollect;
  }

  public void setVipMoneyCashCollect(Double vipMoneyCashCollect) {
    this.vipMoneyCashCollect = vipMoneyCashCollect;
  }

  public Double getAliPayMoneyCashCollect() {
    return aliPayMoneyCashCollect;
  }

  public void setAliPayMoneyCashCollect(Double aliPayMoneyCashCollect) {
    this.aliPayMoneyCashCollect = aliPayMoneyCashCollect;
  }

  public Double getCashMoneyCashCollect() {
    return cashMoneyCashCollect;
  }

  public void setCashMoneyCashCollect(Double cashMoneyCashCollect) {
    this.cashMoneyCashCollect = cashMoneyCashCollect;
  }

  public Double getSubTotalMoneyCashCollect() {
    return subTotalMoneyCashCollect;
  }

  public void setSubTotalMoneyCashCollect(Double subTotalMoneyCashCollect) {
    this.subTotalMoneyCashCollect = subTotalMoneyCashCollect;
  }

  public Double getVipRechargeAmountTotal() {
    return vipRechargeAmountTotal;
  }

  public void setVipRechargeAmountTotal(Double vipRechargeAmountTotal) {
    this.vipRechargeAmountTotal = vipRechargeAmountTotal;
  }

  public Double getActualMoney() {
    return actualMoney;
  }

  public void setActualMoney(Double actualMoney) {
    this.actualMoney = actualMoney;
  }

  public Double getRechargeMoney() {
    return rechargeMoney;
  }

  public void setRechargeMoney(Double rechargeMoney) {
    this.rechargeMoney = rechargeMoney;
  }

  public Double getPresentMoney() {
    return presentMoney;
  }

  public void setPresentMoney(Double presentMoney) {
    this.presentMoney = presentMoney;
  }

  public int getRechargePeopleNum() {
    return rechargePeopleNum;
  }

  public void setRechargePeopleNum(int rechargePeopleNum) {
    this.rechargePeopleNum = rechargePeopleNum;
  }

  public Double getSubTotalMoney() {
    return subTotalMoney;
  }

  public void setSubTotalMoney(Double subTotalMoney) {
    this.subTotalMoney = subTotalMoney;
  }

  public int getBankCardCount() {
    return bankCardCount;
  }

  public void setBankCardCount(int bankCardCount) {
    this.bankCardCount = bankCardCount;
  }

  public int getWingPayCount() {
    return wingPayCount;
  }

  public void setWingPayCount(int wingPayCount) {
    this.wingPayCount = wingPayCount;
  }

  public int getVipCount() {
    return vipCount;
  }

  public void setVipCount(int vipCount) {
    this.vipCount = vipCount;
  }

  public int getAliPayCount() {
    return aliPayCount;
  }

  public void setAliPayCount(int aliPayCount) {
    this.aliPayCount = aliPayCount;
  }

  public int getWeiXinCount() {
    return weiXinCount;
  }

  public void setWeiXinCount(int weiXinCount) {
    this.weiXinCount = weiXinCount;
  }

  public Double getBankCardMoney() {
    return bankCardMoney;
  }

  public void setBankCardMoney(Double bankCardMoney) {
    this.bankCardMoney = bankCardMoney;
  }

  public Double getWingPayMoney() {
    return wingPayMoney;
  }

  public void setWingPayMoney(Double wingPayMoney) {
    this.wingPayMoney = wingPayMoney;
  }

  public Double getVipMoney() {
    return vipMoney;
  }

  public void setVipMoney(Double vipMoney) {
    this.vipMoney = vipMoney;
  }

  public Double getAliPayMoney() {
    return aliPayMoney;
  }

  public void setAliPayMoney(Double aliPayMoney) {
    this.aliPayMoney = aliPayMoney;
  }

  public Double getWeixinMoney() {
    return weixinMoney;
  }

  public void setWeixinMoney(Double weixinMoney) {
    this.weixinMoney = weixinMoney;
  }

  public Double getActualAmount() {
    return actualAmount;
  }

  public void setActualAmount(Double actualAmount) {
    this.actualAmount = actualAmount;
  }

  public Double getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(Double discountAmount) {
    this.discountAmount = discountAmount;
  }

  public Double getGainLoseAmount() {
    return gainLoseAmount;
  }

  public void setGainLoseAmount(Double gainLoseAmount) {
    this.gainLoseAmount = gainLoseAmount;
  }

  public Double getAcceptAmount() {
    return acceptAmount;
  }

  public void setAcceptAmount(Double acceptAmount) {
    this.acceptAmount = acceptAmount;
  }

  public int getPeopleNum() {
    return peopleNum;
  }

  public void setPeopleNum(int peopleNum) {
    this.peopleNum = peopleNum;
  }

  public Double getConsumeAverage() {
    return consumeAverage;
  }

  public void setConsumeAverage(Double consumeAverage) {
    this.consumeAverage = consumeAverage;
  }

  public int getBillsCount() {
    return billsCount;
  }

  public void setBillsCount(int billsCount) {
    this.billsCount = billsCount;
  }

  public String getWorkZone() {
    return workZone;
  }

  public void setWorkZone(String workZone) {
    this.workZone = workZone;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getShitTime() {
    return shitTime;
  }

  public void setShitTime(Date shitTime) {
    this.shitTime = shitTime;
  }

  public Double getChangeAmount() {
    return changeAmount;
  }

  public void setChangeAmount(Double changeAmount) {
    this.changeAmount = changeAmount;
  }

  public Double getStaticsExclusive() {
    return StaticsExclusive;
  }

  public void setStaticsExclusive(Double staticsExclusive) {
    StaticsExclusive = staticsExclusive;
  }
}