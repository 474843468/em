package com.psi.easymanager.module;

import com.psi.easymanager.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxPayInfoDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
// KEEP INCLUDES END

/**
 * 付款信息
 */
public class PxPayInfo implements java.io.Serializable {

    private Long id;
    /**
     * 付款时间
     */
     @Expose
    private java.util.Date payTime;
    /**
     * 实收金额
     */
     @Expose
    private Double received;
    /**
     * 找零
     */
     @Expose
    private Double change;
    /**
     * 凭证码(用于POS刷卡)
     */
     @Expose
    private String voucherCode;
    /**
     * 流水号(支付宝、微信)
     */
     @Expose
    private String tradeNo;
    /**
     * 备注,免单原因等
     */
     @Expose
    private String remarks;
    /**
     * 会员手机
     */
     @Expose
    private String vipMobile;
    /**
     * 会员id
     */
     @Expose
    private String vipId;
    /**
     * 会员内部卡号
     */
     @Expose
    private String idCardNum;
    /**
     * 支付方式Id
     */
     @Expose
    private String paymentId;
    /**
     * 支付方式类型
     */
     @Expose
    private String paymentType;
    /**
     * 支付方式名称
     */
     @Expose
    private String paymentName;
    /**
     * 是否计算入销售额(0:是 1：否)
     */
     @Expose
    private String salesAmount;
    /**
     * 支付类优惠
     */
     @Expose
    private Double payPrivilege;
    /**
     * 验券码
     */
     @Expose
    private String ticketCode;
    private Long pxOrderInfoId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PxPayInfoDao myDao;

    private PxOrderInfo dbOrder;
    private Long dbOrder__resolvedKey;


    // KEEP FIELDS - put your custom fields here
  public static final String PAY_TYPE_CASH = "0";
  public static final String PAY_TYPE_VIP_CARD = "1";
  public static final String PAY_TYPE_WING_PAY = "2";
  public static final String PAY_TYPE_POS_PAY = "3";
  public static final String PAY_TYPE_ALI_PAY = "4";
  public static final String PAY_TYPE_WX_PAY = "5";
    // KEEP FIELDS END

    public PxPayInfo() {
    }

    public PxPayInfo(Long id) {
        this.id = id;
    }

    public PxPayInfo(Long id, java.util.Date payTime, Double received, Double change, String voucherCode, String tradeNo, String remarks, String vipMobile, String vipId, String idCardNum, String paymentId, String paymentType, String paymentName, String salesAmount, Double payPrivilege, String ticketCode, Long pxOrderInfoId) {
        this.id = id;
        this.payTime = payTime;
        this.received = received;
        this.change = change;
        this.voucherCode = voucherCode;
        this.tradeNo = tradeNo;
        this.remarks = remarks;
        this.vipMobile = vipMobile;
        this.vipId = vipId;
        this.idCardNum = idCardNum;
        this.paymentId = paymentId;
        this.paymentType = paymentType;
        this.paymentName = paymentName;
        this.salesAmount = salesAmount;
        this.payPrivilege = payPrivilege;
        this.ticketCode = ticketCode;
        this.pxOrderInfoId = pxOrderInfoId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPxPayInfoDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.util.Date getPayTime() {
        return payTime;
    }

    public void setPayTime(java.util.Date payTime) {
        this.payTime = payTime;
    }

    public Double getReceived() {
        return received;
    }

    public void setReceived(Double received) {
        this.received = received;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getVipMobile() {
        return vipMobile;
    }

    public void setVipMobile(String vipMobile) {
        this.vipMobile = vipMobile;
    }

    public String getVipId() {
        return vipId;
    }

    public void setVipId(String vipId) {
        this.vipId = vipId;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(String salesAmount) {
        this.salesAmount = salesAmount;
    }

    public Double getPayPrivilege() {
        return payPrivilege;
    }

    public void setPayPrivilege(Double payPrivilege) {
        this.payPrivilege = payPrivilege;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public Long getPxOrderInfoId() {
        return pxOrderInfoId;
    }

    public void setPxOrderInfoId(Long pxOrderInfoId) {
        this.pxOrderInfoId = pxOrderInfoId;
    }

    /** To-one relationship, resolved on first access. */
    public PxOrderInfo getDbOrder() {
        Long __key = this.pxOrderInfoId;
        if (dbOrder__resolvedKey == null || !dbOrder__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PxOrderInfoDao targetDao = daoSession.getPxOrderInfoDao();
            PxOrderInfo dbOrderNew = targetDao.load(__key);
            synchronized (this) {
                dbOrder = dbOrderNew;
            	dbOrder__resolvedKey = __key;
            }
        }
        return dbOrder;
    }

    public void setDbOrder(PxOrderInfo dbOrder) {
        synchronized (this) {
            this.dbOrder = dbOrder;
            pxOrderInfoId = dbOrder == null ? null : dbOrder.getId();
            dbOrder__resolvedKey = pxOrderInfoId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
