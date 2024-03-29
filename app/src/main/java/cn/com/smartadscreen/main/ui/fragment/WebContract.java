package cn.com.smartadscreen.main.ui.fragment;

import android.view.View;

import cn.com.smartadscreen.model.bean.OnVideoPlayer;

public interface WebContract {

    interface view {
        //        void removeParent(View view);
//        void initTextClock();
        void operateStatusBar(String timeType, String logoPth);
        void bringTimeToFront();
    }

    interface presenter {
        void sendStateMessage(int state, boolean isNeedSendToH5);

        void onPlayVideo(OnVideoPlayer onVideoPlayer);
    }
}
