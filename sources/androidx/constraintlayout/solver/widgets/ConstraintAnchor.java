package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.Cache;
import androidx.constraintlayout.solver.SolverVariable;
import java.util.ArrayList;
import java.util.HashSet;

public class ConstraintAnchor {
    private static final boolean ALLOW_BINARY = false;
    public static final int AUTO_CONSTRAINT_CREATOR = 2;
    public static final int SCOUT_CREATOR = 1;
    private static final int UNSET_GONE_MARGIN = -1;
    public static final int USER_CREATOR = 0;
    private int mConnectionCreator = 0;
    private ConnectionType mConnectionType = ConnectionType.RELAXED;
    int mGoneMargin = -1;
    public int mMargin = 0;
    final ConstraintWidget mOwner;
    private ResolutionAnchor mResolutionAnchor = new ResolutionAnchor(this);
    SolverVariable mSolverVariable;
    private Strength mStrength = Strength.NONE;
    ConstraintAnchor mTarget;
    final Type mType;

    public enum ConnectionType {
        RELAXED,
        STRICT
    }

    public enum Strength {
        NONE,
        STRONG,
        WEAK
    }

    public enum Type {
        NONE,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        BASELINE,
        CENTER,
        CENTER_X,
        CENTER_Y
    }

    public ResolutionAnchor getResolutionNode() {
        return this.mResolutionAnchor;
    }

    public ConstraintAnchor(ConstraintWidget owner, Type type) {
        this.mOwner = owner;
        this.mType = type;
    }

    public SolverVariable getSolverVariable() {
        return this.mSolverVariable;
    }

    public void resetSolverVariable(Cache cache) {
        SolverVariable solverVariable = this.mSolverVariable;
        if (solverVariable == null) {
            this.mSolverVariable = new SolverVariable(SolverVariable.Type.UNRESTRICTED, (String) null);
        } else {
            solverVariable.reset();
        }
    }

    public ConstraintWidget getOwner() {
        return this.mOwner;
    }

    public Type getType() {
        return this.mType;
    }

    public int getMargin() {
        ConstraintAnchor constraintAnchor;
        if (this.mOwner.getVisibility() == 8) {
            return 0;
        }
        if (this.mGoneMargin <= -1 || (constraintAnchor = this.mTarget) == null || constraintAnchor.mOwner.getVisibility() != 8) {
            return this.mMargin;
        }
        return this.mGoneMargin;
    }

    public Strength getStrength() {
        return this.mStrength;
    }

    public ConstraintAnchor getTarget() {
        return this.mTarget;
    }

    public ConnectionType getConnectionType() {
        return this.mConnectionType;
    }

    public void setConnectionType(ConnectionType type) {
        this.mConnectionType = type;
    }

    public int getConnectionCreator() {
        return this.mConnectionCreator;
    }

    public void setConnectionCreator(int creator) {
        this.mConnectionCreator = creator;
    }

    public void reset() {
        this.mTarget = null;
        this.mMargin = 0;
        this.mGoneMargin = -1;
        this.mStrength = Strength.STRONG;
        this.mConnectionCreator = 0;
        this.mConnectionType = ConnectionType.RELAXED;
        this.mResolutionAnchor.reset();
    }

    public boolean connect(ConstraintAnchor toAnchor, int margin, Strength strength, int creator) {
        return connect(toAnchor, margin, -1, strength, creator, false);
    }

    public boolean connect(ConstraintAnchor toAnchor, int margin, int goneMargin, Strength strength, int creator, boolean forceConnection) {
        if (toAnchor == null) {
            this.mTarget = null;
            this.mMargin = 0;
            this.mGoneMargin = -1;
            this.mStrength = Strength.NONE;
            this.mConnectionCreator = 2;
            return true;
        } else if (!forceConnection && !isValidConnection(toAnchor)) {
            return false;
        } else {
            this.mTarget = toAnchor;
            if (margin > 0) {
                this.mMargin = margin;
            } else {
                this.mMargin = 0;
            }
            this.mGoneMargin = goneMargin;
            this.mStrength = strength;
            this.mConnectionCreator = creator;
            return true;
        }
    }

    public boolean connect(ConstraintAnchor toAnchor, int margin, int creator) {
        return connect(toAnchor, margin, -1, Strength.STRONG, creator, false);
    }

    public boolean connect(ConstraintAnchor toAnchor, int margin) {
        return connect(toAnchor, margin, -1, Strength.STRONG, 0, false);
    }

    public boolean isConnected() {
        return this.mTarget != null;
    }

    public boolean isValidConnection(ConstraintAnchor anchor) {
        boolean isCompatible = false;
        if (anchor == null) {
            return false;
        }
        Type target = anchor.getType();
        Type type = this.mType;
        if (target != type) {
            switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
                case 1:
                    if (target == Type.BASELINE || target == Type.CENTER_X || target == Type.CENTER_Y) {
                        return false;
                    }
                    return true;
                case 2:
                case 3:
                    boolean isCompatible2 = target == Type.LEFT || target == Type.RIGHT;
                    if (!(anchor.getOwner() instanceof Guideline)) {
                        return isCompatible2;
                    }
                    if (isCompatible2 || target == Type.CENTER_X) {
                        isCompatible = true;
                    }
                    return isCompatible;
                case 4:
                case 5:
                    boolean isCompatible3 = target == Type.TOP || target == Type.BOTTOM;
                    if (!(anchor.getOwner() instanceof Guideline)) {
                        return isCompatible3;
                    }
                    if (isCompatible3 || target == Type.CENTER_Y) {
                        isCompatible = true;
                    }
                    return isCompatible;
                case 6:
                case 7:
                case 8:
                case 9:
                    return false;
                default:
                    throw new AssertionError(this.mType.name());
            }
        } else if (type != Type.BASELINE || (anchor.getOwner().hasBaseline() && getOwner().hasBaseline())) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: androidx.constraintlayout.solver.widgets.ConstraintAnchor$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type;

