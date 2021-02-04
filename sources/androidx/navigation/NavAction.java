package androidx.navigation;

import android.os.Bundle;

public final class NavAction {
    private Bundle mDefaultArguments;
    private final int mDestinationId;
    private NavOptions mNavOptions;

    public NavAction(int destinationId) {
        this(destinationId, null);
    }

    public NavAction(int destinationId, NavOptions navOptions) {
        this(destinationId, navOptions, null);
    }

    public NavAction(int destinationId, NavOptions navOptions, Bundle defaultArgs) {
        this.mDestinationId = destinationId;
        this.mNavOptions = navOptions;
        this.mDefaultArguments = defaultArgs;
    }

    public int getDestinationId() {
        return this.mDestinationId;
    }

    public void setNavOptions(NavOptions navOptions) {
        this.mNavOptions = navOptions;
    }

    public NavOptions getNavOptions() {
        return this.mNavOptions;
    }

    public Bundle getDefaultArguments() {
        return this.mDefaultArguments;
    }

    public void setDefaultArguments(Bundle defaultArgs) {
        this.mDefaultArguments = defaultArgs;
    }
}
