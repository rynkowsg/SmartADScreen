package cn.com.smartadscreen.presenter.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.FileIOUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.com.smartadscreen.model.db.entity.App;
import cn.com.smartadscreen.model.db.entity.BroadcastTable;
import cn.com.smartadscreen.model.db.entity.Screen;
import cn.com.smartadscreen.model.db.manager.BroadcastTableHelper;
import cn.com.smartadscreen.utils.AppFileUtils;
import cn.com.startai.smartadh5.R;


/**
 * 作用：当 NMC 警告 ROM 容量不足时，删除不需要的文件（除：当前播放播表，所有需要延时播放的播表，未下载完成的播表）
 */
public class ClearDataIntentService extends IntentService {

    /**
     * 描述文件后注明列表
     */
    private List<String> descriptionFileSuffixList = new ArrayList<>();
    private File dataFolderFile;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private ClearDataIntentService(String name) {
        super(name);
    }

    public ClearDataIntentService() {
        this("ClearDataIntentService");
    }

    private Set<String> useFilePaths;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    BroadcastTableHelper mBroadcastTableHelper;

    @Override
    protected void onHandleIntent(Intent intent) {
        descriptionFileSuffixList.clear();
        descriptionFileSuffixList.addAll(Arrays.asList(
                getApplicationContext()
                        .getResources()
                        .getStringArray(R.array.description_file_suffix)
        ));

        useFilePaths = new HashSet<>();
        mBroadcastTableHelper = BroadcastTableHelper.getInstance();
        //获取项目 data 目录
        String dataFolder = AppFileUtils.getAppDataFolderPath();

        dataFolderFile = new File(dataFolder);
        //获得该目录下的所有文件目录
        File[] childFiles = new File(dataFolder).listFiles();

        List<File> allFiles = Arrays.asList(childFiles);

        Collections.sort(allFiles, (o1, o2) -> {
            if (o1.lastModified() > o2.lastModified()) {
                return 1;
            } else if (o1.lastModified() < o2.lastModified()) {
                return -1;
            } else {
                return 0;
            }
        });

        if(allFiles.isEmpty()) {
            return;
        }

        //所有当前要用到的播表
        List<BroadcastTable> usedTables = mBroadcastTableHelper.getAllNeedUsedBroadcastTable();

        for (File file : allFiles) {
            DeleteFileDescription description = fileIsUsed(usedTables, file.getAbsolutePath());
            if (!description.isUsed) {
                Logger.d("待删除文件路径 ==> " + description.filePath + "\n待删除描述文件路径 ==>" + description.descriptionFilePath);
                //播表文件路径
                String filePath = description.filePath;
                //描述文件路径
                String descriptionFilePath = description.descriptionFilePath;

                if (!TextUtils.isEmpty(filePath)) {
                    File deleteFile = new File(filePath);
                    if (deleteFile.exists()) {
                        if(deleteFile.isFile()) {
                            deleteFile.delete();
                        } else {
                            delDir(deleteFile);
                        }
                    }
                }

                if (!TextUtils.isEmpty(descriptionFilePath)) {
                    File descriptionFile = new File(descriptionFilePath);
                    if (descriptionFile.exists()) {
                        if(descriptionFile.isFile()) {
                            descriptionFile.delete();
                        } else {
                            delDir(descriptionFile);
                        }
                    }
                }
                break;
            }
        }

    }

    /**
     * 判断当前传入的文件是否被使用，并且返回 文件路径 和 描述文件路径
     * @param allUsedTable
     * @param deleteFilePath
     * @return
     */
    public DeleteFileDescription fileIsUsed(List<BroadcastTable> allUsedTable, String deleteFilePath) {
        //播表文件路径
        String filePath = deleteFilePath;
        //描述文件路径
        String descriptionFilePath = null;
        String fileSuffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        //deleteFilePath 字段所表示的文件是否为描述文件
        boolean isDescriptionFile = false;

        File deleteFile = new File(filePath);
        //如果后缀名是描述文件类型
        if (descriptionFileSuffixList.contains(fileSuffix) && deleteFile.isFile()) {
            descriptionFilePath = filePath;
            isDescriptionFile = true;
            String fileContent = FileIOUtils.readFile2String(filePath, "UTF-8");
            JSONObject fileContentJson = JSON.parseObject(fileContent);
            //将文件路径修改为 描述文件中的路径
            filePath = fileContentJson.getString("path");
            if(TextUtils.isEmpty(filePath)) {
                //如果描述内容中的文件路径为 空
                return new DeleteFileDescription(false, null, descriptionFilePath);
            }
            File relevanceFile = new File(filePath);
            if (!relevanceFile.exists()) {
                //如果描述内容中的文件路径指向的文件不存在
                return new DeleteFileDescription(false, null, descriptionFilePath);
            }
        } else if(deleteFile.isDirectory()) {
            //如果传入的路径是文件夹
            return new DeleteFileDescription(false, filePath, null);
        }

        //判断当前播表文件是否被用到
        for (BroadcastTable table : allUsedTable) {
            List<Screen> screens = table.getScreens();
            for (Screen screen : screens) {
                List<App> apps = screen.getApps();
                for (App app : apps) {
                    JSONArray items = JSON.parseArray(app.getItems());
                    for (int i = 0; i < items.size(); i++) {
                        JSONArray item = items.getJSONArray(i);
                        for (int j = 0; j < item.size(); j++) {
                            JSONObject itemItem = item.getJSONObject(j);
                            if (itemItem.containsKey("path") && itemItem.getString("path").equals(filePath)) {
                                return new DeleteFileDescription(true);
                            }

                        }
                    }
                }
            }
        }

        if (!isDescriptionFile) {
            //如果 deleteFilePath 所代表的文件不是描述文件，即为播表文件
            //获得对应的描述文件路径
            descriptionFilePath = getRelevanceDescriptionFilePath(filePath);
        }

        return new DeleteFileDescription(false, filePath, descriptionFilePath);
    }

