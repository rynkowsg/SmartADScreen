package cn.com.smartadscreen.main.ui.web;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.facebook.stetho.common.LogUtil;

import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.sp.SPManager;


/**
 * Created by Taro on 2017/3/15.
 * WebView
 */
public class SmartWebView extends WebView {

    private OnScrollChangeListener mOnScrollChangeListener;

    private static SmartWebView instance;
    // Load Javascript Prefix
    public static final String LOAD_JS_PREFIX = "javascript:$method";

    public static void setup() {

//        if (instance == null) {
        instance = new SmartWebView(Config.getContext());
        WebSettings settings = instance.getSettings();

        // 支持 JavaScript
        settings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        //缩放操作
        settings.setSupportZoom(false);

        // 缓存
        settings.setAppCacheEnabled(false);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(false);

        //其他细节操作
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//关闭webview中缓存
        settings.setAllowFileAccess(true); //设置可以访问文件
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);//支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true);
        settings.setDefaultTextEncodingName("utf-8");

        // 设置WebView是否通过手势触发播放媒体，默认是true，需要手势触发
        settings.setMediaPlaybackRequiresUserGesture(false);

        instance.setLayerType(SPManager.getManager().getInt(SPManager.KEY_APP_WEBVIEW_LAYER_TYPE, 1), null);
        //复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
        instance.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        instance.setWebChromeClient(new WebChromeClient());
        instance.addJavascriptInterface(new SmartJsInterface(), "AndroidNative");
//        }

    }

    public static SmartWebView getInstance() {
        if (instance == null) {
            setup();
        }
        return instance;
    }

    private SmartWebView(Context context) {
        super(context);
    }

    private SmartWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private SmartWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        LogUtil.d("SmartWebView.onScroll", "jjjjjjjjjjjjjjjjjjjj");
        // webview的高度
        float webcontent = getContentHeight() * getScale();
        // 当前webview的高度
        float webnow = getHeight() + getScrollY();
        if (Math.abs(webcontent - webnow) < 1) {
            //处于底端
            mOnScrollChangeListener.onPageEnd(l, t, oldl, oldt);
        } else if (getScrollY() == 0) {
            //处于顶端
            mOnScrollChangeListener.onPageTop(l, t, oldl, oldt);
        } else {
            mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
        }

    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        this.mOnScrollChangeListener = listener;
    }

    public interface OnScrollChangeListener {

        void onPageEnd(int l, int t, int oldl, int oldt);

        void onPageTop(int l, int t, int oldl, int oldt);

        void onScrollChanged(int l, int t, int oldl, int oldt);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

}
