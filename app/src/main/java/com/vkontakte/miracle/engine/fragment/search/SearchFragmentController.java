package com.vkontakte.miracle.engine.fragment.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.IMiracleFragment;
import com.vkontakte.miracle.engine.fragment.MiracleFragmentController;
import com.vkontakte.miracle.engine.util.IMEUtil;
import com.vkontakte.miracle.executors.util.SimpleTimer;

public abstract class SearchFragmentController extends MiracleFragmentController {

    private final ISearchFragment searchFragment;
    private SimpleTimer timer;
    private String query = "";
    private String initialQuery = "";
    private String contextQuery = "";
    private boolean handleInput = true;
    private EditText searchEditText;

    protected SearchFragmentController(IMiracleFragment miracleFragment) {
        super(miracleFragment);
        searchFragment = (ISearchFragment) miracleFragment;
    }

    @Override
    public void onCreateView(@NonNull View rootView, Bundle savedInstanceState) {
        query = "";
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(initialQuery.isEmpty()){
            showKeyboard();
        } else {
            handleInput = false;
            searchEditText.setText(initialQuery);
            initialQuery = "";
            handleInput = true;
        }
    }

    @Override
    public void findViews(@NonNull View rootView) {
        searchEditText = rootView.findViewById(R.id.searchEditText);
    }

    @Override
    public void initViews() {
        if(searchEditText!=null) {
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (handleInput) {
                        query = searchEditText.getText().toString().trim();
                        if (!query.isEmpty()) {
                            if (timer != null) {
                                timer.reset();
                            } else {
                                timer = new SimpleTimer(500, 250) {
                                    @Override
                                    public void onExecute(Boolean result) {
                                        timer = null;
                                        if (result) {
                                            searchFragment.onSearch(query);
                                        }
                                        query = "";
                                    }
                                };
                                timer.start();
                            }
                        }
                    }
                }
            };
            searchEditText.addTextChangedListener(textWatcher);
        }
    }

    public final EditText getSearchEditText() {
        return searchEditText;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        IMEUtil.hideKeyboard(getMiracleFragment().getMiracleActivity());
        handleInput = false;
        this.query = query;
        this.contextQuery = "";
        killTimer();
        searchEditText.setText(query);
        searchFragment.onSearch(query);
        this.query = "";
        handleInput = true;
    }

    public String getContextQuery() {
        return contextQuery;
    }

    public void setContextQuery(String contextQuery, String query) {
        IMEUtil.hideKeyboard(getMiracleFragment().getMiracleActivity());
        handleInput = false;
        this.query = query;
        this.contextQuery = contextQuery;
        killTimer();
        searchEditText.setText(query);
        searchFragment.onContextSearch(contextQuery);
        this.contextQuery = "";
        this.query = "";
        handleInput = true;
    }

    private void killTimer(){
        if(timer!=null&&!timer.workIsDone()){
            timer.interrupt();
            timer = null;
        }
    }

    @CallSuper
    public void showKeyboard(){
        searchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getMiracleFragment()
                .getMiracleActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEditText, 0);
    }

    public void readBundleArguments(Bundle arguments) {
        String initialQ = arguments.getString("initialQ");
        if(initialQ!=null){
            initialQuery = initialQ;
            query = initialQuery;
        }
    }
}
