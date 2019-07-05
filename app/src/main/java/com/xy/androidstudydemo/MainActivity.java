package com.xy.androidstudydemo;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.xy.common.base.BaseActivity;
import com.xy.common.utils.DipPixelUtil;
import com.xy.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.scaleview)
    ScaleView mScaleView;

    private MySimpleAdapter mMySimpleAdapter;

    private List<MySimpleAdapter.MyData> mMyData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

//


    }

    private void initAdapter() {



    }

    @Override
    protected void initEvent() {
    }

    @OnClick(R.id.tv_left)
    public void leftClick() {

    }

}
