package cn.com.smartadscreen.model.db.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.smartadscreen.model.db.entity.Screen;
import cn.com.smartadscreen.model.db.gen.DaoSession;
import cn.com.smartadscreen.model.db.gen.ScreenDao;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/5
 * 作用：播表中的某个 Screen 操作类辅助
 */

public final class ScreenHelper {

    private ScreenDao mScreenDao;

    private static class SingleInstance {
        private static final ScreenHelper INSTANCE = new ScreenHelper();
    }

    public static ScreenHelper getInstance() {
        return SingleInstance.INSTANCE;
    }

    private ScreenHelper() {
        DaoSession daoSession = DBManager.getDaoSession();
        mScreenDao = daoSession.getScreenDao();
    }

    /**
     * 根据播表 id 查询 Screen
     * @param btId
     * @return
     */
    public List<Screen> query(long btId){
        return mScreenDao.queryBuilder()
                .where(ScreenDao.Properties.BtId.eq(btId))
                .list();
    }

    /**
     * 根据播表 ID 查询所有 Screen 中最早结束的Screen
     */
    public Screen queryLastEndTime(long btId) {

        return mScreenDao.queryBuilder()
                .where(ScreenDao.Properties.BtId.eq(btId))
                .orderAsc(ScreenDao.Properties.End)
                .limit(1)
                .unique();
    }


    /**
     * 判断是否有播表即将播放
     */
    public boolean hasBroadWillPlay() {
        Long nowTime = System.currentTimeMillis() - 1000;
        Screen willPlayScreen = mScreenDao.queryBuilder()
                .where(ScreenDao.Properties.Start.ge(nowTime))
                .orderAsc((ScreenDao.Properties.Start))
                .limit(1)
                .build()
                .unique();

        return willPlayScreen != null
                && willPlayScreen.getStart().getTime() - nowTime <= 10 * 1000;
    }

    /**
     * 删除指定播表 Screen
     * @return 被删除的播表的id
     */
    void delete(List<Long> btId){
        List<Long> deleteIds = new ArrayList<>();
        List<Screen> needDeleteScreen = mScreenDao.queryBuilder()
                .where(ScreenDao.Properties.BtId.in(btId))
                .list();
        for (Screen screen : needDeleteScreen) {
            deleteIds.add(screen.getId());
        }
        mScreenDao.deleteByKeyInTx(deleteIds);
        AppHelper.getInstance().delete(deleteIds);
    }

    public void delete(Long... btIds){
        delete(Arrays.asList(btIds));
    }
}
