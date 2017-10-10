package moe.shizuku.fontprovider.compat;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by rikka on 2017/9/27.
 */

@SuppressLint("PrivateApi")
public class TypefaceCompat {

    private static boolean available = true;

    protected static Field sFallbackFontsField;
    protected static Field sSystemFontMapField;
    protected static Method createFromFamiliesMethod;
    protected static Method setDefaultMethod;
    protected static Method nativeCreateWeightAliasMethod;
    protected static Constructor constructor;
    protected static Field nativeInstanceField;

    static {
        try {
            sFallbackFontsField = Typeface.class.getDeclaredField("sFallbackFonts");
            sFallbackFontsField.setAccessible(true);

            sSystemFontMapField = Typeface.class.getDeclaredField("sSystemFontMap");
            sSystemFontMapField.setAccessible(true);

            createFromFamiliesMethod = Typeface.class.getDeclaredMethod("createFromFamilies",
                    FontFamilyCompat.getFontFamilyArrayClass());
            createFromFamiliesMethod.setAccessible(true);

            setDefaultMethod = Typeface.class.getDeclaredMethod("setDefault",
                    Typeface.class);
            setDefaultMethod.setAccessible(true);

            nativeCreateWeightAliasMethod = Typeface.class.getDeclaredMethod("nativeCreateWeightAlias",
                    Long.TYPE, Integer.TYPE);
            nativeCreateWeightAliasMethod.setAccessible(true);

            constructor = Typeface.class.getDeclaredConstructor(Long.TYPE);
            constructor.setAccessible(true);

            nativeInstanceField = Typeface.class.getDeclaredField("native_instance");
            nativeInstanceField.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();

            available = false;
        }
    }

    /**
     * Return Typeface.sFallbackFonts.
     */
    public static Object getFallbackFontsArray() {
        if (!available) {
            return null;
        }

        try {
            return sFallbackFontsField.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Typeface> sSystemFontMap;

    /**
     * Return Typeface.sSystemFontMap.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Typeface> getSystemFontMap() {
        if (!available) {
            return null;
        }

        if (sSystemFontMap == null) {
            try {
                sSystemFontMap = (Map<String, Typeface>) sSystemFontMapField.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        return sSystemFontMap;
    }

    public static void setDefault(Typeface typeface) {
        if (!available) {
            return;
        }

        try {
            setDefaultMethod.invoke(null, typeface);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static long nativeCreateWeightAlias(long native_instance, int weight) {
        if (!available) {
            return -1;
        }

        try {
            return (long) nativeCreateWeightAliasMethod.invoke(null, native_instance, weight);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Typeface create(long ni) {
        if (!available) {
            return null;
        }

        try {
            return (Typeface) constructor.newInstance(ni);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Typeface createWeightAlias(Typeface family, int weight) {
        if (!available) {
            return null;
        }

        try {
            return (Typeface) constructor.newInstance(nativeCreateWeightAlias(getNativeInstance(family), weight));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create Typeface with the order of your fontFamilies.
     *
     * @param families FontFamily array Object
     * @return Typeface object
     */
    public static Typeface createFromFamilies(Object families) {
        if (!available) {
            return null;
        }

        try {
            return (Typeface) createFromFamiliesMethod.invoke(null, families);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a new typeface from an array of font families, including
     * also the font families in the fallback list.
     * @param weight the weight for this family, required on API 26+.
     * @param italic the italic information for this family, required on API 26+.
     * @param families array of font families.
     * @return Typeface object
     */
    public static Typeface createFromFamiliesWithDefault(Object families, int weight, int italic) {
        if (Build.VERSION.SDK_INT >= 26) {
            return TypefaceCompatApi26.createFromFamiliesWithDefault(families, weight, italic);
        } else if (Build.VERSION.SDK_INT >= 21) {
            return TypefaceCompatApi21.createFromFamiliesWithDefault(families, weight);
        } else {
            throw new IllegalStateException("unsupported system version");
        }
    }

    public static long getNativeInstance(Typeface typeface) throws IllegalAccessException {
        return (long) nativeInstanceField.get(typeface);
    }
}
