package com.vkontakte.miracle.fragment.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.engine.fragment.side.SideTabsFragment;

import java.util.ArrayList;

public class FragmentFriends extends SideTabsFragment {

    private String ownerId;

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_tabs, container, false);
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
}
