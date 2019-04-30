package com.xy.common.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xy.common.R;
import com.xy.common.utils.DipPixelUtil;
import com.xy.common.utils.MathUtils;


public class RockerView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private static int DEFAULT_REFRESH_CYCLE = 30;
    private static int DEFAULT_CALLBACK_CYCLE = 40;

    private SurfaceHolder mHolder;
    private Thread mDrawThread;
    private Thread mCallbackThread;
    private boolean mDrawOk = true;
    private boolean mCallbackOk = true;

    private int mViewWidth;
    private int mViewheight;

    private Paint mPaint;

    private Point mArrowPosition = new Point();
    /**
     * The rocker active area center position.
     * usually, it is the center of this view.
     */
    private Point mAreaPosition = new Point();

    /**
     * The Rocker position.
     * usually, it as same asmAreaPosition .
     * if this view touched, it will follow the touch position.
     * <p/>
     * we get position information from this.
     */
    private Point mRockerPosition = new Point();

    /**
     * 是否锁定摇杆的位置，当锁定后，摇杆固定在中心不动。
     * 当不锁定时，当点击摇杆范围之外时，摇杆会移动到所点击的位置附近，再进行摇杆操作
     */
    private boolean mLockPosition;

    private int mAreaRadius = -1;
    private int mRockerRadius = -1;

    private Rect mInnerPointRect;//上下顶边时，形成的最大内矩形。当圆心在此矩形的边上时，摇杆刚好接触到屏幕边缘

    private Bitmap mAreaBitmap;
    private Bitmap mRockerBitmap;
    private Bitmap mArrowBitmap;

    private Bitmap mLeftBitmap;
    private Bitmap mLeftPressBitmap;

    private Bitmap mTopBitmap;
    private Bitmap mTopPressBitmap;

    private Bitmap mRightBitmap;
    private Bitmap mRightPressBitmap;

    private Bitmap mBottomBitmap;
    private Bitmap mBottomPressBitmap;


    private RockerListener mListener;
    public static final int EVENT_ACTION = 1;
    public static final int EVENT_CLOCK = 2;

    private int mRefreshCycle = DEFAULT_REFRESH_CYCLE;
    private int mCallbackCycle = DEFAULT_CALLBACK_CYCLE;
    private boolean mTouchDown;

    /**
     * 设置摇杆复位点
     */
    private Point mResetPosition = new Point();
    /**
     * 复位点的起始点（其实就是以控件区域的 [0-左上] [1-右上] [2-左下] [3-右下] 四个点作为起始点，如果不设置，默认就是左上）
     */
    private int mResetStart;


    /*Life Cycle***********************************************************************************/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RockerView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RockerView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.customRockerViewAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RockerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.customRockerViewStyle);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RockerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // init attrs
        initAttrs(context, attrs, defStyleAttr, defStyleRes);

        // set paint
        setPaint();

        setBitmap();

        if (isInEditMode()) {
            return;
        }

        // config surfaceView
        configSurfaceView();

        // config surfaceHolder
        configSurfaceHolder();
    }

    private void setBitmap() {
        mLeftBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.controller_left_white);
        mLeftPressBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.controller_left_gray);

        mRightBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.controller_right_white);
        mRightPressBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.controller_right_gray);

        mTopBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.controller_on_white);
        mTopPressBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.controller_on_gray);

        mBottomBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.controller_under_white);
        mBottomPressBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.controller_under_gray);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RockerView, defStyleAttr, defStyleRes);

        mLockPosition = ta.getBoolean(R.styleable.RockerView_lock_position, true);
        mRefreshCycle = ta.getInteger(R.styleable.RockerView_refresh_cycle, DEFAULT_REFRESH_CYCLE);
        Drawable arrowDrawable = ta.getDrawable(R.styleable.RockerView_arrow_img);
        Drawable areaDrawable = ta.getDrawable(R.styleable.RockerView_area_img);
        Drawable rockerDrawable = ta.getDrawable(R.styleable.RockerView_rocker_img);
        int resetX = (int) ta.getDimension(R.styleable.RockerView_reset_point_x, -1);
        int resetY = (int) ta.getDimension(R.styleable.RockerView_reset_point_y, -1);
        mResetStart = ta.getInt(R.styleable.RockerView_reset_point_start, 0);
        ta.recycle();

        mResetPosition = new Point(resetX, resetY);
        if (arrowDrawable != null) {
            mArrowBitmap = ((BitmapDrawable) arrowDrawable).getBitmap();
        }
        if (areaDrawable != null) {
            mAreaBitmap = ((BitmapDrawable) areaDrawable).getBitmap();
        }
        if (rockerDrawable != null) {
            mRockerBitmap = ((BitmapDrawable) rockerDrawable).getBitmap();
        }
        mAreaRadius = mAreaBitmap.getWidth() / 2;
        mRockerRadius = mRockerBitmap.getWidth() / 2;
    }

    private void setPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void configSurfaceView() {
        setKeepScreenOn(true);          // do not lock screen when surfaceView is running.
        setFocusable(true);             // make sure this surfaceView can get focus from keyboard.
        setFocusableInTouchMode(true);  // make sure this surfaceView can get focus from touch.
        setZOrderOnTop(true);           // make sure this surface is placed on top of the window
    }

    private void configSurfaceHolder() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setFormat(PixelFormat.TRANSPARENT); //设置背景透明
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = 0, measureHeight = 0;
        int defaultWidth = (mAreaRadius) * 2;
        int defaultHeight = defaultWidth;

        int widthsize = MeasureSpec.getSize(widthMeasureSpec);      //取出宽度的确切数值
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);      //取出宽度的测量模式

        int heightsize = MeasureSpec.getSize(heightMeasureSpec);    //取出高度的确切数值
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);    //取出高度的测量模式

        if (widthmode == MeasureSpec.AT_MOST || widthmode == MeasureSpec.UNSPECIFIED || widthsize < 0) {
            measureWidth = defaultWidth;
        } else {
            measureWidth = widthsize;
        }

        if (heightmode == MeasureSpec.AT_MOST || heightmode == MeasureSpec.UNSPECIFIED || heightsize < 0) {
            measureHeight = defaultHeight;
        } else {
            measureHeight = heightsize;
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mViewWidth = w;
        mViewheight = h;

        mInnerPointRect = new Rect(mAreaRadius, mAreaRadius, mViewWidth - mAreaRadius, mViewheight - mAreaRadius);

        resetPosition();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        createDrawThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        destroyDrawThread();
    }

    private void createDrawThread() {
        try {
            mDrawOk = true;
            mDrawThread = new Thread(this);
            mDrawThread.start();

            mCallbackOk = true;
            mCallbackThread = new Thread(new Runnable() {
                long beforeTime;

                @Override
                public void run() {
                    while (mCallbackOk) {

                        long nowTime = SystemClock.elapsedRealtime();
                        if (nowTime - beforeTime >= mCallbackCycle) {
                            beforeTime = nowTime;
                            if (isShown()) {
                                // listener callback
                                listenerCallback();
                            }
                        }
                    }
                }
            });
            mCallbackThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void destroyDrawThread() {
        try {
            mDrawOk = false;
            if (mDrawThread != null) {
                mDrawThread.interrupt();
                mDrawThread = null;
            }
            mCallbackOk = false;
            if (mCallbackThread != null) {
                mCallbackThread.interrupt();
                mCallbackThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    String TAG = "event-rockerview";
    /*Event Response*******************************************************************************/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        ZYLog.i(TAG,event.getAction()+" / "+ event.getPointerCount());

        if (!isEnabled() || !isShown()) {
            return false;
        }

        try {

            int len = MathUtils.getDistance(mAreaPosition.x, mAreaPosition.y, event.getX(), event.getY());

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mTouchDown = true;
                if (len > mAreaRadius) {
                    if (mLockPosition) {
                        mTouchDown = false;
                        return true;
                    }
                    //如果屏幕接触点不在摇杆挥动范围内,则移动到触摸处附近
                    if (mInnerPointRect.contains(((int) event.getX()), (int) event.getY())) {
                        mArrowPosition.set((int) event.getX(), (int) event.getY());
                        mAreaPosition.set((int) event.getX(), (int) event.getY());
                        mRockerPosition.set((int) event.getX(), (int) event.getY());
                    } else {
                        int x = event.getX() < mAreaRadius ?
                                mAreaRadius : (int) (event.getX() > mViewWidth - mAreaRadius ?
                                mViewWidth - mAreaRadius : event.getX());
                        int y = event.getY() < mAreaRadius ?
                                mAreaRadius : (int) (event.getY() > mViewheight - mAreaRadius ?
                                mViewheight - mAreaRadius : event.getY());
                        mArrowPosition.set(x, y);
                        mAreaPosition.set(x, y);
                        mRockerPosition.set(x, y);
                    }

                }
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (!mTouchDown) {
                    return true;
                }
                //保证Rocker不会超越边缘
                if (len <= mAreaRadius - mRockerRadius - DipPixelUtil.dip2px(40)) {
                    //如果手指在摇杆活动范围内，则摇杆处于手指触摸位置
                    mRockerPosition.set((int) event.getX(), (int) event.getY());
                } else {
                    //设置摇杆位置，使其处于手指触摸方向的 摇杆活动范围边缘
                    mRockerPosition = MathUtils.getPointByCutLength(mAreaPosition,
                            new Point((int) event.getX(), (int) event.getY()), mAreaRadius - mRockerRadius - DipPixelUtil.dip2px(40));
                }
                if (mListener != null) {
                    float radian = MathUtils.getRadian(mAreaPosition, new Point((int) event.getX(), (int) event.getY()));
                    int angle = RockerView.this.getAngleClockwise(radian);
                    float distance = MathUtils.getDistance(mAreaPosition.x, mAreaPosition.y, event.getX(), event.getY());
                    int level = getDistanceLevel((int) distance);
                    mListener.callback(EVENT_ACTION, angle, distance, level);
                    mListener.callback((int) (event.getX() - mAreaPosition.x), (int) event.getY() - mAreaPosition.y);
                }
            }
            //如果手指离开屏幕，则摇杆返回初始位置
            if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) {   //多点触控会造成cancel事件
                mTouchDown = false;
                if (mLockPosition) {
                    mRockerPosition = new Point(mAreaPosition);
                } else {
                    resetPosition();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /*Thread - draw view***************************************************************************/


    @Override
    public void run() {
        if (isInEditMode()) {
            return;
        }

        long begin = SystemClock.elapsedRealtime();

        Canvas canvas = null;
        while (mDrawOk) {
            long now = SystemClock.elapsedRealtime();
            if (now - begin < mRefreshCycle) {
                continue;
            }
            begin = now;
            if (!isShown()) {
                clearCanvas();
                continue;
            }
            if (mHolder == null) {
                return;
            }
            Surface surface = mHolder.getSurface();
            if (surface != null && !surface.isValid()) {
                return;
            }
            boolean finishing = false;
            Context context = getContext();
            if (context != null) {
                finishing = ((FragmentActivity) getContext()).isFinishing();
            }
            if (finishing) {
                return;
            }
            try {
                canvas = mHolder.lockCanvas();
                if (canvas == null) {
                    return;
                }
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                drawArea(canvas);
                drawRocker(canvas);
                drawArrow(canvas);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null && mHolder != null && surface != null && surface.isValid()) {
                    try {
                        mHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        clearCanvas();
    }

    /**
     * 清空画布
     */
    private void clearCanvas() {
        Canvas canvas = null;
        if (mHolder == null) {
            return;
        }
        Surface surface = mHolder.getSurface();
        if (surface != null && !surface.isValid()) {
            return;
        }
        boolean finishing = false;
        Context context = getContext();
        if (context != null) {
            finishing = ((FragmentActivity) getContext()).isFinishing();
        }
        if (finishing) {
            return;
        }
        try {
            canvas = mHolder.lockCanvas();
            if (canvas == null) {
                return;
            }
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null && mHolder != null && surface != null && surface.isValid()) {
                try {
                    mHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawArea(Canvas canvas) {
        if (null != mAreaBitmap) {
            Rect dst = new Rect(
                    mAreaPosition.x - mAreaRadius,
                    mAreaPosition.y - mAreaRadius,
                    mAreaPosition.x + mAreaRadius,
                    mAreaPosition.y + mAreaRadius);
            canvas.drawBitmap(mAreaBitmap, null, dst, mPaint);
            canvas.drawBitmap(mLeftBitmap, mAreaPosition.y - mAreaRadius + DipPixelUtil.dip2px(10), mAreaPosition.y - mLeftBitmap.getHeight() / 2, mPaint);
            canvas.drawBitmap(mRightBitmap, mAreaPosition.y + mAreaRadius - mRightBitmap.getWidth() - DipPixelUtil.dip2px(10), mAreaPosition.y - mRightBitmap.getHeight() / 2, mPaint);
            canvas.drawBitmap(mTopBitmap, mAreaPosition.x - (mTopBitmap.getWidth() >> 1), mAreaPosition.x - mAreaRadius + DipPixelUtil.dip2px(10), mPaint);
            canvas.drawBitmap(mBottomBitmap, mAreaPosition.x - (mBottomBitmap.getWidth() >> 1), mAreaPosition.x + mAreaRadius - mBottomBitmap.getHeight() - DipPixelUtil.dip2px(10), mPaint);


        }
    }

    private void drawRocker(Canvas canvas) {
        if (null != mRockerBitmap) {
            Rect dst = new Rect(
                    mRockerPosition.x - mRockerRadius,
                    mRockerPosition.y - mRockerRadius,
                    mRockerPosition.x + mRockerRadius,
                    mRockerPosition.y + mRockerRadius);
            canvas.drawBitmap(mRockerBitmap, null, dst, mPaint);
        }
    }

    private void drawArrow(Canvas canvas) {
        float radian1 = MathUtils.getRadian(mAreaPosition, new Point(mRockerPosition.x, mRockerPosition.y));
        int angle1 = RockerView.this.getAngleClockwise(radian1);

        if (angle1 > 45 && angle1 < 135) {
            canvas.drawBitmap(mBottomPressBitmap, mAreaPosition.x - (mBottomBitmap.getWidth() >> 1), mAreaPosition.x + mAreaRadius - mBottomBitmap.getHeight() - DipPixelUtil.dip2px(10), mPaint);
        } else if (angle1 >= 135 && angle1 <= 225) {
            canvas.drawBitmap(mLeftPressBitmap, mAreaPosition.y - mAreaRadius + DipPixelUtil.dip2px(10), mAreaPosition.y - mLeftBitmap.getHeight() / 2, mPaint);
        } else if (angle1 > 225 && angle1 < 315) {
            canvas.drawBitmap(mTopPressBitmap, mAreaPosition.x - (mTopBitmap.getWidth() >> 1), mAreaPosition.x - mAreaRadius + DipPixelUtil.dip2px(10), mPaint);
        } else if (angle1 > 315 || (angle1 < 45 && angle1 > 0)) {
            canvas.drawBitmap(mRightPressBitmap, mAreaPosition.y + mAreaRadius - mRightBitmap.getWidth() - DipPixelUtil.dip2px(10), mAreaPosition.y - mRightBitmap.getHeight() / 2, mPaint);
        }

        if (mArrowBitmap != null && mTouchDown) {
            Matrix matrix = new Matrix();
            matrix.postTranslate(-mArrowBitmap.getWidth() / 2, -mArrowBitmap.getHeight() / 2);
            float radian = MathUtils.getRadian(mAreaPosition, new Point(mRockerPosition.x, mRockerPosition.y));
            int angle = RockerView.this.getAngleClockwise(radian);
            matrix.postRotate(angle);
            matrix.postTranslate(mArrowPosition.x, mArrowPosition.y);
            canvas.drawBitmap(mArrowBitmap, matrix, mPaint);

        }
    }

    private void listenerCallback() {
        if (mListener != null) {
            if (mRockerPosition.x == mAreaPosition.x && mRockerPosition.y == mAreaPosition.y) {
//                mListener.callback(EVENT_CLOCK, -1, 0,1);
            } else {
                float radian = MathUtils.getRadian(mAreaPosition, new Point(mRockerPosition.x, mRockerPosition.y));
                int angle = RockerView.this.getAngleClockwise(radian);
                float distance = MathUtils.getDistance(mAreaPosition.x, mAreaPosition.y, mRockerPosition.x, mRockerPosition.y);
                int level = getDistanceLevel((int) distance);
                mListener.callback(EVENT_CLOCK, angle, distance, level);
            }
        }
    }

    //获取摇杆偏移角度 0-360°
    private int getAngleConvert(float radian) {
        int tmp = (int) Math.round(radian / Math.PI * 180);
        if (tmp < 0) {
            return -tmp;
        } else {
            return 180 + (180 - tmp);
        }
    }

    private int getDistanceLevel(int distance) {
        if (distance < mAreaRadius) {
            return 1;
        } else if (distance > 2 * mAreaRadius) {
            return 3;
        } else {
            return 2;
        }
    }

    //顺时针方向角度一直增加，0-360°
    private int getAngleClockwise(float radian) {
        int tmp = (int) Math.round(radian / Math.PI * 180);
        if (tmp < 0) {
            return 360 + tmp;
        } else {
            return tmp;
        }
    }

    private void resetPosition() {

//        if (mViewheight > mViewWidth) {
//            mArrowPosition.set(mViewWidth / 2, 2 * mViewheight / 3);
//            mAreaPosition.set(mViewWidth / 2, 2 * mViewheight / 3);
//            mRockerPosition.set(mViewWidth / 2, 2 * mViewheight / 3);
//        } else if (mViewheight == mViewWidth) {
//            mArrowPosition.set(mViewWidth / 2, mViewheight / 2);
//            mAreaPosition.set(mViewWidth / 2, mViewheight / 2);
//            mRockerPosition.set(mViewWidth / 2, mViewheight / 2);
//        } else {
//            mArrowPosition.set(2 * mViewWidth / 3, mViewheight / 2);
//            mAreaPosition.set(2 * mViewWidth / 3, mViewheight / 2);
//            mRockerPosition.set(2 * mViewWidth / 3, mViewheight / 2);
//        }
        //根据复位起始点来 确定复位点 [0-左上] [1-右上] [2-左下] [3-右下]
        int x = mResetPosition.x;
        int y = mResetPosition.y;
        switch (mResetStart) {
            case 3: //右下
                if (x > 0) {
                    x = mViewWidth - x;
                }
                if (y > 0) {
                    y = mViewheight - y;
                }
                break;
            case 2: //左下
                if (y > 0) {
                    y = mViewheight - y;
                }
                break;
            case 1: //右上
                if (x > 0) {
                    x = mViewWidth - x;
                }
                break;
            case 0: //左上
            default:
                break;

        }

        if (mInnerPointRect != null) {
            if (mInnerPointRect.contains(x, y)) {
                //如果这个点不会使控件超出边缘
            } else {
                if (x >= mInnerPointRect.left && x <= mInnerPointRect.right) {
                    //如果只有x不会使控件超出边缘
                    y = mViewheight / 2;
                } else if (y >= mInnerPointRect.top && y <= mInnerPointRect.bottom) {
                    //如果只有y不会使控件超出边缘
                    x = mViewWidth / 2;
                } else {
                    //默认的复位点
                    x = mViewWidth / 2;
                    y = mViewheight / 2;
                    if (mViewheight > mViewWidth) {
                        y = mViewheight * 2 / 3;
                    } else if (mViewheight < mViewWidth) {
                        x = mViewWidth * 2 / 3;
                    }
                }
            }
        }
        mArrowPosition.set(x, y);
        mAreaPosition.set(x, y);
        mRockerPosition.set(x, y);
    }

    // for preview
    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            canvas.drawColor(Color.WHITE);
            drawArea(canvas);
            drawRocker(canvas);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (isShown() && !enabled) {
            mTouchDown = false;
            if (mLockPosition) {
                mRockerPosition = new Point(mAreaPosition);
            } else {
                resetPosition();
            }
        }
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        mPaint.setAlpha((int) (255 * alpha));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHolder.removeCallback(this);
        mHolder = null;
    }

    public void setListener(@NonNull RockerListener listener) {
        mListener = listener;
    }

    /**
     * rocker listener
     */
    public interface RockerListener {

        /**
         * you can get some event from this method
         *
         * @param eventType       The event type, EVENT_ACTION or EVENT_CLOCK
         * @param currentAngle    The current angle 右边为0°，顺时针旋转
         * @param currentDistance The current distance (px)
         * @param distanceLevel   距离等级，在底部rocker区域内为1级，距离mArea中心1个半径为2级，2个半径为3级。最大3级
         */
        void callback(int eventType, int currentAngle, float currentDistance, int distanceLevel);

        /**
         * 当前触摸点的坐标移动偏移量
         *
         * @param xOffset x坐标
         * @param yOffset y坐标
         */
        void callback(int xOffset, int yOffset);
    }

}