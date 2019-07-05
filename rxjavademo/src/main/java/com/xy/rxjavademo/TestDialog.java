package com.xy.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.xy.common.base.SpcBaseDialogFragment;

/**
 * Created by xieying on 2019/6/27.
 * Description：
 */
public class TestDialog extends SpcBaseDialogFragment {
    @Override
    public int getLayoutResource() {
        return R.layout.test_dialog;
    }

    @Override
    public void init() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setClearNotFocusable(false);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setWindowLayout(Window window) {
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        super.setWindowLayout(window);
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        FrameLayout layout = new FrameLayout(getContext());
//        //外面包一层,为了正确的宽高
//        inflater.inflate(getLayoutResource(), layout);
//        return layout;
//    }
}
