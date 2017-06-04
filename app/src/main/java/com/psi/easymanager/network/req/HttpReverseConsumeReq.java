package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * 会员和会员卡消费记录冲正
 */
public class HttpReverseConsumeReq extends HttpReq<HttpReverseConsumeReq>{


	@Expose private String tradeNo;//交易流水号
	@Expose private String vipId; //会员编号
	@Expose private String orderNo; // 订单号
	@Expose private String selfTradeNo; //收银机产生的交易流水号

	public String getSelfTradeNo() {
		return selfTradeNo;
	}

	public void setSelfTradeNo(String selfTradeNo) {
		this.selfTradeNo = selfTradeNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getVipId() {
		return vipId;
	}

	public void setVipId(String vipId) {
		this.vipId = vipId;
	}

}
