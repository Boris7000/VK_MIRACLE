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

public class FragmentDebugSettings extends BaseListFragment {

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        View interfaceSettings = rootView.findViewById(R.id.url_test);
        interfaceSettings.setOnClickListener(view -> NavigationUtil.goToUrlTest(getContext()));

        View test = rootView.findViewById(R.id.system_theme);
        test.setOnClickListener(view -> NavigationUtil.goToSystemThemeExtractor(getContext()));
    }

    @Override
    public int requestTitleTextResId() {
        return R.string.debug;
    }

    @Override
    public void inflateContent(@NonNull LayoutInflater inflater, @NonNull LinearLayout container) {
        inflater.inflate(R.layout.fragment_content_settings_debug, container, true);
    }


}
