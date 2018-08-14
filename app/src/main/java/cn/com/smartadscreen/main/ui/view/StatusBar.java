package cn.com.smartadscreen.main.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;

import butterknife.ButterKnife;
import cn.com.smartadscreen.utils.GlideUtils;
import cn.com.startai.smartadh5.R;


/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/8
 * 作用：状态栏（Logo, 时间显示）
 */

public class StatusBar extends RelativeLayout {
    private Context context;
    private RelativeLayout root;
    private ImageView ivLogo;
    private TextClock tcTime;

    private int timeColor;//时间控件颜色
    private float timeSize;//时间控件字体大小
    private boolean showTime;
    private boolean showLogo;
    private String timeFormat;


    public StatusBar(Context context) {
        this(context, null);
    }

    public StatusBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs){
        root = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.layout_status_bar, this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StatusBar);
        timeColor = ta.getColor(R.styleable.StatusBar_timeColor, 0x80808080);
        timeSize = ta.getDimension(R.styleable.StatusBar_timeSize, 24);
        showTime = ta.getBoolean(R.styleable.StatusBar_showTime, false);
        float logoAlpha = ta.getFloat(R.styleable.StatusBar_logoAlpha, 0.5f);
        showLogo = ta.getBoolean(R.styleable.StatusBar_showLogo, false);
        Drawable logSrc = ta.getDrawable(R.styleable.StatusBar_logoSrc);
        ta.recycle();

        ivLogo = ButterKnife.findById(root, R.id.iv_logo);
        tcTime = ButterKnife.findById(root, R.id.tc_time);


        tcTime.setTextColor(timeColor);
        tcTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, timeSize);
        tcTime.setVisibility(showTime ? VISIBLE : GONE);

        ivLogo.setAlpha(logoAlpha);
        ivLogo.setVisibility(showLogo ? VISIBLE : GONE);

//        Paint.FontMetrics fontMetrics
//        Paint.FontMetricsInt fontMetrics = tcTime.getPaint().getFontMetricsInt();
//        Logger.d(fontMetrics.toString() + "\n" + tcTime.getHeight());
        if(logSrc != null)
            ivLogo.setImageDrawable(logSrc);
    }

    public void setTimeFormat24Hour(String format){
        tcTime.setFormat24Hour(format);
    }

    public String getTimeFormat24Hour(){
        return tcTime.getFormat24Hour().toString();
    }

    public void setTimeShow(boolean showTime){
        if(this.showTime != showTime) {
            this.showTime = showTime;
            tcTime.setVisibility(showTime ? VISIBLE : GONE);
        }
    }

    /**
     * 设置时间字体大小
     */
    public void setTimeSizeSp(float sp){
        tcTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    /**
     * 设置 Logo 图片隐藏与显示
     */
    public void setLogoShow(boolean showLogo){
        if(this.showLogo != showLogo) {
            this.showLogo = showLogo;
            ivLogo.setVisibility(showLogo ? VISIBLE : GONE);
        }
    }

    /**
     * 设置logo图片透明度
     */
    public void setLogoAlpha(float alpha){
        ivLogo.setAlpha(alpha);
    }

    /**
     * 设置图片
     */
    public void setLogoUri(String filePath){
//        Uri uri = Uri.fromFile(new File(filePath));
//        ivLogo.setImageURI(uri);
        GlideUtils.setLogoImage(ivLogo, filePath);
    }
}
