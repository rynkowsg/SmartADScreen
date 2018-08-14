package cn.com.smartadscreen.locallog.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Taro on 2017/3/14.
 * 本地存储日志实体类
 */
public class LogMsg implements Serializable {

    public static final String TYPE_RECEIVED = "Received 接收";
    public static final String TYPE_SEND = "Send 发送";
    public static final String TYPE_HANDLER = "Handler 自身处理";
    public static final String TYPE_EXCEPTION = "Exception 异常捕捉";
    public static final String TYPE_HTMLWRITE="HTML记录日志";

    // 日志创建时间
    private Date createDate;
    // 日志 Type: Received 接收 || Send 发送 || Handler 自身处理
    private String type;
    // 日志来源
    private String from;
    // 日志指向
    private String to;
    // 日志详细内容
    private String content;
    // 日志备注
    private ArrayList<String> remarks;

    public LogMsg(String type, String from, String to, String content) {
        this.createDate = new Date();
        this.type = type;
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public LogMsg(String type, String from, String to, String content, ArrayList<String> remarks) {
        this.createDate = new Date();
        this.type = type;
        this.from = from;
        this.to = to;
        this.content = content;
        this.remarks = remarks;
    }

    public LogMsg(String type, String from, String to, ArrayList<String> remarks) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.remarks = remarks;
    }

    public LogMsg(Date createDate, String type, String from, String to, String content) {
        this.createDate = createDate;
        this.type = type;
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public LogMsg(Date createDate, String type, String from, String to, String content, ArrayList<String> remarks) {
        this.createDate = createDate;
        this.type = type;
        this.from = from;
        this.to = to;
        this.content = content;
        this.remarks = remarks;
    }

    public Date getCreateDate() {
        if (createDate == null) {
            createDate = new Date();
        }
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        if (createDate == null) {
            createDate = new Date();
        } else {
            this.createDate = createDate;
        }
    }

    public String getType() {
        if (type == null) {
            type = "";
        }
        return type;
    }

    public void setType(String type) {
        if (type == null) {
            this.type = "";
        } else {
            this.type = type;
        }
    }

    public String getFrom() {
        if (from == null) {
            from = "";
        }
        return from;
    }

    public void setFrom(String from) {
        if (from == null) {
            this.from = "";
        } else {
            this.from = from;
        }
    }

    public String getTo() {
        if (to == null) {
            to = "";
        }
        return to;
    }

    public void setTo(String to) {
        if (to == null) {
            this.to = "";
        } else {
            this.to = to;
        }
    }

    public String getContent() {
        if (content == null) {
            content = "";
        }
        return content;
    }

    public void setContent(String content) {
        if (content == null) {
            this.content = "";
        } else {
            this.content = content;
        }
    }

    public ArrayList<String> getRemarks() {
        if (remarks == null) {
            remarks = new ArrayList<>();
        }
        return remarks;
    }

    public void setRemarks(ArrayList<String> remarks) {
        if (remarks == null || remarks.size() == 0) {
            this.remarks = new ArrayList<>();
        } else {
            this.remarks = remarks;
        }
    }
}
