package com.uu.udemo.stackImage;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/10/20.
 */

public class ImageLayoutManager extends RecyclerView.LayoutManager {

    /**起始ItemX、Y坐标*/
//    private int mStartX = 0;
//    private int mStartY = 0;

    /**Item宽和高*/
    private int mDecoratedChildWidth = 0;
    private int mDecoratedChildHeight = 0;

    /**是否正在布局*/
    private boolean mInLayout = false;
    /**
     * 缓存的三个view：前一个（已经移除屏幕），当前屏幕view，当前屏幕下的一个view
     */
    private SparseArray<View> mCacheViews = new SparseArray<>();
    private int[] mCacheOffsets = new int[]{0, 0, 0};
    private float[] mCacheScales = new float[]{0f, 0f, 0f};

    // 加载时的数值
    private int minCount = 5;//每次低于多少个开始加载更多数据
    private float mBaseScale = 0.08f;
    LayoutCallBack mCallBack;
    private float mThreshold = 0.0f;// 距离两边边距的临界值，小于这个值时，自动退出
    /**滑动总偏移量*/
    private int mScrollOffset = 0;


    public interface LayoutCallBack {
        void loadMoreData();
        void onItemClickListener(int index);
    }

    public ImageLayoutManager(LayoutCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
//        detachAndScrapAttachedViews(recycler);

        // 开始布局
        mInLayout = true;

        //开始测量item的宽和高
        //得到子view的宽和高，这边的item的宽高都是一样的，所以只需要进行一次测量
        View scrap = recycler.getViewForPosition(0);
        addView(scrap);
        measureChildWithMargins(scrap, 0, 0);
        //计算item的宽高
        mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap) + 1;
        mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);
        // 计算完成后，回收掉这个View
        detachAndScrapView(scrap, recycler);

        // 初始化缓存view
        initCacheViews(recycler);

        //开始放置item
        recyclerAndLayoutItems(recycler, state);
        // 结束布局
        mInLayout = false;

        // 数据不足时，加载更多数据
        if (getItemCount() < minCount) {
            mCallBack.loadMoreData();
        }
    }

    /**
     * 初始化缓存view，这一步开始添加item
     * @param recycler
     */
    private void initCacheViews(RecyclerView.Recycler recycler) {
        // 根据item的数量来添加不同的缓存值
        if (mCacheViews.get(1) == null ) {
            View one = recycler.getViewForPosition(0);
            addView(one, 0);
            measureChildWithMargins(one, 0, 0);
            mCacheViews.put(1, one);
        }
        if (getItemCount() > 1) {// 如果多于一条数据
            if (mCacheViews.get(2) == null ) {
                View second = recycler.getViewForPosition(1);
                addView(second, 0);
                measureChildWithMargins(second, 0, 0);
                mCacheViews.put(2, second);
            }
        }
    }

    private void recyclerAndLayoutItems(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) return;
