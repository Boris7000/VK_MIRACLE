package com.miracle.engine.util;

import static com.miracle.engine.util.ColorUtil.getColorByAttributeId;

import android.content.Context;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.miracle.engine.R;

public class FragmentUtil {

    public static void setDefaultSwipeRefreshLayoutStyle(SwipeRefreshLayout swipeRefreshLayout, Context context){
        swipeRefreshLayout.setColorSchemeColors(getColorByAttributeId(context,
                R.attr.colorPrimary), getColorByAttributeId(context, R.attr.colorSecondary));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getColorByAttributeId(context,
                R.attr.swipeRefreshCircleColor));
        swipeRefreshLayout.setProgressViewOffset(true, 0, 1);
        swipeRefreshLayout.setProgressViewEndTarget(true, 64);
    }

}
