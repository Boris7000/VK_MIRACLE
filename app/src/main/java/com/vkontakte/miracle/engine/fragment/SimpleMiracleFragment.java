package com.vkontakte.miracle.engine.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

public class SimpleMiracleFragment extends ListMiracleFragment{

    private AppBarLayout appBarLayout;
    private Toolbar toolBar;
    private TextView title;
    private boolean scrollAndElevate = true;

    //////////////////////////////////////////////////
    public void setBackClick(){
        setToolBarNavigationClickListener(v -> getMiracleActivity().fragmentBack());
    }

    public void setToolBarNavigationClickListener(View.OnClickListener onClickListener){
        toolBar.setNavigationOnClickListener(onClickListener);
    }

    public void setAppbarClickToTop(){
        setAppbarClickListener(v -> scrollToTop());
    }

    public void setAppbarClickListener(View.OnClickListener onClickListener){
        toolBar.setOnClickListener(onClickListener);
    }

    public void setToolBar(Toolbar toolBar) {
        this.toolBar = toolBar;
    }

    public Toolbar getToolBar() {
        return toolBar;
    }

    public void setAppBarLayout(AppBarLayout appBarLayout){
        this.appBarLayout = appBarLayout;
        //appBarLayout.setStatusBarForeground(MaterialShapeDrawable.createWithElevationOverlay(requireContext()));
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
        if(toolBar!=null){
            toolBar.setTitle(titleText);
        }
        if(title!=null) {
            title.setText(titleText);
        }
    }

    public TextView getTitle(){
        return title;
    }

    //////////////////////////////////////////////////



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
