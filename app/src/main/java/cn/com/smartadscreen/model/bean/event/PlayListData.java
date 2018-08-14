package cn.com.smartadscreen.model.bean.event;


import java.util.List;

import cn.com.smartadscreen.model.db.entity.PlayInfo;
import cn.com.smartadscreen.utils.JSONUtils;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/23
 *         作用：
 */

public class PlayListData {



    private int isReplace;
    private String fileType;
    private List<PlayInfo> playList;

    public PlayListData() {
    }

    public PlayListData(String fileType, List<PlayInfo> playList) {
        this.fileType = fileType;
        this.playList = playList;
    }

    public int getIsReplace() {
        return isReplace;
    }

    public void setIsReplace(int isReplace) {
        this.isReplace = isReplace;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<PlayInfo> getPlayList() {
        return playList;
    }

    public void setPlayList(List<PlayInfo> playList) {
        this.playList = playList;
    }

    public String toJsonString(){
        return JSONUtils.toJSONString(this);
    }

}
