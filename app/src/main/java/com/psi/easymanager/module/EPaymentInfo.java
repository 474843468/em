package com.psi.easymanager.module;

import com.psi.easymanager.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.psi.easymanager.dao.EPaymentInfoDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxPayInfoDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
// KEEP INCLUDES END

/**
 * 电子支付信息
 */
public class EPaymentInfo implements java.io.Serializable {

    private Long id;
    /**
     * 类型(0:支付宝 1:微信 2:会员 3:翼支付)
     */
     @Expose
    private String type;
    /**
     * 支付时间
     */
     @Expose
    private java.util.Date payTime;
    /**
     * 单号
     */
     @Expose
    private String orderNo;
    /**
     * 桌名
     */
     @Expose
    private String tableName;
    /**
     * 状态(0:已付款 1:已退款 2:付款过并已退款)
     */
     @Expose
    private String status;
    /**
     * 交易码
     */
     @Expose
    private String tradeNo;
    /**
     * 支付金额
     */
     @Expose
    private Double price;
    /**
     * 已处理(0:未处理 1:已处理)
     */
     @Expose
    private String isHandled;
    private Long payInfoId;
    private Long orderInfoId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient EPaymentInfoDao myDao;

    private PxPayInfo dbPayInfo;
    private Long dbPayInfo__resolvedKey;

    private PxOrderInfo dbOrder;
    private Long dbOrder__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    public static final String TYPE_ALI_PAY = "0";
    public static final String TYPE_WX_PAY = "1";
    public static final String TYPE_VIP_PAY = "2";
    public static final String TYPE_BEST_PAY = "3";

    public static final String STATUS_PAYED = "0";
    public static final String STATUS_REFUND = "1";
    public static final String STATUS_PAYED_AND_REFUND = "2";
    //已处理(0:未处理 1:已处理)
    public static final String HAS_HANDLED = "1";
    // KEEP FIELDS END

    public EPaymentInfo() {
    }

    public EPaymentInfo(Long id) {
        this.id = id;
    }

    public EPaymentInfo(Long id, String type, java.util.Date payTime, String orderNo, String tableName, String status, String tradeNo, Double price, String isHandled, Long payInfoId, Long orderInfoId) {
        this.id = id;
        this.type = type;
        this.payTime = payTime;
        this.orderNo = orderNo;
        this.tableName = tableName;
        this.status = status;
        this.tradeNo = tradeNo;
        this.price = price;
        this.isHandled = isHandled;
        this.payInfoId = payInfoId;
        this.orderInfoId = orderInfoId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEPaymentInfoDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public java.util.Date getPayTime() {
        return payTime;
    }

    public void setPayTime(java.util.Date payTime) {
        this.payTime = payTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getIsHandled() {
        return isHandled;
    }

    public void setIsHandled(String isHandled) {
        this.isHandled = isHandled;
    }

    public Long getPayInfoId() {
        return payInfoId;
    }

    public void setPayInfoId(Long payInfoId) {
        this.payInfoId = payInfoId;
    }

    public Long getOrderInfoId() {
        return orderInfoId;
    }

    public void setOrderInfoId(Long orderInfoId) {
        this.orderInfoId = orderInfoId;
    }

    /** To-one relationship, resolved on first access. */
    public PxPayInfo getDbPayInfo() {
        Long __key = this.payInfoId;
        if (dbPayInfo__resolvedKey == null || !dbPayInfo__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PxPayInfoDao targetDao = daoSession.getPxPayInfoDao();
            PxPayInfo dbPayInfoNew = targetDao.load(__key);
            synchronized (this) {
                dbPayInfo = dbPayInfoNew;
            	dbPayInfo__resolvedKey = __key;
            }
        }
        return dbPayInfo;
    }

    public void setDbPayInfo(PxPayInfo dbPayInfo) {
        synchronized (this) {
            this.dbPayInfo = dbPayInfo;
            payInfoId = dbPayInfo == null ? null : dbPayInfo.getId();
            dbPayInfo__resolvedKey = payInfoId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public PxOrderInfo getDbOrder() {
        Long __key = this.orderInfoId;
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
            orderInfoId = dbOrder == null ? null : dbOrder.getId();
            dbOrder__resolvedKey = orderInfoId;
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
