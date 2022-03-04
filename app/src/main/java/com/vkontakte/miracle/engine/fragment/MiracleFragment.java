package com.vkontakte.miracle.engine.fragment;

import androidx.fragment.app.Fragment;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;

public abstract class MiracleFragment extends Fragment {

    private MiracleActivity miracleActivity;

    public void iniContext(){
        miracleActivity = (MiracleActivity) getActivity();
    }

    public MiracleActivity getMiracleActivity(){
        return miracleActivity;
    }

    public MiracleApp getMiracleApp(){
        return miracleActivity.getMiracleApp();
    }

    public abstract void scrollToTop();

    public boolean notTop(){
        return false;
    }

}
