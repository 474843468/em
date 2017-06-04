package com.psi.easymanager.network.req;

import com.google.gson.annotations.Expose;

/**
 * Created by wangzhen on 2016-10-20.
 *
 * 会员登录请求
 */
public class HttpVipLoginReq extends HttpReq {
    @Expose
    private String mobile;
    @Expose
    private String companyId;
    @Expose
    private String cardNo;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
