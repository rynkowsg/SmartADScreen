package cn.com.smartadscreen.presenter.audio;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import java.io.IOException;

import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.startai.smartadh5.R;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkMusicPlayer {

    private static IjkMediaPlayer ijkMediaPlayer;

    public static void playBroadcastMessageAudio() {

        if (ijkMediaPlayer != null) {
            if (ijkMediaPlayer.isPlaying()){
                ijkMediaPlayer.stop();
                ijkMediaPlayer.release();
            }
            ijkMediaPlayer = null ;
        }

        ijkMediaPlayer = new IjkMediaPlayer();
        AssetFileDescriptor fileDescriptor;
        try {
            ijkMediaPlayer.reset();
            fileDescriptor = Config.getContext().getAssets().openFd("alarm.mp3");
            ijkMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor());
            ijkMediaPlayer.setOnPreparedListener(iMediaPlayer -> ijkMediaPlayer.start());
            ijkMediaPlayer.setOnCompletionListener(iMediaPlayer -> {
                ijkMediaPlayer.stop();
                ijkMediaPlayer.release();
            });
            ijkMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
