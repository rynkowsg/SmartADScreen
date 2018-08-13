package cn.com.smartadscreen.model.bean;

/**
 * Created by Taro on 2017/4/21.
 * 初始化任务推送实体类
 */
public class InitTaskPush {

    private long id;
    private TaskPush startTask;
    private TaskPush endTask;

    public InitTaskPush() {}

    public InitTaskPush(long id, TaskPush startTask, TaskPush endTask) {
        this.id = id;
        this.startTask = startTask;
        this.endTask = endTask;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TaskPush getStartTask() {
        return startTask;
    }

    public void setStartTask(TaskPush startTask) {
        this.startTask = startTask;
    }

    public TaskPush getEndTask() {
        return endTask;
    }

    public void setEndTask(TaskPush endTask) {
        this.endTask = endTask;
    }
}
