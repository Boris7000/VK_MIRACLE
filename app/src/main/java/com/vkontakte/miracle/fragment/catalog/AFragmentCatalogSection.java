package com.vkontakte.miracle.fragment.catalog;

import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_FIRST_LOADING_COMPLETE;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.fragment.side.SideRecyclerFragment;
import com.vkontakte.miracle.model.catalog.CatalogSection;

public abstract class AFragmentCatalogSection extends SideRecyclerFragment {

    private String catalogSectionTitle;

    @Override
    public String requestTitleText() {
        return getCatalogSectionTitle();
    }

    public void setCatalogSectionTitle(String catalogSectionTitle) {
        this.catalogSectionTitle = catalogSectionTitle;
    }

    public String getCatalogSectionTitle() {
        return catalogSectionTitle;
    }

    @CallSuper
    @Override
    public void onRecyclerAdapterStateChange(int state) {
        if (state == SATE_FIRST_LOADING_COMPLETE) {
            if(catalogSectionTitle==null||catalogSectionTitle.isEmpty()) {
                RecyclerView.Adapter<?> adapter = getRecyclerView().getAdapter();
                if(adapter instanceof CatalogSectionAdapter){
                    CatalogSectionAdapter catalogSectionAdapter = (CatalogSectionAdapter) adapter;
                    CatalogSection catalogSection = catalogSectionAdapter.getCatalogSection();
                    if(catalogSection!=null) {
                        catalogSectionTitle = catalogSectionAdapter.getCatalogSection().getTitle();
                    }
                }
            }
        }
        super.onRecyclerAdapterStateChange(state);
    }

    @CallSuper
    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        String key = savedInstanceState.getString("catalogSectionTitle");
        if (key!=null) {
            catalogSectionTitle = key;
            savedInstanceState.remove("catalogSectionTitle");
        }
    }

    @CallSuper
    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("catalogSectionTitle");
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(catalogSectionTitle!=null) {
            outState.putString("catalogSectionTitle", catalogSectionTitle);
        }
    }

}
