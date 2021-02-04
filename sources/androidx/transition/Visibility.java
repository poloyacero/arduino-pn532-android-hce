package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.res.TypedArrayUtils;
import androidx.transition.AnimatorUtils;
import androidx.transition.Transition;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class Visibility extends Transition {
    public static final int MODE_IN = 1;
    public static final int MODE_OUT = 2;
    private static final String PROPNAME_PARENT = "android:visibility:parent";
    private static final String PROPNAME_SCREEN_LOCATION = "android:visibility:screenLocation";
    static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
    private static final String[] sTransitionProperties = {PROPNAME_VISIBILITY, PROPNAME_PARENT};
    private int mMode = 3;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    /* access modifiers changed from: private */
    public static class VisibilityInfo {
        ViewGroup mEndParent;
        int mEndVisibility;
        boolean mFadeIn;
        ViewGroup mStartParent;
        int mStartVisibility;
        boolean mVisibilityChange;

        VisibilityInfo() {
        }
    }

    public Visibility() {
    }

    public Visibility(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.VISIBILITY_TRANSITION);
        int mode = TypedArrayUtils.getNamedInt(a, (XmlResourceParser) attrs, "transitionVisibilityMode", 0, 0);
        a.recycle();
        if (mode != 0) {
            setMode(mode);
        }
    }

    public void setMode(int mode) {
        if ((mode & -4) == 0) {
            this.mMode = mode;
            return;
        }
        throw new IllegalArgumentException("Only MODE_IN and MODE_OUT flags are allowed");
    }

    public int getMode() {
        return this.mMode;
    }

    @Override // androidx.transition.Transition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    private void captureValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_VISIBILITY, Integer.valueOf(transitionValues.view.getVisibility()));
        transitionValues.values.put(PROPNAME_PARENT, transitionValues.view.getParent());
        int[] loc = new int[2];
        transitionValues.view.getLocationOnScreen(loc);
        transitionValues.values.put(PROPNAME_SCREEN_LOCATION, loc);
    }

    @Override // androidx.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // androidx.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public boolean isVisible(TransitionValues values) {
        if (values == null) {
            return false;
        }
        int visibility = ((Integer) values.values.get(PROPNAME_VISIBILITY)).intValue();
        View parent = (View) values.values.get(PROPNAME_PARENT);
        if (visibility != 0 || parent == null) {
            return false;
        }
        return true;
    }

    private VisibilityInfo getVisibilityChangeInfo(TransitionValues startValues, TransitionValues endValues) {
        VisibilityInfo visInfo = new VisibilityInfo();
        visInfo.mVisibilityChange = false;
        visInfo.mFadeIn = false;
        if (startValues == null || !startValues.values.containsKey(PROPNAME_VISIBILITY)) {
            visInfo.mStartVisibility = -1;
            visInfo.mStartParent = null;
        } else {
            visInfo.mStartVisibility = ((Integer) startValues.values.get(PROPNAME_VISIBILITY)).intValue();
            visInfo.mStartParent = (ViewGroup) startValues.values.get(PROPNAME_PARENT);
        }
        if (endValues == null || !endValues.values.containsKey(PROPNAME_VISIBILITY)) {
            visInfo.mEndVisibility = -1;
            visInfo.mEndParent = null;
        } else {
            visInfo.mEndVisibility = ((Integer) endValues.values.get(PROPNAME_VISIBILITY)).intValue();
            visInfo.mEndParent = (ViewGroup) endValues.values.get(PROPNAME_PARENT);
        }
        if (startValues == null || endValues == null) {
            if (startValues == null && visInfo.mEndVisibility == 0) {
                visInfo.mFadeIn = true;
                visInfo.mVisibilityChange = true;
            } else if (endValues == null && visInfo.mStartVisibility == 0) {
                visInfo.mFadeIn = false;
                visInfo.mVisibilityChange = true;
            }
        } else if (visInfo.mStartVisibility == visInfo.mEndVisibility && visInfo.mStartParent == visInfo.mEndParent) {
            return visInfo;
        } else {
            if (visInfo.mStartVisibility != visInfo.mEndVisibility) {
                if (visInfo.mStartVisibility == 0) {
                    visInfo.mFadeIn = false;
                    visInfo.mVisibilityChange = true;
                } else if (visInfo.mEndVisibility == 0) {
                    visInfo.mFadeIn = true;
                    visInfo.mVisibilityChange = true;
                }
            } else if (visInfo.mEndParent == null) {
                visInfo.mFadeIn = false;
                visInfo.mVisibilityChange = true;
            } else if (visInfo.mStartParent == null) {
                visInfo.mFadeIn = true;
                visInfo.mVisibilityChange = true;
            }
        }
        return visInfo;
    }

    @Override // androidx.transition.Transition
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        VisibilityInfo visInfo = getVisibilityChangeInfo(startValues, endValues);
        if (!visInfo.mVisibilityChange) {
            return null;
        }
        if (visInfo.mStartParent == null && visInfo.mEndParent == null) {
            return null;
        }
        if (visInfo.mFadeIn) {
            return onAppear(sceneRoot, startValues, visInfo.mStartVisibility, endValues, visInfo.mEndVisibility);
        }
        return onDisappear(sceneRoot, startValues, visInfo.mStartVisibility, endValues, visInfo.mEndVisibility);
    }

    public Animator onAppear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        if ((this.mMode & 1) != 1 || endValues == null) {
            return null;
        }
        if (startValues == null) {
            View endParent = (View) endValues.view.getParent();
            if (getVisibilityChangeInfo(getMatchedTransitionValues(endParent, false), getTransitionValues(endParent, false)).mVisibilityChange) {
                return null;
            }
        }
        return onAppear(sceneRoot, endValues.view, startValues, endValues);
    }

    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0119 A[RETURN] */
    public Animator onDisappear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        int id;
        if ((this.mMode & 2) != 2) {
            return null;
        }
        View startView = startValues != null ? startValues.view : null;
        View endView = endValues != null ? endValues.view : null;
        final View overlayView = null;
        View viewToKeep = null;
        if (endView != null) {
            if (endView.getParent() != null) {
                if (endVisibility == 4) {
                    viewToKeep = endView;
                } else if (startView == endView) {
                    viewToKeep = endView;
                } else {
                    overlayView = this.mCanRemoveViews ? startView : TransitionUtils.copyViewImage(sceneRoot, startView, (View) startView.getParent());
                }
                if (overlayView == null && startValues != null) {
                    int[] screenLoc = (int[]) startValues.values.get(PROPNAME_SCREEN_LOCATION);
                    int screenX = screenLoc[0];
                    int screenY = screenLoc[1];
                    int[] loc = new int[2];
                    sceneRoot.getLocationOnScreen(loc);
                    overlayView.offsetLeftAndRight((screenX - loc[0]) - overlayView.getLeft());
                    overlayView.offsetTopAndBottom((screenY - loc[1]) - overlayView.getTop());
                    final ViewGroupOverlayImpl overlay = ViewGroupUtils.getOverlay(sceneRoot);
                    overlay.add(overlayView);
                    Animator animator = onDisappear(sceneRoot, overlayView, startValues, endValues);
                    if (animator == null) {
                        overlay.remove(overlayView);
                    } else {
                        animator.addListener(new AnimatorListenerAdapter() {
                            /* class androidx.transition.Visibility.AnonymousClass1 */

                            public void onAnimationEnd(Animator animation) {
                                overlay.remove(overlayView);
                            }
                        });
                    }
                    return animator;
                } else if (viewToKeep != null) {
                    return null;
                } else {
                    int originalVisibility = viewToKeep.getVisibility();
                    ViewUtils.setTransitionVisibility(viewToKeep, 0);
                    Animator animator2 = onDisappear(sceneRoot, viewToKeep, startValues, endValues);
                    if (animator2 != null) {
                        DisappearListener disappearListener = new DisappearListener(viewToKeep, endVisibility, true);
                        animator2.addListener(disappearListener);
                        AnimatorUtils.addPauseListener(animator2, disappearListener);
                        addListener(disappearListener);
                    } else {
                        ViewUtils.setTransitionVisibility(viewToKeep, originalVisibility);
                    }
                    return animator2;
                }
            }
        }
        if (endView != null) {
            overlayView = endView;
        } else if (startView != null) {
            if (startView.getParent() == null) {
                overlayView = startView;
            } else if (startView.getParent() instanceof View) {
                View startParent = (View) startView.getParent();
                if (!getVisibilityChangeInfo(getTransitionValues(startParent, true), getMatchedTransitionValues(startParent, true)).mVisibilityChange) {
                    overlayView = TransitionUtils.copyViewImage(sceneRoot, startView, startParent);
                } else if (startParent.getParent() == null && (id = startParent.getId()) != -1 && sceneRoot.findViewById(id) != null && this.mCanRemoveViews) {
                    overlayView = startView;
                }
            }
        }
        if (overlayView == null) {
        }
        if (viewToKeep != null) {
        }
    }

    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    @Override // androidx.transition.Transition
    public boolean isTransitionRequired(TransitionValues startValues, TransitionValues newValues) {
        if (startValues == null && newValues == null) {
            return false;
        }
        if (startValues != null && newValues != null && newValues.values.containsKey(PROPNAME_VISIBILITY) != startValues.values.containsKey(PROPNAME_VISIBILITY)) {
            return false;
        }
        VisibilityInfo changeInfo = getVisibilityChangeInfo(startValues, newValues);
        if (!changeInfo.mVisibilityChange) {
            return false;
        }
        if (changeInfo.mStartVisibility == 0 || changeInfo.mEndVisibility == 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public static class DisappearListener extends AnimatorListenerAdapter implements Transition.TransitionListener, AnimatorUtils.AnimatorPauseListenerCompat {
        boolean mCanceled = false;
        private final int mFinalVisibility;
        private boolean mLayoutSuppressed;
        private final ViewGroup mParent;
        private final boolean mSuppressLayout;
        private final View mView;

        DisappearListener(View view, int finalVisibility, boolean suppressLayout) {
            this.mView = view;
            this.mFinalVisibility = finalVisibility;
            this.mParent = (ViewGroup) view.getParent();
            this.mSuppressLayout = suppressLayout;
            suppressLayout(true);
        }

        @Override // androidx.transition.AnimatorUtils.AnimatorPauseListenerCompat
        public void onAnimationPause(Animator animation) {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility);
            }
        }

        @Override // androidx.transition.AnimatorUtils.AnimatorPauseListenerCompat
        public void onAnimationResume(Animator animation) {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, 0);
            }
        }

        public void onAnimationCancel(Animator animation) {
            this.mCanceled = true;
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            hideViewWhenNotCanceled();
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionStart(Transition transition) {
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionEnd(Transition transition) {
            hideViewWhenNotCanceled();
            transition.removeListener(this);
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionCancel(Transition transition) {
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionPause(Transition transition) {
            suppressLayout(false);
        }

        @Override // androidx.transition.Transition.TransitionListener
        public void onTransitionResume(Transition transition) {
            suppressLayout(true);
        }

        private void hideViewWhenNotCanceled() {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility);
                ViewGroup viewGroup = this.mParent;
                if (viewGroup != null) {
                    viewGroup.invalidate();
                }
            }
            suppressLayout(false);
        }

        private void suppressLayout(boolean suppress) {
            ViewGroup viewGroup;
            if (this.mSuppressLayout && this.mLayoutSuppressed != suppress && (viewGroup = this.mParent) != null) {
                this.mLayoutSuppressed = suppress;
                ViewGroupUtils.suppressLayout(viewGroup, suppress);
            }
        }
    }
}
