package com.psi.easymanager.module;

import com.psi.easymanager.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.psi.easymanager.dao.PxProductConfigPlanDao;
import com.psi.easymanager.dao.PxProductConfigPlanRelDao;
import com.psi.easymanager.dao.PxProductInfoDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END
/**
 * Entity mapped to table "ProductConfigPlanRel".
 */
public class PxProductConfigPlanRel implements java.io.Serializable {

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
    private Long pxProductInfoId;
    private Long pxProductConfigPlanId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PxProductConfigPlanRelDao myDao;

    private PxProductInfo dbProduct;
    private Long dbProduct__resolvedKey;

    private PxProductConfigPlan dbProductConfigPlan;
    private Long dbProductConfigPlan__resolvedKey;


    // KEEP FIELDS - put your custom fields here
  @Expose private PxProductInfo product;//解析用
  @Expose private PxProductConfigPlan configPlan;
    // KEEP FIELDS END

    public PxProductConfigPlanRel() {
    }

    public PxProductConfigPlanRel(Long id) {
        this.id = id;
    }

    public PxProductConfigPlanRel(Long id, String objectId, String delFlag, Long pxProductInfoId, Long pxProductConfigPlanId) {
        this.id = id;
        this.objectId = objectId;
        this.delFlag = delFlag;
        this.pxProductInfoId = pxProductInfoId;
        this.pxProductConfigPlanId = pxProductConfigPlanId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPxProductConfigPlanRelDao() : null;
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

    public Long getPxProductInfoId() {
        return pxProductInfoId;
    }

    public void setPxProductInfoId(Long pxProductInfoId) {
        this.pxProductInfoId = pxProductInfoId;
    }

    public Long getPxProductConfigPlanId() {
        return pxProductConfigPlanId;
    }

    public void setPxProductConfigPlanId(Long pxProductConfigPlanId) {
        this.pxProductConfigPlanId = pxProductConfigPlanId;
    }

    /** To-one relationship, resolved on first access. */
    public PxProductInfo getDbProduct() {
        Long __key = this.pxProductInfoId;
        if (dbProduct__resolvedKey == null || !dbProduct__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PxProductInfoDao targetDao = daoSession.getPxProductInfoDao();
            PxProductInfo dbProductNew = targetDao.load(__key);
            synchronized (this) {
                dbProduct = dbProductNew;
            	dbProduct__resolvedKey = __key;
            }
        }
        return dbProduct;
    }

    public void setDbProduct(PxProductInfo dbProduct) {
        synchronized (this) {
            this.dbProduct = dbProduct;
            pxProductInfoId = dbProduct == null ? null : dbProduct.getId();
            dbProduct__resolvedKey = pxProductInfoId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public PxProductConfigPlan getDbProductConfigPlan() {
        Long __key = this.pxProductConfigPlanId;
        if (dbProductConfigPlan__resolvedKey == null || !dbProductConfigPlan__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PxProductConfigPlanDao targetDao = daoSession.getPxProductConfigPlanDao();
            PxProductConfigPlan dbProductConfigPlanNew = targetDao.load(__key);
            synchronized (this) {
                dbProductConfigPlan = dbProductConfigPlanNew;
            	dbProductConfigPlan__resolvedKey = __key;
            }
        }
        return dbProductConfigPlan;
    }

    public void setDbProductConfigPlan(PxProductConfigPlan dbProductConfigPlan) {
        synchronized (this) {
            this.dbProductConfigPlan = dbProductConfigPlan;
            pxProductConfigPlanId = dbProductConfigPlan == null ? null : dbProductConfigPlan.getId();
            dbProductConfigPlan__resolvedKey = pxProductConfigPlanId;
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
  public PxProductInfo getProduct() {
    return product;
  }

  public void setProduct(PxProductInfo product) {
    this.product = product;
  }

  public PxProductConfigPlan getConfigPlan() {
    return configPlan;
  }

  public void setConfigPlan(PxProductConfigPlan configPlan) {
    this.configPlan = configPlan;
  }
    // KEEP METHODS END

}
