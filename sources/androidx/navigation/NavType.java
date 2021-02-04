package androidx.navigation;

import android.os.Bundle;
import android.os.Parcelable;
import java.io.Serializable;

public abstract class NavType<T> {
    public static final NavType<boolean[]> BoolArrayType = new NavType<boolean[]>(true) {
        /* class androidx.navigation.NavType.AnonymousClass9 */

        public void put(Bundle bundle, String key, boolean[] value) {
            bundle.putBooleanArray(key, value);
        }

        @Override // androidx.navigation.NavType
        public boolean[] get(Bundle bundle, String key) {
            return (boolean[]) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public boolean[] parseValue(String value) {
            throw new UnsupportedOperationException("Arrays don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "boolean[]";
        }
    };
    public static final NavType<Boolean> BoolType = new NavType<Boolean>(false) {
        /* class androidx.navigation.NavType.AnonymousClass8 */

        public void put(Bundle bundle, String key, Boolean value) {
            bundle.putBoolean(key, value.booleanValue());
        }

        @Override // androidx.navigation.NavType
        public Boolean get(Bundle bundle, String key) {
            return (Boolean) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public Boolean parseValue(String value) {
            if ("true".equals(value)) {
                return true;
            }
            if ("false".equals(value)) {
                return false;
            }
            throw new IllegalArgumentException("A boolean NavType only accepts \"true\" or \"false\" values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "boolean";
        }
    };
    public static final NavType<float[]> FloatArrayType = new NavType<float[]>(true) {
        /* class androidx.navigation.NavType.AnonymousClass7 */

        public void put(Bundle bundle, String key, float[] value) {
            bundle.putFloatArray(key, value);
        }

        @Override // androidx.navigation.NavType
        public float[] get(Bundle bundle, String key) {
            return (float[]) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public float[] parseValue(String value) {
            throw new UnsupportedOperationException("Arrays don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "float[]";
        }
    };
    public static final NavType<Float> FloatType = new NavType<Float>(false) {
        /* class androidx.navigation.NavType.AnonymousClass6 */

        public void put(Bundle bundle, String key, Float value) {
            bundle.putFloat(key, value.floatValue());
        }

        @Override // androidx.navigation.NavType
        public Float get(Bundle bundle, String key) {
            return (Float) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public Float parseValue(String value) {
            return Float.valueOf(Float.parseFloat(value));
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "float";
        }
    };
    public static final NavType<int[]> IntArrayType = new NavType<int[]>(true) {
        /* class androidx.navigation.NavType.AnonymousClass3 */

        public void put(Bundle bundle, String key, int[] value) {
            bundle.putIntArray(key, value);
        }

        @Override // androidx.navigation.NavType
        public int[] get(Bundle bundle, String key) {
            return (int[]) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public int[] parseValue(String value) {
            throw new UnsupportedOperationException("Arrays don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "integer[]";
        }
    };
    public static final NavType<Integer> IntType = new NavType<Integer>(false) {
        /* class androidx.navigation.NavType.AnonymousClass1 */

        public void put(Bundle bundle, String key, Integer value) {
            bundle.putInt(key, value.intValue());
        }

        @Override // androidx.navigation.NavType
        public Integer get(Bundle bundle, String key) {
            return (Integer) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public Integer parseValue(String value) {
            if (value.startsWith("0x")) {
                return Integer.valueOf(Integer.parseInt(value.substring(2), 16));
            }
            return Integer.valueOf(Integer.parseInt(value));
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "integer";
        }
    };
    public static final NavType<long[]> LongArrayType = new NavType<long[]>(true) {
        /* class androidx.navigation.NavType.AnonymousClass5 */

        public void put(Bundle bundle, String key, long[] value) {
            bundle.putLongArray(key, value);
        }

        @Override // androidx.navigation.NavType
        public long[] get(Bundle bundle, String key) {
            return (long[]) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public long[] parseValue(String value) {
            throw new UnsupportedOperationException("Arrays don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "long[]";
        }
    };
    public static final NavType<Long> LongType = new NavType<Long>(false) {
        /* class androidx.navigation.NavType.AnonymousClass4 */

        public void put(Bundle bundle, String key, Long value) {
            bundle.putLong(key, value.longValue());
        }

        @Override // androidx.navigation.NavType
        public Long get(Bundle bundle, String key) {
            return (Long) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public Long parseValue(String value) {
            if (value.endsWith("L")) {
                value = value.substring(0, value.length() - 1);
            }
            if (value.startsWith("0x")) {
                return Long.valueOf(Long.parseLong(value.substring(2), 16));
            }
            return Long.valueOf(Long.parseLong(value));
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "long";
        }
    };
    public static final NavType<Integer> ReferenceType = new NavType<Integer>(false) {
        /* class androidx.navigation.NavType.AnonymousClass2 */

        public void put(Bundle bundle, String key, Integer value) {
            bundle.putInt(key, value.intValue());
        }

        @Override // androidx.navigation.NavType
        public Integer get(Bundle bundle, String key) {
            return (Integer) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public Integer parseValue(String value) {
            throw new UnsupportedOperationException("References don't support parsing string values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "reference";
        }
    };
    public static final NavType<String[]> StringArrayType = new NavType<String[]>(true) {
        /* class androidx.navigation.NavType.AnonymousClass11 */

        public void put(Bundle bundle, String key, String[] value) {
            bundle.putStringArray(key, value);
        }

        @Override // androidx.navigation.NavType
        public String[] get(Bundle bundle, String key) {
            return (String[]) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public String[] parseValue(String value) {
            throw new UnsupportedOperationException("Arrays don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "string[]";
        }
    };
    public static final NavType<String> StringType = new NavType<String>(true) {
        /* class androidx.navigation.NavType.AnonymousClass10 */

        public void put(Bundle bundle, String key, String value) {
            bundle.putString(key, value);
        }

        @Override // androidx.navigation.NavType
        public String get(Bundle bundle, String key) {
            return (String) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public String parseValue(String value) {
            return value;
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return "string";
        }
    };
    private final boolean mNullableAllowed;

    public abstract T get(Bundle bundle, String str);

    public abstract String getName();

    public abstract T parseValue(String str);

    public abstract void put(Bundle bundle, String str, T t);

    NavType(boolean nullableAllowed) {
        this.mNullableAllowed = nullableAllowed;
    }

    public boolean isNullableAllowed() {
        return this.mNullableAllowed;
    }

    /* access modifiers changed from: package-private */
    public T parseAndPut(Bundle bundle, String key, String value) {
        T parsedValue = parseValue(value);
        put(bundle, key, parsedValue);
        return parsedValue;
    }

    public String toString() {
        return getName();
    }

    /* JADX DEBUG: Type inference failed for r0v45. Raw type applied. Possible types: androidx.navigation.NavType<float[]>, androidx.navigation.NavType<?> */
    /* JADX DEBUG: Type inference failed for r0v47. Raw type applied. Possible types: androidx.navigation.NavType<java.lang.String[]>, androidx.navigation.NavType<?> */
    /* JADX DEBUG: Type inference failed for r0v49. Raw type applied. Possible types: androidx.navigation.NavType<boolean[]>, androidx.navigation.NavType<?> */
    /* JADX DEBUG: Type inference failed for r0v51. Raw type applied. Possible types: androidx.navigation.NavType<long[]>, androidx.navigation.NavType<?> */
    /* JADX DEBUG: Type inference failed for r0v53. Raw type applied. Possible types: androidx.navigation.NavType<int[]>, androidx.navigation.NavType<?> */
    public static NavType<?> fromArgType(String type, String packageName) {
        String className;
        if (IntType.getName().equals(type)) {
            return IntType;
        }
        if (IntArrayType.getName().equals(type)) {
            return IntArrayType;
        }
        if (LongType.getName().equals(type)) {
            return LongType;
        }
        if (LongArrayType.getName().equals(type)) {
            return LongArrayType;
        }
        if (BoolType.getName().equals(type)) {
            return BoolType;
        }
        if (BoolArrayType.getName().equals(type)) {
            return BoolArrayType;
        }
        if (StringType.getName().equals(type)) {
            return StringType;
        }
        if (StringArrayType.getName().equals(type)) {
            return StringArrayType;
        }
        if (FloatType.getName().equals(type)) {
            return FloatType;
        }
        if (FloatArrayType.getName().equals(type)) {
            return FloatArrayType;
        }
        if (ReferenceType.getName().equals(type)) {
            return ReferenceType;
        }
        if (type == null || type.isEmpty()) {
            return StringType;
        }
        try {
            if (!type.startsWith(".") || packageName == null) {
                className = type;
            } else {
                className = packageName + type;
            }
            if (type.endsWith("[]")) {
                className = className.substring(0, className.length() - 2);
                Class clazz = Class.forName(className);
                if (Parcelable.class.isAssignableFrom(clazz)) {
                    return new ParcelableArrayType(clazz);
                }
                if (Serializable.class.isAssignableFrom(clazz)) {
                    return new SerializableArrayType(clazz);
                }
            } else {
                Class clazz2 = Class.forName(className);
                if (Parcelable.class.isAssignableFrom(clazz2)) {
                    return new ParcelableType(clazz2);
                }
                if (Enum.class.isAssignableFrom(clazz2)) {
                    return new EnumType(clazz2);
                }
                if (Serializable.class.isAssignableFrom(clazz2)) {
                    return new SerializableType(clazz2);
                }
            }
            throw new IllegalArgumentException(className + " is not Serializable or Parcelable.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static NavType inferFromValue(String value) {
        try {
            IntType.parseValue(value);
            return IntType;
        } catch (IllegalArgumentException e) {
            try {
                LongType.parseValue(value);
                return LongType;
            } catch (IllegalArgumentException e2) {
                try {
                    FloatType.parseValue(value);
                    return FloatType;
                } catch (IllegalArgumentException e3) {
                    try {
                        BoolType.parseValue(value);
                        return BoolType;
                    } catch (IllegalArgumentException e4) {
                        return StringType;
                    }
                }
            }
        }
    }

    static NavType inferFromValueType(Object value) {
        if (value instanceof Integer) {
            return IntType;
        }
        if (value instanceof int[]) {
            return IntArrayType;
        }
        if (value instanceof Long) {
            return LongType;
        }
        if (value instanceof long[]) {
            return LongArrayType;
        }
        if (value instanceof Float) {
            return FloatType;
        }
        if (value instanceof float[]) {
            return FloatArrayType;
        }
        if (value instanceof Boolean) {
            return BoolType;
        }
        if (value instanceof boolean[]) {
            return BoolArrayType;
        }
        if ((value instanceof String) || value == null) {
            return StringType;
        }
        if (value instanceof String[]) {
            return StringArrayType;
        }
        if (value.getClass().isArray() && Parcelable.class.isAssignableFrom(value.getClass().getComponentType())) {
            return new ParcelableArrayType(value.getClass().getComponentType());
        }
        if (value.getClass().isArray() && Serializable.class.isAssignableFrom(value.getClass().getComponentType())) {
            return new SerializableArrayType(value.getClass().getComponentType());
        }
        if (value instanceof Parcelable) {
            return new ParcelableType(value.getClass());
        }
        if (value instanceof Enum) {
            return new EnumType(value.getClass());
        }
        if (value instanceof Serializable) {
            return new SerializableType(value.getClass());
        }
        throw new IllegalArgumentException("Object of type " + value.getClass().getName() + " is not supported for navigation arguments.");
    }

    public static final class ParcelableType<D> extends NavType<D> {
        private final Class<D> mType;

        public ParcelableType(Class<D> type) {
            super(true);
            if (Parcelable.class.isAssignableFrom(type) || Serializable.class.isAssignableFrom(type)) {
                this.mType = type;
                return;
            }
            throw new IllegalArgumentException(type + " does not implement Parcelable or Serializable.");
        }

        @Override // androidx.navigation.NavType
        public void put(Bundle bundle, String key, D value) {
            this.mType.cast(value);
            if (value == null || (value instanceof Parcelable)) {
                bundle.putParcelable(key, value);
            } else if (value instanceof Serializable) {
                bundle.putSerializable(key, value);
            }
        }

        @Override // androidx.navigation.NavType
        public D get(Bundle bundle, String key) {
            return (D) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public D parseValue(String value) {
            throw new UnsupportedOperationException("Parcelables don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return this.mType.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return this.mType.equals(((ParcelableType) o).mType);
        }

        public int hashCode() {
            return this.mType.hashCode();
        }
    }

    public static final class ParcelableArrayType<D extends Parcelable> extends NavType<D[]> {
        private final Class<D[]> mArrayType;

        public ParcelableArrayType(Class<D> type) {
            super(true);
            if (Parcelable.class.isAssignableFrom(type)) {
                try {
                    this.mArrayType = (Class<D[]>) Class.forName("[L" + type.getName() + ";");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalArgumentException(type + " does not implement Parcelable.");
            }
        }

        public void put(Bundle bundle, String key, D[] value) {
            this.mArrayType.cast(value);
            bundle.putParcelableArray(key, value);
        }

        @Override // androidx.navigation.NavType
        public D[] get(Bundle bundle, String key) {
            return (D[]) ((Parcelable[]) bundle.get(key));
        }

        @Override // androidx.navigation.NavType
        public D[] parseValue(String value) {
            throw new UnsupportedOperationException("Arrays don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return this.mArrayType.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return this.mArrayType.equals(((ParcelableArrayType) o).mArrayType);
        }

        public int hashCode() {
            return this.mArrayType.hashCode();
        }
    }

    public static class SerializableType<D extends Serializable> extends NavType<D> {
        private final Class<D> mType;

        public SerializableType(Class<D> type) {
            super(true);
            if (!Serializable.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException(type + " does not implement Serializable.");
            } else if (!type.isEnum()) {
                this.mType = type;
            } else {
                throw new IllegalArgumentException(type + " is an Enum. You should use EnumType instead.");
            }
        }

        SerializableType(boolean nullableAllowed, Class<D> type) {
            super(nullableAllowed);
            if (Serializable.class.isAssignableFrom(type)) {
                this.mType = type;
                return;
            }
            throw new IllegalArgumentException(type + " does not implement Serializable.");
        }

        public void put(Bundle bundle, String key, D value) {
            this.mType.cast(value);
            bundle.putSerializable(key, value);
        }

        @Override // androidx.navigation.NavType
        public D get(Bundle bundle, String key) {
            return (D) ((Serializable) bundle.get(key));
        }

        @Override // androidx.navigation.NavType
        public D parseValue(String value) {
            throw new UnsupportedOperationException("Serializables don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return this.mType.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return this.mType.equals(((SerializableType) o).mType);
        }

        public int hashCode() {
            return this.mType.hashCode();
        }
    }

    public static final class EnumType<D extends Enum> extends SerializableType<D> {
        private final Class<D> mType;

        public EnumType(Class<D> type) {
            super(false, type);
            if (type.isEnum()) {
                this.mType = type;
                return;
            }
            throw new IllegalArgumentException(type + " is not an Enum type.");
        }

        @Override // androidx.navigation.NavType, androidx.navigation.NavType.SerializableType, androidx.navigation.NavType.SerializableType
        public D parseValue(String value) {
            Object[] objArr = (Enum[]) this.mType.getEnumConstants();
            for (Object constant : objArr) {
                if (((Enum) constant).name().equals(value)) {
                    return (Enum) constant;
                }
            }
            throw new IllegalArgumentException("Enum value " + value + " not found for type " + this.mType.getName() + ".");
        }

        @Override // androidx.navigation.NavType, androidx.navigation.NavType.SerializableType
        public String getName() {
            return this.mType.getName();
        }
    }

    public static final class SerializableArrayType<D extends Serializable> extends NavType<D[]> {
        private final Class<D[]> mArrayType;

        public SerializableArrayType(Class<D> type) {
            super(true);
            if (Serializable.class.isAssignableFrom(type)) {
                try {
                    this.mArrayType = (Class<D[]>) Class.forName("[L" + type.getName() + ";");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalArgumentException(type + " does not implement Serializable.");
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for r4v0, resolved type: D extends java.io.Serializable[] */
        /* JADX WARN: Multi-variable type inference failed */
        public void put(Bundle bundle, String key, D[] value) {
            this.mArrayType.cast(value);
            bundle.putSerializable(key, value);
        }

        @Override // androidx.navigation.NavType
        public D[] get(Bundle bundle, String key) {
            return (D[]) ((Serializable[]) bundle.get(key));
        }

        @Override // androidx.navigation.NavType
        public D[] parseValue(String value) {
            throw new UnsupportedOperationException("Arrays don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return this.mArrayType.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return this.mArrayType.equals(((SerializableArrayType) o).mArrayType);
        }

        public int hashCode() {
            return this.mArrayType.hashCode();
        }
    }
}
