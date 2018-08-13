package cn.com.smartadscreen.main.ui.activity;


import org.xwalk.core.XWalkInitializer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.smartadscreen.app.di.compoent.DaggerAppComponent;
import cn.com.smartadscreen.di.componets.DaggerIndexComponets;
import cn.com.smartadscreen.di.module.IndexModule;
import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.main.ui.adapter.ViewPagerAdapter;
import cn.com.smartadscreen.main.ui.base.BaseActivityV1;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.main.ui.fragment.DefaultFragment;
import cn.com.smartadscreen.main.ui.fragment.WebFragment;
import cn.com.smartadscreen.main.ui.pager.VerticalViewPager;
import cn.com.smartadscreen.model.bean.TaskPush;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.main.IndexPresenter;
import cn.com.smartadscreen.presenter.service.PluginServer;
import cn.com.smartadscreen.utils.SmartTimerService;
import cn.com.startai.smartadh5.R;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IndexActivity extends BaseActivityV1 implements XWalkInitializer.XWalkInitListener ,IndexContract.View{
    @BindView(R.id.verticalViewPager)
    VerticalViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<BaseFragment> mFragments;

    private DefaultFragment mDefaultFragment;
    private WebFragment mWebFragment;


    private int fixedPager;
    private static boolean isBtStop = false;
    private int defaultPlayMode = 0;
    private String TAG = this.getClass().getSimpleName();
    @Inject
    IndexPresenter indexPresenter;


    @Override
    protected boolean shouldInterceptBack() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void initData() {
        //初始化dagger
        DaggerIndexComponets.builder()
                .indexModule(new IndexModule(this,mWebFragment,mFragments))
                .build()
                .inject(this);

    }

    @Override
    public void initView() {

    }

    @Override
    public void addViewListener() {

    }

    @Override
    public void onXWalkInitStarted() {

    }

    @Override
    public void onXWalkInitCancelled() {

    }

    @Override
    public void onXWalkInitFailed() {

    }

    @Override
    public void onXWalkInitCompleted() {

    }

    @Override
    public void setCurrentItem(int item) {

    }

    @Override
    public int getCurrentItem() {
        return 0;
    }
}
