package cn.com.smartadscreen.main.ui.pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/7/18
 * 作用：垂直的ViewPager，参考：https://github.com/kaelaela/VerticalViewPager
 */

public class VerticalViewPager extends ViewPager {
    private float mDownX;
    private float mDownY;
    private float mTouchSlop;
    private boolean mIntercept = true;

    public VerticalViewPager(Context context) {
        this(context, null);
        init(context);
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        //设置切换动画
        setPageTransformer(true, new StackTransformer());
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;
        /*
         *  对当前的event对象中的x和y的值进行重新设定，当这个event重新分发给其它的view时，
         *  就会按照设定后的值进行处理相关的逻辑。设定后，event这个对象通过event.getX()
         *  等方法获取到的值就是经过重新设定后的值了。和原来的event相关的值就不同了。
         */
        event.setLocation(swappedX, swappedY);

        return event;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(mIntercept) {
            boolean intercept = super.onInterceptTouchEvent(swapTouchEvent(event));
            //解决与内部可横向滑动的ViewPager的事件冲突，参考：https://juejin.im/entry/57ab5f1e165abd00617c8e67
            float x = event.getRawX();
            float y = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = x;
                    mDownY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(x - mDownX);
                    float dy = Math.abs(y - mDownY);
                    if (!intercept && dy > mTouchSlop && dy * 0.5f > dx) {
                        intercept = true;
                    }
                    break;
                default:
                    break;
            }
            //将交换后的 x,y 值在交换一次，恢复交换前的状态
            swapTouchEvent(event);
            return intercept;
        } else {
            return false;
        }
    }

    public void setIsIntercept(boolean isIntercept){
        mIntercept = isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapTouchEvent(ev));
    }

    //切换动画
    private class StackTransformer implements PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            page.setTranslationX(page.getWidth() * -position);
            page.setTranslationY(position < 0 ? position * page.getHeight() : 0f);
        }
    }

}
