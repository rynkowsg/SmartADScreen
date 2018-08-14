package cn.com.smartadscreen.main.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.main.ui.adapter.ViewPagerAdapter;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.model.bean.DeviceInfoBean;
import cn.com.smartadscreen.model.bean.event.PlayModeChangedEvent;
import cn.com.smartadscreen.model.db.entity.PlayInfo;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.rx.RxInterval;
import cn.com.smartadscreen.utils.HomeDataUtils;
import cn.com.smartadscreen.utils.NmcUtils;
import cn.com.smartadscreen.utils.TimerUtils;
import cn.com.startai.smartadh5.R;

//@SuppressWarnings("all")
public class DefaultFragment extends BaseFragment {
    @BindView(R.id.id_default_view_pager)
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    @BindView(R.id.fragment_date)
    TextView fragmentDate;
    @BindView(R.id.fragment_morning)
    TextView fragmentMorning;
    @BindView(R.id.fragment_time)
    TextView fragmentTime;
    @BindView(R.id.fragment_week)
    TextView fragmentWeek;
    @BindView(R.id.fragment_areas)
    TextView fragmentAreas;
    Unbinder unbinder;
    @BindView(R.id.wifi_show_state)
    ImageView wifiShowState;
    @BindView(R.id.wifi_ssid)
    TextView wifiSsid;
    @BindView(R.id.battery_state)
    ImageView batteryState;
    @BindView(R.id.batter_number)
    TextView batterNumber;
    @BindView(R.id.spring_text)
    TextView springText;
    @BindView(R.id.ll_toolbar_warning)
    LinearLayout llToolbarWarning;

    private Calendar calendar;
    private int mPosition;


    private int mId = -1;
    private int batteryPower;
    private RxInterval rxInterval;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_default, container, false);
    }

    @Override
    public void initData() {
        adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new DefaultClockFragment());
        adapter.addFragment(new DefaultFileFragment());
        TimerUtils.schedule("movePager", new FinishActivityTask(), 10000 * 18);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView() {
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        calendar = Calendar.getInstance();
        String timeData = HomeDataUtils.getInstance().getTimeData();
        String timePeriod = HomeDataUtils.getInstance().getTimePeriod();

        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        String weekString = "";
        switch (week) {
            case 1:
                weekString = "星期日";
                break;
            case 2:
                weekString = "星期一";
                break;
            case 3:
                weekString = "星期二";
                break;
            case 4:
                weekString = "星期三";
                break;
            case 5:
                weekString = "星期四";
                break;
            case 6:
                weekString = "星期五";
                break;
            case 7:
                weekString = "星期六";
                break;
            default:
                break;
        }

        String dateString = month + "月" + day + "日";
        fragmentDate.setText(dateString);
        fragmentWeek.setText(weekString);
        fragmentMorning.setText(timePeriod);
        fragmentTime.setText(timeData);


        //设置时间
        rxInterval = new RxInterval(fragmentTime);
        rxInterval.setTimeData();

    }


    class FinishActivityTask extends TimerTask {
        public void run() {
            if (mPosition == 1) {
                switchPage(0);
            }
        }
    }

    public void switchPage(int page) {
        viewPager.setCurrentItem(page);
    }

    @Override
    public void addViewListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("onPageSelected", "current page = " + position);
                mPosition = position;
                TimerUtils.schedule("movePager", new FinishActivityTask(), 10000);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onLeftPress() {
        super.onLeftPress();
        // 左移
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onRightPress() {
        super.onRightPress();
        // 右移
        if (viewPager.getCurrentItem() < adapter.getCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPlayModeChange(PlayModeChangedEvent event) {
        if (event.getMode() == PlayInfo.TYPE_CLOCK) {
            SPManager.getInstance().saveCurrentPlayerMode(PlayInfo.TYPE_CLOCK);
            NmcUtils.sendToNmcPlayerStatusChanged(-1, 0, 0);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onAddressInfoChanged(DeviceInfoBean bean) {
        DeviceInfoBean.AddressInfo addressInfo = bean.getAddressInfo();
        DeviceInfoBean.WifiInfomation wifiInfomation = bean.getWifiInfomation();
        DeviceInfoBean.GfInfoBean gfInfoBean = bean.getGfInfo();
        int battery = 0;
        String ssid;
        int wifiStrength;

        //设置地理位置
        String ss = HomeDataUtils.getInstance().getAddressInfor(addressInfo);
        fragmentAreas.setText(ss);

        //设置wifi状态
        if (wifiInfomation != null) {
            ssid = wifiInfomation.getSsid();
            wifiStrength = wifiInfomation.getStrength();
            llToolbarWarning.setVisibility(View.GONE);
            Log.i("DefaultFragment", "ssid=" + ssid);
            wifiSsid.setText(ssid);
            switch (wifiStrength) {
                case 0:
                    wifiShowState.setImageResource(R.drawable.wifi_disconnect);
                    break;
                case 1:
                    wifiShowState.setImageResource(R.drawable.wifi_one);
                    break;
                case 2:
                    wifiShowState.setImageResource(R.drawable.wifi_two);
                    break;
                case 3:
                    wifiShowState.setImageResource(R.drawable.wifi_three);
                    break;
                case 4:
                    wifiShowState.setImageResource(R.drawable.wifi_four);
                    break;

            }

        } else {
            wifiShowState.setImageResource(R.drawable.wifi_disconnect);
            llToolbarWarning.setVisibility(View.VISIBLE);
            springText.setText(R.string.spring_text);
        }

        if (gfInfoBean != null) {
            batteryPower = gfInfoBean.getPower();
            battery = gfInfoBean.getBattery();
        }

        int batteryImageResId = HomeDataUtils.getInstance().getbatterStateChange(battery, batteryPower);
        batteryState.setImageResource(batteryImageResId);
        batterNumber.setText(battery * 10 + "%");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mId = getActivity().getIntent().getIntExtra("mId", 2);
        if (mId == 1) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onVisible() {
        super.onVisible();
    }

    @Override
    public void onDestroyView() {
//        handler.removeCallbacksAndMessages(null);
//        new myThread().interrupt();
        rxInterval.cancleTimeData();
        EventBus.getDefault().unregister(this);
        TimerUtils.close("movePager");
        unbinder.unbind();
        super.onDestroyView();
        Application.getContext().mRefWatcher.watch(this);

    }
}
