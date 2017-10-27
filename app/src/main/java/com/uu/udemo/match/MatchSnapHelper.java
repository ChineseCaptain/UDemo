package com.uu.udemo.match;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.SparseArray;
import android.view.View;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/10/27.
 */

public class MatchSnapHelper extends SnapHelper {

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        out[1] = 0;
        OrientationHelper horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        final int childStart = horizontalHelper.getDecoratedStart(targetView);
        int target = ((MatchLayoutManager)layoutManager).getHorizontalSpace();
        out[0] = childStart - target;
        return out;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof MatchLayoutManager) {
            SparseArray<View> cacheViews = ((MatchLayoutManager)layoutManager).getCacheViews();
            View topView = cacheViews.get(1);
            // 注意，这里的topView可能为null
            return topView;
        }
        return null;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        View topView = findSnapView(layoutManager);
        if (topView == null) {
            return RecyclerView.NO_POSITION;
        }
        return layoutManager.getPosition(topView);
    }
}
