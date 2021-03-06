package com.psi.easymanager.module;

import com.psi.easymanager.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.psi.easymanager.dao.PxVipCardInfoDao;
import com.psi.easymanager.dao.PxVipCardTypeDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// KEEP INCLUDES END

/**
 * 会员卡信息
 */
public class PxVipCardInfo implements java.io.Serializable {

    private Long id;
    /**
     * 对应服务器id
     */
     @SerializedName("id") @Expose
    private String objectId;
    /**
     * ID卡内部卡号
     */
     @Expose
    private String idcardNum;
    /**
     * 卡号
     */
     @Expose
    private String cardNum;
    /**
     * 密码（明文）
     */
     @Expose
    private String password;
    /**
     * 会员手机号
     */
     @Expose
    private String mobile;
    /**
     * 充值金额
     */
     @Expose
    private Double rechargeMoney;
    /**
     * 实收金额
     */
     @Expose
    private Double receivedMoney;
    /**
     * 余额
     */
     @Expose
    private Double accountBalance;
    /**
     * 卡状态（0：未使用 1：使用）
     */
     @Expose
    private String status;
    /**
     * 积分
     */
     @Expose
    private Integer score;
    /**
     * 总公司
     */
     @Expose
    private String pid;
    /**
     * 虚拟删除 0：正常 1：删除 2：审核
     */
     @Expose
    private String delFlag;
    private Long pxVipCardTypeId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PxVipCardInfoDao myDao;

    private PxVipCardType dbVipCardType;
    private Long dbVipCardType__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    @Expose
    private PxVipCardType cardType; // 卡类型
    // KEEP FIELDS END

    public PxVipCardInfo() {
    }

    public PxVipCardInfo(Long id) {
        this.id = id;
    }

    public PxVipCardInfo(Long id, String objectId, String idcardNum, String cardNum, String password, String mobile, Double rechargeMoney, Double receivedMoney, Double accountBalance, String status, Integer score, String pid, String delFlag, Long pxVipCardTypeId) {
        this.id = id;
        this.objectId = objectId;
        this.idcardNum = idcardNum;
        this.cardNum = cardNum;
        this.password = password;
        this.mobile = mobile;
        this.rechargeMoney = rechargeMoney;
        this.receivedMoney = receivedMoney;
        this.accountBalance = accountBalance;
        this.status = status;
        this.score = score;
        this.pid = pid;
        this.delFlag = delFlag;
        this.pxVipCardTypeId = pxVipCardTypeId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPxVipCardInfoDao() : null;
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

    public String getIdcardNum() {
        return idcardNum;
    }

    public void setIdcardNum(String idcardNum) {
        this.idcardNum = idcardNum;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Double getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(Double rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public Double getReceivedMoney() {
        return receivedMoney;
    }

    public void setReceivedMoney(Double receivedMoney) {
        this.receivedMoney = receivedMoney;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public Long getPxVipCardTypeId() {
        return pxVipCardTypeId;
    }

    public void setPxVipCardTypeId(Long pxVipCardTypeId) {
        this.pxVipCardTypeId = pxVipCardTypeId;
    }

    /** To-one relationship, resolved on first access. */
    public PxVipCardType getDbVipCardType() {
        Long __key = this.pxVipCardTypeId;
        if (dbVipCardType__resolvedKey == null || !dbVipCardType__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PxVipCardTypeDao targetDao = daoSession.getPxVipCardTypeDao();
            PxVipCardType dbVipCardTypeNew = targetDao.load(__key);
            synchronized (this) {
                dbVipCardType = dbVipCardTypeNew;
            	dbVipCardType__resolvedKey = __key;
            }
        }
        return dbVipCardType;
    }

    public void setDbVipCardType(PxVipCardType dbVipCardType) {
        synchronized (this) {
            this.dbVipCardType = dbVipCardType;
            pxVipCardTypeId = dbVipCardType == null ? null : dbVipCardType.getId();
            dbVipCardType__resolvedKey = pxVipCardTypeId;
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

    public PxVipCardType getCardType() {
        return cardType;
    }

    @Override public String toString() {
        return "PxVipCardInfo{" +
            "id=" + id +
            ", objectId='" + objectId + '\'' +
            ", idcardNum='" + idcardNum + '\'' +
            ", cardNum='" + cardNum + '\'' +
            ", password='" + password + '\'' +
            ", mobile='" + mobile + '\'' +
            ", rechargeMoney=" + rechargeMoney +
            ", receivedMoney=" + receivedMoney +
            ", accountBalance=" + accountBalance +
            ", status='" + status + '\'' +
            ", score=" + score +
            ", pid='" + pid + '\'' +
            ", delFlag='" + delFlag + '\'' +
            ", pxVipCardTypeId=" + pxVipCardTypeId +
            ", daoSession=" + daoSession +
            ", myDao=" + myDao +
            ", dbVipCardType=" + dbVipCardType +
            ", dbVipCardType__resolvedKey=" + dbVipCardType__resolvedKey +
            ", cardType=" + cardType +
            '}';
    }
    // KEEP METHODS END

}
