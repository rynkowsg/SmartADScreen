package cn.com.smartadscreen.main.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.smartadscreen.app.Application;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.model.bean.DeviceInfoBean;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.sp.SPManager;
import cn.com.smartadscreen.presenter.rx.RxInterval;
import cn.com.smartadscreen.presenter.update.DataSourceUpdateModule;
import cn.com.smartadscreen.utils.HomeDataUtils;
import cn.com.smartadscreen.utils.QRCodeUtil;
import cn.com.startai.smartadh5.R;

public class DefaultClockFragment extends BaseFragment {

    @BindView(R.id.bt_state)
    TextView mBtState;
    @BindView(R.id.first_fragment_time)
    TextView firstFragmentTime;
    @BindView(R.id.first_fragment_morning)
    TextView firstFragmentMorning;
    @BindView(R.id.weather_sun)
    ImageView weatherSun;
    @BindView(R.id.temp_noraml)
    TextView tempNoraml;
    @BindView(R.id.temp_max_min)
    TextView tempMaxMin;
    @BindView(R.id.id_default_clock_clock_wrapper)
    LinearLayout idDefaultClockClockWrapper;
    @BindView(R.id.id_default_qr_image)
    ImageView idDefaultQrImage;
    @BindView(R.id.id_default_qr_text)
    TextView idDefaultQrText;
    Unbinder unbinder;
    @BindView(R.id.air_quality)
    TextView airQuality;
    @BindView(R.id.air_state)
    TextView airState;
    private String sn;
    private String qrText;
    private Calendar calendar;
    private RxInterval rxInterval;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_default_clock, container, false);
    }

    @Override
    public void initData() {
        sn = SPManager.getManager().getString(SPManager.KEY_DEVICE_SN);
        qrText = "http://www.startai.com.cn/qr/?" + sn;
        EventBus.getDefault().register(this);
    }

    private String TAG = "DefaultClockFragment";

    @Override
    public void initView() {
        Log.v(TAG, "initView");
        //显示时间
        String timeData = HomeDataUtils.getInstance().getTimeData();
        //显示时段
        String timePeriod = HomeDataUtils.getInstance().getTimePeriod();
        firstFragmentMorning.setText(timePeriod);
        firstFragmentTime.setText(timeData);

        encodeQrCode();
        BroadcastTable currentBt = DataSourceUpdateModule.getCurrentBt();

        if (!SPManager.getInstance().isMusicBox()) {
            String text = "";
            if (currentBt != null) {
                text = currentBt.getName() + ":   ";
                int state = SPManager.getManager().getInt(SPManager.KEY_PROGRAM_STATE, 2);
                switch (state) {
                    case 1:
                        text += "播放";
                        break;
                    case 2:
                        text += "暂停";
                        break;
                    case 3:
                        text += "停止";
                        break;
                }
            }
            mBtState.setText(text);
        }
        rxInterval = new RxInterval(firstFragmentTime);
        rxInterval.setTimeData();
    }

    private void encodeQrCode() {
        int width = 300;
        int height = 300;
        String filePath = getActivity().getFilesDir() + "/sn_qrcode.jpg";
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        boolean success = QRCodeUtil.createQRImage(qrText, width, height, logo, filePath);
        if (success) {
            Bitmap bm = BitmapFactory.decodeFile(filePath);
            idDefaultQrImage.setImageBitmap(bm);
        }

        idDefaultQrText.setText("本机注册码:\n\n" + sn);
        Log.i("DefaultClockFragment", "sn=" + sn);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void WeatherChangeState(DeviceInfoBean bean) {
        DeviceInfoBean.WeatherInfo weatherInfo = bean.getWeatherInfo();
        getWeatherData(weatherInfo);
    }

    private void getWeatherData(DeviceInfoBean.WeatherInfo weatherInfo) {
        String weatherNow = weatherInfo.getTem_now(); //现在温度
        String weatherMax = weatherInfo.getTmp_max();//最高温度
        String weatherMin = weatherInfo.getTmp_min();//最低温度
        String weatherCode = weatherInfo.getCond_code();//天气code
        String weatherTxt = weatherInfo.getCond_txt();//天气情况
        String weatherQlty = weatherInfo.getQlty();//空气质量
        if (weatherNow != null) {
            tempNoraml.setText(weatherNow + "°");
        } else {
            tempNoraml.setText("28°");
        }
        if (weatherMax != null && weatherMin != null) {
            tempMaxMin.setText(weatherMin + "°" + "/" + weatherMax + "°");
        } else {
            tempMaxMin.setText("19° / 20°");
        }
        if (weatherQlty != null) {
            airQuality.setText("空气 " + weatherQlty);
        } else {
            airQuality.setText("空气良好");
        }
        if (weatherTxt != null) {
            airState.setText(weatherTxt);
        } else {
            airState.setText("晴");
        }
        if (weatherCode != null) {

            switch (weatherCode) {
                case "100":
                    weatherSun.setImageResource(R.drawable.weather_sun);
                    break;
                case "100n":
                    weatherSun.setImageResource(R.drawable.weather_100n_moon);
                    break;
                case "101":
                    weatherSun.setImageResource(R.drawable.weather_101_cloudy);
                    break;
                case "102":
                    weatherSun.setImageResource(R.drawable.weather_102_cloudy);
                    break;
                case "103":
                    weatherSun.setImageResource(R.drawable.weather_103_cloudy);
                    break;
                case "103n":
                    weatherSun.setImageResource(R.drawable.weather_103n_cloudy);
                    break;
                case "104":
                    weatherSun.setImageResource(R.drawable.weather_104_sun);
                    break;
                case "104n":
                    weatherSun.setImageResource(R.drawable.weather_104n_sun);
                    break;
                case "200":
                    weatherSun.setImageResource(R.drawable.weather_200_wind);
                    break;
                case "201":
                    weatherSun.setImageResource(R.drawable.weather_201_wind);
                    break;
                case "202":
                    weatherSun.setImageResource(R.drawable.weather_202_wind);
                    break;
                case "203":
                    weatherSun.setImageResource(R.drawable.weather_203_wind);
                    break;
                case "204":
                    weatherSun.setImageResource(R.drawable.weather_204_wind);
                    break;
                case "205":
                    weatherSun.setImageResource(R.drawable.weather_205_wind);
                    break;
                case "206":
                    weatherSun.setImageResource(R.drawable.weather_206_wind);
                    break;
                case "207":
                    weatherSun.setImageResource(R.drawable.weather_207_wind);
                    break;
                case "208":
                case "209":
                case "210":
                case "211":
                case "212":
                case "213":
                    weatherSun.setImageResource(R.drawable.weather_208_hurricane);
                    break;
                case "300":
                    weatherSun.setImageResource(R.drawable.weather_300_sun_rain);
                    break;
                case "300n":
                    weatherSun.setImageResource(R.drawable.weather_300n_sun_rain);
                    break;
                case "301":
                    weatherSun.setImageResource(R.drawable.weather_301_sun_rain);
                    break;
                case "301n":
                    weatherSun.setImageResource(R.drawable.weather_301n_sun_rain);
                    break;
                case "302":
                    weatherSun.setImageResource(R.drawable.weather_302_sun_rain);
                    break;
                case "303":
                    weatherSun.setImageResource(R.drawable.weather_303_sun_rain);
                    break;
                case "304":
                    weatherSun.setImageResource(R.drawable.weather_304_sun_rain);
                    break;
                case "305":
                    weatherSun.setImageResource(R.drawable.weather_305_sun_rain);
                    break;
                case "306":
                    weatherSun.setImageResource(R.drawable.weather_306_sun_rain);
                    break;
                case "307":
                    weatherSun.setImageResource(R.drawable.weather_307_sun_rain);
                    break;
                case "309":
                    weatherSun.setImageResource(R.drawable.weather_309_sun_rain);
                    break;
                case "310":
                    weatherSun.setImageResource(R.drawable.weather_310_sun_rain);
                    break;
                case "311":
                    weatherSun.setImageResource(R.drawable.weather_311_sun_rain);
                    break;
                case "312":
                    weatherSun.setImageResource(R.drawable.weather_312_sun_rain);
                    break;
                case "313":
                    weatherSun.setImageResource(R.drawable.weather_313_sun_rain);
                    break;
                case "402":
                    weatherSun.setImageResource(R.drawable.weather_402_ice);
                    break;
                case "403":
                    weatherSun.setImageResource(R.drawable.weather_403_ice);
                    break;
                case "404":
                    weatherSun.setImageResource(R.drawable.weather_404_ice);
                    break;
                case "405":
                    weatherSun.setImageResource(R.drawable.weather_405_ice);
                    break;
                case "406":
                    weatherSun.setImageResource(R.drawable.weather_406_ice);
                    break;
                case "406n":
                    weatherSun.setImageResource(R.drawable.weather_406n_ice);
                    break;
                case "407":
                    weatherSun.setImageResource(R.drawable.weather_407_ice);
                    break;
                case "500":
                    weatherSun.setImageResource(R.drawable.weather_500_log);
                    break;
                case "501":
                    weatherSun.setImageResource(R.drawable.weather_501_log);
                    break;
                case "502":
                    weatherSun.setImageResource(R.drawable.weather_502_log);
                    break;
                case "503":
                    weatherSun.setImageResource(R.drawable.weather_503_log);
                    break;
                case "504":
                    weatherSun.setImageResource(R.drawable.weather_504_log);
                    break;
                case "507":
                    weatherSun.setImageResource(R.drawable.weather_507_log);
                    break;
                case "508":
                    weatherSun.setImageResource(R.drawable.weather_508_log);
                    break;
                case "900":
                    weatherSun.setImageResource(R.drawable.weather_900_log);
                    break;
                case "901":
                    weatherSun.setImageResource(R.drawable.weather_901_log);
                    break;

            }
        } else {
            weatherSun.setImageResource(R.drawable.weather_999_log);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void addViewListener() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.d(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
        rxInterval.cancleTimeData();
        Application.getContext().mRefWatcher.watch(this);

    }
}
