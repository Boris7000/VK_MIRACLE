package com.vkontakte.miracle.engine.fragment.nested;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.vkontakte.miracle.engine.fragment.MiracleFragment;

public abstract class NestedFragment extends MiracleFragment implements INestedFragment {

    private final NestedFragmentController nestedFragmentController = requestNestedFragmentController();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        getNestedFragmentController().onCreateView(rootView, savedInstanceState);

        return rootView;
    }

    @CallSuper
    @Override
    public void findViews(@NonNull View rootView) {
        getNestedFragmentController().findViews(rootView);
    }

    @CallSuper
    @Override
    public void initViews() {
        getNestedFragmentController().initViews();
    }

    @Override
    public NestedFragmentController requestNestedFragmentController() {
        return new NestedFragmentController(this) {};
    }

    @Override
    public NestedFragmentController getNestedFragmentController() {
        return nestedFragmentController;
    }
}
