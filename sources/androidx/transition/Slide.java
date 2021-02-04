package androidx.transition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.core.content.res.TypedArrayUtils;
import androidx.core.view.ViewCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.xmlpull.v1.XmlPullParser;

public class Slide extends Visibility {
    private static final String PROPNAME_SCREEN_POSITION = "android:slide:screenPosition";
    private static final TimeInterpolator sAccelerate = new AccelerateInterpolator();
    private static final CalculateSlide sCalculateBottom = new CalculateSlideVertical() {
        /* class androidx.transition.Slide.AnonymousClass6 */

        @Override // androidx.transition.Slide.CalculateSlide
        public float getGoneY(ViewGroup sceneRoot, View view) {
            return view.getTranslationY() + ((float) sceneRoot.getHeight());
        }
    };
    private static final CalculateSlide sCalculateEnd = new CalculateSlideHorizontal() {
        /* class androidx.transition.Slide.AnonymousClass5 */

        @Override // androidx.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            boolean isRtl = true;
            if (ViewCompat.getLayoutDirection(sceneRoot) != 1) {
                isRtl = false;
            }
            if (isRtl) {
                return view.getTranslationX() - ((float) sceneRoot.getWidth());
            }
            return view.getTranslationX() + ((float) sceneRoot.getWidth());
        }
    };
    private static final CalculateSlide sCalculateLeft = new CalculateSlideHorizontal() {
        /* class androidx.transition.Slide.AnonymousClass1 */

        @Override // androidx.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            return view.getTranslationX() - ((float) sceneRoot.getWidth());
        }
    };
    private static final CalculateSlide sCalculateRight = new CalculateSlideHorizontal() {
        /* class androidx.transition.Slide.AnonymousClass4 */

        @Override // androidx.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            return view.getTranslationX() + ((float) sceneRoot.getWidth());
        }
    };
    private static final CalculateSlide sCalculateStart = new CalculateSlideHorizontal() {
        /* class androidx.transition.Slide.AnonymousClass2 */

        @Override // androidx.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            boolean isRtl = true;
            if (ViewCompat.getLayoutDirection(sceneRoot) != 1) {
                isRtl = false;
            }
            if (isRtl) {
                return view.getTranslationX() + ((float) sceneRoot.getWidth());
            }
            return view.getTranslationX() - ((float) sceneRoot.getWidth());
        }
    };
    private static final CalculateSlide sCalculateTop = new CalculateSlideVertical() {
        /* class androidx.transition.Slide.AnonymousClass3 */

        @Override // androidx.transition.Slide.CalculateSlide
        public float getGoneY(ViewGroup sceneRoot, View view) {
            return view.getTranslationY() - ((float) sceneRoot.getHeight());
        }
    };
    private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
    private CalculateSlide mSlideCalculator = sCalculateBottom;
    private int mSlideEdge = 80;

    /* access modifiers changed from: private */
    public interface CalculateSlide {
        float getGoneX(ViewGroup viewGroup, View view);

        float getGoneY(ViewGroup viewGroup, View view);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface GravityFlag {
    }

    private static abstract class CalculateSlideHorizontal implements CalculateSlide {
        private CalculateSlideHorizontal() {
        }

        @Override // androidx.transition.Slide.CalculateSlide
        public float getGoneY(ViewGroup sceneRoot, View view) {
            return view.getTranslationY();
        }
    }

    private static abstract class CalculateSlideVertical implements CalculateSlide {
        private CalculateSlideVertical() {
        }

        @Override // androidx.transition.Slide.CalculateSlide
        public float getGoneX(ViewGroup sceneRoot, View view) {
            return view.getTranslationX();
        }
    }

    public Slide() {
        setSlideEdge(80);
    }

    public Slide(int slideEdge) {
        setSlideEdge(slideEdge);
    }

    public Slide(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.SLIDE);
        int edge = TypedArrayUtils.getNamedInt(a, (XmlPullParser) attrs, "slideEdge", 0, 80);
        a.recycle();
        setSlideEdge(edge);
    }

    private void captureValues(TransitionValues transitionValues) {
        int[] position = new int[2];
        transitionValues.view.getLocationOnScreen(position);
        transitionValues.values.put(PROPNAME_SCREEN_POSITION, position);
    }

    @Override // androidx.transition.Transition, androidx.transition.Visibility
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override // androidx.transition.Transition, androidx.transition.Visibility
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    public void setSlideEdge(int slideEdge) {
        if (slideEdge == 3) {
            this.mSlideCalculator = sCalculateLeft;
        } else if (slideEdge == 5) {
            this.mSlideCalculator = sCalculateRight;
        } else if (slideEdge == 48) {
            this.mSlideCalculator = sCalculateTop;
        } else if (slideEdge == 80) {
            this.mSlideCalculator = sCalculateBottom;
        } else if (slideEdge == 8388611) {
            this.mSlideCalculator = sCalculateStart;
        } else if (slideEdge == 8388613) {
            this.mSlideCalculator = sCalculateEnd;
        } else {
            throw new IllegalArgumentException("Invalid slide direction");
        }
        this.mSlideEdge = slideEdge;
        SidePropagation propagation = new SidePropagation();
        propagation.setSide(slideEdge);
        setPropagation(propagation);
    }

    public int getSlideEdge() {
        return this.mSlideEdge;
    }

    @Override // androidx.transition.Visibility
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (endValues == null) {
            return null;
        }
        int[] position = (int[]) endValues.values.get(PROPNAME_SCREEN_POSITION);
        float endX = view.getTranslationX();
        float endY = view.getTranslationY();
        return TranslationAnimationCreator.createAnimation(view, endValues, position[0], position[1], this.mSlideCalculator.getGoneX(sceneRoot, view), this.mSlideCalculator.getGoneY(sceneRoot, view), endX, endY, sDecelerate);
    }

    @Override // androidx.transition.Visibility
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null) {
            return null;
        }
        int[] position = (int[]) startValues.values.get(PROPNAME_SCREEN_POSITION);
        return TranslationAnimationCreator.createAnimation(view, startValues, position[0], position[1], view.getTranslationX(), view.getTranslationY(), this.mSlideCalculator.getGoneX(sceneRoot, view), this.mSlideCalculator.getGoneY(sceneRoot, view), sAccelerate);
    }
}
