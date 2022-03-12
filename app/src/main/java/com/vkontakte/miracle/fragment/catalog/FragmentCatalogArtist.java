package com.vkontakte.miracle.fragment.catalog;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.catalog.CatalogSectionAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.model.audio.fields.Artist;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;

public class FragmentCatalogArtist extends SimpleMiracleFragment {

    private MiracleApp miracleApp;
    private Artist artist;

    public void setArtistId(Artist artist) {
        this.artist = artist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        iniContext();

        MiracleActivity miracleActivity = getMiracleActivity();
        miracleApp = getMiracleApp();

        View rootView = inflater.inflate(R.layout.fragment_with_recycleview, container, false);

        setTopBar(rootView.findViewById(R.id.appbarLinear));
        setAppBarLayout(rootView.findViewById(R.id.appbar));
        setBackClick(rootView.findViewById(R.id.backButton));
        setTitle(rootView.findViewById(R.id.title));
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        scrollAndElevate(getRecyclerView(),getAppBarLayout(), miracleActivity);
        setProgressBar(rootView.findViewById(R.id.progressCircle));

        ProfileItem profileItem = miracleActivity.getUserItem();
        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            String key = savedInstanceState.getString("artist");
            if(key!=null){
                artist = (Artist) LargeDataStorage.get().getLargeData(key);
                savedInstanceState.remove("artist");
            }
        }

        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), ()->
                setAdapter(new CatalogSectionAdapter(Catalog.getAudioArtist(artist.getId(), 1, profileItem.getAccessToken()))));
        if(nullSavedAdapter(savedInstanceState)){
            setAdapter(new CatalogSectionAdapter(Catalog.getAudioArtist(artist.getId(), 1, profileItem.getAccessToken())));
        }

        setTitleText(artist.getName());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(artist !=null){
            outState.putString("artist", LargeDataStorage.get().storeLargeData(artist));
        }

        super.onSaveInstanceState(outState);
    }

}