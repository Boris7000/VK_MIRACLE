package com.miracle.engine.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

public class DimensionsUtil {

    public static int getDpByAttributeId(Context context, int resId){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        if(theme.resolveAttribute(resId, typedValue,true)){
            return typedValue.data;
        }
        return 0;
    }

    public static int getPxDpByAttributeId(Context context, int resId){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        if(theme.resolveAttribute(resId, typedValue,true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data,
                    context.getResources().getDisplayMetrics());
        }
        return 0;
    }

    public static float dpToPx(@NonNull Context context, @Dimension(unit = Dimension.DP) int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static float pxToDp(Context context, float px){
        if (px == 0){
            return 0;
        } else{
            return px/context.getResources().getDisplayMetrics().density;
        }
    }


}
