package com.xy.common.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by xieying on 2019/4/18.
 * Description：水印图片工具类
 */
public class WatermarkUtils {

    /**
     *
     * @param srcBitmap 源图片
     * @param watermarkBitmap 水印图片
     * @param paddingLeft 水印图片与左边的间距
     * @param paddingTop 水印图片与上面的间距
     * @return 目标Bitmap
     */
    public static Bitmap createWatermarkBitmap(Bitmap srcBitmap, Bitmap watermarkBitmap,
                                               int paddingLeft, int paddingTop) {
        if (null == srcBitmap)
            return null;
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();

        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(srcBitmap,0,0,null);
        canvas.drawBitmap(watermarkBitmap,paddingLeft,paddingTop,null);
        canvas.save();
        canvas.restore();
        return newBitmap;
    }
}
