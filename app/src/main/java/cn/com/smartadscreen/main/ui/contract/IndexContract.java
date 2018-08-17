package cn.com.smartadscreen.main.ui.contract;

import com.alibaba.fastjson.JSONObject;

import cn.com.smartadscreen.model.bean.TaskPush;

public interface IndexContract {

    interface Presenter {
        void doTaskPushByPre(TaskPush taskPush);//发送任务

        void pushToWebByPre(TaskPush taskPush);//将播表 Screen 推送到 Web 中

        void delayToPushByPre(TaskPush taskPush);

        void checkSpotsByPre(JSONObject messageObj);

        void doWhileTaskPushByPre(long startTime, long splitLength);
    }

    interface View {
        void setCurrentItem(int item);

        int getCurrentItem();


    }
}
