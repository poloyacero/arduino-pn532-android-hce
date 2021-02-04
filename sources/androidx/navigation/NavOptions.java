package androidx.navigation;

public final class NavOptions {
    private int mEnterAnim;
    private int mExitAnim;
    private int mPopEnterAnim;
    private int mPopExitAnim;
    private int mPopUpTo;
    private boolean mPopUpToInclusive;
    private boolean mSingleTop;

    NavOptions(boolean singleTop, int popUpTo, boolean popUpToInclusive, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        this.mSingleTop = singleTop;
        this.mPopUpTo = popUpTo;
        this.mPopUpToInclusive = popUpToInclusive;
        this.mEnterAnim = enterAnim;
        this.mExitAnim = exitAnim;
        this.mPopEnterAnim = popEnterAnim;
        this.mPopExitAnim = popExitAnim;
    }

    public boolean shouldLaunchSingleTop() {
        return this.mSingleTop;
    }

    public int getPopUpTo() {
        return this.mPopUpTo;
    }

    public boolean isPopUpToInclusive() {
        return this.mPopUpToInclusive;
    }

    public int getEnterAnim() {
        return this.mEnterAnim;
    }

    public int getExitAnim() {
        return this.mExitAnim;
    }

    public int getPopEnterAnim() {
        return this.mPopEnterAnim;
    }

    public int getPopExitAnim() {
        return this.mPopExitAnim;
    }

    public static final class Builder {
        int mEnterAnim = -1;
        int mExitAnim = -1;
        int mPopEnterAnim = -1;
        int mPopExitAnim = -1;
        int mPopUpTo = -1;
        boolean mPopUpToInclusive;
        boolean mSingleTop;

        public Builder setLaunchSingleTop(boolean singleTop) {
            this.mSingleTop = singleTop;
            return this;
        }

        public Builder setPopUpTo(int destinationId, boolean inclusive) {
            this.mPopUpTo = destinationId;
            this.mPopUpToInclusive = inclusive;
            return this;
        }

        public Builder setEnterAnim(int enterAnim) {
            this.mEnterAnim = enterAnim;
            return this;
        }

        public Builder setExitAnim(int exitAnim) {
            this.mExitAnim = exitAnim;
            return this;
        }

        public Builder setPopEnterAnim(int popEnterAnim) {
            this.mPopEnterAnim = popEnterAnim;
            return this;
        }

        public Builder setPopExitAnim(int popExitAnim) {
            this.mPopExitAnim = popExitAnim;
            return this;
        }

        public NavOptions build() {
            return new NavOptions(this.mSingleTop, this.mPopUpTo, this.mPopUpToInclusive, this.mEnterAnim, this.mExitAnim, this.mPopEnterAnim, this.mPopExitAnim);
        }
    }
}
