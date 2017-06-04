package com.psi.easymanager.module;

/**
 * Created by psi on 2016/8/2.
 * 仅用于交接班账单汇总(打印)
 */
public class AppShiftBillCollect {

    //收银汇总
    private int cashCollectNumCash;//现金单数
    private int cashCollectNumPos;// 银行卡单数
    private int cashCollectNumVip;//会员卡单数
    private int cashCollectNumWing;//翼支付单数
    private int cashCollectNumAli;//支付宝单数
    private int cashCollectNumWx;//微信单数
    private int cashCollectNumAll;//收银汇总总计单数
    private Double cashCollectAmountCash;//现金金额
    private Double cashCollectAmountPos;//银行卡金额
    private Double cashCollectAmountVip;//会员金额
    private Double cashCollectAmountWing;//翼支付金额
    private Double cashCollectAmountAli;//支付宝金额
    private Double cashCollectAmountWx;//微信金额
    private Double cashCollectAmountAll;//收银汇总总计金额

    //消费统计
    private int ConsumeStaticsNumber;//单数
    private int ConsumeStaticsPeopleNumber;//人数
    private Double ConsumeStaticsTotal;//总价
    private Double ConsumeStaticsReceivable;//应收金额
    private Double ConsumeStaticsPrivilege;//优惠金额
    private Double ConsumeStaticsTail;//抹零金额
    private Double ConsumeStaticsReceived;//实收金额
    private Double ConsumeStaticsChange;//找零金额
    private Double ConsumeStaticsPerPeople;//人均消费

    //收银统计
    private int cashStatisticsNumCash;//现金单数
    private int cashStatisticsNumPos;// 银行卡单数
    private int cashStatisticsNumVip;//会员卡单数
    private int cashStatisticsNumWing;//翼支付单数
    private int cashStatisticsNumAli;//支付宝单数
    private int cashStatisticsNumWx;//微信单数
    private int cashStatisticsNumAll;//收银统计小计单数
    private Double cashStatisticsAmountCash;//现金金额
    private Double cashStatisticsAmountPos;//银行卡金额
    private Double cashStatisticsAmountVip;//会员金额
    private Double cashStatisticsAmountWing;//翼支付金额
    private Double cashStatisticsAmountAli;//支付宝金额
    private Double cashStatisticsAmountWx;//微信金额
    private Double cashStatisticsAmountAll;//收银统计小计金额

    //会员充值
    private int vipRechargeNumber;//充值笔数
    private Double vipRechargeReceived;//实收金额
    private Double vipRechargeGift;//赠送金额
    private Double vipRechargeRecharge;//充值金额

    //会员充值收银
    private int vipRechargeCashNumber;//现金笔数
    private int vipRechargeAllNumber;//所有笔数
    private Double vipRechargeCashAmount;//现金金额
    private Double vipRechargeAllAmount;//所有金额

    public Double getConsumeStaticsTotal() {
        return ConsumeStaticsTotal;
    }

    public void setConsumeStaticsTotal(Double consumeStaticsTotal) {
        ConsumeStaticsTotal = consumeStaticsTotal;
    }

    public int getCashCollectNumCash() {
        return cashCollectNumCash;
    }

    public void setCashCollectNumCash(int cashCollectNumCash) {
        this.cashCollectNumCash = cashCollectNumCash;
    }

    public int getCashCollectNumPos() {
        return cashCollectNumPos;
    }

    public void setCashCollectNumPos(int cashCollectNumPos) {
        this.cashCollectNumPos = cashCollectNumPos;
    }

    public int getCashCollectNumVip() {
        return cashCollectNumVip;
    }

    public void setCashCollectNumVip(int cashCollectNumVip) {
        this.cashCollectNumVip = cashCollectNumVip;
    }

    public int getCashCollectNumWing() {
        return cashCollectNumWing;
    }

    public void setCashCollectNumWing(int cashCollectNumWing) {
        this.cashCollectNumWing = cashCollectNumWing;
    }

    public int getCashCollectNumAli() {
        return cashCollectNumAli;
    }

    public void setCashCollectNumAli(int cashCollectNumAli) {
        this.cashCollectNumAli = cashCollectNumAli;
    }

    public int getCashCollectNumWx() {
        return cashCollectNumWx;
    }

    public void setCashCollectNumWx(int cashCollectNumWx) {
        this.cashCollectNumWx = cashCollectNumWx;
    }

    public int getCashCollectNumAll() {
        return cashCollectNumAll;
    }

    public void setCashCollectNumAll(int cashCollectNumAll) {
        this.cashCollectNumAll = cashCollectNumAll;
    }

