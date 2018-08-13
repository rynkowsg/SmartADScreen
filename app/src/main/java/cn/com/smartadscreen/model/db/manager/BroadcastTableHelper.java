package cn.com.smartadscreen.model.db.manager;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.entity.Screen;
import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.gen.DaoSession;
import cn.com.smartadscreen.model.db.gen.ScreenDao;
import cn.com.smartadscreen.model.sp.SPManager;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/7
 * 作用：播表操作辅助类
 */

public final class BroadcastTableHelper {

    private final BroadcastTableDao mBroadcastTableDao;
    private final DaoSession mDaoSession;

    private static class SingleInstance {
        static final BroadcastTableHelper INSTANCE = new BroadcastTableHelper();
    }

    private BroadcastTableHelper() {
        mDaoSession = DBManager.getDaoSession();
        mBroadcastTableDao = mDaoSession.getBroadcastTableDao();
    }

    public static BroadcastTableHelper getInstance() {
        return SingleInstance.INSTANCE;
    }

    public void insert(BroadcastTable bean) {
        mBroadcastTableDao.insert(bean);
    }

    /**
     * 查询所有播表
     */
    public List<BroadcastTable> queryAll() {
        return mBroadcastTableDao.loadAll();
    }

    /**
     * 根据 播表id 查询播表
     */
    public List<BroadcastTable> queryByBtId(String btId) {

        return mBroadcastTableDao.queryBuilder()
                .where(BroadcastTableDao.Properties.BtId.eq(btId))
                .list();
    }

    /**
     * 根据 ID 查询指定播表
     *
     * @param id
     * @return
     */
    public BroadcastTable queryById(long id) {
        return mBroadcastTableDao.load(id);
    }

    /**
     * 从所有需要延时推送的播表中获取 临近当前时间的播表（当前时间之前最近的一条播表）
     * <p>
     * 如果有置顶播表的话直接返回置顶播表
     *
     * @return 当前时间之前最近的一条播表id，或者返回置顶播表
     */
    public BroadcastTable getNearByBroadcastIdWhereIsNeedDelay(long queryTime) {

        List<Screen> allOutOfDataTables = mDaoSession.getScreenDao()
                .queryBuilder()
                .where(ScreenDao.Properties.End.lt(System.currentTimeMillis()))
                .list();

        //删除所有过时播表
        for (Screen outOfDataTable : allOutOfDataTables) {
            deleteById(outOfDataTable.getBtId());
        }

        long stickBtId = SPManager.getManager().getLong(SPManager.KEY_STICK_TOP_BT_ID, -1L);
        if (stickBtId != -1L) {
            BroadcastTable stickBtTable = queryById(stickBtId);
            if (stickBtTable != null) {
                return stickBtTable;
            }
        }

        List<BroadcastTable> needDelayPlays = mBroadcastTableDao.queryBuilder()
                .where(
                        BroadcastTableDao.Properties.IsNeedDelay.eq(true),
                        BroadcastTableDao.Properties.Finished.eq(true)
                )
                .build()
                .list();

        List<Long> ids = new ArrayList<>();

        for (BroadcastTable broadcastTable : needDelayPlays) {
            ids.add(broadcastTable.getId());
        }

        // 当前播放播表的播放时间，
        // 如果最近延时播表的开始时间早于当前播表播放时间，
        // 则认为当前的播表的播放时间晚于该延时播表，需要展示 KEY_CURRENT_BT_ID 对应的播表
        Long currentBtPlayTime = SPManager.getManager().getLong(SPManager.KEY_CURRENT_BT_PLAY_TIME, -1L);

        Screen screen = mDaoSession.getScreenDao()
                .queryBuilder()
                .where(
                        ScreenDao.Properties.Start.le(queryTime),
                        ScreenDao.Properties.Start.gt(currentBtPlayTime),
                        ScreenDao.Properties.BtId.in(ids)
                )
                .orderDesc(ScreenDao.Properties.Start)
                .limit(1)
                .unique();

        if (screen != null && screen.getEnd().getTime() > queryTime) {
            return queryById(screen.getBtId());
        } else {
            long currentId = SPManager.getInstance().getCurrentBtId();
            BroadcastTable currentTable = queryById(currentId);
            if (currentTable != null) {
                return currentTable;
            } else {
                return queryPreBroadcastTableById(queryLastId() + 1);
            }
        }

    }

    public long queryLastId() {
        BroadcastTable table = mBroadcastTableDao.queryBuilder()
                .orderDesc(BroadcastTableDao.Properties.Id)
                .limit(1)
                .build()
                .unique();

        if(table != null) {
            return table.getId();
        } else {
            return 0;
        }
    }

