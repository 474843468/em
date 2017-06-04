package com.psi.easymanager.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import com.psi.easymanager.module.PxFormatInfo;
import com.psi.easymanager.module.PxProductInfo;

import com.psi.easymanager.module.PxProductFormatRel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ProductFormatRel".
*/
public class PxProductFormatRelDao extends AbstractDao<PxProductFormatRel, Long> {

    public static final String TABLENAME = "ProductFormatRel";

    /**
     * Properties of entity PxProductFormatRel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectId = new Property(1, String.class, "objectId", false, "OBJECT_ID");
        public final static Property Price = new Property(2, Double.class, "price", false, "PRICE");
        public final static Property VipPrice = new Property(3, Double.class, "vipPrice", false, "VIP_PRICE");
        public final static Property DelFlag = new Property(4, String.class, "delFlag", false, "DEL_FLAG");
        public final static Property Stock = new Property(5, Double.class, "stock", false, "STOCK");
        public final static Property Status = new Property(6, String.class, "status", false, "STATUS");
        public final static Property BarCode = new Property(7, String.class, "barCode", false, "BAR_CODE");
        public final static Property PxFormatInfoId = new Property(8, Long.class, "pxFormatInfoId", false, "PX_FORMAT_INFO_ID");
        public final static Property PxProductInfoId = new Property(9, Long.class, "pxProductInfoId", false, "PX_PRODUCT_INFO_ID");
    };

    private DaoSession daoSession;


    public PxProductFormatRelDao(DaoConfig config) {
        super(config);
    }
    
    public PxProductFormatRelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ProductFormatRel\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
                "\"OBJECT_ID\" TEXT," + // 1: objectId
                "\"PRICE\" REAL," + // 2: price
                "\"VIP_PRICE\" REAL," + // 3: vipPrice
                "\"DEL_FLAG\" TEXT," + // 4: delFlag
                "\"STOCK\" REAL," + // 5: stock
                "\"STATUS\" TEXT," + // 6: status
                "\"BAR_CODE\" TEXT," + // 7: barCode
                "\"PX_FORMAT_INFO_ID\" INTEGER," + // 8: pxFormatInfoId
                "\"PX_PRODUCT_INFO_ID\" INTEGER);"); // 9: pxProductInfoId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ProductFormatRel\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PxProductFormatRel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(2, objectId);
        }
 
        Double price = entity.getPrice();
        if (price != null) {
            stmt.bindDouble(3, price);
        }
 
        Double vipPrice = entity.getVipPrice();
        if (vipPrice != null) {
            stmt.bindDouble(4, vipPrice);
        }
 
        String delFlag = entity.getDelFlag();
        if (delFlag != null) {
            stmt.bindString(5, delFlag);
        }
 
        Double stock = entity.getStock();
        if (stock != null) {
            stmt.bindDouble(6, stock);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(7, status);
        }
 
        String barCode = entity.getBarCode();
        if (barCode != null) {
            stmt.bindString(8, barCode);
        }
 
        Long pxFormatInfoId = entity.getPxFormatInfoId();
        if (pxFormatInfoId != null) {
            stmt.bindLong(9, pxFormatInfoId);
        }
 
        Long pxProductInfoId = entity.getPxProductInfoId();
        if (pxProductInfoId != null) {
            stmt.bindLong(10, pxProductInfoId);
        }
    }

    @Override
    protected void attachEntity(PxProductFormatRel entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PxProductFormatRel readEntity(Cursor cursor, int offset) {
        PxProductFormatRel entity = new PxProductFormatRel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // objectId
            cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2), // price
            cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3), // vipPrice
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // delFlag
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // stock
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // status
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // barCode
            cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8), // pxFormatInfoId
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9) // pxProductInfoId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PxProductFormatRel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPrice(cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2));
        entity.setVipPrice(cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3));
        entity.setDelFlag(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStock(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setStatus(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setBarCode(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPxFormatInfoId(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
        entity.setPxProductInfoId(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PxProductFormatRel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PxProductFormatRel entity) {
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
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getPxFormatInfoDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getPxProductInfoDao().getAllColumns());
            builder.append(" FROM ProductFormatRel T");
            builder.append(" LEFT JOIN FormatInfo T0 ON T.\"PX_FORMAT_INFO_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN ProductInfo T1 ON T.\"PX_PRODUCT_INFO_ID\"=T1.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected PxProductFormatRel loadCurrentDeep(Cursor cursor, boolean lock) {
        PxProductFormatRel entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        PxFormatInfo dbFormat = loadCurrentOther(daoSession.getPxFormatInfoDao(), cursor, offset);
        entity.setDbFormat(dbFormat);
        offset += daoSession.getPxFormatInfoDao().getAllColumns().length;

        PxProductInfo dbProduct = loadCurrentOther(daoSession.getPxProductInfoDao(), cursor, offset);
        entity.setDbProduct(dbProduct);

        return entity;    
    }

    public PxProductFormatRel loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<PxProductFormatRel> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<PxProductFormatRel> list = new ArrayList<PxProductFormatRel>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<PxProductFormatRel> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<PxProductFormatRel> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
