package com.miracle.engine.fragment.search;

import android.widget.EditText;

public interface ISearchFragment {

    SearchFragmentController<?> getSearchFragmentController();

    default EditText getSearchEditText(){return getSearchFragmentController().getSearchEditText();}

    default boolean autoSearch(){return true;}

    default boolean searchEmptyInitialQuery(){return true;}

    default void searchQuery(String query){getSearchFragmentController().searchQuery(query);}

    void onSearch(String query);

}
