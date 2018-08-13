package cn.com.smartadscreen.model.db.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

import cn.com.smartadscreen.model.db.gen.AppDao;
import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.gen.DaoMaster;
import cn.com.smartadscreen.model.db.gen.DownloadTableDao;
import cn.com.smartadscreen.model.db.gen.FileMappingDao;
import cn.com.smartadscreen.model.db.gen.KNMappingDao;
import cn.com.smartadscreen.model.db.gen.ScreenDao;
import cn.com.smartadscreen.model.db.gen.ServiceDao;



/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/7/25
 * 作用：数据库升级处理类
 */

public class UpdateOpenHelper extends DaoMaster.OpenHelper {
    public UpdateOpenHelper(Context context, String name) {
        super(context, name);
    }

    public UpdateOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db, ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                }, AppDao.class, BroadcastTableDao.class, FileMappingDao.class,
                KNMappingDao.class, ScreenDao.class, ServiceDao.class, DownloadTableDao.class
                 );
    }
}
