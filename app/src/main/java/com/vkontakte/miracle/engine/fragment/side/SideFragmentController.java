package com.vkontakte.miracle.engine.fragment.side;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.vkontakte.miracle.engine.fragment.IMiracleFragment;
import com.vkontakte.miracle.engine.fragment.MiracleFragmentController;
import com.vkontakte.miracle.engine.fragment.base.IBaseFragment;

public abstract class SideFragmentController extends MiracleFragmentController {

    private final IBaseFragment baseFragment;
    private final ISideFragment sideFragment;

    protected SideFragmentController(IMiracleFragment miracleFragment) {
        super(miracleFragment);
        baseFragment = (IBaseFragment) miracleFragment;
        sideFragment = (ISideFragment) miracleFragment;
    }

    @Override
    public void onCreateView(@NonNull View rootView, Bundle savedInstanceState){

    }

    @Override
    public void findViews(@NonNull View rootView){

    }

    @Override
    public void initViews(){
        Toolbar toolbar = baseFragment.getToolBar();
        if(toolbar!=null) {
            toolbar.setNavigationOnClickListener(v -> getMiracleFragment().getMiracleActivity().onBackPressed());
        }
    }

}
