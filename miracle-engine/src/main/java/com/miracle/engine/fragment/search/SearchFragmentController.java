package com.miracle.engine.fragment.search;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miracle.engine.R;
import com.miracle.engine.async.baseExecutors.SimpleTimer;
import com.miracle.engine.fragment.MiracleFragmentController;
import com.miracle.engine.util.IMEUtil;

public abstract class SearchFragmentController<T extends Fragment & ISearchFragment> extends MiracleFragmentController<T> {

    private EditText searchEditText;

    private String searchQuery = "";

    private SimpleTimer timer;

    private boolean handleETChange = true;

    private boolean searchFromArguments = false;

    protected SearchFragmentController(T fragment) {
        super(fragment);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        searchEditText = rootView.findViewById(R.id.searchEditText);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        if(searchEditText!=null) {
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    if(getFragment().autoSearch()&&handleETChange){
                        if(timer!=null){
                            timer.reset();
                        } else {
                            timer = new SimpleTimer(500, 250) {
                                @Override
                                public void onExecute(Boolean result) {
                                    timer = null;
                                    if (result) {
                                        searchFromEditText();
                                    }
                                }
                            };
                            timer.start();
                        }
                    }
                }
            };

            if(!searchQuery.isEmpty()){
                searchEditText.setText(searchQuery);
                if (searchFromArguments) {
                    searchFromArguments = false;
                    getFragment().onSearch(searchQuery);
                }
            } else {
                if(savedInstanceState==null) {
                    T fragment = getFragment();
                    if (fragment.searchEmptyInitialQuery()) {
                        fragment.onSearch(searchQuery);
                    }
                    Activity activity = getActivity();
                    if (activity != null && fragment.isVisible()) {
                        searchEditText.requestFocus();
                        IMEUtil.showKeyboard(searchEditText, activity);
                    }
                }
            }

            searchEditText.addTextChangedListener(textWatcher);

        }
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public final void searchFromEditText(){
        String query = searchEditText.getText().toString().trim();
        searchQuery(query, true);
    }

    public final EditText getSearchEditText() {
        return searchEditText;
    }

    public final void searchQuery(@Nullable String query){
        searchQuery(query, false);
    }

    private void searchQuery(@Nullable String query, boolean fromET){
        if(query!=null&&!this.searchQuery.equals(query)){
            this.searchQuery = query;
            if(fromET){
                getFragment().onSearch(this.searchQuery);
            } else {
                handleETChange = false;
                Activity activity = getActivity();
                if(activity!=null) {
                    IMEUtil.hideKeyboard(activity);
                }
                searchEditText.setText(searchQuery);
                getFragment().onSearch(searchQuery);
                handleETChange = true;
            }
        }
    }

    @Override
    @CallSuper
    public void readSavedInstance(Bundle savedInstanceState) {
        String query = savedInstanceState.getString("searchQuery");
        if(query!=null){
            searchQuery = query;
        }
    }

    @Override
    @CallSuper
    public void readBundleArguments(Bundle arguments) {
        String query = arguments.getString("searchQuery");
        if(query!=null){
            searchFromArguments = true;
            searchQuery = query;
            arguments.remove("searchQuery");
        }
    }

    @Override
    @CallSuper
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        savedInstanceState.remove("searchQuery");
    }

    @Override
    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("searchQuery", searchQuery);
    }

}
