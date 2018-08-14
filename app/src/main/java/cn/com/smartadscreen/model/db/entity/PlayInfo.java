package cn.com.smartadscreen.model.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PlayInfo implements Parcelable {
    public static final String STR_TYPE_IMAGE = "imageplayer";
    public static final String STR_TYPE_VIDEO = "mediaplayer";
    public static final String STR_TYPE_MUSIC = "musicplayer";
    /**
     * 时钟
     */
    public static final int TYPE_CLOCK = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_MUSIC = 2;
    public static final int TYPE_IMAGE = 3;
    /**
     * 播表类型
     */
    public static final int TYPE_AD = 4;

    public static int SOURCE_TYPE_LOCAL = 1;
    public static int SOURCE_TYPE_ONLINE = 2;
    @Id(autoincrement = true)
    private Long id;
    private Integer type;           //类型
    private String album;           //专辑
    private String author;          //作者
    private String fileType;        //文件类型
    private String filePath;        //文件路径
    private String fileName;        //文件名
    private Integer fileId;         //fileId
    private Long fileSize;          //文件大小
    private Long duration;          //时间
    private Integer sourceType;     //来源类型 本地、网络
    private String coverPath;       //封面图（本地路径）

    @Transient
    private boolean isPlaying;//是否当前正在播放
    /**
     * private Long id;
     private Integer type;           //类型
     private String album;           //专辑
     private String author;          //作者
     private String fileType;        //文件类型
     private String filePath;        //文件路径
     private String fileName;        //文件名
     private Integer fileId;         //fileId
     private Long fileSize;          //文件大小
     private Long duration;          //时间
     private Integer sourceType;     //来源类型 本地、网络
     private String coverPath;       //封面图（本地路径）
     */
    protected PlayInfo(Parcel in) {
        id = in.readLong() == -1 ? null : in.readLong();
        type = in.readInt();
        fileId = in.readInt();
        fileSize = in.readLong();
        duration = in.readLong();
        sourceType = in.readInt();
        album = in.readString();
        author = in.readString();
        fileType = in.readString();
        filePath = in.readString();
        fileName = in.readString();
        coverPath = in.readString();
        isPlaying = in.readByte() != 0;
    }

    @Generated(hash = 2024515695)
    public PlayInfo(Long id, Integer type, String album, String author,
            String fileType, String filePath, String fileName, Integer fileId,
            Long fileSize, Long duration, Integer sourceType, String coverPath) {
        this.id = id;
        this.type = type;
        this.album = album;
        this.author = author;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileId = fileId;
        this.fileSize = fileSize;
        this.duration = duration;
        this.sourceType = sourceType;
        this.coverPath = coverPath;
    }

    @Generated(hash = 620864500)
    public PlayInfo() {
    }



    @Override
    public String toString() {
        return "PlayInfo{" +
                "filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
    public static final Creator<PlayInfo> CREATOR = new Creator<PlayInfo>() {
        @Override
        public PlayInfo createFromParcel(Parcel in) {
            return new PlayInfo(in);
        }

        @Override
        public PlayInfo[] newArray(int size) {
            return new PlayInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id == null ? -1 : id);
        dest.writeInt(type);
        dest.writeLong(fileSize);
        dest.writeLong(duration);
        dest.writeInt(sourceType);
        dest.writeString(album);
        dest.writeString(author);
        dest.writeString(fileType);
        dest.writeString(filePath);
        dest.writeString(fileName);
        dest.writeString(coverPath);
        dest.writeByte((byte) (isPlaying ? 1 : 0));
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileId() {
        return this.fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getCoverPath() {
        return this.coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}
