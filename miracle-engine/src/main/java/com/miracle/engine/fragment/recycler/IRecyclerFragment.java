package com.miracle.engine.fragment.recycler;

import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public interface IRecyclerFragment {

    RecyclerFragmentController<?> getRecyclerFragmentController();

    default RecyclerView getRecyclerView(){return getRecyclerFragmentController().getRecyclerView();}

    default SwipeRefreshLayout getSwipeRefreshLayout(){return getRecyclerFragmentController().getSwipeRefreshLayout();}

    default ProgressBar getProgressBar(){return getRecyclerFragmentController().getProgressBar();}

    default RecyclerView.Adapter<?> onCreateRecyclerAdapter(){return null;}

    default void onRecyclerAdapterStateChange(int state){}

    default boolean reverseRecyclerAdapter(){return false;}

    default boolean saveRecyclerAdapterSate(){return true;}

    default boolean autoSetRecyclerAdapter(){return true;}

    default void onHide(){}

    default void onShow(){}

}
