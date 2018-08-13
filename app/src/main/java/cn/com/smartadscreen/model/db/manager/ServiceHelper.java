package cn.com.smartadscreen.model.db.manager;


import cn.com.smartadscreen.model.db.entity.Service;
import cn.com.smartadscreen.model.db.gen.DaoSession;
import cn.com.smartadscreen.model.db.gen.ServiceDao;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/3/7
 *         作用：
 */

public class ServiceHelper {

    private ServiceDao mServiceDao;

    private static class SingleInstance {
        private static final ServiceHelper INSTANCE = new ServiceHelper();
    }

    public static ServiceHelper getInstance() {
        return SingleInstance.INSTANCE;
    }

    private ServiceHelper() {
        DaoSession daoSession = DBManager.getDaoSession();
        mServiceDao = daoSession.getServiceDao();
    }

    public void insertOrRelease(Service service){
        Service queryService = mServiceDao.queryBuilder()
                .where(ServiceDao.Properties.DownloadKey.eq(service.getDownloadKey()))
                .orderDesc(ServiceDao.Properties.Id)
                .limit(1)
                .unique();
        if(queryService != null) {
            service.setId(queryService.getId());
        }
        mServiceDao.insertOrReplace(service);
    }
}
