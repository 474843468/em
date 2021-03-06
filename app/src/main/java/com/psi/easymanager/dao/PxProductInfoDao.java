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
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.psi.easymanager.module.PxProductCategory;

import com.psi.easymanager.module.PxProductInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ProductInfo".
*/
public class PxProductInfoDao extends AbstractDao<PxProductInfo, Long> {

    public static final String TABLENAME = "ProductInfo";

    /**
     * Properties of entity PxProductInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectId = new Property(1, String.class, "objectId", false, "OBJECT_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Py = new Property(3, String.class, "py", false, "PY");
        public final static Property Code = new Property(4, String.class, "code", false, "CODE");
        public final static Property Price = new Property(5, Double.class, "price", false, "PRICE");
        public final static Property VipPrice = new Property(6, Double.class, "vipPrice", false, "VIP_PRICE");
        public final static Property Unit = new Property(7, String.class, "unit", false, "UNIT");
        public final static Property MultipleUnit = new Property(8, String.class, "multipleUnit", false, "MULTIPLE_UNIT");
        public final static Property OrderUnit = new Property(9, String.class, "orderUnit", false, "ORDER_UNIT");
        public final static Property IsDiscount = new Property(10, String.class, "isDiscount", false, "IS_DISCOUNT");
        public final static Property IsGift = new Property(11, String.class, "isGift", false, "IS_GIFT");
        public final static Property IsPrint = new Property(12, String.class, "isPrint", false, "IS_PRINT");
        public final static Property ChangePrice = new Property(13, String.class, "changePrice", false, "CHANGE_PRICE");
        public final static Property Status = new Property(14, String.class, "status", false, "STATUS");
        public final static Property IsCustom = new Property(15, Boolean.class, "isCustom", false, "IS_CUSTOM");
        public final static Property DelFlag = new Property(16, String.class, "delFlag", false, "DEL_FLAG");
        public final static Property IsUpLoad = new Property(17, Boolean.class, "isUpLoad", false, "IS_UP_LOAD");
        public final static Property BarCode = new Property(18, String.class, "barCode", false, "BAR_CODE");
        public final static Property OverPlus = new Property(19, Double.class, "overPlus", false, "OVER_PLUS");
        public final static Property Type = new Property(20, String.class, "type", false, "TYPE");
        public final static Property Shelf = new Property(21, String.class, "shelf", false, "SHELF");
        public final static Property Visible = new Property(22, String.class, "visible", false, "VISIBLE");
        public final static Property SaleNum = new Property(23, Integer.class, "saleNum", false, "SALE_NUM");
        public final static Property IsLabel = new Property(24, String.class, "isLabel", false, "IS_LABEL");
        public final static Property ShortName = new Property(25, String.class, "shortName", false, "SHORT_NAME");
        public final static Property PxProductCategoryId = new Property(26, long.class, "pxProductCategoryId", false, "PX_PRODUCT_CATEGORY_ID");
    };

    private DaoSession daoSession;

    private Query<PxProductInfo> pxProductCategory_DbProductInfoListQuery;

    public PxProductInfoDao(DaoConfig config) {
        super(config);
    }
    
    public PxProductInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ProductInfo\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ASC ," + // 0: id
                "\"OBJECT_ID\" TEXT," + // 1: objectId
                "\"NAME\" TEXT," + // 2: name
                "\"PY\" TEXT," + // 3: py
                "\"CODE\" TEXT," + // 4: code
                "\"PRICE\" REAL," + // 5: price
                "\"VIP_PRICE\" REAL," + // 6: vipPrice
                "\"UNIT\" TEXT," + // 7: unit
                "\"MULTIPLE_UNIT\" TEXT," + // 8: multipleUnit
                "\"ORDER_UNIT\" TEXT," + // 9: orderUnit
                "\"IS_DISCOUNT\" TEXT," + // 10: isDiscount
                "\"IS_GIFT\" TEXT," + // 11: isGift
                "\"IS_PRINT\" TEXT," + // 12: isPrint
                "\"CHANGE_PRICE\" TEXT," + // 13: changePrice
                "\"STATUS\" TEXT," + // 14: status
                "\"IS_CUSTOM\" INTEGER," + // 15: isCustom
                "\"DEL_FLAG\" TEXT," + // 16: delFlag
                "\"IS_UP_LOAD\" INTEGER," + // 17: isUpLoad
                "\"BAR_CODE\" TEXT," + // 18: barCode
                "\"OVER_PLUS\" REAL," + // 19: overPlus
                "\"TYPE\" TEXT," + // 20: type
                "\"SHELF\" TEXT," + // 21: shelf
                "\"VISIBLE\" TEXT," + // 22: visible
                "\"SALE_NUM\" INTEGER," + // 23: saleNum
                "\"IS_LABEL\" TEXT," + // 24: isLabel
                "\"SHORT_NAME\" TEXT," + // 25: shortName
                "\"PX_PRODUCT_CATEGORY_ID\" INTEGER NOT NULL );"); // 26: pxProductCategoryId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ProductInfo\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PxProductInfo entity) {
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
 
        String py = entity.getPy();
        if (py != null) {
            stmt.bindString(4, py);
        }
 
        String code = entity.getCode();
        if (code != null) {
            stmt.bindString(5, code);
        }
 
        Double price = entity.getPrice();
        if (price != null) {
            stmt.bindDouble(6, price);
        }
 
        Double vipPrice = entity.getVipPrice();
        if (vipPrice != null) {
            stmt.bindDouble(7, vipPrice);
        }
 
        String unit = entity.getUnit();
        if (unit != null) {
            stmt.bindString(8, unit);
        }
 
        String multipleUnit = entity.getMultipleUnit();
        if (multipleUnit != null) {
            stmt.bindString(9, multipleUnit);
        }
 
        String orderUnit = entity.getOrderUnit();
        if (orderUnit != null) {
            stmt.bindString(10, orderUnit);
        }
 
        String isDiscount = entity.getIsDiscount();
        if (isDiscount != null) {
            stmt.bindString(11, isDiscount);
        }
 
        String isGift = entity.getIsGift();
        if (isGift != null) {
            stmt.bindString(12, isGift);
        }
 
        String isPrint = entity.getIsPrint();
        if (isPrint != null) {
            stmt.bindString(13, isPrint);
        }
 
        String changePrice = entity.getChangePrice();
        if (changePrice != null) {
            stmt.bindString(14, changePrice);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(15, status);
        }
 
        Boolean isCustom = entity.getIsCustom();
        if (isCustom != null) {
            stmt.bindLong(16, isCustom ? 1L: 0L);
        }
 
        String delFlag = entity.getDelFlag();
        if (delFlag != null) {
            stmt.bindString(17, delFlag);
        }
 
        Boolean isUpLoad = entity.getIsUpLoad();
        if (isUpLoad != null) {
            stmt.bindLong(18, isUpLoad ? 1L: 0L);
        }
 
        String barCode = entity.getBarCode();
        if (barCode != null) {
            stmt.bindString(19, barCode);
        }
 
        Double overPlus = entity.getOverPlus();
        if (overPlus != null) {
            stmt.bindDouble(20, overPlus);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(21, type);
        }
 
        String shelf = entity.getShelf();
        if (shelf != null) {
            stmt.bindString(22, shelf);
        }
 
        String visible = entity.getVisible();
        if (visible != null) {
            stmt.bindString(23, visible);
        }
 
        Integer saleNum = entity.getSaleNum();
        if (saleNum != null) {
            stmt.bindLong(24, saleNum);
        }
 
        String isLabel = entity.getIsLabel();
        if (isLabel != null) {
            stmt.bindString(25, isLabel);
        }
 
        String shortName = entity.getShortName();
        if (shortName != null) {
            stmt.bindString(26, shortName);
        }
        stmt.bindLong(27, entity.getPxProductCategoryId());
    }

    @Override
    protected void attachEntity(PxProductInfo entity) {
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
    public PxProductInfo readEntity(Cursor cursor, int offset) {
        PxProductInfo entity = new PxProductInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // objectId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // py
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // code
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // price
            cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6), // vipPrice
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // unit
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // multipleUnit
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // orderUnit
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // isDiscount
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // isGift
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // isPrint
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // changePrice
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // status
            cursor.isNull(offset + 15) ? null : cursor.getShort(offset + 15) != 0, // isCustom
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // delFlag
            cursor.isNull(offset + 17) ? null : cursor.getShort(offset + 17) != 0, // isUpLoad
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // barCode
            cursor.isNull(offset + 19) ? null : cursor.getDouble(offset + 19), // overPlus
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // type
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // shelf
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // visible
            cursor.isNull(offset + 23) ? null : cursor.getInt(offset + 23), // saleNum
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // isLabel
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // shortName
            cursor.getLong(offset + 26) // pxProductCategoryId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PxProductInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPy(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCode(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPrice(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setVipPrice(cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6));
        entity.setUnit(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setMultipleUnit(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setOrderUnit(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setIsDiscount(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setIsGift(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setIsPrint(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setChangePrice(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setStatus(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setIsCustom(cursor.isNull(offset + 15) ? null : cursor.getShort(offset + 15) != 0);
        entity.setDelFlag(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setIsUpLoad(cursor.isNull(offset + 17) ? null : cursor.getShort(offset + 17) != 0);
        entity.setBarCode(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setOverPlus(cursor.isNull(offset + 19) ? null : cursor.getDouble(offset + 19));
        entity.setType(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setShelf(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setVisible(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setSaleNum(cursor.isNull(offset + 23) ? null : cursor.getInt(offset + 23));
        entity.setIsLabel(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setShortName(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setPxProductCategoryId(cursor.getLong(offset + 26));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PxProductInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PxProductInfo entity) {
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
    
    /** Internal query to resolve the "dbProductInfoList" to-many relationship of PxProductCategory. */
    public List<PxProductInfo> _queryPxProductCategory_DbProductInfoList(long pxProductCategoryId) {
        synchronized (this) {
            if (pxProductCategory_DbProductInfoListQuery == null) {
                QueryBuilder<PxProductInfo> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.PxProductCategoryId.eq(null));
                pxProductCategory_DbProductInfoListQuery = queryBuilder.build();
            }
        }
        Query<PxProductInfo> query = pxProductCategory_DbProductInfoListQuery.forCurrentThread();
        query.setParameter(0, pxProductCategoryId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getPxProductCategoryDao().getAllColumns());
            builder.append(" FROM ProductInfo T");
            builder.append(" LEFT JOIN ProductCategory T0 ON T.\"PX_PRODUCT_CATEGORY_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected PxProductInfo loadCurrentDeep(Cursor cursor, boolean lock) {
        PxProductInfo entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        PxProductCategory dbCategory = loadCurrentOther(daoSession.getPxProductCategoryDao(), cursor, offset);
         if(dbCategory != null) {
            entity.setDbCategory(dbCategory);
        }

        return entity;    
    }

    public PxProductInfo loadDeep(Long key) {
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
    public List<PxProductInfo> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<PxProductInfo> list = new ArrayList<PxProductInfo>(count);
        
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
    
    protected List<PxProductInfo> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<PxProductInfo> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
