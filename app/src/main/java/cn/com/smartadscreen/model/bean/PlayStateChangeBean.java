package cn.com.smartadscreen.model.bean;


import cn.com.smartadscreen.utils.JSONUtils;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/24
 *         作用：视频、音频、图片 播放状态改变
 */

public class PlayStateChangeBean {

    public static final int STATUS_PLAY = 1;
    public static final int STATUS_PAUSE = 0;

    //状态
    private int status;
    //文件 id
    private int fileId;
    //播放进度
    private int progress;

    public PlayStateChangeBean() {
    }

    public PlayStateChangeBean(int status, int fileId, int progress) {
        this.status = status;
        this.fileId = fileId;
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String toJsonString(){
        return JSONUtils.toJSONString(this);
    }
}
