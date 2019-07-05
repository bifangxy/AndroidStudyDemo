package com.xy.androidstudydemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xy.common.utils.DipPixelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xieying on 2019/7/2.
 * Description：
 */
public class MyRecyclerView extends RecyclerView {

    private Context mContext;

    private int width;

    private PonitDecoration ponitDecoration;

    private List<MySimpleAdapter.MyData> mMyDataList;

    private MySimpleAdapter mMySimpleAdapter;

    public MyRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {

        setHasFixedSize(true);
        setItemAnimator(new DefaultItemAnimator());//设置默认动画
        LinearLayoutManager mLayoutManage = new LinearLayoutManager(mContext);
        mLayoutManage.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(mLayoutManage);
        ponitDecoration = new PonitDecoration(mContext,
                PonitDecoration.BLANK_LIST, R.color.com_color_red, 100);

//        addItemDecoration(new MyDivderItemDecotation(mContext,DividerItemDecoration.HORIZONTAL));
        mMyDataList = new ArrayList<>();
        Drawable drawable = getResources().getDrawable(R.mipmap.color_crane);
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));
        mMyDataList.add(new MySimpleAdapter.MyData(0, drawable));

        mMySimpleAdapter = new MySimpleAdapter(mMyDataList);

        setAdapter(mMySimpleAdapter);

        mMySimpleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MySimpleAdapter.MyData myData = (MySimpleAdapter.MyData) adapter.getData().get(position);
                myData.getState();
            }
        });
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        width = getMeasureSize(widthSpec, DipPixelUtil.dip2px(230));
        int height = getMeasureSize(heightSpec, DipPixelUtil.dip2px(40));
        setMeasuredDimension(width, height);

        ponitDecoration.setDistance(getDistance());
        int itemDecorationCount = getItemDecorationCount();
        if (itemDecorationCount != 0)
            removeItemDecorationAt(0);
        addItemDecoration(ponitDecoration);

    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
    }

    private int getMeasureSize(int measureSpec, int defaultSize) {
        int size = defaultSize;

        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);

        switch (measureMode) {
            //父容器对当前View没有任何限制，当前View可以取任意值
            case MeasureSpec.UNSPECIFIED:
                size = defaultSize;
                break;
            //当前尺寸就是当前View最大的取值
            case MeasureSpec.AT_MOST:
                size = measureSize;
                break;
            //指定了大小，当前尺寸就是View应该的取值
            case MeasureSpec.EXACTLY:
                size = measureSize;
                break;
        }
        return size;
    }

    private int getDistance() {
        int distance;

        int count = width / DipPixelUtil.dip2px(36);

        int remaining = width % DipPixelUtil.dip2px(36);

        if (remaining == 0)
            count = count - 1;
        if (count > 5)
            count = 5;
        distance = (width - count * DipPixelUtil.dip2px(36)) / (count - 1);

        return distance;
    }

    public void addData(MySimpleAdapter.MyData myData) {
        mMyDataList.add(myData);
        mMySimpleAdapter.notifyDataSetChanged();
    }

}
