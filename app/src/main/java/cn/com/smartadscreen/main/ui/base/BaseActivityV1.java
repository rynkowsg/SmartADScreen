package cn.com.smartadscreen.main.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.alibaba.fastjson.JSONObject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.smartadscreen.main.ui.activity.SetupActivity;
import cn.com.smartadscreen.utils.DeviceInfoUtils;

import cn.startai.apkcommunicate.CommunicateType;
import cn.startai.apkcommunicate.StartaiCommunicate;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/17
 * 作用：activity 基类
 */

public abstract class BaseActivityV1 extends AppCompatActivity implements IActivity{

    private Unbinder unbinder;
    protected boolean isForeground = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        DeviceInfoUtils.getInstance().getCpuArchitecture();
        this.initData();
        this.initView();
        this.addViewListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isForeground = true;
    }

    //是否需要拦截返回按键
    protected abstract boolean shouldInterceptBack();

    public void showActivityForResult(Class<?> cls, Bundle extras, int requestCode) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(this, cls);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        // 调起APP设置界面
        if(shouldInterceptBack()) {
            SetupActivity.actionStart(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            JSONObject keyObject = new JSONObject();
            keyObject.put("keyCode", keyCode);
            StartaiCommunicate.getInstance().send(this.getApplicationContext(), CommunicateType.COMMUNICATE_TYPE_KEY, keyObject.toString());
            return false ;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isForeground = false;
    }

    public boolean isForeground(){
        return isForeground;
    }

    /**
     * 获取布局id
     */
    protected abstract int getLayoutId();

}
