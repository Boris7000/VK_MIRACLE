package com.miracle.engine.fragment.recycler.templates;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.fragment.IScrollableFragment;
import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.fragment.recycler.IRecyclerFragment;
import com.miracle.engine.fragment.recycler.RecyclerFragmentController;

public abstract class RecyclerFragment extends MiracleFragment implements IRecyclerFragment, IScrollableFragment {

    private final RecyclerFragmentController<RecyclerFragment> recyclerController =
            new RecyclerFragmentController<RecyclerFragment>(this){};

    @Override
    public RecyclerFragmentController<?> getRecyclerFragmentController() {
        return recyclerController;
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        getRecyclerFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        getRecyclerFragmentController().initViews(rootView, savedInstanceState);
    }

    @Override
    public void scrollToTop() {
        getRecyclerFragmentController().scrollToTop();
    }

    @Override
    public boolean notTop() {
        return getRecyclerFragmentController().notTop();
    }

    @CallSuper
    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        getRecyclerFragmentController().onClearSavedInstance(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        getRecyclerFragmentController().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @CallSuper
    @Override
    public void onDestroy() {
        getRecyclerFragmentController().onDestroy();
        super.onDestroy();
    }

}
