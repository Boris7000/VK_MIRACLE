package com.vkontakte.miracle.fragment.catalog;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Catalog;

import org.json.JSONObject;

import retrofit2.Call;

public class FragmentCatalogGroups extends ATabsFragmentCatalogSections {

    private String ownerId;

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public Call<JSONObject> requestCall() {
        User user = StorageUtil.get().currentUser();
        return Catalog.getGroups(ownerId, user.getAccessToken());
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("ownerId");
        if (key != null) {
            ownerId = key;
            savedInstanceState.remove("ownerId");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("ownerId");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(ownerId !=null){
            outState.putString("ownerId", ownerId);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean asyncLoadTabs() {
        return true;
    }

    @Override
    public void onSearchButtonClicked() {

    }
}
