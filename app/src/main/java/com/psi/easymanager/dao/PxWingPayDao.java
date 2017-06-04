package com.psi.easymanager.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.psi.easymanager.module.PxWingPay;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PxWingPay".
*/
public class PxWingPayDao extends AbstractDao<PxWingPay, Long> {

    public static final String TABLENAME = "PxWingPay";

    /**
     * Properties of entity PxWingPay.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectId = new Property(1, String.class, "objectId", false, "OBJECT_ID");
        public final static Property OrderTable = new Property(2, String.class, "orderTable", false, "ORDER_TABLE");
        public final static Property OrderNo = new Property(3, String.class, "orderNo", false, "ORDER_NO");
        public final static Property OrderSerialNum = new Property(4, String.class, "orderSerialNum", false, "ORDER_SERIAL_NUM");
        public final static Property OrderTime = new Property(5, String.class, "orderTime", false, "ORDER_TIME");
        public final static Property OrderStatus = new Property(6, String.class, "orderStatus", false, "ORDER_STATUS");
        public final static Property OrderMoney = new Property(7, Double.class, "orderMoney", false, "ORDER_MONEY");
        public final static Property OrderBarcode = new Property(8, String.class, "orderBarcode", false, "ORDER_BARCODE");
        public final static Property DelFlag = new Property(9, String.class, "delFlag", false, "DEL_FLAG");
    };


    public PxWingPayDao(DaoConfig config) {
        super(config);
    }
    
    public PxWingPayDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PxWingPay\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
                "\"OBJECT_ID\" TEXT," + // 1: objectId
                "\"ORDER_TABLE\" TEXT," + // 2: orderTable
                "\"ORDER_NO\" TEXT," + // 3: orderNo
                "\"ORDER_SERIAL_NUM\" TEXT," + // 4: orderSerialNum
                "\"ORDER_TIME\" TEXT," + // 5: orderTime
                "\"ORDER_STATUS\" TEXT," + // 6: orderStatus
                "\"ORDER_MONEY\" REAL," + // 7: orderMoney
                "\"ORDER_BARCODE\" TEXT," + // 8: orderBarcode
                "\"DEL_FLAG\" TEXT);"); // 9: delFlag
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PxWingPay\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PxWingPay entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(2, objectId);
        }
 
        String orderTable = entity.getOrderTable();
        if (orderTable != null) {
            stmt.bindString(3, orderTable);
        }
 
        String orderNo = entity.getOrderNo();
        if (orderNo != null) {
            stmt.bindString(4, orderNo);
        }
 
        String orderSerialNum = entity.getOrderSerialNum();
        if (orderSerialNum != null) {
            stmt.bindString(5, orderSerialNum);
        }
 
        String orderTime = entity.getOrderTime();
        if (orderTime != null) {
            stmt.bindString(6, orderTime);
        }
 
        String orderStatus = entity.getOrderStatus();
        if (orderStatus != null) {
            stmt.bindString(7, orderStatus);
        }
 
        Double orderMoney = entity.getOrderMoney();
        if (orderMoney != null) {
            stmt.bindDouble(8, orderMoney);
        }
 
        String orderBarcode = entity.getOrderBarcode();
        if (orderBarcode != null) {
            stmt.bindString(9, orderBarcode);
        }
 
        String delFlag = entity.getDelFlag();
        if (delFlag != null) {
            stmt.bindString(10, delFlag);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PxWingPay readEntity(Cursor cursor, int offset) {
        PxWingPay entity = new PxWingPay( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // objectId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // orderTable
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // orderNo
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // orderSerialNum
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // orderTime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // orderStatus
            cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7), // orderMoney
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // orderBarcode
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // delFlag
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PxWingPay entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setOrderTable(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setOrderNo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setOrderSerialNum(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setOrderTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setOrderStatus(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setOrderMoney(cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7));
        entity.setOrderBarcode(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDelFlag(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PxWingPay entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PxWingPay entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
