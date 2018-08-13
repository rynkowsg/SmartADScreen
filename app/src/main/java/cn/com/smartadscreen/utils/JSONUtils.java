package cn.com.smartadscreen.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/15
 * 作用：JSON 处理工具类
 */

public class JSONUtils {

    public static <T> T parseObject(String jsonText, Class<T> clazz) {
        return JSON.parseObject(jsonText, clazz);
    }

    public static String toJSONString(Object obj) {
        return obj == null ? "" : JSON.toJSONString(obj);
    }

    public static JSONObject toJSONObject(Object obj) {
        return (JSONObject) JSON.toJSON(obj);
    }

}
