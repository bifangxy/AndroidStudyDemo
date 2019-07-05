package com.xy.common.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


/**
 * Copyright (c) 2017 桂林智神信息技术有限公司. All rights reserved.
 *
 * @author cnjer
 * @date 2017/8/31
 * @description 画廊效果的viewpager
 */

public class GalleryViewPager extends RelativeLayout implements ViewPager.OnPageChangeListener {

    public static final String TAG = "GalleryViewPager";

    private ViewPager mViewPager;
    private RelativeLayout mIndicator;
    private Point mSelectPoint;

    private int mCount;//循环的图片数量

    private int mPointColorNormal = Color.argb(76,255,255,255);//小圆点默认颜色
    private int mPointColorSelected = Color.WHITE;//小圆点选中颜色
    private int mPointMargin = 30;//小圆点之间的默认间距
    private int mPointRadius = 9;//小圆点指示器的默认半径
    private int mIndicatorBottomMargin = 30;//小圆点指示器与底部的间距

    private long mCycleTime = 4000L;//默认4秒循环一次

    private float mScale = 1f;//放大缩小
    private int mMargin = 0;

    private boolean mCycle = false;//是否开启自动循环播放,默认不开启

    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    /**
     * 设置小圆点默认颜色
     * @param pointColorNormal
     */
    public void setPointColorNormal(int pointColorNormal) {
        mPointColorNormal = pointColorNormal;
    }

    /**
     * 设置小圆点选中颜色
     * @param pointColorSelected
     */
    public void setPointColorSelected(int pointColorSelected) {
        mPointColorSelected = pointColorSelected;
    }

    /**
     * 设置指示器圆点的间距
     *
     * @param margin
     */
    public void setPointMargin(int margin) {
        this.mPointMargin = margin;
    }

    /**
     * 设置指示器圆点半径
     *
     * @param radius
     */
    public void setPointRadius(int radius) {
        this.mPointRadius = radius;
    }

    /**
     * 设置指示器与底部的间距
     * @param indicatorBottomMargin
     */
    public void setIndicatorBottomMargin(int indicatorBottomMargin) {
        this.mIndicatorBottomMargin = indicatorBottomMargin;
    }

    /**
     * 设置轮播间隔时间
     *
     * @param cycleTime
     */
    public void setCycleTime(long cycleTime) {
        this.mCycleTime = cycleTime;
    }

    /**
     * 设置缩放比例
     *
     * @param scale
     */
    public void setScale(float scale) {
        this.mScale = scale;
    }

