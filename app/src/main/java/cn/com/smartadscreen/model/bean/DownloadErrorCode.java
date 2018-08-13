package cn.com.smartadscreen.model.bean;

/**
 * 名称：
 * 描述：
 * Created by Robin on 2016-10-17
 * QQ 419109715 彬影
 */
public abstract class DownloadErrorCode {

    public static final int ERROR_CODE_CONN_TRACKER_FAILED = -1001;// 连接tracker服务器失败
    public static final int ERROR_CODE_CONN_STORAGE_FAILED = -1002;// 连接存储服务器失败
    public static final int ERROR_CODE_WRITE_FILE_FAILED = -1003;// 本地文件写入失败
    public static final int ERROR_CODE_UNKNOW_EXCEPTION = -1004;// 服务器无法进行tcp下载该文件
    public static final int ERROR_CODE_NETWORK_UNVALIBLE = -1005;// 网络异常
    public static final int ERROR_CODE_DOWNLOAD_EXCEPTION = -1006;// 其他异常
    public static final int ERROR_CODE_CONN_SERVER_FAILED = -1007;// 连接服务器失败
    public static final int ERROR_CODE_LOCAL_PATH_NOT_EXIT = -1008;// 本地文件找不到
    public static final int ERROR_CODE_LOCAL_SAVE_FILE_EXCPETION = -1011;// 文件操作异常
    public static final int ERROR_CODE_FILE_IS_DOWNLOADING = -1009;// 已经有正在下载的任务
    public static final int ERROR_CODE_URL_FORMAT_ERROR = -1010;// URL格式不正确
    public static final int ERROR_CODE_GET_FILEINFO_ERROR = -1012;// 获取文件服务器文件信息失败
    public static final int ERROR_UNKNOW_DOWNLOAD_TYPE = -1013;// 不支持的下载方式
    public static final int ERROR_CODE_CONN_NO_HASH = -1014;//没有hash值
    public static final int ERROR_CODE_CONN_NO_LOCALPATH = -1015;//本地路径非法
    public static final int ERROR_CODE_FILE_NOT_COORECT = -1016;//文件与其hash值不匹配
    public static final int ERROR_CODE_CONN_NO_URL = -1017;//没有url
    public static final int ERROR_CODE_NOT_IN_DOWNLOADTIME = -1018;//不在下载时间段内
    public static final int ERROR_CODE_LAN_FILE_DOWNLOAD_FIALED = -1019;//局域网文件下载失败
    public static final int ERROR_CODE_FILE_NOT_IN_LAN = -1020; //局域网url与本机ip不在同一网段

    public static String getDownloadMsgByCode(int downloadErrorCode) {

        if (downloadErrorCode == ERROR_CODE_CONN_TRACKER_FAILED) {
            return "连接tracker服务器失败";
        } else if (downloadErrorCode == ERROR_CODE_CONN_STORAGE_FAILED) {
            return "连接存储服务器失败";
        } else if (downloadErrorCode == ERROR_CODE_WRITE_FILE_FAILED) {
            return "本地文件写入失败";
        } else if (downloadErrorCode == ERROR_CODE_UNKNOW_EXCEPTION) {
            return "服务器无法进行tcp下载该文件";
        } else if (downloadErrorCode == ERROR_CODE_NETWORK_UNVALIBLE) {
            return "网络异常";
        } else if (downloadErrorCode == ERROR_CODE_DOWNLOAD_EXCEPTION) {
            return "其他异常";
        } else if (downloadErrorCode == ERROR_CODE_CONN_SERVER_FAILED) {
            return "连接服务器失败";
        } else if (downloadErrorCode == ERROR_CODE_LOCAL_PATH_NOT_EXIT) {
            return "本地文件找不到";
        } else if (downloadErrorCode == ERROR_CODE_LOCAL_SAVE_FILE_EXCPETION) {
            return "文件操作异常";
        } else if (downloadErrorCode == ERROR_CODE_FILE_IS_DOWNLOADING) {
            return "已经有正在下载的任务";
        } else if (downloadErrorCode == ERROR_CODE_URL_FORMAT_ERROR) {
            return "URL格式不正确";
        } else if (downloadErrorCode == ERROR_CODE_GET_FILEINFO_ERROR) {
            return "获取文件服务器文件信息失败";
        } else if (downloadErrorCode == ERROR_UNKNOW_DOWNLOAD_TYPE) {
            return "不支持的下载方式";
        } else if (downloadErrorCode == ERROR_CODE_FILE_NOT_COORECT) {
            return "文件与其hash值不匹配";
        } else if (downloadErrorCode == ERROR_CODE_CONN_NO_LOCALPATH) {
            return "本地路径非法";
        } else if (downloadErrorCode == ERROR_CODE_CONN_NO_HASH) {
            return "没有hash值";
        } else if (downloadErrorCode == ERROR_CODE_NOT_IN_DOWNLOADTIME) {
            return "不在下载时间段内";
        } else {
            return "";
        }

    }

}