    public Double getCashCollectAmountCash() {
        return cashCollectAmountCash;
    }

    public void setCashCollectAmountCash(Double cashCollectAmountCash) {
        this.cashCollectAmountCash = cashCollectAmountCash;
    }

    public Double getCashCollectAmountPos() {
        return cashCollectAmountPos;
    }

    public void setCashCollectAmountPos(Double cashCollectAmountPos) {
        this.cashCollectAmountPos = cashCollectAmountPos;
    }

    public Double getCashCollectAmountVip() {
        return cashCollectAmountVip;
    }

    public void setCashCollectAmountVip(Double cashCollectAmountVip) {
        this.cashCollectAmountVip = cashCollectAmountVip;
    }

    public Double getCashCollectAmountWing() {
        return cashCollectAmountWing;
    }

    public void setCashCollectAmountWing(Double cashCollectAmountWing) {
        this.cashCollectAmountWing = cashCollectAmountWing;
    }

    public Double getCashCollectAmountAli() {
        return cashCollectAmountAli;
    }

    public void setCashCollectAmountAli(Double cashCollectAmountAli) {
        this.cashCollectAmountAli = cashCollectAmountAli;
    }

    public Double getCashCollectAmountWx() {
        return cashCollectAmountWx;
    }

    public void setCashCollectAmountWx(Double cashCollectAmountWx) {
        this.cashCollectAmountWx = cashCollectAmountWx;
    }

    public Double getCashCollectAmountAll() {
        return cashCollectAmountAll;
    }

    public void setCashCollectAmountAll(Double cashCollectAmountAll) {
        this.cashCollectAmountAll = cashCollectAmountAll;
    }

    public int getConsumeStaticsNumber() {
        return ConsumeStaticsNumber;
    }

    public void setConsumeStaticsNumber(int consumeStaticsNumber) {
        ConsumeStaticsNumber = consumeStaticsNumber;
    }

    public int getConsumeStaticsPeopleNumber() {
        return ConsumeStaticsPeopleNumber;
    }

    public void setConsumeStaticsPeopleNumber(int consumeStaticsPeopleNumber) {
        ConsumeStaticsPeopleNumber = consumeStaticsPeopleNumber;
    }

    public Double getConsumeStaticsReceivable() {
        return ConsumeStaticsReceivable;
    }

    public void setConsumeStaticsReceivable(Double consumeStaticsReceivable) {
        ConsumeStaticsReceivable = consumeStaticsReceivable;
    }

    public Double getConsumeStaticsPrivilege() {
        return ConsumeStaticsPrivilege;
    }

    public void setConsumeStaticsPrivilege(Double consumeStaticsPrivilege) {
        ConsumeStaticsPrivilege = consumeStaticsPrivilege;
    }

    public Double getConsumeStaticsTail() {
        return ConsumeStaticsTail;
    }

    public void setConsumeStaticsTail(Double consumeStaticsTail) {
        ConsumeStaticsTail = consumeStaticsTail;
    }

    public Double getConsumeStaticsReceived() {
        return ConsumeStaticsReceived;
    }

    public void setConsumeStaticsReceived(Double consumeStaticsReceived) {
        ConsumeStaticsReceived = consumeStaticsReceived;
    }

    public Double getConsumeStaticsPerPeople() {
        return ConsumeStaticsPerPeople;
    }

    public void setConsumeStaticsPerPeople(Double consumeStaticsPerPeople) {
        ConsumeStaticsPerPeople = consumeStaticsPerPeople;
    }

    public int getCashStatisticsNumCash() {
        return cashStatisticsNumCash;
    }

    public void setCashStatisticsNumCash(int cashStatisticsNumCash) {
        this.cashStatisticsNumCash = cashStatisticsNumCash;
    }

    public int getCashStatisticsNumPos() {
        return cashStatisticsNumPos;
    }

    public void setCashStatisticsNumPos(int cashStatisticsNumPos) {
        this.cashStatisticsNumPos = cashStatisticsNumPos;
    }

    public int getCashStatisticsNumVip() {
        return cashStatisticsNumVip;
    }

    public void setCashStatisticsNumVip(int cashStatisticsNumVip) {
        this.cashStatisticsNumVip = cashStatisticsNumVip;
    }

    public int getCashStatisticsNumWing() {
        return cashStatisticsNumWing;
    }

    public void setCashStatisticsNumWing(int cashStatisticsNumWing) {
        this.cashStatisticsNumWing = cashStatisticsNumWing;
    }

    public int getCashStatisticsNumAli() {
        return cashStatisticsNumAli;
    }

