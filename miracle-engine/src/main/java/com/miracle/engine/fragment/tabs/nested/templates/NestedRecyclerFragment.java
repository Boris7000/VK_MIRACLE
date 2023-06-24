package com.miracle.engine.fragment.tabs.nested.templates;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.R;
import com.miracle.engine.fragment.ScrollAndElevate;
import com.miracle.engine.fragment.base.IBaseFragment;
import com.miracle.engine.fragment.recycler.templates.RecyclerFragment;
import com.miracle.engine.fragment.tabs.ITabsFragment;
import com.miracle.engine.fragment.tabs.nested.INestedFragment;
import com.miracle.engine.fragment.tabs.nested.NestedFragmentController;

public abstract class NestedRecyclerFragment extends RecyclerFragment implements INestedFragment {

    private final NestedFragmentController<NestedRecyclerFragment> nestedController =
            new NestedFragmentController<NestedRecyclerFragment>(this){};

    @Override
    public NestedFragmentController<?> getNestedFragmentController() {
        return nestedController;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_recycleview_nested, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getNestedFragmentController().onAttach(context);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        ITabsFragment iTabsFragment = getNestedFragmentController().getTabsFragment();
        if (iTabsFragment instanceof IBaseFragment) {
            IBaseFragment iBaseFragment = (IBaseFragment) iTabsFragment;
            if (iBaseFragment.scrollAndElevateEnabled()) {
                ScrollAndElevate.scrollAndElevate(getRecyclerView(), iBaseFragment.getAppBarLayout());
            }
        }
    }
}
