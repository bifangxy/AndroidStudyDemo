
package com.xy.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

/**
 * Dip和Pixel之间转化
 *
 * PS: 加 0.5 是为了让结果四舍五入
 */
public class DipPixelUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @deprecated 推荐使用不需要传入context的{@link #dip2px(float)}
     */
    @SuppressLint("DefaultLocale")
    @Deprecated
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * @deprecated 推荐使用不需要context的{@link #px2dip(float)}
     */
    @Deprecated
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * px转dpi
     * @param pxValue px的值
     * @return 返回转换后的dip的值
     */
    public static int px2dip(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param spValue
     * @param fontScale
     *            (DisplayMetrics类中的scaledDensity属性)
     * @return
     */
    public static int sp2pix(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param context
     * @param spValue
     * @return
     * @deprecated 推荐使用不传入context的{@link #sp2px(float)}
     */
    @Deprecated
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale);
    }

    /**
     * sp转px
     * @param spValue sp的值
     * @return 返回转换为像素后的值
     */
    public static int sp2px(float spValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale);
    }

    /**
     * px转sp
     * @param context
     * @param pxValue
     * @return
     * @deprecated 推荐使用不传入context的{@link #px2sp(float)}
     */
    @Deprecated
    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);

    }

    /**
     * px转sp
     * @param pxValue px的值
     * @return 返回转换后的px的值
     */
    public static int px2sp(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);

    }
}
