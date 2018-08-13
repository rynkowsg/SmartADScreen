package cn.com.smartadscreen.presenter.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.TimeUtils;
import com.facebook.stetho.common.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.com.smartadscreen.locallog.SmartLocalLog;
import cn.com.smartadscreen.locallog.entity.LogMsg;
import cn.com.smartadscreen.main.ui.activity.IndexContract;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.main.ui.fragment.WebFragment;
import cn.com.smartadscreen.model.bean.StatusBarBean;
import cn.com.smartadscreen.model.bean.TaskPush;
import cn.com.smartadscreen.model.bean.config.Config;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.utils.SmartTimerService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

@SuppressWarnings("all")
public class IndexPresenter implements IndexContract.Presenter {
    private IndexContract.View view;
    private WebFragment mWebFragment;
    private List<BaseFragment> mFragments;
    private int fixedPager;
    private final String TAG="IndexPresenter";

    public IndexPresenter(IndexContract.View view, WebFragment mWebFragment, List<BaseFragment> mFragments) {
        this.view = view;
        this.mWebFragment = mWebFragment;
        this.mFragments = mFragments;
        fixedPager = SPManager.getManager().getInt(SPManager.KEY_FIXED_VIEWPAGER_INDEX, -1);
    }


    @Override
    public void doTaskPushByPre(TaskPush taskPush) {

    }

    @Override
    public void pushToWebByPre(TaskPush taskPush) {

    }

    @Override
    public void delayToPushByPre(TaskPush taskPush) {

    }

    @Override
    public void checkSpotsByPre(JSONObject messageObj) {

    }

    @Override
    public void doWhileTaskPushByPre(long startTime, long splitLength) {

    }
}
