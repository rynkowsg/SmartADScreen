package cn.com.smartadscreen.model.bean;

import android.text.TextUtils;

import cn.com.smartadscreen.utils.JSONUtils;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Taro on 2017/3/19.
 * 下载任务实体类
 */
public class DownloadTask {

    // 下载路径
    private String url;
    // 本地文件地址
    private String localPath;
    // 下载文件名
    private String name;
    // 文件hash值
    private String hash;
    // hash2
    private String hash2;
    // 下载进度
    private int progress;
    // 错误码
    private String errorCode;
//    private JSONObject itemObj;

    private ContentItemBean itemBean;

    public DownloadTask(String url, String localPath, String name, String hash) {
        this.url = url;
        this.localPath = localPath;
        this.name = name;
        this.hash = hash;
    }

    public DownloadTask(String url, String localPath, String name, String hash, String hash2) {
        this.url = url;
        this.localPath = localPath;
        this.name = name;
        this.hash = hash;
        this.hash2 = hash2;
    }

//    public JSONObject getItemObj() {
//        return itemObj;
//    }
//
//    public void setItemObj(JSONObject itemObj) {
//        this.itemObj = itemObj;
//    }

    public ContentItemBean getItemBean() {
        return itemBean;
    }

    public void setItemBean(ContentItemBean itemBean) {
        this.itemBean = itemBean;
    }

    public String getHash2() {
        return hash2;
    }

    public void setHash2(String hash2) {
        this.hash2 = hash2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }



    /**
     * 判断是否为相同任务
     * 条件：url相同，或者 hash 值相同，或者 hash2 值相同
     * 原判断方法
         if (downloadTask.getUrl().equals(task.getUrl())) {
             return;
         }
         if (!TextUtils.isEmpty(downloadTask.getHash()) && downloadTask.getHash().equals(task.getHash())) {
             return;
         }
         if (!TextUtils.isEmpty(downloadTask.getHash2()) && downloadTask.getHash2().equals(task.getHash2())) {
             return;
         }
     */
    public boolean isSame(DownloadTask task) {
        if (this == task) {
            return true;
        }
        if (!isEmpty(url) && TextUtils.equals(url, task.getUrl())) {
            return true;
        }
        if (!isEmpty(hash) && !isEmpty(task.getHash())  && TextUtils.equals(this.hash, task.getHash())) {
            return true;
        }
        if (!isEmpty(hash2) && !isEmpty(task.getHash2()) && TextUtils.equals(this.hash2, task.getHash2())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this) + "\n";
    }
}
