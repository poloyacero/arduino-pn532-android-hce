package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;

public class GridLayoutManager extends LinearLayoutManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_SPAN_COUNT = -1;
    private static final String TAG = "GridLayoutManager";
    int[] mCachedBorders;
    final Rect mDecorInsets = new Rect();
    boolean mPendingSpanCountChange = false;
    final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
    final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
    View[] mSet;
    int mSpanCount = -1;
    SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();

    public GridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSpanCount(getProperties(context, attrs, defStyleAttr, defStyleRes).spanCount);
    }

    public GridLayoutManager(Context context, int spanCount) {
        super(context);
        setSpanCount(spanCount);
    }

    public GridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setSpanCount(spanCount);
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager
    public void setStackFromEnd(boolean stackFromEnd) {
        if (!stackFromEnd) {
            super.setStackFromEnd(false);
            return;
        }
        throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int getColumnCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View host, AccessibilityNodeInfoCompat info) {
        ViewGroup.LayoutParams lp = host.getLayoutParams();
        if (!(lp instanceof LayoutParams)) {
            super.onInitializeAccessibilityNodeInfoForItem(host, info);
            return;
        }
        LayoutParams glp = (LayoutParams) lp;
        int spanGroupIndex = getSpanGroupIndex(recycler, state, glp.getViewLayoutPosition());
        if (this.mOrientation == 0) {
            info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(glp.getSpanIndex(), glp.getSpanSize(), spanGroupIndex, 1, this.mSpanCount > 1 && glp.getSpanSize() == this.mSpanCount, false));
        } else {
            info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(spanGroupIndex, 1, glp.getSpanIndex(), glp.getSpanSize(), this.mSpanCount > 1 && glp.getSpanSize() == this.mSpanCount, false));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, androidx.recyclerview.widget.LinearLayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        clearPreLayoutSpanMappingCache();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, androidx.recyclerview.widget.LinearLayoutManager
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        this.mPendingSpanCountChange = false;
    }

    private void clearPreLayoutSpanMappingCache() {
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }

    private void cachePreLayoutSpanMapping() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
            int viewPosition = lp.getViewLayoutPosition();
            this.mPreLayoutSpanSizeCache.put(viewPosition, lp.getSpanSize());
            this.mPreLayoutSpanIndexCache.put(viewPosition, lp.getSpanIndex());
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsChanged(RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, androidx.recyclerview.widget.LinearLayoutManager
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -1);
        }
        return new LayoutParams(-1, -2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
        }
        return new LayoutParams(lp);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return this.mSpanSizeLookup;
    }

    private void updateMeasurements() {
        int totalSpace;
        if (getOrientation() == 1) {
            totalSpace = (getWidth() - getPaddingRight()) - getPaddingLeft();
        } else {
            totalSpace = (getHeight() - getPaddingBottom()) - getPaddingTop();
        }
        calculateItemBorders(totalSpace);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        int width;
        int usedHeight;
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(childrenBounds, wSpec, hSpec);
        }
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int verticalPadding = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation == 1) {
            width = chooseSize(hSpec, childrenBounds.height() + verticalPadding, getMinimumHeight());
            int[] iArr = this.mCachedBorders;
            usedHeight = chooseSize(wSpec, iArr[iArr.length - 1] + horizontalPadding, getMinimumWidth());
        } else {
            int width2 = chooseSize(wSpec, childrenBounds.width() + horizontalPadding, getMinimumWidth());
            int[] iArr2 = this.mCachedBorders;
            usedHeight = width2;
            width = chooseSize(hSpec, iArr2[iArr2.length - 1] + verticalPadding, getMinimumHeight());
        }
        setMeasuredDimension(usedHeight, width);
    }

    private void calculateItemBorders(int totalSpace) {
        this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, totalSpace);
    }

    static int[] calculateItemBorders(int[] cachedBorders, int spanCount, int totalSpace) {
        if (!(cachedBorders != null && cachedBorders.length == spanCount + 1 && cachedBorders[cachedBorders.length - 1] == totalSpace)) {
            cachedBorders = new int[(spanCount + 1)];
        }
        cachedBorders[0] = 0;
        int sizePerSpan = totalSpace / spanCount;
        int sizePerSpanRemainder = totalSpace % spanCount;
        int consumedPixels = 0;
        int additionalSize = 0;
        for (int i = 1; i <= spanCount; i++) {
            int itemSize = sizePerSpan;
            additionalSize += sizePerSpanRemainder;
            if (additionalSize > 0 && spanCount - additionalSize < sizePerSpanRemainder) {
                itemSize++;
                additionalSize -= spanCount;
            }
            consumedPixels += itemSize;
            cachedBorders[i] = consumedPixels;
        }
        return cachedBorders;
    }

    /* access modifiers changed from: package-private */
    public int getSpaceForSpanRange(int startSpan, int spanSize) {
        if (this.mOrientation != 1 || !isLayoutRTL()) {
            int[] iArr = this.mCachedBorders;
            return iArr[startSpan + spanSize] - iArr[startSpan];
        }
        int[] iArr2 = this.mCachedBorders;
        int i = this.mSpanCount;
        return iArr2[i - startSpan] - iArr2[(i - startSpan) - spanSize];
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.recyclerview.widget.LinearLayoutManager
    public void onAnchorReady(RecyclerView.Recycler recycler, RecyclerView.State state, LinearLayoutManager.AnchorInfo anchorInfo, int itemDirection) {
        super.onAnchorReady(recycler, state, anchorInfo, itemDirection);
        updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, itemDirection);
        }
        ensureViewSet();
    }

    private void ensureViewSet() {
        View[] viewArr = this.mSet;
        if (viewArr == null || viewArr.length != this.mSpanCount) {
            this.mSet = new View[this.mSpanCount];
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, androidx.recyclerview.widget.LinearLayoutManager
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, androidx.recyclerview.widget.LinearLayoutManager
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    private void ensureAnchorIsInCorrectSpan(RecyclerView.Recycler recycler, RecyclerView.State state, LinearLayoutManager.AnchorInfo anchorInfo, int itemDirection) {
        boolean layingOutInPrimaryDirection = itemDirection == 1;
        int span = getSpanIndex(recycler, state, anchorInfo.mPosition);
        if (layingOutInPrimaryDirection) {
            while (span > 0 && anchorInfo.mPosition > 0) {
                anchorInfo.mPosition--;
                span = getSpanIndex(recycler, state, anchorInfo.mPosition);
            }
            return;
        }
        int indexLimit = state.getItemCount() - 1;
        int pos = anchorInfo.mPosition;
        int bestSpan = span;
        while (pos < indexLimit) {
            int next = getSpanIndex(recycler, state, pos + 1);
            if (next <= bestSpan) {
                break;
            }
            pos++;
            bestSpan = next;
        }
        anchorInfo.mPosition = pos;
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.recyclerview.widget.LinearLayoutManager
    public View findReferenceChild(RecyclerView.Recycler recycler, RecyclerView.State state, int start, int end, int itemCount) {
        ensureLayoutState();
        View invalidMatch = null;
        View outOfBoundsMatch = null;
        int boundsStart = this.mOrientationHelper.getStartAfterPadding();
        int boundsEnd = this.mOrientationHelper.getEndAfterPadding();
        int diff = end > start ? 1 : -1;
        for (int i = start; i != end; i += diff) {
            View view = getChildAt(i);
            int position = getPosition(view);
            if (position >= 0 && position < itemCount && getSpanIndex(recycler, state, position) == 0) {
                if (((RecyclerView.LayoutParams) view.getLayoutParams()).isItemRemoved()) {
                    if (invalidMatch == null) {
                        invalidMatch = view;
                    }
                } else if (this.mOrientationHelper.getDecoratedStart(view) < boundsEnd && this.mOrientationHelper.getDecoratedEnd(view) >= boundsStart) {
                    return view;
                } else {
                    if (outOfBoundsMatch == null) {
                        outOfBoundsMatch = view;
                    }
                }
            }
        }
        return outOfBoundsMatch != null ? outOfBoundsMatch : invalidMatch;
    }

    private int getSpanGroupIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int viewPosition) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanGroupIndex(viewPosition, this.mSpanCount);
        }
        int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(viewPosition);
        if (adapterPosition != -1) {
            return this.mSpanSizeLookup.getSpanGroupIndex(adapterPosition, this.mSpanCount);
        }
        Log.w(TAG, "Cannot find span size for pre layout position. " + viewPosition);
        return 0;
    }

    private int getSpanIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int pos) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(pos, this.mSpanCount);
        }
        int cached = this.mPreLayoutSpanIndexCache.get(pos, -1);
        if (cached != -1) {
            return cached;
        }
        int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(pos);
        if (adapterPosition != -1) {
            return this.mSpanSizeLookup.getCachedSpanIndex(adapterPosition, this.mSpanCount);
        }
        Log.w(TAG, "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + pos);
        return 0;
    }

    private int getSpanSize(RecyclerView.Recycler recycler, RecyclerView.State state, int pos) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(pos);
        }
        int cached = this.mPreLayoutSpanSizeCache.get(pos, -1);
        if (cached != -1) {
            return cached;
        }
        int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(pos);
        if (adapterPosition != -1) {
            return this.mSpanSizeLookup.getSpanSize(adapterPosition);
        }
        Log.w(TAG, "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + pos);
        return 1;
    }

    /* access modifiers changed from: package-private */
    @Override // androidx.recyclerview.widget.LinearLayoutManager
    public void collectPrefetchPositionsForLayoutState(RecyclerView.State state, LinearLayoutManager.LayoutState layoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int remainingSpan = this.mSpanCount;
        for (int count = 0; count < this.mSpanCount && layoutState.hasMore(state) && remainingSpan > 0; count++) {
            int pos = layoutState.mCurrentPosition;
            layoutPrefetchRegistry.addPosition(pos, Math.max(0, layoutState.mScrollingOffset));
            remainingSpan -= this.mSpanSizeLookup.getSpanSize(pos);
            layoutState.mCurrentPosition += layoutState.mItemDirection;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r25v0, resolved type: androidx.recyclerview.widget.GridLayoutManager */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v0 */
    /* JADX WARN: Type inference failed for: r13v1, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r13v9 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // androidx.recyclerview.widget.LinearLayoutManager
    public void layoutChunk(RecyclerView.Recycler recycler, RecyclerView.State state, LinearLayoutManager.LayoutState layoutState, LinearLayoutManager.LayoutChunkResult result) {
        int consumedSpanCount;
        int itemSpanSize;
        int maxSize;
        int bottom;
        int right;
        int left;
        int top;
        int otherDirSpecMode;
        float maxSizeInOther;
        int remainingSpan;
        int hSpec;
        int wSpec;
        View view;
        int otherDirSpecMode2 = this.mOrientationHelper.getModeInOther();
        ?? r13 = 0;
        boolean flexibleInOtherDir = otherDirSpecMode2 != 1073741824;
        int currentOtherDirSize = getChildCount() > 0 ? this.mCachedBorders[this.mSpanCount] : 0;
        if (flexibleInOtherDir) {
            updateMeasurements();
        }
        boolean layingOutInPrimaryDirection = layoutState.mItemDirection == 1;
        int remainingSpan2 = this.mSpanCount;
        if (!layingOutInPrimaryDirection) {
            remainingSpan2 = getSpanIndex(recycler, state, layoutState.mCurrentPosition) + getSpanSize(recycler, state, layoutState.mCurrentPosition);
            itemSpanSize = 0;
            consumedSpanCount = 0;
        } else {
            itemSpanSize = 0;
            consumedSpanCount = 0;
        }
        while (itemSpanSize < this.mSpanCount && layoutState.hasMore(state) && remainingSpan2 > 0) {
            int pos = layoutState.mCurrentPosition;
            int spanSize = getSpanSize(recycler, state, pos);
            if (spanSize <= this.mSpanCount) {
                remainingSpan2 -= spanSize;
                if (remainingSpan2 < 0 || (view = layoutState.next(recycler)) == null) {
                    break;
                }
                consumedSpanCount += spanSize;
                this.mSet[itemSpanSize] = view;
                itemSpanSize++;
            } else {
                throw new IllegalArgumentException("Item at position " + pos + " requires " + spanSize + " spans but GridLayoutManager has only " + this.mSpanCount + " spans.");
            }
        }
        int remainingSpan3 = remainingSpan2;
        if (itemSpanSize == 0) {
            result.mFinished = true;
            return;
        }
        assignSpans(recycler, state, itemSpanSize, consumedSpanCount, layingOutInPrimaryDirection);
        int i = 0;
        int maxSize2 = 0;
        float maxSizeInOther2 = 0.0f;
        while (i < itemSpanSize) {
            View view2 = this.mSet[i];
            if (layoutState.mScrapList == null) {
                if (layingOutInPrimaryDirection) {
                    addView(view2);
                } else {
                    addView(view2, r13);
                }
            } else if (layingOutInPrimaryDirection) {
                addDisappearingView(view2);
            } else {
                int i2 = r13 == true ? 1 : 0;
                int i3 = r13 == true ? 1 : 0;
                int i4 = r13 == true ? 1 : 0;
                addDisappearingView(view2, i2);
            }
            calculateItemDecorationsForChild(view2, this.mDecorInsets);
            measureChild(view2, otherDirSpecMode2, (boolean) r13);
            int size = this.mOrientationHelper.getDecoratedMeasurement(view2);
            if (size > maxSize2) {
                maxSize2 = size;
            }
            float otherSize = (((float) this.mOrientationHelper.getDecoratedMeasurementInOther(view2)) * 1.0f) / ((float) ((LayoutParams) view2.getLayoutParams()).mSpanSize);
            if (otherSize > maxSizeInOther2) {
                maxSizeInOther2 = otherSize;
            }
            i++;
            maxSize2 = maxSize2;
            r13 = 0;
        }
        if (flexibleInOtherDir) {
            guessMeasurement(maxSizeInOther2, currentOtherDirSize);
            int maxSize3 = 0;
            for (int i5 = 0; i5 < itemSpanSize; i5++) {
                View view3 = this.mSet[i5];
                measureChild(view3, 1073741824, true);
                int size2 = this.mOrientationHelper.getDecoratedMeasurement(view3);
                if (size2 > maxSize3) {
                    maxSize3 = size2;
                }
            }
            maxSize = maxSize3;
        } else {
            maxSize = maxSize2;
        }
        int i6 = 0;
        while (i6 < itemSpanSize) {
            View view4 = this.mSet[i6];
            if (this.mOrientationHelper.getDecoratedMeasurement(view4) != maxSize) {
                LayoutParams lp = (LayoutParams) view4.getLayoutParams();
                Rect decorInsets = lp.mDecorInsets;
                maxSizeInOther = maxSizeInOther2;
                int verticalInsets = decorInsets.top + decorInsets.bottom + lp.topMargin + lp.bottomMargin;
                int horizontalInsets = decorInsets.left + decorInsets.right + lp.leftMargin + lp.rightMargin;
                int totalSpaceInOther = getSpaceForSpanRange(lp.mSpanIndex, lp.mSpanSize);
                otherDirSpecMode = otherDirSpecMode2;
                if (this.mOrientation == 1) {
                    remainingSpan = remainingSpan3;
                    wSpec = getChildMeasureSpec(totalSpaceInOther, 1073741824, horizontalInsets, lp.width, false);
                    hSpec = View.MeasureSpec.makeMeasureSpec(maxSize - verticalInsets, 1073741824);
                } else {
                    remainingSpan = remainingSpan3;
                    wSpec = View.MeasureSpec.makeMeasureSpec(maxSize - horizontalInsets, 1073741824);
                    hSpec = getChildMeasureSpec(totalSpaceInOther, 1073741824, verticalInsets, lp.height, false);
                }
                measureChildWithDecorationsAndMargin(view4, wSpec, hSpec, true);
            } else {
                maxSizeInOther = maxSizeInOther2;
                otherDirSpecMode = otherDirSpecMode2;
                remainingSpan = remainingSpan3;
            }
            i6++;
            remainingSpan3 = remainingSpan;
            maxSizeInOther2 = maxSizeInOther;
            otherDirSpecMode2 = otherDirSpecMode;
        }
        result.mConsumed = maxSize;
        int left2 = 0;
        int right2 = 0;
        int top2 = 0;
        int bottom2 = 0;
        if (this.mOrientation == 1) {
            if (layoutState.mLayoutDirection == -1) {
                bottom2 = layoutState.mOffset;
                top2 = bottom2 - maxSize;
            } else {
                top2 = layoutState.mOffset;
                bottom2 = top2 + maxSize;
            }
        } else if (layoutState.mLayoutDirection == -1) {
            right2 = layoutState.mOffset;
            left2 = right2 - maxSize;
        } else {
            left2 = layoutState.mOffset;
            right2 = left2 + maxSize;
        }
        int i7 = 0;
        while (i7 < itemSpanSize) {
            View view5 = this.mSet[i7];
            LayoutParams params = (LayoutParams) view5.getLayoutParams();
            if (this.mOrientation != 1) {
                left = left2;
                right = right2;
                int top3 = getPaddingTop() + this.mCachedBorders[params.mSpanIndex];
                top = top3;
                bottom = this.mOrientationHelper.getDecoratedMeasurementInOther(view5) + top3;
            } else if (isLayoutRTL()) {
                int right3 = getPaddingLeft() + this.mCachedBorders[this.mSpanCount - params.mSpanIndex];
                left = right3 - this.mOrientationHelper.getDecoratedMeasurementInOther(view5);
                top = top2;
                bottom = bottom2;
                right = right3;
            } else {
                int left3 = getPaddingLeft() + this.mCachedBorders[params.mSpanIndex];
                left = left3;
                right = this.mOrientationHelper.getDecoratedMeasurementInOther(view5) + left3;
                top = top2;
                bottom = bottom2;
            }
            layoutDecoratedWithMargins(view5, left, top, right, bottom);
            if (params.isItemRemoved() || params.isItemChanged()) {
                result.mIgnoreConsumed = true;
            }
            result.mFocusable |= view5.hasFocusable();
            i7++;
            top2 = top;
            left2 = left;
            right2 = right;
            bottom2 = bottom;
            maxSize = maxSize;
        }
        Arrays.fill(this.mSet, (Object) null);
    }

    private void measureChild(View view, int otherDirParentSpecMode, boolean alreadyMeasured) {
        int hSpec;
        int wSpec;
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        Rect decorInsets = lp.mDecorInsets;
        int verticalInsets = decorInsets.top + decorInsets.bottom + lp.topMargin + lp.bottomMargin;
        int horizontalInsets = decorInsets.left + decorInsets.right + lp.leftMargin + lp.rightMargin;
        int availableSpaceInOther = getSpaceForSpanRange(lp.mSpanIndex, lp.mSpanSize);
        if (this.mOrientation == 1) {
            wSpec = getChildMeasureSpec(availableSpaceInOther, otherDirParentSpecMode, horizontalInsets, lp.width, false);
            hSpec = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), verticalInsets, lp.height, true);
        } else {
            hSpec = getChildMeasureSpec(availableSpaceInOther, otherDirParentSpecMode, verticalInsets, lp.height, false);
            wSpec = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getWidthMode(), horizontalInsets, lp.width, true);
        }
        measureChildWithDecorationsAndMargin(view, wSpec, hSpec, alreadyMeasured);
    }

    private void guessMeasurement(float maxSizeInOther, int currentOtherDirSize) {
        calculateItemBorders(Math.max(Math.round(((float) this.mSpanCount) * maxSizeInOther), currentOtherDirSize));
    }

    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec, boolean alreadyMeasured) {
        boolean measure;
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        if (alreadyMeasured) {
            measure = shouldReMeasureChild(child, widthSpec, heightSpec, lp);
        } else {
            measure = shouldMeasureChild(child, widthSpec, heightSpec, lp);
        }
        if (measure) {
            child.measure(widthSpec, heightSpec);
        }
    }

    private void assignSpans(RecyclerView.Recycler recycler, RecyclerView.State state, int count, int consumedSpanCount, boolean layingOutInPrimaryDirection) {
        int diff;
        int end;
        int start;
        if (layingOutInPrimaryDirection) {
            start = 0;
            end = count;
            diff = 1;
        } else {
            start = count - 1;
            end = -1;
            diff = -1;
        }
        int span = 0;
        for (int i = start; i != end; i += diff) {
            View view = this.mSet[i];
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.mSpanSize = getSpanSize(recycler, state, getPosition(view));
            params.mSpanIndex = span;
            span += params.mSpanSize;
        }
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public void setSpanCount(int spanCount) {
        if (spanCount != this.mSpanCount) {
            this.mPendingSpanCountChange = true;
            if (spanCount >= 1) {
                this.mSpanCount = spanCount;
                this.mSpanSizeLookup.invalidateSpanIndexCache();
                requestLayout();
                return;
            }
            throw new IllegalArgumentException("Span count should be at least 1. Provided " + spanCount);
        }
    }

    public static abstract class SpanSizeLookup {
        private boolean mCacheSpanIndices = false;
        final SparseIntArray mSpanIndexCache = new SparseIntArray();

        public abstract int getSpanSize(int i);

        public void setSpanIndexCacheEnabled(boolean cacheSpanIndices) {
            this.mCacheSpanIndices = cacheSpanIndices;
        }

        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }

        public boolean isSpanIndexCacheEnabled() {
            return this.mCacheSpanIndices;
        }

        /* access modifiers changed from: package-private */
        public int getCachedSpanIndex(int position, int spanCount) {
            if (!this.mCacheSpanIndices) {
                return getSpanIndex(position, spanCount);
            }
            int existing = this.mSpanIndexCache.get(position, -1);
            if (existing != -1) {
                return existing;
            }
            int value = getSpanIndex(position, spanCount);
            this.mSpanIndexCache.put(position, value);
            return value;
        }

        public int getSpanIndex(int position, int spanCount) {
            int prevKey;
            int positionSpanSize = getSpanSize(position);
            if (positionSpanSize == spanCount) {
                return 0;
            }
            int span = 0;
            int startPos = 0;
            if (this.mCacheSpanIndices && this.mSpanIndexCache.size() > 0 && (prevKey = findReferenceIndexFromCache(position)) >= 0) {
                span = this.mSpanIndexCache.get(prevKey) + getSpanSize(prevKey);
                startPos = prevKey + 1;
            }
            for (int i = startPos; i < position; i++) {
                int size = getSpanSize(i);
                span += size;
                if (span == spanCount) {
                    span = 0;
                } else if (span > spanCount) {
                    span = size;
                }
            }
            if (span + positionSpanSize <= spanCount) {
                return span;
            }
            return 0;
        }

        /* access modifiers changed from: package-private */
        public int findReferenceIndexFromCache(int position) {
            int lo = 0;
            int hi = this.mSpanIndexCache.size() - 1;
            while (lo <= hi) {
                int mid = (lo + hi) >>> 1;
                if (this.mSpanIndexCache.keyAt(mid) < position) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            int index = lo - 1;
            if (index < 0 || index >= this.mSpanIndexCache.size()) {
                return -1;
            }
            return this.mSpanIndexCache.keyAt(index);
        }

        public int getSpanGroupIndex(int adapterPosition, int spanCount) {
            int span = 0;
            int group = 0;
            int positionSpanSize = getSpanSize(adapterPosition);
            for (int i = 0; i < adapterPosition; i++) {
                int size = getSpanSize(i);
                span += size;
                if (span == spanCount) {
                    span = 0;
                    group++;
                } else if (span > spanCount) {
                    span = size;
                    group++;
                }
            }
            if (span + positionSpanSize > spanCount) {
                return group + 1;
            }
            return group;
        }
    }

    /* JADX INFO: Multiple debug info for r3v3 int: [D('candidateStart' int), D('prevFocusedChild' android.view.View)] */
    /* JADX INFO: Multiple debug info for r13v10 int: [D('assignAsWeek' boolean), D('overlap' int)] */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, androidx.recyclerview.widget.LinearLayoutManager
    public View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int limit;
        int inc;
        int start;
        int focusableSpanGroupIndex;
        View prevFocusedChild;
        int focusableWeakCandidateSpanIndex;
        int focusableWeakCandidateOverlap;
        int overlap;
        RecyclerView.Recycler recycler2 = recycler;
        RecyclerView.State state2 = state;
        View prevFocusedChild2 = findContainingItemView(focused);
        if (prevFocusedChild2 == null) {
            return null;
        }
        LayoutParams lp = (LayoutParams) prevFocusedChild2.getLayoutParams();
        int prevSpanStart = lp.mSpanIndex;
        int prevSpanEnd = lp.mSpanIndex + lp.mSpanSize;
        if (super.onFocusSearchFailed(focused, focusDirection, recycler, state) == null) {
            return null;
        }
        if ((convertFocusDirectionToLayoutDirection(focusDirection) == 1) != this.mShouldReverseLayout) {
            start = getChildCount() - 1;
            inc = -1;
            limit = -1;
        } else {
            start = 0;
            inc = 1;
            limit = getChildCount();
        }
        boolean preferLastSpan = this.mOrientation == 1 && isLayoutRTL();
        View focusableWeakCandidate = null;
        View unfocusableWeakCandidate = null;
        int focusableSpanGroupIndex2 = getSpanGroupIndex(recycler2, state2, start);
        int focusableWeakCandidateSpanIndex2 = -1;
        int focusableWeakCandidateSpanIndex3 = 0;
        int unfocusableWeakCandidateSpanIndex = -1;
        int unfocusableWeakCandidateOverlap = 0;
        int i = start;
        while (true) {
            if (i == limit) {
                break;
            }
            int spanGroupIndex = getSpanGroupIndex(recycler2, state2, i);
            View candidate = getChildAt(i);
            if (candidate == prevFocusedChild2) {
                break;
            }
            if (!candidate.hasFocusable() || spanGroupIndex == focusableSpanGroupIndex2) {
                LayoutParams candidateLp = (LayoutParams) candidate.getLayoutParams();
                prevFocusedChild = prevFocusedChild2;
                int candidateStart = candidateLp.mSpanIndex;
                focusableSpanGroupIndex = focusableSpanGroupIndex2;
                int candidateEnd = candidateLp.mSpanIndex + candidateLp.mSpanSize;
                if (candidate.hasFocusable() && candidateStart == prevSpanStart && candidateEnd == prevSpanEnd) {
                    return candidate;
                }
                if ((!candidate.hasFocusable() || focusableWeakCandidate != null) && (candidate.hasFocusable() || unfocusableWeakCandidate != null)) {
                    int overlap2 = Math.min(candidateEnd, prevSpanEnd) - Math.max(candidateStart, prevSpanStart);
                    if (!candidate.hasFocusable()) {
                        focusableWeakCandidateSpanIndex = focusableWeakCandidateSpanIndex2;
                        if (focusableWeakCandidate == null) {
                            focusableWeakCandidateOverlap = focusableWeakCandidateSpanIndex3;
                            boolean z = false;
                            if (isViewPartiallyVisible(candidate, false, true)) {
                                if (overlap2 > unfocusableWeakCandidateOverlap) {
                                    overlap = 1;
                                } else if (overlap2 == unfocusableWeakCandidateOverlap) {
                                    if (candidateStart > unfocusableWeakCandidateSpanIndex) {
                                        z = true;
                                    }
                                    if (preferLastSpan == z) {
                                        overlap = 1;
                                    }
                                }
                            }
                        } else {
                            focusableWeakCandidateOverlap = focusableWeakCandidateSpanIndex3;
                        }
                    } else if (overlap2 > focusableWeakCandidateSpanIndex3) {
                        focusableWeakCandidateSpanIndex = focusableWeakCandidateSpanIndex2;
                        focusableWeakCandidateOverlap = focusableWeakCandidateSpanIndex3;
                        overlap = 1;
                    } else {
                        if (overlap2 == focusableWeakCandidateSpanIndex3) {
                            focusableWeakCandidateSpanIndex = focusableWeakCandidateSpanIndex2;
                            if (preferLastSpan == (candidateStart > focusableWeakCandidateSpanIndex2)) {
                                overlap = 1;
                                focusableWeakCandidateOverlap = focusableWeakCandidateSpanIndex3;
                            }
                        } else {
                            focusableWeakCandidateSpanIndex = focusableWeakCandidateSpanIndex2;
                        }
                        focusableWeakCandidateOverlap = focusableWeakCandidateSpanIndex3;
                    }
                    overlap = 0;
                } else {
                    overlap = 1;
                    focusableWeakCandidateSpanIndex = focusableWeakCandidateSpanIndex2;
                    focusableWeakCandidateOverlap = focusableWeakCandidateSpanIndex3;
                }
                if (overlap != 0) {
                    if (candidate.hasFocusable()) {
                        focusableWeakCandidate = candidate;
                        focusableWeakCandidateSpanIndex2 = candidateLp.mSpanIndex;
                        focusableWeakCandidateSpanIndex3 = Math.min(candidateEnd, prevSpanEnd) - Math.max(candidateStart, prevSpanStart);
                    } else {
                        int unfocusableWeakCandidateSpanIndex2 = candidateLp.mSpanIndex;
                        unfocusableWeakCandidate = candidate;
                        unfocusableWeakCandidateOverlap = Math.min(candidateEnd, prevSpanEnd) - Math.max(candidateStart, prevSpanStart);
                        focusableWeakCandidateSpanIndex2 = focusableWeakCandidateSpanIndex;
                        unfocusableWeakCandidateSpanIndex = unfocusableWeakCandidateSpanIndex2;
                        focusableWeakCandidateSpanIndex3 = focusableWeakCandidateOverlap;
                    }
                    i += inc;
                    recycler2 = recycler;
                    state2 = state;
                    start = start;
                    prevFocusedChild2 = prevFocusedChild;
                    focusableSpanGroupIndex2 = focusableSpanGroupIndex;
                }
            } else if (focusableWeakCandidate != null) {
                break;
            } else {
                prevFocusedChild = prevFocusedChild2;
                focusableWeakCandidateSpanIndex = focusableWeakCandidateSpanIndex2;
                focusableWeakCandidateOverlap = focusableWeakCandidateSpanIndex3;
                focusableSpanGroupIndex = focusableSpanGroupIndex2;
            }
            focusableWeakCandidateSpanIndex3 = focusableWeakCandidateOverlap;
            focusableWeakCandidateSpanIndex2 = focusableWeakCandidateSpanIndex;
            i += inc;
            recycler2 = recycler;
            state2 = state;
            start = start;
            prevFocusedChild2 = prevFocusedChild;
            focusableSpanGroupIndex2 = focusableSpanGroupIndex;
        }
        return focusableWeakCandidate != null ? focusableWeakCandidate : unfocusableWeakCandidate;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, androidx.recyclerview.widget.LinearLayoutManager
    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && !this.mPendingSpanCountChange;
    }

    public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
        public int getSpanSize(int position) {
            return 1;
        }

        @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
        public int getSpanIndex(int position, int spanCount) {
            return position % spanCount;
        }
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        int mSpanIndex = -1;
        int mSpanSize = 0;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
        }

        public int getSpanIndex() {
            return this.mSpanIndex;
        }

        public int getSpanSize() {
            return this.mSpanSize;
        }
    }
}
