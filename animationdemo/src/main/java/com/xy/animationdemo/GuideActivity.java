package com.xy.animationdemo;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.xy.common.base.BaseActivity;
import com.xy.common.widget.view.GalleryViewPager;

import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by xieying on 2019/6/14.
 * Description：
 */
public class GuideActivity extends BaseActivity {
    @BindView(R.id.guide_viewpager)
    GalleryViewPager mGuideViewPager;

    @BindArray(R.array.guide_map)
    String[] mGuideMaps;

    private List<ImageView> mImageViewList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initData() {

        for (String guide : mGuideMaps) {
            ViewGroup.LayoutParams params =
                    new GalleryViewPager.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.mipmap.ic_guide_cn_a);
            mImageViewList.add(imageView);
        }

    }

    @Override
    protected void initEvent() {

    }
}