    /**
     * 获得对应的描述文件路径
     * @param filePath 播表文件路径
     * @return 对应的描述文件路径
     */
    public String getRelevanceDescriptionFilePath(String filePath) {
        //获得所有的描述文件
        File[] descriptionFiles = dataFolderFile.listFiles(file -> {
            String fileName = file.getName();
            String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            return descriptionFileSuffixList.contains(fileSuffix);
        });

        for (File descriptionFile : descriptionFiles) {
            if(descriptionFile.isDirectory()) {
                continue;
            }
            String fileContent = FileIOUtils.readFile2String(descriptionFile.getAbsolutePath(), "UTF-8");
            JSONObject fileContentJson = JSON.parseObject(fileContent);
            String descriptionFilePath = fileContentJson.getString("path");
            if (!TextUtils.isEmpty(descriptionFilePath) && descriptionFilePath.equals(filePath)) {
                return descriptionFile.getAbsolutePath();
            }
        }
        return null;
    }

    private void delDir(File f) {
        // 判断是否是一个目录, 不是的话跳过, 直接删除; 如果是一个目录, 先将其内容清空.
        if(f.isDirectory()) {
            // 获取子文件/目录
            File[] subFiles = f.listFiles();
            // 遍历该目录
            for (File subFile : subFiles) {
                // 递归调用删除该文件: 如果这是一个空目录或文件, 一次递归就可删除. 如果这是一个非空目录, 多次
                // 递归清空其内容后再删除
                delDir(subFile);
            }
        }
        // 删除空目录或文件
        f.delete();
    }


    public List<String> getUseFiles(BroadcastTable bt, File[] childFile) {

        List<String> filePaths = new ArrayList<>();

        List<String> suffixList = Arrays.asList("imageplayer", "mediaplayer",
                "textplayer", "urlplayer", "musicplayer");

        List<Screen> screens = bt.getScreens();
        for (Screen screen : screens) {
            List<App> apps = screen.getApps();
            for (App app : apps) {
                JSONArray items = JSON.parseArray(app.getItems());
                for (int i = 0; i < items.size(); i++) {
                    JSONArray item = items.getJSONArray(i);
                    for (int j = 0; j < item.size(); j++) {
                        JSONObject itemItem = item.getJSONObject(j);
                        if (itemItem.containsKey("path")) {
                            filePaths.add(itemItem.getString("path"));
//                            useFilePaths.add(itemItem.getString("path"));
                        }

                        // 循环判断 itemItem 是否有附属文件
                        String fileContent;
                        String fileSuffix;
                        for (File file : childFile) {
                            fileSuffix = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".") + 1);
                            if (suffixList.contains(fileSuffix)) {
                                fileContent = FileIOUtils.readFile2String(file, "UTF-8");

                                JSONObject fileContentJson = JSON.parseObject(fileContent);
                                String itemFile = itemItem.getString("file");
                                String itemContent = itemItem.getString("content");

                                if (!TextUtils.isEmpty(itemFile)
                                        && itemFile.equals(fileContentJson.getString("file"))) {
                                    filePaths.add(file.getAbsolutePath());
//                                    useFilePaths.add(file.getAbsolutePath());
                                    break;
                                } else if (!TextUtils.isEmpty(itemContent)
                                        && itemContent.equals(fileContentJson.getString("content"))) {
                                    filePaths.add(file.getAbsolutePath());
//                                    useFilePaths.add(file.getAbsolutePath());
                                    break;
                                }
                            }
                        }

                    }
                }
            }
        }
        return filePaths;
    }

    private class DeleteFileDescription {
        /**
         * 是否被使用
         */
        boolean isUsed;
        /**
         * 文件路径
         */
        String filePath;
        /**
         * 描述文件路径
         */
        String descriptionFilePath;

        DeleteFileDescription(boolean isUsed, String filePath, String descriptionFilePath) {
            this.isUsed = isUsed;
            this.filePath = filePath;
            this.descriptionFilePath = descriptionFilePath;
        }

        DeleteFileDescription(boolean isUsed) {
            this.isUsed = isUsed;
        }
    }
}
