package com.psi.easymanager.network.req;

/**
 * Created by Administrator on 2016-12-16.
 */
public class HttpUploadErrorResp{
    private String statusCode; // 支付状态(0:失败  1:成功)
    public static String SUCCESS = "1";
    public static String FAIL = "0";

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
