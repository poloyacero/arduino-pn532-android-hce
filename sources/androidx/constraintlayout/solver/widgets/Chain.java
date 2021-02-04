package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.ArrayRow;
import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.SolverVariable;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import java.util.ArrayList;

/* access modifiers changed from: package-private */
public class Chain {
    private static final boolean DEBUG = false;

    Chain() {
    }

    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem system, int orientation) {
        ChainHead[] chainsArray;
        int chainsSize;
        int offset;
        if (orientation == 0) {
            offset = 0;
            chainsSize = constraintWidgetContainer.mHorizontalChainsSize;
            chainsArray = constraintWidgetContainer.mHorizontalChainsArray;
        } else {
            offset = 2;
            chainsSize = constraintWidgetContainer.mVerticalChainsSize;
            chainsArray = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i = 0; i < chainsSize; i++) {
            ChainHead first = chainsArray[i];
            first.define();
            if (!constraintWidgetContainer.optimizeFor(4)) {
                applyChainConstraints(constraintWidgetContainer, system, orientation, offset, first);
            } else if (!Optimizer.applyChainOptimized(constraintWidgetContainer, system, orientation, offset, first)) {
                applyChainConstraints(constraintWidgetContainer, system, orientation, offset, first);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:286:0x0636 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0654  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x0659  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x065c  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0674  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0680  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0683 A[ADDED_TO_REGION] */
    static void applyChainConstraints(ConstraintWidgetContainer container, LinearSystem system, int orientation, int offset, ChainHead chainHead) {
        boolean isChainPacked;
        boolean isChainSpread;
        boolean done;
        boolean isChainSpreadInside;
        ConstraintWidget widget;
        ConstraintWidget previousMatchConstraintsWidget;
        ConstraintWidget widget2;
        ArrayList<ConstraintWidget> listMatchConstraints;
        SolverVariable beginTarget;
        SolverVariable endTarget;
        ConstraintAnchor end;
        ConstraintAnchor end2;
        ConstraintAnchor endTarget2;
        ConstraintWidget previousVisibleWidget;
        ConstraintWidget widget3;
        ConstraintWidget next;
        SolverVariable beginNext;
        SolverVariable beginNextTarget;
        ConstraintAnchor beginNextAnchor;
        ConstraintWidget next2;
        ConstraintWidget previousVisibleWidget2;
        ConstraintWidget widget4;
        ConstraintWidget next3;
        int nextMargin;
        SolverVariable beginTarget2;
        SolverVariable beginNextTarget2;
        SolverVariable beginNext2;
        ConstraintAnchor beginNextAnchor2;
        int margin1;
        int margin2;
        ConstraintAnchor begin;
        ConstraintAnchor end3;
        float bias;
        float totalWeights;
        ConstraintWidget previousMatchConstraintsWidget2;
        ConstraintWidget widget5;
        ArrayList<ConstraintWidget> listMatchConstraints2;
        ConstraintWidget firstMatchConstraintsWidget;
        int margin;
        int strength;
        float totalWeights2;
        int strength2;
        ConstraintWidget next4;
        ConstraintWidget first = chainHead.mFirst;
        ConstraintWidget last = chainHead.mLast;
        ConstraintWidget firstVisibleWidget = chainHead.mFirstVisibleWidget;
        ConstraintWidget lastVisibleWidget = chainHead.mLastVisibleWidget;
        ConstraintWidget head = chainHead.mHead;
        float totalWeights3 = chainHead.mTotalWeight;
        ConstraintWidget firstMatchConstraintsWidget2 = chainHead.mFirstMatchConstraintWidget;
        ConstraintWidget previousMatchConstraintsWidget3 = chainHead.mLastMatchConstraintWidget;
        boolean isWrapContent = container.mListDimensionBehaviors[orientation] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (orientation == 0) {
            isChainSpread = head.mHorizontalChainStyle == 0;
            boolean isChainSpreadInside2 = head.mHorizontalChainStyle == 1;
            done = false;
            isChainPacked = head.mHorizontalChainStyle == 2;
            widget = first;
            isChainSpreadInside = isChainSpreadInside2;
        } else {
            isChainSpread = head.mVerticalChainStyle == 0;
            boolean isChainSpreadInside3 = head.mVerticalChainStyle == 1;
            done = false;
            isChainPacked = head.mVerticalChainStyle == 2;
            widget = first;
            isChainSpreadInside = isChainSpreadInside3;
        }
        while (!done) {
            ConstraintAnchor begin2 = widget.mListAnchors[offset];
            int strength3 = 4;
            if (isWrapContent || isChainPacked) {
                strength3 = 1;
            }
            int margin3 = begin2.getMargin();
            if (begin2.mTarget == null || widget == first) {
                margin = margin3;
            } else {
                margin = margin3 + begin2.mTarget.getMargin();
            }
            if (isChainPacked && widget != first && widget != firstVisibleWidget) {
                strength = 6;
            } else if (!isChainSpread || !isWrapContent) {
                strength = strength3;
            } else {
                strength = 4;
            }
            if (begin2.mTarget != null) {
                if (widget == firstVisibleWidget) {
                    totalWeights2 = totalWeights3;
                    system.addGreaterThan(begin2.mSolverVariable, begin2.mTarget.mSolverVariable, margin, 5);
                } else {
                    totalWeights2 = totalWeights3;
                    system.addGreaterThan(begin2.mSolverVariable, begin2.mTarget.mSolverVariable, margin, 6);
                }
                strength2 = strength;
                system.addEquality(begin2.mSolverVariable, begin2.mTarget.mSolverVariable, margin, strength2);
            } else {
                totalWeights2 = totalWeights3;
                strength2 = strength;
            }
            if (isWrapContent) {
                if (widget.getVisibility() != 8 && widget.mListDimensionBehaviors[orientation] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    system.addGreaterThan(widget.mListAnchors[offset + 1].mSolverVariable, widget.mListAnchors[offset].mSolverVariable, 0, 5);
                }
                system.addGreaterThan(widget.mListAnchors[offset].mSolverVariable, container.mListAnchors[offset].mSolverVariable, 0, 6);
            }
            ConstraintAnchor nextAnchor = widget.mListAnchors[offset + 1].mTarget;
            if (nextAnchor != null) {
                ConstraintWidget next5 = nextAnchor.mOwner;
                next4 = (next5.mListAnchors[offset].mTarget == null || next5.mListAnchors[offset].mTarget.mOwner != widget) ? null : next5;
            } else {
                next4 = null;
            }
            if (next4 != null) {
                widget = next4;
            } else {
                done = true;
            }
            totalWeights3 = totalWeights2;
        }
        if (lastVisibleWidget != null && last.mListAnchors[offset + 1].mTarget != null) {
            ConstraintAnchor end4 = lastVisibleWidget.mListAnchors[offset + 1];
            system.addLowerThan(end4.mSolverVariable, last.mListAnchors[offset + 1].mTarget.mSolverVariable, -end4.getMargin(), 5);
        }
        if (isWrapContent) {
            system.addGreaterThan(container.mListAnchors[offset + 1].mSolverVariable, last.mListAnchors[offset + 1].mSolverVariable, last.mListAnchors[offset + 1].getMargin(), 6);
        }
        ArrayList<ConstraintWidget> listMatchConstraints3 = chainHead.mWeightedMatchConstraintsWidgets;
        if (listMatchConstraints3 != null) {
            int count = listMatchConstraints3.size();
            if (count > 1) {
                ConstraintWidget lastMatch = null;
                float lastWeight = 0.0f;
                if (!chainHead.mHasUndefinedWeights || chainHead.mHasComplexMatchWeights) {
                    totalWeights = totalWeights3;
                } else {
                    totalWeights = (float) chainHead.mWidgetsMatchCount;
                }
                int i = 0;
                while (i < count) {
                    ConstraintWidget match = listMatchConstraints3.get(i);
                    float currentWeight = match.mWeight[orientation];
                    if (currentWeight >= 0.0f) {
                        listMatchConstraints2 = listMatchConstraints3;
                        widget5 = widget;
                        previousMatchConstraintsWidget2 = previousMatchConstraintsWidget3;
                    } else if (chainHead.mHasComplexMatchWeights) {
                        listMatchConstraints2 = listMatchConstraints3;
                        widget5 = widget;
                        previousMatchConstraintsWidget2 = previousMatchConstraintsWidget3;
                        system.addEquality(match.mListAnchors[offset + 1].mSolverVariable, match.mListAnchors[offset].mSolverVariable, 0, 4);
                        firstMatchConstraintsWidget = firstMatchConstraintsWidget2;
                        i++;
                        firstMatchConstraintsWidget2 = firstMatchConstraintsWidget;
                        count = count;
                        listMatchConstraints3 = listMatchConstraints2;
                        widget = widget5;
                        previousMatchConstraintsWidget3 = previousMatchConstraintsWidget2;
                    } else {
                        listMatchConstraints2 = listMatchConstraints3;
                        widget5 = widget;
                        previousMatchConstraintsWidget2 = previousMatchConstraintsWidget3;
                        currentWeight = 1.0f;
                    }
                    if (currentWeight == 0.0f) {
                        firstMatchConstraintsWidget = firstMatchConstraintsWidget2;
                        system.addEquality(match.mListAnchors[offset + 1].mSolverVariable, match.mListAnchors[offset].mSolverVariable, 0, 6);
                    } else {
                        firstMatchConstraintsWidget = firstMatchConstraintsWidget2;
                        if (lastMatch != null) {
                            SolverVariable begin3 = lastMatch.mListAnchors[offset].mSolverVariable;
                            SolverVariable end5 = lastMatch.mListAnchors[offset + 1].mSolverVariable;
                            SolverVariable nextBegin = match.mListAnchors[offset].mSolverVariable;
                            SolverVariable nextEnd = match.mListAnchors[offset + 1].mSolverVariable;
                            ArrayRow row = system.createRow();
                            row.createRowEqualMatchDimensions(lastWeight, totalWeights, currentWeight, begin3, end5, nextBegin, nextEnd);
                            system.addConstraint(row);
                        }
                        lastMatch = match;
                        lastWeight = currentWeight;
                    }
                    i++;
                    firstMatchConstraintsWidget2 = firstMatchConstraintsWidget;
                    count = count;
                    listMatchConstraints3 = listMatchConstraints2;
                    widget = widget5;
                    previousMatchConstraintsWidget3 = previousMatchConstraintsWidget2;
                }
                listMatchConstraints = listMatchConstraints3;
                widget2 = widget;
                previousMatchConstraintsWidget = previousMatchConstraintsWidget3;
            } else {
                listMatchConstraints = listMatchConstraints3;
                widget2 = widget;
                previousMatchConstraintsWidget = previousMatchConstraintsWidget3;
            }
        } else {
            listMatchConstraints = listMatchConstraints3;
            widget2 = widget;
            previousMatchConstraintsWidget = previousMatchConstraintsWidget3;
        }
        if (firstVisibleWidget != null) {
            if (firstVisibleWidget == lastVisibleWidget || isChainPacked) {
                ConstraintAnchor begin4 = first.mListAnchors[offset];
                ConstraintAnchor end6 = last.mListAnchors[offset + 1];
                SolverVariable beginTarget3 = first.mListAnchors[offset].mTarget != null ? first.mListAnchors[offset].mTarget.mSolverVariable : null;
                SolverVariable endTarget3 = last.mListAnchors[offset + 1].mTarget != null ? last.mListAnchors[offset + 1].mTarget.mSolverVariable : null;
                if (firstVisibleWidget == lastVisibleWidget) {
                    begin = firstVisibleWidget.mListAnchors[offset];
                    end3 = firstVisibleWidget.mListAnchors[offset + 1];
                } else {
                    begin = begin4;
                    end3 = end6;
                }
                if (beginTarget3 != null && endTarget3 != null) {
                    if (orientation == 0) {
                        bias = head.mHorizontalBiasPercent;
                    } else {
                        bias = head.mVerticalBiasPercent;
                    }
                    system.addCentering(begin.mSolverVariable, beginTarget3, begin.getMargin(), bias, endTarget3, end3.mSolverVariable, end3.getMargin(), 5);
                    if (!isChainSpread) {
                    }
                    ConstraintAnchor begin5 = firstVisibleWidget.mListAnchors[offset];
                    ConstraintAnchor end7 = lastVisibleWidget.mListAnchors[offset + 1];
                    if (begin5.mTarget == null) {
                    }
                    if (end7.mTarget == null) {
                    }
                    if (last == lastVisibleWidget) {
                    }
                    if (firstVisibleWidget != lastVisibleWidget) {
                    }
                    if (beginTarget == null) {
                    }
                } else if ((!isChainSpread || isChainSpreadInside) && firstVisibleWidget != null) {
                    ConstraintAnchor begin52 = firstVisibleWidget.mListAnchors[offset];
                    ConstraintAnchor end72 = lastVisibleWidget.mListAnchors[offset + 1];
                    beginTarget = begin52.mTarget == null ? begin52.mTarget.mSolverVariable : null;
                    SolverVariable endTarget4 = end72.mTarget == null ? end72.mTarget.mSolverVariable : null;
                    if (last == lastVisibleWidget) {
                        ConstraintAnchor realEnd = last.mListAnchors[offset + 1];
                        endTarget = realEnd.mTarget != null ? realEnd.mTarget.mSolverVariable : null;
                    } else {
                        endTarget = endTarget4;
                    }
                    if (firstVisibleWidget != lastVisibleWidget) {
                        begin52 = firstVisibleWidget.mListAnchors[offset];
                        end = firstVisibleWidget.mListAnchors[offset + 1];
                    } else {
                        end = end72;
                    }
                    if (beginTarget == null && endTarget != null) {
                        int beginMargin = begin52.getMargin();
                        if (lastVisibleWidget == null) {
                            lastVisibleWidget = last;
                        }
                        system.addCentering(begin52.mSolverVariable, beginTarget, beginMargin, 0.5f, endTarget, end.mSolverVariable, lastVisibleWidget.mListAnchors[offset + 1].getMargin(), 5);
                        return;
                    }
                } else {
                    return;
                }
            }
        }
        if (!isChainSpread || firstVisibleWidget == null) {
            int i2 = 8;
            if (isChainSpreadInside && firstVisibleWidget != null) {
                boolean applyFixedEquality = chainHead.mWidgetsMatchCount > 0 && chainHead.mWidgetsCount == chainHead.mWidgetsMatchCount;
                ConstraintWidget widget6 = firstVisibleWidget;
                ConstraintWidget previousVisibleWidget3 = firstVisibleWidget;
                while (widget6 != null) {
                    ConstraintWidget next6 = widget6.mNextChainWidget[orientation];
                    while (next6 != null && next6.getVisibility() == i2) {
                        next6 = next6.mNextChainWidget[orientation];
                    }
                    if (widget6 == firstVisibleWidget || widget6 == lastVisibleWidget || next6 == null) {
                        widget3 = widget6;
                        previousVisibleWidget = previousVisibleWidget3;
                        next = next6;
                    } else {
                        ConstraintWidget next7 = next6 == lastVisibleWidget ? null : next6;
                        ConstraintAnchor beginAnchor = widget6.mListAnchors[offset];
                        SolverVariable begin6 = beginAnchor.mSolverVariable;
                        if (beginAnchor.mTarget != null) {
                            SolverVariable solverVariable = beginAnchor.mTarget.mSolverVariable;
                        }
                        SolverVariable beginTarget4 = previousVisibleWidget3.mListAnchors[offset + 1].mSolverVariable;
                        SolverVariable beginNext3 = null;
                        int beginMargin2 = beginAnchor.getMargin();
                        int nextMargin2 = widget6.mListAnchors[offset + 1].getMargin();
                        if (next7 != null) {
                            ConstraintAnchor beginNextAnchor3 = next7.mListAnchors[offset];
                            SolverVariable beginNext4 = beginNextAnchor3.mSolverVariable;
                            beginNextTarget = beginNextAnchor3.mTarget != null ? beginNextAnchor3.mTarget.mSolverVariable : null;
                            beginNext = beginNext4;
                            beginNextAnchor = beginNextAnchor3;
                        } else {
                            ConstraintAnchor beginNextAnchor4 = widget6.mListAnchors[offset + 1].mTarget;
                            if (beginNextAnchor4 != null) {
                                beginNext3 = beginNextAnchor4.mSolverVariable;
                            }
                            beginNextAnchor = beginNextAnchor4;
                            beginNextTarget = widget6.mListAnchors[offset + 1].mSolverVariable;
                            beginNext = beginNext3;
                        }
                        if (beginNextAnchor != null) {
                            nextMargin2 += beginNextAnchor.getMargin();
                        }
                        if (previousVisibleWidget3 != null) {
                            beginMargin2 += previousVisibleWidget3.mListAnchors[offset + 1].getMargin();
                        }
                        int strength4 = applyFixedEquality ? 6 : 4;
                        if (begin6 == null || beginTarget4 == null || beginNext == null || beginNextTarget == null) {
                            next2 = next7;
                            widget3 = widget6;
                            previousVisibleWidget = previousVisibleWidget3;
                        } else {
                            next2 = next7;
                            widget3 = widget6;
                            previousVisibleWidget = previousVisibleWidget3;
                            system.addCentering(begin6, beginTarget4, beginMargin2, 0.5f, beginNext, beginNextTarget, nextMargin2, strength4);
                        }
                        next = next2;
                    }
                    if (widget3.getVisibility() != 8) {
                        previousVisibleWidget3 = widget3;
                    } else {
                        previousVisibleWidget3 = previousVisibleWidget;
                    }
                    widget6 = next;
                    i2 = 8;
                }
                ConstraintAnchor begin7 = firstVisibleWidget.mListAnchors[offset];
                ConstraintAnchor beginTarget5 = first.mListAnchors[offset].mTarget;
                ConstraintAnchor end8 = lastVisibleWidget.mListAnchors[offset + 1];
                ConstraintAnchor endTarget5 = last.mListAnchors[offset + 1].mTarget;
                if (beginTarget5 == null) {
                    endTarget2 = endTarget5;
                    end2 = end8;
                } else if (firstVisibleWidget != lastVisibleWidget) {
                    system.addEquality(begin7.mSolverVariable, beginTarget5.mSolverVariable, begin7.getMargin(), 5);
                    endTarget2 = endTarget5;
                    end2 = end8;
                } else if (endTarget5 != null) {
                    endTarget2 = endTarget5;
                    end2 = end8;
                    system.addCentering(begin7.mSolverVariable, beginTarget5.mSolverVariable, begin7.getMargin(), 0.5f, end8.mSolverVariable, endTarget5.mSolverVariable, end8.getMargin(), 5);
                } else {
                    endTarget2 = endTarget5;
                    end2 = end8;
                }
                if (endTarget2 != null && firstVisibleWidget != lastVisibleWidget) {
                    system.addEquality(end2.mSolverVariable, endTarget2.mSolverVariable, -end2.getMargin(), 5);
                }
            }
            if (!isChainSpread) {
            }
            ConstraintAnchor begin522 = firstVisibleWidget.mListAnchors[offset];
            ConstraintAnchor end722 = lastVisibleWidget.mListAnchors[offset + 1];
            if (begin522.mTarget == null) {
            }
            if (end722.mTarget == null) {
            }
            if (last == lastVisibleWidget) {
            }
            if (firstVisibleWidget != lastVisibleWidget) {
            }
            if (beginTarget == null) {
            }
        }
        boolean applyFixedEquality2 = chainHead.mWidgetsMatchCount > 0 && chainHead.mWidgetsCount == chainHead.mWidgetsMatchCount;
        ConstraintWidget previousVisibleWidget4 = firstVisibleWidget;
        for (ConstraintWidget widget7 = firstVisibleWidget; widget7 != null; widget7 = next3) {
            ConstraintWidget next8 = widget7.mNextChainWidget[orientation];
            while (true) {
                if (next8 == null) {
                    break;
                }
                if (next8.getVisibility() != 8) {
                    break;
                }
                next8 = next8.mNextChainWidget[orientation];
            }
            if (next8 != null || widget7 == lastVisibleWidget) {
                ConstraintAnchor beginAnchor2 = widget7.mListAnchors[offset];
                SolverVariable begin8 = beginAnchor2.mSolverVariable;
                SolverVariable beginTarget6 = beginAnchor2.mTarget != null ? beginAnchor2.mTarget.mSolverVariable : null;
                if (previousVisibleWidget4 != widget7) {
                    beginTarget2 = previousVisibleWidget4.mListAnchors[offset + 1].mSolverVariable;
                } else if (widget7 == firstVisibleWidget && previousVisibleWidget4 == widget7) {
                    beginTarget2 = first.mListAnchors[offset].mTarget != null ? first.mListAnchors[offset].mTarget.mSolverVariable : null;
                } else {
                    beginTarget2 = beginTarget6;
                }
                SolverVariable beginNext5 = null;
                int beginMargin3 = beginAnchor2.getMargin();
                int nextMargin3 = widget7.mListAnchors[offset + 1].getMargin();
                if (next8 != null) {
                    ConstraintAnchor beginNextAnchor5 = next8.mListAnchors[offset];
                    beginNextAnchor2 = beginNextAnchor5;
                    beginNext2 = beginNextAnchor5.mSolverVariable;
                    beginNextTarget2 = widget7.mListAnchors[offset + 1].mSolverVariable;
                } else {
                    ConstraintAnchor beginNextAnchor6 = last.mListAnchors[offset + 1].mTarget;
                    if (beginNextAnchor6 != null) {
                        beginNext5 = beginNextAnchor6.mSolverVariable;
                    }
                    beginNextAnchor2 = beginNextAnchor6;
                    beginNext2 = beginNext5;
                    beginNextTarget2 = widget7.mListAnchors[offset + 1].mSolverVariable;
                }
                if (beginNextAnchor2 != null) {
                    nextMargin3 += beginNextAnchor2.getMargin();
                }
                if (previousVisibleWidget4 != null) {
                    beginMargin3 += previousVisibleWidget4.mListAnchors[offset + 1].getMargin();
                }
                if (begin8 == null || beginTarget2 == null || beginNext2 == null || beginNextTarget2 == null) {
                    next3 = next8;
                    widget4 = widget7;
                    previousVisibleWidget2 = previousVisibleWidget4;
                    nextMargin = 8;
                } else {
                    if (widget7 == firstVisibleWidget) {
                        margin1 = firstVisibleWidget.mListAnchors[offset].getMargin();
                    } else {
                        margin1 = beginMargin3;
                    }
                    if (widget7 == lastVisibleWidget) {
                        margin2 = lastVisibleWidget.mListAnchors[offset + 1].getMargin();
                    } else {
                        margin2 = nextMargin3;
                    }
                    nextMargin = 8;
                    next3 = next8;
                    widget4 = widget7;
                    previousVisibleWidget2 = previousVisibleWidget4;
                    system.addCentering(begin8, beginTarget2, margin1, 0.5f, beginNext2, beginNextTarget2, margin2, applyFixedEquality2 ? 6 : 4);
                }
            } else {
                next3 = next8;
                widget4 = widget7;
                previousVisibleWidget2 = previousVisibleWidget4;
                nextMargin = 8;
            }
            if (widget4.getVisibility() != nextMargin) {
                previousVisibleWidget4 = widget4;
            } else {
                previousVisibleWidget4 = previousVisibleWidget2;
            }
        }
        if (!isChainSpread) {
        }
        ConstraintAnchor begin5222 = firstVisibleWidget.mListAnchors[offset];
        ConstraintAnchor end7222 = lastVisibleWidget.mListAnchors[offset + 1];
        if (begin5222.mTarget == null) {
        }
        if (end7222.mTarget == null) {
        }
        if (last == lastVisibleWidget) {
        }
        if (firstVisibleWidget != lastVisibleWidget) {
        }
        if (beginTarget == null) {
        }
    }
}
