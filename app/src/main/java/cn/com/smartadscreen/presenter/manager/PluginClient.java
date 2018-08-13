package cn.com.smartadscreen.presenter.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.facebook.stetho.common.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * Created by Taro on 2017/4/5.
 * PluginClient 管理类
 */
public class PluginClient {

    private static final String TAG = "PluginClient";
    public static final String ACTION_SERVER_PAUSE = "ACTION_SERVER_PAUSE";
    public static final String ACTION_PLUGIN_LOAD = "ACTION_PLUGIN_LOAD";
    public static final String ACTION_PLUGIN_SEND_MESSAGE = "cn.com.startai.smartad.plugin.message.from";

    public static final String EXTRA_PLUGIN_MESSAGE_CODE = "message_code";
    public static final String EXTRA_PLUGIN_MESSAGE_REPORT_ID = "message_report_id";
    public static final String EXTRA_PLUGIN_MESSAGE_CONTENT = "message_content";

    private static PluginClient instance;
    private ViewManager viewManagerInstance;
    private MessageManager messageManagerInstance;

    private PluginClient() {}

    public static PluginClient getInstance() {
        if (instance == null) {
            synchronized (PluginClient.class) {
                if (instance ==null) {
                    instance = new PluginClient();
                }
            }
        }
        return instance;
    }

    public void load(Context context) {
        Intent intent = new Intent(ACTION_PLUGIN_LOAD);
        context.getApplicationContext().sendBroadcast(intent);
    }

    public ViewManager getViewManager(Context context) {
        if (viewManagerInstance == null) {
            viewManagerInstance = new ViewManager(context);
        }
        return viewManagerInstance;
    }

    public MessageManager getMessageManager(Context context) {
        if (messageManagerInstance == null) {
            messageManagerInstance = new MessageManager(context);
        }
        return messageManagerInstance;
    }

    public class MessageManager {

        private Context mContext;

        private MessageManager(Context context) {
            mContext = context.getApplicationContext();
        }

        public void sendMessage(int code, String message){
            Intent intent = new Intent(ACTION_PLUGIN_SEND_MESSAGE);
            intent.putExtra(EXTRA_PLUGIN_MESSAGE_CODE, code);
            intent.putExtra(EXTRA_PLUGIN_MESSAGE_CONTENT, message);
            mContext.sendBroadcast(intent);
        }

        public void sendMessage(int code, String message, String reportId){
            Intent intent = new Intent(ACTION_PLUGIN_SEND_MESSAGE);
            intent.putExtra(EXTRA_PLUGIN_MESSAGE_CODE, code);
            intent.putExtra(EXTRA_PLUGIN_MESSAGE_CONTENT, message);
            intent.putExtra(EXTRA_PLUGIN_MESSAGE_REPORT_ID, reportId);
            mContext.sendBroadcast(intent);
        }

    }

    public class ViewManager {

        public static final int VIEW_ANIMATION_NULL = -1;

        private Context mContext;
        private LayoutInflater mInflater;
        private Map<String, View> mViewMap;
        private Handler mHandler;
        private WindowManager mWindowManager;
        private int mWindowWidth;
        private int mWindowHeight;

        private ServerPauseReceiver serverPauseReceiver;

        private ViewManager(Context context) {
            if (mContext == null) {
                mContext = context.getApplicationContext();
                mInflater = LayoutInflater.from(mContext);
                mViewMap = new HashMap<>();
                mHandler = new Handler(Looper.getMainLooper());
                mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics out = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(out);
                mWindowWidth = out.widthPixels;
                mWindowHeight = out.heightPixels;
            }
        }

        private void register() {
            if (serverPauseReceiver == null) {
                serverPauseReceiver = new ServerPauseReceiver();
                IntentFilter filter = new IntentFilter(ACTION_SERVER_PAUSE);
                mContext.registerReceiver(serverPauseReceiver, filter);
            }
        }

        private void unregister() {
            if (serverPauseReceiver != null) {
                mContext.unregisterReceiver(serverPauseReceiver);
                serverPauseReceiver = null ;
            }
        }

        public String getViewKey(View view) {
            String key;
            if (mViewMap.containsValue(view)) {
                key = getKey(view);
            } else {
                key = UUID.randomUUID().toString();
                key  = key.replaceAll("-", "");
                mViewMap.put(key, view);
            }
            return key;
        }

        public View getView(String viewKey) {
            return mViewMap.get(viewKey);
        }

        public String getKey(View view){
            if (mViewMap.containsValue(view)) {
                for (Map.Entry<String, View> entry : mViewMap.entrySet()) {
                    if (entry.getValue() == view) {
                        return entry.getKey();
                    }
                }
            }
            return null;
        }

        private void removeFromMap(View view) {
            if (mViewMap.containsValue(view)) {
                for (Map.Entry<String, View> entry : mViewMap.entrySet()) {
                    if (entry.getValue() == view) {
                        mViewMap.remove(entry.getKey());
                        break;
                    }
                }
            }
        }

        private WindowManager.LayoutParams getLayoutParams(int x, int y, int width, int height, int animation) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.x = x ;
            lp.y = y ;
            lp.type = WindowManager.LayoutParams.TYPE_PHONE ;
            lp.format = PixelFormat.RGBA_8888 ;
            lp.gravity = Gravity.LEFT | Gravity.TOP ;
            lp.width = width ;
            lp.height = height ;
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            if (animation != VIEW_ANIMATION_NULL) {
                lp.windowAnimations = animation;
            }
            return lp;
        }

        public int getWindowWidth(){
            return mWindowWidth;
        }

        public int getWindowHeight(){
            return mWindowHeight;
        }

        public String showView(int layoutId, int x, int y, int width, int height, int animation) {
            View view = mInflater.inflate(layoutId, null);
            return showView(view, x, y, width, height, animation);
        }

        public String showView(View view, int x, int y, int width, int height, int animation) {
            WindowManager.LayoutParams layoutParams = getLayoutParams(x, y, width, height, animation);
            mWindowManager.addView(view, layoutParams);
            register();
            return getViewKey(view);
        }

        public boolean removeView(String key) {
            return removeView(mViewMap.get(key));
        }

        public boolean removeView(View view) {
            if (view != null) {
                removeFromMap(view);
                try {
                    mWindowManager.removeView(view);
                } catch (Exception e) {
                    LogUtil.e(TAG, "View 已经移除过了, 不必再次移除!");
                }

                if (mViewMap.size() == 0) {
                    unregister();
                }
                return true;
            } else {
                return false ;
            }
        }

        public void removeViewDelay(String key, long delay) {
            removeViewDelay(mViewMap.get(key), delay);
        }

        public void removeViewDelay(final View view, long delay) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeView(view);
                }
            }, delay);
        }

        public void onHide() {
            if (mViewMap != null && mViewMap.size() > 0) {
                for (View view : mViewMap.values()) {
                    if (view.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                    }
                }
            }
        }

        public void onShow() {
            if (mViewMap != null && mViewMap.size() > 0) {
                for (View view : mViewMap.values()) {
                    if (view.getVisibility() == View.GONE) {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

    }

    public class ServerPauseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (viewManagerInstance != null) {
                boolean hide = intent.getBooleanExtra("hide", false);
                if (hide) {
                    viewManagerInstance.onHide();
                } else {
                    viewManagerInstance.onShow();
                }
            }
        }

    }

}
