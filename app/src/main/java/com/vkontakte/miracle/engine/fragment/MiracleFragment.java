package com.vkontakte.miracle.engine.fragment;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vkontakte.miracle.MiracleActivity;

public abstract class MiracleFragment extends Fragment {

    private MiracleActivity miracleActivity;

    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        miracleActivity = (MiracleActivity) getActivity();
    }

    public MiracleActivity getMiracleActivity(){
        return miracleActivity;
    }

    public abstract void scrollToTop();

    public boolean notTop(){
        return false;
    }

}
