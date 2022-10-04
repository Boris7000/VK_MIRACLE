package com.vkontakte.miracle.engine.fragment.base;

import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

public interface IBaseFragment {

    BaseFragmentController requestBaseFragmentController();

    BaseFragmentController getBaseFragmentController();

    AppBarLayout getAppBarLayout();

    Toolbar getToolBar();

    TextView getTitle();

    boolean scrollAndElevateEnabled();

    boolean needChangeTitleText();

    String requestTitleText();

}
