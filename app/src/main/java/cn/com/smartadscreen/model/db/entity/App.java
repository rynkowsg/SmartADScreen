package cn.com.smartadscreen.model.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
@Entity
public class App implements Serializable {
    
    static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    private Long id;
    // APP 父级 Screen ID
    private Long screenId;
    // APP ID
    private String aid;
    // APP items
    private String items;
    @Generated(hash = 896536885)
    public App(Long id, Long screenId, String aid, String items) {
        this.id = id;
        this.screenId = screenId;
        this.aid = aid;
        this.items = items;
    }
    @Generated(hash = 407064589)
    public App() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getScreenId() {
        return this.screenId;
    }
    public void setScreenId(Long screenId) {
        this.screenId = screenId;
    }
    public String getAid() {
        return this.aid;
    }
    public void setAid(String aid) {
        this.aid = aid;
    }
    public String getItems() {
        return this.items;
    }
    public void setItems(String items) {
        this.items = items;
    }
}
