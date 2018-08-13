package cn.com.smartadscreen.model.bean;

/**
 * Created by Taro on 2017/3/24.
 * 回信实体类
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * _ooOoo_
 * o8888888o
 * 88" . "88
 * (| -_- |)
 *  O\ = /O
 * ___/`---'\____
 * .   ' \\| |// `.
 * / \\||| : |||// \
 * / _||||| -:- |||||- \
 * | | \\\ - /// | |
 * | \_| ''\---/'' | |
 * \ .-\__ `-` ___/-. /
 * ___`. .' /--.--\ `. . __
 * ."" '< `.___\_<|>_/___.' >'"".
 * | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * \ \ `-. \_ __\ /__ _/ .-` / /
 * ======`-.____`-.___\_____/___.-`____.-'======
 * `=---='
 *          .............................................
 *           佛曰：bug泛滥，我已瘫痪！
 */
public class ReportMsg {

    private int result;
    private JSONObject content;
    private JSONArray contentArray;
    private String downloadKey;

    public ReportMsg(int result, JSONObject content, String downloadKey) {
        this.result = result;
        this.content = content;
        this.downloadKey = downloadKey;
    }

    public ReportMsg(int result, JSONArray content, String downloadKey) {
        this.result = result;
        this.contentArray = content;
        this.downloadKey = downloadKey;
    }

    public ReportMsg() {
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public String getDownloadKey() {
        return downloadKey;
    }

    public void setDownloadKey(String downloadKey) {
        this.downloadKey = downloadKey;
    }

    public JSONArray getContentArray() {
        return contentArray;
    }

    public void setContentArray(JSONArray contentArray) {
        this.contentArray = contentArray;
    }
}
