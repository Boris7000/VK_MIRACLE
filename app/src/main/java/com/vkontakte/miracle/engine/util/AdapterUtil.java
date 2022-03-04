package com.vkontakte.miracle.engine.util;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.engine.adapter.StableLinearLayoutManager;

public class AdapterUtil {
    public static RecyclerView.LayoutManager getHorizontalLayoutManager(Context context){
        return new StableLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
    }

    public static RecyclerView.LayoutManager getVerticalLayoutManager(Context context){
        return getVerticalLayoutManager(context, false);
    }

    public static RecyclerView.LayoutManager getVerticalLayoutManager(Context context, boolean reverse){
        return new StableLinearLayoutManager(context, LinearLayoutManager.VERTICAL,reverse);
    }
}
