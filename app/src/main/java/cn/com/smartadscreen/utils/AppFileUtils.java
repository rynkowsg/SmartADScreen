package cn.com.smartadscreen.utils;

import java.io.File;

import cn.com.smartadscreen.model.sp.SPManager;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/4
 * 作用：文件操作工具类
 */

public final class AppFileUtils {

    /**
     * 获取项目 data 目录
     *
     * @return
     */
    public static String getAppDataFolderPath() {

        String dataFolder = SPManager.getInstance().getAppDataPath();
        File dataFile = new File(dataFolder);
        if (!dataFile.exists()) {
            dataFile.mkdirs();
        }
        return dataFolder;
    }
}
