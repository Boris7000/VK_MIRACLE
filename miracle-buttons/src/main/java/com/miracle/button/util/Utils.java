package com.miracle.button.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.miracle.button.drawable.AlwaysStatefulRippleDrawable;

public class Utils {

    public static TintInfo cloneTintInfo(TintInfo tintInfo){
        TintInfo cloneTintInfo = new TintInfo();
        cloneTintInfo.mTintList = tintInfo.mTintList;
        cloneTintInfo.mHasTintList = tintInfo.mHasTintList;
        cloneTintInfo.mTintMode = tintInfo.mTintMode;
        cloneTintInfo.mHasTintMode = tintInfo.mHasTintMode;
        return cloneTintInfo;
    }

    public static TintInfo extractTintInfo(@NonNull TypedArray a, int tintAttr, int tintModeAttr){
        TintInfo tintInfo = new TintInfo();
        if (a.hasValue(tintAttr)) {
            tintInfo.mTintList = a.getColorStateList(tintAttr);
            tintInfo.mHasTintList = true;
        } else {
            tintInfo.mTintList = null;
            tintInfo.mHasTintList = false;
        }

        if (a.hasValue(tintModeAttr)) {
            tintInfo.mTintMode = intToMode(a.getInt(tintAttr, 0));
            tintInfo.mHasTintMode = true;
        } else {
            tintInfo.mTintMode = null;
            tintInfo.mHasTintMode = false;
        }
        return tintInfo;
    }

    public static PorterDuff.Mode intToMode(int val) {
        switch (val) {
            default:
            case  0: return PorterDuff.Mode.CLEAR;
            case  1: return PorterDuff.Mode.SRC;
            case  2: return PorterDuff.Mode.DST;
            case  3: return PorterDuff.Mode.SRC_OVER;
            case  4: return PorterDuff.Mode.DST_OVER;
            case  5: return PorterDuff.Mode.SRC_IN;
            case  6: return PorterDuff.Mode.DST_IN;
            case  7: return PorterDuff.Mode.SRC_OUT;
            case  8: return PorterDuff.Mode.DST_OUT;
            case  9: return PorterDuff.Mode.SRC_ATOP;
            case 10: return PorterDuff.Mode.DST_ATOP;
            case 11: return PorterDuff.Mode.XOR;
            case 16: return PorterDuff.Mode.DARKEN;
            case 17: return PorterDuff.Mode.LIGHTEN;
            case 13: return PorterDuff.Mode.MULTIPLY;
            case 14: return PorterDuff.Mode.SCREEN;
            case 12: return PorterDuff.Mode.ADD;
            case 15: return PorterDuff.Mode.OVERLAY;
        }
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @NonNull
    public static RippleDrawable createRippleDrawable(Drawable backgroundDrawable, int[][] states, int rippleColor) {
        return new AlwaysStatefulRippleDrawable(new ColorStateList(states, new int[]{rippleColor}),
                backgroundDrawable, null);
    }

    @Nullable
    public static Drawable getDrawableFromAttributes(
            @NonNull Context context, @NonNull TypedArray attributes, @StyleableRes int index) {
        if (attributes.hasValue(index)) {
            int resourceId = attributes.getResourceId(index, 0);
            if (resourceId != 0) {
                Drawable value = getDrawableByResourceId(context, resourceId);
                if (value != null) {
                    return value;
                }
            }
        }
        return attributes.getDrawable(index);
    }

    public static Drawable getDrawableByResourceId(Context context,@DrawableRes int resourceId){
        if (resourceId != 0) {
            return AppCompatResources.getDrawable(context, resourceId);
        }
        return null;
    }


    public static int getColorByAttributeId(Resources.Theme theme, int attrId){
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attrId, typedValue,true);
        return typedValue.data;
    }

    public static float dpToPx(Context context, float dp) {
        if (dp == 0){
            return 0;
        } else {
            return dp * context.getResources().getDisplayMetrics().density;
        }
    }

}
