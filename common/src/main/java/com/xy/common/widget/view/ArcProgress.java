package com.xy.common.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xy.common.R;
import com.xy.common.utils.DipPixelUtil;


/**
 * Created by xieying on 2019/4/29.
 * Description：
 */
public class ArcProgress extends View {
    private Paint mPaint;

    private Paint outSidePaint;

    private Paint innerPaint;

    private float mStartAngle = 210;

    private float mSweepAngle = 120;

    private int mPaddingLeft = 0;

    private int mPaddingTop = 50;

    private Point mCirclePoint;

    private int mWidth;

    private int mHeight;

    private int mRadius;

    private RectF mArcRectF;

    private Path mArcPath;

    private float[] coords = new float[]{0f, 0f};

    private float mMeasureAngle;

    private Bitmap mBitmapBall;

    private int mBallHeight;

    private int mBallWidth;

    private int mOutsideStrokeWidth;

    private int mInsideStrokeWidth;

    private int mMiddleStrokeWidth;

    private Matrix matrix = new Matrix();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcProgress(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.customArcProgressStyle);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArcProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initAttrs(context, attrs, defStyleAttr, defStyleRes);

        setPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = 0, measureHeight = 0;
        int defaultWidth = 750;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED || widthSize < 0) {
            measureWidth = defaultWidth;
        } else {
            measureWidth = widthSize;
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED || heightSize < 0) {
            measureHeight = (measureWidth >> 2) + (mPaddingTop << 1);
        } else {
            measureHeight = heightSize;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, defStyleRes);

        Drawable thumbDrawable = ta.getDrawable(R.styleable.ArcProgress_arc_thumb_img);
        mStartAngle = ta.getInteger(R.styleable.ArcProgress_arc_start_angle, 210);
        mSweepAngle = ta.getInteger(R.styleable.ArcProgress_arc_sweep_angle, 120);

        mOutsideStrokeWidth = DipPixelUtil.dip2px(ta.getDimension(
                R.styleable.ArcProgress_arc_outside_stroke_width, 6));
        mMiddleStrokeWidth = DipPixelUtil.dip2px(ta.getDimension(
                R.styleable.ArcProgress_arc_middle_stroke_width, 4));
        mInsideStrokeWidth = DipPixelUtil.dip2px(ta.getDimension(
                R.styleable.ArcProgress_arc_inside_stroke_width, 1));
        ta.recycle();
        if (thumbDrawable != null) {
            mBitmapBall = ((BitmapDrawable) thumbDrawable).getBitmap();
        } else {
            mBitmapBall = BitmapFactory.decodeResource(getResources(), R.mipmap.h_focal_length_ball);
        }

        mPaddingTop = mBitmapBall.getHeight() / 2 > (mOutsideStrokeWidth / 2) ? mBitmapBall.getHeight() / 2 : mOutsideStrokeWidth / 2;

    }

    private void setPaint() {
        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);
        innerPaint.setStrokeWidth(mMiddleStrokeWidth);
        innerPaint.setStyle(Paint.Style.STROKE);
        innerPaint.setStrokeJoin(Paint.Join.ROUND);
        innerPaint.setStrokeCap(Paint.Cap.ROUND);

        outSidePaint = new Paint();
        outSidePaint.setAntiAlias(true);
        outSidePaint.setStrokeWidth(mOutsideStrokeWidth);
        outSidePaint.setStyle(Paint.Style.STROKE);
        outSidePaint.setStrokeJoin(Paint.Join.ROUND);
        outSidePaint.setStrokeCap(Paint.Cap.ROUND);


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeWidth(mInsideStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initMeasure();
    }


    private void initMeasure() {
        mWidth = getMeasuredWidth();

        mHeight = getMeasuredHeight();

        Log.d("xieying", "width = " + mWidth + "  height = " + mHeight);

        mRadius = (mWidth >> 1) - mPaddingLeft;

        mCirclePoint = new Point(mWidth >> 1, mPaddingTop + mRadius);

        mArcRectF = new RectF(mPaddingLeft, mPaddingTop, mPaddingLeft + 2 * mRadius, mPaddingTop + 2 * mRadius);

        mArcPath = new Path();

        mArcPath.addArc(mArcRectF, mStartAngle, mSweepAngle);

        mMeasureAngle = mStartAngle;

        mBallHeight = mBitmapBall.getHeight() / 2;
        mBallWidth = mBitmapBall.getWidth() / 2;

        int colors[] = new int[3];
        colors[0] = 0xFFFFE86E;
        colors[1] = 0xFFFF5D0B;
        colors[2] = 0xFFFFE86E;

        int colors1[] = new int[3];
        colors1[0] = 0xFF3A3A3F;
        colors1[1] = 0xFF4A4A52;
        colors1[2] = 0xFF3A3A3F;

        int colors2[] = new int[3];
        colors2[0] = 0xFF19191A;
        colors2[1] = 0xFF1F1F21;
        colors2[2] = 0xFF19191A;


        LinearGradient linearGradient = new LinearGradient(0, 0, mWidth, 0, colors,
                null, LinearGradient.TileMode.CLAMP);

        LinearGradient linearGradient1 = new LinearGradient(0, 0, mWidth, 0, colors1,
                null, LinearGradient.TileMode.CLAMP);

        LinearGradient linearGradient2 = new LinearGradient(0, 0, mWidth, 0, colors2,
                null, LinearGradient.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        innerPaint.setShader(linearGradient1);
        outSidePaint.setShader(linearGradient2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawArc(canvas);

        drawThumb(canvas);

    }

    private void drawArc(Canvas canvas) {

        canvas.drawPath(mArcPath, outSidePaint);
        canvas.drawPath(mArcPath, innerPaint);
        canvas.drawPath(mArcPath, mPaint);
    }

    private void drawThumb(Canvas canvas) {
        matrix.reset();
        //将圆点平移到圆的0度点
        matrix.postTranslate(mCirclePoint.x + mRadius - mBallWidth, mCirclePoint.y - mBallHeight);
        //将圆点旋转到点击的角度
        matrix.postRotate(mMeasureAngle, mCirclePoint.x, mCirclePoint.y);
        canvas.drawBitmap(mBitmapBall, matrix, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (judgeCanDrag(event)) {
                    Log.d("xieying", "can drag");
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (judgeCanDrag(event)) {
                    float angle = getAngle(event.getX(), event.getY());
                    if (angle > mStartAngle + mSweepAngle || angle < 90) {
                        mMeasureAngle = mStartAngle + mSweepAngle;
                    } else if (angle > 90 && angle < mStartAngle) {
                        mMeasureAngle = mStartAngle;
                    } else {
                        mMeasureAngle = angle;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        invalidate();

        return true;
    }

    // 判断是否允许拖动
    private boolean judgeCanDrag(MotionEvent event) {
        boolean mCanDrag = false;
        float[] pos = {event.getX(), event.getY()};

        if (getDistance(pos[0], pos[1]) <= mRadius + 100 && getDistance(pos[0], pos[1]) >= mRadius - 100) {
            mCanDrag = true;
        }
        return mCanDrag;
    }

    // 计算指定位置与上次位置的距离
    private float getDistance(float px, float py) {
        return (float) Math.sqrt((px - mCirclePoint.x) * (px - mCirclePoint.x) + (py - mCirclePoint.y) * (py - mCirclePoint.y));
    }

    private float getAngle(float px, float py) {
        float angle = (float) ((Math.atan2(py - mCirclePoint.y, px - mCirclePoint.x)) * 180 / Math.PI);
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * 获取当前的角度
     *
     * @return
     */
    public float getProgress() {
        return mMeasureAngle;
    }
}
