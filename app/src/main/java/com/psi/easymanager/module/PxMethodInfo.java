package com.psi.easymanager.module;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END

/**
 * 做法信息
 */
public class PxMethodInfo implements java.io.Serializable {

    private Long id;
    /**
     * 对应服务器id
     */
     @SerializedName("id") @Expose
    private String objectId;
    /**
     * 做法名称
     */
     @Expose
    private String name;
    /**
     * 虚拟删除 0：正常 1：删除 2：审核
     */
     @Expose
    private String delFlag;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public PxMethodInfo() {
    }

    public PxMethodInfo(Long id) {
        this.id = id;
    }

    public PxMethodInfo(Long id, String objectId, String name, String delFlag) {
        this.id = id;
        this.objectId = objectId;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
