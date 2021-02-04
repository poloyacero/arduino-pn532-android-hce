package androidx.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import androidx.core.app.ActivityCompat;
import java.lang.ref.WeakReference;

public final class Navigation {
    private Navigation() {
    }

    public static NavController findNavController(Activity activity, int viewId) {
        NavController navController = findViewNavController(ActivityCompat.requireViewById(activity, viewId));
        if (navController != null) {
            return navController;
        }
        throw new IllegalStateException("Activity " + activity + " does not have a NavController set on " + viewId);
    }

    public static NavController findNavController(View view) {
        NavController navController = findViewNavController(view);
        if (navController != null) {
            return navController;
        }
        throw new IllegalStateException("View " + view + " does not have a NavController set");
    }

    public static View.OnClickListener createNavigateOnClickListener(int resId) {
        return createNavigateOnClickListener(resId, null);
    }

    public static View.OnClickListener createNavigateOnClickListener(final int resId, final Bundle args) {
        return new View.OnClickListener() {
            /* class androidx.navigation.Navigation.AnonymousClass1 */

            public void onClick(View view) {
                Navigation.findNavController(view).navigate(resId, args);
            }
        };
    }

    public static void setViewNavController(View view, NavController controller) {
        view.setTag(R.id.nav_controller_view_tag, controller);
    }

    private static NavController findViewNavController(View view) {
        while (true) {
            View view2 = null;
            if (view == null) {
                return null;
            }
            NavController controller = getViewNavController(view);
            if (controller != null) {
                return controller;
            }
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                view2 = (View) parent;
            }
            view = view2;
        }
    }

    private static NavController getViewNavController(View view) {
        Object tag = view.getTag(R.id.nav_controller_view_tag);
        if (tag instanceof WeakReference) {
            return (NavController) ((WeakReference) tag).get();
        }
        if (tag instanceof NavController) {
            return (NavController) tag;
        }
        return null;
    }
}
