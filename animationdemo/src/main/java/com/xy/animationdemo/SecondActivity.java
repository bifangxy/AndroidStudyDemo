package com.xy.animationdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.EditText;
import android.widget.TextView;

import com.xy.common.base.BaseActivity;
import com.xy.common.utils.InputMethodUtil;
import com.xy.common.utils.LogUtils;
import com.xy.common.widget.view.MyRulerView;
import com.xy.common.widget.view.RulerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xieying on 2019/6/4.
 * Description：
 */
public class SecondActivity extends BaseActivity {

    @BindView(R.id.et_search)
    EditText mEtSearch;

    @BindView(R.id.tv_parameter_value)
    TextView mTvParameterValue;

    @BindView(R.id.my_ruler_view)
    MyRulerView mMyRulerView;

    @BindView(R.id.ruler_view)
    RulerView mRulerView;

    private int value = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_second;
    }

    @Override
    protected void initData() {
        mEtSearch.setFocusable(true);
        mEtSearch.setFocusableInTouchMode(true);
        mEtSearch.requestFocus();
    }

    @Override
    protected void initEvent() {
        mMyRulerView.scrollToPosition(48);
//        mRulerView.scrollTo(48);
        mMyRulerView.setOnRulerPositionChangeListener(position -> {
            mTvParameterValue.setText(String.valueOf(position));
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                mMyRulerView.scrollToPosition(48);
            }
        },100);
    }


    @OnClick(R.id.tv_cancel)
    public void cancel() {
        InputMethodUtil.hiddenInput(this, mEtSearch);
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("------onCreate------");

    }

    @Override
    protected void onStart() {
        super.onStart();
//        mMyRulerView.scrollToPosition(48);
        LogUtils.d("------onStart------");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("------onResume------");

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d("------onPause------");

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d("------onStop------");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("------onDestroy------");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("value", value);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        value = savedInstanceState.getInt("value");
    }
}
