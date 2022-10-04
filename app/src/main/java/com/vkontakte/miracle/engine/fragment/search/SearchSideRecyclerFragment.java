package com.vkontakte.miracle.engine.fragment.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.side.SideRecyclerFragment;

public class SearchSideRecyclerFragment extends SideRecyclerFragment implements ISearchFragment{

    private final SearchFragmentController searchFragmentController = requestSearchFragmentController();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        getSearchFragmentController().onCreateView(rootView, savedInstanceState);

        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_recycleview_search, container, false);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        getSearchFragmentController().findViews(rootView);
    }

    @Override
    public void initViews() {
        super.initViews();
        getSearchFragmentController().initViews();
    }

    @Override
    public SearchFragmentController requestSearchFragmentController() {
        return new SearchFragmentController(this) {};
    }

    @Override
    public SearchFragmentController getSearchFragmentController() {
        return searchFragmentController;
    }

    @Override
    public EditText getSearchEditText() {
        return getSearchFragmentController().getSearchEditText();
    }

    @Override
    public boolean needChangeTitleText() {
        return false;
    }

    @Override
    public void setQuery(String query) {
        getSearchFragmentController().setQuery(query);
    }

    @Override
    public void setContextQuery(String contextQuery, String query) {
        getSearchFragmentController().setContextQuery(contextQuery, query);
    }

    @Override
    public void onSearch(String query) {
        getRecyclerFragmentController().setRecyclerAdapter(onCreateRecyclerAdapter());
    }

    @Override
    public void onContextSearch(String contextQuery) {
        getRecyclerFragmentController().setRecyclerAdapter(onCreateRecyclerAdapter());
    }

    @CallSuper
    @Override
    public void readBundleArguments(Bundle arguments) {
        getSearchFragmentController().readBundleArguments(arguments);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSearchFragmentController().onViewCreated(view, savedInstanceState);
    }

}
