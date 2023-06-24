package com.miracle.engine.fragment.tabs.nested;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.miracle.engine.fragment.MiracleFragmentController;
import com.miracle.engine.fragment.tabs.ITabsFragment;

public abstract class NestedFragmentController<T extends Fragment & INestedFragment> extends MiracleFragmentController<T> {

    private ITabsFragment tabsFragment;

    protected NestedFragmentController(T fragment) {
        super(fragment);
    }

    public final ITabsFragment getTabsFragment() {
        return tabsFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Fragment fragment = getFragment().getParentFragment();
        if(fragment!=null){
            if(fragment instanceof ITabsFragment){
                tabsFragment = (ITabsFragment) fragment;
            }
        }
    }
}
