package com.miracle.engine.fragment.tabs.templates;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.fragment.IScrollableFragment;
import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.fragment.tabs.ITabsFragment;
import com.miracle.engine.fragment.tabs.TabsFragmentController;

public abstract class TabsFragment extends MiracleFragment implements ITabsFragment, IScrollableFragment {

    private final TabsFragmentController<TabsFragment> tabsController =
            new TabsFragmentController<TabsFragment>(this){};

    @Override
    public TabsFragmentController<?> getTabsFragmentController() {
        return tabsController;
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        getTabsFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        getTabsFragmentController().initViews(rootView, savedInstanceState);
    }

    @Override
    public boolean notTop(){
        return getTabsFragmentController().notTop();
    }

    @Override
    public void scrollToTop() {
        getTabsFragmentController().scrollToTop();
    }

    @CallSuper
    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        getTabsFragmentController().onClearSavedInstance(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        getTabsFragmentController().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

}
