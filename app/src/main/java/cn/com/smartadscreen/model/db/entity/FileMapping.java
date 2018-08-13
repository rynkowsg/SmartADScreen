package cn.com.smartadscreen.model.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Taro on 2017/4/12.
 * 播表下载的文件和 itemtype 描述文件的映射关系
 */
@Entity
public class FileMapping {
    
    @Id(autoincrement = true)
    private Long id;
    private String filePath;
    private String itemTypePath;
    @Generated(hash = 1398480561)
    public FileMapping(Long id, String filePath, String itemTypePath) {
        this.id = id;
        this.filePath = filePath;
        this.itemTypePath = itemTypePath;
    }
    @Generated(hash = 626955213)
    public FileMapping() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getItemTypePath() {
        return this.itemTypePath;
    }
    public void setItemTypePath(String itemTypePath) {
        this.itemTypePath = itemTypePath;
    }
  

    


}
