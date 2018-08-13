package cn.com.smartadscreen.main.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.com.smartadscreen.main.ui.base.BaseFragment;


/**
 * Created by Taro on 2017/3/18.
 * ViewPager for fragment adapter
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments;

    public ViewPagerAdapter(FragmentManager fm, List<BaseFragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mFragments = new ArrayList<>();
    }

    public void addFragment(BaseFragment f) {
        this.mFragments.add(f);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
