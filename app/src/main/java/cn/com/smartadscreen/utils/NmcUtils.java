package cn.com.smartadscreen.utils;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.model.bean.PlayStateChangeBean;
import cn.com.smartadscreen.model.bean.event.PlayListData;
import cn.com.smartadscreen.model.db.entity.PlayInfo;

import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/2/3
 *         作用：与 nmc 交互相关类
 */

public class NmcUtils {

    /**
     * 向 nmc 发送播放列表改变
     * @param type 当前类型
     * @param playList 播放列表
     */
    public static void sendToNmcPlayListChanged(int type, List<PlayInfo> playList){
        Logger.d("COMMUNICATE_TYPE_MEDIA_PLAYLIST_CHANGE    type ==> " + type );
        PlayListData data = new PlayListData(getPlayerTypeString(type), playList);
        StartaiCommunicate.getInstance().send(Application.getContext(),
                CommunicateType.COMMUNICATE_TYPE_MEDIA_PLAYLIST_CHANGE,
                data.toJsonString());
    }

    /**
     * 向 nmc 发送播放器的播放状态改变
     * @param fileId fileId
     * @param status 播放状态
     * @param progress 进度（百分比）
     */
    public static void sendToNmcPlayerStatusChanged(int fileId, int status, int progress){
        PlayStateChangeBean bean = new PlayStateChangeBean(status,
                fileId, progress);
        StartaiCommunicate.getInstance().send(Application.getContext(),
                CommunicateType.COMMUNICATE_TYPE_MEDIA_PLAYSTATUS_CHANGE,
                bean.toJsonString());
    }

    public static String getPlayerTypeString(int type) {
        String str;
        switch (type) {
            default:
            case PlayInfo.TYPE_VIDEO:
                str = PlayInfo.STR_TYPE_VIDEO;
                break;
            case PlayInfo.TYPE_MUSIC:
                str = PlayInfo.STR_TYPE_MUSIC;
                break;
            case PlayInfo.TYPE_IMAGE:
                str = PlayInfo.STR_TYPE_IMAGE;
                break;
        }
        return str;
    }
}
