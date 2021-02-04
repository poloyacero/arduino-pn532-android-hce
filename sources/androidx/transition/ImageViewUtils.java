package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ImageViewUtils {
    private static final String TAG = "ImageViewUtils";
    private static Method sAnimateTransformMethod;
    private static boolean sAnimateTransformMethodFetched;

    static void startAnimateTransform(ImageView view) {
        if (Build.VERSION.SDK_INT < 21) {
            ImageView.ScaleType scaleType = view.getScaleType();
            view.setTag(R.id.save_scale_type, scaleType);
            if (scaleType == ImageView.ScaleType.MATRIX) {
                view.setTag(R.id.save_image_matrix, view.getImageMatrix());
            } else {
                view.setScaleType(ImageView.ScaleType.MATRIX);
            }
            view.setImageMatrix(MatrixUtils.IDENTITY_MATRIX);
        }
    }

    static void animateTransform(ImageView view, Matrix matrix) {
        if (Build.VERSION.SDK_INT < 21) {
            view.setImageMatrix(matrix);
            return;
        }
        fetchAnimateTransformMethod();
        Method method = sAnimateTransformMethod;
        if (method != null) {
            try {
                method.invoke(view, matrix);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
                throw new RuntimeException(e2.getCause());
            }
        }
    }

    private static void fetchAnimateTransformMethod() {
        if (!sAnimateTransformMethodFetched) {
            try {
                Method declaredMethod = ImageView.class.getDeclaredMethod("animateTransform", Matrix.class);
                sAnimateTransformMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to retrieve animateTransform method", e);
            }
            sAnimateTransformMethodFetched = true;
        }
    }

    static void reserveEndAnimateTransform(final ImageView view, Animator animator) {
        if (Build.VERSION.SDK_INT < 21) {
            animator.addListener(new AnimatorListenerAdapter() {
                /* class androidx.transition.ImageViewUtils.AnonymousClass1 */

                public void onAnimationEnd(Animator animation) {
                    ImageView.ScaleType scaleType = (ImageView.ScaleType) view.getTag(R.id.save_scale_type);
                    view.setScaleType(scaleType);
                    view.setTag(R.id.save_scale_type, null);
                    if (scaleType == ImageView.ScaleType.MATRIX) {
                        ImageView imageView = view;
                        imageView.setImageMatrix((Matrix) imageView.getTag(R.id.save_image_matrix));
                        view.setTag(R.id.save_image_matrix, null);
                    }
                    animation.removeListener(this);
                }
            });
        }
    }

    private ImageViewUtils() {
    }
}
