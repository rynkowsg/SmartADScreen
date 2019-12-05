package cn.com.smartadscreen.model.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Taro on 2017/3/24.
 * Miof 请求头实体类
 */
@Entity
public class Service implements Serializable {

    static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id ;

    private String requestId;
    private String method;
    private String downloadKey;
    @Generated(hash = 190490132)
    public Service(Long id, String requestId, String method, String downloadKey) {
        this.id = id;
        this.requestId = requestId;
        this.method = method;
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
    public String getRequestId() {
        return this.requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getMethod() {
        return this.method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getDownloadKey() {
        return this.downloadKey;
    }
    public void setDownloadKey(String downloadKey) {
        this.downloadKey = downloadKey;
    }

}
