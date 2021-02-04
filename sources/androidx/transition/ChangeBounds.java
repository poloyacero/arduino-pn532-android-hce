package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.res.TypedArrayUtils;
import androidx.core.view.ViewCompat;
import java.util.Map;

public class ChangeBounds extends Transition {
    private static final Property<View, PointF> BOTTOM_RIGHT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, "bottomRight") {
        /* class androidx.transition.ChangeBounds.AnonymousClass4 */

        public void set(View view, PointF bottomRight) {
            ViewUtils.setLeftTopRightBottom(view, view.getLeft(), view.getTop(), Math.round(bottomRight.x), Math.round(bottomRight.y));
        }

        public PointF get(View view) {
            return null;
        }
    };
    private static final Property<ViewBounds, PointF> BOTTOM_RIGHT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, "bottomRight") {
        /* class androidx.transition.ChangeBounds.AnonymousClass3 */

        public void set(ViewBounds viewBounds, PointF bottomRight) {
            viewBounds.setBottomRight(bottomRight);
        }

        public PointF get(ViewBounds viewBounds) {
            return null;
        }
    };
    private static final Property<Drawable, PointF> DRAWABLE_ORIGIN_PROPERTY = new Property<Drawable, PointF>(PointF.class, "boundsOrigin") {
        /* class androidx.transition.ChangeBounds.AnonymousClass1 */
        private Rect mBounds = new Rect();

        public void set(Drawable object, PointF value) {
            object.copyBounds(this.mBounds);
            this.mBounds.offsetTo(Math.round(value.x), Math.round(value.y));
            object.setBounds(this.mBounds);
        }

        public PointF get(Drawable object) {
            object.copyBounds(this.mBounds);
            return new PointF((float) this.mBounds.left, (float) this.mBounds.top);
        }
    };
    private static final Property<View, PointF> POSITION_PROPERTY = new Property<View, PointF>(PointF.class, "position") {
        /* class androidx.transition.ChangeBounds.AnonymousClass6 */

        public void set(View view, PointF topLeft) {
            int left = Math.round(topLeft.x);
            int top = Math.round(topLeft.y);
            ViewUtils.setLeftTopRightBottom(view, left, top, view.getWidth() + left, view.getHeight() + top);
        }

        public PointF get(View view) {
            return null;
        }
    };
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_CLIP = "android:changeBounds:clip";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";
    private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
    private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
    private static final Property<View, PointF> TOP_LEFT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, "topLeft") {
        /* class androidx.transition.ChangeBounds.AnonymousClass5 */

        public void set(View view, PointF topLeft) {
            ViewUtils.setLeftTopRightBottom(view, Math.round(topLeft.x), Math.round(topLeft.y), view.getRight(), view.getBottom());
        }

        public PointF get(View view) {
            return null;
        }
    };
    private static final Property<ViewBounds, PointF> TOP_LEFT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, "topLeft") {
        /* class androidx.transition.ChangeBounds.AnonymousClass2 */

        public void set(ViewBounds viewBounds, PointF topLeft) {
            viewBounds.setTopLeft(topLeft);
        }

        public PointF get(ViewBounds viewBounds) {
            return null;
        }
    };
    private static RectEvaluator sRectEvaluator = new RectEvaluator();
    private static final String[] sTransitionProperties = {PROPNAME_BOUNDS, PROPNAME_CLIP, PROPNAME_PARENT, PROPNAME_WINDOW_X, PROPNAME_WINDOW_Y};
    private boolean mReparent;
    private boolean mResizeClip;
    private int[] mTempLocation;

    public ChangeBounds() {
        this.mTempLocation = new int[2];
        this.mResizeClip = false;
        this.mReparent = false;
    }

    public ChangeBounds(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTempLocation = new int[2];
        this.mResizeClip = false;
        this.mReparent = false;
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.CHANGE_BOUNDS);
        boolean resizeClip = TypedArrayUtils.getNamedBoolean(a, (XmlResourceParser) attrs, "resizeClip", 0, false);
        a.recycle();
        setResizeClip(resizeClip);
    }

    @Override // androidx.transition.Transition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public void setResizeClip(boolean resizeClip) {
        this.mResizeClip = resizeClip;
    }

    public boolean getResizeClip() {
        return this.mResizeClip;
    }

    private void captureValues(TransitionValues values) {
        View view = values.view;
        if (ViewCompat.isLaidOut(view) || view.getWidth() != 0 || view.getHeight() != 0) {
            values.values.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
            values.values.put(PROPNAME_PARENT, values.view.getParent());
            if (this.mReparent) {
                values.view.getLocationInWindow(this.mTempLocation);
                values.values.put(PROPNAME_WINDOW_X, Integer.valueOf(this.mTempLocation[0]));
                values.values.put(PROPNAME_WINDOW_Y, Integer.valueOf(this.mTempLocation[1]));
            }
            if (this.mResizeClip) {
                values.values.put(PROPNAME_CLIP, ViewCompat.getClipBounds(view));
            }
        }
    }

    @Override // androidx.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // androidx.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private boolean parentMatches(View startParent, View endParent) {
        if (!this.mReparent) {
            return true;
        }
        boolean parentMatches = true;
        TransitionValues endValues = getMatchedTransitionValues(startParent, true);
        if (endValues == null) {
            if (startParent != endParent) {
                parentMatches = false;
            }
            return parentMatches;
        }
        if (endParent != endValues.view) {
            parentMatches = false;
        }
        return parentMatches;
    }

    /* JADX INFO: Multiple debug info for r11v5 int: [D('startParentVals' java.util.Map<java.lang.String, java.lang.Object>), D('endRight' int)] */
    /* JADX INFO: Multiple debug info for r12v4 int: [D('startBottom' int), D('endParentVals' java.util.Map<java.lang.String, java.lang.Object>)] */
    /* JADX INFO: Multiple debug info for r13v3 int: [D('startParent' android.view.ViewGroup), D('endBottom' int)] */
    /* JADX INFO: Multiple debug info for r14v3 int: [D('endParent' android.view.ViewGroup), D('startWidth' int)] */
    /* JADX INFO: Multiple debug info for r6v4 int: [D('startBounds' android.graphics.Rect), D('startHeight' int)] */
    /* JADX INFO: Multiple debug info for r5v6 int: [D('endBounds' android.graphics.Rect), D('endWidth' int)] */
    @Override // androidx.transition.Transition
    public Animator createAnimator(final ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        boolean z;
        final View view;
        Animator anim;
        final int endLeft;
        int startTop;
        int startLeft;
        ObjectAnimator positionAnimator;
        Rect startClip;
        int i;
        Rect endClip;
        Rect rect;
        if (startValues == null) {
            return null;
        }
        if (endValues == null) {
            return null;
        }
        Map<String, Object> startParentVals = startValues.values;
        Map<String, Object> endParentVals = endValues.values;
        ViewGroup startParent = (ViewGroup) startParentVals.get(PROPNAME_PARENT);
        ViewGroup endParent = (ViewGroup) endParentVals.get(PROPNAME_PARENT);
        if (startParent == null) {
            return null;
        }
        if (endParent == null) {
            return null;
        }
        final View view2 = endValues.view;
        if (parentMatches(startParent, endParent)) {
            Rect startBounds = (Rect) startValues.values.get(PROPNAME_BOUNDS);
            Rect endBounds = (Rect) endValues.values.get(PROPNAME_BOUNDS);
            int startLeft2 = startBounds.left;
            int endLeft2 = endBounds.left;
            int startTop2 = startBounds.top;
            final int endTop = endBounds.top;
            int startRight = startBounds.right;
            final int endRight = endBounds.right;
            int startBottom = startBounds.bottom;
            final int endBottom = endBounds.bottom;
            int startWidth = startRight - startLeft2;
            int startHeight = startBottom - startTop2;
            int endWidth = endRight - endLeft2;
            int endHeight = endBottom - endTop;
            Rect startClip2 = (Rect) startValues.values.get(PROPNAME_CLIP);
            final Rect endClip2 = (Rect) endValues.values.get(PROPNAME_CLIP);
            int numChanges = 0;
            if (!((startWidth == 0 || startHeight == 0) && (endWidth == 0 || endHeight == 0))) {
                if (!(startLeft2 == endLeft2 && startTop2 == endTop)) {
                    numChanges = 0 + 1;
                }
                if (!(startRight == endRight && startBottom == endBottom)) {
                    numChanges++;
                }
            }
            if ((startClip2 != null && !startClip2.equals(endClip2)) || (startClip2 == null && endClip2 != null)) {
                numChanges++;
            }
            if (numChanges <= 0) {
                return null;
            }
            if (!this.mResizeClip) {
                ViewUtils.setLeftTopRightBottom(view2, startLeft2, startTop2, startRight, startBottom);
                if (numChanges != 2) {
                    if (startLeft2 != endLeft2) {
                        view = view2;
                    } else if (startTop2 != endTop) {
                        view = view2;
                    } else {
                        view = view2;
                        anim = ObjectAnimatorUtils.ofPointF(view, BOTTOM_RIGHT_ONLY_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) endRight, (float) endBottom));
                        z = true;
                    }
                    anim = ObjectAnimatorUtils.ofPointF(view, TOP_LEFT_ONLY_PROPERTY, getPathMotion().getPath((float) startLeft2, (float) startTop2, (float) endLeft2, (float) endTop));
                    z = true;
                } else if (startWidth == endWidth && startHeight == endHeight) {
                    anim = ObjectAnimatorUtils.ofPointF(view2, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft2, (float) startTop2, (float) endLeft2, (float) endTop));
                    view = view2;
                    z = true;
                } else {
                    final ViewBounds viewBounds = new ViewBounds(view2);
                    ObjectAnimator topLeftAnimator = ObjectAnimatorUtils.ofPointF(viewBounds, TOP_LEFT_PROPERTY, getPathMotion().getPath((float) startLeft2, (float) startTop2, (float) endLeft2, (float) endTop));
                    ObjectAnimator bottomRightAnimator = ObjectAnimatorUtils.ofPointF(viewBounds, BOTTOM_RIGHT_PROPERTY, getPathMotion().getPath((float) startRight, (float) startBottom, (float) endRight, (float) endBottom));
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(topLeftAnimator, bottomRightAnimator);
                    set.addListener(new AnimatorListenerAdapter() {
                        /* class androidx.transition.ChangeBounds.AnonymousClass7 */
                        private ViewBounds mViewBounds = viewBounds;
                    });
                    anim = set;
                    view = view2;
                    z = true;
                }
            } else {
                view = view2;
                ViewUtils.setLeftTopRightBottom(view, startLeft2, startTop2, startLeft2 + Math.max(startWidth, endWidth), startTop2 + Math.max(startHeight, endHeight));
                if (startLeft2 == endLeft2 && startTop2 == endTop) {
                    endLeft = endLeft2;
                    positionAnimator = null;
                    startTop = startTop2;
                    startLeft = startLeft2;
                } else {
                    startLeft = startLeft2;
                    startTop = startTop2;
                    endLeft = endLeft2;
                    positionAnimator = ObjectAnimatorUtils.ofPointF(view, POSITION_PROPERTY, getPathMotion().getPath((float) startLeft2, (float) startTop2, (float) endLeft2, (float) endTop));
                }
                if (startClip2 == null) {
                    i = 0;
                    startClip = new Rect(0, 0, startWidth, startHeight);
                } else {
                    i = 0;
                    startClip = startClip2;
                }
                if (endClip2 == null) {
                    endClip = new Rect(i, i, endWidth, endHeight);
                } else {
                    endClip = endClip2;
                }
                ObjectAnimator clipAnimator = null;
                if (!startClip.equals(endClip)) {
                    ViewCompat.setClipBounds(view, startClip);
                    ObjectAnimator clipAnimator2 = ObjectAnimator.ofObject(view, "clipBounds", sRectEvaluator, startClip, endClip);
                    rect = startClip;
                    z = true;
                    clipAnimator2.addListener(new AnimatorListenerAdapter() {
                        /* class androidx.transition.ChangeBounds.AnonymousClass8 */
                        private boolean mIsCanceled;

                        public void onAnimationCancel(Animator animation) {
                            this.mIsCanceled = true;
                        }

                        public void onAnimationEnd(Animator animation) {
                            if (!this.mIsCanceled) {
                                ViewCompat.setClipBounds(view, endClip2);
                                ViewUtils.setLeftTopRightBottom(view, endLeft, endTop, endRight, endBottom);
                            }
                        }
                    });
                    clipAnimator = clipAnimator2;
                } else {
                    z = true;
                    rect = startClip;
                }
                anim = TransitionUtils.mergeAnimators(positionAnimator, clipAnimator);
            }
            if (view.getParent() instanceof ViewGroup) {
                final ViewGroup parent = (ViewGroup) view.getParent();
                ViewGroupUtils.suppressLayout(parent, z);
                addListener(new TransitionListenerAdapter() {
                    /* class androidx.transition.ChangeBounds.AnonymousClass9 */
                    boolean mCanceled = false;

                    @Override // androidx.transition.Transition.TransitionListener, androidx.transition.TransitionListenerAdapter
                    public void onTransitionCancel(Transition transition) {
                        ViewGroupUtils.suppressLayout(parent, false);
                        this.mCanceled = true;
                    }

                    @Override // androidx.transition.Transition.TransitionListener, androidx.transition.TransitionListenerAdapter
                    public void onTransitionEnd(Transition transition) {
                        if (!this.mCanceled) {
                            ViewGroupUtils.suppressLayout(parent, false);
                        }
                        transition.removeListener(this);
                    }

                    @Override // androidx.transition.Transition.TransitionListener, androidx.transition.TransitionListenerAdapter
                    public void onTransitionPause(Transition transition) {
                        ViewGroupUtils.suppressLayout(parent, false);
                    }

                    @Override // androidx.transition.Transition.TransitionListener, androidx.transition.TransitionListenerAdapter
                    public void onTransitionResume(Transition transition) {
                        ViewGroupUtils.suppressLayout(parent, true);
                    }
                });
            }
            return anim;
        }
        int startX = ((Integer) startValues.values.get(PROPNAME_WINDOW_X)).intValue();
        int startY = ((Integer) startValues.values.get(PROPNAME_WINDOW_Y)).intValue();
        int endX = ((Integer) endValues.values.get(PROPNAME_WINDOW_X)).intValue();
        int endY = ((Integer) endValues.values.get(PROPNAME_WINDOW_Y)).intValue();
        if (startX == endX && startY == endY) {
            return null;
        }
        sceneRoot.getLocationInWindow(this.mTempLocation);
        Bitmap bitmap = Bitmap.createBitmap(view2.getWidth(), view2.getHeight(), Bitmap.Config.ARGB_8888);
        view2.draw(new Canvas(bitmap));
        final BitmapDrawable drawable = new BitmapDrawable(bitmap);
        final float transitionAlpha = ViewUtils.getTransitionAlpha(view2);
        ViewUtils.setTransitionAlpha(view2, 0.0f);
        ViewUtils.getOverlay(sceneRoot).add(drawable);
        PathMotion pathMotion = getPathMotion();
        int[] iArr = this.mTempLocation;
        ObjectAnimator anim2 = ObjectAnimator.ofPropertyValuesHolder(drawable, PropertyValuesHolderUtils.ofPointF(DRAWABLE_ORIGIN_PROPERTY, pathMotion.getPath((float) (startX - iArr[0]), (float) (startY - iArr[1]), (float) (endX - iArr[0]), (float) (endY - iArr[1]))));
        anim2.addListener(new AnimatorListenerAdapter() {
            /* class androidx.transition.ChangeBounds.AnonymousClass10 */

            public void onAnimationEnd(Animator animation) {
                ViewUtils.getOverlay(sceneRoot).remove(drawable);
                ViewUtils.setTransitionAlpha(view2, transitionAlpha);
            }
        });
        return anim2;
    }

    /* access modifiers changed from: private */
    public static class ViewBounds {
        private int mBottom;
        private int mBottomRightCalls;
        private int mLeft;
        private int mRight;
        private int mTop;
        private int mTopLeftCalls;
        private View mView;

        ViewBounds(View view) {
            this.mView = view;
        }

        /* access modifiers changed from: package-private */
        public void setTopLeft(PointF topLeft) {
            this.mLeft = Math.round(topLeft.x);
            this.mTop = Math.round(topLeft.y);
            int i = this.mTopLeftCalls + 1;
            this.mTopLeftCalls = i;
            if (i == this.mBottomRightCalls) {
                setLeftTopRightBottom();
            }
        }

        /* access modifiers changed from: package-private */
        public void setBottomRight(PointF bottomRight) {
            this.mRight = Math.round(bottomRight.x);
            this.mBottom = Math.round(bottomRight.y);
            int i = this.mBottomRightCalls + 1;
            this.mBottomRightCalls = i;
            if (this.mTopLeftCalls == i) {
                setLeftTopRightBottom();
            }
        }

        private void setLeftTopRightBottom() {
            ViewUtils.setLeftTopRightBottom(this.mView, this.mLeft, this.mTop, this.mRight, this.mBottom);
            this.mTopLeftCalls = 0;
            this.mBottomRightCalls = 0;
        }
    }
}
