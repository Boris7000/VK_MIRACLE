package com.vkontakte.miracle.fragment.catalog;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;

public class FragmentCatalogSection extends SimpleMiracleFragment {
    private String catalogSectionId;
    private String catalogSectionTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MiracleActivity miracleActivity = getMiracleActivity();

        View rootView = inflater.inflate(R.layout.fragment_with_recycleview, container, false);

        setAppBarLayout(rootView.findViewById(R.id.appbarlayout));
        setToolBar(getAppBarLayout().findViewById(R.id.toolbar));
        setAppbarClickToTop();
        setBackClick();

        setTitle(rootView.findViewById(R.id.title));
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        scrollAndElevate(getRecyclerView(),getAppBarLayout(), miracleActivity);
        setProgressBar(rootView.findViewById(R.id.progressCircle));

        if(savedInstanceState!=null) {
            if (savedInstanceState.getString("catalogSectionId") != null) {
                catalogSectionId = savedInstanceState.getString("catalogSectionId");
                savedInstanceState.remove(savedInstanceState.getString("catalogSectionId"));
            }
            if (savedInstanceState.getString("catalogSectionTitle") != null) {
                catalogSectionTitle = savedInstanceState.getString("catalogSectionTitle");
                savedInstanceState.remove(savedInstanceState.getString("catalogSectionTitle"));
                setTitleText(catalogSectionTitle);
            }
        }

        if(nullSavedAdapter(savedInstanceState)){
            CatalogSectionAdapter catalogSectionAdapter = new CatalogSectionAdapter(catalogSectionId);
            catalogSectionAdapter.setOnSectionLoadedListener(catalogSection -> {
                        catalogSectionTitle = catalogSection.getTitle();
                        setTitleText(catalogSectionTitle);
            });
            setAdapter(catalogSectionAdapter);
        }

        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), this::reloadAdapter);

        return rootView;
    }

    public void setCatalogSectionId(String catalogSectionId){
        this.catalogSectionId = catalogSectionId;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(catalogSectionId!=null) {
            outState.putString("catalogSectionId", catalogSectionId);
        }
        if(catalogSectionTitle!=null) {
            outState.putString("catalogSectionTitle", catalogSectionTitle);
        }
        super.onSaveInstanceState(outState);
    }

}