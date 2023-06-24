package com.miracle.engine.fragment.list.templates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.fragment.IScrollableFragment;
import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.fragment.list.IListFragment;
import com.miracle.engine.fragment.list.ListFragmentController;

public abstract class ListFragment extends MiracleFragment implements IListFragment, IScrollableFragment {

    private final ListFragmentController<ListFragment> listController =
            new ListFragmentController<ListFragment>(this){};

    @Override
    public ListFragmentController<?> getListFragmentController() {
        return listController;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        getListFragmentController().onCreateView(inflater, rootView, savedInstanceState);

        return rootView;
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
