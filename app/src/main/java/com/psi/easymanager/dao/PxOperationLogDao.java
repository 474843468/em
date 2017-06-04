package com.psi.easymanager.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.psi.easymanager.module.PxOperationLog;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "OperationLog".
*/
public class PxOperationLogDao extends AbstractDao<PxOperationLog, Long> {

    public static final String TABLENAME = "OperationLog";

    /**
     * Properties of entity PxOperationLog.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property OrderNo = new Property(1, String.class, "orderNo", false, "ORDER_NO");
        public final static Property Operater = new Property(2, String.class, "operater", false, "OPERATER");
        public final static Property ProductName = new Property(3, String.class, "productName", false, "PRODUCT_NAME");
        public final static Property Type = new Property(4, String.class, "type", false, "TYPE");
        public final static Property Remarks = new Property(5, String.class, "remarks", false, "REMARKS");
        public final static Property Cid = new Property(6, String.class, "cid", false, "CID");
        public final static Property OperaterDate = new Property(7, Long.class, "operaterDate", false, "OPERATER_DATE");
        public final static Property TotalPrice = new Property(8, Double.class, "totalPrice", false, "TOTAL_PRICE");
    };


    public PxOperationLogDao(DaoConfig config) {
        super(config);
    }
    
    public PxOperationLogDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"OperationLog\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
                "\"ORDER_NO\" TEXT," + // 1: orderNo
                "\"OPERATER\" TEXT," + // 2: operater
                "\"PRODUCT_NAME\" TEXT," + // 3: productName
                "\"TYPE\" TEXT," + // 4: type
                "\"REMARKS\" TEXT," + // 5: remarks
                "\"CID\" TEXT," + // 6: cid
                "\"OPERATER_DATE\" INTEGER," + // 7: operaterDate
                "\"TOTAL_PRICE\" REAL);"); // 8: totalPrice
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"OperationLog\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PxOperationLog entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String orderNo = entity.getOrderNo();
        if (orderNo != null) {
            stmt.bindString(2, orderNo);
        }
 
        String operater = entity.getOperater();
        if (operater != null) {
            stmt.bindString(3, operater);
        }
 
        String productName = entity.getProductName();
        if (productName != null) {
            stmt.bindString(4, productName);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(5, type);
        }
 
        String remarks = entity.getRemarks();
        if (remarks != null) {
            stmt.bindString(6, remarks);
        }
 
        String cid = entity.getCid();
        if (cid != null) {
            stmt.bindString(7, cid);
        }
 
        Long operaterDate = entity.getOperaterDate();
        if (operaterDate != null) {
            stmt.bindLong(8, operaterDate);
        }
 
        Double totalPrice = entity.getTotalPrice();
        if (totalPrice != null) {
            stmt.bindDouble(9, totalPrice);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PxOperationLog readEntity(Cursor cursor, int offset) {
        PxOperationLog entity = new PxOperationLog( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // orderNo
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // operater
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // productName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // type
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // remarks
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // cid
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // operaterDate
            cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8) // totalPrice
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PxOperationLog entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOrderNo(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setOperater(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setProductName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setRemarks(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCid(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setOperaterDate(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setTotalPrice(cursor.isNull(offset + 8) ? null : cursor.getDouble(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PxOperationLog entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PxOperationLog entity) {
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