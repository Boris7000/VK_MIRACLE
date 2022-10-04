package com.vkontakte.miracle.engine.fragment.nested;

import androidx.fragment.app.Fragment;


public interface INestedFragment {

    NestedFragmentController requestNestedFragmentController();

    NestedFragmentController getNestedFragmentController();

    Fragment getParentFragment();

}
