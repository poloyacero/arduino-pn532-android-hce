package androidx.navigation;

import android.os.Bundle;
import androidx.navigation.Navigator;
import java.util.ArrayDeque;
import java.util.Iterator;

@Navigator.Name("navigation")
public class NavGraphNavigator extends Navigator<NavGraph> {
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-graph:navigator:backStackIds";
    private ArrayDeque<Integer> mBackStack = new ArrayDeque<>();
    private final NavigatorProvider mNavigatorProvider;

    public NavGraphNavigator(NavigatorProvider navigatorProvider) {
        this.mNavigatorProvider = navigatorProvider;
    }

    @Override // androidx.navigation.Navigator
    public NavGraph createDestination() {
        return new NavGraph(this);
    }

    public NavDestination navigate(NavGraph destination, Bundle args, NavOptions navOptions, Navigator.Extras navigatorExtras) {
        int startId = destination.getStartDestination();
        if (startId != 0) {
            NavDestination startDestination = destination.findNode(startId, false);
            if (startDestination != null) {
                if (navOptions == null || !navOptions.shouldLaunchSingleTop() || !isAlreadyTop(destination)) {
                    this.mBackStack.add(Integer.valueOf(destination.getId()));
                }
                return this.mNavigatorProvider.getNavigator(startDestination.getNavigatorName()).navigate(startDestination, startDestination.addInDefaultArgs(args), navOptions, navigatorExtras);
            }
            String dest = destination.getStartDestDisplayName();
            throw new IllegalArgumentException("navigation destination " + dest + " is not a direct child of this NavGraph");
        }
        throw new IllegalStateException("no start destination defined via app:startDestination for " + destination.getDisplayName());
    }

    private boolean isAlreadyTop(NavGraph destination) {
        if (this.mBackStack.isEmpty()) {
            return false;
        }
        int topDestId = this.mBackStack.peekLast().intValue();
        NavGraph current = destination;
        while (current.getId() != topDestId) {
            NavDestination startDestination = current.findNode(current.getStartDestination());
            if (!(startDestination instanceof NavGraph)) {
                return false;
            }
            current = (NavGraph) startDestination;
        }
        return true;
    }

    @Override // androidx.navigation.Navigator
    public boolean popBackStack() {
        return this.mBackStack.pollLast() != null;
    }

    @Override // androidx.navigation.Navigator
    public Bundle onSaveState() {
        Bundle b = new Bundle();
        int[] backStack = new int[this.mBackStack.size()];
        int index = 0;
        Iterator<Integer> it = this.mBackStack.iterator();
        while (it.hasNext()) {
            backStack[index] = it.next().intValue();
            index++;
        }
        b.putIntArray(KEY_BACK_STACK_IDS, backStack);
        return b;
    }

    @Override // androidx.navigation.Navigator
    public void onRestoreState(Bundle savedState) {
        int[] backStack;
        if (!(savedState == null || (backStack = savedState.getIntArray(KEY_BACK_STACK_IDS)) == null)) {
            this.mBackStack.clear();
            for (int destId : backStack) {
                this.mBackStack.add(Integer.valueOf(destId));
            }
        }
    }
}
