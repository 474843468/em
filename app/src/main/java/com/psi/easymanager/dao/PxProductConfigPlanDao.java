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

import com.psi.easymanager.module.PxPrinterInfo;

import com.psi.easymanager.module.PxProductConfigPlan;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PxProductConfigPlan".
*/
public class PxProductConfigPlanDao extends AbstractDao<PxProductConfigPlan, Long> {

    public static final String TABLENAME = "PxProductConfigPlan";

    /**
     * Properties of entity PxProductConfigPlan.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectId = new Property(1, String.class, "objectId", false, "OBJECT_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Flag = new Property(3, String.class, "flag", false, "FLAG");
        public final static Property DelFlag = new Property(4, String.class, "delFlag", false, "DEL_FLAG");
        public final static Property Count = new Property(5, Integer.class, "count", false, "COUNT");
        public final static Property PxPrinterInfoId = new Property(6, Long.class, "pxPrinterInfoId", false, "PX_PRINTER_INFO_ID");
    };

    private DaoSession daoSession;


    public PxProductConfigPlanDao(DaoConfig config) {
        super(config);
    }
    
    public PxProductConfigPlanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PxProductConfigPlan\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
                "\"OBJECT_ID\" TEXT," + // 1: objectId
                "\"NAME\" TEXT," + // 2: name
                "\"FLAG\" TEXT," + // 3: flag
                "\"DEL_FLAG\" TEXT," + // 4: delFlag
                "\"COUNT\" INTEGER," + // 5: count
                "\"PX_PRINTER_INFO_ID\" INTEGER);"); // 6: pxPrinterInfoId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PxProductConfigPlan\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PxProductConfigPlan entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(2, objectId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String flag = entity.getFlag();
        if (flag != null) {
            stmt.bindString(4, flag);
        }
 
        String delFlag = entity.getDelFlag();
        if (delFlag != null) {
            stmt.bindString(5, delFlag);
        }
 
        Integer count = entity.getCount();
        if (count != null) {
            stmt.bindLong(6, count);
        }
 
        Long pxPrinterInfoId = entity.getPxPrinterInfoId();
        if (pxPrinterInfoId != null) {
            stmt.bindLong(7, pxPrinterInfoId);
        }
    }

    @Override
    protected void attachEntity(PxProductConfigPlan entity) {
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
    public PxProductConfigPlan readEntity(Cursor cursor, int offset) {
        PxProductConfigPlan entity = new PxProductConfigPlan( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // objectId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // flag
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // delFlag
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // count
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6) // pxPrinterInfoId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PxProductConfigPlan entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFlag(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDelFlag(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCount(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setPxPrinterInfoId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PxProductConfigPlan entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PxProductConfigPlan entity) {
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
            SqlUtils.appendColumns(builder, "T0", daoSession.getPxPrinterInfoDao().getAllColumns());
            builder.append(" FROM PxProductConfigPlan T");
            builder.append(" LEFT JOIN PrinterInfo T0 ON T.\"PX_PRINTER_INFO_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected PxProductConfigPlan loadCurrentDeep(Cursor cursor, boolean lock) {
        PxProductConfigPlan entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        PxPrinterInfo dbPrinter = loadCurrentOther(daoSession.getPxPrinterInfoDao(), cursor, offset);
        entity.setDbPrinter(dbPrinter);

        return entity;    
    }

    public PxProductConfigPlan loadDeep(Long key) {
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
    public List<PxProductConfigPlan> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<PxProductConfigPlan> list = new ArrayList<PxProductConfigPlan>(count);
        
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
    
    protected List<PxProductConfigPlan> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<PxProductConfigPlan> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}