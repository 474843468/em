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

import com.psi.easymanager.module.PxOrderInfo;
import com.psi.easymanager.module.PxTableInfo;

import com.psi.easymanager.module.TableOrderRel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TableOrderRel".
*/
public class TableOrderRelDao extends AbstractDao<TableOrderRel, Long> {

    public static final String TABLENAME = "TableOrderRel";

    /**
     * Properties of entity TableOrderRel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property OrderEndTime = new Property(1, java.util.Date.class, "orderEndTime", false, "ORDER_END_TIME");
        public final static Property PxOrderInfoId = new Property(2, Long.class, "pxOrderInfoId", false, "PX_ORDER_INFO_ID");
        public final static Property PxTableInfoId = new Property(3, Long.class, "pxTableInfoId", false, "PX_TABLE_INFO_ID");
    };

    private DaoSession daoSession;


    public TableOrderRelDao(DaoConfig config) {
        super(config);
    }
    
    public TableOrderRelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TableOrderRel\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
                "\"ORDER_END_TIME\" INTEGER," + // 1: orderEndTime
                "\"PX_ORDER_INFO_ID\" INTEGER," + // 2: pxOrderInfoId
                "\"PX_TABLE_INFO_ID\" INTEGER);"); // 3: pxTableInfoId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TableOrderRel\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TableOrderRel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        java.util.Date orderEndTime = entity.getOrderEndTime();
        if (orderEndTime != null) {
            stmt.bindLong(2, orderEndTime.getTime());
        }
 
        Long pxOrderInfoId = entity.getPxOrderInfoId();
        if (pxOrderInfoId != null) {
            stmt.bindLong(3, pxOrderInfoId);
        }
 
        Long pxTableInfoId = entity.getPxTableInfoId();
        if (pxTableInfoId != null) {
            stmt.bindLong(4, pxTableInfoId);
        }
    }

    @Override
    protected void attachEntity(TableOrderRel entity) {
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
    public TableOrderRel readEntity(Cursor cursor, int offset) {
        TableOrderRel entity = new TableOrderRel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // orderEndTime
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // pxOrderInfoId
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3) // pxTableInfoId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TableOrderRel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOrderEndTime(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setPxOrderInfoId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setPxTableInfoId(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(TableOrderRel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(TableOrderRel entity) {
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
            SqlUtils.appendColumns(builder, "T0", daoSession.getPxOrderInfoDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getPxTableInfoDao().getAllColumns());
            builder.append(" FROM TableOrderRel T");
            builder.append(" LEFT JOIN OrderInfo T0 ON T.\"PX_ORDER_INFO_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN TableInfo T1 ON T.\"PX_TABLE_INFO_ID\"=T1.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected TableOrderRel loadCurrentDeep(Cursor cursor, boolean lock) {
        TableOrderRel entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        PxOrderInfo dbOrder = loadCurrentOther(daoSession.getPxOrderInfoDao(), cursor, offset);
        entity.setDbOrder(dbOrder);
        offset += daoSession.getPxOrderInfoDao().getAllColumns().length;

        PxTableInfo dbTable = loadCurrentOther(daoSession.getPxTableInfoDao(), cursor, offset);
        entity.setDbTable(dbTable);

        return entity;    
    }

    public TableOrderRel loadDeep(Long key) {
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
    public List<TableOrderRel> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<TableOrderRel> list = new ArrayList<TableOrderRel>(count);
        
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
    
    protected List<TableOrderRel> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<TableOrderRel> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}