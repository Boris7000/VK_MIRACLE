package com.miracle.engine.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public interface FragmentFabric {
    @NonNull
    Fragment createFragment();
}
