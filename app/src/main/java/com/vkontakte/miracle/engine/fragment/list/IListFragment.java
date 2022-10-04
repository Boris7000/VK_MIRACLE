package com.vkontakte.miracle.engine.fragment.list;

import androidx.core.widget.NestedScrollView;

public interface IListFragment {

    ListFragmentController requestListFragmentController();

    ListFragmentController getListFragmentController();

    NestedScrollView getScrollView();

}
