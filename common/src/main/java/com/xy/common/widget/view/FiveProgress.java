package com.xy.common.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：created by xieying on 2019/5/18 12:14
 * 功能：
 */
public class FiveProgress extends View {
    private Paint mCirclePaint;

    private Paint mLinePaint;

    private Paint mTextPaint;

    private int maxProgress = 5;

    private int progress = 2;

    private RectF mRect;

    private int radius = 50;

    private int mWidth;

    private int mHeight;

    private int mYPosition;

    private int lineLength;

    private List<String> stringList;

    private float baseLineY;


    public FiveProgress(Context context) {
        this(context, null);
    }

    public FiveProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FiveProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint();
        initData();
    }


    private void initData() {
        mRect = new RectF();

    }

    private void initPaint() {
        mCirclePaint = new Paint();
        mCirclePaint.setColor(0xFFF7c528);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint();
        mLinePaint.setColor(0xFF000000);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(20);

        mTextPaint = new Paint();
        mTextPaint.setColor(0xFFFFFFFF);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(42);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        initPosition();
    }

    private void initPosition() {
        mYPosition = mHeight / 2;

        lineLength = (mWidth - radius * 2 * maxProgress) / (maxProgress - 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setDescribeText();
//        canvas.drawOval(new RectF(0, 0, 50, 50), mPaint);

        for (int i = 0; i < maxProgress; i++) {
            if (i < progress) {
                mCirclePaint.setColor(0xFFF7c528);
                mLinePaint.setColor(0xFFF7c528);
                mTextPaint.setColor(0XFF000000);
            } else {
                mCirclePaint.setColor(0x7F19191C);
                mLinePaint.setColor(0x7F19191C);
                mTextPaint.setColor(0XFFFFFFFF);
            }

            int startCircle = radius + i * (lineLength + 2 * radius);
            int startLine = (i + 1) * (2 * radius) + i * lineLength;
            canvas.drawCircle(startCircle, mYPosition, radius, mCirclePaint);
            if (i != 4) {
                canvas.drawLine(startLine, mYPosition, startLine + lineLength, mYPosition, mLinePaint);
            }
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            baseLineY = mYPosition + distance;
            canvas.drawText(stringList.get(i), startCircle, baseLineY, mTextPaint);
        }
    }

    private void setDescribeText() {
        stringList = new ArrayList<>();
        for (int i = 65; i <= 65 + maxProgress; i++) {
            char newChar = (char) i;
            stringList.add(String.valueOf(newChar));
        }
    }

    public void setMaxProgress(int maxProgress) throws Exception {
        if (maxProgress < 1 || maxProgress > 26) {
            throw new Exception("maxProgress can not below 0 and can not 大于 26");
        }
        this.maxProgress = maxProgress;
        setDescribeText();
        initPosition();
    }
}
