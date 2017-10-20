package com.uu.udemo.recents.temp.recents;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * item布局的算法
 */
public class LayoutChildAlgorithm {

    /** Return the relative coordinate given coordinates in another size. */
    private int getRelativeCoordinate(int availableOffset, int availableSize, int otherCoord, int otherSize) {
        float relPos = (float) otherCoord / otherSize;
        return availableOffset + (int) (relPos * availableSize);
    }

    /**
     * Computes and returns the bounds that each of the stack views should take up.
     * @param itemViews    全部任务Views
     * @param availableBounds    可留给任务栈列表显示的区域
     * @return
     */
    public List<Rect> computeStackRects(List<ItemView> itemViews, Rect availableBounds) {
        ArrayList<Rect> bounds = new ArrayList<Rect>(itemViews.size());
        int itemViewsCount = itemViews.size();
        for (int i = 0; i < itemViewsCount; i++) {
            ItemView item = itemViews.get(i);
            Rect sb = item.stackBounds;//栈的全部区域
            Rect db = item.displayBounds;//可用户显示的区域
            Rect ab = availableBounds;
            bounds.add(new Rect(getRelativeCoordinate(ab.left, ab.width(), sb.left, db.width()),
                    getRelativeCoordinate(ab.top, ab.height(), sb.top, db.height()),
                    getRelativeCoordinate(ab.left, ab.width(), sb.right, db.width()),
                    getRelativeCoordinate(ab.top, ab.height(), sb.bottom, db.height())));
        }
        return bounds;
    }
}
