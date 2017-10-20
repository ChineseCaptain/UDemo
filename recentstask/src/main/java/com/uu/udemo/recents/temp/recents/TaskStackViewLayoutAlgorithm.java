package com.uu.udemo.recents.temp.recents;

import android.graphics.Rect;

/* The layout logic for a TaskStackView.
 * TaskStackView的布局逻辑
 *
 * We are using a curve that defines the curve of the tasks as that go back in the recents list.
 * The curve is defined such that at curve progress p = 0 is the end of the curve (the top of the
 * stack rect), and p = 1 at the start of the curve and the bottom of the stack rect.
 * 我们正在使用一个曲线来定义任务的曲线，因为这些曲线可以在回调列表中返回。
 * 定义这条曲线为了让曲线在进行中，在曲线的结束（堆栈rect的顶部）时 p = 0，
 * 并且在曲线开始和堆叠rect的底部时p = 1。
 */
public class TaskStackViewLayoutAlgorithm {

    // These are all going to change
    // The min scale of the last card in the peek area
    // 这个数值是会改变的
    // 定义了栈层叠peek区域的最小值。
    static final float StackPeekMinScale = 0.8f;

    // A report of the visibility state of the stack
    public class VisibilityReport {
        public int numVisibleTasks;
        public int numVisibleThumbnails;

        /** Package level ctor */
        VisibilityReport(int tasks, int thumbnails) {
            numVisibleTasks = tasks;
            numVisibleThumbnails = thumbnails;
        }
    }

    RecentsConfiguration mConfig;

    // The various rects that define the stack view
    // 用下面的这些值来定义stack view
    /**
     * 整个屏幕的矩形区域
     */
    Rect mViewRect = new Rect();
    /**
     * 栈的矩形可见区域
     */
    Rect mStackVisibleRect = new Rect();
    /**
     * 栈的矩形区域
     */
    Rect mStackRect = new Rect();
    /**
     * 任务的矩形区域
     */
    Rect mTaskRect = new Rect();

    // The min/max scroll progress
    // 最大最小滑动进度
    float mMinScrollP;// 最小滚动进度
    float mMaxScrollP;// 最大滚动进度
    float mInitialScrollP;// 初始滚动进度值
    int mWithinAffiliationOffset;
    int mBetweenAffiliationOffset;
    HashMap<Task.TaskKey, Float> mTaskProgressMap = new HashMap<Task.TaskKey, Float>();

    // Log function
    // The large the XScale, the longer the flat area of the curve
    // Log函数：XScale越大，曲线的平面面积就越长
    static final float XScale = 1.75f;
    static final float LogBase = 3000;
    /**
     * 总步数
     */
    static final int PrecisionSteps = 250;
    static float[] xp;
    static float[] px;

    public TaskStackViewLayoutAlgorithm(RecentsConfiguration config) {
        mConfig = config;

        // Precompute the path
        // 预计算弧形的路径
        initializeCurve();
    }

    /**
     * Initializes the curve.
     * 初始化这条曲线
     * */
    public static void initializeCurve() {
        if (xp != null && px != null) return;
        xp = new float[PrecisionSteps + 1];
        px = new float[PrecisionSteps + 1];

        // Approximate f(x)
        // 使用曲线的对数函数，大致估算出每个item在曲线上的位置
        float[] fx = new float[PrecisionSteps + 1];
        float step = 1f / PrecisionSteps;
        float x = 0;
        for (int xStep = 0; xStep <= PrecisionSteps; xStep++) {
            fx[xStep] = logFunc(x);
            x += step;
        }

        // Calculate the arc length for x:1->0
        // 计算x：1-> 0的弧长
        float pLength = 0;
        float[] dx = new float[PrecisionSteps + 1];
        dx[0] = 0;
        for (int xStep = 1; xStep < PrecisionSteps; xStep++) {
            dx[xStep] = (float) Math.sqrt(Math.pow(fx[xStep] - fx[xStep - 1], 2) + Math.pow(step, 2));
            pLength += dx[xStep];
        }

        // Approximate p(x), a function of cumulative progress with x, normalized to 0..1
        // 函数p(x)：用x值来累计进度的函数。
        float p = 0;
        px[0] = 0f;
        px[PrecisionSteps] = 1f;
        for (int xStep = 1; xStep <= PrecisionSteps; xStep++) {
            p += Math.abs(dx[xStep] / pLength);
            px[xStep] = p;
        }

        // Given p(x), calculate the inverse function x(p). This assumes that x(p) is also a valid
        // function.
        // 利用p(x)来计算反函数x(p).
        // 这里假定x(p)是一个有效的函数
        int xStep = 0;
        p = 0;
        xp[0] = 0f;
        xp[PrecisionSteps] = 1f;
        for (int pStep = 0; pStep < PrecisionSteps; pStep++) {
            // Walk forward in px and find the x where px <= p && p < px+1
            while (xStep < PrecisionSteps) {
                if (px[xStep] > p) break;
                xStep++;
            }
            // Now, px[xStep-1] <= p < px[xStep]
            if (xStep == 0) {
                xp[pStep] = 0;
            } else {
                // Find x such that proportionally, x is correct
                float fraction = (p - px[xStep - 1]) / (px[xStep] - px[xStep - 1]);
                x = (xStep - 1 + fraction) * step;
                xp[pStep] = x;
            }
            p += step;
        }
    }

