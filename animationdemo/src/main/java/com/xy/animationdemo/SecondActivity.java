package com.xy.animationdemo;

import android.support.v4.app.ActivityCompat;
import android.widget.EditText;

import com.xy.common.base.BaseActivity;
import com.xy.common.utils.InputMethodUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xieying on 2019/6/4.
 * Description：
 */
public class SecondActivity extends BaseActivity {

    @BindView(R.id.et_search)
    EditText mEtSearch;

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

    }

    @OnClick(R.id.tv_cancel)
    public void cancel() {
        InputMethodUtil.hiddenInput(this, mEtSearch);
        ActivityCompat.finishAfterTransition(this);
    }
}
