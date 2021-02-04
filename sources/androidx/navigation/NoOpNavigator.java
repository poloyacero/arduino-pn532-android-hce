package androidx.navigation;

import android.os.Bundle;
import androidx.navigation.Navigator;

@Navigator.Name("NoOp")
class NoOpNavigator extends Navigator<NavDestination> {
    NoOpNavigator() {
    }

    @Override // androidx.navigation.Navigator
    public NavDestination createDestination() {
        return new NavDestination(this);
    }

    @Override // androidx.navigation.Navigator
    public NavDestination navigate(NavDestination destination, Bundle args, NavOptions navOptions, Navigator.Extras navigatorExtras) {
        return destination;
    }

    @Override // androidx.navigation.Navigator
    public boolean popBackStack() {
        return true;
    }
}
