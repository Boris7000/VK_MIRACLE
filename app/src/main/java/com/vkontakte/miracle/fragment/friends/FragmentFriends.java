package com.vkontakte.miracle.fragment.friends;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.miracle.engine.fragment.tabs.nested.NestedMiracleFragmentFabric;
import com.miracle.engine.fragment.searchable.templates.SearchableBaseTabsFragment;

import java.util.ArrayList;

public class FragmentFriends extends SearchableBaseTabsFragment {

    private String ownerId;

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
    public ArrayList<NestedMiracleFragmentFabric> loadTabs() {
        ArrayList<NestedMiracleFragmentFabric> fabrics = new ArrayList<>();
        fabrics.add(new NestedFragmentAllFriends.Fabric(ownerId, "Все друзья", 0));
        fabrics.add(new NestedFragmentOnlineFriends.Fabric(ownerId, "Онлайн", 0));
        fabrics.add(new NestedFragmentMutualFriends.Fabric(ownerId, "Общие", 0));
        return fabrics;
    }

    @Override
    public ArrayList<NestedMiracleFragmentFabric> getErrorTabs() {
        return null;
    }

    @Override
    public void onSearchButtonClicked() {

    }
}
