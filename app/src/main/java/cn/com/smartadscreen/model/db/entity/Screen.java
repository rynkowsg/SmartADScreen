package cn.com.smartadscreen.model.db.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.com.smartadscreen.model.db.gen.AppDao;
import cn.com.smartadscreen.model.db.gen.DaoSession;
import cn.com.smartadscreen.model.db.gen.ScreenDao;
import cn.com.smartadscreen.utils.JSONUtils;

/**
 * Created by Taro on 2017/3/14.
 * 数据库实体类,Screen实体类
 */
@Entity
public class Screen implements Serializable {
    static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    private Long id;
    // 父级播表ID
    private Long btId;
    // Screen ID
    private String sid;
    private String times;
    // Screen 开始时间
    private Date start;
    // Screen 结束时间
    private Date end;
    // Screen 优先级
    private String priority;
    // Screen 类型
    private String utype;
    // Screen 占比
    private String size;
    // Screen 内部布局
    private String layout;
    // Screen 是否更新
    private boolean newLine;
    // Screen 内部 APP 关联
    private String appsRelation;
    // Json 字符串
    private String content;
    // Screen 子级 APPS
    @ToMany(referencedJoinProperty = "screenId")
    private List<App> apps;



    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }



    @Override
    public int hashCode() {
        int result = sid.hashCode();
        result = 31 * result + times.hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + priority.hashCode();
        result = 31 * result + utype.hashCode();
        result = 31 * result + size.hashCode();
        result = 31 * result + layout.hashCode();
        result = 31 * result + appsRelation.hashCode();
        return result;
    }
    public void resetFiled(Screen screen) {
        this.btId = screen.getBtId();
        this.sid = screen.getSid();
        this.times = screen.getTimes();
        this.start = screen.getStart();
        this.end = screen.getEnd();
        this.priority = screen.getPriority();
        this.utype = screen.getUtype();
        this.size = screen.getSize();
        this.layout = screen.getLayout();
        this.appsRelation = screen.getAppsRelation();
        this.content = screen.getContent();
        this.apps = null ;
        this.newLine = true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Screen)) return false;

        Screen screen = (Screen) o;

        if (btId != null ? !btId.equals(screen.btId) : screen.btId != null) return false;
        if (sid != null ? !sid.equals(screen.sid) : screen.sid != null) return false;
        if (times != null ? !times.equals(screen.times) : screen.times != null) return false;
        if (start != null ? !start.equals(screen.start) : screen.start != null) return false;
        if (end != null ? !end.equals(screen.end) : screen.end != null) return false;
        if (priority != null ? !priority.equals(screen.priority) : screen.priority != null)
            return false;
        if (utype != null ? !utype.equals(screen.utype) : screen.utype != null) return false;
        if (size != null ? !size.equals(screen.size) : screen.size != null) return false;
        if (layout != null ? !layout.equals(screen.layout) : screen.layout != null) return false;
        if (appsRelation != null ? !appsRelation.equals(screen.appsRelation) : screen.appsRelation != null)
            return false;
        return !(content != null ? !content.equals(screen.content) : screen.content != null);

    }


    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 371691035)
    private transient ScreenDao myDao;
    @Generated(hash = 1776441481)
    public Screen(Long id, Long btId, String sid, String times, Date start,
            Date end, String priority, String utype, String size, String layout,
            boolean newLine, String appsRelation, String content) {
        this.id = id;
        this.btId = btId;
        this.sid = sid;
        this.times = times;
        this.start = start;
        this.end = end;
        this.priority = priority;
        this.utype = utype;
        this.size = size;
        this.layout = layout;
        this.newLine = newLine;
        this.appsRelation = appsRelation;
        this.content = content;
    }
    @Generated(hash = 495490358)
    public Screen() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getBtId() {
        return this.btId;
    }
    public void setBtId(Long btId) {
        this.btId = btId;
    }
    public String getSid() {
        return this.sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public String getTimes() {
        return this.times;
    }
    public void setTimes(String times) {
        this.times = times;
    }
    public Date getStart() {
        return this.start;
    }
    public void setStart(Date start) {
        this.start = start;
    }
    public Date getEnd() {
        return this.end;
    }
    public void setEnd(Date end) {
        this.end = end;
    }
    public String getPriority() {
        return this.priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getUtype() {
        return this.utype;
    }
    public void setUtype(String utype) {
        this.utype = utype;
    }
    public String getSize() {
        return this.size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getLayout() {
        return this.layout;
    }
    public void setLayout(String layout) {
        this.layout = layout;
    }
    public boolean getNewLine() {
        return this.newLine;
    }
    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }
    public String getAppsRelation() {
        return this.appsRelation;
    }
    public void setAppsRelation(String appsRelation) {
        this.appsRelation = appsRelation;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 837347385)
    public List<App> getApps() {
        if (apps == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AppDao targetDao = daoSession.getAppDao();
            List<App> appsNew = targetDao._queryScreen_Apps(id);
            synchronized (this) {
                if (apps == null) {
                    apps = appsNew;
                }
            }
        }
        return apps;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1387798467)
    public synchronized void resetApps() {
        apps = null;
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
    @Generated(hash = 611995189)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getScreenDao() : null;
    }
}
