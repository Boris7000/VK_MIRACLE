package com.vkontakte.miracle.engine.fragment;

import android.view.View;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

public class SimpleMiracleFragment extends ListMiracleFragment{

    private LinearLayout topBar;
    private AppBarLayout appBarLayout;
    private TextView title;
    private boolean scrollAndElevate = true;

    public void setTopBar(LinearLayout topBar){
        setTopBar(topBar, v -> scrollToTop());
    }

    public void setTopBar(LinearLayout topBar, View.OnClickListener onClickListener){
        topBar.setOnClickListener(onClickListener);
        this.topBar = topBar;
    }

    public LinearLayout getTopBar(){
        return topBar;
    }

    //////////////////////////////////////////////////

    public void setAppBarLayout(AppBarLayout appBarLayout){
        this.appBarLayout = appBarLayout;
    }

    public AppBarLayout getAppBarLayout(){
        return appBarLayout;
    }

    public void disableScrollAndElevate(){
        scrollAndElevate = false;
    }

    //////////////////////////////////////////////////

    public void setTitle(TextView title){
        this.title = title;
    }

    public void setTitleText(String titleText){
        if(title!=null) {
            title.setText(titleText);
        }
    }

    public TextView getTitle(){
        return title;
    }

    //////////////////////////////////////////////////

    public void setBackClick(View view){
        view.setOnClickListener(v -> getMiracleActivity().fragmentBack());
    }

    @Override
    public void hide(boolean animate) {
        super.hide(animate);

        if (appBarLayout != null) {
            appBarLayout.setExpanded(true);
            if(scrollAndElevate) {
                ScrollAndElevate.appBarLand(appBarLayout);
            }
        }
    }

    @Override
    public void scrollToTop() {

        super.scrollToTop();

        if (appBarLayout != null) {
            appBarLayout.setExpanded(true);
            if(scrollAndElevate) {
                ScrollAndElevate.appBarLand(appBarLayout);
            }
        }
    }

}
