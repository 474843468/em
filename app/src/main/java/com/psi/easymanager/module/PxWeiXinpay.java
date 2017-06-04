package com.psi.easymanager.module;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END

/**
 * 微信支付相关参数
 */
public class PxWeiXinpay implements java.io.Serializable {

    private Long id;
    /**
     * 对应服务器id
     */
     @SerializedName("id") @Expose
    private String objectId;
    /**
     * 微信key
     */
     @Expose
    private String key;
    /**
     * 商户APPID
     */
     @Expose
    private String appId;
    /**
     * 商户号
     */
     @Expose
    private String macId;
    /**
     * 虚拟删除 0：正常 1：删除 2：审核
     */
     @Expose
    private String delFlag;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public PxWeiXinpay() {
    }

    public PxWeiXinpay(Long id) {
        this.id = id;
    }

    public PxWeiXinpay(Long id, String objectId, String key, String appId, String macId, String delFlag) {
        this.id = id;
        this.objectId = objectId;
        this.key = key;
        this.appId = appId;
        this.macId = macId;
        this.delFlag = delFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
