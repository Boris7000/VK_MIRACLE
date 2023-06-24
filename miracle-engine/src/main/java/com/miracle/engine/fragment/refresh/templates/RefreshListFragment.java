package com.miracle.engine.fragment.refresh.templates;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.fragment.refresh.IRefreshFragment;
import com.miracle.engine.fragment.refresh.RefreshFragmentController;
import com.miracle.engine.fragment.list.templates.ListFragment;

public abstract class RefreshListFragment extends ListFragment implements IRefreshFragment {

    private final RefreshFragmentController<RefreshListFragment> refreshController =
            new RefreshFragmentController<RefreshListFragment>(this){};

    @Override
    public RefreshFragmentController<?> getRefreshFragmentController() {
        return refreshController;
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        getRefreshFragmentController().findViews(rootView);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        getRefreshFragmentController().initViews(rootView, savedInstanceState);
    }

}
