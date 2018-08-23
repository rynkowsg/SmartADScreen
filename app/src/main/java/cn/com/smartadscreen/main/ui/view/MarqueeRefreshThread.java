package cn.com.smartadscreen.main.ui.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.SurfaceHolder;

import com.facebook.stetho.common.LogUtil;

import java.util.ArrayList;


/**
 * author: Guoqiang_Sun
 * date : 2018/4/20 0020
 * desc :
 */
public class MarqueeRefreshThread implements Runnable {

    private String TAG = AutoScrollMarqueeView.TAG;

    private SurfaceHolder holder;
    private TextPaint mTextPaint;

    public MarqueeRefreshThread(SurfaceHolder holder, TextPaint mTextPaint) {
        this.holder = holder;
        this.mTextPaint = mTextPaint;
    }

    private final Object mWHobj = new Object();

    private int mWidgetWidth;
    private int mWidgetHeight;

    // 控件宽高
    public void setWidgetWH(int width, int height) {
        this.mWidgetWidth = width;
        this.mWidgetHeight = height;

        synchronized (mWHobj) {
            mWHobj.notify();
        }

    }

    private int stepSpeed = 3;

    public void setSpeed(int speed) {
        this.stepSpeed = speed;
    }

    private String originalMsg;

    public void setMsg(String msg) {
        originalMsg = msg;
    }

    private Frame mCurFrame = new Frame();
    private Frame mSecondFrame = new Frame();

    private void swap() {

        LogUtil.e(TAG, " swap curPoint:" + mCurPoint);

        if (mCurPoint >= mShowTxts.size()) {
            mCurPoint = 0;
        }
        int secondPoint = mCurPoint + 1;
        if (secondPoint >= mShowTxts.size()) {
            secondPoint = 0;
        }

        Frame showTxt = mShowTxts.get(mCurPoint);
        Frame secondShowTxt = mShowTxts.get(secondPoint);

        mCurFrame.msg = showTxt.msg;
        mCurFrame.measureWidth = showTxt.measureWidth;

        mSecondFrame.msg = secondShowTxt.msg;
        mSecondFrame.measureWidth = secondShowTxt.measureWidth;

        mCurPoint++;

    }

    private void setStartDrawX(float x) {
        this.drawX = x;
    }

    private Rect rect;

    private float baseLineOffset = 0;//文字基线，相对于绘制的中间线的偏移量

    private ArrayList<Frame> mShowTxts;

    private void init() {

        this.mShowTxts = splitData(originalMsg);

        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        baseLineOffset = (fontMetrics.bottom - fontMetrics.top) / 2 + fontMetrics.top;
        setStartDrawX(mWidgetWidth);
        drawY = mWidgetHeight / 2 - baseLineOffset;
        rect = new Rect(0, 0, mWidgetWidth, mWidgetHeight);
        mCurFrame.step = 0;
        mSecondFrame.step = 0;

        LogUtil.e(TAG, " MarqueeRefreshThread init> mWidgetWidth(" + mWidgetWidth + ");mWidgetHeight(" + mWidgetHeight + ")baseLineOffSet:" + baseLineOffset + ", drawY:" + drawY);

        swap();

    }

    private boolean run;

    public void start() {

        this.run = true;
    }

    public void stop() {
        this.run = false;
    }

    private PorterDuffXfermode mPorterDuffXfermodeClear = new PorterDuffXfermode(
            PorterDuff.Mode.CLEAR);
    private PorterDuffXfermode mPorterDuffXfermodeSrc = new PorterDuffXfermode(
            PorterDuff.Mode.SRC);
    private Paint mLayerClearPaint = new Paint();

    private void doLayerClear(Canvas canvas) {
        mLayerClearPaint.setXfermode(mPorterDuffXfermodeClear);
        canvas.drawPaint(mLayerClearPaint);
        mLayerClearPaint.setXfermode(mPorterDuffXfermodeSrc);
    }

    private float drawY;
    private float drawX;


    private int mCurPoint = 0;

