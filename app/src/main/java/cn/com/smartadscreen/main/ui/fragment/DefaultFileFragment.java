package cn.com.smartadscreen.main.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.smartadscreen.main.ui.base.BaseFragment;
import cn.com.smartadscreen.model.bean.DeviceInfoBean;
import cn.com.smartadscreen.model.bean.LocalFileDesc;
import cn.com.smartadscreen.utils.TimerUtils;
import cn.com.startai.smartadh5.R;

/**
 * Created by Taro on 2017/4/19.
 * DefaultFragment 的第二个子 Fragment
 */
public  class DefaultFileFragment extends BaseFragment {

    @BindView(R.id.music_text)
    TextView musicText;
    @BindView(R.id.photo_text)
    TextView photoText;
    @BindView(R.id.vedio_text)
    TextView vedioText;
    @BindView(R.id.file_text)
    TextView fileText;
    private DefaultFragment mDefaultFragment;
    private LocalFileDesc.Content json;
    private long size;
    private String sizeString;
    Unbinder unbinder;
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_default_qr, container, false);
    }

    @Override
    public void initData() {
        mDefaultFragment = new DefaultFragment();
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView() {

    }

    @Override
    public void addViewListener() {

    }
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void localFile(DeviceInfoBean bean) {
        LocalFileDesc localFileDesc = bean.getLocalFileDesc();
        ArrayList<LocalFileDesc.Content> list=localFileDesc.getList();
        for (int i = 0; i<list.size(); i++) {
            Log.i("DefaultQRFragment","json="+json);
            Log.i("DefaultQRFragment","size="+size);
            Log.i("DefaultQRFragment","sizeString="+sizeString);
            if(i==0){
                json = list.get(0);
                size= json.getSize();
                sizeString = String.valueOf(size);
                Log.i("DefaultQRFragment","json="+json);
                Log.i("DefaultQRFragment","size="+size);
                Log.i("DefaultQRFragment","sizeString="+sizeString);
                photoText.setText("("+ sizeString +")");
            }else if (i==1) {
                json = list.get(1);
                size= json.getSize();
                sizeString = String.valueOf(size);
                vedioText.setText("("+ sizeString +")");
            }else if (i==2) {
                json = list.get(2);
                size= json.getSize();
                sizeString = String.valueOf(size);
                musicText.setText("("+ sizeString +")");
            }else if (i==3) {
                json = list.get(3);
                size= json.getSize();
                sizeString = String.valueOf(size);
                fileText.setText("("+ sizeString +")");
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        TimerUtils.close("movePager");
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);


        return rootView;
    }
}
