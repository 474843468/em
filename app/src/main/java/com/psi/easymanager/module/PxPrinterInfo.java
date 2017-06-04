package com.psi.easymanager.module;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END

/**
 * Information of current printer.
 */
public class PxPrinterInfo implements java.io.Serializable {

    private Long id;
    /**
     * 对应服务器id
     */
     @SerializedName("id") @Expose
    private String objectId;
    /**
     * 打印机编码
     */
     @Expose
    private String code;
    /**
     * Ip Address.
     */
     @Expose
    private String ipAddress;
    /**
     * 类型（0：收银 1：后厨）
     */
     @Expose
    private String type;
    /**
     * 是否启用（0：启用 1：停用）
     */
     @Expose
    private String status;
    /**
     * 打印机名称
     */
     @Expose
    private String name;
    /**
     * 备注
     */
     @Expose
    private String remarks;
    /**
     * 规格（0：58mm 1：60mm）
     */
     @Expose
    private String format;
    /**
     * 是否连接(0未连接,1已连接)
     */
     @Expose
    private String isConnected;
    /**
     * 虚拟删除 0：正常 1：删除 2：审核
     */
     @Expose
    private String delFlag;
    /**
     * 是否配置钱箱0 否 1是
     */
     @Expose
    private String cashBox;
    /**
     * 是否报警0 否 1是
     */
     @Expose
    private String sound;

    // KEEP FIELDS - put your custom fields here
  public static final String UNCONNECTED = "0";//设备未连接
  public static final String CONNECTED = "1";//设备已连接

  //类型（0：收银 1：后厨 2 : 消费底联）
  public static final String TYPE_CASH = "0";
  public static final String TYPE_KITCH = "1";
  public static final String TYPE_CONSUME = "2";
  //规格（0：58mm 1：80mm）
  public static final String FORMAT_58 = "0";
  public static final String FORMAT_80 = "1";

  public static final String NO = "0";//否
  public static final String YES = "1";//是
  //是否启用（0：启用 1：停用）
  public static final String ENABLE = "0";
  public static final String DIS_ENABLE = "1";
  //是否配置钱箱0 否 1是
  public static final String CONFIG_CASH_BOX_TRUE = "1";
    // KEEP FIELDS END

    public PxPrinterInfo() {
    }

    public PxPrinterInfo(Long id) {
        this.id = id;
    }

    public PxPrinterInfo(Long id, String objectId, String code, String ipAddress, String type, String status, String name, String remarks, String format, String isConnected, String delFlag, String cashBox, String sound) {
        this.id = id;
        this.objectId = objectId;
        this.code = code;
        this.ipAddress = ipAddress;
        this.type = type;
        this.status = status;
        this.name = name;
        this.remarks = remarks;
        this.format = format;
        this.isConnected = isConnected;
        this.delFlag = delFlag;
        this.cashBox = cashBox;
        this.sound = sound;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(String isConnected) {
        this.isConnected = isConnected;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCashBox() {
        return cashBox;
    }

    public void setCashBox(String cashBox) {
        this.cashBox = cashBox;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}