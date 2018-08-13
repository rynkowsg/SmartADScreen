package cn.com.smartadscreen.presenter.main;

import cn.com.smartadscreen.main.ui.fragment.WebContract;
import cn.com.smartadscreen.model.bean.OnVideoPlayer;

public class WebPresenter implements WebContract.presenter {
    private WebContract.view view;

    public WebPresenter(WebContract.view view){
        this.view = view;
    }

    @Override
    public void sendStateMessage(int state, boolean isNeedSendToH5) {

    }

    @Override
    public void onPlayVideo(OnVideoPlayer onVideoPlayer) {

    }
}
