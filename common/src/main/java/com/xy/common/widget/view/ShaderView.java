package com.xy.common.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.EmbossMaskFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.xy.common.R;

/**
 * Created by xieying on 2019/6/5.
 * Description：
 */
public class ShaderView extends View {
    private Paint mPaint;

    private Bitmap mBitmap;

    public ShaderView(Context context) {
        this(context, null);
    }

    public ShaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.avatar);

        Bitmap vBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.v);

        Shader bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

//        Shader vShader = new BitmapShader(vBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

//        Shader shader = new ComposeShader(bitmapShader,vShader, PorterDuff.Mode.DST_OVER);

//        mPaint.setShader(bitmapShader);
//        mPaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.OUTER));

        mPaint.setMaskFilter(new EmbossMaskFilter(new float[]{0,1,0},0.2f,8,10));
        ColorFilter colorFilter = new LightingColorFilter(0xFFFFFF, 0x000000);
        mPaint.setColorFilter(colorFilter);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        canvas.drawBitmap(mBitmap,100,100,mPaint);
//        canvas.drawCircle(width / 2, height / 2, width / 2 > height / 2 ? width / 2 : height / 2, mPaint);

    }
}
