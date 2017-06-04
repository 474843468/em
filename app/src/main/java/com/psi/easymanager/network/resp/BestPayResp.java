package com.psi.easymanager.network.resp;

import com.psi.easymanager.pay.bestpay.BestPaySuccessResult;

public class BestPayResp extends HttpResp {

	private static final long serialVersionUID = 753724409466600474L;

	private boolean success;// 是否成功 true：成功，false：失败(代表商户的请求成功与失败，不代表订单的支付状态)
	private String errorCode;// 错误码
	private String errorMsg;// 错误描述
	private BestPaySuccessResult result;// （说明当 success为true时，result为下单详情，success为false时,result为null）

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public BestPaySuccessResult getResult() {
		return result;
	}

	public void setResult(BestPaySuccessResult result) {
		this.result = result;
	}

}
