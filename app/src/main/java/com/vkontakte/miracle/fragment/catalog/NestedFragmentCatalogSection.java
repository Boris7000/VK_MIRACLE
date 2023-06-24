package com.vkontakte.miracle.fragment.catalog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.tabs.nested.NestedMiracleFragmentFabric;
import com.miracle.engine.fragment.tabs.nested.templates.NestedRecyclerFragment;
import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;

public class NestedFragmentCatalogSection extends NestedRecyclerFragment {

    private String catalogSectionId;

    public void setCatalogSectionId(String catalogSectionId) {
        this.catalogSectionId = catalogSectionId;
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new CatalogSectionAdapter(catalogSectionId);
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("catalogSectionId");
        if (key != null) {
            catalogSectionId = key;
            savedInstanceState.remove("catalogSectionId");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("catalogSectionId");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(catalogSectionId!=null){
            outState.putString("catalogSectionId", catalogSectionId);
        }
        super.onSaveInstanceState(outState);
    }

    public static class Fabric extends NestedMiracleFragmentFabric {

        private final String catalogSectionId;

        public Fabric(String catalogSectionId, String catalogSectionTitle){
            super(catalogSectionTitle, -1);
            this.catalogSectionId = catalogSectionId;
        }

        @NonNull
        @Override
        public Fragment createFragment() {
            NestedFragmentCatalogSection nestedFragmentCatalogSection = new NestedFragmentCatalogSection();
            nestedFragmentCatalogSection.setCatalogSectionId(catalogSectionId);
            return nestedFragmentCatalogSection;
        }
    }
}
