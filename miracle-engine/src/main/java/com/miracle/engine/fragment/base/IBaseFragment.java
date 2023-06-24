package com.miracle.engine.fragment.base;

import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.miracle.engine.fragment.IScrollableFragment;

public interface IBaseFragment extends IScrollableFragment {

    BaseFragmentController<?> getBaseFragmentController();

    default AppBarLayout getAppBarLayout(){return getBaseFragmentController().getAppBarLayout();}

    default Toolbar getToolBar(){return getBaseFragmentController().getToolBar();}

    default TextView getTitle(){return getBaseFragmentController().getTitle();}

    default boolean scrollAndElevateEnabled(){return true;}

    default boolean needChangeTitleText(){return true;}

    default String requestTitleText(){return "";}

    @StringRes
    default int requestTitleTextResId(){return 0;}

    default boolean canBackClick(){return getBaseFragmentController().canBackClick();}

    default void setCanBackClick(boolean positionInStack){getBaseFragmentController().setCanBackClick(positionInStack);}
}
