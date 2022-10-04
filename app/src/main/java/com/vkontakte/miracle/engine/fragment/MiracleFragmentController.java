package com.vkontakte.miracle.engine.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

public abstract class MiracleFragmentController {

    private final IMiracleFragment miracleFragment;

    public IMiracleFragment getMiracleFragment() {
        return miracleFragment;
    }

    protected MiracleFragmentController(IMiracleFragment miracleFragment) {
        this.miracleFragment = miracleFragment;
    }

    public abstract void onCreateView(@NonNull View rootView, Bundle savedInstanceState);

    public abstract void findViews(@NonNull View rootView);

    public abstract void initViews();

}
