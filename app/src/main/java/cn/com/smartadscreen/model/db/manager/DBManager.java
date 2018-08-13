package cn.com.smartadscreen.model.db.manager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;

import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.model.db.gen.DaoMaster;
import cn.com.smartadscreen.model.db.gen.DaoSession;


/**
 * Created by Taro on 2017/3/14.
 * 数据库管理类
 */
public final class DBManager {

    private static DaoSession mDaoSession ;
    private static DBManager instance ;

    public static void setupDatabase(){
        String dbName = "SMART_AD_H5.db";
//        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context,dbName);
        UpdateOpenHelper openHelper = new UpdateOpenHelper(Application.getContext(),dbName);
        Database db = openHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession(){
        return mDaoSession;
    }

    private DBManager(){
        EventBus.getDefault().register(this);
    }

}
