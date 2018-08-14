package cn.com.smartadscreen.di.module;

import java.util.List;

import javax.inject.Singleton;

import cn.com.smartadscreen.main.ui.activity.IndexActivity;
import cn.com.smartadscreen.main.ui.activity.IndexContract;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.main.ui.fragment.WebFragment;
import cn.com.smartadscreen.presenter.main.IndexPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class IndexModule {
    private final IndexActivity view;
    private WebFragment mWebFragment;
    private List<BaseFragment> mFragments;

    public IndexModule(IndexActivity view, WebFragment mWebFragment, List<BaseFragment> mFragments) {
        this.view = view;
        this.mWebFragment = mWebFragment;
        this.mFragments = mFragments;
    }

    @Singleton
    @Provides
    IndexPresenter provideIndexPresenter() {
        return new IndexPresenter(view,mWebFragment,mFragments);
    }
}
