package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * 查询 该订单所有在线支付详情
 */
public class HttpNetPayRecordReq extends HttpReq{

	@Expose private String orderNo;
	
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
}
