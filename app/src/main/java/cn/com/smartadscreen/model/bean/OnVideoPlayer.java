package cn.com.smartadscreen.model.bean;

import android.view.View;

/**
 * Created by Taro on 2017/4/14.
 * H5 请求原生播放视频
 */
public class OnVideoPlayer {

    private String videoId;
    private int x;
    private int y;
    private int width;
    private int height;
    private String path;
    private Boolean cancelIntent;

    private View view;
    private int currentPosition;
    private String callbackKey;

    private int tempX = -1;
    private int tempY = -1;
    private int tempWidth = -1;
    private int tempHeight = -1;

    public String getCallbackKey() {
        return callbackKey;
    }

    public void setCallbackKey(String callbackKey) {
        this.callbackKey = callbackKey;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getCancelIntent() {
        return cancelIntent;
    }

    public void setCancelIntent(Boolean cancelIntent) {
        this.cancelIntent = cancelIntent;
    }

    public int getTempX() {
        return tempX;
    }

    public void setTempX(int tempX) {
        this.tempX = tempX;
    }

    public int getTempY() {
        return tempY;
    }

    public void setTempY(int tempY) {
        this.tempY = tempY;
    }

    public int getTempWidth() {
        return tempWidth;
    }

    public void setTempWidth(int tempWidth) {
        this.tempWidth = tempWidth;
    }

    public int getTempHeight() {
        return tempHeight;
    }

    public void setTempHeight(int tempHeight) {
        this.tempHeight = tempHeight;
    }

    @Override
    public String toString() {
        return "OnVideoPlayer{" +
                "videoId='" + videoId + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", path='" + path + '\'' +
                ", cancelIntent=" + cancelIntent +
                ", view=" + view +
                ", currentPosition=" + currentPosition +
                ", callbackKey='" + callbackKey + '\'' +
                '}';
    }
}
