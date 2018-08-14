package cn.com.smartadscreen.utils;

import android.net.Uri;

import com.facebook.stetho.common.LogUtil;

/**
 * Created by chufeng on 2018/4/19.
 */

public class UriUtil {

    public static String parseUrl(String mUrl){
        String url = mUrl;
        Uri parse = Uri.parse(url);

        String scheme = parse.getScheme();

        LogUtil.v("live", "scheme:" + scheme + " url : " + url);

        if (scheme == null) {
            if (url.startsWith("//")) {
                url = "file:" + url;
            }else if (url.startsWith("/")) {
                url = "file:/" + url;
            }else {
                url = "file://" + url;
            }
        }

        parse = Uri.parse(url);
        scheme = parse.getScheme();

        LogUtil.v("live", "scheme:" + scheme + " url : " + url);

        return url;
    }

}
