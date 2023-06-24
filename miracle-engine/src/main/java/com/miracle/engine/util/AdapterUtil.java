package com.miracle.engine.util;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.miracle.engine.adapter.StableGridLayoutManager;
import com.miracle.engine.adapter.StableLinearLayoutManager;

public class AdapterUtil {
    public static LinearLayoutManager getHorizontalLayoutManager(Context context){
        return new StableLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
    }

    public static GridLayoutManager getHorizontalGridLayoutManager(Context context, int spanCount){
        return new StableGridLayoutManager(context, spanCount, LinearLayoutManager.HORIZONTAL,false);
    }

    public static LinearLayoutManager getVerticalLayoutManager(Context context){
        return getVerticalLayoutManager(context, false);
    }

    public static GridLayoutManager getVerticalGridLayoutManager(Context context, int spanCount){
        return new StableGridLayoutManager(context, spanCount);
    }

    public static LinearLayoutManager getVerticalLayoutManager(Context context, boolean reverse){
        return new StableLinearLayoutManager(context, LinearLayoutManager.VERTICAL,reverse);
    }
}
