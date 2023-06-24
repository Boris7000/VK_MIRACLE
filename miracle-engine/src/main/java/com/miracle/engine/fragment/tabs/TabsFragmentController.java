package com.miracle.engine.fragment.tabs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.miracle.engine.R;
import com.miracle.engine.async.AsyncExecutor;
import com.miracle.engine.fragment.IScrollableFragment;
import com.miracle.engine.fragment.MiracleFragmentController;
import com.miracle.engine.fragment.tabs.nested.NestedMiracleFragmentFabric;
import com.miracle.engine.fragment.NestedTabsAdapter;
import com.miracle.engine.util.LargeDataStorage;
import com.miracle.engine.view.MiracleTabLayout;

import java.util.ArrayList;

public abstract class TabsFragmentController<T extends Fragment & ITabsFragment> extends MiracleFragmentController<T> {

    private ViewPager2 viewPager;
    private MiracleTabLayout tabLayout;
    private ProgressBar progressBar;

    protected TabsFragmentController(T fragment) {
        super(fragment);
    }

    @Override
    public void findViews(@NonNull View rootView){
        viewPager = rootView.findViewById(R.id.viewPager);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        progressBar = rootView.findViewById(R.id.progressCircle);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        if (viewPager!=null){
            View child = viewPager.getChildAt(0);
            if (child instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) child;
                recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            }
            if(!hasSavedAdapter(savedInstanceState)){
                loadTabs();
            }
        }
    }

    public Fragment getFragmentAt(int position){
        return getFragment().getChildFragmentManager().findFragmentByTag("f"+position);
    }

    public final void loadTabs(){
        T fragment = getFragment();
        if(fragment.asyncLoadTabs()){
            new AsyncExecutor<ArrayList<NestedMiracleFragmentFabric>>(){
                @Override
                public ArrayList<NestedMiracleFragmentFabric> inBackground() {
                    return fragment.loadTabs();
                }

                @Override
                public void onExecute(ArrayList<NestedMiracleFragmentFabric> fabrics) {
                    setTabFabrics(fabrics);
                }
            }.start();
        } else {
            setTabFabrics(fragment.loadTabs());
        }
    }

    private void setTabFabrics(ArrayList<NestedMiracleFragmentFabric> fabrics){
        T fragment = getFragment();
        if(fragment.isAdded()) {
            if(fabrics==null||fabrics.isEmpty()){
                fabrics = fragment.getErrorTabs();
            }
            if(fabrics!=null&&!fabrics.isEmpty()) {
                setViewpagerAdapter(createAdapter(fabrics));
                getViewPager().setCurrentItem(fragment.defaultTab(), false);
            }
        }
    }

    @CallSuper
    public void setViewpagerAdapter(NestedTabsAdapter tabsAdapter){
        show();

        viewPager.setAdapter(tabsAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setCustomView(getFragment().customTabItemViewResourceId());

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
                getFragment().onPageSelected(position);
            }
        });
    }

    private NestedTabsAdapter createAdapter(ArrayList<NestedMiracleFragmentFabric> fabrics){
        T fragment = getFragment();
        return new NestedTabsAdapter(
                fragment.getChildFragmentManager(),
                fragment.getLifecycle(),
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
            Fragment nestedFragment = getFragmentAt(viewPager.getCurrentItem());
            if (nestedFragment instanceof IScrollableFragment) {
                return ((IScrollableFragment)nestedFragment).notTop();
            }
        }
        return false;
    }

    @CallSuper
    public void scrollToTop() {
        Fragment nestedFragment = getFragmentAt(viewPager.getCurrentItem());
        if (nestedFragment instanceof IScrollableFragment) {
            ((IScrollableFragment)nestedFragment).scrollToTop();
        }
    }

    @Override
    @CallSuper
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState){
        String key = savedInstanceState.getString("Adapter");
        if(key!=null){
            LargeDataStorage.get().removeLargeData(key);
            savedInstanceState.remove("Adapter");
        }
    }

    @Override
    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(viewPager!=null){
            Object object = viewPager.getAdapter();
            if(object!=null) {
                outState.putString("Adapter", LargeDataStorage.get().storeLargeData(object));
            }
        }
    }


    /////////////////////////////////////////////////////////////////

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public MiracleTabLayout getTabLayout() {
        return tabLayout;
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

}
