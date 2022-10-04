package com.vkontakte.miracle.fragment.catalog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

import org.json.JSONObject;

import retrofit2.Call;

public class FragmentCatalogSectionUrl extends AFragmentCatalogSection {

    private String catalogSectionUrl;

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {

        ProfileItem profileItem = StorageUtil.get().currentUser();
        Call<JSONObject> call = null;

        if(catalogSectionUrl.indexOf("https://vk.com/audio?section")==0){
            call = Catalog.getAudioFromUrl(catalogSectionUrl, profileItem.getAccessToken());
        }

        if(call!=null) {
            return new CatalogSectionAdapter(call);
        }

        return null;
    }


    public void setCatalogSectionUrl(String catalogSectionUrl) {
        this.catalogSectionUrl = catalogSectionUrl;
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        String key = savedInstanceState.getString("catalogSectionUrl");
        if (key!=null) {
            catalogSectionUrl = key;
            savedInstanceState.remove("catalogSectionUrl");
        }
        super.readSavedInstance(savedInstanceState);
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("catalogSectionUrl");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(catalogSectionUrl!=null) {
            outState.putString("catalogSectionUrl", catalogSectionUrl);
        }
    }

}