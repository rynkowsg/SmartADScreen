package cn.com.smartadscreen.model.bean;

/**
 * Created by Taro on 2017/3/22.
 * 下载任务完成通知类
 */
public class DownloadFinished {

    private String downloadKey;
    private String tag ;

    public DownloadFinished(String downloadKey, String tag) {
        this.downloadKey = downloadKey;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDownloadKey() {
        return downloadKey;
    }

    public void setDownloadKey(String downloadKey) {
        this.downloadKey = downloadKey;
    }
}
