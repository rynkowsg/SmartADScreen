package cn.com.smartadscreen.di.componets;


import javax.inject.Singleton;

import cn.com.smartadscreen.di.module.IndexModule;
import cn.com.smartadscreen.main.ui.activity.IndexActivity;
import dagger.Component;

@Singleton
@Component(modules = IndexModule.class)
public interface IndexComponets {
    void inject(IndexActivity indexActivity);
}
