package cn.com.smartadscreen.model.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import cn.com.smartadscreen.model.db.entity.PlayInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PLAY_INFO".
*/
public class PlayInfoDao extends AbstractDao<PlayInfo, Long> {

    public static final String TABLENAME = "PLAY_INFO";

    /**
     * Properties of entity PlayInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type = new Property(1, Integer.class, "type", false, "TYPE");
        public final static Property Album = new Property(2, String.class, "album", false, "ALBUM");
        public final static Property Author = new Property(3, String.class, "author", false, "AUTHOR");
        public final static Property FileType = new Property(4, String.class, "fileType", false, "FILE_TYPE");
        public final static Property FilePath = new Property(5, String.class, "filePath", false, "FILE_PATH");
        public final static Property FileName = new Property(6, String.class, "fileName", false, "FILE_NAME");
        public final static Property FileId = new Property(7, Integer.class, "fileId", false, "FILE_ID");
        public final static Property FileSize = new Property(8, Long.class, "fileSize", false, "FILE_SIZE");
        public final static Property Duration = new Property(9, Long.class, "duration", false, "DURATION");
        public final static Property SourceType = new Property(10, Integer.class, "sourceType", false, "SOURCE_TYPE");
        public final static Property CoverPath = new Property(11, String.class, "coverPath", false, "COVER_PATH");
    }


    public PlayInfoDao(DaoConfig config) {
        super(config);
    }
    
    public PlayInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PLAY_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TYPE\" INTEGER," + // 1: type
                "\"ALBUM\" TEXT," + // 2: album
                "\"AUTHOR\" TEXT," + // 3: author
                "\"FILE_TYPE\" TEXT," + // 4: fileType
                "\"FILE_PATH\" TEXT," + // 5: filePath
                "\"FILE_NAME\" TEXT," + // 6: fileName
                "\"FILE_ID\" INTEGER," + // 7: fileId
                "\"FILE_SIZE\" INTEGER," + // 8: fileSize
                "\"DURATION\" INTEGER," + // 9: duration
                "\"SOURCE_TYPE\" INTEGER," + // 10: sourceType
                "\"COVER_PATH\" TEXT);"); // 11: coverPath
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PLAY_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PlayInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(2, type);
        }
 
        String album = entity.getAlbum();
        if (album != null) {
            stmt.bindString(3, album);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(4, author);
        }
 
        String fileType = entity.getFileType();
        if (fileType != null) {
            stmt.bindString(5, fileType);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(6, filePath);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(7, fileName);
        }
 
        Integer fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindLong(8, fileId);
        }
 
        Long fileSize = entity.getFileSize();
        if (fileSize != null) {
            stmt.bindLong(9, fileSize);
        }
 
        Long duration = entity.getDuration();
        if (duration != null) {
            stmt.bindLong(10, duration);
        }
 
        Integer sourceType = entity.getSourceType();
        if (sourceType != null) {
            stmt.bindLong(11, sourceType);
        }
 
        String coverPath = entity.getCoverPath();
        if (coverPath != null) {
            stmt.bindString(12, coverPath);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PlayInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(2, type);
        }
 
        String album = entity.getAlbum();
        if (album != null) {
            stmt.bindString(3, album);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(4, author);
        }
 
        String fileType = entity.getFileType();
        if (fileType != null) {
            stmt.bindString(5, fileType);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(6, filePath);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(7, fileName);
        }
 
        Integer fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindLong(8, fileId);
        }
 
        Long fileSize = entity.getFileSize();
        if (fileSize != null) {
            stmt.bindLong(9, fileSize);
        }
 
        Long duration = entity.getDuration();
        if (duration != null) {
            stmt.bindLong(10, duration);
        }
 
        Integer sourceType = entity.getSourceType();
        if (sourceType != null) {
            stmt.bindLong(11, sourceType);
        }
 
        String coverPath = entity.getCoverPath();
        if (coverPath != null) {
            stmt.bindString(12, coverPath);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PlayInfo readEntity(Cursor cursor, int offset) {
        PlayInfo entity = new PlayInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // type
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // album
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // author
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // fileType
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // filePath
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // fileName
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // fileId
            cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8), // fileSize
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9), // duration
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // sourceType
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // coverPath
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PlayInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setAlbum(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAuthor(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setFileType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setFilePath(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFileName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setFileId(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setFileSize(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
        entity.setDuration(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
        entity.setSourceType(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setCoverPath(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PlayInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PlayInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PlayInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
