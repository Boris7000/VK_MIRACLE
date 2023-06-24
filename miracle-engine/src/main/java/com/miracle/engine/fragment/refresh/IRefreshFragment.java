package com.miracle.engine.fragment.refresh;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.miracle.engine.fragment.list.IListFragment;

public interface IRefreshFragment extends IListFragment {

    RefreshFragmentController<?> getRefreshFragmentController();

    default SwipeRefreshLayout getSwipeRefreshLayout(){return getRefreshFragmentController().getSwipeRefreshLayout();}

    default SwipeRefreshLayout.OnRefreshListener requestOnRefreshListener(){return null;}

}
