package com.miracle.engine.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

public abstract class MiracleActivityController {

    private final MiracleActivity miracleActivity;

    protected MiracleActivityController(MiracleActivity miracleActivity) {
        this.miracleActivity = miracleActivity;
    }

    public abstract void onCreate(Bundle savedInstanceState);

    public abstract void findViews(@NonNull View rootView);

    public abstract void initViews();

    public MiracleActivity getMiracleActivity() {
        return miracleActivity;
    }
}
