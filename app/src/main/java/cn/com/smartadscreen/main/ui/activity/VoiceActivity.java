package cn.com.startai.smartadh5.main.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.startai.smartadh5.R;
import cn.com.startai.smartadh5.locallog.SmartToast;
import cn.com.startai.smartadh5.processlogic.entity.event.OnCommand;
import cn.startai.apkcommunicate.CommunicateType;

/**
 * 语音识别的Activity
 */
public class VoiceActivity extends AppCompatActivity {

    @BindView(R.id.voice_tip)
    TextView mVideoTip;
    private String loadingTip = "正在识别中....";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        setTitle("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommand(OnCommand command) {
        switch (command.getCmd()) {
            case CommunicateType.COMMUNICATE_TYPE_KEY_RECOGNIZETIONING:
                mVideoTip.setText(loadingTip);
                break;
            case CommunicateType.COMMUNICATE_TYPE_KEY_RECOGNIZETION_SUCCESS:
                finish();
                break;
            case CommunicateType.COMMUNICATE_TYPE_KEY_RECOGNIZETION_FAILED:
                SmartToast.error(command.getErrMsg());
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
