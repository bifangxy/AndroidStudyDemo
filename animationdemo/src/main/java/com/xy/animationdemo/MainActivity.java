package com.xy.animationdemo;

import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.xy.common.base.BaseActivity;
import com.xy.common.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.btn_translate_animation)
    Button mBtnTranslateAnimation;

    @BindView(R.id.btn_scale_animation)
    Button mBtnScaleAnimation;

    @BindView(R.id.btn_alpha_animation)
    Button mBtnAlphaAnimation;

    @BindView(R.id.btn_rotation_animation)
    Button mBtnRotationAnimation;

    @BindView(R.id.btn_group_animation)
    Button mBtnGroupAnimation;

    @BindView(R.id.btn_parabola_animation)
    Button mBtnParabolaAnimation;

    @BindView(R.id.view_search)
    View mViewSearch;

    @BindView(R.id.tv_search)
    TextView mTvSearch;

    private ValueAnimator mValueAnimator;

    private ObjectAnimator mObjectAnimator;

    private AnimatorSet mAnimatorSet;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @OnClick(R.id.btn_alpha_animation)
    public void alphaAnimation() {
       /* mValueAnimator = ValueAnimator.ofFloat(1, 0, 1);
        mValueAnimator.setDuration(3000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBtnAlphaAnimation.setAlpha(value);
                mBtnAlphaAnimation.requestLayout();

            }
        });
        mValueAnimator.start();*/

       /* mObjectAnimator = ObjectAnimator.ofFloat(mBtnAlphaAnimation, "alpha", 1, 0, 1);
        mObjectAnimator.setDuration(3000);
        mObjectAnimator.start();*/

        mBtnAlphaAnimation.animate().alpha(0f).setDuration(3000);
//        mBtnAlphaAnimation.animate().alpha(1f).setDuration(3000);

    }

    @OnClick(R.id.btn_translate_animation)
    public void translateAnimation() {
        //利用ValueAnimator实现平移
//        mValueAnimator = ValueAnimator.ofFloat(mBtnTranslateAnimation.getTranslationY(), 400, mBtnTranslateAnimation.getTranslationY());
//        mValueAnimator.setDuration(3000);
//        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (float) animation.getAnimatedValue();
//                mBtnTranslateAnimation.setTranslationY(value);
//                mBtnTranslateAnimation.requestLayout();
//
//            }
//        });
//        mValueAnimator.start();

        //利用ObjectAnimator实现平移
//        mObjectAnimator = ObjectAnimator.ofFloat(mBtnTranslateAnimation, "translationY",
//                mBtnTranslateAnimation.getTranslationY(), 400, mBtnTranslateAnimation.getTranslationY());
//        mObjectAnimator.setDuration(3000);
//        mObjectAnimator.start();

        //利用PropertyValuesHolder的Keyframe(关键帧)，实现多个阶段动画
        //一半的时间移动到400，在用一半的时间回弹到300
        Keyframe keyframe1 = Keyframe.ofFloat(0, 0);
        Keyframe keyframe2 = Keyframe.ofFloat(0.5f, 400f);
        Keyframe keyframe3 = Keyframe.ofFloat(1, 300f);

        PropertyValuesHolder holder = PropertyValuesHolder.ofKeyframe("translationY", keyframe1, keyframe2, keyframe3);
        mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(mBtnTranslateAnimation, holder);
        mObjectAnimator.setDuration(3000);
        mObjectAnimator.start();

    }

    @OnClick(R.id.btn_rotation_animation)
    public void rotationAnimation() {
        /*mValueAnimator = ValueAnimator.ofFloat(0f, 360f);
        mValueAnimator.setDuration(3000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBtnRotationAnimation.setRotation(value);
                mBtnRotationAnimation.requestLayout();

            }
        });
        mValueAnimator.start();*/
        mObjectAnimator = ObjectAnimator.ofFloat(mBtnRotationAnimation, "rotation", 0f, 360f);
        mObjectAnimator.setDuration(3000);
        mObjectAnimator.start();
    }

    @OnClick(R.id.btn_scale_animation)
    public void scaleAnimation() {
        /*mValueAnimator = ValueAnimator.ofFloat(1, 2, 1);
        mValueAnimator.setDuration(3000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBtnScaleAnimation.setScaleX(value);
                mBtnScaleAnimation.setScaleY(value);
                mBtnScaleAnimation.requestLayout();

            }
        });
        mValueAnimator.start();*/
        LogUtils.d("y = " + mBtnGroupAnimation.getTranslationY());
        mObjectAnimator = ObjectAnimator.ofFloat(mBtnScaleAnimation, "scaleX", 1, 2, 1);
        mObjectAnimator.setDuration(3000);
        mObjectAnimator.start();
    }

    @OnClick(R.id.btn_group_animation)
    public void groupAnimation() {
        mAnimatorSet = new AnimatorSet();

        LogUtils.d("y = " + mBtnGroupAnimation.getTranslationY());
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mBtnGroupAnimation, "alpha", 1, 0, 1);
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(mBtnGroupAnimation, "rotation", 0f, 360f);
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mBtnGroupAnimation, "translationY",
                mBtnGroupAnimation.getTranslationY(), 400, mBtnGroupAnimation.getTranslationY());
        mAnimatorSet.play(alphaAnimator).with(rotationAnimator).with(translationAnimator);
        mAnimatorSet.setDuration(3000);
        mAnimatorSet.start();
        LogUtils.d("y = " + mBtnGroupAnimation.getTranslationY());
    }

    @OnClick(R.id.btn_parabola_animation)
    public void parabolaAnimation() {
        //使用自定义估值器
//        mValueAnimator = ValueAnimator.ofObject(new MyTypeEvaluator(),
//                new Point(mBtnParabolaAnimation.getX(), mBtnParabolaAnimation.getY()),
//                new Point(mBtnParabolaAnimation.getX()+400, mBtnParabolaAnimation.getY()+300));
//        mValueAnimator.setDuration(3000);
//        mValueAnimator.setInterpolator(new LinearInterpolator());
//        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                Point point = (Point) animation.getAnimatedValue();
//                mBtnParabolaAnimation.setX(point.getX());
//                mBtnParabolaAnimation.setY(point.getY());
//                mBtnParabolaAnimation.requestLayout();
//            }
//        });
    }

    @OnClick(R.id.view_search)
    public void searchClick() {
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("name", "MainActivity");

        Pair<View, String> pair = new Pair<>(mViewSearch, "view_search");
        Pair<View, String> pairText = new Pair<>(mTvSearch, "tv_search");

        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, pair, pairText);
        ActivityCompat.startActivity(this, intent, compat.toBundle());
    }

}
