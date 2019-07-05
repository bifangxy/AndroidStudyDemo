package com.xy.androidstudydemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.xy.common.utils.DipPixelUtil;
import com.xy.common.utils.LogUtils;

/**
 * Created by xieying on 2019/7/5.
 * Description：
 */
public class ScaleView extends View {

    private Paint mBackgroundPiant;

    private Paint mRectPaint;

    private int mDefauleWidth;

    private int mDefaultHeight;

    private int mRectWidth;

    private int mRectHeight;

    private RectF mBackgroundRect;

    private float mRectTop;

    private float mRectLeft;

    private RectF mRectF;

    private float raidus;

    private float oldX;

    private float oldY;

    private boolean isInRect;

    public ScaleView(Context context) {
        this(context, null);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mDefauleWidth = DipPixelUtil.dip2px(140);
        mDefaultHeight = DipPixelUtil.dip2px(90);

        mRectWidth = DipPixelUtil.dip2px(40);
        mRectHeight = DipPixelUtil.dip2px(24);

        mRectLeft = (mDefauleWidth - mRectWidth) >> 1;
        mRectTop = (mDefaultHeight - mRectHeight) >> 1;

        raidus = DipPixelUtil.dip2px(4);


        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(mDefauleWidth, mDefaultHeight);
    }


    private void initPaint() {

        mBackgroundPiant = new Paint();
        mBackgroundPiant.setStyle(Paint.Style.FILL);
        mBackgroundPiant.setAntiAlias(true);
        mBackgroundPiant.setColor(getResources().getColor(R.color.com_color_black_60));

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setColor(0xffe22222);
        mRectPaint.setStrokeWidth(DipPixelUtil.dip2px(1.5f));

        mBackgroundRect = new RectF(0, 0, mDefauleWidth, mDefaultHeight);
        mRectF = new RectF();

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                LogUtils.d("----onLongClick------" + v.getX() + "    " + v.getY());
                return false;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mBackgroundRect, raidus, raidus, mBackgroundPiant);
        mRectF.set(mRectLeft, mRectTop, mRectLeft + mRectWidth, mRectTop + mRectHeight);
        canvas.drawRoundRect(mRectF, raidus, raidus, mRectPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isOnRect(event.getX(), event.getY())){
                    isInRect = true;
                    return true;
                }

                oldX = event.getX();
                oldY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isInRect) {
                    float newX = event.getX();
                    float newY = event.getY();
                    mRectLeft = (mRectLeft + newX - oldX);
                    mRectTop = mRectTop + newY - oldY;
                    if (mRectLeft < 5) {
                        mRectLeft = 5;
                    }
                    if (mRectTop < 5) {
                        mRectTop = 5;
                    }
                    if (mRectLeft > mDefauleWidth - mRectWidth - 5) {
                        mRectLeft = mDefauleWidth - mRectWidth - 5;
                    }
                    if (mRectTop > mDefaultHeight - mRectHeight - 5) {
                        mRectTop = mDefaultHeight - mRectHeight - 5;
                    }
                    oldX = newX;
                    oldY = newY;
                }
                break;
            case MotionEvent.ACTION_UP:
                isInRect = false;
                break;
        }
        invalidate();
        return super.onTouchEvent(event);

    }

    private boolean isOnRect(float x, float y) {
        if (x > mRectLeft && x < mRectLeft + mRectWidth) {
            return y > mRectTop && y < mRectTop + mRectHeight;
        }
        return false;
    }




}
