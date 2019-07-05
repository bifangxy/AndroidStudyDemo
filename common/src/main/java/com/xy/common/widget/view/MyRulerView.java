package com.xy.common.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.xy.common.utils.LogUtils;

/**
 * Created by xieying on 2019/6/12.
 * Description：
 */
public class MyRulerView extends View {
    private int startValue = 0;

    private int endValue = 100;

    private Paint mPaint;

    private int indicatePadding = 30;

    private boolean mIsDragged;

    private OverScroller mOverScroller;

    private VelocityTracker mVelocityTracker;

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private int mLastMotionX;

    private int mInnerWidth;

    private OnRulerPositionChangeListener mOnRulerPositionChangeListener;

    public MyRulerView(Context context) {
        this(context, null);
    }

    public MyRulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mOverScroller = new OverScroller(getContext());
        setOverScrollMode(OVER_SCROLL_ALWAYS);

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

        mInnerWidth = (endValue - startValue) * (indicatePadding);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = canvas.save();
        for (int i = startValue, position = 0; i <= endValue; i++, position++) {
            drawLine(canvas, position);
        }
        canvas.restoreToCount(count);
    }

    private void drawLine(Canvas canvas, int position) {
        int startX = getWidth() / 2 + position * (indicatePadding);
        int startY = getPaddingTop() + 20;
        int endY = getHeight() - getPaddingBottom() - 20;
        int color = Color.parseColor("#7FF5f5f5");
        int width = 4;
        if (position % 10 == 0) {
            startY -= 20;
            endY += 20;
        }
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        canvas.drawLine(startX, startY, startX, endY, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsDragged != mOverScroller.isFinished()) {
                    if (getParent() != null)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (!mOverScroller.isFinished())
                    mOverScroller.abortAnimation();

                mLastMotionX = (int) event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                int curX = (int) event.getX();
                int deltaX = mLastMotionX - curX;

                if (!mIsDragged && Math.abs(deltaX) > mTouchSlop) {
                    if (getParent() != null)
                        getParent().requestDisallowInterceptTouchEvent(true);
                    mIsDragged = true;
                    if (deltaX > 0) {
                        deltaX -= mTouchSlop;
                    } else {
                        deltaX += mTouchSlop;
                    }
                }

                if (getScrollX() <= 0 || getScaleX() >= getMaximumScroll()) {
                    deltaX = (int) (deltaX * 0.7f);
                }
                if (mIsDragged) {
                    mLastMotionX = curX;
                    if (overScrollBy(deltaX, 0, getScrollX(), getScrollY(), getMaximumScroll(),
                            0, getWidth(), 0, true)) {
                        mVelocityTracker.clear();
                    }
                }
                break;
            case MotionEvent.ACTION_UP: {
                if (mIsDragged) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) mVelocityTracker.getXVelocity();

                    if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                        fling(-initialVelocity);
                    } else {
                        //alignCenter();
                        springBack();
                    }
                }

                mIsDragged = false;
                recycleVelocityTracker();
                break;
            }
            case MotionEvent.ACTION_CANCEL: {

                if (mIsDragged && mOverScroller.isFinished()) {
                    springBack();
                }

                mIsDragged = false;

                recycleVelocityTracker();
                break;
            }
        }

        return true;
    }


    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private int getMinimumScroll() {
        return 0;
    }

    private int getMaximumScroll() {
        return (endValue - startValue) * (indicatePadding) + getMinimumScroll();
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (!mOverScroller.isFinished()) {
            final int oldX = getScrollX();
            final int oldY = getScrollY();
            setScrollX(scrollX);
            onScrollChanged(scrollX, scrollY, oldX, oldY);
        } else {
            super.scrollTo(scrollX, scrollY);
        }

        int position = computeSelectedPosition();

        if (mOnRulerPositionChangeListener != null)
            mOnRulerPositionChangeListener.changePosition(position + startValue);


    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mOverScroller.getCurrX();
            int y = mOverScroller.getCurrY();
            overScrollBy(x - oldX, y - oldY, oldX, oldY, getMaximumScroll(), 0, getWidth(), 0, false);
            invalidateView();
        } else if (!mIsDragged) {
            adjustIndicate();
        }
    }

    private void adjustIndicate() {
        if (!mOverScroller.isFinished())
            mOverScroller.abortAnimation();
        int position = computeSelectedPosition();
        int scrollX = getScrollByPosition(position);
        scrollX -= getScrollX();
        if (scrollX != 0) {
            mOverScroller.startScroll(getScrollX(), getScrollY(), scrollX, 0);
            invalidateView();
        }
    }

    private void fling(int velocityX) {
        mOverScroller.fling(getScrollX(), getScrollY(), velocityX, 0, getMinimumScroll(), getMaximumScroll(), 0, 0, getWidth() / 2, 0);
        invalidateView();
    }

    private void springBack() {
        mOverScroller.springBack(getScrollX(), getScrollY(), getMinimumScroll(), getMaximumScroll(), 0, 0);
        invalidateView();
    }

    private int getScrollByPosition(int position) {
        return position * indicatePadding + getMinimumScroll();
    }

    private int computeSelectedPosition() {
        int centerX = getScrollX() - getMinimumScroll() + 16;
        centerX = Math.max(0, Math.min(mInnerWidth, centerX));
        int position = centerX / 30;
        return position;

    }

    private void invalidateView() {
        if (Build.VERSION.SDK_INT >= 16) {
            postInvalidateOnAnimation();
        } else
            invalidate();
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void setOnRulerPositionChangeListener(OnRulerPositionChangeListener onRulerPositionChangeListener) {
        mOnRulerPositionChangeListener = onRulerPositionChangeListener;
    }

    public interface OnRulerPositionChangeListener {
        void changePosition(int position);
    }

    public void scrollToPosition(int position) {
        if (position < 0 || startValue + position > endValue)
            return;
        if (!mOverScroller.isFinished())
            mOverScroller.abortAnimation();

        int scrollX = getScrollByPosition(position);
        LogUtils.d("getScrollX = " + getScrollX() + "  getPosition = " + computeSelectedPosition());
        mOverScroller.startScroll(getScrollX(), getScrollY(), scrollX - getScrollByPosition(computeSelectedPosition()), 0, 0);
        invalidateView();

    }

    public void scrollToValue(int value) {
        if (value < startValue || value > endValue)
            return;
        scrollToPosition(value - startValue);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
