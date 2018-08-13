package cn.com.smartadscreen.model.bean;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.com.smartadscreen.utils.JSONUtils;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/15
 * 作用：设备信息实体类
 */

public class DeviceInfoBean {

    private String screenSize;      //屏幕分辨率
    private String sn;              //sn
    private String networkType;     //网络链接类型
    private String rom;             //存储容量大小
    private int machineType;        //机器类型
    private long SystemRunningTime;  //系统运行时间
    private String cpu;             //CPU使用率
    private int screenStatus;       //屏幕状态 1开，0关屏
    private String mac;             //mac 地址
    private String clientId;        //clientId
    private String ip;              //内网 ip
    private int connectStatus;      //连接状态
    private String sdcardPath;      //SD卡路径
    private String outterIp;        //外网 ip
    private String ram;             //内存容量
    private GfInfoBean gfInfo;      //功放信息

    private LocalFileDesc localFileDesc;   //本地文件信息
    private WeatherInfo weatherInfo;     //天气信息
    private WifiInfomation wifiInfomation;  //获取wifi信息
    private List<NormalPlans> deviceNormalPlans; //定时开关机计划
    private List<NormalPlans> screenNormalPlans; //定时开关屏计划

    private AddressInfo addressInfo;     //地址信息

    public LocalFileDesc getLocalFileDesc() {
        return localFileDesc;
    }

    public void setLocalFileDesc(LocalFileDesc localFileDesc) {
        this.localFileDesc = localFileDesc;
    }
    public String getScreenSize() {
        return screenSize;
    }



    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public AddressInfo getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(AddressInfo addressInfo) {
        this.addressInfo = addressInfo;
    }



    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(WeatherInfo weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public WifiInfomation getWifiInfomation() {
        return wifiInfomation;
    }

    public void setWifiInfomation(WifiInfomation wifiInfomation) {
        this.wifiInfomation = wifiInfomation;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public int getMachineType() {
        return machineType;
    }

    public void setMachineType(int machineType) {
        this.machineType = machineType;
    }

    public String getMachineTypeString() {
        String type = "";
        switch (machineType) {
            case 1:
                type = "音箱";
                break;
            default:
                type = "广告屏";
                break;
        }
        return type;
    }

    public long getSystemRunningTime() {
        return SystemRunningTime;
    }

    public void setSystemRunningTime(long SystemRunningTime) {
        this.SystemRunningTime = SystemRunningTime;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public int getScreenStatus() {
        return screenStatus;
    }

    public void setScreenStatus(int screenStatus) {
        this.screenStatus = screenStatus;
    }


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(int connectStatus) {
        this.connectStatus = connectStatus;
    }

    public String getConnectStatusString() {
        String status = "";
        switch (connectStatus) {
            case 1:
                status = "已连接";
                break;
            default:
                status = "未连接";
                break;
        }
        return status;
    }

    public String getSdcardPath() {
        return sdcardPath;
    }

    public void setSdcardPath(String sdcardPath) {
        this.sdcardPath = sdcardPath;
    }

    public String getOutterIp() {
        return outterIp;
    }

    public void setOutterIp(String outterIp) {
        this.outterIp = outterIp;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public GfInfoBean getGfInfo() {
        return gfInfo;
    }

    public void setGfInfo(GfInfoBean gfInfo) {
        this.gfInfo = gfInfo;
    }

    public List<NormalPlans> getDeviceNormalPlans() {
        Collections.sort(deviceNormalPlans, new Comparator<NormalPlans>() {
            @Override
            public int compare(NormalPlans p1, NormalPlans p2) {
                return p1.getWeek() < p2.getWeek() ? -1 : 1;
            }
        });
        return deviceNormalPlans;
    }

    public void setDeviceNormalPlans(List<NormalPlans> deviceNormalPlans) {
        this.deviceNormalPlans = deviceNormalPlans;
    }

    public List<NormalPlans> getScreenNormalPlans() {
        Collections.sort(screenNormalPlans, new Comparator<NormalPlans>() {
            @Override
            public int compare(NormalPlans p1, NormalPlans p2) {
                return p1.getWeek() < p2.getWeek() ? -1 : 1;
            }
        });
        return screenNormalPlans;
    }

    public void setScreenNormalPlans(List<NormalPlans> screenNormalPlans) {
        this.screenNormalPlans = screenNormalPlans;
    }


    /**WIFi信息**/

    public class  WifiInfomation{
        private String mac;
        private int speed;
        private int strength;
        private String units;

        private String ssid;

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public int getStrength() {
            return strength;
        }

        public void setStrength(int strength) {
            this.strength = strength;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

    }
    /**天气信息**/
     public class WeatherInfo{
        private String cond_code ;
        private String cond_txt;
        private String qlty;
        private String tem_now;
        private String tmp_max;

        public String getCond_code() {
            return cond_code;
        }

        public void setCond_code(String cond_code) {
            this.cond_code = cond_code;
        }

        public String getCond_txt() {
            return cond_txt;
        }

        public void setCond_txt(String cond_txt) {
            this.cond_txt = cond_txt;
        }

        public String getQlty() {
            return qlty;
        }

        public void setQlty(String qlty) {
            this.qlty = qlty;
        }

        public String getTem_now() {
            return tem_now;
        }

        public void setTem_now(String tem_now) {
            this.tem_now = tem_now;
        }

        public String getTmp_max() {
            return tmp_max;
        }

        public void setTmp_max(String tmp_max) {
            this.tmp_max = tmp_max;
        }

        public String getTmp_min() {
            return tmp_min;
        }

        public void setTmp_min(String tmp_min) {
            this.tmp_min = tmp_min;
        }

        private String tmp_min;



     }
    /**
     * 本地文件信息
     * **/


    /**
     * 功放信息
     */
    public class GfInfoBean {

        private int aux;
        private int battery;
        private int currApp;
        private int power;

        public int getAux() {
            return aux;
        }

        public void setAux(int aux) {
            this.aux = aux;
        }

        public int getBattery() {
            return battery;
        }

        public void setBattery(int battery) {
            this.battery = battery;
        }

        public int getCurrApp() {
            return currApp;
        }

        public void setCurrApp(int currApp) {
            this.currApp = currApp;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }
    }
    /**
     * 地址信息
     * **/
    public  class  AddressInfo{
        private String city; //城市
        private String country;//国家
        private String province;//省会
        private String street;//街道
        private String town;//区域

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

    }

    /**
     * 开关屏 及 定时开关机信息
     */
    public class NormalPlans {

        private int week;
        private List<PlanTime> timing;

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public List<PlanTime> getTiming() {
            return timing;
        }

        public void setTiming(List<PlanTime> timing) {
            this.timing = timing;
        }

        public class PlanTime{
            private String endTime;
            private String startTime;

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            @Override
            public String toString() {
                return "PlanTime{" +
                        "endTime='" + endTime + '\'' +
                        ", startTime='" + startTime + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "NormalPlans{" +
                    "week=" + week +
                    ", timing=" + timing.toString() +
                    '}';
        }
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }
}
