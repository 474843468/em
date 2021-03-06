package com.psi.easymanager.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.psi.easymanager.module.PxProductPic;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ProductPic".
*/
public class PxProductPicDao extends AbstractDao<PxProductPic, Long> {

    public static final String TABLENAME = "ProductPic";

    /**
     * Properties of entity PxProductPic.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ObjectId = new Property(1, String.class, "objectId", false, "OBJECT_ID");
        public final static Property ProductId = new Property(2, String.class, "productId", false, "PRODUCT_ID");
        public final static Property ImagePath = new Property(3, String.class, "imagePath", false, "IMAGE_PATH");
        public final static Property Code = new Property(4, String.class, "code", false, "CODE");
        public final static Property Name = new Property(5, String.class, "name", false, "NAME");
        public final static Property Format = new Property(6, String.class, "format", false, "FORMAT");
        public final static Property Size = new Property(7, Long.class, "size", false, "SIZE");
    };


    public PxProductPicDao(DaoConfig config) {
        super(config);
    }
    
    public PxProductPicDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ProductPic\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"OBJECT_ID\" TEXT," + // 1: objectId
                "\"PRODUCT_ID\" TEXT," + // 2: productId
                "\"IMAGE_PATH\" TEXT," + // 3: imagePath
                "\"CODE\" TEXT," + // 4: code
                "\"NAME\" TEXT," + // 5: name
                "\"FORMAT\" TEXT," + // 6: format
                "\"SIZE\" INTEGER);"); // 7: size
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ProductPic\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PxProductPic entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(2, objectId);
        }
 
        String productId = entity.getProductId();
        if (productId != null) {
            stmt.bindString(3, productId);
        }
 
        String imagePath = entity.getImagePath();
        if (imagePath != null) {
            stmt.bindString(4, imagePath);
        }
 
        String code = entity.getCode();
        if (code != null) {
            stmt.bindString(5, code);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(6, name);
        }
 
        String format = entity.getFormat();
        if (format != null) {
            stmt.bindString(7, format);
        }
 
        Long size = entity.getSize();
        if (size != null) {
            stmt.bindLong(8, size);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PxProductPic readEntity(Cursor cursor, int offset) {
        PxProductPic entity = new PxProductPic( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // objectId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // productId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // imagePath
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // code
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // name
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // format
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // size
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PxProductPic entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setObjectId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setProductId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setImagePath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCode(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFormat(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSize(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PxProductPic entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PxProductPic entity) {
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
