package com.miracle.engine.fragment.tabs.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.miracle.engine.fragment.tabs.nested.NestedMiracleFragmentFabric;

import java.util.ArrayList;

public class NestedTabsAdapter extends TabsAdapter<NestedMiracleFragmentFabric>{
    public NestedTabsAdapter(@NonNull Fragment fragment, ArrayList<NestedMiracleFragmentFabric> fabrics) {
        super(fragment, fabrics);
    }

    public NestedTabsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<NestedMiracleFragmentFabric> fabrics) {
        super(fragmentManager, lifecycle, fabrics);
    }
}
