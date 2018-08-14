package cn.com.smartadscreen.model.bean.event;

/**
 * Created by Taro on 2017/3/19.
 * 实体按键类
 */
public class HardwareKey {

    // 按键方向
    private int direction;

    public HardwareKey(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
