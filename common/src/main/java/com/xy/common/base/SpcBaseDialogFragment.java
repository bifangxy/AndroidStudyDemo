package com.xy.common.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import com.xy.common.R;
import com.xy.common.utils.DipPixelUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by shangzheng on 2018/4/20.
 * 🐳🐳🐳🍒           16:58 🥥
 */
public abstract class SpcBaseDialogFragment extends AppCompatDialogFragment {

    public static final int TYPE_SIMPLE = 0;
    public static final int TYPE_EXCEPT_BACK = 1;
    public static final int TYPE_ONLY_SELF = 2;

    /**
     * 是否需要清除消极焦点的标志（一般情况为true）
     */
    private boolean mClearNotFocusable = true;

    /**
     * @param clearNotFocusable
     */
    public void setClearNotFocusable(boolean clearNotFocusable) {
        mClearNotFocusable = clearNotFocusable;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResource(), container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Do not show navigation bar when this shows, even the showing time is less than 1s.
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        ButterKnife.bind(this, view);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStart() {
        super.onStart();
        setWindow();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void setWindow() {
        Window window = getDialog().getWindow();
        if (window != null) {

            //设置屏蔽屏幕的交互
            window.getAttributes().dimAmount = 0; //外边框透明度
            window.getAttributes().windowAnimations = R.style.DialogAnimationsFade;
            window.getAttributes().x = getXPosition();
            window.getAttributes().y = getYPosition();
            window.getAttributes().gravity = getWindowGravity();
            setWindowLayout(window);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_spc_base_menu));

            if (isClearNotFocusable()) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
            window.setAttributes(window.getAttributes());
        }
    }

    public void show(FragmentManager fragmentManager) {
        try {
            if (fragmentManager == null) {
                return;
            }
            // Show the dialog.
            super.show(fragmentManager, getDialogTag());

            // It is necessary to call executePendingTransactions() on the FragmentManager
            // before hiding the navigation bar, because otherwise getWindow() would raise a
            // NullPointerException since the window was not yet created.
            fragmentManager.executePendingTransactions();

            // Hide the navigation bar. It is important to do this after show() was called.
            // If we would do this in onCreateDialog(), we would get a requestFeature()
            // error.
            if (getDialog() != null) {
                Window window = getDialog().getWindow();
                if (window != null) {
                    if (getActivity() != null) {
                        if (getActivity().getWindow() != null) {
                            window.getDecorView().setSystemUiVisibility(
                                    getActivity().getWindow().getDecorView().getSystemUiVisibility()
                            );
                        }
                    }

                    // Make the dialogs window focusable again.
                    if (isClearNotFocusable()) {
                        window.clearFlags(
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        );
                    }
                }
            }
            if (mShowAndDismissListeners != null) {
                for (ShowAndDismissListener showAndDismissListener : mShowAndDismissListeners) {
                    showAndDismissListener.onShow();
                }
            }
        } catch (IllegalStateException e) {
            //不处理
        }
    }

//    public void setWindowLayout(Window window) {
//        int width = (int) (WindowUtil.getWindowSize().getWidth() * 0.55);
//        int height = (int) (WindowUtil.getWindowSize().getHeight() * 0.65);
//        window.setLayout(width, height);
//    }

    public void setWindowLayout(Window window) {
        int width = DipPixelUtil.dip2px(320);
        int height = DipPixelUtil.dip2px(250);
        window.setLayout(width, height);
    }

    public int getWindowGravity() {
        return Gravity.CENTER;
    }

    public int getXPosition() {
        return 0;
    }

    public int getYPosition() {
        return 0;
    }

    public abstract int getLayoutResource();

    public abstract void init();

    /**
     * 是否只能通过代码dismiss，不能通过外部输入处理。
     * （只用于当isClearNotFocusable（）返回true，activity或者fragment自行处理。）
     */
    public final boolean isDismissOnlySelf() {
        return getTypeDismiss() == TYPE_ONLY_SELF;
    }

    /**
     * 是否只能通过代码dismiss或者其他输入，不能通过返回键处理。
     * （只用于当isClearNotFocusable（）返回true，activity或者fragment自行处理。）
     */
    public final boolean isDismissExceptBack() {
        return getTypeDismiss() == TYPE_EXCEPT_BACK;
    }

    /**
     * 是否只能通过代码dismiss或者外部输入。
     * （只用于当isClearNotFocusable（）返回true，activity或者fragment自行处理。）
     */
    public final boolean isDismissSimple() {
        return getTypeDismiss() == TYPE_SIMPLE;
    }

    /**
     * 是否点击外部区域Dismiss弹框。
     */
    public final boolean isDismissOutside() {
        return getDismissOutside() && isDismissSimple();
    }

    /**
     * 是否点击外部区域Dismiss弹框。
     * （只用于当isClearNotFocusable（）返回true 并且是 普通类型弹框时，
     * activity或者fragment才会自行处理, 否则重写无效.）
     */
    protected boolean getDismissOutside() {
        return !isClearNotFocusable();
    }

    /**
     * 获取关闭类型级别
     *
     * @return
     */
    public int getTypeDismiss() {
        return TYPE_SIMPLE;
    }

    /**
     * 是否需要清除消极焦点的标志 (是否让弹框自己点击外部消失),如果设置为false，则弹框会失去按键监听
     */
    protected boolean isClearNotFocusable() {
        return mClearNotFocusable;
    }


    private String tagDialog;

    public void setTagDialog(String tagDialog) {
        this.tagDialog = tagDialog;
    }

    /**
     * show的tag, 默认就是类名
     *
     * @return
     */
    public String getDialogTag() {
        if (tagDialog == null) {
            return getClass().getSimpleName();
        } else {
            return getClass().getSimpleName() + tagDialog;
        }
    }

    @Override
    public void dismiss() {
        if (!isAdded()) return;
        if (!isStateSaved()) {
            super.dismiss();
        } else {
            super.dismissAllowingStateLoss();
        }
        callDismissListener();
    }

    private void callDismissListener() {
        if (mShowAndDismissListeners != null) {
            for (ShowAndDismissListener showAndDismissListener : mShowAndDismissListeners) {
                showAndDismissListener.onDismiss();
            }
            mShowAndDismissListeners = null;
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
        callDismissListener();
    }

    List<ShowAndDismissListener> mShowAndDismissListeners;

    public void addShowAndDismissListener(ShowAndDismissListener showAndDismissListener) {
        if (mShowAndDismissListeners == null) {
            mShowAndDismissListeners = new ArrayList<>();
        }
        mShowAndDismissListeners.add(showAndDismissListener);
    }

    public interface ShowAndDismissListener {
        void onShow();

        void onDismiss();
    }
}
