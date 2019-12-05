package cn.com.smartadscreen.model.bean;

/**
 * Created by Taro on 2017/3/24.
 * 错误回信实体类
 */

import com.alibaba.fastjson.JSONObject;


public class ReportErrorMsg {

    private String error;
    private int code;
    private String requestId;

    private int result;
    private String toid;
    private long ts;
    private String msgcw;
    private String msgtype;
    private JSONObject content;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getMsgcw() {
        return msgcw;
    }

    public void setMsgcw(String msgcw) {
        this.msgcw = msgcw;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
}
