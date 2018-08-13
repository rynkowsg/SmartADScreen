package cn.com.smartadscreen.model.db.gen;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import cn.com.smartadscreen.model.db.entity.App;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "APP".
*/
public class AppDao extends AbstractDao<App, Long> {

    public static final String TABLENAME = "APP";

    /**
     * Properties of entity App.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ScreenId = new Property(1, Long.class, "screenId", false, "SCREEN_ID");
        public final static Property Aid = new Property(2, String.class, "aid", false, "AID");
        public final static Property Items = new Property(3, String.class, "items", false, "ITEMS");
    }

    private Query<App> screen_AppsQuery;

    public AppDao(DaoConfig config) {
        super(config);
    }
    
    public AppDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"APP\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SCREEN_ID\" INTEGER," + // 1: screenId
                "\"AID\" TEXT," + // 2: aid
                "\"ITEMS\" TEXT);"); // 3: items
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APP\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, App entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long screenId = entity.getScreenId();
        if (screenId != null) {
            stmt.bindLong(2, screenId);
        }
 
        String aid = entity.getAid();
        if (aid != null) {
            stmt.bindString(3, aid);
        }
 
        String items = entity.getItems();
        if (items != null) {
            stmt.bindString(4, items);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, App entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long screenId = entity.getScreenId();
        if (screenId != null) {
            stmt.bindLong(2, screenId);
        }
 
        String aid = entity.getAid();
        if (aid != null) {
            stmt.bindString(3, aid);
        }
 
        String items = entity.getItems();
        if (items != null) {
            stmt.bindString(4, items);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public App readEntity(Cursor cursor, int offset) {
        App entity = new App( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // screenId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // aid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // items
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, App entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setScreenId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setAid(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setItems(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(App entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(App entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(App entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "apps" to-many relationship of Screen. */
    public List<App> _queryScreen_Apps(Long screenId) {
        synchronized (this) {
            if (screen_AppsQuery == null) {
                QueryBuilder<App> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ScreenId.eq(null));
                screen_AppsQuery = queryBuilder.build();
            }
        }
        Query<App> query = screen_AppsQuery.forCurrentThread();
        query.setParameter(0, screenId);
        return query.list();
    }

}
