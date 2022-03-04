package com.vkontakte.miracle.engine.fragment;

import androidx.annotation.NonNull;

public interface FragmentFabric {
    @NonNull
    MiracleFragment createFragment();
}
