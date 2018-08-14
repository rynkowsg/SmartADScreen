package cn.com.smartadscreen.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.com.smartadscreen.app.Application;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/30
 * 作用：
 */

public class GlideUtils {

    public static void setLogoImage(ImageView view, String url){
        Glide.with(Application.getContext())
                .load(url)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(MyScreenUtils.dip2px(120), MyScreenUtils.dip2px(40))
                .into(view);
    }

}
