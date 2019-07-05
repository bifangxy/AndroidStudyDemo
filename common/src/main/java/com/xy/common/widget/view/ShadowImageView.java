package com.xy.common.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xy.common.utils.LogUtils;

/**
 * Created by xieying on 2019/6/5.
 * Description：
 */
public class ShadowImageView extends View {
    private Paint mPaint;

    private Rect mRect;

    public ShadowImageView(Context context) {
        this(context, null);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(50);
//        mPaint.setShadowLayer(10, 0, 0, Color.RED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtils.d("----onMeasure-----");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtils.d("width = " + getWidth() + "   height = " + getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Bitmap bitmap = drawableToBitmap(getDrawable());
//        canvas.drawBitmap(bitmap,5,5,mPaint);'
        canvas.drawText("Shadow Text",0,100,mPaint);
    }

}
