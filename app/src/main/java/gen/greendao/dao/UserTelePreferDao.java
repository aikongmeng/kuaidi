package gen.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import gen.greendao.bean.UserTelePrefer;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_TELE_PREFER".
*/
public class UserTelePreferDao extends AbstractDao<UserTelePrefer, String> {

    public static final String TABLENAME = "USER_TELE_PREFER";

    /**
     * Properties of entity UserTelePrefer.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property PhoneNumber = new Property(0, String.class, "phoneNumber", true, "PHONE_NUMBER");
        public final static Property TeleType = new Property(1, String.class, "teleType", false, "TELE_TYPE");
        public final static Property ShowWarn = new Property(2, Boolean.class, "showWarn", false, "SHOW_WARN");
    }


    public UserTelePreferDao(DaoConfig config) {
        super(config);
    }
    
    public UserTelePreferDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_TELE_PREFER\" (" + //
                "\"PHONE_NUMBER\" TEXT PRIMARY KEY NOT NULL ," + // 0: phoneNumber
                "\"TELE_TYPE\" TEXT NOT NULL ," + // 1: teleType
                "\"SHOW_WARN\" INTEGER);"); // 2: showWarn
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_USER_TELE_PREFER_PHONE_NUMBER ON USER_TELE_PREFER" +
                " (\"PHONE_NUMBER\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_TELE_PREFER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserTelePrefer entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getPhoneNumber());
        stmt.bindString(2, entity.getTeleType());
 
        Boolean showWarn = entity.getShowWarn();
        if (showWarn != null) {
            stmt.bindLong(3, showWarn ? 1L: 0L);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserTelePrefer entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getPhoneNumber());
        stmt.bindString(2, entity.getTeleType());
 
        Boolean showWarn = entity.getShowWarn();
        if (showWarn != null) {
            stmt.bindLong(3, showWarn ? 1L: 0L);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    @Override
    public UserTelePrefer readEntity(Cursor cursor, int offset) {
        UserTelePrefer entity = new UserTelePrefer( //
            cursor.getString(offset + 0), // phoneNumber
            cursor.getString(offset + 1), // teleType
            cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0 // showWarn
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserTelePrefer entity, int offset) {
        entity.setPhoneNumber(cursor.getString(offset + 0));
        entity.setTeleType(cursor.getString(offset + 1));
        entity.setShowWarn(cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0);
     }
    
    @Override
    protected final String updateKeyAfterInsert(UserTelePrefer entity, long rowId) {
        return entity.getPhoneNumber();
    }
    
    @Override
    public String getKey(UserTelePrefer entity) {
        if(entity != null) {
            return entity.getPhoneNumber();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserTelePrefer entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