    public void setMargin(int margin) {
        mMargin = margin;
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) mViewPager.getLayoutParams();
        layoutParams.setMargins(mMargin, 0, mMargin, 0);//画廊效果
        mViewPager.setLayoutParams(layoutParams);
    }

    /**
     * 设置是否自动循环
     * @param cycle
     */
    public void setCycle(boolean cycle) {
        mCycle = cycle;
    }

    /**
     * 添加滑动监听
     * @param onPageChangeListener
     */
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }


    public void setAdapter(PagerAdapter pagerAdapter) {
        setAdapter(pagerAdapter, pagerAdapter.getCount());
    }

    public void setOffscreenPageLimit(int limit) {
        if (mViewPager != null) {
            mViewPager.setOffscreenPageLimit(limit);
        }
    }

    /**
     * 最后调用设置adapter和实际的图片数量
     *
     * @param pagerAdapter
     * @param count
     */
    public void setAdapter(PagerAdapter pagerAdapter, int count) {
        mCount = count;
        initUI();//根据参数初始化
        mViewPager.setAdapter(pagerAdapter);
        if (pagerAdapter.getCount() == Integer.MAX_VALUE) {
            mViewPager.setCurrentItem(mCount * 1000);//展示第一页的图片
        } else {
            mViewPager.setCurrentItem(0);
        }
    }

    /**
     * 循环展示的任务
     */
    private Runnable cyclerunnable = new Runnable() {
        @Override
        public void run() {
            if (mCycle) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                postDelayed(this, mCycleTime);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        postDelayed(cyclerunnable, mCycleTime);//开启自动循环
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(cyclerunnable);//取消自动循环
        super.onDetachedFromWindow();
    }

    public GalleryViewPager(Context context) {
        this(context, null);
    }

    public GalleryViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initUI() {
        this.setClipChildren(false);//画廊效果

        //初始化viewpager的相关参数
        mViewPager = new ViewPager(getContext());
        RelativeLayout.LayoutParams pagerParam
                = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        pagerParam.setMargins(mMargin, 0, mMargin, 0);//画廊效果
        mViewPager.setClipChildren(false);//画廊效果
        mViewPager.setOffscreenPageLimit(3);//默认至少加载3页
        mViewPager.setPageTransformer(true, new ScaleTransformer(mScale));//缩小放大的效果
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setPageTransformer(true, new ScaleTransformer(mScale));//缩小放大的效果
        addView(mViewPager, pagerParam);

        //包裹指示器的布局
        mIndicator = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams indicatorParam
                = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        indicatorParam.addRule(CENTER_HORIZONTAL);
        indicatorParam.addRule(ALIGN_PARENT_BOTTOM);
        indicatorParam.setMargins(0, 0, 0, mIndicatorBottomMargin);
        addView(mIndicator, indicatorParam);

        createPoints();
    }

    /**
     * 创建指示器小圆点
     */
    private void createPoints() {
        mIndicator.removeAllViews();
        //未被选中的小圆点指示器
        for (int i = 0; i < mCount; i++) {
            Point point = new Point(getContext());
            point.setColor(mPointColorNormal);
            RelativeLayout.LayoutParams layoutParams
                    = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            if (mPointRadius > 0) {
                point.setRadius(mPointRadius);
            }
            //除了最后一个指示器圆点，右边的间距
            layoutParams.leftMargin = i * (mPointMargin + mPointRadius * 2);
            mIndicator.addView(point, layoutParams);
        }

        //选中的小圆点指示器布局
        mSelectPoint = new Point(getContext());
        mSelectPoint.setColor(mPointColorSelected);
        RelativeLayout.LayoutParams layoutParams
                = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mPointRadius > 0) {
            mSelectPoint.setRadius(mPointRadius);
        }
        layoutParams.leftMargin = mPointMargin;
        mIndicator.addView(mSelectPoint);
    }

    /**
     * viewpager的滑动监听start
     **/
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //在viewpager进行页面移动时动态改变选择器选中状态view的margin
        int pos = position % mCount;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSelectPoint
                .getLayoutParams();
        final int margin = (int) ((pos) * (mPointMargin + mPointRadius * 2)
                + positionOffset * (mPointMargin + mPointRadius * 2));
        if (pos == mCount - 1) {//
            params.leftMargin = pos * (mPointMargin + mPointRadius * 2);
        } else {
            params.leftMargin = margin;
        }
        mSelectPoint.setLayoutParams(params);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }
    /**viewpager的滑动监听end**/

    /**
     * 画廊的放大缩小变化效果
     */
    public static class ScaleTransformer implements ViewPager.PageTransformer {

        private float mScale;

        public ScaleTransformer(float scale) {
            mScale = scale;
        }

        @Override
        public void transformPage(View page, float position) {

            if (position < -1 || position > 1) {
                page.setScaleX(mScale);
                page.setScaleY(mScale);
            } else {
                float trans = 1 - mScale;
                if (position < 0) { //position区间[-1,0],将要展示的页面
                    float scaleX = 1 + trans * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                } else {//position区间[0,1],将要消失的页面
                    float scaleX = 1 - trans * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                }
            }
        }
    }

    private PointF lastPoint = new PointF();

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mCount <= 0) {
            getParent().requestDisallowInterceptTouchEvent(false);
            return super.dispatchTouchEvent(event);
        }
        boolean superState = super.dispatchTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPoint.set(event.getX(), event.getY());
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            case MotionEvent.ACTION_MOVE:
                float absXDiff = Math.abs(event.getX() - lastPoint.x);
                float absYDiff = Math.abs(event.getY() - lastPoint.y);
                getParent().requestDisallowInterceptTouchEvent(false);
                if (absXDiff > absYDiff) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else if (absYDiff > absXDiff) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                removeCallbacks(cyclerunnable);
                return superState;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                removeCallbacks(cyclerunnable);
                postDelayed(cyclerunnable, mCycleTime);
                return superState;
            default:
                return superState;
        }
    }

    /**
     * 自定义指示器小圆点
     */
    public static class Point extends View {

        private Paint mPaint;
        private int r;

        public Point(Context context) {
            this(context, null);
        }

        public Point(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Point(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);//默认颜色
        }

        public void setColor(int color) {
            mPaint.setColor(color);
        }

        public void setRadius(int radius) {
            r = radius;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // 获取宽-测量规则的模式和大小
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);

            // 获取高-测量规则的模式和大小
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            // 设置wrap_content的默认宽 / 高值：为画圆的直径
            // 默认宽/高的设定并无固定依据,根据需要灵活设置
            int mWidth = r * 2;
            int mHeight = r * 2;

            if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(mWidth, mHeight);
            } else if (widthMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(mWidth, heightSize);
            } else if (heightMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(widthSize, mHeight);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawCircle(r, r, r, mPaint);
        }
    }
}
