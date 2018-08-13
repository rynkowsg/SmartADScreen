package cn.com.smartadscreen.app.di.compoent;

import cn.com.smartadscreen.app.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by chufeng on 18/6/29.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

}
