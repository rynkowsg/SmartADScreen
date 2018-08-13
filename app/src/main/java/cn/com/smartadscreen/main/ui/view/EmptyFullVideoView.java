package cn.com.startai.smartadh5.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import cn.com.startai.smartadh5.R;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/22
 *         作用：
 */

public class EmptyFullVideoView extends StandardGSYVideoPlayer {

    private LinearLayout llLoading;
    private int currentTime;
    private OnVideoPlayCompleteListener mOnVideoPlayCompleteListener;

    public EmptyFullVideoView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public EmptyFullVideoView(Context context) {
        super(context);
    }

    public EmptyFullVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_empty_control_video;
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }

    @Override
    protected void touchDoubleUp() {
        //super.touchDoubleUp();
        //不需要双击暂停
    }

    @Override
    protected void setProgressAndTime(int progress, int secProgress, int currentTime, int totalTime) {
        super.setProgressAndTime(progress, secProgress, currentTime, totalTime);
        this.currentTime = currentTime;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public void onAutoCompletion() {
        if (mOnVideoPlayCompleteListener != null) {
            mOnVideoPlayCompleteListener.onComplete();
        }
    }

    @Override
    public void onCompletion() {

    }

    public void setOnVideoPlayCompleteListener(OnVideoPlayCompleteListener l) {
        mOnVideoPlayCompleteListener = l;
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
    }


    @Override
    protected void changeUiToError() {
        super.changeUiToError();
        llLoading.setVisibility(VISIBLE);
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        llLoading.setVisibility(VISIBLE);
    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        llLoading.setVisibility(INVISIBLE);
    }


    public interface OnVideoPlayCompleteListener {
        void onComplete();
    }
}
