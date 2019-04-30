package com.xy.common.widget.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xy.common.R;
import com.xy.common.utils.MathUtils;


/**
 * Created by ht on 2018/5/15.
 */
public class BendSeekBar extends View {

    /**
     * 滑块图片-圆形
     */
    private Bitmap mThumbBitmap;
    /**
     * 滑条图片-圆形
     */
    private Bitmap mProgressBitmap;
    /**
     * 滑条图片背景-圆形
     */
    private Bitmap mProgressBgBitmap;

    /**
     * 内半径
     */
    int mInnerRadius;

    /**
     * 外半径
     */
    int mOutRadius;

    /** 没有进度时候的角度 可取0和180*/
    int mStrAngle;

    /**
     * 当前旋转角度,相对于初始角度 (最右边为0度，顺时针为正，逆时针为负)
     * 这里认为右半边的旋转角度范围为 -mRotationAngleMax ~ mRotationAngleMax，
     * 左半边的旋转角度范围为 180 - mRotationAngleMax ~ 180 + mRotationAngleMax，
     * 方向都是往上为+，下为减
     */
    int mRotationAngle;

    /**
     * 最大旋转角度偏移值（以水平中间为准，上下对半分）
     */
    int mRotationAngleMax = 54;

    /**
     * 圆心坐标
     */
    Point mPointO;

    /**
     * 手指按下能消费事件的区域
     */
    Rect mTouchRect;

    boolean mIsLeft = false;

    private Paint mPaint;

    private Matrix matrix = new Matrix();

    Path path = new Path();

    /**
     * 是否复位
     */
    private boolean mLockProgress = true;

    private static final long mCallbackCycleTime = 40;
    private Thread mCallbackThread;
    private boolean isCancel = false;


    public BendSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public BendSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BendSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public BendSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // init attrs
        initAttrs(context, attrs);

        // set paint
        setPaint();

        initThread();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BendSeekBar);

        Drawable thumbDrawable = ta.getDrawable(R.styleable.BendSeekBar_thumb_img);
        Drawable progressBgDrawable = ta.getDrawable(R.styleable.BendSeekBar_progress_bg_img);
        Drawable progressDrawable = ta.getDrawable(R.styleable.BendSeekBar_progress_img);
        boolean bLeft = ta.getBoolean(R.styleable.BendSeekBar_isLeft, isLeft());
        int thumbWidth = 30;
//        = DipPixelUtil.dip2px(ta.getDimension(R.styleable.BendSeekBar_thumb_width, 30));
        int thumbHeight = 50;
