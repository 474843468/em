package com.psi.easymanager.module;

import com.psi.easymanager.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.psi.easymanager.dao.PxExtraChargeDao;
import com.psi.easymanager.dao.PxTableExtraRelDao;
import com.psi.easymanager.dao.PxTableInfoDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END

/**
 * 附加费桌台关联
 */
public class PxTableExtraRel implements java.io.Serializable {

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
    private Long pxTableInfoId;
    private Long pxExtraChargeId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PxTableExtraRelDao myDao;

    private PxTableInfo dbTable;
    private Long dbTable__resolvedKey;

    private PxExtraCharge dbExtraCharge;
    private Long dbExtraCharge__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    @Expose private PxTableInfo table;//桌台
    @Expose private PxExtraCharge extra;//附加费
    // KEEP FIELDS END

    public PxTableExtraRel() {
    }

    public PxTableExtraRel(Long id) {
        this.id = id;
    }

    public PxTableExtraRel(Long id, String objectId, String delFlag, Long pxTableInfoId, Long pxExtraChargeId) {
        this.id = id;
        this.objectId = objectId;
        this.delFlag = delFlag;
        this.pxTableInfoId = pxTableInfoId;
        this.pxExtraChargeId = pxExtraChargeId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPxTableExtraRelDao() : null;
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

    public Long getPxTableInfoId() {
        return pxTableInfoId;
    }

    public void setPxTableInfoId(Long pxTableInfoId) {
        this.pxTableInfoId = pxTableInfoId;
    }

    public Long getPxExtraChargeId() {
        return pxExtraChargeId;
    }

    public void setPxExtraChargeId(Long pxExtraChargeId) {
        this.pxExtraChargeId = pxExtraChargeId;
    }

    /** To-one relationship, resolved on first access. */
    public PxTableInfo getDbTable() {
        Long __key = this.pxTableInfoId;
        if (dbTable__resolvedKey == null || !dbTable__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PxTableInfoDao targetDao = daoSession.getPxTableInfoDao();
            PxTableInfo dbTableNew = targetDao.load(__key);
            synchronized (this) {
                dbTable = dbTableNew;
            	dbTable__resolvedKey = __key;
            }
        }
        return dbTable;
    }

    public void setDbTable(PxTableInfo dbTable) {
        synchronized (this) {
            this.dbTable = dbTable;
            pxTableInfoId = dbTable == null ? null : dbTable.getId();
            dbTable__resolvedKey = pxTableInfoId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public PxExtraCharge getDbExtraCharge() {
        Long __key = this.pxExtraChargeId;
        if (dbExtraCharge__resolvedKey == null || !dbExtraCharge__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PxExtraChargeDao targetDao = daoSession.getPxExtraChargeDao();
            PxExtraCharge dbExtraChargeNew = targetDao.load(__key);
            synchronized (this) {
                dbExtraCharge = dbExtraChargeNew;
            	dbExtraCharge__resolvedKey = __key;
            }
        }
        return dbExtraCharge;
    }

    public void setDbExtraCharge(PxExtraCharge dbExtraCharge) {
        synchronized (this) {
            this.dbExtraCharge = dbExtraCharge;
            pxExtraChargeId = dbExtraCharge == null ? null : dbExtraCharge.getId();
            dbExtraCharge__resolvedKey = pxExtraChargeId;
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
    public PxTableInfo getTable() {
        return table;
    }

    public void setTable(PxTableInfo table) {
        this.table = table;
    }

    public PxExtraCharge getExtra() {
        return extra;
    }

    public void setExtra(PxExtraCharge extra) {
        this.extra = extra;
    }
    // KEEP METHODS END

}
