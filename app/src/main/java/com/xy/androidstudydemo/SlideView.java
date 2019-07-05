package com.xy.androidstudydemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xy.common.utils.DipPixelUtil;
import com.xy.common.utils.LogUtils;

import java.text.DecimalFormat;

/**
 * Created by xieying on 2019/7/5.
 * Description：
 */
public class SlideView extends View {

    private Paint mPaint;

    private Paint mRectPaint;

    private Paint mCirclePaint;

    private int defaultWidth;

    private int defaultHeight;

    private int lineWidth;

    private int rectWidth;

    private int rectHeight;

    private int bigRadius;

    private int smallRadius;

    private float centerX;

    private boolean isInCircle;


    public SlideView(Context context) {
        this(context, null);
    }

    public SlideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        defaultWidth = DipPixelUtil.dip2px(200);
        defaultHeight = DipPixelUtil.dip2px(44);

        rectWidth = DipPixelUtil.dip2px(40);
        rectHeight = DipPixelUtil.dip2px(24);
        bigRadius = DipPixelUtil.dip2px(8);
        smallRadius = DipPixelUtil.dip2px(6);

        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(0xFFb0b0b0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(DipPixelUtil.dip2px(2));

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(DipPixelUtil.dip2px(2));
        mCirclePaint.setColor(0xFFffffff);

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.FILL);
        mRectPaint.setColor(0xFF111111);
    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        int width = getMeasureSize(widthSpec, DipPixelUtil.dip2px(defaultWidth));
        int height = getMeasureSize(heightSpec, DipPixelUtil.dip2px(defaultHeight));
        setMeasuredDimension(width, height);

        centerX = getMeasuredWidth() >> 1;

    }

    private int getMeasureSize(int measureSpec, int defaultSize) {
        int size = defaultSize;

        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);

        switch (measureMode) {
            case MeasureSpec.UNSPECIFIED:
                size = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                size = measureSize;
                break;
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int offsetWidth = rectWidth / 2 - bigRadius;

        int offsetHeight = rectHeight + DipPixelUtil.dip2px(4) + bigRadius;

        canvas.drawLine(rectWidth >> 1, offsetHeight,
                getWidth() - (rectWidth >> 1), offsetHeight, mPaint);
        canvas.drawCircle(centerX, offsetHeight
                , bigRadius - DipPixelUtil.dip2px(1), mCirclePaint);
        canvas.drawCircle(centerX, offsetHeight, smallRadius, mRectPaint);

        canvas.drawRoundRect(centerX - (rectWidth >> 1), 0, centerX + (rectWidth >> 1), rectHeight,
                DipPixelUtil.dip2px(4), DipPixelUtil.dip2px(4), mRectPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isInCircle(event.getX(), event.getY()))
                    isInCircle = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isInCircle) {
                    centerX = event.getX();
                    if (centerX < (rectWidth >> 1))
                        centerX = rectWidth >> 1;
                    if (centerX > (getMeasuredWidth() - (rectWidth >> 1)))
                        centerX = getMeasuredWidth() - (rectWidth >> 1);
                    precent();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isInCircle = false;
                break;
        }
        return true;
    }

    private boolean isInCircle(float x, float y) {

        int offsetHeight = rectHeight + DipPixelUtil.dip2px(4) + bigRadius;

//        LogUtils.d("offsetHeight = " + offsetHeight);
//
//
//        LogUtils.d("x = " + x + "  y = " + y);
//
//        LogUtils.d("small x = " + (centerX - bigRadius - 50) + "  big x = " + (centerX + bigRadius + 50));
//
////        LogUtils.d("small y = " + (offsetHeight - bigRadius - 50) + "  big y = " + (offsetHeight + bigRadius + 50));

        if (x > (centerX - bigRadius - 50) && x < (centerX + bigRadius + 50))
            return true;
        return false;

    }

    private void precent() {
        float width = getWidth() - rectWidth;
        float distance = centerX - (rectWidth >> 1);

        float percent = distance / width;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        LogUtils.d("percent = " + decimalFormat.format(percent));
    }

}
