package com.vkontakte.miracle.engine.activity.tabs;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.activity.MiracleActivity;
import com.vkontakte.miracle.engine.activity.MiracleActivityController;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.util.IMEUtil;
import com.vkontakte.miracle.engine.view.fragmentContainer.TabsFragmentContainer;

public class TabsActivityController extends MiracleActivityController {

    private final ITabsActivity tabsActivity;
    private TabsFragmentContainer tabsFragmentContainer;
    private NavigationBarView navigationBarView;

    protected TabsActivityController(MiracleActivity miracleActivity) {
        super(miracleActivity);
        tabsActivity = (ITabsActivity) miracleActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        ArrayMap<Integer, FragmentFabric> fabrics = tabsActivity.loadTabs();
        if(fabrics==null||fabrics.isEmpty()){
            fabrics = tabsActivity.getErrorTabs();
        }
        if(fabrics!=null&&!fabrics.isEmpty()){
            TabsFragmentContainer.Adapter adapter = createAdapter(fabrics);
            setFragmentContainerAdapter(adapter);
            if(savedInstanceState!=null&&!savedInstanceState.isEmpty()) {
                adapter.restoreFromSavedState(savedInstanceState);
                int savedSelectedTab = savedInstanceState.getInt("selectedTab", R.id.tab_feed);
                int selectedTab = navigationBarView.getSelectedItemId();
                if(selectedTab!=savedSelectedTab) {
                    navigationBarView.setSelectedItemId(selectedTab);
                }
            } else {
                navigationBarView.setSelectedItemId(R.id.tab_feed);
            }
        }

        tabsFragmentContainer.setOnFragmentChangeListener(() -> IMEUtil.hideKeyboard(getMiracleActivity()));
    }

    @Override
    public void findViews(@NonNull View rootView){
        navigationBarView = rootView.findViewById(R.id.bottomNavigationView);
        tabsFragmentContainer = rootView.findViewById(R.id.frameContainer);
    }

    @Override
    public void initViews(){

    }

    public TabsFragmentContainer getTabsFragmentContainer() {
        return tabsFragmentContainer;
    }

    public NavigationBarView getNavigationBarView() {
        return navigationBarView;
    }


    private TabsFragmentContainer.Adapter createAdapter(ArrayMap<Integer, FragmentFabric> fabrics){
        return new TabsFragmentContainer.Adapter(getMiracleActivity().getSupportFragmentManager(), fabrics);
    }

    @CallSuper
    public void setFragmentContainerAdapter(TabsFragmentContainer.Adapter adapter){
        tabsFragmentContainer.setAdapter(adapter);
        adapter.setUpWithNavigationBarView(navigationBarView);
    }

    public final boolean addFragment(Fragment fragment){
        TabsFragmentContainer.Adapter adapter = tabsFragmentContainer.getAdapter();
        if(adapter !=null) {
            adapter.addFragment(fragment);
            return true;
        }
        return false;
    }

    public final boolean backPress(){
        TabsFragmentContainer.Adapter adapter = tabsFragmentContainer.getAdapter();
        if(adapter!=null){
            if(adapter.getFragmentCount()>1){
                adapter.back();
                IMEUtil.hideKeyboard(getMiracleActivity());
                return true;
            }
        }
        return false;
    }

    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(tabsFragmentContainer!=null){
            TabsFragmentContainer.Adapter adapter = tabsFragmentContainer.getAdapter();
            if(adapter!=null){
                adapter.saveState(outState);
                if(navigationBarView!=null) {
                    outState.putInt("selectedTab", navigationBarView.getSelectedItemId());
                }
            }
        }
    }
}
