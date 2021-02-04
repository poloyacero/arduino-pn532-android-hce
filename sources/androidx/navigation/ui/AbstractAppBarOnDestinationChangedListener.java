package androidx.navigation.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import com.example.application.BuildConfig;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class AbstractAppBarOnDestinationChangedListener implements NavController.OnDestinationChangedListener {
    private ValueAnimator mAnimator;
    private DrawerArrowDrawable mArrowDrawable;
    private final Context mContext;
    private final WeakReference<DrawerLayout> mDrawerLayoutWeakReference;
    private final Set<Integer> mTopLevelDestinations;

    /* access modifiers changed from: protected */
    public abstract void setNavigationIcon(Drawable drawable, int i);

    /* access modifiers changed from: protected */
    public abstract void setTitle(CharSequence charSequence);

    AbstractAppBarOnDestinationChangedListener(Context context, AppBarConfiguration configuration) {
        this.mContext = context;
        this.mTopLevelDestinations = configuration.getTopLevelDestinations();
        DrawerLayout drawerLayout = configuration.getDrawerLayout();
        if (drawerLayout != null) {
            this.mDrawerLayoutWeakReference = new WeakReference<>(drawerLayout);
        } else {
            this.mDrawerLayoutWeakReference = null;
        }
    }

    @Override // androidx.navigation.NavController.OnDestinationChangedListener
    public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
        WeakReference<DrawerLayout> weakReference = this.mDrawerLayoutWeakReference;
        DrawerLayout drawerLayout = weakReference != null ? weakReference.get() : null;
        if (this.mDrawerLayoutWeakReference == null || drawerLayout != null) {
            CharSequence label = destination.getLabel();
            boolean z = true;
            if (!TextUtils.isEmpty(label)) {
                StringBuffer title = new StringBuffer();
                Matcher matcher = Pattern.compile("\\{(.+?)\\}").matcher(label);
                while (matcher.find()) {
                    String argName = matcher.group(1);
                    if (arguments == null || !arguments.containsKey(argName)) {
                        throw new IllegalArgumentException("Could not find " + argName + " in " + arguments + " to fill label " + ((Object) label));
                    }
                    matcher.appendReplacement(title, BuildConfig.FLAVOR);
                    title.append(arguments.get(argName).toString());
                }
                matcher.appendTail(title);
                setTitle(title);
            }
            boolean isTopLevelDestination = NavigationUI.matchDestinations(destination, this.mTopLevelDestinations);
            if (drawerLayout != null || !isTopLevelDestination) {
                if (drawerLayout == null || !isTopLevelDestination) {
                    z = false;
                }
                setActionBarUpIndicator(z);
                return;
            }
            setNavigationIcon(null, 0);
            return;
        }
        controller.removeOnDestinationChangedListener(this);
    }

    private void setActionBarUpIndicator(boolean showAsDrawerIndicator) {
        boolean animate = true;
        if (this.mArrowDrawable == null) {
            this.mArrowDrawable = new DrawerArrowDrawable(this.mContext);
            animate = false;
        }
        setNavigationIcon(this.mArrowDrawable, showAsDrawerIndicator ? R.string.nav_app_bar_open_drawer_description : R.string.nav_app_bar_navigate_up_description);
        float endValue = showAsDrawerIndicator ? 0.0f : 1.0f;
        if (animate) {
            float startValue = this.mArrowDrawable.getProgress();
            ValueAnimator valueAnimator = this.mAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mArrowDrawable, NotificationCompat.CATEGORY_PROGRESS, startValue, endValue);
            this.mAnimator = ofFloat;
            ofFloat.start();
            return;
        }
        this.mArrowDrawable.setProgress(endValue);
    }
}
