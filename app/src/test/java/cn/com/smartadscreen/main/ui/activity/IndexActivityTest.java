package cn.com.smartadscreen.main.ui.activity;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.com.smartadscreen.di.componets.DaggerIndexComponets;
import cn.com.smartadscreen.di.module.IndexModule;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.main.ui.fragment.WebFragment;
import cn.com.smartadscreen.main.ui.view.StatusBar;
import cn.com.smartadscreen.model.bean.LocalFileDesc;
import cn.com.smartadscreen.presenter.main.IndexPresenter;
import cn.com.smartadscreen.presenter.task.HourSyncTask;
import cn.com.smartadscreen.utils.SmartTimerService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class IndexActivityTest {
//    @Mock
//    private List<BaseFragment> mFragments;
//    @Mock
//    private WebFragment mWebFragment;
    WebFragment mock;

//    IndexPresenter indexPresenter;
    private StatusBar statusBar;


    @Before
    public void setUp() throws Exception {
//        mFragments = new ArrayList<>();
//        mWebFragment = new WebFragment();
        mock = mock(WebFragment.class);
        initMocks(this);

//        indexPresenter = new IndexPresenter(mock,mWebFragment,mFragments);


        statusBar = new StatusBar(mock.getContext());
    }

    @Test
    public void testt() {
        statusBar.setTimeShow(false);
    }
}