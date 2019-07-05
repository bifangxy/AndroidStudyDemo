package com.xy.androidstudydemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.xy.common.utils.LogUtils;


/**
 * Created by xieying on 2019/5/10.
 * Description：
 */
public class PonitDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    public static final int POINT_LIST = 2;

    public static final int BLANK_LIST = 3;

    private Drawable mDivider;

    private int mOrientation;

    /**
     * 分割线缩进值
     */
    private int inset;

    /**
     * 分割线的宽度
     */
    private int distance;

    private Paint paint;

    private final Rect mBounds = new Rect();

    /**
     * @param context
     * @param orientation layout的方向
     * @param drawable    引入的drawable的ID
     * @param inset       分割线缩进值
     */
    public PonitDecoration(Context context, int orientation, int drawable, int inset) {
        mDivider = context.getResources().getDrawable(drawable);
        this.inset = inset;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        setOrientation(orientation);
    }

    public PonitDecoration(Context context, int orientation, int drawable, int inset, int distance) {
        mDivider = context.getResources().getDrawable(drawable);
        this.inset = inset;
        this.distance = distance;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST && orientation != POINT_LIST && orientation != BLANK_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else if (mOrientation == HORIZONTAL_LIST) {
            drawHorizontal(c, parent);
        } else if (mOrientation == POINT_LIST) {
            drawPoint(c, parent);
        } else if (mOrientation == BLANK_LIST) {
            drawBlank(c, parent);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        //最后一个item不画分割线
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            if (inset > 0) {
                c.drawRect(left, top, right, bottom, paint);
                mDivider.setBounds(left + inset, top, right - inset, bottom);
            } else {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    //针对轨迹拍摄点间隔线做适应
    private void drawPoint(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop() + (parent.getHeight() - mDivider.getIntrinsicHeight()) / 2;
        final int bottom = parent.getHeight() / 2 + mDivider.getIntrinsicHeight() / 2;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawBlank(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();

        LogUtils.d("childCount = " + childCount);

        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int right = left + distance;
            LogUtils.d("left = " + left + "    right = " + right);
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }

    }

    //由于Divider也有宽高，每一个Item需要向下或者向右偏移
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (this.mDivider == null) {
            outRect.set(0, 0, 0, 0);
        } else {
            if (this.mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
            } else {
                //此处用来判断是否是最后一个item，因为最后一个item不需要花分割线 此处也就不用偏移了
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
                    outRect.set(0, 0, 0, 0);
                else
                    outRect.set(0, 0, distance, 0);
            }

        }
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
