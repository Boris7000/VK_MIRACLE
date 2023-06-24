package com.miracle.engine.fragment;

import static com.miracle.engine.util.DimensionsUtil.getPxDpByAttributeId;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.miracle.engine.R;

public class ScrollAndElevate {
    public static void scrollAndElevate(final NestedScrollView scrollView, final AppBarLayout appBarLayout){
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if(!scrollView.canScrollVertically(-1)) {
                appBarLand(appBarLayout);
            } else {
                appBarElevate(appBarLayout, appBarLayout.getContext());
            }
        });
    }

    public static void scrollAndElevate(RecyclerView recyclerView, final AppBarLayout appBarLayout){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)) {
                    appBarLand(appBarLayout);
                } else {
                    appBarElevate(appBarLayout, appBarLayout.getContext());
                }
            }
        });
    }

    public  static  void appBarLand(AppBarLayout appBarLayout){
        appBarLayout.animate().translationZ(0).start();
    }

    public  static  void appBarElevate(AppBarLayout appBarLayout, Context context){
        float dimension = getPxDpByAttributeId(context, R.attr.globalTranslationZ);
        if(appBarLayout.getTranslationZ()!=dimension) {
            appBarLayout.animate().translationZ(dimension).start();
        }
    }

}
