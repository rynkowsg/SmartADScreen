package cn.com.smartadscreen.di.componets;

import javax.inject.Singleton;

import cn.com.smartadscreen.di.module.SetupModule;
import cn.com.smartadscreen.main.ui.activity.SetupActivity;
import dagger.Component;

@Singleton
@Component(modules = SetupModule.class)
public interface SetupComponets {
    void inject (SetupActivity setupActivity);
}