    public void setCashStatisticsNumAli(int cashStatisticsNumAli) {
        this.cashStatisticsNumAli = cashStatisticsNumAli;
    }

    public int getCashStatisticsNumWx() {
        return cashStatisticsNumWx;
    }

    public void setCashStatisticsNumWx(int cashStatisticsNumWx) {
        this.cashStatisticsNumWx = cashStatisticsNumWx;
    }

    public int getCashStatisticsNumAll() {
        return cashStatisticsNumAll;
    }

    public void setCashStatisticsNumAll(int cashStatisticsNumAll) {
        this.cashStatisticsNumAll = cashStatisticsNumAll;
    }

    public Double getCashStatisticsAmountCash() {
        return cashStatisticsAmountCash;
    }

    public void setCashStatisticsAmountCash(Double cashStatisticsAmountCash) {
        this.cashStatisticsAmountCash = cashStatisticsAmountCash;
    }

    public Double getCashStatisticsAmountPos() {
        return cashStatisticsAmountPos;
    }

    public void setCashStatisticsAmountPos(Double cashStatisticsAmountPos) {
        this.cashStatisticsAmountPos = cashStatisticsAmountPos;
    }

    public Double getCashStatisticsAmountVip() {
        return cashStatisticsAmountVip;
    }

    public void setCashStatisticsAmountVip(Double cashStatisticsAmountVip) {
        this.cashStatisticsAmountVip = cashStatisticsAmountVip;
    }

    public Double getCashStatisticsAmountWing() {
        return cashStatisticsAmountWing;
    }

    public void setCashStatisticsAmountWing(Double cashStatisticsAmountWing) {
        this.cashStatisticsAmountWing = cashStatisticsAmountWing;
    }

    public Double getCashStatisticsAmountAli() {
        return cashStatisticsAmountAli;
    }

    public void setCashStatisticsAmountAli(Double cashStatisticsAmountAli) {
        this.cashStatisticsAmountAli = cashStatisticsAmountAli;
    }

    public Double getCashStatisticsAmountWx() {
        return cashStatisticsAmountWx;
    }

    public void setCashStatisticsAmountWx(Double cashStatisticsAmountWx) {
        this.cashStatisticsAmountWx = cashStatisticsAmountWx;
    }

    public Double getCashStatisticsAmountAll() {
        return cashStatisticsAmountAll;
    }

    public void setCashStatisticsAmountAll(Double cashStatisticsAmountAll) {
        this.cashStatisticsAmountAll = cashStatisticsAmountAll;
    }

    public int getVipRechargeNumber() {
        return vipRechargeNumber;
    }

    public void setVipRechargeNumber(int vipRechargeNumber) {
        this.vipRechargeNumber = vipRechargeNumber;
    }

    public Double getVipRechargeReceived() {
        return vipRechargeReceived;
    }

    public void setVipRechargeReceived(Double vipRechargeReceived) {
        this.vipRechargeReceived = vipRechargeReceived;
    }

    public Double getVipRechargeGift() {
        return vipRechargeGift;
    }

    public void setVipRechargeGift(Double vipRechargeGift) {
        this.vipRechargeGift = vipRechargeGift;
    }

    public Double getVipRechargeRecharge() {
        return vipRechargeRecharge;
    }

    public void setVipRechargeRecharge(Double vipRechargeRecharge) {
        this.vipRechargeRecharge = vipRechargeRecharge;
    }

    public int getVipRechargeCashNumber() {
        return vipRechargeCashNumber;
    }

    public void setVipRechargeCashNumber(int vipRechargeCashNumber) {
        this.vipRechargeCashNumber = vipRechargeCashNumber;
    }

    public int getVipRechargeAllNumber() {
        return vipRechargeAllNumber;
    }

    public void setVipRechargeAllNumber(int vipRechargeAllNumber) {
        this.vipRechargeAllNumber = vipRechargeAllNumber;
    }

    public Double getVipRechargeCashAmount() {
        return vipRechargeCashAmount;
    }

    public void setVipRechargeCashAmount(Double vipRechargeCashAmount) {
        this.vipRechargeCashAmount = vipRechargeCashAmount;
    }

    public Double getVipRechargeAllAmount() {
        return vipRechargeAllAmount;
    }

    public void setVipRechargeAllAmount(Double vipRechargeAllAmount) {
        this.vipRechargeAllAmount = vipRechargeAllAmount;
    }

    public Double getConsumeStaticsChange() {
        return ConsumeStaticsChange;
    }

    public void setConsumeStaticsChange(Double consumeStaticsChange) {
        ConsumeStaticsChange = consumeStaticsChange;
    }
}
