package com.miracle.engine.fragment.search.templates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.R;
import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.miracle.engine.fragment.search.ISearchFragment;
import com.miracle.engine.fragment.search.SearchFragmentController;

public abstract class SearchBaseRecyclerFragment extends BaseRecyclerFragment implements ISearchFragment {

    private final SearchFragmentController<SearchBaseRecyclerFragment> searchController =
            new SearchFragmentController<SearchBaseRecyclerFragment>(this){};

    @Override
    public SearchFragmentController<?> getSearchFragmentController() {
        return searchController;
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
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        super.initViews(rootView, savedInstanceState);
        getSearchFragmentController().initViews(rootView, savedInstanceState);
    }

    @Override
    public boolean needChangeTitleText() {
        return false;
    }

    @Override
    public boolean autoSetRecyclerAdapter() {
        return false;
    }

    @Override
    public void onSearch(String query) {
        getRecyclerFragmentController().setRecyclerAdapter(onCreateRecyclerAdapter());
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        getSearchFragmentController().readSavedInstance(savedInstanceState);
    }

    @Override
    public void readBundleArguments(Bundle arguments) {
        super.readBundleArguments(arguments);
        getSearchFragmentController().readBundleArguments(arguments);
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        getSearchFragmentController().onClearSavedInstance(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSearchFragmentController().onSaveInstanceState(outState);
    }



}
