package com.miracle.engine.fragment.base.templates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.R;
import com.miracle.engine.fragment.ScrollAndElevate;
import com.miracle.engine.fragment.base.BaseFragmentController;
import com.miracle.engine.fragment.base.IBaseFragment;
import com.miracle.engine.fragment.refresh.templates.RefreshListFragment;

public abstract class BaseRefreshListFragment extends RefreshListFragment implements IBaseFragment {

    private final BaseFragmentController<BaseRefreshListFragment> baseController =
            new BaseFragmentController<BaseRefreshListFragment>(this){};

    @Override
    public BaseFragmentController<?> getBaseFragmentController() {
        return baseController;
    }


    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_refresh_list_base, container, false);
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        getBaseFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        super.initViews(rootView, savedInstanceState);
        getBaseFragmentController().initViews(rootView, savedInstanceState);
        if(scrollAndElevateEnabled()) {
            ScrollAndElevate.scrollAndElevate(getScrollView(), getAppBarLayout());
        }
    }

    @Override
    public void scrollToTop() {
        super.scrollToTop();
        getBaseFragmentController().expandAppBar();
    }

    @Override
    public boolean notTop() {
        return !getBaseFragmentController().appbarExpanded() || super.notTop();
    }

    @CallSuper
    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        getBaseFragmentController().readSavedInstance(savedInstanceState);
    }

    @CallSuper
    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        getBaseFragmentController().onClearSavedInstance(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        getBaseFragmentController().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

}
