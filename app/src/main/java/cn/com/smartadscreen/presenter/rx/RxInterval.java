package cn.com.smartadscreen.presenter.rx;

import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import cn.com.smartadscreen.utils.HomeDataUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static org.greenrobot.eventbus.EventBus.TAG;

public class RxInterval {

    private  TextView textView;
    private Disposable subscribe;

    public RxInterval(TextView textView){
        this.textView=textView;
    }

    public void setTimeData(){
        subscribe = Observable.interval(60, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        String timeData = HomeDataUtils.getInstance().getTimeData();

                        if (timeData!=null){
                            textView.setText(timeData);
                        }else{
                            textView.setText("系统时间异常");
                        }
                    }
                });
    }

    public void cancleTimeData(){
        subscribe.dispose();
    }
}
