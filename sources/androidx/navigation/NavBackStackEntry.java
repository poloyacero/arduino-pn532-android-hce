package androidx.navigation;

import android.os.Bundle;

final class NavBackStackEntry {
    private final Bundle mArgs;
    private final NavDestination mDestination;

    NavBackStackEntry(NavDestination destination, Bundle args) {
        this.mDestination = destination;
        this.mArgs = args;
    }

    public NavDestination getDestination() {
        return this.mDestination;
    }

    public Bundle getArguments() {
        return this.mArgs;
    }
}
