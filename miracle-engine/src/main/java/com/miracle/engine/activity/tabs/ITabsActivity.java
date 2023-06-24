package com.miracle.engine.activity.tabs;

import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;

import com.miracle.engine.fragment.FragmentFabric;

public interface ITabsActivity {

    TabsActivityController requestTabsActivityController();

    TabsActivityController getTabsActivityController();

    ArrayMap<Integer, FragmentFabric> loadTabs();

    ArrayMap<Integer, FragmentFabric> getErrorTabs();

    int defaultTab();

    boolean addFragment(Fragment fragment);
}
