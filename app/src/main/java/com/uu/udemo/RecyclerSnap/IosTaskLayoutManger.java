package com.uu.udemo.RecyclerSnap;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * Cover Flow布局类
 * <p>通过重写LayoutManger布局方法{@link #onLayoutChildren(RecyclerView.Recycler, RecyclerView.State)}
 * 对Item进行布局，并对超出屏幕的Item进行回收
 * <p>通过重写LayoutManger中的{@link #scrollHorizontallyBy(int, RecyclerView.Recycler, RecyclerView.State)}
 * 进行水平滚动处理
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @version V1.0
 * @Datetime 2017-04-18
 */

public class IosTaskLayoutManger extends RecyclerView.LayoutManager {

    /**滑动总偏移量*/
    private int mOffsetAll = 0;

    /**Item宽*/
    private int mDecoratedChildWidth = 0;

    /**Item高*/
    private int mDecoratedChildHeight = 0;

    /**Item间隔与item宽的比例*/
    private float mIntervalRatio = 0.5f;

    /**起始ItemX坐标*/
    private int mStartX = 0;

    /**起始Item Y坐标*/
    private int mStartY = 0;

    /**保存所有的Item的上下左右的偏移量信息*/
    private SparseArray<Rect> mAllItemFrames = new SparseArray<>();

    /**记录Item是否出现过屏幕且还没有回收。true表示出现过屏幕上，并且还没被回收*/
    private SparseBooleanArray mHasAttachedItems = new SparseBooleanArray();

    /**RecyclerView的Item回收器*/
    private RecyclerView.Recycler mRecycle;

    /**RecyclerView的状态器*/
    private RecyclerView.State mState;

    /**滚动动画*/
    private ValueAnimator mAnimation;

    /**正显示在中间的Item*/
    private int mSelectPosition = 0;

    /**前一个正显示在中间的Item*/
    private int mLastSelectPosition = 0;

    /**滑动的方向：左*/
    private static int SCROLL_LEFT = 1;

    /**滑动的方向：右*/
    private static int SCROLL_RIGHT = 2;

    /**
     * 选中监听
     */
    private OnSelected mSelectedListener;

    /**是否启动Item灰度值渐变*/
    private boolean mItemGradualGrey = false;

    /**是否启动Item半透渐变*/
    private boolean mItemGradualAlpha = false;

    public IosTaskLayoutManger(boolean isGreyItem,
                               boolean isAlphaItem, float cstInterval) {
        mItemGradualGrey = isGreyItem;
        mItemGradualAlpha = isAlphaItem;
        if (cstInterval >= 0) {
            mIntervalRatio = cstInterval;
        }
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //如果没有item，直接返回
        //跳过preLayout，preLayout主要用于支持动画
        if (getItemCount() <= 0 || state.isPreLayout()) {
            mOffsetAll = 0;
            return;
        }
        mAllItemFrames.clear();
        mHasAttachedItems.clear();

        //得到子view的宽和高，这边的item的宽高都是一样的，所以只需要进行一次测量
        View scrap = recycler.getViewForPosition(0);
        addView(scrap);
        measureChildWithMargins(scrap, 0, 0);
        //计算测量布局的宽高
        mDecoratedChildWidth = getDecoratedMeasuredWidth(scrap);
        mDecoratedChildHeight = getDecoratedMeasuredHeight(scrap);
        //测量第一个item的起始坐标
        mStartX = Math.round(getPaddingLeft() * 1.0f / 2);
        mStartY = Math.round((getVerticalSpace() - mDecoratedChildHeight) *1.0f / 2);

        float offset = mStartX;
        //存储所有item具体位置
        for (int i = 0; i < getItemCount(); i++) {
            Rect frame = mAllItemFrames.get(i);
            if (frame == null) {
                frame = new Rect();
            }
            frame.set(Math.round(offset), mStartY, Math.round(offset + mDecoratedChildWidth), mStartY + mDecoratedChildHeight);
            mAllItemFrames.put(i, frame);
            mHasAttachedItems.put(i, false);
            offset = offset + getIntervalDistance(-1); //原始位置累加，否则越后面误差越大
        }
//        mAllItemFrames = layoutChildAlgorithm.computeStackRects();

        detachAndScrapAttachedViews(recycler); //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        mSelectPosition = getChildCount()-1;//默认选中最后一个
//        if ((mRecycle == null || mState == null) && //在为初始化前调用smoothScrollToPosition 或者 scrollToPosition,只会记录位置
//                mSelectPosition != 0) {                 //所以初始化时需要滚动到对应位置
//            mOffsetAll = calculateOffsetForPosition(mSelectPosition);
//            onSelectedCallBack();
//        }

        layoutItems(recycler, state, SCROLL_RIGHT);

        mRecycle = recycler;
        mState = state;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler,
                                    RecyclerView.State state) {
        if (mAnimation != null && mAnimation.isRunning()) mAnimation.cancel();
        int travel = dx;
        if (dx + mOffsetAll < 0) {
            travel = -mOffsetAll;
        } else if (dx + mOffsetAll > getMaxOffset()){
            travel = (int) (getMaxOffset() - mOffsetAll);
        }
        mOffsetAll += travel; //累计偏移量
        layoutItems(recycler, state, dx > 0 ? SCROLL_RIGHT : SCROLL_LEFT);
        return travel;
    }

    /**
     * 布局Item
     * <p>注意：1，先清除已经超出屏幕的item
     * <p>     2，再绘制可以显示在屏幕里面的item
     */
    private void layoutItems(RecyclerView.Recycler recycler,
                             RecyclerView.State state, int scrollDirection) {
        if (state.isPreLayout()) return;

        Rect displayFrame = new Rect(mOffsetAll, 0, mOffsetAll + getHorizontalSpace(), getVerticalSpace());

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int position = getPosition(child);

            if (!Rect.intersects(displayFrame, mAllItemFrames.get(position))) {//Item没有在显示区域，就说明需要回收
                removeAndRecycleView(child, recycler); //回收滑出屏幕的View
                mHasAttachedItems.put(position, false);
            } else { //Item还在显示区域内，更新滑动后Item的位置
                layoutItem(i, child, mAllItemFrames.get(position)); //更新Item位置
                mHasAttachedItems.put(position, true);
            }
        }

        for (int i = 0; i < getItemCount(); i++) {
            if (Rect.intersects(displayFrame, mAllItemFrames.get(i)) &&
                    !mHasAttachedItems.get(i)) { //重新加载可见范围内的Item
                View scrap = recycler.getViewForPosition(i);
                measureChildWithMargins(scrap, 0, 0);
                if (scrollDirection == SCROLL_LEFT) { //向左滚动，新增的Item需要添加在最前面
                    addView(scrap, 0);
                } else { //向右滚动，新增的item要添加在最后面
                    addView(scrap);
                }
                layoutItem(i, scrap, mAllItemFrames.get(i)); //将这个Item布局出来
                mHasAttachedItems.put(i, true);
            }
        }
    }

    /**
     * 布局Item位置
     * @param child 要布局的Item
     * @param frame 位置信息
     */
    private void layoutItem(int index, View child, Rect frame) {
//        computeIosOffset(index, child, frame);
        layoutDecorated(child,
                frame.left - mOffsetAll,
                frame.top,
                frame.right - mOffsetAll,
                frame.bottom);
        child.setScaleX(computeScale(frame.left - mOffsetAll)); //缩放
        child.setScaleY(computeScale(frame.left - mOffsetAll)); //缩放

        if (mItemGradualAlpha) {
            child.setAlpha(computeAlpha(frame.left - mOffsetAll));
        }

        if (mItemGradualGrey) {
            greyItem(child, frame);
        }
    }

    /**
     * 变化Item的灰度值
     * @param child 需要设置灰度值的Item
     * @param frame 位置信息
     */
    private void greyItem(View child, Rect frame) {
        float value = computeGreyScale(frame.left - mOffsetAll);
        ColorMatrix cm = new ColorMatrix(new float[]{
                value, 0, 0, 0, 120*(1-value),
                0, value, 0, 0, 120*(1-value),
                0, 0, value, 0, 120*(1-value),
                0, 0, 0, 1, 250*(1-value),
        });
//            cm.setSaturation(0.9f);

        // Create a paint object with color matrix
        Paint greyPaint = new Paint();
        greyPaint.setColorFilter(new ColorMatrixColorFilter(cm));

        // Create a hardware layer with the grey paint
        child.setLayerType(View.LAYER_TYPE_HARDWARE, greyPaint);
        if (value >= 1) {
            // Remove the hardware layer
            child.setLayerType(View.LAYER_TYPE_NONE, null);
        }

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state){
            case RecyclerView.SCROLL_STATE_IDLE:
                //滚动停止时
                // TODO: 2017/9/30 停止滚动时的处理
                fixOffsetWhenFinishScroll();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                //拖拽滚动时
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                //动画滚动时
                break;
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (position < 0 || position > getItemCount() - 1) return;
        mOffsetAll = calculateOffsetForPosition(position);
        if (mRecycle == null || mState == null) {//如果RecyclerView还没初始化完，先记录下要滚动的位置
            mSelectPosition = position;
        } else {
            layoutItems(mRecycle, mState, position > mSelectPosition ? SCROLL_RIGHT : SCROLL_LEFT);
            onSelectedCallBack();
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        int finalOffset = calculateOffsetForPosition(position);
        if (mRecycle == null || mState == null) {//如果RecyclerView还没初始化完，先记录下要滚动的位置
            mSelectPosition = position;
        } else {
            startScroll(mOffsetAll, finalOffset);
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
        mRecycle = null;
        mState = null;
        mOffsetAll = 0;
        mSelectPosition = 0;
        mLastSelectPosition = 0;
        mHasAttachedItems.clear();
        mAllItemFrames.clear();
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

    /**
     * 获取最大偏移量
     */
    private float getMaxOffset() {
        return (getItemCount() - 1) * getIntervalDistance(-1);
    }

    /**
     * 计算Item缩放系数
     * @param x Item的偏移量
     * @return 缩放系数
     */
    private float computeScale(int x) {
        float scale = 1 - (mStartX - x) * 1.0f / Math.abs(mStartX + mDecoratedChildWidth / mIntervalRatio);
        if (scale < 0) scale = 0;
//        if (scale > 1) scale = 1;
        return scale;
    }

    /**
     * 计算Item的灰度值
     * @param x Item的偏移量
     * @return 灰度系数
     */
    private float computeGreyScale(int x) {
        float itemMidPos = x + mDecoratedChildWidth / 2; //item中点x坐标
        float itemDx2Mid = Math.abs(itemMidPos - getHorizontalSpace() / 2); //item中点距离控件中点距离
        float value = 1 - itemDx2Mid * 1.0f / (getHorizontalSpace() /2);
        if (value < 0.1) value = 0.1f;
        if (value > 1) value = 1;
        value = (float) Math.pow(value,.8);
        return value;
    }

    /**
     * 计算Item半透值
     * @param x Item的偏移量
     * @return 缩放系数
     */
    private float computeAlpha(int x) {
        float alpha = 1 - Math.abs(x - mStartX) * 1.0f / Math.abs(mStartX + mDecoratedChildWidth / mIntervalRatio);
        if (alpha < 0.3f) alpha = 0.3f;
        if (alpha > 1) alpha = 1.0f;
        return alpha;
    }

    /**
     * 计算Item所在的位置偏移
     * @param position 要计算Item位置
     */
    private int calculateOffsetForPosition(int position) {
        return Math.round(getIntervalDistance(-1) * position);
    }

    /**
     * 修正停止滚动后，Item滚动到中间位置
     */
    private void fixOffsetWhenFinishScroll() {
        int scrollN = (int) (mOffsetAll * 1.0f / getIntervalDistance(-1));
        float moreDx = (mOffsetAll % getIntervalDistance(-1));
        if (moreDx > (getIntervalDistance(-1) * 0.5)) {
            scrollN ++;
        }
        int finalOffset = (int) (scrollN * getIntervalDistance(-1));
        startScroll(mOffsetAll, finalOffset);
        mSelectPosition = Math.round (finalOffset * 1.0f / getIntervalDistance(-1));
    }

    /**
     * 滚动到指定X轴位置
     * @param from X轴方向起始点的偏移量
     * @param to X轴方向终点的偏移量
     */
    private void startScroll(int from, int to) {
        if (mAnimation != null && mAnimation.isRunning()) {
            mAnimation.cancel();
        }
        final int direction = from < to ? SCROLL_RIGHT : SCROLL_LEFT;
        mAnimation = ValueAnimator.ofFloat(from, to);
        mAnimation.setDuration(500);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        mAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetAll = Math.round((float) animation.getAnimatedValue());
                layoutItems(mRecycle, mState, direction);
            }
        });
        mAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onSelectedCallBack();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimation.start();
    }

    /**
     * 获取Item间隔
     * 这里需要根据在屏幕中的位置来确定
     */
    private float getIntervalDistance(int posiInDis) {
        // TODO: 2017/9/30  根据在屏幕中的位置来确定item的间隔
        return mDecoratedChildWidth * mIntervalRatio;
    }

    /**
     * 计算当前选中位置，并回调
     */
    private void onSelectedCallBack() {
        mSelectPosition = Math.round (mOffsetAll / getIntervalDistance(-1));
        if (mSelectedListener != null && mSelectPosition != mLastSelectPosition) {
            mSelectedListener.onItemSelected(mSelectPosition);
        }
        mLastSelectPosition = mSelectPosition;
    }

    /**
     * 获取第一个可见的Item位置
     * <p>Note:该Item为绘制在可见区域的第一个Item，有可能被第二个Item遮挡
     */
    public int getFirstVisiblePosition() {
        int pos = 0;
        for (int i = 0; i < mHasAttachedItems.size(); i++) {
            if (!mHasAttachedItems.get(i)) pos++;
            else break;
        }
        return pos;
    }

    /**
     * 获取最后一个可见的Item位置
     * <p>Note:该Item为绘制在可见区域的最后一个Item，有可能被倒数第二个Item遮挡
     */
    public int getLastVisiblePosition() {
        int pos = mHasAttachedItems.size() - 1;
        for (int i = mHasAttachedItems.size() - 1; i > 0; i--) {
            if (!mHasAttachedItems.get(i)) pos--;
            else break;
        }
        return pos;
    }

    /**
     * 获取可见范围内最大的显示Item个数
     */
    public int getMaxVisibleCount() {
        int oneSide = (int) ((getHorizontalSpace() - mStartX) / (getIntervalDistance(-1)));
        return oneSide * 2 + 1;
    }

    /**
     * 获取中间位置
     * <p>Note:该方法主要用于{@link RecyclerCoverFlow#getChildDrawingOrder(int, int)}判断中间位置
     * <p>如果需要获取被选中的Item位置，调用{@link #getSelectedPos()}
     */
    public int getCenterPosition() {
        int pos = (int) (mOffsetAll / getIntervalDistance(-1));
        int more = (int) (mOffsetAll % getIntervalDistance(-1));
        if (more > getIntervalDistance(-1) * 0.5f) pos++;
        return pos;
    }

    /**
     * 设置选中监听
     * @param l 监听接口
     */
    public void setOnSelectedListener(OnSelected l) {
        mSelectedListener = l;
    }

    /**
     * 获取被选中Item位置
     */
    public int getSelectedPos() {
        return mSelectPosition;
    }

    /**
     * 选中监听接口
     */
    public interface OnSelected {
        /**
         * 监听选中回调
         * @param position 显示在中间的Item的位置
         */
        void onItemSelected(int position);
    }

    private void computeIosOffset(int index, View itemView, Rect frame) {
        // TODO: 2017/9/30 仿iOS的后台任务切换算法
        int viewWidth = getHorizontalSpace();
        int virtualItemWidth = mDecoratedChildWidth/2;
        int MARGIN = viewWidth / 3 - virtualItemWidth / 2;
        float cX = mOffsetAll + viewWidth/2;
        float attributesX = virtualItemWidth * index + virtualItemWidth/2 + MARGIN;

        float delta = cX - attributesX;
        float ratio = delta/virtualItemWidth;
        float centerX = attributesX;
        float offset = (float)Math.pow((-ratio +2), 3) / 23 * itemView.getWidth();
        centerX = cX - viewWidth/2 + offset + itemView.getWidth() / 2 - 5;

        int startX = (int)centerX - virtualItemWidth;
        int endX = (int)centerX + virtualItemWidth;
        if (index == 0) {
            Log.i("uu", "mOffsetAll："+mOffsetAll);
            Log.i("uu", "centerX："+centerX);
            Log.i("uu", "startX："+startX);
            Log.i("uu", "endX："+endX);
            Log.i("uu", "frame.left - mOffsetAll："+(frame.left - mOffsetAll));
            Log.i("uu", "frame.right - mOffsetAll："+(frame.right - mOffsetAll));
        }
        layoutDecorated(itemView,
                startX,
                frame.top,
                endX,
                frame.bottom);

        float scale = (-ratio + 2 - 2) / 50 + 1;
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);

        if(ratio > 1.3) {
            float alpha = (float)Math.max(0, 2.3 - ratio);
            itemView.setAlpha(alpha);
            Log.i("uu", "Alpha: "+alpha);
        }
    }

    private void computeMaterial() {
//        int width = getHorizontalSpace();
//        int height = getVerticalSpace();
//        for (int i = 0; i < getChildCount(); i++) {
//            float topSpace = height - width;
//            int y = (int) (topSpace * Math.pow(2, (i * width - scroll) / (float) width));
//            float scale = (float) (-Math.pow(2, -y / topSpace / 10.0f) + 19.0f / 10);
//            childTouchRect[i].set(
//                    getPaddingLeft(),
//                    y + getPaddingTop(),
//                    (int) (scale * (getPaddingLeft() + getWidth() - getPaddingLeft() - getPaddingRight())),
//                    (int) (scale * (y + getPaddingTop() + getWidth() - getPaddingLeft() - getPaddingRight())));
//            ViewHelper.setTranslationX(getChildAt(i), getPaddingLeft());
//            ViewHelper.setTranslationY(getChildAt(i), y + getPaddingTop());
//            ViewHelper.setScaleX(getChildAt(i), scale);
//            ViewHelper.setScaleY(getChildAt(i), scale);
//        }
    }
}