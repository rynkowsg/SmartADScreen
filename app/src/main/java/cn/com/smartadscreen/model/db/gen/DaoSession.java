package cn.com.smartadscreen.model.db.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import cn.com.smartadscreen.model.db.entity.App;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.entity.DownloadTable;
import cn.com.smartadscreen.model.db.entity.FileMapping;
import cn.com.smartadscreen.model.db.entity.KNMapping;
import cn.com.smartadscreen.model.db.entity.PlayInfo;
import cn.com.smartadscreen.model.db.entity.Screen;
import cn.com.smartadscreen.model.db.entity.Service;

import cn.com.smartadscreen.model.db.gen.AppDao;
import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.gen.DownloadTableDao;
import cn.com.smartadscreen.model.db.gen.FileMappingDao;
import cn.com.smartadscreen.model.db.gen.KNMappingDao;
import cn.com.smartadscreen.model.db.gen.PlayInfoDao;
import cn.com.smartadscreen.model.db.gen.ScreenDao;
import cn.com.smartadscreen.model.db.gen.ServiceDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig appDaoConfig;
    private final DaoConfig broadcastTableDaoConfig;
    private final DaoConfig downloadTableDaoConfig;
    private final DaoConfig fileMappingDaoConfig;
    private final DaoConfig kNMappingDaoConfig;
    private final DaoConfig playInfoDaoConfig;
    private final DaoConfig screenDaoConfig;
    private final DaoConfig serviceDaoConfig;

    private final AppDao appDao;
    private final BroadcastTableDao broadcastTableDao;
    private final DownloadTableDao downloadTableDao;
    private final FileMappingDao fileMappingDao;
    private final KNMappingDao kNMappingDao;
    private final PlayInfoDao playInfoDao;
    private final ScreenDao screenDao;
    private final ServiceDao serviceDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        appDaoConfig = daoConfigMap.get(AppDao.class).clone();
        appDaoConfig.initIdentityScope(type);

        broadcastTableDaoConfig = daoConfigMap.get(BroadcastTableDao.class).clone();
        broadcastTableDaoConfig.initIdentityScope(type);

        downloadTableDaoConfig = daoConfigMap.get(DownloadTableDao.class).clone();
        downloadTableDaoConfig.initIdentityScope(type);

        fileMappingDaoConfig = daoConfigMap.get(FileMappingDao.class).clone();
        fileMappingDaoConfig.initIdentityScope(type);

        kNMappingDaoConfig = daoConfigMap.get(KNMappingDao.class).clone();
        kNMappingDaoConfig.initIdentityScope(type);

        playInfoDaoConfig = daoConfigMap.get(PlayInfoDao.class).clone();
        playInfoDaoConfig.initIdentityScope(type);

        screenDaoConfig = daoConfigMap.get(ScreenDao.class).clone();
        screenDaoConfig.initIdentityScope(type);

        serviceDaoConfig = daoConfigMap.get(ServiceDao.class).clone();
        serviceDaoConfig.initIdentityScope(type);

        appDao = new AppDao(appDaoConfig, this);
        broadcastTableDao = new BroadcastTableDao(broadcastTableDaoConfig, this);
        downloadTableDao = new DownloadTableDao(downloadTableDaoConfig, this);
        fileMappingDao = new FileMappingDao(fileMappingDaoConfig, this);
        kNMappingDao = new KNMappingDao(kNMappingDaoConfig, this);
        playInfoDao = new PlayInfoDao(playInfoDaoConfig, this);
        screenDao = new ScreenDao(screenDaoConfig, this);
        serviceDao = new ServiceDao(serviceDaoConfig, this);

        registerDao(App.class, appDao);
        registerDao(BroadcastTable.class, broadcastTableDao);
        registerDao(DownloadTable.class, downloadTableDao);
        registerDao(FileMapping.class, fileMappingDao);
        registerDao(KNMapping.class, kNMappingDao);
        registerDao(PlayInfo.class, playInfoDao);
        registerDao(Screen.class, screenDao);
        registerDao(Service.class, serviceDao);
    }
    
    public void clear() {
        appDaoConfig.clearIdentityScope();
        broadcastTableDaoConfig.clearIdentityScope();
        downloadTableDaoConfig.clearIdentityScope();
        fileMappingDaoConfig.clearIdentityScope();
        kNMappingDaoConfig.clearIdentityScope();
        playInfoDaoConfig.clearIdentityScope();
        screenDaoConfig.clearIdentityScope();
        serviceDaoConfig.clearIdentityScope();
    }

    public AppDao getAppDao() {
        return appDao;
    }

    public BroadcastTableDao getBroadcastTableDao() {
        return broadcastTableDao;
    }

    public DownloadTableDao getDownloadTableDao() {
        return downloadTableDao;
    }

    public FileMappingDao getFileMappingDao() {
        return fileMappingDao;
    }

    public KNMappingDao getKNMappingDao() {
        return kNMappingDao;
    }

    public PlayInfoDao getPlayInfoDao() {
        return playInfoDao;
    }

    public ScreenDao getScreenDao() {
        return screenDao;
    }

    public ServiceDao getServiceDao() {
        return serviceDao;
    }

}