    /**
     * The log function describing the curve.
     * 描述曲线的对数函数
     * */
    static float logFunc(float x) {
        return 1f - (float) (Math.pow(LogBase, reverse(x))) / (LogBase);
    }

    /**
     * Reverses and scales out x.
     * 反转并缩放x
     * */
    static float reverse(float x) {
        return (-x * XScale) + 1;
    }

    /** Computes the stack and task rects
     * 计算栈和任务的显示区域。
     * 在TaskStackView的onMeasure中进行调用
     *
     * @param windowWidth  屏幕的宽
     * @param windowHeight  屏幕的高
     * @param taskStackBounds   任务栈的显示区域
     * */
    public void computeRects(int windowWidth, int windowHeight, Rect taskStackBounds) {
        // Compute the stack rects
        mViewRect.set(0, 0, windowWidth, windowHeight);
        mStackRect.set(taskStackBounds);
        mStackVisibleRect.set(taskStackBounds);
        mStackVisibleRect.bottom = mViewRect.bottom;

        int widthPadding = (int) (mConfig.taskStackWidthPaddingPct * mStackRect.width());
        int heightPadding = mConfig.taskStackTopPaddingPx;
        mStackRect.inset(widthPadding, heightPadding);

        // Compute the task rect
        // 计算任务的矩形区域rect
        int size = mStackRect.width();
        int left = mStackRect.left + (mStackRect.width() - size) / 2;
        mTaskRect.set(left, mStackRect.top,
                left + size, mStackRect.top + size);

        // Update the affiliation offsets
        float visibleTaskPct = 0.5f;
        mWithinAffiliationOffset = mConfig.taskBarHeight;
        mBetweenAffiliationOffset = (int) (visibleTaskPct * mTaskRect.height());
    }



