package androidx.navigation;

import androidx.navigation.Navigator;
import java.util.HashMap;
import java.util.Map;

public class NavigatorProvider {
    private static final HashMap<Class, String> sAnnotationNames = new HashMap<>();
    private final HashMap<String, Navigator<? extends NavDestination>> mNavigators = new HashMap<>();

    private static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    static String getNameForNavigator(Class<? extends Navigator> navigatorClass) {
        String name = sAnnotationNames.get(navigatorClass);
        if (name == null) {
            Navigator.Name annotation = (Navigator.Name) navigatorClass.getAnnotation(Navigator.Name.class);
            name = annotation != null ? annotation.value() : null;
            if (validateName(name)) {
                sAnnotationNames.put(navigatorClass, name);
            } else {
                throw new IllegalArgumentException("No @Navigator.Name annotation found for " + navigatorClass.getSimpleName());
            }
        }
        return name;
    }

    public final <T extends Navigator<?>> T getNavigator(Class<T> navigatorClass) {
        return (T) getNavigator(getNameForNavigator(navigatorClass));
    }

    public <T extends Navigator<?>> T getNavigator(String name) {
        if (validateName(name)) {
            Navigator<? extends NavDestination> navigator = this.mNavigators.get(name);
            if (navigator != null) {
                return navigator;
            }
            throw new IllegalStateException("Could not find Navigator with name \"" + name + "\". You must call NavController.addNavigator() for each navigation type.");
        }
        throw new IllegalArgumentException("navigator name cannot be an empty string");
    }

    public final Navigator<? extends NavDestination> addNavigator(Navigator<? extends NavDestination> navigator) {
        return addNavigator(getNameForNavigator(navigator.getClass()), navigator);
    }

    public Navigator<? extends NavDestination> addNavigator(String name, Navigator<? extends NavDestination> navigator) {
        if (validateName(name)) {
            return this.mNavigators.put(name, navigator);
        }
        throw new IllegalArgumentException("navigator name cannot be an empty string");
    }

    /* access modifiers changed from: package-private */
    public Map<String, Navigator<? extends NavDestination>> getNavigators() {
        return this.mNavigators;
    }
}
