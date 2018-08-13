package cn.com.smartadscreen.model.db.entity;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import cn.com.smartadscreen.model.bean.DownloadProgressBean;
import cn.com.smartadscreen.model.bean.DownloadTask;
import cn.com.startai.smartadh5.R;



/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/21
 * 作用：数据库 下载任务列表的实体类
 */
@Entity
public class DownloadTable {
    @Id(autoincrement = true)
    private Long id;
    private String url;
    private String size;
    private String fileName;            //文件名
    private String filePath;            //文件路径
    private String hash;
    private String hash2;
    private Integer progress;               //下载进度
    private Integer errorCode;              //错误码
    private Long addTime;//添加时间（毫秒值）
    private Integer origin;//来源 1 内网，2 外网






    @Generated(hash = 1043753921)
    public DownloadTable(Long id, String url, String size, String fileName,
            String filePath, String hash, String hash2, Integer progress,
            Integer errorCode, Long addTime, Integer origin) {
        this.id = id;
        this.url = url;
        this.size = size;
        this.fileName = fileName;
        this.filePath = filePath;
        this.hash = hash;
        this.hash2 = hash2;
        this.progress = progress;
        this.errorCode = errorCode;
        this.addTime = addTime;
        this.origin = origin;
    }

    @Generated(hash = 1974455446)
    public DownloadTable() {
    }






    public double getFileSizeDouble() {
        if(TextUtils.isEmpty(size))
            return -1;
        return Double.parseDouble(this.size);
    }

    public int getFileOriginDescribeResId(){
        if(this.origin == null) {
            return R.string.empty;
        } else {
            if(origin == 1) {
                return R.string.file_origin_inner;
            } else {
                return R.string.file_origin_internet;
            }
        }
    }



    public static DownloadTable convert(DownloadTask task) {
        DownloadTable downloadTable = new DownloadTable();
        downloadTable.setUrl(task.getUrl());
        downloadTable.setFileName(task.getName());
        downloadTable.setFilePath(task.getLocalPath());
        downloadTable.setHash(task.getHash());
        downloadTable.setHash2(task.getHash2());
        downloadTable.setProgress(0);
        if(task.getItemBean() != null) {
            long size = 0;
            try {
                size = Long.parseLong(task.getItemBean().getSize()) * 1024;
            } catch (NumberFormatException ignored) {
            }
            downloadTable.setSize(String.valueOf(size));
        }
        return downloadTable;
    }

    public static DownloadTable convert(DownloadProgressBean bean) {
        DownloadTable downloadTable = new DownloadTable();
        downloadTable.setUrl(bean.getUrl());
        downloadTable.setFileName(bean.getOriginName());
        downloadTable.setFilePath(bean.getLocalPath());
        downloadTable.setSize(String.valueOf(bean.getTotalSize()));
        downloadTable.setOrigin(bean.getOrigin());
        downloadTable.setProgress(0);
        return downloadTable;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash2() {
        return this.hash2;
    }

    public void setHash2(String hash2) {
        this.hash2 = hash2;
    }

    public Integer getProgress() {
        return this.progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Integer getOrigin() {
        return this.origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }
}
