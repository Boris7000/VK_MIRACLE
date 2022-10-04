package com.miracle.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.appcompat.content.res.AppCompatResources;

public class Utils {

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
