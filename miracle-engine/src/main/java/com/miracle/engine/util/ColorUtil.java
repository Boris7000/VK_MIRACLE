package com.miracle.engine.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.core.content.res.ResourcesCompat;

public class ColorUtil {

    public static int getColorByAttributeId(Context context, int attrId){
        return getColorByAttributeId(context.getTheme(), attrId);
    }

    public static int getColorByAttributeId(Resources.Theme theme, int attrId){
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attrId, typedValue,true);
        return typedValue.data;
    }

    public static int getColorByResId(Context context, int resId){
        return ResourcesCompat.getColor(context.getResources(), resId, context.getTheme());
    }
}
