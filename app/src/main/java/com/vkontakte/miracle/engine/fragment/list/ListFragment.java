package com.vkontakte.miracle.engine.fragment.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.vkontakte.miracle.engine.fragment.MiracleFragment;

public abstract class ListFragment extends MiracleFragment implements IListFragment {

    private final ListFragmentController listFragmentController = requestListFragmentController();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        getListFragmentController().onCreateView(rootView, savedInstanceState);

        return rootView;
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        getListFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews() {
        getListFragmentController().initViews();
    }

    @Override
    public ListFragmentController requestListFragmentController() {
        return new ListFragmentController(this){};
    }

    @Override
    public ListFragmentController getListFragmentController() {
        return listFragmentController;
    }

    @Override
    public NestedScrollView getScrollView() {
        return getListFragmentController().getScrollView();
    }

    @Override
    public void scrollToTop() {
        getListFragmentController().scrollToTop();
    }

    @Override
    public boolean notTop() {
        return getListFragmentController().notTop();
    }
}
