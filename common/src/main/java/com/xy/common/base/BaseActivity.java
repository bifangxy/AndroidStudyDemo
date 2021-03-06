package com.xy.common.base;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xy.common.R;
import com.xy.common.utils.ActivityUtils;
import com.xy.common.utils.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xieying on 2019/4/16.
 * Description：
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mUnbinder;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        onCreateTask(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mUnbinder = ButterKnife.bind(this);
        initData();
        initEvent();
        ActivityUtils.addActivity(this);
    }

    protected void onCreateTask(@Nullable Bundle saveInstanceState) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        ActivityUtils.removeActivity(this);
    }


    protected abstract int getLayoutId();

    protected abstract void initData();

    protected abstract void initEvent();


}
