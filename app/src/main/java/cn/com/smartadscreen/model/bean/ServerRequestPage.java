package cn.com.smartadscreen.model.bean;

/**
 * Created by Taro on 2017/3/24.
 * Server 请求 page 切换
 */
public class ServerRequestPage {

    // 按键方向
    private int direction;

    public ServerRequestPage(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
