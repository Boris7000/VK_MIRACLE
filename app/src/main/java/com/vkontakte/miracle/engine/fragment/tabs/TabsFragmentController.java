package com.vkontakte.miracle.engine.fragment.tabs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.fragment.IMiracleFragment;
import com.vkontakte.miracle.engine.fragment.MiracleFragmentController;
import com.vkontakte.miracle.engine.fragment.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.engine.fragment.NestedTabsAdapter;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.view.MiracleTabLayout;
import com.vkontakte.miracle.model.groups.GroupItem;

import java.util.ArrayList;

public abstract class TabsFragmentController extends MiracleFragmentController {

    private final ITabsFragment tabsFragment;
    private ViewPager2 viewPager;
    private MiracleTabLayout tabLayout;
    private ProgressBar progressBar;

    protected TabsFragmentController(IMiracleFragment miracleFragment) {
        super(miracleFragment);
        tabsFragment = (ITabsFragment) miracleFragment;
    }

    @Override
    public void onCreateView(@NonNull View rootView, Bundle savedInstanceState){
        if(!hasSavedAdapter(savedInstanceState)){
            loadTabs();
        }
    }

    @Override
    public void findViews(@NonNull View rootView){
        viewPager = rootView.findViewById(R.id.viewPager);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        progressBar = rootView.findViewById(R.id.progressCircle);
    }

    @Override
    public void initViews(){
        if (viewPager!=null){
            View child = viewPager.getChildAt(0);
            if (child instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) child;
                recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            }
        }
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public MiracleTabLayout getTabLayout() {
        return tabLayout;
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    public Fragment getFragmentAt(int position){
        return tabsFragment.getChildFragmentManager().findFragmentByTag("f"+position);
    }

    public final void loadTabs(){
        if(tabsFragment.asyncLoadTabs()){
            new AsyncExecutor<ArrayList<NestedMiracleFragmentFabric>>(){
                @Override
                public ArrayList<NestedMiracleFragmentFabric> inBackground() {
                    return tabsFragment.loadTabs();
                }

                @Override
                public void onExecute(ArrayList<NestedMiracleFragmentFabric> fabrics) {
                    if(fabrics==null||fabrics.isEmpty()){
                        fabrics = tabsFragment.getErrorTabs();
                    }
                    if(fabrics!=null&&!fabrics.isEmpty()){
                        setViewpagerAdapter(createAdapter(fabrics));
                        getViewPager().setCurrentItem(tabsFragment.defaultTab(), false);
                    }
                }
            }.start();
        } else {
            ArrayList<NestedMiracleFragmentFabric> fabrics = tabsFragment.loadTabs();
            if(fabrics==null||fabrics.isEmpty()){
                fabrics = tabsFragment.getErrorTabs();
            }
            if(fabrics!=null&&!fabrics.isEmpty()){
                setViewpagerAdapter(createAdapter(fabrics));
                getViewPager().setCurrentItem(tabsFragment.defaultTab(), false);
            }
        }
    }

    @CallSuper
    public void setViewpagerAdapter(NestedTabsAdapter tabsAdapter){
        show();

        viewPager.setAdapter(tabsAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setCustomView(R.layout.view_custom_tab_item);

                    NestedMiracleFragmentFabric fabric = tabsAdapter.getFabrics().get(position);

                    if(fabric.getTitle()!=null) {
                        tab.setText(fabric.getTitle());
                    }
                    if(fabric.getIcon()>0) {
                        tab.setIcon(fabric.getIcon());
                    }
                    tabLayout.updateTab(tab);
                }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabsFragment.onPageSelected(position);
            }
        });
    }

    private NestedTabsAdapter createAdapter(ArrayList<NestedMiracleFragmentFabric> fabrics){
        return new NestedTabsAdapter(
                tabsFragment.getChildFragmentManager(),
                tabsFragment.getLifecycle(),
                fabrics);
    }

    public final boolean hasSavedAdapter(Bundle savedInstanceState){
        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()) {
            String key = savedInstanceState.getString("Adapter");
            if (key != null) {
                NestedTabsAdapter tabsAdapter = (NestedTabsAdapter) LargeDataStorage.get().getLargeData(key);
                savedInstanceState.remove("Adapter");
                if (tabsAdapter != null) {
                    setViewpagerAdapter(createAdapter(tabsAdapter.getFabrics()));
                    return true;
                }
            }
        }
        return false;
    }

    public void show(boolean animate){
        if(animate){
            tabLayout.setAlpha(0);
            tabLayout.animate().alpha(1f).setDuration(200).start();
        }else {
            tabLayout.setAlpha(1f);
        }
        if (progressBar != null){
            progressBar.setVisibility(GONE);
        }
    }

    public void show() {
        show(true);
    }

    public void hide(boolean animate) {
        if(animate){
            tabLayout.setAlpha(1);
            tabLayout.animate().alpha(0f).setDuration(200).start();
        }else {
            tabLayout.setAlpha(0f);
        }
        if(progressBar != null){
            progressBar.setVisibility(VISIBLE);
        }
    }

    public void hide() {
        hide(true);
    }

    @CallSuper
    public boolean notTop(){
        if(viewPager!=null) {
            IMiracleFragment nestedMiracleFragment = (IMiracleFragment) getFragmentAt(viewPager.getCurrentItem());
            if (nestedMiracleFragment != null) {
                return nestedMiracleFragment.notTop();
            }
        }
        return false;
    }

    @CallSuper
    public void scrollToTop() {
        IMiracleFragment nestedMiracleFragment = (IMiracleFragment) getFragmentAt(viewPager.getCurrentItem());
        if(nestedMiracleFragment!=null) {
            nestedMiracleFragment.scrollToTop();
        }
    }

    @CallSuper
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState){
        String key = savedInstanceState.getString("Adapter");
        if(key!=null){
            LargeDataStorage.get().removeLargeData(key);
            savedInstanceState.remove("Adapter");
        }
    }

    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(viewPager!=null){
            Object object = viewPager.getAdapter();
            if(object!=null) {
                outState.putString("Adapter", LargeDataStorage.get().storeLargeData(object));
            }
        }
    }

}
