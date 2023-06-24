package com.miracle.engine.fragment.base.templates;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.R;
import com.miracle.engine.fragment.ScrollAndElevate;
import com.miracle.engine.fragment.base.BaseFragmentController;
import com.miracle.engine.fragment.base.IBaseFragment;
import com.miracle.engine.fragment.recycler.IRecyclerFragment;
import com.miracle.engine.fragment.tabs.templates.TabsFragment;

public abstract class BaseTabsFragment extends TabsFragment implements IBaseFragment {

    private final BaseFragmentController<BaseTabsFragment> baseController =
            new BaseFragmentController<BaseTabsFragment>(this){};

    @Override
    public BaseFragmentController<?> getBaseFragmentController() {
        return baseController;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_tabs_base, container, false);
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
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        if(scrollAndElevateEnabled()) {
            Fragment miracleFragment = getTabsFragmentController().getFragmentAt(position);
            if (miracleFragment instanceof IRecyclerFragment) {
                IRecyclerFragment recyclerFragment = (IRecyclerFragment) miracleFragment;
                RecyclerView recyclerView = recyclerFragment.getRecyclerView();
                if (recyclerView != null) {
                    if (recyclerView.canScrollVertically(-1)) {
                        Context context = getContext();
                        if(context!=null) {
                            ScrollAndElevate.appBarElevate(getAppBarLayout(), context);
                        }
                    } else {
                        ScrollAndElevate.appBarLand(getAppBarLayout());
                    }
                }
            }
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

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        getBaseFragmentController().onClearSavedInstance(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        getBaseFragmentController().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

}
