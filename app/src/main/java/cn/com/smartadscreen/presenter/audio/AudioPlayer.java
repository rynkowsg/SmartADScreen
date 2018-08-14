package cn.com.smartadscreen.presenter.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;

import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.startai.smartadh5.R;

/**
 * Created by Taro on 2017/1/22.
 * Audio 播放类
 */
public class AudioPlayer {

    private static MediaPlayer mediaPlayer ;
    private static boolean mediaPlayerIsStop = true ;

    /**
     * 播放推送播表成功音乐
     */
    public static void playHandleBroadcastMessageAudio(){
        try {

            if (mediaPlayer != null) {
                if (!mediaPlayerIsStop && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = null ;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            AssetFileDescriptor fileDescriptor = Config.getContext().getAssets().openFd("alarm.mp3");
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getStartOffset());
            mediaPlayer.setOnPreparedListener( mp -> {
                    mp.start();
                    mediaPlayerIsStop = false ;

            });

            mediaPlayer.setOnCompletionListener( mp -> {
                    mp.stop();
                    mp.release();
                    mediaPlayerIsStop = true ;
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
