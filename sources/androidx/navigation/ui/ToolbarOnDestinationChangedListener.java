package androidx.navigation.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import java.lang.ref.WeakReference;

/* access modifiers changed from: package-private */
public class ToolbarOnDestinationChangedListener extends AbstractAppBarOnDestinationChangedListener {
    private final WeakReference<Toolbar> mToolbarWeakReference;

    ToolbarOnDestinationChangedListener(Toolbar toolbar, AppBarConfiguration configuration) {
        super(toolbar.getContext(), configuration);
        this.mToolbarWeakReference = new WeakReference<>(toolbar);
    }

    @Override // androidx.navigation.NavController.OnDestinationChangedListener, androidx.navigation.ui.AbstractAppBarOnDestinationChangedListener
    public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
        if (this.mToolbarWeakReference.get() == null) {
            controller.removeOnDestinationChangedListener(this);
        } else {
            super.onDestinationChanged(controller, destination, arguments);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.navigation.ui.AbstractAppBarOnDestinationChangedListener
    public void setTitle(CharSequence title) {
        this.mToolbarWeakReference.get().setTitle(title);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.navigation.ui.AbstractAppBarOnDestinationChangedListener
    public void setNavigationIcon(Drawable icon, int contentDescription) {
        Toolbar toolbar = this.mToolbarWeakReference.get();
        if (toolbar != null) {
            toolbar.setNavigationIcon(icon);
            toolbar.setNavigationContentDescription(contentDescription);
        }
    }
}
