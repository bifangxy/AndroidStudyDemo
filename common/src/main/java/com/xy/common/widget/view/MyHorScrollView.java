package com.xy.common.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by xieying on 2019/7/1.
 * Description：
 */
public class MyHorScrollView extends HorizontalScrollView {

    public MyHorScrollView(Context context) {
        super(context);
    }

    public MyHorScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);
    }
}
