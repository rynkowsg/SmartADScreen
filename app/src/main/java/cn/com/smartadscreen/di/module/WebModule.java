package cn.com.smartadscreen.di.module;

import javax.inject.Singleton;

import cn.com.smartadscreen.main.ui.fragment.WebContract;
import cn.com.smartadscreen.presenter.main.WebPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class WebModule {
    private final WebContract.view view;

    public WebModule(WebContract.view view) {
        this.view = view;
    }
    @Singleton
    @Provides
    WebPresenter provideIndexPresenter() {
        return new WebPresenter(view);
    }

}
