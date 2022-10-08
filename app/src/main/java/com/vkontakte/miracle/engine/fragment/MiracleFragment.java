package com.vkontakte.miracle.engine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class MiracleFragment extends Fragment implements IMiracleFragment {

    private Bundle notUsedSavedInstanceState;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            readSavedInstance(savedInstanceState);
        }

        Bundle arguments = getArguments();
        if(arguments!=null&&!arguments.isEmpty()){
            readBundleArguments(arguments);
        }

        View rootView = inflateRootView(inflater, container);

        findViews(rootView);
        initViews();

        return rootView;
    }

    @NonNull
    public abstract View inflateRootView(LayoutInflater inflater, ViewGroup container);

    public void findViews(@NonNull View rootView) {}

    public void initViews() {}

    public void readSavedInstance(Bundle savedInstanceState){}

    public void readBundleArguments(Bundle arguments){}

    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {}

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        if(notUsedSavedInstanceState !=null&&!notUsedSavedInstanceState.isEmpty()) {
            onClearSavedInstance(notUsedSavedInstanceState);
        }
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        notUsedSavedInstanceState = outState;
        super.onSaveInstanceState(outState);
    }

    @Override
    public void scrollToTop(){}

    @Override
    public boolean notTop(){
        return false;
    }

}
