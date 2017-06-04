package com.psi.easymanager.module;

import com.psi.easymanager.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.psi.easymanager.dao.PxFormatInfoDao;
import com.psi.easymanager.dao.PxProductFormatRelDao;
import com.psi.easymanager.dao.PxProductInfoDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END
/**
 * Entity mapped to table "ProductFormatRel".
 */
public class PxProductFormatRel implements java.io.Serializable {

    private Long id;
    /**
     * 对应服务器id
     */
     @SerializedName("id") @Expose
    private String objectId;
    /**
     * 价格
     */
     @Expose
    private Double price;
    /**
     * 会员价
     */
     @Expose
    private Double vipPrice;
    /**
     * 虚拟删除 0：正常 1：删除 2：审核
     */
     @Expose
    private String delFlag;
    /**
     * 库存余量
     */
     @Expose
    private Double stock;
    /**
     * 销售状态(0:正常  1:停售)
     */
     @Expose
    private String status;
    /**
     * 条码
     */
     @Expose
    private String barCode;
    private Long pxFormatInfoId;
    private Long pxProductInfoId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PxProductFormatRelDao myDao;

    private PxFormatInfo dbFormat;
    private Long dbFormat__resolvedKey;

    private PxProductInfo dbProduct;
    private Long dbProduct__resolvedKey;


    // KEEP FIELDS - put your custom fields here
  @Expose private PxFormatInfo format;
  @Expose private PxProductInfo product;
  //正常
  public static final String STATUS_ON_SALE = "0";
  //停售
  public static final String STATUS_STOP_SALE= "1";
    // KEEP FIELDS END

    public PxProductFormatRel() {
    }

    public PxProductFormatRel(Long id) {
        this.id = id;
    }

    public PxProductFormatRel(Long id, String objectId, Double price, Double vipPrice, String delFlag, Double stock, String status, String barCode, Long pxFormatInfoId, Long pxProductInfoId) {
        this.id = id;
        this.objectId = objectId;
        this.price = price;
        this.vipPrice = vipPrice;
        this.delFlag = delFlag;
        this.stock = stock;
        this.status = status;
        this.barCode = barCode;
        this.pxFormatInfoId = pxFormatInfoId;
        this.pxProductInfoId = pxProductInfoId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPxProductFormatRelDao() : null;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(Double vipPrice) {
        this.vipPrice = vipPrice;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Double getStock() {
        return stock;
    }

    public void setStock(Double stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Long getPxFormatInfoId() {
        return pxFormatInfoId;
    }

    public void setPxFormatInfoId(Long pxFormatInfoId) {
        this.pxFormatInfoId = pxFormatInfoId;
    }

    public Long getPxProductInfoId() {
        return pxProductInfoId;
    }

    public void setPxProductInfoId(Long pxProductInfoId) {
        this.pxProductInfoId = pxProductInfoId;
    }

    /** To-one relationship, resolved on first access. */
    public PxFormatInfo getDbFormat() {
        Long __key = this.pxFormatInfoId;
        if (dbFormat__resolvedKey == null || !dbFormat__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PxFormatInfoDao targetDao = daoSession.getPxFormatInfoDao();
            PxFormatInfo dbFormatNew = targetDao.load(__key);
            synchronized (this) {
                dbFormat = dbFormatNew;
            	dbFormat__resolvedKey = __key;
            }
        }
        return dbFormat;
    }

    public void setDbFormat(PxFormatInfo dbFormat) {
        synchronized (this) {
            this.dbFormat = dbFormat;
            pxFormatInfoId = dbFormat == null ? null : dbFormat.getId();
            dbFormat__resolvedKey = pxFormatInfoId;
        }
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

  public PxFormatInfo getFormat() {
    return format;
  }

  public void setFormat(PxFormatInfo format) {
    this.format = format;
  }

  public PxProductInfo getProduct() {
    return product;
  }

  public void setProduct(PxProductInfo product) {
    this.product = product;
  }
    // KEEP METHODS END

}
