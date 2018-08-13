package cn.com.smartadscreen.model.db.manager;

import java.util.ArrayList;
import java.util.List;

import cn.com.smartadscreen.model.bean.DownloadProgressBean;
import cn.com.smartadscreen.model.db.entity.DownloadTable;
import cn.com.smartadscreen.model.db.gen.DaoSession;
import cn.com.smartadscreen.model.db.gen.DownloadTableDao;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/21
 * 作用：数据可下载列表操作类
 */

public final class DownloadTableHelper {

    private DownloadTableDao mDownloadTableDao;

    private static class SingleInstance {
        private static final DownloadTableHelper INSTANCE = new DownloadTableHelper();
    }

    public static DownloadTableHelper getInstance() {
        return SingleInstance.INSTANCE;
    }

    private DownloadTableHelper() {
        DaoSession daoSession = DBManager.getDaoSession();
        mDownloadTableDao = daoSession.getDownloadTableDao();
    }

    public void add(DownloadTable entity) {
        String keyUrl = entity.getUrl();
        DownloadTable table = queryByUrl(keyUrl);
        entity.setAddTime(System.currentTimeMillis());
        if (table != null) {
            entity.setId(table.getId());
            mDownloadTableDao.update(entity);
        } else {
            mDownloadTableDao.insert(entity);
        }
    }

    public void update(DownloadTable entity){
        mDownloadTableDao.update(entity);
    }

    /**
     * 修改下载进度
     *
     * @param bean   下载进度实体类
     * @return 是否修改成功
     */
    public boolean changeProgress(DownloadProgressBean bean) {
        DownloadTable table = queryByUrl(bean.getUrl());
        if (table != null) {
            table.setProgress(bean.getProgress());
            //设置来源
            if(table.getOrigin() == null || table.getOrigin() != bean.getOrigin()) {
                table.setOrigin(bean.getOrigin());
            }

            //如果 本地存储的 文件大小 与 进度中的文件大小不一致，一进度中的文件大小为准
            long fileSize = 0;
            try {
                fileSize = Long.parseLong(table.getSize());
            } catch (NumberFormatException ignored) {
            }
            if(fileSize != bean.getTotalSize()) {
                table.setSize(String.valueOf(bean.getTotalSize()));
            }
            mDownloadTableDao.update(table);
            return true;
        }
        return false;
    }

    /**
     * 下载失败，改变错误码 和 进度
     */
    public boolean changeError(DownloadProgressBean bean) {
        DownloadTable table = queryByUrl(bean.getUrl());
        if (table != null) {
            //设置来源
            if(table.getOrigin() == null || table.getOrigin() != bean.getOrigin()) {
                table.setOrigin(bean.getOrigin());
            }
            table.setProgress(-1);
            int errorCodeInt = 0;
            try {
                errorCodeInt = Integer.parseInt(bean.getErrorCode());
            } catch (NumberFormatException ignored) {
            }
            table.setErrorCode(errorCodeInt);
            mDownloadTableDao.update(table);
            return true;
        }
        return false;
    }

    public DownloadTable queryByUrl(String keyUrl) {
        return mDownloadTableDao.queryBuilder()
                .where(DownloadTableDao.Properties.Url.eq(keyUrl))
                .build()
                .unique();
    }


    /**
     * 查询下载列表
     * @return 10条下载失败 和 20条最近下载记录
     */
    public List<DownloadTable> queryDownloadList(){
        List<DownloadTable> list = new ArrayList<>();
        List<DownloadTable> tenFail = mDownloadTableDao.queryBuilder()
                .where(DownloadTableDao.Properties.Progress.eq(-1))
                .orderDesc(DownloadTableDao.Properties.AddTime)
                .limit(10)
                .build()
                .list();
        list.addAll(tenFail);

        List<DownloadTable> downloadList = mDownloadTableDao.queryBuilder()
                .orderDesc(DownloadTableDao.Properties.AddTime)
                .where(DownloadTableDao.Properties.Progress.gt(-1))
                .orderDesc(DownloadTableDao.Properties.AddTime)
                .limit(20)
                .build()
                .list();
        list.addAll(downloadList);
        return list;
    }


}
