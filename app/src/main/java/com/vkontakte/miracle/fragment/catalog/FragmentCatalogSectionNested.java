package com.vkontakte.miracle.fragment.catalog;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.fragment.tabs.NestedMiracleFragment;
import com.vkontakte.miracle.engine.fragment.tabs.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.engine.fragment.tabs.TabsMiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.model.catalog.CatalogSection;

public class FragmentCatalogSectionNested extends NestedMiracleFragment {

    private CatalogSection catalogSection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_with_recycleview_nested, container, false);
        setRecyclerView(rootView.findViewById(R.id.recyclerView));

        setTabsMiracleFragment((TabsMiracleFragment) getParentFragment());
        if(getTabsMiracleFragment()!=null&&getTabsMiracleFragment().scrollAndElevate()) {
            scrollAndElevate(getRecyclerView(), getTabsMiracleFragment().getAppBarLayout(), getMiracleActivity());
        }

        setProgressBar(rootView.findViewById(R.id.progressCircle));

        if(savedInstanceState!=null) {
            if (savedInstanceState.getString("catalogSection") != null) {
                CatalogSection catalogSection = (CatalogSection)
                        LargeDataStorage.get().getLargeData(savedInstanceState.getString("catalogSection"));
                savedInstanceState.remove(savedInstanceState.getString("catalogSection"));
                if (catalogSection != null) {
                    setCatalogSection(catalogSection);
                }
            }
        }

        if(nullSavedAdapter(savedInstanceState)){
            setAdapter(new CatalogSectionAdapter(catalogSection.getId()));
        }

        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), this::reloadAdapter);

        return rootView;
    }

    public void setCatalogSection(CatalogSection catalogSection){
        this.catalogSection = catalogSection;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(catalogSection!=null) {
            outState.putString("catalogSection", LargeDataStorage.get().storeLargeData(catalogSection));
        }
        super.onSaveInstanceState(outState);
    }

    public static class Fabric extends NestedMiracleFragmentFabric {

        private final CatalogSection catalogSection;

        public Fabric(CatalogSection catalogSection){
            super(catalogSection.getTitle(), -1);
            this.catalogSection = catalogSection;
        }

        @NonNull
        @Override
        public NestedMiracleFragment createFragment() {
            FragmentCatalogSectionNested fragmentCatalogSectionNested = new FragmentCatalogSectionNested();
            fragmentCatalogSectionNested.setCatalogSection(catalogSection);
            return fragmentCatalogSectionNested;
        }
    }
}
