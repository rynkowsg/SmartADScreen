package cn.com.startai.smartadh5.processlogic.entity.bean;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/7/14
 * 作用：NMC 发送删播表指令时 EventBus 中需要用的实体类
 */

public class BroadcastTableDeleteBean {

    private String id;//播表id

    public BroadcastTableDeleteBean(){}

    public BroadcastTableDeleteBean(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
