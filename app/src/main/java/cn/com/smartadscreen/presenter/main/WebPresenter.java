package cn.com.smartadscreen.presenter.main;

import cn.com.smartadscreen.main.ui.fragment.WebContract;
import cn.com.smartadscreen.model.bean.OnVideoPlayer;

public class WebPresenter   {
    private WebContract.view view;

    public WebPresenter(WebContract.view view){
        this.view = view;
    }


}
