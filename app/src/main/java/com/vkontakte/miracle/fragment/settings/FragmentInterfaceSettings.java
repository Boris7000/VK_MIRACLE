package com.vkontakte.miracle.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

public class FragmentInterfaceSettings extends SimpleMiracleFragment {
    private View rootView;
    private MiracleActivity miracleActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        iniContext();

        miracleActivity = getMiracleActivity();

        rootView = inflater.inflate(R.layout.fragment_settings_interface, container, false);

        setTopBar(rootView.findViewById(R.id.appbarLinear));
        setAppBarLayout(rootView.findViewById(R.id.appbar));
        setBackClick(rootView.findViewById(R.id.backButton));
        setScrollView(rootView.findViewById(R.id.scrollView));
        scrollAndElevate(getScrollView(),getAppBarLayout(),miracleActivity);

        View colorSchemeSettings = rootView.findViewById(R.id.settings_color_scheme);
        colorSchemeSettings.setOnClickListener(view -> miracleActivity.addFragment(new FragmentColorSchemeSettings()));

        return rootView;
    }

}
