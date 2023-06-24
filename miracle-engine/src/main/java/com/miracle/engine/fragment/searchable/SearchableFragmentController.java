package com.miracle.engine.fragment.searchable;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miracle.engine.R;
import com.miracle.engine.fragment.MiracleFragmentController;

public abstract class SearchableFragmentController<T extends Fragment & ISearchableFragment> extends MiracleFragmentController<T> {

    private View searchButton;

    protected SearchableFragmentController(T fragment) {
        super(fragment);
    }

    @Override
    public void findViews(@NonNull View rootView){
        searchButton = rootView.findViewById(R.id.searchBar);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        if(searchButton!=null) {
            searchButton.setOnClickListener(view -> getFragment().onSearchButtonClicked());
        }
    }

    ///////////////////////////////////////////////////////

    public final View getSearchButton(){
        return searchButton;
    }

}
