package cn.com.startai.smartadh5.extension.utils.homedata;

import android.util.Log;

import java.util.Calendar;

import cn.com.startai.smartadh5.R;
import cn.com.startai.smartadh5.processlogic.entity.bean.DeviceInfoBean;

/**
 * Created by chufeng on 2018/4/7.
 * 获取音响首页的数据
 */

public class HomeDataUtils {

    private Calendar calendar;

    public static HomeDataUtils getInstance(){
        return InstanceHolder.INSTANCE;
    }

    private static final class InstanceHolder {
        private static final HomeDataUtils INSTANCE = new HomeDataUtils();
    }

    public String getTimeData (){
        String timeString ;
         calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String s = String.valueOf(minute);
        if (s.length()==1){
             timeString = hour + ":" +"0"+ minute;
        }else {
            timeString = hour + ":" + minute;
        }
        return timeString;
    }
    //得到时段
    public String getTimePeriod(){
        if(calendar==null){
            calendar = Calendar.getInstance();
        }
            int apm = calendar.get(Calendar.AM_PM);
            if (apm == 0) {
                return "上午";
            } else {
                return "下午";
            }
    }
    //得到地理位置信息
    public String getAddressInfor(DeviceInfoBean.AddressInfo addressInfo) {
        String addressCity;
        String addressTown;
        if (addressInfo != null) {
            addressCity = addressInfo.getCity();
            addressTown = addressInfo.getTown();
            if (addressCity != null && addressTown != null) {
                String af = addressCity + addressTown;
                Log.i("DefaultFragment", "addressTown=" + addressTown);
                Log.i("DefaultFragment", "addressCity=" + addressCity);
                Log.i("DefaultFragment", "af=" + af);
                return af;
            } else {
                return "定位失败";
            }
        }
        return "定位失败";
    }

    //获得电池信息
    public Integer getbatterStateChange(int battery,int power){
        int batteryImageResId = 0;
        if(power==1){
            batteryImageResId=R.drawable.battery_five;
        }else {
        if (battery <= 0) {
            batteryImageResId = R.drawable.battery_zeo;
        } else if (battery >= 10) {
            batteryImageResId = R.drawable.battery_four;
        } else {
            switch (battery) {
                case 1:
                case 2:
                case 3:
                    batteryImageResId = R.drawable.battery_one;
                    break;
                case 4:
                case 5:
                case 6:
                    batteryImageResId = R.drawable.battery_two;
                    break;
                case 7:
                case 8:
                case 9:
                    batteryImageResId = R.drawable.battery_four;
                    break;
            }
        }
        }

            return batteryImageResId;
    }

}
