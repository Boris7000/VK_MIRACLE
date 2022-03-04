package com.vkontakte.miracle.engine.fragment.tabs;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.ScrollAndElevate;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.view.MiracleTabLayout;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogSectionNested;
import com.vkontakte.miracle.model.catalog.CatalogSection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class TabsMiracleFragment extends MiracleFragment {

    private TabsAdapter tabsAdapter;
    private ViewPager2 viewPager;
    private MiracleTabLayout tabLayout;
    private LinearLayout topBar;
    private AppBarLayout appBarLayout;
    private boolean scrollAndElevate = true;

    public void setAdapter(TabsAdapter tabsAdapter){
        this.tabsAdapter = tabsAdapter;

        viewPager.setAdapter(tabsAdapter);

        setupViewPagerAndTabLayout();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                NestedMiracleFragment nestedMiracleFragment = getFragmentAt(position);

                if(nestedMiracleFragment!=null) {
                    RecyclerView recyclerView = nestedMiracleFragment.getRecyclerView();

                    if (recyclerView != null) {
                        if (recyclerView.canScrollVertically(-1)) {
                            ScrollAndElevate.appBarElevate(appBarLayout, getMiracleActivity());
                        } else {
                            ScrollAndElevate.appBarLand(appBarLayout);
                        }
                    }
                }
            }
        });
    }

    public void setupViewPagerAndTabLayout(){
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
    }

    public TabsAdapter getTabsAdapter() {
        return tabsAdapter;
    }

    //////////////////////////////////////////////////

    public void setViewPager(ViewPager2 viewPager) {
        this.viewPager = viewPager;
        View child = viewPager.getChildAt(0);
        if (child instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) child;
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    public void setTabsSwipeEnabled(boolean enabled){
        viewPager.setUserInputEnabled(enabled);
    }

    //////////////////////////////////////////////////

    public void setTabLayout(MiracleTabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    public MiracleTabLayout getTabLayout() {
        return tabLayout;
    }

    //////////////////////////////////////////////////

    public void setTopBar(LinearLayout topBar){
        setTopBar(topBar, v -> scrollToTop());
    }

    public void setTopBar(LinearLayout topBar, View.OnClickListener onClickListener){
        topBar.setOnClickListener(onClickListener);
        this.topBar = topBar;
    }

    public LinearLayout getTopBar(){
        return topBar;
    }

    //////////////////////////////////////////////////

    public void setAppBarLayout(AppBarLayout appBarLayout){
        this.appBarLayout = appBarLayout;
    }

    public AppBarLayout getAppBarLayout(){
        return appBarLayout;
    }

    public void disableScrollAndElevate(){
        scrollAndElevate = false;
    }

    public boolean scrollAndElevate() {
        return scrollAndElevate;
    }

    //////////////////////////////////////////////////

    public void setBackClick(View view){
        view.setOnClickListener(v -> getMiracleActivity().fragmentBack());
    }

    @Override
    public boolean notTop(){

        NestedMiracleFragment nestedMiracleFragment = getFragmentAt(viewPager.getCurrentItem());

        if(nestedMiracleFragment!=null) {
            return nestedMiracleFragment.notTop();
        } else {
            return false;
        }
    }

    @Override
    public void scrollToTop() {

        NestedMiracleFragment nestedMiracleFragment = getFragmentAt(viewPager.getCurrentItem());

        if(nestedMiracleFragment!=null) {
            nestedMiracleFragment.scrollToTop();
        }

        if (appBarLayout != null) {
            appBarLayout.setExpanded(true);
            if(scrollAndElevate) {
                ScrollAndElevate.appBarLand(appBarLayout);
            }
        }
    }

    public NestedMiracleFragment getFragmentAt(int position){
        return (NestedMiracleFragment) getChildFragmentManager().findFragmentByTag("f"+position);
    }

    public boolean nullSavedAdapter(Bundle savedInstanceState){
        if(savedInstanceState!=null) {
            if (savedInstanceState.getString("adapter") != null) {
                TabsAdapter tabsAdapter = (TabsAdapter)
                        getMiracleApp().getLargeDataStorage().getLargeData(savedInstanceState.getString("adapter"));
                savedInstanceState.remove(savedInstanceState.getString("adapter"));

                setAdapter(new TabsAdapter(this, tabsAdapter.getFabrics()));
               return false;
            }
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if(viewPager!=null){
            Object object = viewPager.getAdapter();
            if(object!=null) {
                if (object instanceof TabsAdapter) {
                    TabsAdapter adapter = (TabsAdapter) object;
                    LargeDataStorage largeDataStorage = getMiracleApp().getLargeDataStorage();
                    outState.putString("adapter", largeDataStorage.storeLargeData(adapter, largeDataStorage.createUniqueKey()));
                }
            }
        }
        super.onSaveInstanceState(outState);
    }

    public void loadCatalogSections(Call<JSONObject> call){

        new AsyncExecutor<Boolean>() {

            private final ArrayList<NestedMiracleFragmentFabric> fabrics = new ArrayList<>();

            @Override
            public Boolean inBackground() {
                try {
                    Response<JSONObject> response = call.execute();

                    JSONObject jsonObject = validateBody(response).getJSONObject("response");

                    JSONArray sections = jsonObject.getJSONObject("catalog").getJSONArray("sections");

                    for (int i=0; i<sections.length();i++){
                        CatalogSection catalogSection = new CatalogSection(sections.getJSONObject(i));
                        fabrics.add(new FragmentCatalogSectionNested.Fabric(catalogSection));
                    }

                    return true;

                }catch (Exception e){
                    return false;
                }
            }

            @Override
            public void onExecute(Boolean object) {
                if(object){
                    setAdapter(new TabsAdapter(TabsMiracleFragment.this, fabrics));
                }
            }
        }.start();
    }
}
