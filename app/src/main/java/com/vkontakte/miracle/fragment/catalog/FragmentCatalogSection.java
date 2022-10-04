package com.vkontakte.miracle.fragment.catalog;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.util.LargeDataStorage;

public class FragmentCatalogSection extends AFragmentCatalogSection {

    private String catalogSectionId;

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new CatalogSectionAdapter(catalogSectionId);
    }

    public void setCatalogSectionId(String catalogSectionId){
        this.catalogSectionId = catalogSectionId;
    }

    @CallSuper
    public void readSavedInstance(Bundle savedInstanceState){
        String key = savedInstanceState.getString("catalogSectionId");
        if (key!=null) {
            catalogSectionId = key;
            savedInstanceState.remove("catalogSectionId");
        }
        super.readSavedInstance(savedInstanceState);
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("catalogSectionId");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(catalogSectionId!=null) {
            outState.putString("catalogSectionId", catalogSectionId);
        }
    }

}