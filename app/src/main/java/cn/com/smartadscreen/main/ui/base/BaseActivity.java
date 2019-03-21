package cn.com.smartadscreen.main.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import cn.com.smartadscreen.presenter.main.SetupPresenter;

/**
 * Created by Taro on 2017/3/13.
 * BaseActivity
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ButterKnife
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MediaUtils.toReportPlayStatusChangeMessage(this, "0x9975", -1, null, 0, PlayStateChangeBean.STATUS_PAUSE);
        EventBus.getDefault().unregister(this);
    }



    @Override
    public void onBackPressed() {
        // 调起APP设置界面
//        SetupPresenter.actionStart(this);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            JSONObject keyObject = new JSONObject();
//            keyObject.put("keyCode", keyCode);
//            StartaiCommunicate.getInstance().send(this.getApplicationContext(), CommunicateType.COMMUNICATE_TYPE_KEY, keyObject.toString());
//            return false ;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
