package cn.com.startai.smartadh5.processlogic.entity.event;

import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;

/**
 * Created by Taro on 2017/4/26.
 * H5 请求原生跑马灯
 */
public class OnTextPlayer {

    private String textId;
    private int x;
    private int y;
    private int width;
    private int height;
    private String content;
    private long len;
    private String color;
    private String backgroundColor;
    private int size = 100;
    private int speed = 50;
    private Boolean cancelIntent;
    private String callbackKey;
    private View view;

    private int tempX = -1;
    private int tempY = -1;
    private int tempWidth = -1;
    private int tempHeight = -1;
    private int tempSize = -1;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getTextId() {
        return textId;
    }

    public void setTextId(String textId) {
        this.textId = textId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }

    public Boolean getCancelIntent() {
        return cancelIntent;
    }

    public void setCancelIntent(Boolean cancelIntent) {
        this.cancelIntent = cancelIntent;
    }

    public String getCallbackKey() {
        return callbackKey;
    }

    public void setCallbackKey(String callbackKey) {
        this.callbackKey = callbackKey;
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

    public int getTempSize() {
        return tempSize;
    }

    public void setTempSize(int tempSize) {
        this.tempSize = tempSize;
    }

    @Override
    public String toString() {
        return "OnTextPlayer{" +
                "textId='" + textId + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", content='" + content + '\'' +
                ", len=" + len +
                ", color='" + color + '\'' +
                ", backgroundColor￥￥￥='" + backgroundColor + '\'' +
                ", size=" + size +
                ", speed=" + speed +
                ", cancelIntent=" + cancelIntent +
                ", callbackKey='" + callbackKey + '\'' +
                ", view=" + view +
                '}';
    }
}
