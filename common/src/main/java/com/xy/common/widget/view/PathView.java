package com.xy.common.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.SumPathEffect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xieying on 2019/6/5.
 * Description：
 */
public class PathView extends View {
    private Paint mPaint;


    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(1);
        mPaint.setTextSize(50);
        mPaint.setColor(Color.parseColor("#1b8fe6"));
//        mPaint.setShadowLayer(10, 0, 0, Color.RED);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setTypeface(Typeface.SANS_SERIF);
        mPaint.setFakeBoldText(true);

        PathEffect pathEffect1 = new CornerPathEffect(40);
        PathEffect pathEffect2 = new DiscretePathEffect(20, 5);

        PathEffect pathEffect = new SumPathEffect(pathEffect1, pathEffect2);

//        mPaint.setPathEffect(pathEffect);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#DDDDDD"));
        Path path = new Path();
        path.moveTo(100, 100);
        path.lineTo(200, 100);
        path.lineTo(100, 200);
        path.lineTo(200, 200);
        path.lineTo(100, 300);
        path.lineTo(300, 300);

        float baseline = 2*(mPaint.getFontMetrics().bottom)-mPaint.getFontMetrics().top;
        canvas.drawText("看看是不是真的有效果", 10, baseline, mPaint);

//        canvas.drawTextOnPath("看看是不是真的有效果", path, 0, 0, mPaint);

//        canvas.drawPath(path, mPaint);


//        canvas.drawRect(100, 100, 200, 200, mPaint);
    }
}
