package com.vkontakte.miracle.engine.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DimensionsUtil {
    public static int getDimensionByAttributeId(Context context, int resid){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(resid,typedValue,false);
        return typedValue.data;
    }

    /*
    public static float dpToPx(float dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
    */

    public static float dpToPx(float dp, Context context) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(float px, Context context){
        if (px == 0){
            return 0;
        } else{
            return px/context.getResources().getDisplayMetrics().density;
        }
    }


}
