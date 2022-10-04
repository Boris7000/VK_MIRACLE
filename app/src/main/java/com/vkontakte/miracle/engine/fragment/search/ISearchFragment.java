package com.vkontakte.miracle.engine.fragment.search;

import android.widget.EditText;

public interface ISearchFragment {

    SearchFragmentController requestSearchFragmentController();

    SearchFragmentController getSearchFragmentController();

    EditText getSearchEditText();

    void setQuery(String query);

    void setContextQuery(String contextQuery, String query);

    void onSearch(String query);

    void onContextSearch(String contextQuery);

}
