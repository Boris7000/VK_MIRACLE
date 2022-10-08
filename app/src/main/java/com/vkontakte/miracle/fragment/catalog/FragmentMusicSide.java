package com.vkontakte.miracle.fragment.catalog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.fragment.audio.FragmentOfflineAudioNested;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;

public class FragmentMusicSide extends ASideTabsFragmentCatalogSections {
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        View searchEditText = rootView.findViewById(R.id.searchBar);
        searchEditText.setOnClickListener(view -> NavigationUtil.goToAudioSearch(null, getContext()));

        return rootView;
    }

    @Override
    public ArrayList<NestedMiracleFragmentFabric> getErrorTabs() {
        ArrayList<NestedMiracleFragmentFabric> fabrics = new ArrayList<>();
        String title="";
        Context context = getContext();
        if(context!=null){
            title = context.getString(R.string.audio_offline);
        }
        fabrics.add(new FragmentOfflineAudioNested.Fabric(title));
        return fabrics;
    }

    @Override
    public boolean asyncLoadTabs() {
        return true;
    }

    @Override
    public Call<JSONObject> requestCall() {
        ProfileItem profileItem = StorageUtil.get().currentUser();
        return Catalog.getAudio(profileItem.getId(), profileItem.getAccessToken());
    }

}
