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

import com.psi.easymanager.module.PxExtraCharge;
import com.psi.easymanager.module.PxTableInfo;

import com.psi.easymanager.module.PxTableExtraRel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TableExtraRel".
*/
public class PxTableExtraRelDao extends AbstractDao<PxTableExtraRel, Long> {

    public static final String TABLENAME = "TableExtraRel";

    /**
     * Properties of entity PxTableExtraRel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectId = new Property(1, String.class, "objectId", false, "OBJECT_ID");
        public final static Property DelFlag = new Property(2, String.class, "delFlag", false, "DEL_FLAG");
        public final static Property PxTableInfoId = new Property(3, Long.class, "pxTableInfoId", false, "PX_TABLE_INFO_ID");
        public final static Property PxExtraChargeId = new Property(4, Long.class, "pxExtraChargeId", false, "PX_EXTRA_CHARGE_ID");
    };

    private DaoSession daoSession;


    public PxTableExtraRelDao(DaoConfig config) {
        super(config);
    }
    
    public PxTableExtraRelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TableExtraRel\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
                "\"OBJECT_ID\" TEXT," + // 1: objectId
                "\"DEL_FLAG\" TEXT," + // 2: delFlag
                "\"PX_TABLE_INFO_ID\" INTEGER," + // 3: pxTableInfoId
                "\"PX_EXTRA_CHARGE_ID\" INTEGER);"); // 4: pxExtraChargeId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TableExtraRel\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PxTableExtraRel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(2, objectId);
        }
 
        String delFlag = entity.getDelFlag();
        if (delFlag != null) {
            stmt.bindString(3, delFlag);
        }
 
        Long pxTableInfoId = entity.getPxTableInfoId();
        if (pxTableInfoId != null) {
            stmt.bindLong(4, pxTableInfoId);
        }
 
        Long pxExtraChargeId = entity.getPxExtraChargeId();
        if (pxExtraChargeId != null) {
            stmt.bindLong(5, pxExtraChargeId);
        }
    }

    @Override
    protected void attachEntity(PxTableExtraRel entity) {
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
    public PxTableExtraRel readEntity(Cursor cursor, int offset) {
        PxTableExtraRel entity = new PxTableExtraRel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // objectId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // delFlag
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // pxTableInfoId
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // pxExtraChargeId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PxTableExtraRel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDelFlag(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPxTableInfoId(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setPxExtraChargeId(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PxTableExtraRel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PxTableExtraRel entity) {
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
            SqlUtils.appendColumns(builder, "T0", daoSession.getPxTableInfoDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getPxExtraChargeDao().getAllColumns());
            builder.append(" FROM TableExtraRel T");
            builder.append(" LEFT JOIN TableInfo T0 ON T.\"PX_TABLE_INFO_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN ExtraCharge T1 ON T.\"PX_EXTRA_CHARGE_ID\"=T1.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected PxTableExtraRel loadCurrentDeep(Cursor cursor, boolean lock) {
        PxTableExtraRel entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        PxTableInfo dbTable = loadCurrentOther(daoSession.getPxTableInfoDao(), cursor, offset);
        entity.setDbTable(dbTable);
        offset += daoSession.getPxTableInfoDao().getAllColumns().length;

        PxExtraCharge dbExtraCharge = loadCurrentOther(daoSession.getPxExtraChargeDao(), cursor, offset);
        entity.setDbExtraCharge(dbExtraCharge);

        return entity;    
    }

    public PxTableExtraRel loadDeep(Long key) {
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
    public List<PxTableExtraRel> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<PxTableExtraRel> list = new ArrayList<PxTableExtraRel>(count);
        
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
    
    protected List<PxTableExtraRel> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<PxTableExtraRel> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