    /** Computes the minimum and maximum scroll progress values.
     * This method may be called before
     * the RecentsConfiguration is set,
     * so we need to pass in the alt-tab state. */
    void computeMinMaxScroll(ArrayList<Task> tasks, boolean launchedWithAltTab,
                             boolean launchedFromHome) {
        // Clear the progress map
        mTaskProgressMap.clear();

        // Return early if we have no tasks
        if (tasks.isEmpty()) {
            mMinScrollP = mMaxScrollP = 0;
            return;
        }

        // Note that we should account for the scale difference of the offsets at the screen bottom
        int taskHeight = mTaskRect.height();
        float pAtBottomOfStackRect = screenYToCurveProgress(mStackVisibleRect.bottom);
        float pWithinAffiliateTop = screenYToCurveProgress(mStackVisibleRect.bottom -
                mWithinAffiliationOffset);
        float scale = curveProgressToScale(pWithinAffiliateTop);
        int scaleYOffset = (int) (((1f - scale) * taskHeight) / 2);
        pWithinAffiliateTop = screenYToCurveProgress(mStackVisibleRect.bottom -
                mWithinAffiliationOffset + scaleYOffset);
        float pWithinAffiliateOffset = pAtBottomOfStackRect - pWithinAffiliateTop;
        float pBetweenAffiliateOffset = pAtBottomOfStackRect -
                screenYToCurveProgress(mStackVisibleRect.bottom - mBetweenAffiliationOffset);
        float pTaskHeightOffset = pAtBottomOfStackRect -
                screenYToCurveProgress(mStackVisibleRect.bottom - taskHeight);

        // Update the task offsets
        float pAtBackMostCardTop = 0.5f;
        float pAtFrontMostCardTop = pAtBackMostCardTop;
        int taskCount = tasks.size();
        for (int i = 0; i < taskCount; i++) {
            Task task = tasks.get(i);
            mTaskProgressMap.put(task.key, pAtFrontMostCardTop);

            if (i < (taskCount - 1)) {
                // Increment the peek height
                float pPeek = task.group.isFrontMostTask(task) ?
                        pBetweenAffiliateOffset : pWithinAffiliateOffset;
                pAtFrontMostCardTop += pPeek;
            }
        }

        mMaxScrollP = pAtFrontMostCardTop + pDismissAllButtonOffset -
                ((1f - pTaskHeightOffset - pNavBarOffset));
        mMinScrollP = tasks.size() == 1 ? Math.max(mMaxScrollP, 0f) : 0f;
        if (launchedWithAltTab && launchedFromHome) {
            // Center the top most task, since that will be focused first
            mInitialScrollP = mMaxScrollP;
        } else {
            mInitialScrollP = pAtFrontMostCardTop - 0.825f;
        }
        mInitialScrollP = Math.min(mMaxScrollP, Math.max(0, mInitialScrollP));
    }


















    /**
     * Computes the maximum number of visible tasks and thumbnails.
     * Requires that computeMinMaxScroll() is called first.
     * 没有用到
     */
    public VisibilityReport computeStackVisibilityReport(ArrayList<Task> tasks) {
        if (tasks.size() <= 1) {
            return new VisibilityReport(1, 1);
        }

        // Walk backwards in the task stack and count the number of tasks and visible thumbnails
        int taskHeight = mTaskRect.height();
        int numVisibleTasks = 1;
        int numVisibleThumbnails = 1;
        float progress = mTaskProgressMap.get(tasks.get(tasks.size() - 1).key) - mInitialScrollP;
        int prevScreenY = curveProgressToScreenY(progress);
        for (int i = tasks.size() - 2; i >= 0; i--) {
            Task task = tasks.get(i);
            progress = mTaskProgressMap.get(task.key) - mInitialScrollP;
            if (progress < 0) {
                break;
            }
            boolean isFrontMostTaskInGroup = task.group.isFrontMostTask(task);
            if (isFrontMostTaskInGroup) {
                float scaleAtP = curveProgressToScale(progress);
                int scaleYOffsetAtP = (int) (((1f - scaleAtP) * taskHeight) / 2);
                int screenY = curveProgressToScreenY(progress) + scaleYOffsetAtP;
                boolean hasVisibleThumbnail = (prevScreenY - screenY) > mConfig.taskBarHeight;
                if (hasVisibleThumbnail) {
                    numVisibleThumbnails++;
                    numVisibleTasks++;
                    prevScreenY = screenY;
                } else {
                    // Once we hit the next front most task that does not have a visible thumbnail,
                    // walk through remaining visible set
                    for (int j = i; j >= 0; j--) {
                        numVisibleTasks++;
                        progress = mTaskProgressMap.get(tasks.get(j).key) - mInitialScrollP;
                        if (progress < 0) {
                            break;
                        }
                    }
                    break;
                }
            } else if (!isFrontMostTaskInGroup) {
                // Affiliated task, no thumbnail
                numVisibleTasks++;
            }
        }
        return new VisibilityReport(numVisibleTasks, numVisibleThumbnails);
    }

    /**
     * Update/get the transform
     * */
    public TaskViewTransform getStackTransform(Task task, float stackScroll,
            TaskViewTransform transformOut, TaskViewTransform prevTransform) {
        // Return early if we have an invalid index
        if (task == null || !mTaskProgressMap.containsKey(task.key)) {
            transformOut.reset();
            return transformOut;
        }
        return getStackTransform(mTaskProgressMap.get(task.key), stackScroll, transformOut,
                prevTransform);
    }