//        Log.i("uu", "previous的右边坐标："+ );
        // 回收复用view
        // 回收的原则：只保存三个：前一个（已经移除屏幕的），顶层view，顶层view下面的那个view
        for (int i = 0; i < getChildCount(); i++) {
            View scrap = getChildAt(i);
            if (scrap != mCacheViews.get(0) && scrap != mCacheViews.get(1) && scrap != mCacheViews.get(2)) {
                detachAndScrapView(scrap, recycler);
            }
        }

        // 进行缩放, 根据偏移量来计算
        float ratio = mScrollOffset / getThreshold();
        // ratio 最大为 1 或 -1
        if (ratio > 1) {
            ratio = 1;
        } else if (ratio < -1) {
            ratio = -1;
        }

        //因为item是占满整个屏幕，所以根据mScrollOffset的正负值来决定是移动TopView还是PreviousView
        if (mScrollOffset == 0) {
            layoutPreviousView(0, 1);
            layoutTopView(0, 1);
            layoutBottomView(0, 1 - mBaseScale);
        } else if (mScrollOffset > 0) {// 向左移动
            if (mCacheViews.get(2) != null) {
                if (Math.abs(mScrollOffset) < getThreshold()) {
                    // 移动TopView
                    layoutTopView(mScrollOffset, 1);

                    float scale = 1 - (1 - Math.abs(ratio)) * mBaseScale;
                    layoutBottomView(0, scale);
                }else {// 直接移除
                    getNext(recycler, state);
                }
            }else {
                mScrollOffset = 0;
                recyclerAndLayoutItems(recycler, state);
            }
        }else {// 向右移动
            if (mCacheViews.get(0) != null) {
                if (Math.abs(mScrollOffset) < getThreshold()) {
                    // 移动PreviousView
                    layoutPreviousView(mScrollOffset, 1);

                    float scale = 1 - Math.abs(ratio) * mBaseScale;
                    layoutTopView(0, scale);
                }else {// 直接移除
                    getPrevious(recycler, state);
                }
            }else {
                mScrollOffset = 0;
                recyclerAndLayoutItems(recycler, state);
            }
        }
    }

    /**
     * 将当前的view移除，然后添加一个新view
     * @param recycler
     * @param state
     */
    private void getNext(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mCacheViews.get(1).animate().translationX(0).setInterpolator(new DecelerateInterpolator());
        mCacheViews.get(1).animate().scaleX(1);
        mCacheViews.get(1).animate().scaleY(1);

        // 移除之前的previous，然后缓存item向前进一位
        mCacheViews.put(0, mCacheViews.get(1));
        mCacheViews.put(1, mCacheViews.get(2));
        int curBottom = getPosition(mCacheViews.get(2));
        // 如果还有item，那就再获取下一个item，放到最底下
        if (curBottom < getItemCount() -1) {
            View bottom = recycler.getViewForPosition(curBottom + 1);
            addView(bottom, 0);
            measureChildWithMargins(bottom, 0, 0);
            mCacheViews.put(2, bottom);
        }else {
            mCacheViews.put(2, null);
        }

        // 全部复位，重新布局
        mScrollOffset = 0;
        recyclerAndLayoutItems(recycler, state);
    }

    /**
     * 获取之前的view
     */
    private void getPrevious(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mCacheViews.get(0).animate().translationX(0);

        // 移除之前的previous，然后缓存item向前后退一位
        // 注意这两个的顺序千万不能变
        mCacheViews.put(2, mCacheViews.get(1));
        mCacheViews.put(1, mCacheViews.get(0));
        int curTop = getPosition(mCacheViews.get(0));
        // 如果还有item，那就再获取下一个item，放到最底下
        if (curTop > 0) {
            View top = recycler.getViewForPosition(curTop - 1);
            addView(top);
            measureChildWithMargins(top, 0, 0);
            mCacheViews.put(0, top);
        }else {
            mCacheViews.put(0, null);
        }

        // 全部复位，重新布局
        mScrollOffset = 0;
        recyclerAndLayoutItems(recycler, state);
    }

    /**
     * 给前一个View布局
     */
    private void layoutPreviousView(int offset, float scale) {
        if (mCacheViews.get(0) != null) {
            layoutDecoratedWithMargins(mCacheViews.get(0),
                    0 - mDecoratedChildWidth - offset,
                    0,
                    0 - offset,
                    mDecoratedChildHeight);

            ViewCompat.setScaleX(mCacheViews.get(0), scale);
            ViewCompat.setScaleY(mCacheViews.get(0), scale);
        }
    }

    /**
     * 给当前屏幕中间的view布局
     */
    private void layoutTopView(int offset, float scale) {
        if (mCacheViews.get(1) != null) {
            layoutDecoratedWithMargins(mCacheViews.get(1),
                    - offset,
                    0,
                    mDecoratedChildWidth - offset,
                    mDecoratedChildHeight);

            ViewCompat.setScaleX(mCacheViews.get(1), scale);
            ViewCompat.setScaleY(mCacheViews.get(1), scale);
        }
    }

    /**
     * 给下一个view布局
     */
    private void layoutBottomView(int offset, float scale) {
        if (mCacheViews.get(2) != null) {
            layoutDecoratedWithMargins(mCacheViews.get(2),
                    - offset,
                    0,
                    mDecoratedChildWidth - offset,
                    mDecoratedChildHeight);

            ViewCompat.setScaleX(mCacheViews.get(2), scale);
            ViewCompat.setScaleY(mCacheViews.get(2), scale);
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

    @Override
    public boolean canScrollHorizontally() {
        // 支持水平滚动
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        mScrollOffset += dx; //累计偏移量

        // 限定最大偏移值
        if (mScrollOffset > mDecoratedChildWidth) {
            mScrollOffset = mDecoratedChildWidth;
        }else if (mScrollOffset < -mDecoratedChildWidth) {
            mScrollOffset = -mDecoratedChildWidth;
        }

        // 重新进行回收和布局
        Log.i("uu", "scrollHorizontallyBy");
        recyclerAndLayoutItems(recycler, state);
        return dx;
    }



    /**设置缩放基数
     * @param baseScale
     */
    public void setBaseScale(float baseScale) {
        this.mBaseScale = baseScale;
    }

    /**设置滑动的阈值
     * @param value
     */
    private void setThreshold(float value) {
        mThreshold = value;
    }

    public float getThreshold() {
        if (mThreshold == 0) {
            mThreshold = getHorizontalSpace()*.85f;
        }
        return mThreshold;
    }
}
