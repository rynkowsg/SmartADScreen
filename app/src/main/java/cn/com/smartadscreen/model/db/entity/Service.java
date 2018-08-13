package cn.com.smartadscreen.model.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by Taro on 2017/3/24.
 * Miof 请求头实体类
 */
@Entity
public class Service implements Serializable {

    static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id ;

    private String msgcw;
    private String msgtype;
    private String fromid;
    private String toid;
    private long ts;
    private String msgid;
    private int result;
    private String downloadKey;
    @Generated(hash = 2030077840)
    public Service(Long id, String msgcw, String msgtype, String fromid,
            String toid, long ts, String msgid, int result, String downloadKey) {
        this.id = id;
        this.msgcw = msgcw;
        this.msgtype = msgtype;
        this.fromid = fromid;
        this.toid = toid;
        this.ts = ts;
        this.msgid = msgid;
        this.result = result;
        this.downloadKey = downloadKey;
    }
    @Generated(hash = 552382128)
    public Service() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMsgcw() {
        return this.msgcw;
    }
    public void setMsgcw(String msgcw) {
        this.msgcw = msgcw;
    }
    public String getMsgtype() {
        return this.msgtype;
    }
    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
    public String getFromid() {
        return this.fromid;
    }
    public void setFromid(String fromid) {
        this.fromid = fromid;
    }
    public String getToid() {
        return this.toid;
    }
    public void setToid(String toid) {
        this.toid = toid;
    }
    public long getTs() {
        return this.ts;
    }
    public void setTs(long ts) {
        this.ts = ts;
    }
    public String getMsgid() {
        return this.msgid;
    }
    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
    public int getResult() {
        return this.result;
    }
    public void setResult(int result) {
        this.result = result;
    }
    public String getDownloadKey() {
        return this.downloadKey;
    }
    public void setDownloadKey(String downloadKey) {
        this.downloadKey = downloadKey;
    }

  

}
