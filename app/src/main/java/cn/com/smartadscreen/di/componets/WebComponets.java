package cn.com.smartadscreen.di.componets;

import javax.inject.Singleton;

import cn.com.smartadscreen.di.module.WebModule;
import cn.com.smartadscreen.main.ui.fragment.WebFragment;
import dagger.Component;

@Singleton
@Component(modules = WebModule.class)
public interface WebComponets {
    void inject(WebFragment webFragment);
}
