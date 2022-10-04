package com.vkontakte.miracle.fragment.friends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.adapter.friends.OnlineFriendsAdapter;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.engine.fragment.nested.NestedRecyclerFragment;

public class NestedFragmentOnlineFriends extends NestedRecyclerFragment {

    private String ownerId;

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new OnlineFriendsAdapter(ownerId);
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

    public static class Fabric extends NestedMiracleFragmentFabric {
        private final String ownerId;
        public Fabric(String ownerId,String title, int icon) {
            super(title, icon);
            this.ownerId = ownerId;
        }

        @NonNull
        @Override
        public MiracleFragment createFragment() {
            NestedFragmentOnlineFriends nestedFragmentOnlineFriends = new NestedFragmentOnlineFriends();
            nestedFragmentOnlineFriends.setOwnerId(ownerId);
            return nestedFragmentOnlineFriends;
        }
    }
}
