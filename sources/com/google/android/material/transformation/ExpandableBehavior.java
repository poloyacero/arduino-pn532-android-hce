package com.google.android.material.transformation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import com.google.android.material.expandable.ExpandableWidget;
import java.util.List;

public abstract class ExpandableBehavior extends CoordinatorLayout.Behavior<View> {
    private static final int STATE_COLLAPSED = 2;
    private static final int STATE_EXPANDED = 1;
    private static final int STATE_UNINITIALIZED = 0;
    private int currentState = 0;

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public abstract boolean layoutDependsOn(CoordinatorLayout coordinatorLayout, View view, View view2);

    /* access modifiers changed from: protected */
    public abstract boolean onExpandedStateChange(View view, View view2, boolean z, boolean z2);

    public ExpandableBehavior() {
    }

    public ExpandableBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = findExpandableWidget(r5, r6);
     */
    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onLayoutChild(CoordinatorLayout parent, final View child, int layoutDirection) {
        final ExpandableWidget dep;
        if (ViewCompat.isLaidOut(child) || dep == null || !didStateChange(dep.isExpanded())) {
            return false;
        }
        this.currentState = dep.isExpanded() ? 1 : 2;
        final int expectedState = this.currentState;
        child.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            /* class com.google.android.material.transformation.ExpandableBehavior.AnonymousClass1 */

            public boolean onPreDraw() {
                child.getViewTreeObserver().removeOnPreDrawListener(this);
                if (ExpandableBehavior.this.currentState == expectedState) {
                    ExpandableBehavior expandableBehavior = ExpandableBehavior.this;
                    ExpandableWidget expandableWidget = dep;
                    expandableBehavior.onExpandedStateChange((View) expandableWidget, child, expandableWidget.isExpanded(), false);
                }
                return false;
            }
        });
        return false;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        ExpandableWidget dep = (ExpandableWidget) dependency;
        if (!didStateChange(dep.isExpanded())) {
            return false;
        }
        this.currentState = dep.isExpanded() ? 1 : 2;
        return onExpandedStateChange((View) dep, child, dep.isExpanded(), true);
    }

    /* access modifiers changed from: protected */
    public ExpandableWidget findExpandableWidget(CoordinatorLayout parent, View child) {
        List<View> dependencies = parent.getDependencies(child);
        int size = dependencies.size();
        for (int i = 0; i < size; i++) {
            View dependency = dependencies.get(i);
            if (layoutDependsOn(parent, child, dependency)) {
                return (ExpandableWidget) dependency;
            }
        }
        return null;
    }

    private boolean didStateChange(boolean expanded) {
        if (expanded) {
            int i = this.currentState;
            if (i == 0 || i == 2) {
                return true;
            }
            return false;
        } else if (this.currentState == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static <T extends ExpandableBehavior> T from(View view, Class<T> klass) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.Behavior<?> behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
            if (behavior instanceof ExpandableBehavior) {
                return klass.cast(behavior);
            }
            throw new IllegalArgumentException("The view is not associated with ExpandableBehavior");
        }
        throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
    }
}
