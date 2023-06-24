package com.miracle.engine.fragment.searchable.templates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.R;
import com.miracle.engine.fragment.base.templates.BaseTabsFragment;
import com.miracle.engine.fragment.searchable.ISearchableFragment;
import com.miracle.engine.fragment.searchable.SearchableFragmentController;

public abstract class SearchableBaseTabsFragment extends BaseTabsFragment implements ISearchableFragment {

    private final SearchableFragmentController<SearchableBaseTabsFragment> searchableController =
            new SearchableFragmentController<SearchableBaseTabsFragment>(this){};

    @Override
    public SearchableFragmentController<?> getSearchableFragmentController() {
        return searchableController;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_tabs_sb_base, container, false);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        getSearchableFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        super.initViews(rootView, savedInstanceState);
        getSearchableFragmentController().initViews(rootView, savedInstanceState);
    }

    @Override
    public boolean needChangeTitleText() {
        return false;
    }

}
