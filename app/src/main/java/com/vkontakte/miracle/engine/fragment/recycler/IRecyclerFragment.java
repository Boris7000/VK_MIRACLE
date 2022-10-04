package com.vkontakte.miracle.engine.fragment.recycler;

import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public interface IRecyclerFragment {

    RecyclerFragmentController requestRecyclerFragmentController();

    RecyclerFragmentController getRecyclerFragmentController();

    RecyclerView getRecyclerView();

    SwipeRefreshLayout getSwipeRefreshLayout();

    ProgressBar getProgressBar();

    RecyclerView.Adapter<?> onCreateRecyclerAdapter();

    void onRecyclerAdapterStateChange(int state);

    boolean reverseRecyclerAdapter();

    boolean saveRecyclerAdapterSate();

    boolean autoSetRecyclerAdapter();

    void onHide();

    void onShow();

}
