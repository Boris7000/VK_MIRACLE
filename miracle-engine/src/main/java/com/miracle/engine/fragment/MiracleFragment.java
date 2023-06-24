package com.miracle.engine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class MiracleFragment extends Fragment {

    private Bundle notUsedSavedInstanceState;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            readSavedInstance(savedInstanceState);
        }

        Bundle arguments = getArguments();
        if(arguments!=null&&!arguments.isEmpty()){
            readBundleArguments(arguments);
        }



        return inflateRootView(inflater, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        initViews(view, savedInstanceState);
    }

    @NonNull
    public abstract View inflateRootView(LayoutInflater inflater, ViewGroup container);

    public void findViews(@NonNull View rootView) {}

    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {}

    /////////////////////////////////////////////////////////////////////////////

    public void readSavedInstance(Bundle savedInstanceState){}

    public void readBundleArguments(Bundle arguments){}

    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {}

    /////////////////////////////////////////////////////////////////////////////

    @CallSuper
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        notUsedSavedInstanceState = outState;
        super.onSaveInstanceState(outState);
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        if(notUsedSavedInstanceState !=null&&!notUsedSavedInstanceState.isEmpty()) {
            onClearSavedInstance(notUsedSavedInstanceState);
        }
    }

}
