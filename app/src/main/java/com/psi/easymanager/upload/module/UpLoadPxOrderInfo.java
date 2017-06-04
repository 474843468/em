package com.psi.easymanager.upload.module;

import com.google.gson.annotations.Expose;

import com.psi.easymanager.module.PxTableInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ylw
 * Date: 2016-05-23
 * Time: 11:54
 * 用于上传营业数据的OrderInfo
 */
public class UpLoadPxOrderInfo implements Serializable {
  public static final String TAIL_YES = "0";
  public static final String TAIL_NO = "1";

  @Expose private String code;        // 订单编号 不用处理
  @Expose private PxTableInfo table;        // 桌位编号
  @Expose private Double totalPrice;        // 订单总价
  @Expose private Double accountReceivable;        // 应收款
  @Expose private Double realPrice;        // 实收款
  @Expose private Double discountPrice;        // 优惠金额
  @Expose private String payType;        // 支付方式(0:现金 1:刷卡 2：会员卡 3:其他)
  @Expose private String status;        // 订单状态(0:未结账 1：结账 2:撤单)
  @Expose private String tail;        // 是否抹零（0：是 1：否  2:请选择 ）
  @Expose private Double tailMoney;        // 抹零金额
  @Expose private String useVipCard;        // 是否刷会员卡(0：不刷 1：刷)
  @Expose private Double discountRate;        // 折扣率(0-100)
  @Expose private String free;   // 是否免单(0:否 1:是)
  @Expose private String orderNo; // 订单号
  @Expose private String orderReqNo; // 订单请求交易流水号
  @Expose private String vipNo;//会员手机号
  @Expose private String vipCardPay;// 会员支付金额
  @Expose private Double extraMoney;//附加费
  @Expose private Double complementMoney;//补足金额
  @Expose private Double totalChange;//总找零
  @Expose private String waiterId;//开单服务员ID
  @Expose private long orderEndTime;//订单结束时间|
  @Expose private String isReversed;//是否反结账(0:未反结账 1:反结账)
  @Expose private List<UploadPayInfo> payInfoList;//支付信息
  @Expose private Double payPrivilege;// 支付类优惠
  @Expose private String type = "0"; // 订单类型 (0:默认订单 1：微信点餐(到店) 2：微信点菜(外卖))
  @Expose private String currentUser;// == waiterid
  @Expose private String companyId;//

  public String getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(String currentUser) {
    this.currentUser = currentUser;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Double getPayPrivilege() {
    return payPrivilege;
  }

  public void setPayPrivilege(Double payPrivilege) {
    this.payPrivilege = payPrivilege;
  }

  public Double getTotalChange() {
    return totalChange;
  }

  public void setTotalChange(Double totalChange) {
    this.totalChange = totalChange;
  }

  public List<UploadPayInfo> getPayInfoList() {
    return payInfoList;
  }

  public void setPayInfoList(List<UploadPayInfo> payInfoList) {
    this.payInfoList = payInfoList;
  }

  public String getIsReversed() {
    return isReversed;
  }

  public void setIsReversed(String isReversed) {
    this.isReversed = isReversed;
  }

  public long getOrderEndTime() {
    return orderEndTime;
  }

  public void setOrderEndTime(long orderEndTime) {
    this.orderEndTime = orderEndTime;
  }

  public String getWaiterId() {
    return waiterId;
  }

  public void setWaiterId(String waiterId) {
    this.waiterId = waiterId;
  }

  @Expose private List<UpLoadPxOrderDetails> pxOrderDetailsList = new ArrayList<>();        // 子表列表

  public Double getExtraMoney() {
    return extraMoney;
  }

  public void setExtraMoney(Double extraMoney) {
    this.extraMoney = extraMoney;
  }

  public Double getComplementMoney() {
    return complementMoney;
  }

  public void setComplementMoney(Double complementMoney) {
    this.complementMoney = complementMoney;
  }

  public String getVipNo() {
    return vipNo;
  }

  public void setVipNo(String vipNo) {
    this.vipNo = vipNo;
  }

  public String getVipCardPay() {
    return vipCardPay;
  }

  public void setVipCardPay(String vipCardPay) {
    this.vipCardPay = vipCardPay;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getOrderReqNo() {
    return orderReqNo;
  }

  public void setOrderReqNo(String orderReqNo) {
    this.orderReqNo = orderReqNo;
  }

  public String getFree() {
    return free;
  }

  public void setFree(String free) {
    this.free = free;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public PxTableInfo getTable() {
    return table;
  }

  public void setTable(PxTableInfo table) {
    this.table = table;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public Double getAccountReceivable() {
    return accountReceivable;
  }

  public void setAccountReceivable(Double accountReceivable) {
    this.accountReceivable = accountReceivable;
  }

  public Double getRealPrice() {
    return realPrice;
  }

  public void setRealPrice(Double realPrice) {
    this.realPrice = realPrice;
  }

  public Double getDiscountPrice() {
    return discountPrice;
  }

  public void setDiscountPrice(Double discountPrice) {
    this.discountPrice = discountPrice;
  }

  public String getPayType() {
    return payType;
  }

  public void setPayType(String payType) {
    this.payType = payType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTail() {
    return tail;
  }

  public void setTail(String tail) {
    this.tail = tail;
  }

  public Double getTailMoney() {
    return tailMoney;
  }

  public void setTailMoney(Double tailMoney) {
    this.tailMoney = tailMoney;
  }

  public String getUseVipCard() {
    return useVipCard;
  }

  public void setUseVipCard(String useVipCard) {
    this.useVipCard = useVipCard;
  }

  public Double getDiscountRate() {
    return discountRate;
  }

  public void setDiscountRate(Double discountRate) {
    this.discountRate = discountRate;
  }

  public List<UpLoadPxOrderDetails> getPxOrderDetailsList() {
    return pxOrderDetailsList;
  }

  public void setPxOrderDetailsList(List<UpLoadPxOrderDetails> pxOrderDetailsList) {
    this.pxOrderDetailsList = pxOrderDetailsList;
  }
}