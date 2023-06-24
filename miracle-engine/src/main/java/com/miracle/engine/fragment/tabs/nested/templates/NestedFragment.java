package com.miracle.engine.fragment.tabs.nested.templates;

import android.content.Context;

import androidx.annotation.NonNull;

import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.fragment.tabs.nested.INestedFragment;
import com.miracle.engine.fragment.tabs.nested.NestedFragmentController;

public abstract class NestedFragment extends MiracleFragment implements INestedFragment {

    private final NestedFragmentController<NestedFragment> nestedController =
            new NestedFragmentController<NestedFragment>(this){};

    @Override
    public NestedFragmentController<?> getNestedFragmentController() {
        return nestedController;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getNestedFragmentController().onAttach(context);
    }
}
