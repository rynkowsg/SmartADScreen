package cn.com.smartadscreen.model.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Taro on 2017/3/27.
 * DownloadKey 与 Name 的对应关系表
 */
@Entity
public class KNMapping {

    @Id(autoincrement = true)
    private Long id ;
    private String downloadKey;
    private String name;
    @Generated(hash = 136603112)
    public KNMapping(Long id, String downloadKey, String name) {
        this.id = id;
        this.downloadKey = downloadKey;
        this.name = name;
    }
    @Generated(hash = 1861662029)
    public KNMapping() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDownloadKey() {
        return this.downloadKey;
    }
    public void setDownloadKey(String downloadKey) {
        this.downloadKey = downloadKey;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

 

}
