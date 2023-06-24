package com.miracle.engine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public abstract class MiracleFragmentController<T extends Fragment>{

    private final T fragment;

    protected MiracleFragmentController(T fragment) {
        this.fragment = fragment;
    }

    @NonNull
    public T getFragment() {
        return fragment;
    }

    @Nullable
    public Context getContext(){
        return fragment.getContext();
    }

    @Nullable
    public FragmentActivity getActivity(){
        return fragment.getActivity();
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void onCreateView(@NonNull LayoutInflater inflate, @NonNull View rootView, @Nullable Bundle savedInstanceState){}

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){}

    public void onAttach(@NonNull Context context){}

    public void findViews(@NonNull View rootView){}

    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){}

    public void readSavedInstance(Bundle savedInstanceState){}

    public void readBundleArguments(Bundle arguments){}

    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {}

    public void onSaveInstanceState(@NonNull Bundle outState) {}

    public void onDestroy() {}

}
