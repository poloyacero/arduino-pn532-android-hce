package com.google.android.material.bottomnavigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;
import com.google.android.material.R;
import com.google.android.material.internal.ThemeEnforcement;

public class BottomNavigationView extends FrameLayout {
    private static final int MENU_PRESENTER_ID = 1;
    private final MenuBuilder menu;
    private MenuInflater menuInflater;
    private final BottomNavigationMenuView menuView;
    private final BottomNavigationPresenter presenter;
    private OnNavigationItemReselectedListener reselectedListener;
    private OnNavigationItemSelectedListener selectedListener;

    public interface OnNavigationItemReselectedListener {
        void onNavigationItemReselected(MenuItem menuItem);
    }

    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(MenuItem menuItem);
    }

    public BottomNavigationView(Context context) {
        this(context, null);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.bottomNavigationStyle);
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.presenter = new BottomNavigationPresenter();
        this.menu = new BottomNavigationMenu(context);
        this.menuView = new BottomNavigationMenuView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = 17;
        this.menuView.setLayoutParams(params);
        this.presenter.setBottomNavigationMenuView(this.menuView);
        this.presenter.setId(1);
        this.menuView.setPresenter(this.presenter);
        this.menu.addMenuPresenter(this.presenter);
        this.presenter.initForMenu(getContext(), this.menu);
        TintTypedArray a = ThemeEnforcement.obtainTintedStyledAttributes(context, attrs, R.styleable.BottomNavigationView, defStyleAttr, R.style.Widget_Design_BottomNavigationView, R.styleable.BottomNavigationView_itemTextAppearanceInactive, R.styleable.BottomNavigationView_itemTextAppearanceActive);
        if (a.hasValue(R.styleable.BottomNavigationView_itemIconTint)) {
            this.menuView.setIconTintList(a.getColorStateList(R.styleable.BottomNavigationView_itemIconTint));
        } else {
            BottomNavigationMenuView bottomNavigationMenuView = this.menuView;
            bottomNavigationMenuView.setIconTintList(bottomNavigationMenuView.createDefaultColorStateList(16842808));
        }
        setItemIconSize(a.getDimensionPixelSize(R.styleable.BottomNavigationView_itemIconSize, getResources().getDimensionPixelSize(R.dimen.design_bottom_navigation_icon_size)));
        if (a.hasValue(R.styleable.BottomNavigationView_itemTextAppearanceInactive)) {
            setItemTextAppearanceInactive(a.getResourceId(R.styleable.BottomNavigationView_itemTextAppearanceInactive, 0));
        }
        if (a.hasValue(R.styleable.BottomNavigationView_itemTextAppearanceActive)) {
            setItemTextAppearanceActive(a.getResourceId(R.styleable.BottomNavigationView_itemTextAppearanceActive, 0));
        }
        if (a.hasValue(R.styleable.BottomNavigationView_itemTextColor)) {
            setItemTextColor(a.getColorStateList(R.styleable.BottomNavigationView_itemTextColor));
        }
        if (a.hasValue(R.styleable.BottomNavigationView_elevation)) {
            ViewCompat.setElevation(this, (float) a.getDimensionPixelSize(R.styleable.BottomNavigationView_elevation, 0));
        }
        setLabelVisibilityMode(a.getInteger(R.styleable.BottomNavigationView_labelVisibilityMode, -1));
        setItemHorizontalTranslationEnabled(a.getBoolean(R.styleable.BottomNavigationView_itemHorizontalTranslationEnabled, true));
        this.menuView.setItemBackgroundRes(a.getResourceId(R.styleable.BottomNavigationView_itemBackground, 0));
        if (a.hasValue(R.styleable.BottomNavigationView_menu)) {
            inflateMenu(a.getResourceId(R.styleable.BottomNavigationView_menu, 0));
        }
        a.recycle();
        addView(this.menuView, params);
        if (Build.VERSION.SDK_INT < 21) {
            addCompatibilityTopDivider(context);
        }
        this.menu.setCallback(new MenuBuilder.Callback() {
            /* class com.google.android.material.bottomnavigation.BottomNavigationView.AnonymousClass1 */

            @Override // androidx.appcompat.view.menu.MenuBuilder.Callback
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                if (BottomNavigationView.this.reselectedListener != null && item.getItemId() == BottomNavigationView.this.getSelectedItemId()) {
                    BottomNavigationView.this.reselectedListener.onNavigationItemReselected(item);
                    return true;
                } else if (BottomNavigationView.this.selectedListener == null || BottomNavigationView.this.selectedListener.onNavigationItemSelected(item)) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override // androidx.appcompat.view.menu.MenuBuilder.Callback
            public void onMenuModeChange(MenuBuilder menu) {
            }
        });
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        this.selectedListener = listener;
    }

    public void setOnNavigationItemReselectedListener(OnNavigationItemReselectedListener listener) {
        this.reselectedListener = listener;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void inflateMenu(int resId) {
        this.presenter.setUpdateSuspended(true);
        getMenuInflater().inflate(resId, this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(true);
    }

    public int getMaxItemCount() {
        return 5;
    }

    public ColorStateList getItemIconTintList() {
        return this.menuView.getIconTintList();
    }

    public void setItemIconTintList(ColorStateList tint) {
        this.menuView.setIconTintList(tint);
    }

    public void setItemIconSize(int iconSize) {
        this.menuView.setItemIconSize(iconSize);
    }

    public void setItemIconSizeRes(int iconSizeRes) {
        setItemIconSize(getResources().getDimensionPixelSize(iconSizeRes));
    }

    public int getItemIconSize() {
        return this.menuView.getItemIconSize();
    }

    public ColorStateList getItemTextColor() {
        return this.menuView.getItemTextColor();
    }

    public void setItemTextColor(ColorStateList textColor) {
        this.menuView.setItemTextColor(textColor);
    }

    @Deprecated
    public int getItemBackgroundResource() {
        return this.menuView.getItemBackgroundRes();
    }

    public void setItemBackgroundResource(int resId) {
        this.menuView.setItemBackgroundRes(resId);
    }

    public Drawable getItemBackground() {
        return this.menuView.getItemBackground();
    }

    public void setItemBackground(Drawable background) {
        this.menuView.setItemBackground(background);
    }

    public int getSelectedItemId() {
        return this.menuView.getSelectedItemId();
    }

    public void setSelectedItemId(int itemId) {
        MenuItem item = this.menu.findItem(itemId);
        if (item != null && !this.menu.performItemAction(item, this.presenter, 0)) {
            item.setChecked(true);
        }
    }

    public void setLabelVisibilityMode(int labelVisibilityMode) {
        if (this.menuView.getLabelVisibilityMode() != labelVisibilityMode) {
            this.menuView.setLabelVisibilityMode(labelVisibilityMode);
            this.presenter.updateMenuView(false);
        }
    }

    public int getLabelVisibilityMode() {
        return this.menuView.getLabelVisibilityMode();
    }

    public void setItemTextAppearanceInactive(int textAppearanceRes) {
        this.menuView.setItemTextAppearanceInactive(textAppearanceRes);
    }

    public int getItemTextAppearanceInactive() {
        return this.menuView.getItemTextAppearanceInactive();
    }

    public void setItemTextAppearanceActive(int textAppearanceRes) {
        this.menuView.setItemTextAppearanceActive(textAppearanceRes);
    }

    public int getItemTextAppearanceActive() {
        return this.menuView.getItemTextAppearanceActive();
    }

    public void setItemHorizontalTranslationEnabled(boolean itemHorizontalTranslationEnabled) {
        if (this.menuView.isItemHorizontalTranslationEnabled() != itemHorizontalTranslationEnabled) {
            this.menuView.setItemHorizontalTranslationEnabled(itemHorizontalTranslationEnabled);
            this.presenter.updateMenuView(false);
        }
    }

    public boolean isItemHorizontalTranslationEnabled() {
        return this.menuView.isItemHorizontalTranslationEnabled();
    }

    private void addCompatibilityTopDivider(Context context) {
        View divider = new View(context);
        divider.setBackgroundColor(ContextCompat.getColor(context, R.color.design_bottom_navigation_shadow_color));
        divider.setLayoutParams(new FrameLayout.LayoutParams(-1, getResources().getDimensionPixelSize(R.dimen.design_bottom_navigation_shadow_height)));
        addView(divider);
    }

    private MenuInflater getMenuInflater() {
        if (this.menuInflater == null) {
            this.menuInflater = new SupportMenuInflater(getContext());
        }
        return this.menuInflater;
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.menuPresenterState = new Bundle();
        this.menu.savePresenterStates(savedState.menuPresenterState);
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.menu.restorePresenterStates(savedState.menuPresenterState);
    }

    /* access modifiers changed from: package-private */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
            /* class com.google.android.material.bottomnavigation.BottomNavigationView.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        Bundle menuPresenterState;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            readFromParcel(source, loader);
        }

        @Override // androidx.customview.view.AbsSavedState
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBundle(this.menuPresenterState);
        }

        private void readFromParcel(Parcel in, ClassLoader loader) {
            this.menuPresenterState = in.readBundle(loader);
        }
    }
}
