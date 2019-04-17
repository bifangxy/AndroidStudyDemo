package com.xy.camerademo;

import android.content.Intent;
import android.hardware.camera2.CameraDevice;
import android.widget.TextView;

import com.xy.camerademo.camera.CameraActivity;
import com.xy.common.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

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

    @OnClick(R.id.main_tv_jump)
    public void jump(){
        startActivity(new Intent(this, CameraActivity.class));
    }


}
