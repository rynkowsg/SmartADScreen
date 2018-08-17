package cn.com.smartadscreen.di.module;

import android.app.Activity;

import javax.inject.Singleton;

import cn.com.smartadscreen.main.ui.activity.SetupActivity;
import cn.com.smartadscreen.presenter.main.SetupPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class SetupModule {

    private final Activity activity;

    public SetupModule(SetupActivity activity){
        this.activity =activity;
    }
    @Provides
    @Singleton
    SetupPresenter providesSetupPresenter(){
        return  new SetupPresenter(activity);
    }
}
