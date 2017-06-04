package com.psi.easymanager.module;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END

/**
 * 设置信息
 */
public class PxSetInfo implements java.io.Serializable {

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
     * 商业模式(1:默认)
     */
     @Expose
    private String model;
    /**
     * 是否快速开单(1:是2:否)
     */
     @Expose
    private String isFastOpenOrder;
    /**
     * 点餐完毕是否自动切换到结账页面(1:是2:否)
     */
     @Expose
    private String isAutoTurnCheckout;
    /**
     * 商品添加后自动下单(1:是2:否)
     */
     @Expose
    private String autoOrder;
    /**
     * 结账完毕自动开单(1:是2:否)
     */
     @Expose
    private String overAutoStartBill;
    /**
     * 会员充值消费是否打印凭证(1:是2:否)
     */
     @Expose
    private String isAutoPrintRechargeVoucher;
    /**
     * 财务联是否打印分类统计信息(1:是2:否)
     */
     @Expose
    private String isFinancePrintCategory;

    // KEEP FIELDS - put your custom fields here
    public static final String MODEL_DEFAULT = "1";

    public static final String FAST_START_ORDER_TRUE = "1";
    public static final String FAST_START_ORDER_FALSE = "2";

    public static final String AUTO_TURN_CHECKOUT_TRUE = "1";
    public static final String AUTO_TURN_CHECKOUT_FALSE = "2";

    public static final String AUTO_ORDER_TRUE = "1";
    public static final String AUTO_ORDER_FALSE = "2";

    public static final String OVER_AUTO_START_BILL_TRUE = "1";
    public static final String OVER_AUTO_START_BILL_FALSE = "2";

    public static final String AUTO_PRINT_RECHARGE_TRUE = "1";
    public static final String AUTO_PRINT_RECHARGE_FALSE = "2";

    public static final String FINANCE_PRINT_CATEGORY_TRUE = "1";
    public static final String FINANCE_PRINT_CATEGORY_FALSE = "2";
    // KEEP FIELDS END

    public PxSetInfo() {
    }

    public PxSetInfo(Long id) {
        this.id = id;
    }

    public PxSetInfo(Long id, String objectId, String delFlag, String model, String isFastOpenOrder, String isAutoTurnCheckout, String autoOrder, String overAutoStartBill, String isAutoPrintRechargeVoucher, String isFinancePrintCategory) {
        this.id = id;
        this.objectId = objectId;
        this.delFlag = delFlag;
        this.model = model;
        this.isFastOpenOrder = isFastOpenOrder;
        this.isAutoTurnCheckout = isAutoTurnCheckout;
        this.autoOrder = autoOrder;
        this.overAutoStartBill = overAutoStartBill;
        this.isAutoPrintRechargeVoucher = isAutoPrintRechargeVoucher;
        this.isFinancePrintCategory = isFinancePrintCategory;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getIsFastOpenOrder() {
        return isFastOpenOrder;
    }

    public void setIsFastOpenOrder(String isFastOpenOrder) {
        this.isFastOpenOrder = isFastOpenOrder;
    }

    public String getIsAutoTurnCheckout() {
        return isAutoTurnCheckout;
    }

    public void setIsAutoTurnCheckout(String isAutoTurnCheckout) {
        this.isAutoTurnCheckout = isAutoTurnCheckout;
    }

    public String getAutoOrder() {
        return autoOrder;
    }

    public void setAutoOrder(String autoOrder) {
        this.autoOrder = autoOrder;
    }

    public String getOverAutoStartBill() {
        return overAutoStartBill;
    }

    public void setOverAutoStartBill(String overAutoStartBill) {
        this.overAutoStartBill = overAutoStartBill;
    }

    public String getIsAutoPrintRechargeVoucher() {
        return isAutoPrintRechargeVoucher;
    }

    public void setIsAutoPrintRechargeVoucher(String isAutoPrintRechargeVoucher) {
        this.isAutoPrintRechargeVoucher = isAutoPrintRechargeVoucher;
    }

    public String getIsFinancePrintCategory() {
        return isFinancePrintCategory;
    }

    public void setIsFinancePrintCategory(String isFinancePrintCategory) {
        this.isFinancePrintCategory = isFinancePrintCategory;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}