package cn.com.smartadscreen.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import cn.com.smartadscreen.utils.JSONUtils;


/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/11
 *         作用：下载进度
 */

public class DownloadProgressBean implements Parcelable{

    private String url;
    private String errorCode;
    private String localPath;
    private String originName;
    private long downloadSize;
    private long totalSize;
    private int progress;
    private int origin;

    public DownloadProgressBean() {
    }

    protected DownloadProgressBean(Parcel in) {
        url = in.readString();
        errorCode = in.readString();
        localPath = in.readString();
        originName = in.readString();
        downloadSize = in.readLong();
        totalSize = in.readLong();
        progress = in.readInt();
        origin = in.readInt();
    }

    public static final Creator<DownloadProgressBean> CREATOR = new Creator<DownloadProgressBean>() {
        @Override
        public DownloadProgressBean createFromParcel(Parcel in) {
            return new DownloadProgressBean(in);
        }

        @Override
        public DownloadProgressBean[] newArray(int size) {
            return new DownloadProgressBean[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(errorCode);
        dest.writeString(localPath);
        dest.writeString(originName);
        dest.writeLong(downloadSize);
        dest.writeLong(totalSize);
        dest.writeInt(progress);
        dest.writeInt(origin);
    }
}
