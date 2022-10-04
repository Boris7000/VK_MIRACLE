package com.vkontakte.miracle.fragment.wall;

import static com.vkontakte.miracle.engine.adapter.AdapterStates.SATE_FIRST_LOADING_COMPLETE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.wall.ProfileAdapter;
import com.vkontakte.miracle.engine.fragment.side.SideRecyclerFragment;
import com.vkontakte.miracle.model.users.ProfileItem;

public class FragmentProfile extends SideRecyclerFragment {

    private String profileId;
    private String profileName;

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_recycleview_dark, container, false);
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new ProfileAdapter(profileId);
    }

    @Override
    public String requestTitleText() {
        if(profileName!=null){
            return profileName;
        }
        return super.requestTitleText();
    }

    @CallSuper
    @Override
    public void onRecyclerAdapterStateChange(int state) {
        if (state == SATE_FIRST_LOADING_COMPLETE) {
            if(profileName==null||profileName.isEmpty()) {
                RecyclerView.Adapter<?> adapter = getRecyclerView().getAdapter();
                if(adapter instanceof ProfileAdapter){
                    ProfileAdapter profileAdapter = (ProfileAdapter) adapter;
                    ProfileItem profileItem = profileAdapter.getProfileItem();
                    if(profileItem!=null){
                        profileName = profileItem.getFullName();
                    }
                }
            }
        }
        super.onRecyclerAdapterStateChange(state);
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        String key = savedInstanceState.getString("profileId");
        if(key!=null){
            profileId = key;
            savedInstanceState.remove("profileId");
        }
        key = savedInstanceState.getString("profileName");
        if(key!=null){
            profileName = key;
            savedInstanceState.remove("profileName");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("profileId");
        savedInstanceState.remove("profileName");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(profileId !=null){
            outState.putString("profileId", profileId);
        }
        if(profileName !=null){
            outState.putString("profileName", profileName);
        }
        super.onSaveInstanceState(outState);
    }
}