    /** Update/get the transform */
    public TaskViewTransform getStackTransform(float taskProgress, float stackScroll,
            TaskViewTransform transformOut, TaskViewTransform prevTransform) {
        float pTaskRelative = taskProgress - stackScroll;
        float pBounded = Math.max(0, Math.min(pTaskRelative, 1f));
        // If the task top is outside of the bounds below the screen, then immediately reset it
        if (pTaskRelative > 1f) {
            transformOut.reset();
            transformOut.rect.set(mTaskRect);
            return transformOut;
        }
        // The check for the top is trickier, since we want to show the next task if it is at all
        // visible, even if p < 0.
        if (pTaskRelative < 0f) {
            if (prevTransform != null && Float.compare(prevTransform.p, 0f) <= 0) {
                transformOut.reset();
                transformOut.rect.set(mTaskRect);
                return transformOut;
            }
        }
        float scale = curveProgressToScale(pBounded);
        int scaleYOffset = (int) (((1f - scale) * mTaskRect.height()) / 2);
        int minZ = mConfig.taskViewTranslationZMinPx;
        int maxZ = mConfig.taskViewTranslationZMaxPx;
        transformOut.scale = scale;
        transformOut.translationY = curveProgressToScreenY(pBounded) - mStackVisibleRect.top -
                scaleYOffset;
        transformOut.translationZ = Math.max(minZ, minZ + (pBounded * (maxZ - minZ)));
        transformOut.rect.set(mTaskRect);
        transformOut.rect.offset(0, transformOut.translationY);
        Utilities.scaleRectAboutCenter(transformOut.rect, transformOut.scale);
        transformOut.visible = true;
        transformOut.p = pTaskRelative;
        return transformOut;
    }

    /** Returns the untransformed task view size. */
    public Rect getUntransformedTaskViewSize() {
        Rect tvSize = new Rect(mTaskRect);
        tvSize.offsetTo(0, 0);
        return tvSize;
    }

    /** Returns the scroll to such task top = 1f; */
    float getStackScrollForTask(Task t) {
        if (!mTaskProgressMap.containsKey(t.key)) return 0f;
        return mTaskProgressMap.get(t.key);
    }

    /** The inverse of the log function describing the curve. */
    float invLogFunc(float y) {
        return (float) (Math.log((1f - reverse(y)) * (LogBase - 1) + 1) / Math.log(LogBase));
    }

    /** Converts from the progress along the curve to a screen coordinate. */
    int curveProgressToScreenY(float p) {
        if (p < 0 || p > 1) return mStackVisibleRect.top + (int) (p * mStackVisibleRect.height());
        float pIndex = p * PrecisionSteps;
        int pFloorIndex = (int) Math.floor(pIndex);
        int pCeilIndex = (int) Math.ceil(pIndex);
        float xFraction = 0;
        if (pFloorIndex < PrecisionSteps && (pCeilIndex != pFloorIndex)) {
            float pFraction = (pIndex - pFloorIndex) / (pCeilIndex - pFloorIndex);
            xFraction = (xp[pCeilIndex] - xp[pFloorIndex]) * pFraction;
        }
        float x = xp[pFloorIndex] + xFraction;
        return mStackVisibleRect.top + (int) (x * mStackVisibleRect.height());
    }

    /** Converts from the progress along the curve to a scale. */
    float curveProgressToScale(float p) {
        if (p < 0) return StackPeekMinScale;
        if (p > 1) return 1f;
        float scaleRange = (1f - StackPeekMinScale);
        float scale = StackPeekMinScale + (p * scaleRange);
        return scale;
    }

    /** Converts from a screen coordinate to the progress along the curve. */
    float screenYToCurveProgress(int screenY) {
        float x = (float) (screenY - mStackVisibleRect.top) / mStackVisibleRect.height();
        if (x < 0 || x > 1) return x;
        float xIndex = x * PrecisionSteps;
        int xFloorIndex = (int) Math.floor(xIndex);
        int xCeilIndex = (int) Math.ceil(xIndex);
        float pFraction = 0;
        if (xFloorIndex < PrecisionSteps && (xCeilIndex != xFloorIndex)) {
            float xFraction = (xIndex - xFloorIndex) / (xCeilIndex - xFloorIndex);
            pFraction = (px[xCeilIndex] - px[xFloorIndex]) * xFraction;
        }
        return px[xFloorIndex] + pFraction;
    }
}