//        = DipPixelUtil.dip2px(ta.getDimension(R.styleable.BendSeekBar_thumb_height, 50));
        ta.recycle();

        if (thumbDrawable != null) {
            mThumbBitmap = ((BitmapDrawable) thumbDrawable).getBitmap();
            thumbWidth = mThumbBitmap.getWidth() / 4;
            thumbHeight = mThumbBitmap.getHeight() / 2;
        }
        if (progressBgDrawable != null) {
            mProgressBgBitmap = ((BitmapDrawable) progressBgDrawable).getBitmap();
        }
        if (progressDrawable != null) {
            mProgressBitmap = ((BitmapDrawable) progressDrawable).getBitmap();
        }
        mInnerRadius = mThumbBitmap.getWidth() / 2;
        mOutRadius = mProgressBgBitmap.getWidth() / 2;
        mPointO = new Point( mOutRadius, mOutRadius);
        mIsLeft = bLeft;
        if(isLeft()){
            mStrAngle = 180;
            mTouchRect = new Rect( mPointO.x - mOutRadius - 20,
                    mPointO.y - thumbHeight / 2,
                    mPointO.x - mInnerRadius + thumbWidth,
                    mPointO.y + thumbHeight / 2);
        }else {
            mStrAngle = 0;
            mTouchRect = new Rect( mPointO.x + mInnerRadius - thumbWidth,
                    mPointO.y - thumbHeight / 2,
                    mPointO.x + mOutRadius + 20,
                    mPointO.y + thumbHeight / 2);
        }
        mRotationAngle = 0;
    }

    private void setPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point f = new Point((int)event.getX(), (int)event.getY());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mTouchRect.contains(f.x, f.y)){
                    moveThumb(f);
                }else {
                    return false;
                }
                if(mOnCamSeekListener != null){
                    mOnCamSeekListener.start(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                moveThumb(f);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mLockProgress){
                    mRotationAngle = 0;
                }else {
                    moveThumb(f);
                }
                if(mOnCamSeekListener != null){
                    mOnCamSeekListener.end(this);
                }
                break;
        }
        invalidate();
        return true;
    }

    private void moveThumb(Point f) {
        if( (isLeft() && f.x > mPointO.x) ||
                (!isLeft() && f.x < mPointO.x)){
            f.x = mPointO.x + (mPointO.x - f.x);
        } //将另外半边的坐标换算到有效半边
        mRotationAngle = (int) MathUtils.radian2Angle( MathUtils.getRadian(mPointO, f) );
        //得到结果为-180~180
        if(isLeft() && mRotationAngle < 0){
            mRotationAngle = mStrAngle + (180 + mRotationAngle);
        }
        mRotationAngle -= mStrAngle;
        if(mRotationAngle <  - mRotationAngleMax){
            mRotationAngle =  - mRotationAngleMax;
        }else if(mRotationAngle >  mRotationAngleMax){
            mRotationAngle =  mRotationAngleMax;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected synchronized void onDraw(Canvas c) {
        super.onDraw(c);
        drawProgressBg(c);
        drawProgress(c);
        drawThumb(c);
    }

    private void drawProgressBg(Canvas c) {
        if(mProgressBgBitmap == null) return;
        c.drawBitmap(mProgressBgBitmap, mPointO.x - mOutRadius, mPointO.y - mOutRadius, mPaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawProgress(Canvas c) {
        float left = mPointO.x - mOutRadius;
        float top = mPointO.y - mOutRadius;
        float right = mPointO.x + mOutRadius;
        float bottom = mPointO.y + mOutRadius;
        if(mProgressBitmap == null) return;
        if(getRotationAngle() == 0){

        }else {
            c.save();
            path.reset();
            path.moveTo(mPointO.x,mPointO.y);
            path.arcTo(left, top, right, bottom, mStrAngle, getRotationAngle(), false);
            c.clipPath(path);
//            mPaint.setColor(Color.RED);
//            c.drawOval(left, top, right, bottom,mPaint);
            c.drawBitmap(mProgressBitmap, left, top, mPaint);
            c.restore();
        }
    }

    private void drawThumb(Canvas c) {
        if(mThumbBitmap == null) return;
        matrix.reset();
        matrix.postTranslate(mPointO.x - mInnerRadius,mPointO.y - mInnerRadius);
        matrix.postRotate(getRotationAngle(), mPointO.x, mPointO.y);
        c.drawBitmap(mThumbBitmap, matrix, mPaint);
    }


    public boolean isLeft() {
        return mIsLeft;
    }

    public float getRotationAngle() {
        return mRotationAngle;
    }

    public float getProgress() {
        float progress = mRotationAngle / (float) mRotationAngleMax;
        return isLeft()? progress : - progress;
    }


    private void initThread() {
        if(mCallbackThread != null) return;
        mCallbackThread = new Thread(() -> {
            while (!cancelCallbackThread()) {
                if(isCallback()){
                    mOnCamSeekListener.callback(this, getProgress());
                }
                SystemClock.sleep(mCallbackCycleTime);
            }
        });
        mCallbackThread.start();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isCancel = true;
    }

    /**
     * 是否取消线程
     * @return
     */
    protected boolean cancelCallbackThread() {
        return isCancel;
    }

    /**
     * 是否回调监听
     * @return
     */
    protected boolean isCallback() {
        return mOnCamSeekListener != null && getProgress() != 0;
    }

    private OnCamSeekListener mOnCamSeekListener;

    public OnCamSeekListener getOnCamSeekListener() {
        return mOnCamSeekListener;
    }

    public void setOnCamSeekListener(OnCamSeekListener mOnCamSeekListener) {
        this.mOnCamSeekListener = mOnCamSeekListener;
    }

    public interface OnCamSeekListener{
        void callback(BendSeekBar seekBar, float progress);
        void start(BendSeekBar seekBar);
        void end(BendSeekBar seekBar);
    }
}
