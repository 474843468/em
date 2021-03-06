package com.psi.easymanager.module;

import com.psi.easymanager.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.psi.easymanager.dao.PdConfigRelDao;
import com.psi.easymanager.dao.PrintDetailsCollectDao;
import com.psi.easymanager.dao.PrintDetailsDao;
import com.psi.easymanager.dao.PxOrderInfoDao;
import com.psi.easymanager.dao.PxProductConfigPlanDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
// KEEP INCLUDES END

/**
 * 打印详情和配菜方案rel
 */
public class PdConfigRel implements java.io.Serializable {

    private Long id;
    /**
     * 0：下单 1:退单)
     */
     @Expose
    private String type;
    /**
     * 操作时间(下单时间或者退货时间)
     */
     @Expose
    private java.util.Date operateTime;
    /**
     * 是否已打印
     */
     @Expose
    private Boolean isPrinted;
    private Long pxOrderInfoId;
    private Long dbPrintDetailsId;
    private Long dbConfigId;
    private Long PdConfigRelId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PdConfigRelDao myDao;

    private PxOrderInfo dbOrder;
    private Long dbOrder__resolvedKey;

    private PrintDetails dbPrintDetails;
    private Long dbPrintDetails__resolvedKey;

    private PxProductConfigPlan dbConfig;
    private Long dbConfig__resolvedKey;

    private PrintDetailsCollect dbPdCollect;
    private Long dbPdCollect__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    public static final String TYPE_ORDER = "0";
    public static final String TYPE_REFUND = "1";
    // KEEP FIELDS END

    public PdConfigRel() {
    }

    public PdConfigRel(Long id) {
        this.id = id;
    }

    public PdConfigRel(Long id, String type, java.util.Date operateTime, Boolean isPrinted, Long pxOrderInfoId, Long dbPrintDetailsId, Long dbConfigId, Long PdConfigRelId) {
        this.id = id;
        this.type = type;
        this.operateTime = operateTime;
        this.isPrinted = isPrinted;
        this.pxOrderInfoId = pxOrderInfoId;
        this.dbPrintDetailsId = dbPrintDetailsId;
        this.dbConfigId = dbConfigId;
        this.PdConfigRelId = PdConfigRelId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPdConfigRelDao() : null;
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

    public java.util.Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(java.util.Date operateTime) {
        this.operateTime = operateTime;
    }

    public Boolean getIsPrinted() {
        return isPrinted;
    }

    public void setIsPrinted(Boolean isPrinted) {
        this.isPrinted = isPrinted;
    }

    public Long getPxOrderInfoId() {
        return pxOrderInfoId;
    }

    public void setPxOrderInfoId(Long pxOrderInfoId) {
        this.pxOrderInfoId = pxOrderInfoId;
    }

    public Long getDbPrintDetailsId() {
        return dbPrintDetailsId;
    }

    public void setDbPrintDetailsId(Long dbPrintDetailsId) {
        this.dbPrintDetailsId = dbPrintDetailsId;
    }

    public Long getDbConfigId() {
        return dbConfigId;
    }

    public void setDbConfigId(Long dbConfigId) {
        this.dbConfigId = dbConfigId;
    }

    public Long getPdConfigRelId() {
        return PdConfigRelId;
    }

    public void setPdConfigRelId(Long PdConfigRelId) {
        this.PdConfigRelId = PdConfigRelId;
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

    /** To-one relationship, resolved on first access. */
    public PrintDetails getDbPrintDetails() {
        Long __key = this.dbPrintDetailsId;
        if (dbPrintDetails__resolvedKey == null || !dbPrintDetails__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PrintDetailsDao targetDao = daoSession.getPrintDetailsDao();
            PrintDetails dbPrintDetailsNew = targetDao.load(__key);
            synchronized (this) {
                dbPrintDetails = dbPrintDetailsNew;
            	dbPrintDetails__resolvedKey = __key;
            }
        }
        return dbPrintDetails;
    }

    public void setDbPrintDetails(PrintDetails dbPrintDetails) {
        synchronized (this) {
            this.dbPrintDetails = dbPrintDetails;
            dbPrintDetailsId = dbPrintDetails == null ? null : dbPrintDetails.getId();
            dbPrintDetails__resolvedKey = dbPrintDetailsId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public PxProductConfigPlan getDbConfig() {
        Long __key = this.dbConfigId;
        if (dbConfig__resolvedKey == null || !dbConfig__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PxProductConfigPlanDao targetDao = daoSession.getPxProductConfigPlanDao();
            PxProductConfigPlan dbConfigNew = targetDao.load(__key);
            synchronized (this) {
                dbConfig = dbConfigNew;
            	dbConfig__resolvedKey = __key;
            }
        }
        return dbConfig;
    }

    public void setDbConfig(PxProductConfigPlan dbConfig) {
        synchronized (this) {
            this.dbConfig = dbConfig;
            dbConfigId = dbConfig == null ? null : dbConfig.getId();
            dbConfig__resolvedKey = dbConfigId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public PrintDetailsCollect getDbPdCollect() {
        Long __key = this.PdConfigRelId;
        if (dbPdCollect__resolvedKey == null || !dbPdCollect__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PrintDetailsCollectDao targetDao = daoSession.getPrintDetailsCollectDao();
            PrintDetailsCollect dbPdCollectNew = targetDao.load(__key);
            synchronized (this) {
                dbPdCollect = dbPdCollectNew;
            	dbPdCollect__resolvedKey = __key;
            }
        }
        return dbPdCollect;
    }

    public void setDbPdCollect(PrintDetailsCollect dbPdCollect) {
        synchronized (this) {
            this.dbPdCollect = dbPdCollect;
            PdConfigRelId = dbPdCollect == null ? null : dbPdCollect.getId();
            dbPdCollect__resolvedKey = PdConfigRelId;
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
