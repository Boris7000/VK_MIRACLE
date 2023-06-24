package com.vkontakte.miracle.engine.util;

import static com.miracle.engine.util.TimeUtil.getLowDateString;
import static com.vkontakte.miracle.engine.util.StringsUtil.getOnlineSexDeclensions;

import android.content.Context;

import com.google.android.material.transition.MaterialArcMotion;
import com.google.android.material.transition.MaterialContainerTransform;
import com.vkontakte.miracle.R;

public class TimeUtil {

    public static String getOnlineDateString(Context context, long dateSec, int sex){
        return String.format(getOnlineSexDeclensions(context, sex),getLowDateString(context, dateSec));
    }

    public static String getUpdatedDateString(Context context, long dateSec){
        return String.format(context.getString(R.string.last_update), getLowDateString(context, dateSec));
    }

    public static MaterialContainerTransform buildContainerTransform(Context context, boolean entering, int drawingViewId) {
        MaterialContainerTransform transform = new MaterialContainerTransform(context, entering);
        transform.setDrawingViewId(drawingViewId);
        transform.setPathMotion(new MaterialArcMotion());
        return transform;
    }
}