    private void drawCanvas(Canvas mCanvas) {
        mCanvas.save();
        doLayerClear(mCanvas);

        float x = drawX - mCurFrame.step;
        mCurFrame.step += stepSpeed;
        float y = drawY;
        mCanvas.drawText(mCurFrame.msg, x, y, mTextPaint);
//        LogUtil.v(TAG, " MarqueeView drawText x(" + x + ") +y(" + y + ")");

        if (mCurFrame.step > mCurFrame.measureWidth) {
            // 可以绘制第二帧
//            LogUtil.v(TAG, " MarqueeView drawText first finish");

            if (mCurPoint >= mShowTxts.size()) {
                // 最后一帧
                if (mCurFrame.step > (mCurFrame.measureWidth + drawX)) {
                    LogUtil.e(TAG, " draw the last frame finish");
                    swap();
                    mCurFrame.step = 0;
                    mSecondFrame.step = 0;
                }

            } else {

                float x2 = drawX - mSecondFrame.step;
                mSecondFrame.step += stepSpeed;
                float y2 = drawY;
                mCanvas.drawText(mSecondFrame.msg, x2, y2, mTextPaint);
//                LogUtil.v(TAG, " MarqueeView drawText x(" + x + ") +y(" + y + ")");

                if (mCurFrame.step > (mCurFrame.measureWidth + drawX)) {
                    // 第一帧绘制结束
                    LogUtil.e(TAG, " last frame draw finish ; step(" + mCurFrame.step + ") +>[textWidth(" + mSecondFrame.measureWidth + ")+drawX(" + drawX + ")],step2(" + mSecondFrame.step + ")");

                    swap();

                    mCurFrame.step = drawX;
                    mSecondFrame.step = 0;

                }

            }


        }
        mCanvas.restore();
    }

    @Override
    public void run() {

        if (mWidgetWidth <= 0 || mWidgetHeight <= 0) {
            LogUtil.e(TAG, " MarqueeRefreshThread mWidgetWidth(" + mWidgetWidth + ")<=0;mWidgetHeight(" + mWidgetHeight + ")<=0;wait");
            synchronized (mWHobj) {
                try {
                    mWHobj.wait(1000 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!this.run) {
            LogUtil.e(TAG, " this.run == false ");
            return;
        }

        init();

        LogUtil.v(TAG, " MarqueeRefreshThread start draw... ");

        while (this.run) {

            synchronized (holder) {
                Canvas mCanvas = null;

                try {
                    mCanvas = holder.lockCanvas(rect);
                    mCanvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.SRC);
                    if (mCanvas == null) {
                        LogUtil.e(TAG, " lockCanvas canvas == null ");
                        continue;
                    }

                    long checkEmptyMillis = System.currentTimeMillis();
                    //绘制
                    drawCanvas(mCanvas);

                    long diff = (System.currentTimeMillis() - checkEmptyMillis);


                } catch (Exception e) {

                } finally {
                    if (mCanvas != null) {
                        try {
                            holder.unlockCanvasAndPost(mCanvas);
                        } catch (Exception e) {
                        }
                    }

                }


            }


        }

    }


    private class Frame {
        public String msg;
        public int length;
        public int id;
        public float measureWidth;
        public float step;

        @Override
        public String toString() {
            return "id" + id + "，msg.length:" + msg.length() + ", length:" + length + ",measureWidth:" + measureWidth;
        }
    }

    private static final int MAX_MSG_LENGTH = 500;

    private ArrayList<Frame> splitData(String marqueeString) {
        ArrayList<Frame> data = new ArrayList<>();

        final int length = marqueeString.length();

        float strPx = mTextPaint.measureText(marqueeString);

        if (strPx > mWidgetWidth * 3 && length > MAX_MSG_LENGTH) {
//        if (strPx > 20) {
            int i;
            if (length % MAX_MSG_LENGTH == 0) {
                i = length / MAX_MSG_LENGTH;
            } else {
                i = length / MAX_MSG_LENGTH + 1;
            }

            int count = length / i;

            LogUtil.v(TAG, " length:" + length + "/(" + MAX_MSG_LENGTH + ") = i: " + i + " count:" + count);

            for (int j = 0; j < i; j++) {

                Frame st = new Frame();
                st.id = j;
                if (j == (i - 1)) {
                    st.length = length - j * count;
                    st.msg = marqueeString.substring(j * count, j * count + st.length);
                } else {
                    st.length = count;
                    st.msg = marqueeString.substring(j * count, j * count + count);
                }
                st.measureWidth = mTextPaint.measureText(st.msg);
                data.add(st);

                LogUtil.v(TAG, "showTxt:" + st.toString());
            }
        } else {
            Frame st = new Frame();
            st.id = 0;
            st.length = length;
            st.msg = marqueeString;
            st.measureWidth = strPx;
            LogUtil.v(TAG, "showTxt:" + st.toString());
            data.add(st);
        }

        return data;

    }


}