        static {
            int[] iArr = new int[Type.values().length];
            $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type = iArr;
            try {
                iArr[Type.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[Type.LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[Type.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[Type.TOP.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[Type.BOTTOM.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[Type.BASELINE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[Type.CENTER_X.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[Type.CENTER_Y.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[Type.NONE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    public boolean isSideAnchor() {
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
            case 6:
            case 7:
            case 8:
            case 9:
                return false;
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public boolean isSimilarDimensionConnection(ConstraintAnchor anchor) {
        Type target = anchor.getType();
        if (target == this.mType) {
            return true;
        }
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
                if (target != Type.BASELINE) {
                    return true;
                }
                return false;
            case 2:
            case 3:
            case 7:
                if (target == Type.LEFT || target == Type.RIGHT || target == Type.CENTER_X) {
                    return true;
                }
                return false;
            case 4:
            case 5:
            case 6:
            case 8:
                if (target == Type.TOP || target == Type.BOTTOM || target == Type.CENTER_Y || target == Type.BASELINE) {
                    return true;
                }
                return false;
            case 9:
                return false;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public void setStrength(Strength strength) {
        if (isConnected()) {
            this.mStrength = strength;
        }
    }

    public void setMargin(int margin) {
        if (isConnected()) {
            this.mMargin = margin;
        }
    }

    public void setGoneMargin(int margin) {
        if (isConnected()) {
            this.mGoneMargin = margin;
        }
    }

    public boolean isVerticalAnchor() {
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 7:
                return false;
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
                return true;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public String toString() {
        return this.mOwner.getDebugName() + ":" + this.mType.toString();
    }

    public int getSnapPriorityLevel() {
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
                return 3;
            case 2:
                return 1;
            case 3:
                return 1;
            case 4:
                return 0;
            case 5:
                return 0;
            case 6:
                return 2;
            case 7:
                return 0;
            case 8:
                return 1;
            case 9:
                return 0;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public int getPriorityLevel() {
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
                return 2;
            case 2:
                return 2;
            case 3:
                return 2;
            case 4:
                return 2;
            case 5:
                return 2;
            case 6:
                return 1;
            case 7:
                return 0;
            case 8:
                return 0;
            case 9:
                return 0;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public boolean isSnapCompatibleWith(ConstraintAnchor anchor) {
        if (this.mType == Type.CENTER) {
            return false;
        }
        if (this.mType == anchor.getType()) {
            return true;
        }
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
            case 6:
            case 9:
                return false;
            case 2:
                int i = AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[anchor.getType().ordinal()];
                if (i == 3 || i == 7) {
                    return true;
                }
                return false;
            case 3:
                int i2 = AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[anchor.getType().ordinal()];
                if (i2 == 2 || i2 == 7) {
                    return true;
                }
                return false;
            case 4:
                int i3 = AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[anchor.getType().ordinal()];
                if (i3 == 5 || i3 == 8) {
                    return true;
                }
                return false;
            case 5:
                int i4 = AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[anchor.getType().ordinal()];
                if (i4 == 4 || i4 == 8) {
                    return true;
                }
                return false;
            case 7:
                int i5 = AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[anchor.getType().ordinal()];
                if (i5 == 2 || i5 == 3) {
                    return true;
                }
                return false;
            case 8:
                int i6 = AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[anchor.getType().ordinal()];
                if (i6 == 4 || i6 == 5) {
                    return true;
                }
                return false;
            default:
                throw new AssertionError(this.mType.name());
        }
    }

    public boolean isConnectionAllowed(ConstraintWidget target, ConstraintAnchor anchor) {
        return isConnectionAllowed(target);
    }

    public boolean isConnectionAllowed(ConstraintWidget target) {
        if (isConnectionToMe(target, new HashSet<>())) {
            return false;
        }
        ConstraintWidget parent = getOwner().getParent();
        if (parent == target || target.getParent() == parent) {
            return true;
        }
        return false;
    }

    private boolean isConnectionToMe(ConstraintWidget target, HashSet<ConstraintWidget> checked) {
        if (checked.contains(target)) {
            return false;
        }
        checked.add(target);
        if (target == getOwner()) {
            return true;
        }
        ArrayList<ConstraintAnchor> targetAnchors = target.getAnchors();
        int targetAnchorsSize = targetAnchors.size();
        for (int i = 0; i < targetAnchorsSize; i++) {
            ConstraintAnchor anchor = targetAnchors.get(i);
            if (anchor.isSimilarDimensionConnection(this) && anchor.isConnected() && isConnectionToMe(anchor.getTarget().getOwner(), checked)) {
                return true;
            }
        }
        return false;
    }

    public final ConstraintAnchor getOpposite() {
        switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()]) {
            case 1:
            case 6:
            case 7:
            case 8:
            case 9:
                return null;
            case 2:
                return this.mOwner.mRight;
            case 3:
                return this.mOwner.mLeft;
            case 4:
                return this.mOwner.mBottom;
            case 5:
                return this.mOwner.mTop;
            default:
                throw new AssertionError(this.mType.name());
        }
    }
}
