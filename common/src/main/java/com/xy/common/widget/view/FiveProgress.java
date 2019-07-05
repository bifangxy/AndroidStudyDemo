package com.xy.common.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：created by xieying on 2019/5/18 12:14
 * 功能：
 */
public class FiveProgress extends View {

    private int DEFAULT_PADDING = 40;
    private int POINT_RADIUS = 30;
    private int LABEL_SIZE = 40;

    private Paint mPaint = new Paint();
    private Paint mProgressPaint = new Paint();
    private Paint mLabelPaint = new Paint();

    private Paint mCirclePaint;

    private Paint mLinePaint;

    private Paint mTextPaint;

    private Paint mNewPaint;

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
        mCirclePaint.setColor(0x7F19191C);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(10);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        mLinePaint = new Paint();
        mLinePaint.setColor(0x7F19191C);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        mTextPaint = new Paint();
        mTextPaint.setColor(0xFFF7c528);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(42);
        mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mNewPaint = new Paint();
        mNewPaint.setColor(0xFFF7c528);
        mNewPaint.setAntiAlias(true);
        mNewPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(LABEL_SIZE);
        mPaint.setStrokeWidth(10);

        mProgressPaint.setColor(Color.parseColor("#fff100"));
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP
        ));

        mLabelPaint.setColor(Color.parseColor("#1f1f1f"));
        mLabelPaint.setStyle(Paint.Style.FILL);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(LABEL_SIZE);


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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0x7F19191C);


        canvas.drawLine(
                DEFAULT_PADDING,
                DEFAULT_PADDING + POINT_RADIUS,
                getWidth() - DEFAULT_PADDING,
                DEFAULT_PADDING + POINT_RADIUS,
                mNewPaint);
        setDescribeText();
//        canvas.drawOval(new RectF(0, 0, 50, 50), mPaint);

        int count = canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), 0xFF);
        canvas.drawLine(
                DEFAULT_PADDING,
                DEFAULT_PADDING + POINT_RADIUS,
                getWidth() - DEFAULT_PADDING,
                DEFAULT_PADDING + POINT_RADIUS,
                mLinePaint);

        for (int i = 0; i < maxProgress; i++) {

            int startCircle = radius + i * (lineLength + 2 * radius);
            int startLine = (i + 1) * (2 * radius) + i * lineLength;
            canvas.drawCircle(startCircle, DEFAULT_PADDING + POINT_RADIUS, POINT_RADIUS, mCirclePaint);
//            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
//            baseLineY = mYPosition + distance;
//            canvas.drawText(stringList.get(i), startCircle, baseLineY, mTextPaint);
        }

        canvas.drawRect(
                0,
                0,
                getWidth() * 40 / 100f,
                getHeight(),
                mTextPaint);
        canvas.restoreToCount(count);


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

    public void setPositionList() {

    }
}
