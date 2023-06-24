package com.miracle.engine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MarginLayoutParamsCompat;

import com.google.android.material.tabs.TabLayout;
import com.miracle.engine.util.DimensionsUtil;

public class MiracleTabLayout extends TabLayout {

    public MiracleTabLayout(@NonNull Context context) {
        super(context);
    }

    public MiracleTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addTab(@NonNull Tab tab) {
        super.addTab(tab);
        updateLastTab();
    }

    private void updateLastTab(){
        if(getChildCount()>0){
            Tab tab = getTabAt(getTabCount()-1);
            updateTab(tab);
        }
    }

    public void updateTab(Tab tab){
        if(tab!=null){
            TabView tabView = tab.view;

            ImageView imageView = tabView.findViewById(android.R.id.icon);
            TextView textView = tabView.findViewById(android.R.id.text1);
            MarginLayoutParams ilp = ((MarginLayoutParams) imageView.getLayoutParams());


            if(tab.getText()!=null&&tab.getIcon()!=null){

                textView.setVisibility(VISIBLE);
                imageView.setVisibility(VISIBLE);

                MarginLayoutParamsCompat.setMarginStart(ilp, (int) DimensionsUtil.dpToPx(getContext(),8));
                MarginLayoutParamsCompat.setMarginEnd(ilp, 0);
                imageView.setLayoutParams(ilp);
                imageView.requestLayout();
            } else {
                if(tab.getText()!=null){
                    textView.setVisibility(VISIBLE);
                    imageView.setVisibility(GONE);
                } else {
                    if(tab.getIcon()!=null){
                        textView.setVisibility(GONE);
                        imageView.setVisibility(VISIBLE);

                        MarginLayoutParamsCompat.setMarginStart(ilp, (int) DimensionsUtil.dpToPx(getContext(), 0));
                        MarginLayoutParamsCompat.setMarginEnd(ilp, 0);
                        imageView.setLayoutParams(ilp);
                        imageView.requestLayout();
                    } else {
                        textView.setVisibility(GONE);
                        imageView.setVisibility(GONE);
                    }
                }
            }
        }
    }

}
