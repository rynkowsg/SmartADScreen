package cn.com.startai.smartadh5.locallog;

import android.support.annotation.NonNull;
import android.widget.Toast;

import cn.com.startai.smartadh5.extension.config.Config;
import cn.com.startai.smartadh5.main.application.Application;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Taro on 2017/3/13.
 * Toast 工具类
 */
@SuppressWarnings("all")
public class SmartToast {
    private static Toast mToast = null;

    public static void success(String message){
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->
                        Toasty.success(Config.getContext(), message, Toast.LENGTH_SHORT, true).show()
                );
    }

    public static void wraning(String message){
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->
                        Toasty.warning(Config.getContext(), message, Toast.LENGTH_SHORT, true).show()
                );
    }

    public static void error(String message){
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->
                        Toasty.error(Config.getContext(), message, Toast.LENGTH_SHORT, true).show()
                );
    }

    public static void info(String message){
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->
                    Toasty.info(Config.getContext(), message, Toast.LENGTH_SHORT, true).show()
                );
    }

    public static void normal(String message){
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    cancel();
                    mToast = Toasty.normal(Application.getContext(), message, Toast.LENGTH_SHORT);
                    mToast.show();
                });
    }

    public static void normalShallowColor(@NonNull String message){
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Toasty.Config.getInstance().setTextColor(0xc8ffffff).apply();
                    cancel();
                    mToast = Toasty.custom(Application.getContext(), message, null, 0x60000000, Toast.LENGTH_SHORT, false, true);
                    mToast.show();
                    Toasty.Config.reset();
                });
    }

    public static void cancel(){
        if(mToast != null){
            mToast.cancel();
            mToast = null;
        }
    }


}
