package cn.com.smartadscreen.main.ui.base;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/17
 * 作用：
 */

public interface IActivity {

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 初始化view
     */
    void initView();

    /**
     * 添加view监听
     */
    void addViewListener();
}
