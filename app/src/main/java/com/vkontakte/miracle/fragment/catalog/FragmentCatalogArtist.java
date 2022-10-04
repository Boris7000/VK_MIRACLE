package com.vkontakte.miracle.fragment.catalog;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.fields.Artist;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

public class FragmentCatalogArtist extends AFragmentCatalogSection {

    private String artistId;
    private String artistName;
    private String url;

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
        setCatalogSectionTitle(artistName);
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(artistId!=null) {
            return new CatalogSectionAdapter(Catalog.getAudioArtist(artistId, 1, profileItem.getAccessToken()));
        } else {
            if(url!=null){
                return new CatalogSectionAdapter(Catalog.getAudioArtistFromUrl(url, 1, profileItem.getAccessToken()));
            }
        }
        return null;
    }


    @CallSuper
    public void readSavedInstance(Bundle savedInstanceState){
        String key = savedInstanceState.getString("artistId");
        if(key!=null){
            artistId = key;
            savedInstanceState.remove("artistId");
        }
        key = savedInstanceState.getString("artistName");
        if(key!=null){
            artistName = key;
            savedInstanceState.remove("artistName");
        }
        key = savedInstanceState.getString("url");
        if (key!=null) {
            url = key;
            savedInstanceState.remove("url");
        }
        super.readSavedInstance(savedInstanceState);
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("artistId");
        savedInstanceState.remove("artistName");
        savedInstanceState.remove("url");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(artistId !=null){
            outState.putString("artistId", artistId);
        }
        if(artistName !=null){
            outState.putString("artistName", artistName);
        }
        if(url !=null){
            outState.putString("url", url);
        }
    }

}