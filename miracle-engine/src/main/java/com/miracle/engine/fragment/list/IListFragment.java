package com.miracle.engine.fragment.list;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

public interface IListFragment {

    ListFragmentController<?> getListFragmentController();

    default NestedScrollView getScrollView(){return getListFragmentController().getScrollView();}

    default void inflateContent(@NonNull LayoutInflater inflater, @NonNull LinearLayout container){}

}
