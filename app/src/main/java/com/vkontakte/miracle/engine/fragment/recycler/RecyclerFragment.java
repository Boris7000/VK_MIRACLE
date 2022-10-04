package com.vkontakte.miracle.engine.fragment.recycler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.vkontakte.miracle.engine.fragment.MiracleFragment;

public abstract class RecyclerFragment extends MiracleFragment implements IRecyclerFragment {

    private final RecyclerFragmentController recyclerFragmentController = requestRecyclerFragmentController();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        getRecyclerFragmentController().onCreateView(rootView, savedInstanceState);

        return rootView;
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        getRecyclerFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews() {
        getRecyclerFragmentController().initViews();
    }

    @Override
    public RecyclerFragmentController requestRecyclerFragmentController() {
        return new RecyclerFragmentController(this) {};
    }

    @Override
    public RecyclerFragmentController getRecyclerFragmentController() {
        return recyclerFragmentController;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return getRecyclerFragmentController().getRecyclerView();
    }

    @Override
    public ProgressBar getProgressBar() {
        return getRecyclerFragmentController().getProgressBar();
    }

    @Override
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return getRecyclerFragmentController().getSwipeRefreshLayout();
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return null;
    }

    @Override
    public void onRecyclerAdapterStateChange(int state) {

    }

    @Override
    public boolean reverseRecyclerAdapter() {
        return false;
    }

    @Override
    public boolean saveRecyclerAdapterSate() {
        return true;
    }

    @Override
    public boolean autoSetRecyclerAdapter() {
        return true;
    }

    @Override
    public void onHide() {}

    @Override
    public void onShow() {}

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

    @CallSuper
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
