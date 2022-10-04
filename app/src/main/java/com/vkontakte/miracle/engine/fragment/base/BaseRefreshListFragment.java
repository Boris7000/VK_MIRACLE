package com.vkontakte.miracle.engine.fragment.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.vkontakte.miracle.engine.fragment.ScrollAndElevate;
import com.vkontakte.miracle.engine.fragment.refresh.RefreshListFragment;

public abstract class BaseRefreshListFragment extends RefreshListFragment implements IBaseFragment{

    private final BaseFragmentController baseFragmentController = requestBaseFragmentController();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        getBaseFragmentController().onCreateView(rootView, savedInstanceState);

        if(scrollAndElevateEnabled()) {
            ScrollAndElevate.scrollAndElevate(getScrollView(), getAppBarLayout(), getMiracleActivity());
        }

        return rootView;
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        getBaseFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews() {
        super.initViews();
        getBaseFragmentController().initViews();
    }

    @Override
    public BaseFragmentController requestBaseFragmentController() {
        return new BaseFragmentController(this){};
    }

    @Override
    public BaseFragmentController getBaseFragmentController() {
        return baseFragmentController;
    }

    @Override
    public AppBarLayout getAppBarLayout() {
        return getBaseFragmentController().getAppBarLayout();
    }

    @Override
    public Toolbar getToolBar() {
        return getBaseFragmentController().getToolBar();
    }

    @Override
    public TextView getTitle() {
        return getBaseFragmentController().getTitle();
    }

    @Override
    public boolean scrollAndElevateEnabled() {
        return true;
    }

    @Override
    public boolean needChangeTitleText() {
        return false;
    }

    @Override
    public String requestTitleText() {
        return "";
    }

}
