package com.uu.udemo.itemtouch;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.List;

/**
 * description：
 * autohor：zhangguiyou
 * date: 2017/11/30.
 */

public class MyTouchCallback extends ItemTouchHelper.Callback {

    private static final String TAG = "MyTouchCallback";

    private OnItemTouchCallback onItemTouchCallback;

    public MyTouchCallback(OnItemTouchCallback onItemTouchCallback) {
        this.onItemTouchCallback = onItemTouchCallback;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        // 支持长按拖动
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 获取活动和拖拽的Flag
        // 在这个方法里设置你希望支持的滑动和拖拽方向。

        // 这里我们要支持上下拖动，上下滑动
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // 当发生了拖拽移动时调用
        if (onItemTouchCallback != null) {
            onItemTouchCallback.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        Log.i(TAG, "onMove");
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // 当发生了滑动时调用
        if (onItemTouchCallback != null) {
            onItemTouchCallback.onSwiped(viewHolder.getAdapterPosition(), direction);
        }
        Log.i(TAG, "onSwiped");
    }

    @Override
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected, List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
        return super.chooseDropTarget(selected, dropTargets, curX, curY);
    }

    public interface OnItemTouchCallback {
        void onMove(int from, int to);
        void onSwiped(int targetPosition, int direction);
    }
}
