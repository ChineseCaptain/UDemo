package com.uu.udemo.stackImage;

import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/10/20.
 */

public class ImageLayoutManager extends RecyclerView.LayoutManager {

    /**起始ItemX、Y坐标*/
    private int mStartX = 0;
    private int mStartY = 0;

    /**Item宽和高*/
    private int mDecoratedChildWidth = 0;
    private int mDecoratedChildHeight = 0;

    // 加载时的数值
    private int minCount = 2;//每次低于多少个开始加载更多数据
    private float mBaseScale = 0.98f;
    LayoutCallBack mCallBack;

    public interface LayoutCallBack {
        void loadMoreData();
    }

    public ImageLayoutManager(LayoutCallBack callBack) {
        this.mCallBack = callBack;

    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        //得到子view的宽和高，这边的item的宽高都是一样的，所以只需要进行一次测量
        View scrap = recycler.getViewForPosition(0);
        addView(scrap);
        measureChildWithMargins(scrap, 0, 0);

        //计算item的宽高
        mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
        mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);

        //item居中显示，下面开始计算起始点的坐标
        mStartX = (getHorizontalSpace() - mDecoratedChildWidth)/2;
        mStartY = (getVerticalSpace() - mDecoratedChildHeight)/2;

        // 计算完成后，回收掉这个View
        removeAndRecycleView(scrap, recycler);

        //开始放置item
        layoutItems(recycler, state);
    }

    private void layoutItems(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) return;

        for (int i = 0; i < getItemCount(); i++) {
            View scrap = recycler.getViewForPosition(i);
            measureChildWithMargins(scrap, 0, 0);
            addView(scrap);

            layoutDecorated(scrap, mStartX, mStartY,
                    mStartX + mDecoratedChildWidth,
                    mStartY + mDecoratedChildHeight);

            // 进行缩放
            float scale = (float) Math.pow(mBaseScale, i);
            ViewCompat.setScaleX(scrap, scale);
            ViewCompat.setScaleY(scrap, scale);
        }

    }

    /**
     * 获取整个布局的水平空间大小
     */
    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    /**
     * 获取整个布局的垂直空间大小
     */
    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }
}
