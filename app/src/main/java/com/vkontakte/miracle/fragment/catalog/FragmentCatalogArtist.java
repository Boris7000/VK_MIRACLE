package com.vkontakte.miracle.fragment.catalog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Catalog;

public class FragmentCatalogArtist extends ARecyclerFragmentCatalogSection {

    private String artistId;
    private String url;
    private String artistName;

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        User user = StorageUtil.get().currentUser();
        if(artistId!=null) {
            return new CatalogSectionAdapter(Catalog.getAudioArtist(artistId, 1, user.getAccessToken()));
        } else {
            if(url!=null){
                return new CatalogSectionAdapter(Catalog.getAudioArtistFromUrl(url, 1, user.getAccessToken()));
            }
        }
        return null;
    }

    @Override
    public String requestTitleText() {
        if(artistName!=null){
            return artistName;
        }
        return super.requestTitleText();
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState){
        super.readSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("artistId");
        if(key!=null){
            artistId = key;
            savedInstanceState.remove("artistId");
        }
        key = savedInstanceState.getString("url");
        if (key!=null) {
            url = key;
            savedInstanceState.remove("url");
        }
        key = savedInstanceState.getString("artistName");
        if(key!=null){
            artistName = key;
            savedInstanceState.remove("artistName");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("artistId");
        savedInstanceState.remove("url");
        savedInstanceState.remove("artistName");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(artistId !=null){
            outState.putString("artistId", artistId);
        }
        if(url !=null){
            outState.putString("url", url);
        }
        if(artistName !=null){
            outState.putString("artistName", artistName);
        }
    }

}