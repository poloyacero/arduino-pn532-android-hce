package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuPresenter;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.appcompat.widget.LinearLayoutCompat;

public class ActionMenuView extends LinearLayoutCompat implements MenuBuilder.ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBaselineAligned(false);
        float density = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * density);
        this.mGeneratedItemPadding = (int) (4.0f * density);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    public void setPopupTheme(int resId) {
        if (this.mPopupTheme != resId) {
            this.mPopupTheme = resId;
            if (resId == 0) {
                this.mPopupContext = getContext();
            } else {
                this.mPopupContext = new ContextThemeWrapper(getContext(), resId);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    public void setPresenter(ActionMenuPresenter presenter) {
        this.mPresenter = presenter;
        presenter.setMenuView(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.LinearLayoutCompat
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        MenuBuilder menuBuilder;
        boolean wasFormatted = this.mFormatItems;
        boolean z = View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824;
        this.mFormatItems = z;
        if (wasFormatted != z) {
            this.mFormatItemsWidth = 0;
        }
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        if (!(!this.mFormatItems || (menuBuilder = this.mMenu) == null || widthSize == this.mFormatItemsWidth)) {
            this.mFormatItemsWidth = widthSize;
            menuBuilder.onItemsChanged(true);
        }
        int childCount = getChildCount();
        if (!this.mFormatItems || childCount <= 0) {
            for (int i = 0; i < childCount; i++) {
                LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
                lp.rightMargin = 0;
                lp.leftMargin = 0;
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        onMeasureExactFormat(widthMeasureSpec, heightMeasureSpec);
    }

    /* JADX WARN: Type inference failed for: r3v11, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r3v28 */
    /* JADX WARN: Type inference failed for: r3v29 */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0287  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02b5  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02bd  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x02bf  */
    /* JADX WARNING: Unknown variable types count: 1 */
    private void onMeasureExactFormat(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize;
        int heightMode;
        boolean needsExpansion;
        boolean needsExpansion2;
        int heightSize;
        int visibleItemCount;
        int visibleItemCount2;
        ?? r3;
        int heightMode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize2 = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int itemHeightSpec = getChildMeasureSpec(heightMeasureSpec, heightPadding, -2);
        int widthSize3 = widthSize2 - widthPadding;
        int i = this.mMinCellSize;
        int cellCount = widthSize3 / i;
        int cellSizeRemaining = widthSize3 % i;
        if (cellCount == 0) {
            setMeasuredDimension(widthSize3, 0);
            return;
        }
        int cellSize = i + (cellSizeRemaining / cellCount);
        int cellsRemaining = cellCount;
        int maxChildHeight = 0;
        int maxCellsUsed = 0;
        int expandableItemCount = 0;
        boolean hasOverflow = false;
        long smallestItemsAt = 0;
        int childCount = getChildCount();
        int maxChildHeight2 = 0;
        int i2 = 0;
        while (i2 < childCount) {
            View child = getChildAt(i2);
            if (child.getVisibility() != 8) {
                boolean isGeneratedItem = child instanceof ActionMenuItemView;
                int visibleItemCount3 = maxChildHeight2 + 1;
                if (isGeneratedItem) {
                    int i3 = this.mGeneratedItemPadding;
                    visibleItemCount2 = visibleItemCount3;
                    r3 = 0;
                    child.setPadding(i3, 0, i3, 0);
                } else {
                    visibleItemCount2 = visibleItemCount3;
                    r3 = 0;
                }
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.expanded = r3;
                int i4 = r3 == true ? 1 : 0;
                int i5 = r3 == true ? 1 : 0;
                int i6 = r3 == true ? 1 : 0;
                lp.extraPixels = i4;
                lp.cellsUsed = r3;
                lp.expandable = r3;
                lp.leftMargin = r3;
                lp.rightMargin = r3;
                lp.preventEdgeOffset = isGeneratedItem && ((ActionMenuItemView) child).hasText();
                int cellsUsed = measureChildForCells(child, cellSize, lp.isOverflowButton ? 1 : cellsRemaining, itemHeightSpec, heightPadding);
                maxCellsUsed = Math.max(maxCellsUsed, cellsUsed);
                if (lp.expandable) {
                    expandableItemCount++;
                }
                if (lp.isOverflowButton) {
                    hasOverflow = true;
                }
                cellsRemaining -= cellsUsed;
                int maxChildHeight3 = Math.max(maxChildHeight, child.getMeasuredHeight());
                if (cellsUsed == 1) {
                    maxChildHeight = maxChildHeight3;
                    smallestItemsAt |= (long) (1 << i2);
                    maxChildHeight2 = visibleItemCount2;
                } else {
                    maxChildHeight = maxChildHeight3;
                    maxChildHeight2 = visibleItemCount2;
                }
            }
            i2++;
            cellCount = cellCount;
            cellSizeRemaining = cellSizeRemaining;
        }
        boolean centerSingleExpandedItem = hasOverflow && maxChildHeight2 == 2;
        boolean needsExpansion3 = false;
        while (true) {
            if (expandableItemCount <= 0 || cellsRemaining <= 0) {
                heightMode = heightMode2;
                widthSize = widthSize3;
                needsExpansion = needsExpansion3;
            } else {
                int minCells = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                long minCellsAt = 0;
                int minCellsItemCount = 0;
                int i7 = 0;
                while (i7 < childCount) {
                    LayoutParams lp2 = (LayoutParams) getChildAt(i7).getLayoutParams();
                    if (lp2.expandable) {
                        if (lp2.cellsUsed < minCells) {
                            minCells = lp2.cellsUsed;
                            minCellsAt = 1 << i7;
                            minCellsItemCount = 1;
                        } else if (lp2.cellsUsed == minCells) {
                            minCellsAt |= 1 << i7;
                            minCellsItemCount++;
                        }
                    }
                    i7++;
                    expandableItemCount = expandableItemCount;
                    needsExpansion3 = needsExpansion3;
                }
                needsExpansion = needsExpansion3;
                smallestItemsAt |= minCellsAt;
                if (minCellsItemCount > cellsRemaining) {
                    heightMode = heightMode2;
                    widthSize = widthSize3;
                    break;
                }
                int minCells2 = minCells + 1;
                int i8 = 0;
                while (i8 < childCount) {
                    View child2 = getChildAt(i8);
                    LayoutParams lp3 = (LayoutParams) child2.getLayoutParams();
                    if ((minCellsAt & ((long) (1 << i8))) != 0) {
                        if (centerSingleExpandedItem && lp3.preventEdgeOffset && cellsRemaining == 1) {
                            int i9 = this.mGeneratedItemPadding;
                            child2.setPadding(i9 + cellSize, 0, i9, 0);
                        }
                        lp3.cellsUsed++;
                        lp3.expanded = true;
                        cellsRemaining--;
                    } else if (lp3.cellsUsed == minCells2) {
                        smallestItemsAt |= (long) (1 << i8);
                    }
                    i8++;
                    minCellsItemCount = minCellsItemCount;
                    heightMode2 = heightMode2;
                    widthSize3 = widthSize3;
                }
                needsExpansion3 = true;
                heightPadding = heightPadding;
                expandableItemCount = expandableItemCount;
            }
        }
        heightMode = heightMode2;
        widthSize = widthSize3;
        needsExpansion = needsExpansion3;
        boolean singleItem = !hasOverflow && maxChildHeight2 == 1;
        if (cellsRemaining > 0 && smallestItemsAt != 0) {
            if (cellsRemaining < maxChildHeight2 - 1 || singleItem || maxCellsUsed > 1) {
                float expandCount = (float) Long.bitCount(smallestItemsAt);
                if (!singleItem) {
                    if ((smallestItemsAt & 1) != 0) {
                        if (!((LayoutParams) getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                            expandCount -= 0.5f;
                        }
                    }
                    if ((smallestItemsAt & ((long) (1 << (childCount - 1)))) != 0 && !((LayoutParams) getChildAt(childCount - 1).getLayoutParams()).preventEdgeOffset) {
                        expandCount -= 0.5f;
                    }
                }
                int extraPixels = expandCount > 0.0f ? (int) (((float) (cellsRemaining * cellSize)) / expandCount) : 0;
                int i10 = 0;
                needsExpansion2 = needsExpansion;
                while (i10 < childCount) {
                    if ((smallestItemsAt & ((long) (1 << i10))) != 0) {
                        View child3 = getChildAt(i10);
                        LayoutParams lp4 = (LayoutParams) child3.getLayoutParams();
                        if (child3 instanceof ActionMenuItemView) {
                            lp4.extraPixels = extraPixels;
                            lp4.expanded = true;
                            if (i10 == 0 && !lp4.preventEdgeOffset) {
                                lp4.leftMargin = (-extraPixels) / 2;
                            }
                            needsExpansion2 = true;
                        } else if (lp4.isOverflowButton) {
                            lp4.extraPixels = extraPixels;
                            lp4.expanded = true;
                            lp4.rightMargin = (-extraPixels) / 2;
                            needsExpansion2 = true;
                        } else {
                            if (i10 != 0) {
                                lp4.leftMargin = extraPixels / 2;
                            }
                            if (i10 != childCount - 1) {
                                lp4.rightMargin = extraPixels / 2;
                            }
                        }
                    }
                    i10++;
                    singleItem = singleItem;
                    expandCount = expandCount;
                }
                if (!needsExpansion2) {
                    int i11 = 0;
                    while (i11 < childCount) {
                        View child4 = getChildAt(i11);
                        LayoutParams lp5 = (LayoutParams) child4.getLayoutParams();
                        if (!lp5.expanded) {
                            visibleItemCount = maxChildHeight2;
                        } else {
                            visibleItemCount = maxChildHeight2;
                            child4.measure(View.MeasureSpec.makeMeasureSpec((lp5.cellsUsed * cellSize) + lp5.extraPixels, 1073741824), itemHeightSpec);
                        }
                        i11++;
                        maxChildHeight2 = visibleItemCount;
                    }
                }
                if (heightMode == 1073741824) {
                    heightSize = maxChildHeight;
                } else {
                    heightSize = heightSize2;
                }
                setMeasuredDimension(widthSize, heightSize);
            }
        }
        needsExpansion2 = needsExpansion;
        if (!needsExpansion2) {
        }
        if (heightMode == 1073741824) {
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    static int measureChildForCells(View child, int cellSize, int cellsRemaining, int parentHeightMeasureSpec, int parentHeightPadding) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int childHeightSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(parentHeightMeasureSpec) - parentHeightPadding, View.MeasureSpec.getMode(parentHeightMeasureSpec));
        ActionMenuItemView itemView = child instanceof ActionMenuItemView ? (ActionMenuItemView) child : null;
        boolean expandable = false;
        boolean hasText = itemView != null && itemView.hasText();
        int cellsUsed = 0;
        if (cellsRemaining > 0 && (!hasText || cellsRemaining >= 2)) {
            child.measure(View.MeasureSpec.makeMeasureSpec(cellSize * cellsRemaining, Integer.MIN_VALUE), childHeightSpec);
            int measuredWidth = child.getMeasuredWidth();
            cellsUsed = measuredWidth / cellSize;
            if (measuredWidth % cellSize != 0) {
                cellsUsed++;
            }
            if (hasText && cellsUsed < 2) {
                cellsUsed = 2;
            }
        }
        if (!lp.isOverflowButton && hasText) {
            expandable = true;
        }
        lp.expandable = expandable;
        lp.cellsUsed = cellsUsed;
        child.measure(View.MeasureSpec.makeMeasureSpec(cellsUsed * cellSize, 1073741824), childHeightSpec);
        return cellsUsed;
    }

    /* JADX INFO: Multiple debug info for r9v10 int: [D('t' int), D('isLayoutRtl' boolean)] */
    /* JADX INFO: Multiple debug info for r2v10 int: [D('midVertical' int), D('b' int)] */
    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.LinearLayoutCompat
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int i;
        int overflowWidth;
        int dividerWidth;
        boolean isLayoutRtl;
        int midVertical;
        int r;
        int l;
        ActionMenuView actionMenuView = this;
        if (!actionMenuView.mFormatItems) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        int childCount = getChildCount();
        int midVertical2 = (bottom - top) / 2;
        int dividerWidth2 = getDividerWidth();
        int overflowWidth2 = 0;
        int nonOverflowWidth = 0;
        int nonOverflowCount = 0;
        int widthRemaining = ((right - left) - getPaddingRight()) - getPaddingLeft();
        int i2 = 0;
        boolean isLayoutRtl2 = ViewUtils.isLayoutRtl(this);
        int i3 = 0;
        while (true) {
            i = 8;
            if (i3 >= childCount) {
                break;
            }
            View v = actionMenuView.getChildAt(i3);
            if (v.getVisibility() == 8) {
                midVertical = midVertical2;
                isLayoutRtl = isLayoutRtl2;
            } else {
                LayoutParams p = (LayoutParams) v.getLayoutParams();
                if (p.isOverflowButton) {
                    overflowWidth2 = v.getMeasuredWidth();
                    if (actionMenuView.hasSupportDividerBeforeChildAt(i3)) {
                        overflowWidth2 += dividerWidth2;
                    }
                    int height = v.getMeasuredHeight();
                    if (isLayoutRtl2) {
                        l = getPaddingLeft() + p.leftMargin;
                        r = l + overflowWidth2;
                    } else {
                        r = (getWidth() - getPaddingRight()) - p.rightMargin;
                        l = r - overflowWidth2;
                    }
                    isLayoutRtl = isLayoutRtl2;
                    int t = midVertical2 - (height / 2);
                    midVertical = midVertical2;
                    v.layout(l, t, r, t + height);
                    widthRemaining -= overflowWidth2;
                    i2 = 1;
                } else {
                    midVertical = midVertical2;
                    isLayoutRtl = isLayoutRtl2;
                    int size = v.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                    nonOverflowWidth += size;
                    widthRemaining -= size;
                    if (actionMenuView.hasSupportDividerBeforeChildAt(i3)) {
                        nonOverflowWidth += dividerWidth2;
                    }
                    nonOverflowCount++;
                }
            }
            i3++;
            midVertical2 = midVertical;
            isLayoutRtl2 = isLayoutRtl;
        }
        if (childCount == 1 && i2 == 0) {
            View v2 = actionMenuView.getChildAt(0);
            int width = v2.getMeasuredWidth();
            int height2 = v2.getMeasuredHeight();
            int l2 = ((right - left) / 2) - (width / 2);
            int t2 = midVertical2 - (height2 / 2);
            v2.layout(l2, t2, l2 + width, t2 + height2);
            return;
        }
        int spacerCount = nonOverflowCount - (i2 ^ 1);
        int spacerSize = Math.max(0, spacerCount > 0 ? widthRemaining / spacerCount : 0);
        if (isLayoutRtl2) {
            int startRight = getWidth() - getPaddingRight();
            int i4 = 0;
            while (i4 < childCount) {
                View v3 = actionMenuView.getChildAt(i4);
                LayoutParams lp = (LayoutParams) v3.getLayoutParams();
                if (v3.getVisibility() == i) {
                    dividerWidth = dividerWidth2;
                    overflowWidth = overflowWidth2;
                } else if (lp.isOverflowButton) {
                    dividerWidth = dividerWidth2;
                    overflowWidth = overflowWidth2;
                } else {
                    int startRight2 = startRight - lp.rightMargin;
                    int width2 = v3.getMeasuredWidth();
                    int height3 = v3.getMeasuredHeight();
                    int t3 = midVertical2 - (height3 / 2);
                    dividerWidth = dividerWidth2;
                    overflowWidth = overflowWidth2;
                    v3.layout(startRight2 - width2, t3, startRight2, t3 + height3);
                    startRight = startRight2 - ((lp.leftMargin + width2) + spacerSize);
                }
                i4++;
                dividerWidth2 = dividerWidth;
                overflowWidth2 = overflowWidth;
                i = 8;
            }
            return;
        }
        int startLeft = getPaddingLeft();
        int i5 = 0;
        while (i5 < childCount) {
            View v4 = actionMenuView.getChildAt(i5);
            LayoutParams lp2 = (LayoutParams) v4.getLayoutParams();
            if (v4.getVisibility() != 8 && !lp2.isOverflowButton) {
                int startLeft2 = startLeft + lp2.leftMargin;
                int width3 = v4.getMeasuredWidth();
                int height4 = v4.getMeasuredHeight();
                int t4 = midVertical2 - (height4 / 2);
                v4.layout(startLeft2, t4, startLeft2 + width3, t4 + height4);
                startLeft = startLeft2 + lp2.rightMargin + width3 + spacerSize;
            }
            i5++;
            actionMenuView = this;
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    public void setOverflowIcon(Drawable icon) {
        getMenu();
        this.mPresenter.setOverflowIcon(icon);
    }

    public Drawable getOverflowIcon() {
        getMenu();
        return this.mPresenter.getOverflowIcon();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    public void setOverflowReserved(boolean reserveOverflow) {
        this.mReserveOverflow = reserveOverflow;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.LinearLayoutCompat, androidx.appcompat.widget.LinearLayoutCompat
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(-2, -2);
        params.gravity = 16;
        return params;
    }

    @Override // android.view.ViewGroup, androidx.appcompat.widget.LinearLayoutCompat, androidx.appcompat.widget.LinearLayoutCompat
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    @Override // android.view.ViewGroup, androidx.appcompat.widget.LinearLayoutCompat, androidx.appcompat.widget.LinearLayoutCompat
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p == null) {
            return generateDefaultLayoutParams();
        }
        LayoutParams result = p instanceof LayoutParams ? new LayoutParams((LayoutParams) p) : new LayoutParams(p);
        if (result.gravity <= 0) {
            result.gravity = 16;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.widget.LinearLayoutCompat
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && (p instanceof LayoutParams);
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams result = generateDefaultLayoutParams();
        result.isOverflowButton = true;
        return result;
    }

    @Override // androidx.appcompat.view.menu.MenuBuilder.ItemInvoker
    public boolean invokeItem(MenuItemImpl item) {
        return this.mMenu.performItemAction(item, 0);
    }

    @Override // androidx.appcompat.view.menu.MenuView
    public int getWindowAnimations() {
        return 0;
    }

    @Override // androidx.appcompat.view.menu.MenuView
    public void initialize(MenuBuilder menu) {
        this.mMenu = menu;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            MenuBuilder menuBuilder = new MenuBuilder(context);
            this.mMenu = menuBuilder;
            menuBuilder.setCallback(new MenuBuilderCallback());
            ActionMenuPresenter actionMenuPresenter = new ActionMenuPresenter(context);
            this.mPresenter = actionMenuPresenter;
            actionMenuPresenter.setReserveOverflow(true);
            ActionMenuPresenter actionMenuPresenter2 = this.mPresenter;
            MenuPresenter.Callback callback = this.mActionMenuPresenterCallback;
            if (callback == null) {
                callback = new ActionMenuPresenterCallback();
            }
            actionMenuPresenter2.setCallback(callback);
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    public void setMenuCallbacks(MenuPresenter.Callback pcb, MenuBuilder.Callback mcb) {
        this.mActionMenuPresenterCallback = pcb;
        this.mMenuBuilderCallback = mcb;
    }

    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public boolean showOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.hideOverflowMenu();
    }

    public boolean isOverflowMenuShowing() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowMenuShowPending() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowPending();
    }

    public void dismissPopupMenus() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.dismissPopupMenus();
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasSupportDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return false;
        }
        View childBefore = getChildAt(childIndex - 1);
        View child = getChildAt(childIndex);
        boolean result = false;
        if (childIndex < getChildCount() && (childBefore instanceof ActionMenuChildView)) {
            result = false | ((ActionMenuChildView) childBefore).needsDividerAfter();
        }
        if (childIndex <= 0 || !(child instanceof ActionMenuChildView)) {
            return result;
        }
        return result | ((ActionMenuChildView) child).needsDividerBefore();
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    public void setExpandedActionViewsExclusive(boolean exclusive) {
        this.mPresenter.setExpandedActionViewsExclusive(exclusive);
    }

    /* access modifiers changed from: private */
    public class MenuBuilderCallback implements MenuBuilder.Callback {
        MenuBuilderCallback() {
        }

        @Override // androidx.appcompat.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(item);
        }

        @Override // androidx.appcompat.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menu) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menu);
            }
        }
    }

    /* access modifiers changed from: private */
    public static class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        ActionMenuPresenterCallback() {
        }

        @Override // androidx.appcompat.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        @Override // androidx.appcompat.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            return false;
        }
    }

    public static class LayoutParams extends LinearLayoutCompat.LayoutParams {
        @ViewDebug.ExportedProperty
        public int cellsUsed;
        @ViewDebug.ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ViewDebug.ExportedProperty
        public int extraPixels;
        @ViewDebug.ExportedProperty
        public boolean isOverflowButton;
        @ViewDebug.ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super((ViewGroup.LayoutParams) other);
            this.isOverflowButton = other.isOverflowButton;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.isOverflowButton = false;
        }

        LayoutParams(int width, int height, boolean isOverflowButton2) {
            super(width, height);
            this.isOverflowButton = isOverflowButton2;
        }
    }
}
