package com.vkontakte.miracle.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.fragment.base.templates.BaseListFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.NavigationUtil;

public class FragmentInterfaceSettings extends BaseListFragment {

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        View colorSchemeSettings = rootView.findViewById(R.id.settings_color_scheme);
        colorSchemeSettings.setOnClickListener(view -> NavigationUtil.goToThemeSettings(getContext()));
    }

    @Override
    public int requestTitleTextResId() {
        return R.string.settings_interface;
    }

    @Override
    public void inflateContent(@NonNull LayoutInflater inflater, @NonNull LinearLayout container) {
        inflater.inflate(R.layout.fragment_content_settings_interface, container, true);
    }

}
