package com.vkontakte.miracle.engine.fragment;

import com.vkontakte.miracle.MainActivity;

public interface IMiracleFragment {

    void scrollToTop();

    boolean notTop();

    MainActivity getMiracleActivity();
}
