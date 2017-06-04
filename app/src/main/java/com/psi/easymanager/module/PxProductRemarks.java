package com.psi.easymanager.module;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END

/**
 * 商品备注
 */
public class PxProductRemarks implements java.io.Serializable {

    private Long id;
    /**
     * 对应服务器id
     */
     @SerializedName("id") @Expose
    private String objectId;
    /**
     * 虚拟删除 0：正常 1：删除 2：审核
     */
     @Expose
    private String delFlag;
    /**
     * 备注
     */
     @Expose
    private String remarks;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public PxProductRemarks() {
    }

    public PxProductRemarks(Long id) {
        this.id = id;
    }

    public PxProductRemarks(Long id, String objectId, String delFlag, String remarks) {
        this.id = id;
        this.objectId = objectId;
        this.delFlag = delFlag;
        this.remarks = remarks;
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

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // KEEP METHODS - put your custom methods here

    @Override public String toString() {
        return this.remarks;
    }
    // KEEP METHODS END

}
