package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConstraintWidgetContainer extends WidgetContainer {
    private static final boolean DEBUG = false;
    static final boolean DEBUG_GRAPH = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final int MAX_ITERATIONS = 8;
    private static final boolean USE_SNAPSHOT = true;
    int mDebugSolverPassCount;
    public boolean mGroupsWrapOptimized;
    private boolean mHeightMeasuredTooSmall;
    ChainHead[] mHorizontalChainsArray;
    int mHorizontalChainsSize;
    public boolean mHorizontalWrapOptimized;
    private boolean mIsRtl;
    private int mOptimizationLevel;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    public boolean mSkipSolver;
    private Snapshot mSnapshot;
    protected LinearSystem mSystem;
    ChainHead[] mVerticalChainsArray;
    int mVerticalChainsSize;
    public boolean mVerticalWrapOptimized;
    public List<ConstraintWidgetGroup> mWidgetGroups;
    private boolean mWidthMeasuredTooSmall;
    public int mWrapFixedHeight;
    public int mWrapFixedWidth;

    public void fillMetrics(Metrics metrics) {
        this.mSystem.fillMetrics(metrics);
    }

    public ConstraintWidgetContainer() {
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mWidgetGroups = new ArrayList();
        this.mGroupsWrapOptimized = false;
        this.mHorizontalWrapOptimized = false;
        this.mVerticalWrapOptimized = false;
        this.mWrapFixedWidth = 0;
        this.mWrapFixedHeight = 0;
        this.mOptimizationLevel = 7;
        this.mSkipSolver = false;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        this.mDebugSolverPassCount = 0;
    }

    public ConstraintWidgetContainer(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mWidgetGroups = new ArrayList();
        this.mGroupsWrapOptimized = false;
        this.mHorizontalWrapOptimized = false;
        this.mVerticalWrapOptimized = false;
        this.mWrapFixedWidth = 0;
        this.mWrapFixedHeight = 0;
        this.mOptimizationLevel = 7;
        this.mSkipSolver = false;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        this.mDebugSolverPassCount = 0;
    }

    public ConstraintWidgetContainer(int width, int height) {
        super(width, height);
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mWidgetGroups = new ArrayList();
        this.mGroupsWrapOptimized = false;
        this.mHorizontalWrapOptimized = false;
        this.mVerticalWrapOptimized = false;
        this.mWrapFixedWidth = 0;
        this.mWrapFixedHeight = 0;
        this.mOptimizationLevel = 7;
        this.mSkipSolver = false;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        this.mDebugSolverPassCount = 0;
    }

    public void setOptimizationLevel(int value) {
        this.mOptimizationLevel = value;
    }

    public int getOptimizationLevel() {
        return this.mOptimizationLevel;
    }

    public boolean optimizeFor(int feature) {
        if ((this.mOptimizationLevel & feature) == feature) {
            return USE_SNAPSHOT;
        }
        return false;
    }

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public String getType() {
        return "ConstraintLayout";
    }

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget, androidx.constraintlayout.solver.widgets.WidgetContainer
    public void reset() {
        this.mSystem.reset();
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        this.mWidgetGroups.clear();
        this.mSkipSolver = false;
        super.reset();
    }

    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }

    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }

    public boolean addChildrenToSolver(LinearSystem system) {
        addToSolver(system);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof ConstraintWidgetContainer) {
                ConstraintWidget.DimensionBehaviour horizontalBehaviour = widget.mListDimensionBehaviors[0];
                ConstraintWidget.DimensionBehaviour verticalBehaviour = widget.mListDimensionBehaviors[1];
                if (horizontalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                if (verticalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                widget.addToSolver(system);
                if (horizontalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setHorizontalDimensionBehaviour(horizontalBehaviour);
                }
                if (verticalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setVerticalDimensionBehaviour(verticalBehaviour);
                }
            } else {
                Optimizer.checkMatchParent(this, system, widget);
                widget.addToSolver(system);
            }
        }
        if (this.mHorizontalChainsSize > 0) {
            Chain.applyChainConstraints(this, system, 0);
        }
        if (this.mVerticalChainsSize > 0) {
            Chain.applyChainConstraints(this, system, 1);
        }
        return USE_SNAPSHOT;
    }

    public void updateChildrenFromSolver(LinearSystem system, boolean[] flags) {
        flags[2] = false;
        updateFromSolver(system);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            widget.updateFromSolver(system);
            if (widget.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.getWidth() < widget.getWrapWidth()) {
                flags[2] = USE_SNAPSHOT;
            }
            if (widget.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.getHeight() < widget.getWrapHeight()) {
                flags[2] = USE_SNAPSHOT;
            }
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
    }

    public void setRtl(boolean isRtl) {
        this.mIsRtl = isRtl;
    }

    public boolean isRtl() {
        return this.mIsRtl;
    }

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public void analyze(int optimizationLevel) {
        super.analyze(optimizationLevel);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ((ConstraintWidget) this.mChildren.get(i)).analyze(optimizationLevel);
        }
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:157:0x0319 */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02af  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02be  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x019a  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01f9  */
    @Override // androidx.constraintlayout.solver.widgets.WidgetContainer
    public void layout() {
        int groupSize;
        boolean needsSolving;
        int groupSize2;
        boolean wrap_override;
        boolean needsSolving2;
        boolean needsSolving3;
        int count;
        boolean z;
        int maxX;
        int width;
        int height;
        int i;
        int needsSolving4;
        boolean z2;
        int i2;
        boolean z3;
        boolean needsSolving5;
        Exception e;
        int prex = this.mX;
        int prey = this.mY;
        int prew = Math.max(0, getWidth());
        int preh = Math.max(0, getHeight());
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            setX(this.mPaddingLeft);
            setY(this.mPaddingTop);
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = 0;
            this.mY = 0;
        }
        int i3 = 32;
        if (this.mOptimizationLevel != 0) {
            if (!optimizeFor(8)) {
                optimizeReset();
            }
            if (!optimizeFor(32)) {
                optimize();
            }
            this.mSystem.graphOptimizer = USE_SNAPSHOT;
        } else {
            this.mSystem.graphOptimizer = false;
        }
        boolean wrap_override2 = false;
        ConstraintWidget.DimensionBehaviour originalVerticalDimensionBehaviour = this.mListDimensionBehaviors[1];
        ConstraintWidget.DimensionBehaviour originalHorizontalDimensionBehaviour = this.mListDimensionBehaviors[0];
        resetChains();
        if (this.mWidgetGroups.size() == 0) {
            this.mWidgetGroups.clear();
            this.mWidgetGroups.add(0, new ConstraintWidgetGroup(this.mChildren));
        }
        int groupSize3 = this.mWidgetGroups.size();
        List<ConstraintWidget> allChildren = this.mChildren;
        boolean hasWrapContent = (getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) ? USE_SNAPSHOT : false;
        int groupIndex = 0;
        while (groupIndex < groupSize3 && !this.mSkipSolver) {
            if (this.mWidgetGroups.get(groupIndex).mSkipSolver) {
                groupSize = groupSize3;
            } else {
                if (optimizeFor(i3)) {
                    if (getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.FIXED && getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.FIXED) {
                        this.mChildren = (ArrayList) this.mWidgetGroups.get(groupIndex).getWidgetsToSolve();
                    } else {
                        this.mChildren = (ArrayList) this.mWidgetGroups.get(groupIndex).mConstrainedGroup;
                    }
                }
                resetChains();
                int count2 = this.mChildren.size();
                int countSolve = 0;
                int i4 = 0;
                while (i4 < count2) {
                    ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i4);
                    if (widget instanceof WidgetContainer) {
                        ((WidgetContainer) widget).layout();
                    }
                    i4++;
                    countSolve = countSolve;
                }
                int countSolve2 = countSolve;
                boolean height2 = true;
                boolean wrap_override3 = wrap_override2;
                boolean needsSolving6 = needsSolving;
                while (height2) {
                    int countSolve3 = countSolve2 + 1;
                    try {
                        this.mSystem.reset();
                        resetChains();
                        createObjectVariables(this.mSystem);
                        int i5 = 0;
                        boolean needsSolving7 = height2;
                        while (i5 < count2) {
                            wrap_override = wrap_override3;
                            try {
                                needsSolving6 = needsSolving7;
                            } catch (Exception e2) {
                                e = e2;
                                needsSolving5 = needsSolving7;
                                e.printStackTrace();
                                PrintStream printStream = System.out;
                                StringBuilder sb = new StringBuilder();
                                groupSize2 = groupSize3;
                                sb.append("EXCEPTION : ");
                                sb.append(e);
                                printStream.println(sb.toString());
                                needsSolving2 = needsSolving5;
                                if (needsSolving2) {
                                }
                                int i6 = 0;
                                if (hasWrapContent) {
                                }
                                i2 = 0;
                                count = count2;
                                z2 = needsSolving3;
                                maxX = i2;
                                wrap_override3 = wrap_override;
                                z = z2;
                                width = Math.max(this.mMinWidth, getWidth());
                                if (width > getWidth()) {
                                }
                                height = Math.max(this.mMinHeight, getHeight());
                                if (height > getHeight()) {
                                }
                                if (!wrap_override3) {
                                }
                                countSolve2 = countSolve3;
                                groupSize3 = groupSize2;
                                count2 = count;
                                needsSolving6 = z;
                            }
                            try {
                                ((ConstraintWidget) this.mChildren.get(i5)).createObjectVariables(this.mSystem);
                                i5++;
                                wrap_override3 = wrap_override;
                                needsSolving7 = needsSolving6;
                            } catch (Exception e3) {
                                e = e3;
                                needsSolving5 = needsSolving6;
                                e.printStackTrace();
                                PrintStream printStream2 = System.out;
                                StringBuilder sb2 = new StringBuilder();
                                groupSize2 = groupSize3;
                                sb2.append("EXCEPTION : ");
                                sb2.append(e);
                                printStream2.println(sb2.toString());
                                needsSolving2 = needsSolving5;
                                if (needsSolving2) {
                                }
                                int i62 = 0;
                                if (hasWrapContent) {
                                }
                                i2 = 0;
                                count = count2;
                                z2 = needsSolving3;
                                maxX = i2;
                                wrap_override3 = wrap_override;
                                z = z2;
                                width = Math.max(this.mMinWidth, getWidth());
                                if (width > getWidth()) {
                                }
                                height = Math.max(this.mMinHeight, getHeight());
                                if (height > getHeight()) {
                                }
                                if (!wrap_override3) {
                                }
                                countSolve2 = countSolve3;
                                groupSize3 = groupSize2;
                                count2 = count;
                                needsSolving6 = z;
                            }
                        }
                        wrap_override = wrap_override3;
                        boolean needsSolving8 = addChildrenToSolver(this.mSystem);
                        if (needsSolving8) {
                            try {
                                this.mSystem.minimize();
                            } catch (Exception e4) {
                                e = e4;
                                needsSolving5 = needsSolving8;
                            }
                        }
                        groupSize2 = groupSize3;
                        needsSolving2 = needsSolving8;
                    } catch (Exception e5) {
                        e = e5;
                        wrap_override = wrap_override3;
                        needsSolving5 = height2;
                        e.printStackTrace();
                        PrintStream printStream22 = System.out;
                        StringBuilder sb22 = new StringBuilder();
                        groupSize2 = groupSize3;
                        sb22.append("EXCEPTION : ");
                        sb22.append(e);
                        printStream22.println(sb22.toString());
                        needsSolving2 = needsSolving5;
                        if (needsSolving2) {
                        }
                        int i622 = 0;
                        if (hasWrapContent) {
                        }
                        i2 = 0;
                        count = count2;
                        z2 = needsSolving3;
                        maxX = i2;
                        wrap_override3 = wrap_override;
                        z = z2;
                        width = Math.max(this.mMinWidth, getWidth());
                        if (width > getWidth()) {
                        }
                        height = Math.max(this.mMinHeight, getHeight());
                        if (height > getHeight()) {
                        }
                        if (!wrap_override3) {
                        }
                        countSolve2 = countSolve3;
                        groupSize3 = groupSize2;
                        count2 = count;
                        needsSolving6 = z;
                    }
                    if (needsSolving2) {
                        updateFromSolver(this.mSystem);
                        int i7 = 0;
                        while (true) {
                            if (i7 >= count2) {
                                needsSolving3 = needsSolving2;
                                break;
                            }
                            ConstraintWidget widget2 = (ConstraintWidget) this.mChildren.get(i7);
                            boolean needsSolving9 = needsSolving2;
                            if (widget2.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                                z3 = USE_SNAPSHOT;
                            } else if (widget2.getWidth() < widget2.getWrapWidth()) {
                                Optimizer.flags[2] = USE_SNAPSHOT;
                                needsSolving3 = needsSolving9;
                                break;
                            } else {
                                z3 = USE_SNAPSHOT;
                            }
                            ConstraintWidget.DimensionBehaviour[] dimensionBehaviourArr = widget2.mListDimensionBehaviors;
                            char c = z3 ? 1 : 0;
                            char c2 = z3 ? 1 : 0;
                            char c3 = z3 ? 1 : 0;
                            if (dimensionBehaviourArr[c] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget2.getHeight() < widget2.getWrapHeight()) {
                                Optimizer.flags[2] = z3;
                                needsSolving3 = needsSolving9;
                                break;
                            }
                            i7++;
                            needsSolving2 = needsSolving9;
                        }
                    } else {
                        updateChildrenFromSolver(this.mSystem, Optimizer.flags);
                        needsSolving3 = needsSolving2;
                    }
                    int i6222 = 0;
                    if (hasWrapContent || countSolve3 >= 8 || !Optimizer.flags[2]) {
                        i2 = 0;
                        count = count2;
                        z2 = needsSolving3;
                    } else {
                        int maxX2 = 0;
                        int maxY = 0;
                        int i8 = 0;
                        int i9 = needsSolving3;
                        while (i8 < count2) {
                            ConstraintWidget widget3 = (ConstraintWidget) this.mChildren.get(i8);
                            maxX2 = Math.max(maxX2, widget3.mX + widget3.getWidth());
                            int i10 = widget3.mY;
                            int height3 = widget3.getHeight();
                            maxY = Math.max(maxY, i10 + height3);
                            i8++;
                            i6222 = i6222;
                            count2 = count2;
                            i9 = height3;
                        }
                        i2 = i6222;
                        count = count2;
                        int maxX3 = Math.max(this.mMinWidth, maxX2);
                        int maxY2 = Math.max(this.mMinHeight, maxY);
                        if (originalHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && getWidth() < maxX3) {
                            setWidth(maxX3);
                            this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                            wrap_override = true;
                            i2 = 1;
                        }
                        z2 = i9;
                        if (originalVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                            z2 = i9;
                            if (getHeight() < maxY2) {
                                setHeight(maxY2);
                                this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                                wrap_override3 = true;
                                maxX = 1;
                                z = i9;
                                width = Math.max(this.mMinWidth, getWidth());
                                if (width > getWidth()) {
                                    setWidth(width);
                                    this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                    wrap_override3 = USE_SNAPSHOT;
                                    maxX = 1;
                                }
                                height = Math.max(this.mMinHeight, getHeight());
                                if (height > getHeight()) {
                                    setHeight(height);
                                    this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                                    wrap_override3 = USE_SNAPSHOT;
                                    maxX = 1;
                                }
                                if (!wrap_override3) {
                                    if (this.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || prew <= 0 || getWidth() <= prew) {
                                        i = maxX;
                                    } else {
                                        this.mWidthMeasuredTooSmall = USE_SNAPSHOT;
                                        wrap_override3 = USE_SNAPSHOT;
                                        this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                        setWidth(prew);
                                        i = 1;
                                    }
                                    if (this.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || preh <= 0) {
                                        needsSolving4 = i;
                                    } else if (getHeight() > preh) {
                                        this.mHeightMeasuredTooSmall = USE_SNAPSHOT;
                                        this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                                        setHeight(preh);
                                        height2 = true;
                                        wrap_override3 = true;
                                    } else {
                                        needsSolving4 = i;
                                    }
                                    wrap_override3 = wrap_override3;
                                    height2 = needsSolving4;
                                } else {
                                    height2 = maxX;
                                }
                                countSolve2 = countSolve3;
                                groupSize3 = groupSize2;
                                count2 = count;
                                needsSolving6 = z;
                            }
                        }
                    }
                    maxX = i2;
                    wrap_override3 = wrap_override;
                    z = z2;
                    width = Math.max(this.mMinWidth, getWidth());
                    if (width > getWidth()) {
                    }
                    height = Math.max(this.mMinHeight, getHeight());
                    if (height > getHeight()) {
                    }
                    if (!wrap_override3) {
                    }
                    countSolve2 = countSolve3;
                    groupSize3 = groupSize2;
                    count2 = count;
                    needsSolving6 = z;
                }
                needsSolving = height2;
                groupSize = groupSize3;
                this.mWidgetGroups.get(groupIndex).updateUnresolvedWidgets();
                wrap_override2 = wrap_override3;
            }
            groupIndex++;
            groupSize3 = groupSize;
            i3 = 32;
        }
        this.mChildren = (ArrayList) allChildren;
        if (this.mParent != null) {
            int width2 = Math.max(this.mMinWidth, getWidth());
            int height4 = Math.max(this.mMinHeight, getHeight());
            this.mSnapshot.applyTo(this);
            setWidth(this.mPaddingLeft + width2 + this.mPaddingRight);
            setHeight(this.mPaddingTop + height4 + this.mPaddingBottom);
        } else {
            this.mX = prex;
            this.mY = prey;
        }
        if (wrap_override2) {
            this.mListDimensionBehaviors[0] = originalHorizontalDimensionBehaviour;
            this.mListDimensionBehaviors[1] = originalVerticalDimensionBehaviour;
        }
        resetSolverVariables(this.mSystem.getCache());
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    public void preOptimize() {
        optimizeReset();
        analyze(this.mOptimizationLevel);
    }

    public void solveGraph() {
        ResolutionAnchor leftNode = getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
        ResolutionAnchor topNode = getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
        leftNode.resolve(null, 0.0f);
        topNode.resolve(null, 0.0f);
    }

    public void resetGraph() {
        ResolutionAnchor leftNode = getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
        ResolutionAnchor topNode = getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
        leftNode.invalidateAnchors();
        topNode.invalidateAnchors();
        leftNode.resolve(null, 0.0f);
        topNode.resolve(null, 0.0f);
    }

    public void optimizeForDimensions(int width, int height) {
        if (!(this.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || this.mResolutionWidth == null)) {
            this.mResolutionWidth.resolve(width);
        }
        if (this.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && this.mResolutionHeight != null) {
            this.mResolutionHeight.resolve(height);
        }
    }

    public void optimizeReset() {
        int count = this.mChildren.size();
        resetResolutionNodes();
        for (int i = 0; i < count; i++) {
            ((ConstraintWidget) this.mChildren.get(i)).resetResolutionNodes();
        }
    }

    public void optimize() {
        if (!optimizeFor(8)) {
            analyze(this.mOptimizationLevel);
        }
        solveGraph();
    }

    public boolean handlesInternalConstraints() {
        return false;
    }

    public ArrayList<Guideline> getVerticalGuidelines() {
        ArrayList<Guideline> guidelines = new ArrayList<>();
        int mChildrenSize = this.mChildren.size();
        for (int i = 0; i < mChildrenSize; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 1) {
                    guidelines.add(guideline);
                }
            }
        }
        return guidelines;
    }

    public ArrayList<Guideline> getHorizontalGuidelines() {
        ArrayList<Guideline> guidelines = new ArrayList<>();
        int mChildrenSize = this.mChildren.size();
        for (int i = 0; i < mChildrenSize; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 0) {
                    guidelines.add(guideline);
                }
            }
        }
        return guidelines;
    }

    public LinearSystem getSystem() {
        return this.mSystem;
    }

    private void resetChains() {
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
    }

    /* access modifiers changed from: package-private */
    public void addChain(ConstraintWidget constraintWidget, int type) {
        if (type == 0) {
            addHorizontalChain(constraintWidget);
        } else if (type == 1) {
            addVerticalChain(constraintWidget);
        }
    }

    private void addHorizontalChain(ConstraintWidget widget) {
        int i = this.mHorizontalChainsSize + 1;
        ChainHead[] chainHeadArr = this.mHorizontalChainsArray;
        if (i >= chainHeadArr.length) {
            this.mHorizontalChainsArray = (ChainHead[]) Arrays.copyOf(chainHeadArr, chainHeadArr.length * 2);
        }
        this.mHorizontalChainsArray[this.mHorizontalChainsSize] = new ChainHead(widget, 0, isRtl());
        this.mHorizontalChainsSize++;
    }

    private void addVerticalChain(ConstraintWidget widget) {
        int i = this.mVerticalChainsSize + 1;
        ChainHead[] chainHeadArr = this.mVerticalChainsArray;
        if (i >= chainHeadArr.length) {
            this.mVerticalChainsArray = (ChainHead[]) Arrays.copyOf(chainHeadArr, chainHeadArr.length * 2);
        }
        this.mVerticalChainsArray[this.mVerticalChainsSize] = new ChainHead(widget, 1, isRtl());
        this.mVerticalChainsSize++;
    }

    public List<ConstraintWidgetGroup> getWidgetGroups() {
        return this.mWidgetGroups;
    }
}
