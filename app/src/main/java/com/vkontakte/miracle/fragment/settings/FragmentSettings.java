package com.vkontakte.miracle.fragment.settings;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.fragment.base.FragmentTest;

public class FragmentSettings extends SimpleMiracleFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MiracleActivity miracleActivity = getMiracleActivity();

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        setAppBarLayout(rootView.findViewById(R.id.appbarlayout));
        setToolBar(getAppBarLayout().findViewById(R.id.toolbar));
        setAppbarClickToTop();
        setBackClick();
        setScrollView(rootView.findViewById(R.id.scrollView));
        scrollAndElevate(getScrollView(),getAppBarLayout(),miracleActivity);

        View interfaceSettings = rootView.findViewById(R.id.settings_interface);
        interfaceSettings.setOnClickListener(view -> miracleActivity.addFragment(new FragmentInterfaceSettings()));

        View colorSchemeGenerator = rootView.findViewById(R.id.color_generator);
        colorSchemeGenerator.setOnClickListener(view -> miracleActivity.addFragment(new FragmentColorSchemeGenerator()));

        View test = rootView.findViewById(R.id.test);
        test.setOnClickListener(view -> miracleActivity.addFragment(new FragmentTest()));

        return rootView;
    }

}
