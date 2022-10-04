package com.vkontakte.miracle.engine.fragment.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.IMiracleFragment;
import com.vkontakte.miracle.engine.fragment.ScrollAndElevate;
import com.vkontakte.miracle.engine.fragment.MiracleFragmentController;

public abstract class BaseFragmentController extends MiracleFragmentController {

    private final IBaseFragment baseFragment;
    private AppBarLayout appBarLayout;
    private Toolbar toolBar;
    private TextView title;

    protected BaseFragmentController(IMiracleFragment miracleFragment) {
        super(miracleFragment);
        baseFragment = (IBaseFragment) miracleFragment;
    }

    @Override
    public void onCreateView(@NonNull View rootView, Bundle savedInstanceState){
        if(baseFragment.needChangeTitleText()){
            setTitleText(baseFragment.requestTitleText());
        }
    }

    @Override
    public void findViews(@NonNull View rootView){
        appBarLayout = rootView.findViewById(R.id.appbarlayout);
        toolBar = rootView.findViewById(R.id.toolbar);
        title = rootView.findViewById(R.id.title);
    }

    @Override
    public void initViews(){
        if(appBarLayout!=null) {
            appBarLayout.setOnClickListener(view -> getMiracleFragment().scrollToTop());
        }
    }

    public AppBarLayout getAppBarLayout(){
        return appBarLayout;
    }

    public Toolbar getToolBar() {
        return toolBar;
    }

    public TextView getTitle(){
        return title;
    }

    public void setTitleText(String titleText){
        if(title!=null) {
            title.setText(titleText);
        } else {
            if(toolBar!=null){
                toolBar.setTitle(titleText);
            }
        }
    }

    @CallSuper
    public void expandAppBar(){
        AppBarLayout appBarLayout = getAppBarLayout();
        if (appBarLayout != null) {
            appBarLayout.setExpanded(true);
            if(baseFragment.scrollAndElevateEnabled()) {
                ScrollAndElevate.appBarLand(appBarLayout);
            }
        }
    }
}
