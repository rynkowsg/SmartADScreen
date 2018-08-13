package cn.com.smartadscreen.model.bean;


import cn.com.smartadscreen.utils.JSONUtils;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/21
 * 作用：播表中的内容 item 的实体类
 */

public class ContentItemBean {

    private String itemtype;        //内容类型  视频、音频、图片、文字mediaplayer
    private String hash;
    private String hash2;
    private String file;            //文件下载路径（url）
    private String name;            //文件名称
    private String volume;          //音量
    private String path;            //文件本地路径
    private String resolution;      //分辨率
    private String len;               //时长
    private String mLen;            //（时长）分钟数
    private String size;               //大小
    private int isWechat;           //是否是来自微信，1 是
    private String content;         //内容
    private String speed;           //速度

    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash2() {
        return hash2;
    }

    public void setHash2(String hash2) {
        this.hash2 = hash2;
    }


    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }

    public String getMLen() {
        return mLen;
    }

    public void setMLen(String mLen) {
        this.mLen = mLen;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getIsWechat() {
        return isWechat;
    }

    public void setIsWechat(int isWechat) {
        this.isWechat = isWechat;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String toJSONString() {
        return JSONUtils.toJSONString(this);
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}