    /**
     * 查询所有需要延时播放的播表(已下载完成的播表)
     *
     * @return 所有需要延时播放的播表，即 开始时间大于当前时间
     */
    public List<BroadcastTable> queryAllDelayBroadcastTable(long queryTime) {

        QueryBuilder<Screen> queryBuilder = mDaoSession.getScreenDao()
                .queryBuilder();

        List<BroadcastTable> needDelayPlays = mBroadcastTableDao.queryBuilder()
                .where(
                        BroadcastTableDao.Properties.IsNeedDelay.eq(true),
                        BroadcastTableDao.Properties.Finished.eq(true)
                )
                .build()
                .list();

        List<Long> ids = new ArrayList<>();

        for (BroadcastTable broadcastTable : needDelayPlays) {
            ids.add(broadcastTable.getId());
        }

        for (long id : ids) {
            Screen screen = queryBuilder.where(
                    ScreenDao.Properties.Start.gt(queryTime),
                    ScreenDao.Properties.BtId.eq(id)
            )
                    .orderAsc(ScreenDao.Properties.Start)
                    .limit(1)
                    .unique();

            if (screen == null) {
                //screen == null 说明当前播表最早的开始时间早于当前时间
                Iterator<BroadcastTable> each = needDelayPlays.iterator();
                while (each.hasNext()) {
                    if (each.next().getId() == id) {
                        each.remove();
                        break;
                    }
                }

            }
        }

        return needDelayPlays;
    }

    /**
     * 查询所有未完成播表
     */
    public List<BroadcastTable> queryAllUnfinishedBroadcastTable() {
        return mBroadcastTableDao.queryBuilder()
                .where(BroadcastTableDao.Properties.Finished.eq(false))
                .list();
    }

    /**
     * 根据 ID 查询上一张播表（下载完成，并且开始时间小于当前时间）
     *
     * @return
     */
    public BroadcastTable queryPreBroadcastTableById(long id) {

        BroadcastTable leftNeedPlayBroadcast = null;

        long now = System.currentTimeMillis();

        Screen screen = mDaoSession.getScreenDao()
                .queryBuilder()
                .where(ScreenDao.Properties.BtId.lt(id))
                .orderDesc(ScreenDao.Properties.BtId)
                .where(ScreenDao.Properties.Start.lt(now), ScreenDao.Properties.End.gt(now))
                .limit(1)
                .unique();

        if (screen != null) {
            leftNeedPlayBroadcast = mBroadcastTableDao.load(screen.getBtId());

            if (leftNeedPlayBroadcast == null || !leftNeedPlayBroadcast.getFinished()) {
                return queryPreBroadcastTableById(screen.getBtId());
            }
        }

        return leftNeedPlayBroadcast;
    }


    /**
     * 根据 ID 查询下一张播表（下载完成，并且开始时间小于当前时间）
     *
     * @return
     */
    public BroadcastTable getNextBroadcastTableById(long id) {

        BroadcastTable nextNeedPlayBroadcast = null;

        long now = System.currentTimeMillis();

        Screen screen = mDaoSession.getScreenDao()
                .queryBuilder()
                .where(ScreenDao.Properties.BtId.gt(id))
                .orderAsc(ScreenDao.Properties.BtId)
                .where(ScreenDao.Properties.Start.lt(now))
                .limit(1)
                .unique();

        if (screen != null) {
            nextNeedPlayBroadcast = mBroadcastTableDao.load(screen.getBtId());

            if (nextNeedPlayBroadcast == null || !nextNeedPlayBroadcast.getFinished()) {
                return getNextBroadcastTableById(screen.getBtId());
            }
        }

        return nextNeedPlayBroadcast;
    }

    /**
     * 获取所有即将播放的 和 正在播放的 播表 和 所有未下载完成的播表
     */
    public List<BroadcastTable> getAllNeedUsedBroadcastTable() {

        List<BroadcastTable> allNeedUsedBroadcastTable = new ArrayList<>();

        //当前播放播表
        long currPlayId = SPManager.getInstance().getCurrentBtId();
        BroadcastTable currentPlayBt = queryById(currPlayId);
        if (currentPlayBt != null) allNeedUsedBroadcastTable.add(currentPlayBt);

        //所有已下载完成的延时播表
        allNeedUsedBroadcastTable.addAll(queryAllDelayBroadcastTable(System.currentTimeMillis()));

        //所有未下载完成的播表
        allNeedUsedBroadcastTable.addAll(queryAllUnfinishedBroadcastTable());

        return allNeedUsedBroadcastTable;
    }


    /**
     * 删除指定的播表
     */
    public void deleteBroadcastTable(List<BroadcastTable> tables) {
        mBroadcastTableDao.deleteInTx(tables);

        List<Long> deleteIds = new ArrayList<>();
        for (BroadcastTable table : tables) {
            deleteIds.add(table.getId());
        }
        ScreenHelper.getInstance().delete(deleteIds);
    }

    /**
     * 删除指定 id 下的播表
     */
    public void deleteById(long id) {
        mBroadcastTableDao.deleteByKey(id);
        ScreenHelper.getInstance().delete(id);
    }

    /**
     * 查询所有未完成播表
     */
    public List<BroadcastTable> queryAllUnFinished() {
        return mBroadcastTableDao.queryBuilder()
                .where(BroadcastTableDao.Properties.Finished.eq(false))
                .list();
    }

}
