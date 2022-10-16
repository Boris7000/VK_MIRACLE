package com.vkontakte.miracle.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.side.SideListFragment;
import com.vkontakte.miracle.engine.util.NavigationUtil;

public class FragmentInterfaceSettings extends SideListFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        View colorSchemeSettings = rootView.findViewById(R.id.settings_color_scheme);
        colorSchemeSettings.setOnClickListener(view -> NavigationUtil.goToThemeSettings(getContext()));

        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings_interface, container, false);
    }
}
