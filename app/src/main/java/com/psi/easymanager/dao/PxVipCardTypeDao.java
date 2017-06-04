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

import com.psi.easymanager.module.PxDiscounScheme;

import com.psi.easymanager.module.PxVipCardType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "VipCardType".
*/
public class PxVipCardTypeDao extends AbstractDao<PxVipCardType, Long> {

    public static final String TABLENAME = "VipCardType";

    /**
     * Properties of entity PxVipCardType.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectId = new Property(1, String.class, "objectId", false, "OBJECT_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Type = new Property(3, String.class, "type", false, "TYPE");
        public final static Property MarginForegift = new Property(4, Integer.class, "marginForegift", false, "MARGIN_FOREGIFT");
        public final static Property RechargeScore = new Property(5, Integer.class, "rechargeScore", false, "RECHARGE_SCORE");
        public final static Property RequirePassword = new Property(6, String.class, "requirePassword", false, "REQUIRE_PASSWORD");
        public final static Property ConsumeSendScore = new Property(7, String.class, "consumeSendScore", false, "CONSUME_SEND_SCORE");
        public final static Property DiscountRate = new Property(8, Integer.class, "discountRate", false, "DISCOUNT_RATE");
        public final static Property ConsumeScore = new Property(9, Integer.class, "consumeScore", false, "CONSUME_SCORE");
        public final static Property DelFlag = new Property(10, String.class, "delFlag", false, "DEL_FLAG");
        public final static Property PxDiscounSchemeId = new Property(11, Long.class, "pxDiscounSchemeId", false, "PX_DISCOUN_SCHEME_ID");
    };

    private DaoSession daoSession;


    public PxVipCardTypeDao(DaoConfig config) {
        super(config);
    }
    
    public PxVipCardTypeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"VipCardType\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
                "\"OBJECT_ID\" TEXT," + // 1: objectId
                "\"NAME\" TEXT," + // 2: name
                "\"TYPE\" TEXT," + // 3: type
                "\"MARGIN_FOREGIFT\" INTEGER," + // 4: marginForegift
                "\"RECHARGE_SCORE\" INTEGER," + // 5: rechargeScore
                "\"REQUIRE_PASSWORD\" TEXT," + // 6: requirePassword
                "\"CONSUME_SEND_SCORE\" TEXT," + // 7: consumeSendScore
                "\"DISCOUNT_RATE\" INTEGER," + // 8: discountRate
                "\"CONSUME_SCORE\" INTEGER," + // 9: consumeScore
                "\"DEL_FLAG\" TEXT," + // 10: delFlag
                "\"PX_DISCOUN_SCHEME_ID\" INTEGER);"); // 11: pxDiscounSchemeId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"VipCardType\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PxVipCardType entity) {
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
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(4, type);
        }
 
        Integer marginForegift = entity.getMarginForegift();
        if (marginForegift != null) {
            stmt.bindLong(5, marginForegift);
        }
 
        Integer rechargeScore = entity.getRechargeScore();
        if (rechargeScore != null) {
            stmt.bindLong(6, rechargeScore);
        }
 
        String requirePassword = entity.getRequirePassword();
        if (requirePassword != null) {
            stmt.bindString(7, requirePassword);
        }
 
        String consumeSendScore = entity.getConsumeSendScore();
        if (consumeSendScore != null) {
            stmt.bindString(8, consumeSendScore);
        }
 
        Integer discountRate = entity.getDiscountRate();
        if (discountRate != null) {
            stmt.bindLong(9, discountRate);
        }
 
        Integer consumeScore = entity.getConsumeScore();
        if (consumeScore != null) {
            stmt.bindLong(10, consumeScore);
        }
 
        String delFlag = entity.getDelFlag();
        if (delFlag != null) {
            stmt.bindString(11, delFlag);
        }
 
        Long pxDiscounSchemeId = entity.getPxDiscounSchemeId();
        if (pxDiscounSchemeId != null) {
            stmt.bindLong(12, pxDiscounSchemeId);
        }
    }

    @Override
    protected void attachEntity(PxVipCardType entity) {
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
    public PxVipCardType readEntity(Cursor cursor, int offset) {
        PxVipCardType entity = new PxVipCardType( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // objectId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // marginForegift
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // rechargeScore
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // requirePassword
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // consumeSendScore
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // discountRate
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // consumeScore
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // delFlag
            cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11) // pxDiscounSchemeId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PxVipCardType entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMarginForegift(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setRechargeScore(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setRequirePassword(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setConsumeSendScore(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDiscountRate(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setConsumeScore(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setDelFlag(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setPxDiscounSchemeId(cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PxVipCardType entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PxVipCardType entity) {
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
            SqlUtils.appendColumns(builder, "T0", daoSession.getPxDiscounSchemeDao().getAllColumns());
            builder.append(" FROM VipCardType T");
            builder.append(" LEFT JOIN DiscounScheme T0 ON T.\"PX_DISCOUN_SCHEME_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected PxVipCardType loadCurrentDeep(Cursor cursor, boolean lock) {
        PxVipCardType entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        PxDiscounScheme dbDiscounScheme = loadCurrentOther(daoSession.getPxDiscounSchemeDao(), cursor, offset);
        entity.setDbDiscounScheme(dbDiscounScheme);

        return entity;    
    }

    public PxVipCardType loadDeep(Long key) {
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
    public List<PxVipCardType> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<PxVipCardType> list = new ArrayList<PxVipCardType>(count);
        
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
    
    protected List<PxVipCardType> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<PxVipCardType> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
