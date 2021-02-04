package androidx.navigation.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.NavigatorProvider;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Navigator.Name("fragment")
public class FragmentNavigator extends Navigator<Destination> {
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-fragment:navigator:backStackIds";
    private static final String TAG = "FragmentNavigator";
    ArrayDeque<Integer> mBackStack = new ArrayDeque<>();
    private final int mContainerId;
    private final Context mContext;
    final FragmentManager mFragmentManager;
    boolean mIsPendingBackStackOperation = false;
    private final FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        /* class androidx.navigation.fragment.FragmentNavigator.AnonymousClass1 */

        @Override // androidx.fragment.app.FragmentManager.OnBackStackChangedListener
        public void onBackStackChanged() {
            if (FragmentNavigator.this.mIsPendingBackStackOperation) {
                FragmentNavigator fragmentNavigator = FragmentNavigator.this;
                fragmentNavigator.mIsPendingBackStackOperation = !fragmentNavigator.isBackStackEqual();
                return;
            }
            int newCount = FragmentNavigator.this.mFragmentManager.getBackStackEntryCount() + 1;
            if (newCount < FragmentNavigator.this.mBackStack.size()) {
                while (FragmentNavigator.this.mBackStack.size() > newCount) {
                    FragmentNavigator.this.mBackStack.removeLast();
                }
                FragmentNavigator.this.dispatchOnNavigatorBackPress();
            }
        }
    };

    public FragmentNavigator(Context context, FragmentManager manager, int containerId) {
        this.mContext = context;
        this.mFragmentManager = manager;
        this.mContainerId = containerId;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.navigation.Navigator
    public void onBackPressAdded() {
        this.mFragmentManager.addOnBackStackChangedListener(this.mOnBackStackChangedListener);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.navigation.Navigator
    public void onBackPressRemoved() {
        this.mFragmentManager.removeOnBackStackChangedListener(this.mOnBackStackChangedListener);
    }

    @Override // androidx.navigation.Navigator
    public boolean popBackStack() {
        if (this.mBackStack.isEmpty()) {
            return false;
        }
        if (this.mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state");
            return false;
        }
        if (this.mFragmentManager.getBackStackEntryCount() > 0) {
            this.mFragmentManager.popBackStack(generateBackStackName(this.mBackStack.size(), this.mBackStack.peekLast().intValue()), 1);
            this.mIsPendingBackStackOperation = true;
        }
        this.mBackStack.removeLast();
        return true;
    }

    @Override // androidx.navigation.Navigator
    public Destination createDestination() {
        return new Destination(this);
    }

    public Fragment instantiateFragment(Context context, FragmentManager fragmentManager, String className, Bundle args) {
        return Fragment.instantiate(context, className, args);
    }

    public NavDestination navigate(Destination destination, Bundle args, NavOptions navOptions, Navigator.Extras navigatorExtras) {
        boolean isAdded;
        if (this.mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state");
            return null;
        }
        String className = destination.getClassName();
        boolean isSingleTopReplacement = false;
        if (className.charAt(0) == '.') {
            className = this.mContext.getPackageName() + className;
        }
        Fragment frag = instantiateFragment(this.mContext, this.mFragmentManager, className, args);
        frag.setArguments(args);
        FragmentTransaction ft = this.mFragmentManager.beginTransaction();
        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (!(enterAnim == -1 && exitAnim == -1 && popEnterAnim == -1 && popExitAnim == -1)) {
            ft.setCustomAnimations(enterAnim != -1 ? enterAnim : 0, exitAnim != -1 ? exitAnim : 0, popEnterAnim != -1 ? popEnterAnim : 0, popExitAnim != -1 ? popExitAnim : 0);
        }
        ft.replace(this.mContainerId, frag);
        ft.setPrimaryNavigationFragment(frag);
        int destId = destination.getId();
        boolean initialNavigation = this.mBackStack.isEmpty();
        if (navOptions != null && !initialNavigation && navOptions.shouldLaunchSingleTop() && this.mBackStack.peekLast().intValue() == destId) {
            isSingleTopReplacement = true;
        }
        if (initialNavigation) {
            isAdded = true;
        } else if (isSingleTopReplacement) {
            if (this.mBackStack.size() > 1) {
                this.mFragmentManager.popBackStack(generateBackStackName(this.mBackStack.size(), this.mBackStack.peekLast().intValue()), 1);
                ft.addToBackStack(generateBackStackName(this.mBackStack.size(), destId));
                this.mIsPendingBackStackOperation = true;
            }
            isAdded = false;
        } else {
            ft.addToBackStack(generateBackStackName(this.mBackStack.size() + 1, destId));
            this.mIsPendingBackStackOperation = true;
            isAdded = true;
        }
        if (navigatorExtras instanceof Extras) {
            for (Map.Entry<View, String> sharedElement : ((Extras) navigatorExtras).getSharedElements().entrySet()) {
                ft.addSharedElement(sharedElement.getKey(), sharedElement.getValue());
            }
        }
        ft.setReorderingAllowed(true);
        ft.commit();
        if (!isAdded) {
            return null;
        }
        this.mBackStack.add(Integer.valueOf(destId));
        return destination;
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

    private String generateBackStackName(int backStackIndex, int destId) {
        return backStackIndex + "-" + destId;
    }

    private int getDestId(String backStackName) {
        String[] split = backStackName != null ? backStackName.split("-") : new String[0];
        if (split.length == 2) {
            try {
                Integer.parseInt(split[0]);
                return Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.");
            }
        } else {
            throw new IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isBackStackEqual() {
        int fragmentBackStackCount = this.mFragmentManager.getBackStackEntryCount();
        if (this.mBackStack.size() != fragmentBackStackCount + 1) {
            return false;
        }
        Iterator<Integer> backStackIterator = this.mBackStack.descendingIterator();
        int fragmentBackStackIndex = fragmentBackStackCount - 1;
        while (backStackIterator.hasNext() && fragmentBackStackIndex >= 0) {
            try {
                int fragmentBackStackIndex2 = fragmentBackStackIndex - 1;
                try {
                    if (backStackIterator.next().intValue() != getDestId(this.mFragmentManager.getBackStackEntryAt(fragmentBackStackIndex).getName())) {
                        return false;
                    }
                    fragmentBackStackIndex = fragmentBackStackIndex2;
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.");
                }
            } catch (NumberFormatException e2) {
                throw new IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.");
            }
        }
        return true;
    }

    public static class Destination extends NavDestination {
        private String mClassName;

        public Destination(NavigatorProvider navigatorProvider) {
            this(navigatorProvider.getNavigator(FragmentNavigator.class));
        }

        public Destination(Navigator<? extends Destination> fragmentNavigator) {
            super(fragmentNavigator);
        }

        @Override // androidx.navigation.NavDestination
        public void onInflate(Context context, AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.FragmentNavigator);
            String className = a.getString(R.styleable.FragmentNavigator_android_name);
            if (className != null) {
                setClassName(className);
            }
            a.recycle();
        }

        public final Destination setClassName(String className) {
            this.mClassName = className;
            return this;
        }

        public final String getClassName() {
            String str = this.mClassName;
            if (str != null) {
                return str;
            }
            throw new IllegalStateException("Fragment class was not set");
        }
    }

    public static final class Extras implements Navigator.Extras {
        private final LinkedHashMap<View, String> mSharedElements;

        Extras(Map<View, String> sharedElements) {
            LinkedHashMap<View, String> linkedHashMap = new LinkedHashMap<>();
            this.mSharedElements = linkedHashMap;
            linkedHashMap.putAll(sharedElements);
        }

        public Map<View, String> getSharedElements() {
            return Collections.unmodifiableMap(this.mSharedElements);
        }

        public static final class Builder {
            private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap<>();

            public Builder addSharedElements(Map<View, String> sharedElements) {
                for (Map.Entry<View, String> sharedElement : sharedElements.entrySet()) {
                    View view = sharedElement.getKey();
                    String name = sharedElement.getValue();
                    if (!(view == null || name == null)) {
                        addSharedElement(view, name);
                    }
                }
                return this;
            }

            public Builder addSharedElement(View sharedElement, String name) {
                this.mSharedElements.put(sharedElement, name);
                return this;
            }

            public Extras build() {
                return new Extras(this.mSharedElements);
            }
        }
    }
}
