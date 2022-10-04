package com.vkontakte.miracle.engine.fragment.refresh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vkontakte.miracle.engine.fragment.list.ListFragment;

public abstract class RefreshListFragment extends ListFragment implements IRefreshFragment{

    private final RefreshFragmentController refreshFragmentController = requestRefreshFragmentController();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        getRefreshFragmentController().onCreateView(rootView, savedInstanceState);

        return rootView;
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        getRefreshFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews() {
        super.initViews();
        getRefreshFragmentController().initViews();
    }

    @Override
    public RefreshFragmentController requestRefreshFragmentController() {
        return new RefreshFragmentController(this){};
    }

    @Override
    public RefreshFragmentController getRefreshFragmentController() {
        return refreshFragmentController;
    }

    @Override
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return getRefreshFragmentController().getSwipeRefreshLayout();
    }

}
