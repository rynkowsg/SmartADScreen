package cn.com.smartadscreen.main.ui.web;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;

import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

import cn.com.smartadscreen.model.sp.SPManager;


/**
 * Created by Taro on 2017/4/1.
 * WebView CrossWalk 版本
 */
public class SmartXWalkView extends XWalkView {

    private static SmartXWalkView instance;
    // Load Javascript Prefix
    public static final String LOAD_JS_PREFIX = "javascript:$method";

    public static void setup(Activity activity){

        if (instance == null) {
            instance = new SmartXWalkView(activity);
            XWalkSettings settings = instance.getSettings();

            // 支持 JavaScript
            settings.setJavaScriptEnabled(true);

            //设置自适应屏幕，两者合用
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);

            //缩放操作
            settings.setSupportZoom(false);

            // 缓存
            settings.setDomStorageEnabled(false);
            settings.setDatabaseEnabled(false);

            //其他细节操作
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            settings.setAllowFileAccess(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(false);
            settings.setLoadsImagesAutomatically(true);

            // 设置WebView是否通过手势触发播放媒体，默认是true，需要手势触发
            settings.setMediaPlaybackRequiresUserGesture(false);

            instance.setLayerType(SPManager.getManager().getInt(SPManager.KEY_APP_WEBVIEW_LAYER_TYPE, 1), null);

//            instance.setWebChromeClient(new WebChromeClient());
            instance.addJavascriptInterface(new SmartXJsInterface(), "AndroidNative");
        }

    }

    public static SmartXWalkView getInstance(Activity activity){
        if (instance == null) {
            setup(activity);
        }
        return instance;
    }

    public SmartXWalkView(Context context) {
        super(context);
    }

    public SmartXWalkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartXWalkView(Context context, Activity activity) {
        super(context, activity);
    }

}
