package com.vkontakte.miracle.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.side.SideListFragment;
import com.vkontakte.miracle.fragment.base.FragmentTest;

public class FragmentSettings extends SideListFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        MainActivity mainActivity = getMiracleActivity();

        View interfaceSettings = rootView.findViewById(R.id.settings_interface);
        interfaceSettings.setOnClickListener(view -> mainActivity.addFragment(new FragmentInterfaceSettings()));

        View colorSchemeGenerator = rootView.findViewById(R.id.color_generator);
        colorSchemeGenerator.setOnClickListener(view -> mainActivity.addFragment(new FragmentColorSchemeGenerator()));

        View test = rootView.findViewById(R.id.test);
        test.setOnClickListener(view -> mainActivity.addFragment(new FragmentTest()));

        View urlTest = rootView.findViewById(R.id.url_test);
        urlTest.setOnClickListener(view -> mainActivity.addFragment(new FragmentUrlOpenTest()));

        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

}
