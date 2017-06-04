package com.psi.easymanager.module;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END

/**
 * 支付宝商户信息
 */
public class PxAlipayInfo implements java.io.Serializable {

    private Long id;
    /**
     * 对应服务器id
     */
     @SerializedName("id") @Expose
    private String objectId;
    /**
     * 商家支付宝账号
     */
     @Expose
    private String alipayAccount;
    /**
     * 合作伙伴身份ID
     */
     @Expose
    private String sellerId;
    /**
     * 虚拟删除 0：正常 1：删除 2：审核
     */
     @Expose
    private String delFlag;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public PxAlipayInfo() {
    }

    public PxAlipayInfo(Long id) {
        this.id = id;
    }

    public PxAlipayInfo(Long id, String objectId, String alipayAccount, String sellerId, String delFlag) {
        this.id = id;
        this.objectId = objectId;
        this.alipayAccount = alipayAccount;
        this.sellerId = sellerId;
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

    public String getAlipayAccount() {
        return alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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
