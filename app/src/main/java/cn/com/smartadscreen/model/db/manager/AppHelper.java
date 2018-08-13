package cn.com.smartadscreen.model.db.manager;

import java.util.List;

import cn.com.smartadscreen.model.db.entity.App;
import cn.com.smartadscreen.model.db.gen.AppDao;
import cn.com.smartadscreen.model.db.gen.DaoSession;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/21
 * 作用：数据库表 App 操作辅助类
 */

public class AppHelper {

    private final AppDao mAppDao;
    private final DaoSession mDaoSession;

    private static class SingleInstance {
        static final AppHelper INSTANCE = new AppHelper();
    }

    private AppHelper() {
        mDaoSession = DBManager.getDaoSession();
        mAppDao = mDaoSession.getAppDao();
    }

    public static AppHelper getInstance() {
        return SingleInstance.INSTANCE;
    }

    /**
     * 删除 Screen 中 关联的 Application 表中的数据
     */
    void delete(List<Long> screenIds) {
        List<App> needDeleteApp = mAppDao.queryBuilder()
                .where(AppDao.Properties.ScreenId.in(screenIds))
                .list();
        mAppDao.deleteInTx(needDeleteApp);
    }

    /**
     * 根据 Screen 的 id 查询 app
     */
    public List<App> queryByScreenId(long screenId) {
        return mAppDao.queryBuilder()
                .where(AppDao.Properties.ScreenId.eq(screenId))
                .list();
    }

}
