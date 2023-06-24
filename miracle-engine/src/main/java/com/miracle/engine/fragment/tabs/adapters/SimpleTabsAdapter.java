package com.miracle.engine.fragment.tabs.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.miracle.engine.fragment.FragmentFabric;

import java.util.ArrayList;

public class SimpleTabsAdapter extends TabsAdapter<FragmentFabric> {
    public SimpleTabsAdapter(@NonNull Fragment fragment, ArrayList<FragmentFabric> fabrics) {
        super(fragment, fabrics);
    }

    public SimpleTabsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<FragmentFabric> fabrics) {
        super(fragmentManager, lifecycle, fabrics);
    }
}
