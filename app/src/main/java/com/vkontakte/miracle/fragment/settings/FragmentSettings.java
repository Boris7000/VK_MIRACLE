package com.vkontakte.miracle.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.side.SideListFragment;
import com.vkontakte.miracle.engine.util.NavigationUtil;

public class FragmentSettings extends SideListFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        View interfaceSettings = rootView.findViewById(R.id.settings_interface);
        interfaceSettings.setOnClickListener(view -> NavigationUtil.goToInterfaceSettings(getContext()));

        View test = rootView.findViewById(R.id.debug);
        test.setOnClickListener(view -> NavigationUtil.goToDebug(getContext()));


        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

}
