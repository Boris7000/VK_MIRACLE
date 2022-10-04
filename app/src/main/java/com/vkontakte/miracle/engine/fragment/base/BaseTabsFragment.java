package com.vkontakte.miracle.engine.fragment.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.vkontakte.miracle.engine.fragment.IMiracleFragment;
import com.vkontakte.miracle.engine.fragment.ScrollAndElevate;
import com.vkontakte.miracle.engine.fragment.recycler.IRecyclerFragment;
import com.vkontakte.miracle.engine.fragment.tabs.TabsFragment;

public abstract class BaseTabsFragment extends TabsFragment implements IBaseFragment {

    private final BaseFragmentController baseFragmentController = requestBaseFragmentController();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        getBaseFragmentController().onCreateView(rootView, savedInstanceState);

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
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        if(scrollAndElevateEnabled()) {
            IMiracleFragment miracleFragment = (IMiracleFragment) getTabsFragmentController().getFragmentAt(position);
            if (miracleFragment != null) {
                if (miracleFragment instanceof IRecyclerFragment) {
                    IRecyclerFragment recyclerFragment = (IRecyclerFragment) miracleFragment;
                    RecyclerView recyclerView = recyclerFragment.getRecyclerView();
                    if (recyclerView != null) {
                        if (recyclerView.canScrollVertically(-1)) {
                            ScrollAndElevate.appBarElevate(getAppBarLayout(), getMiracleActivity());
                        } else {
                            ScrollAndElevate.appBarLand(getAppBarLayout());
                        }
                    }
                }
            }
        }
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

    @CallSuper
    @Override
    public void scrollToTop() {
        super.scrollToTop();
        if(scrollAndElevateEnabled()) {
            AppBarLayout appBarLayout = getAppBarLayout();
            if (appBarLayout != null) {
                appBarLayout.setExpanded(true);
                if (scrollAndElevateEnabled()) {
                    ScrollAndElevate.appBarLand(appBarLayout);
                }
            }
        }
    }

}
