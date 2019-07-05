package com.xy.androidstudydemo;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by xieying on 2019/7/2.
 * Description：
 */
public class MySimpleAdapter extends BaseQuickAdapter<MySimpleAdapter.MyData, BaseViewHolder> {


    public MySimpleAdapter(@Nullable List<MyData> data) {
        super(R.layout.item_function, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyData item) {
        ImageView iv_function = helper.getView(R.id.iv_function_img);
        iv_function.setImageDrawable(item.getDrawable());
    }

    public static class MyData {
        int state;

        Drawable mDrawable;

        public MyData(int state, Drawable drawable) {
            this.state = state;
            mDrawable = drawable;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public Drawable getDrawable() {
            return mDrawable;
        }

        public void setDrawable(Drawable drawable) {
            mDrawable = drawable;
        }
    }
}
