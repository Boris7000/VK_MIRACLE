package com.vkontakte.miracle.engine.fragment;

import static com.vkontakte.miracle.engine.util.DimensionsUtil.getDimensionByAttributeId;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.vkontakte.miracle.R;

public class ScrollAndElevate {
    public static void scrollAndElevate(final NestedScrollView scrollView, final AppBarLayout appBarLayout, final Context context){
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if(!scrollView.canScrollVertically(-1)) {
                appBarLand(appBarLayout);
            } else {
                appBarElevate(appBarLayout,context);
            }
        });
    }

    public static void scrollAndElevate(RecyclerView recyclerView, final AppBarLayout appBarLayout, final Context context){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)) {
                    appBarLand(appBarLayout);
                } else {
                    appBarElevate(appBarLayout, context);
                }
            }
        });
    }

    public  static  void appBarLand(AppBarLayout appBarLayout){
        appBarLayout.animate().translationZ(0).start();
    }

    public  static  void appBarElevate(AppBarLayout appBarLayout, Context context){
        float dimension = context.getResources().getDimension(getDimensionByAttributeId(context, R.attr.globalTranslationZ));
        if(appBarLayout.getTranslationZ()!=dimension) {
            appBarLayout.animate().translationZ(dimension).start();
        }
    }

}
