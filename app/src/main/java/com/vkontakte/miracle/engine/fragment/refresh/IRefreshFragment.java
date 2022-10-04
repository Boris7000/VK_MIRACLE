package com.vkontakte.miracle.engine.fragment.refresh;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public interface IRefreshFragment {

    RefreshFragmentController requestRefreshFragmentController();

    RefreshFragmentController getRefreshFragmentController();

    SwipeRefreshLayout getSwipeRefreshLayout();

    SwipeRefreshLayout.OnRefreshListener requestOnRefreshListener();

}
