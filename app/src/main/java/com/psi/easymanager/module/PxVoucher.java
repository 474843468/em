package com.psi.easymanager.module;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END

/**
 * 优惠券
 */
public class PxVoucher implements java.io.Serializable {

    private Long id;
    /**
     * 虚拟删除 0：正常 1：删除 2：审核
     */
     @Expose
    private String delFlag;
    /**
     * 对应服务器id
     */
     @SerializedName("id") @Expose
    private String objectId;
    /**
     * 编码
     */
     @Expose
    private String code;
    /**
     * 优惠券金额
     */
     @Expose
    private Double price;
    /**
     * 减免金额
     */
     @Expose
    private Double deratePrice;
    /**
     * 优惠券类型(0:默认1:其他)说明默认为0
     */
     @Expose
    private String type;
    /**
     * 开始日期
     */
     @Expose
    private java.util.Date startDate;
    /**
     * 结束日期
     */
     @Expose
    private java.util.Date endDate;
    /**
     * 是否永久有效(0：否 1：是)
     */
     @Expose
    private String permanent;

    // KEEP FIELDS - put your custom fields here
  public static final String PERMANENT_TRUE = "1";
  public static final String PERMANENT_FALSE = "0";
    // KEEP FIELDS END

    public PxVoucher() {
    }

    public PxVoucher(Long id) {
        this.id = id;
    }

    public PxVoucher(Long id, String delFlag, String objectId, String code, Double price, Double deratePrice, String type, java.util.Date startDate, java.util.Date endDate, String permanent) {
        this.id = id;
        this.delFlag = delFlag;
        this.objectId = objectId;
        this.code = code;
        this.price = price;
        this.deratePrice = deratePrice;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.permanent = permanent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDeratePrice() {
        return deratePrice;
    }

    public void setDeratePrice(Double deratePrice) {
        this.deratePrice = deratePrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public String getPermanent() {
        return permanent;
    }

    public void setPermanent(String permanent) {
        this.permanent = permanent;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
