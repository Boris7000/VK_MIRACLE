package com.vkontakte.miracle.engine.fragment.tabs;

import com.google.android.material.appbar.AppBarLayout;
import com.vkontakte.miracle.engine.fragment.ListMiracleFragment;
import com.vkontakte.miracle.engine.fragment.ScrollAndElevate;

public class NestedMiracleFragment extends ListMiracleFragment {

    private TabsMiracleFragment tabsMiracleFragment;

    public TabsMiracleFragment getTabsMiracleFragment() {
        return tabsMiracleFragment;
    }

    public void setTabsMiracleFragment(TabsMiracleFragment tabsMiracleFragment) {
        this.tabsMiracleFragment = tabsMiracleFragment;
    }

    @Override
    public void hide(boolean animate) {
        super.hide(animate);

        if(tabsMiracleFragment!=null){
            AppBarLayout appBarLayout = tabsMiracleFragment.getAppBarLayout();
            if (appBarLayout != null) {
                appBarLayout.setExpanded(true);
                if(tabsMiracleFragment.scrollAndElevate()) {
                    ScrollAndElevate.appBarLand(appBarLayout);
                }
            }
        }
    }
}
