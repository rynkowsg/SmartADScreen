package cn.com.startai.smartadh5.processlogic.entity.event;

/**
 * Created by Taro on 2017/5/24.
 * XX
 */
public class OnCommand {
    private int cmd;
    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }
}
