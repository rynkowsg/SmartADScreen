package cn.com.smartadscreen.model.db.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.com.smartadscreen.model.db.gen.BroadcastTableDao;
import cn.com.smartadscreen.model.db.gen.DaoSession;
import cn.com.smartadscreen.model.db.gen.ScreenDao;
/**
 * Created by Taro on 2017/3/14.
 * 数据库实体, 播表实体类
 */
@Entity
public class BroadcastTable implements Serializable {
    static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id;
    // 播表ID
    private String btId;
    //背景图片
    private String bg;
    // 播表Name
    private String name;
    // 播表分辨率
    private String resolution;
    // 播表appType
    private String appType;
    // 当前播表是否下载完成
    private boolean finished;
    // 当前播表的下载任务UUID Key
    private String downloadKey;
    // Json 字符串
    private String content;
    // 播表Logo
    private String logo;
    // 播表来历
    private String comeFrom;
    private Date createDate;
    private Date modifyDate;
    // 播表包含的Screens
    @ToMany(referencedJoinProperty = "btId")
    private List<Screen> screens;
    // timetype(时间类型（格式）)
    private String timeType;
    // 是否要延时播放
    private Boolean isNeedDelay ;




    public void resetFiled(BroadcastTable bt) {
        this.name = bt.getName();
        this.resolution = bt.getResolution();
        this.appType = bt.getAppType();
        this.finished = false;
        this.content = bt.getContent();
        this.modifyDate = bt.getModifyDate();
        this.screens = null;
        this.timeType = bt.getTimeType();

        this.isNeedDelay = bt.isNeedDelay();
    }

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 117836208)
    private transient BroadcastTableDao myDao;
    @Generated(hash = 518788031)
    public BroadcastTable(Long id, String btId, String bg, String name, String resolution, String appType, boolean finished,
            String downloadKey, String content, String logo, String comeFrom, Date createDate, Date modifyDate,
            String timeType, Boolean isNeedDelay) {
        this.id = id;
        this.btId = btId;
        this.bg = bg;
        this.name = name;
        this.resolution = resolution;
        this.appType = appType;
        this.finished = finished;
        this.downloadKey = downloadKey;
        this.content = content;
        this.logo = logo;
        this.comeFrom = comeFrom;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.timeType = timeType;
        this.isNeedDelay = isNeedDelay;
    }
    @Generated(hash = 226916496)
    public BroadcastTable() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBtId() {
        return this.btId;
    }
    public void setBtId(String btId) {
        this.btId = btId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getResolution() {
        return this.resolution;
    }
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    public String getAppType() {
        return this.appType;
    }
    public void setAppType(String appType) {
        this.appType = appType;
    }
    public boolean getFinished() {
        return this.finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    public String getDownloadKey() {
        return this.downloadKey;
    }
    public void setDownloadKey(String downloadKey) {
        this.downloadKey = downloadKey;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getLogo() {
        return this.logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }
    public String getComeFrom() {
        return this.comeFrom;
    }
    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }
    public Date getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Date getModifyDate() {
        return this.modifyDate;
    }
    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
    public String getTimeType() {
        return this.timeType;
    }
    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }
    public Boolean getIsNeedDelay() {
        return this.isNeedDelay;
    }
    public void setIsNeedDelay(Boolean isNeedDelay) {
        this.isNeedDelay = isNeedDelay;
    }
    public Boolean isNeedDelay() {
        return isNeedDelay;
    }

    public void setNeedDelay(Boolean needDelay) {
        isNeedDelay = needDelay;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1416762266)
    public List<Screen> getScreens() {
        if (screens == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ScreenDao targetDao = daoSession.getScreenDao();
            List<Screen> screensNew = targetDao._queryBroadcastTable_Screens(id);
            synchronized (this) {
                if (screens == null) {
                    screens = screensNew;
                }
            }
        }
        return screens;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1586289116)
    public synchronized void resetScreens() {
        screens = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 401132823)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBroadcastTableDao() : null;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BroadcastTable table = (BroadcastTable) o;

        if (finished != table.finished) return false;
        if (getId() != null ? !getId().equals(table.getId()) : table.getId() != null) return false;
        if (getBtId() != null ? !getBtId().equals(table.getBtId()) : table.getBtId() != null) return false;
        if (getName() != null ? !getName().equals(table.getName()) : table.getName() != null) return false;
        if (getResolution() != null ? !getResolution().equals(table.getResolution()) : table.getResolution() != null)
            return false;
        if (getAppType() != null ? !getAppType().equals(table.getAppType()) : table.getAppType() != null) return false;
        if (getDownloadKey() != null ? !getDownloadKey().equals(table.getDownloadKey()) : table.getDownloadKey() != null)
            return false;
        if (getContent() != null ? !getContent().equals(table.getContent()) : table.getContent() != null) return false;
        if (getLogo() != null ? !getLogo().equals(table.getLogo()) : table.getLogo() != null) return false;
        if (getComeFrom() != null ? !getComeFrom().equals(table.getComeFrom()) : table.getComeFrom() != null)
            return false;
        if (getCreateDate() != null ? !getCreateDate().equals(table.getCreateDate()) : table.getCreateDate() != null)
            return false;
        if (getModifyDate() != null ? !getModifyDate().equals(table.getModifyDate()) : table.getModifyDate() != null)
            return false;
        if (getScreens() != null ? !getScreens().equals(table.getScreens()) : table.getScreens() != null) return false;
        if (getTimeType() != null ? !getTimeType().equals(table.getTimeType()) : table.getTimeType() != null)
            return false;
        if (getIsNeedDelay() != null ? !getIsNeedDelay().equals(table.getIsNeedDelay()) : table.getIsNeedDelay() != null)
            return false;
        if (getBg() != null ? !getScreens().equals(table.getBg()) : table.getBg() != null)
            return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (btId != null ? btId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (resolution != null ? resolution.hashCode() : 0);
        result = 31 * result + (appType != null ? appType.hashCode() : 0);
        result = 31 * result + (finished ? 1 : 0);
        result = 31 * result + (downloadKey != null ? downloadKey.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (logo != null ? logo.hashCode() : 0);
        result = 31 * result + (comeFrom != null ? comeFrom.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (modifyDate != null ? modifyDate.hashCode() : 0);
        result = 31 * result + (screens != null ? screens.hashCode() : 0);
        result = 31 * result + (timeType != null ? timeType.hashCode() : 0);
        result = 31 * result + (isNeedDelay != null ? isNeedDelay.hashCode() : 0);
        result = 31 * result + (bg != null ? bg.hashCode() : 0);
        return result;
    }
    public String getBg() {
        return this.bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }
    
}
