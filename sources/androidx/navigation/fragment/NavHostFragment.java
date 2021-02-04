package androidx.navigation.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

public class NavHostFragment extends Fragment implements NavHost {
    private static final String KEY_DEFAULT_NAV_HOST = "android-support-nav:fragment:defaultHost";
    private static final String KEY_GRAPH_ID = "android-support-nav:fragment:graphId";
    private static final String KEY_NAV_CONTROLLER_STATE = "android-support-nav:fragment:navControllerState";
    private static final String KEY_START_DESTINATION_ARGS = "android-support-nav:fragment:startDestinationArgs";
    private boolean mDefaultNavHost;
    private int mGraphId;
    private NavController mNavController;

    public static NavController findNavController(Fragment fragment) {
        for (Fragment findFragment = fragment; findFragment != null; findFragment = findFragment.getParentFragment()) {
            if (findFragment instanceof NavHostFragment) {
                return ((NavHostFragment) findFragment).getNavController();
            }
            Fragment primaryNavFragment = findFragment.requireFragmentManager().getPrimaryNavigationFragment();
            if (primaryNavFragment instanceof NavHostFragment) {
                return ((NavHostFragment) primaryNavFragment).getNavController();
            }
        }
        View view = fragment.getView();
        if (view != null) {
            return Navigation.findNavController(view);
        }
        throw new IllegalStateException("Fragment " + fragment + " does not have a NavController set");
    }

    public static NavHostFragment create(int graphResId) {
        return create(graphResId, null);
    }

    public static NavHostFragment create(int graphResId, Bundle startDestinationArgs) {
        Bundle b = null;
        if (graphResId != 0) {
            b = new Bundle();
            b.putInt(KEY_GRAPH_ID, graphResId);
        }
        if (startDestinationArgs != null) {
            if (b == null) {
                b = new Bundle();
            }
            b.putBundle(KEY_START_DESTINATION_ARGS, startDestinationArgs);
        }
        NavHostFragment result = new NavHostFragment();
        if (b != null) {
            result.setArguments(b);
        }
        return result;
    }

    @Override // androidx.navigation.NavHost
    public final NavController getNavController() {
        NavController navController = this.mNavController;
        if (navController != null) {
            return navController;
        }
        throw new IllegalStateException("NavController is not available before onCreate()");
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (this.mDefaultNavHost) {
            requireFragmentManager().beginTransaction().setPrimaryNavigationFragment(this).commit();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavController navController = new NavController(requireContext());
        this.mNavController = navController;
        navController.getNavigatorProvider().addNavigator(createFragmentNavigator());
        Bundle navState = null;
        int graphId = 0;
        if (savedInstanceState != null) {
            navState = savedInstanceState.getBundle(KEY_NAV_CONTROLLER_STATE);
            if (savedInstanceState.getBoolean(KEY_DEFAULT_NAV_HOST, false)) {
                this.mDefaultNavHost = true;
                requireFragmentManager().beginTransaction().setPrimaryNavigationFragment(this).commit();
            }
        }
        if (navState != null) {
            this.mNavController.restoreState(navState);
        }
        int i = this.mGraphId;
        if (i != 0) {
            this.mNavController.setGraph(i);
            return;
        }
        Bundle args = getArguments();
        if (args != null) {
            graphId = args.getInt(KEY_GRAPH_ID);
        }
        Bundle startDestinationArgs = args != null ? args.getBundle(KEY_START_DESTINATION_ARGS) : null;
        if (graphId != 0) {
            this.mNavController.setGraph(graphId, startDestinationArgs);
        }
    }

    /* access modifiers changed from: protected */
    public Navigator<? extends FragmentNavigator.Destination> createFragmentNavigator() {
        return new FragmentNavigator(requireContext(), getChildFragmentManager(), getId());
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(inflater.getContext());
        frameLayout.setId(getId());
        return frameLayout;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view instanceof ViewGroup) {
            Navigation.setViewNavController(view.getParent() != null ? (View) view.getParent() : view, this.mNavController);
            return;
        }
        throw new IllegalStateException("created host view " + view + " is not a ViewGroup");
    }

    @Override // androidx.fragment.app.Fragment
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment);
        int graphId = a.getResourceId(R.styleable.NavHostFragment_navGraph, 0);
        boolean defaultHost = a.getBoolean(R.styleable.NavHostFragment_defaultNavHost, false);
        if (graphId != 0) {
            this.mGraphId = graphId;
        }
        if (defaultHost) {
            this.mDefaultNavHost = true;
        }
        a.recycle();
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle navState = this.mNavController.saveState();
        if (navState != null) {
            outState.putBundle(KEY_NAV_CONTROLLER_STATE, navState);
        }
        if (this.mDefaultNavHost) {
            outState.putBoolean(KEY_DEFAULT_NAV_HOST, true);
        }
    }
}
