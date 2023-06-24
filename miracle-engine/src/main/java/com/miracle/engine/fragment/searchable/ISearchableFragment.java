package com.miracle.engine.fragment.searchable;

import android.view.View;

public interface ISearchableFragment {

    SearchableFragmentController<?> getSearchableFragmentController();

    default View getSearchButton(){return getSearchableFragmentController().getSearchButton();}

    default void onSearchButtonClicked(){}

}
