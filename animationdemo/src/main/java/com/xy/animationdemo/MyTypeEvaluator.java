package com.xy.animationdemo;

import android.animation.TypeEvaluator;

/**
 * Created by xieying on 2019/6/4.
 * Description：
 */
public class MyTypeEvaluator implements TypeEvaluator<Point> {
    @Override
    public Point evaluate(float fraction, Point startValue, Point endValue) {
        Point point = new Point();
        point.setX(startValue.getX() + fraction * (endValue.getX() - startValue.getX()));
        point.setY(startValue.getY() + fraction * (endValue.getY() - startValue.getX()));
        return point;
    }
}
