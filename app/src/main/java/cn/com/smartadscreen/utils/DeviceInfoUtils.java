package cn.com.smartadscreen.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chufeng on 2018/4/11.
 * 描述：用于获取设备信息
 */

public class DeviceInfoUtils {
    private String[] abis = new String[]{};

    public static DeviceInfoUtils getInstance(){
        return InstanceHolder.INSTANCE;
    }

    private static final class InstanceHolder {
        private static final DeviceInfoUtils INSTANCE = new DeviceInfoUtils();
    }

    //获取内存总大小
    public ArrayList<String> getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2 = "";
        ArrayList<String> list = new ArrayList<>();
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                list.add(str2);
                return list;
            }
        } catch (IOException e) {
        }
        return null;
    }

    //剩余的内存
    public long getAvailMemory(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    //获取cpu信息
    public String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }
    //获取cpu构架

    public void  getCpuArchitecture(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI,Build.CPU_ABI2};
        }
        StringBuilder abiStr = new StringBuilder();
        for(String abi:abis)
        {
            abiStr.append(abi);
            abiStr.append(',');
        }
        Log.i("CPUINFOR", "cpu构架=============>" + abiStr);
    }



}
