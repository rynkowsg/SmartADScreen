package cn.com.smartadscreen.main.ui.base;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Taro on 2017/3/18.
 * BaseFragment
 */
public abstract class BaseFragment extends Fragment implements IActivity{

    private boolean mIsVisible ;
    protected Activity mContext;
    protected View mFragmentRootView;
    private Unbinder unbinder;

    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentRootView = inflaterView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, mFragmentRootView);
        this.initData();
        this.initView();
        this.addViewListener();
        return mFragmentRootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInVisible();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void onVisible() {
        mIsVisible = true ;
    }

    public void onInVisible() {
        mIsVisible = false ;
    }

    public void onLeftPress(){}

    public void onRightPress(){}

    public final boolean getVisible(){
        return mIsVisible;
    }

}
