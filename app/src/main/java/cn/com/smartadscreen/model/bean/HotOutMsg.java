package cn.com.smartadscreen.model.bean;

import java.io.Serializable;

/**
 * Created by Taro on 2017/3/15.
 * HotCodeUpdateModule 传递 HotCodeOut 动作实体类
 */
public class HotOutMsg implements Serializable {

    private boolean start;
    private boolean finish;
    // 0 代表开始, 100 代表结束
    private long progress;
    private String progressTip;

    public String getProgressTip() {
        return progressTip;
    }

    public void setProgressTip(String progressTip) {
        this.progressTip = progressTip;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }
}
