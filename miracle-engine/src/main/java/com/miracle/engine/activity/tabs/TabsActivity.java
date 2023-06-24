package com.miracle.engine.activity.tabs;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.miracle.engine.activity.MiracleActivity;

public abstract class TabsActivity extends MiracleActivity implements ITabsActivity{

    private final TabsActivityController tabsFragmentController = requestTabsActivityController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState==null){
            savedInstanceState = savedInstanceStateCrutch();
        }
        super.onCreate(savedInstanceState);
        getTabsActivityController().onCreate(savedInstanceState);
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        getTabsActivityController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews() {
        getTabsActivityController().initViews();
    }

    @CallSuper
    @Override
    public boolean addFragment(Fragment fragment){
        return getTabsActivityController().addFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        if (!getTabsActivityController().backPress()) {
            super.onBackPressed();
        }
    }

    @Override
    public TabsActivityController requestTabsActivityController() {
        return new TabsActivityController(this){};
    }

    @Override
    public TabsActivityController getTabsActivityController() {
        return tabsFragmentController;
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getTabsActivityController().onSaveInstanceState(outState);
    }


}
