package com.lois.tytool.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;

import androidx.annotation.ArrayRes;
import androidx.annotation.BoolRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.PluralsRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.lois.tytool.TyTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description ResourceUtils
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public final class ResourceUtils {

    /**
     * Don't let anyone instantiate this class.
     */
    private ResourceUtils() {
        throw new Error("Do not need instantiate!");
    }

    /**
     * get an asset using ACCESS_STREAMING mode. This provides access to files that have been bundled with an
     * application as assets -- that is, files placed in to the "assets" directory.
     *
     * @param context
     * @param fileName The name of the asset to open. This name can be hierarchical.
     * @return
     */
    public static String getFileFromAssets(Context context, String fileName) {
        if (context == null || TextUtils.isEmpty(fileName)) {
            return null;
        }

        StringBuilder s = new StringBuilder("");
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get content from a raw resource. This can only be used with resources whose value is the name of an asset files
     * -- that is, it can be used to open drawable, sound, and raw resources; it will fail on string and color
     * resources.
     *
     * @param context
     * @param resId   The resource identifier to open, as generated by the appt tool.
     * @return
     */
    public static String getFileFromRaw(Context context, int resId) {
        if (context == null) {
            return null;
        }

        StringBuilder s = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * same to {@link ResourceUtils#getFileFromAssets(Context, String)}, but return type is List<String>
     *
     * @param context
     * @param fileName
     * @return
     */
    public static List<String> getFileToListFromAssets(Context context, String fileName) {
        if (context == null || TextUtils.isEmpty(fileName)) {
            return null;
        }

        List<String> fileContent = new ArrayList<String>();
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.add(line);
            }
            br.close();
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * same to {@link ResourceUtils#getFileFromRaw(Context, int)}, but return type is List<String>
     *
     * @param context
     * @param resId
     * @return
     */
    public static List<String> getFileToListFromRaw(Context context, int resId) {
        if (context == null) {
            return null;
        }

        List<String> fileContent = new ArrayList<String>();
        BufferedReader reader;
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
            reader = new BufferedReader(in);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float getAttrFloatValue(Context context, int attrRes){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.getFloat();
    }

    public static int getAttrColor(Context context, int attrRes){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
    }

    public static ColorStateList getAttrColorStateList(Context context, int attrRes){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return ContextCompat.getColorStateList(context, typedValue.resourceId);
    }

    public static Drawable getAttrDrawable(Context context, int attrRes){
        int[] attrs = new int[] { attrRes };
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable drawable = getAttrDrawable(context, ta, 0);
        ta.recycle();
        return drawable;
    }

    public static Drawable getAttrDrawable(Context context, TypedArray typedArray, int index){
        TypedValue value = typedArray.peekValue(index);
        if (value != null) {
            if (value.type != TypedValue.TYPE_ATTRIBUTE && value.resourceId != 0) {
                return AppCompatResources.getDrawable(context, value.resourceId);
            }
        }
        return null;
    }

    public static int getAttrDimen(Context context, int attrRes){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrRes, typedValue, true);
        return TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
    }

    /*----------------------------------wrapper methods--------------------------------------*/

    public static int[] getIntArray(@ArrayRes int id) {
        return TyTool.getInstance().getContext().getResources().getIntArray(id);
    }

    public static CharSequence[] getTextArray(@ArrayRes int id) {
        return TyTool.getInstance().getContext().getResources().getTextArray(id);
    }

    public static String[] getStringArray(@ArrayRes int id) {
        return TyTool.getInstance().getContext().getResources().getStringArray(id);
    }

    @ColorInt
    public static int getColor(@ColorRes int id) {
        return TyTool.getInstance().getContext().getResources().getColor(id);
    }

    public static String getString(@StringRes int id) {
        return TyTool.getInstance().getContext().getResources().getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs) {
        return TyTool.getInstance().getContext().getResources().getString(id, formatArgs);
    }

    public static CharSequence getText(@StringRes int id) {
        return TyTool.getInstance().getContext().getResources().getText(id);
    }

    public static CharSequence getQuantityText(@PluralsRes int id, int quantity) {
        return TyTool.getInstance().getContext().getResources().getQuantityText(id, quantity);
    }

    public static String getQuantityString(@PluralsRes int id, int quantity) {
        return TyTool.getInstance().getContext().getResources().getQuantityString(id, quantity);
    }

    public static String getQuantityString(@PluralsRes int id, int quantity, Object... formatArgs) {
        return TyTool.getInstance().getContext().getResources().getQuantityString(id, quantity, formatArgs);
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return TyTool.getInstance().getContext().getResources().getDrawable(id);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static Typeface getFont(@FontRes int id) {
        return TyTool.getInstance().getContext().getResources().getFont(id);
    }

    public static float getDimension(@DimenRes int id) {
        return TyTool.getInstance().getContext().getResources().getDimension(id);
    }

    public static boolean getBoolean(@BoolRes int id) {
        return TyTool.getInstance().getContext().getResources().getBoolean(id);
    }

    public static int getInteger(@IntegerRes int id) {
        return TyTool.getInstance().getContext().getResources().getInteger(id);
    }

    public static InputStream openRawResource(@RawRes int id) {
        return TyTool.getInstance().getContext().getResources().openRawResource(id);
    }

    public static AssetFileDescriptor openRawResourceFd(@RawRes int id) {
        return TyTool.getInstance().getContext().getResources().openRawResourceFd(id);
    }

    public static AssetManager getAssets() {
        return TyTool.getInstance().getContext().getResources().getAssets();
    }
}
