package com.xy.common.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xieying on 2019/5/29.
 * Description：
 */
public class CanvasView extends View {

    private int DEFAULT_PADDING = 40;
    private int POINT_RADIUS = 30;

    private Paint mPaint;

    private Paint mSrcPaint;

    private Paint mDstPaint;

    private Paint mDstPaint1;


    public CanvasView(Context context) {
        this(context, null);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.FILL);


        mSrcPaint = new Paint();
        mSrcPaint.setColor(0x7F19191C);
        mSrcPaint.setStrokeWidth(20);
        mSrcPaint.setStyle(Paint.Style.FILL);




        mDstPaint = new Paint();
        mDstPaint.setColor(Color.RED);
        mDstPaint.setStyle(Paint.Style.FILL);
        mDstPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        mDstPaint1 = new Paint();
        mDstPaint1.setColor(Color.RED);
        mDstPaint1.setStyle(Paint.Style.FILL);
        mDstPaint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint);
//        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mSrcPaint);
        canvas.drawRect(0, 0, getWidth(), 2 * getHeight() / 3, mSrcPaint);
        canvas.drawRect(50, 1 * getHeight() / 3, getWidth() - 50, getHeight(), mDstPaint);
//        canvas.drawRect(0, 0, getWidth(), 2 * getHeight() / 3, mDstPaint1);

//        canvas.drawRect(50, 1 * getHeight() / 3, getWidth() - 50, getHeight(), mDstPaint1);

        canvas.restore();

    }
}
