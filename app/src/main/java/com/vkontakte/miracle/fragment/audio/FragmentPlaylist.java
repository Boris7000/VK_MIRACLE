package com.vkontakte.miracle.fragment.audio;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.PlaylistAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Album;

public class FragmentPlaylist extends SimpleMiracleFragment {

    private MiracleApp miracleApp;
    private PlaylistItem playlistItem;
    private Album album;

    public void setPlaylistItem(PlaylistItem playlistItem) {
        this.playlistItem = playlistItem;
    }

    public void setAlbum(Album album) {
        this.album = album;
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

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            String key = savedInstanceState.getString("playlistItem");
            if(key!=null){
                LargeDataStorage largeDataStorage = miracleApp.getLargeDataStorage();
                playlistItem = (PlaylistItem) largeDataStorage.getLargeData(key);
                savedInstanceState.remove("playlistItem");
            } else {
                key = savedInstanceState.getString("album");
                if(key!=null){
                    LargeDataStorage largeDataStorage = miracleApp.getLargeDataStorage();
                    album = (Album) largeDataStorage.getLargeData(key);
                    savedInstanceState.remove("album");
                }
            }
        }

        if(playlistItem!=null){
            setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout),
                    ()-> setAdapter(new PlaylistAdapter(playlistItem)));
            if(nullSavedAdapter(savedInstanceState)){
                setAdapter(new PlaylistAdapter(playlistItem));
            }
        } else {
            if(album!=null){
                setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout),
                        ()-> setAdapter(new PlaylistAdapter(album)));
                if(nullSavedAdapter(savedInstanceState)){
                    setAdapter(new PlaylistAdapter(album));
                }
            }
        }

        setTitleText(miracleActivity.getString(R.string.playlist));

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if(playlistItem !=null){
            LargeDataStorage largeDataStorage = miracleApp.getLargeDataStorage();
            String key = largeDataStorage.createUniqueKey();
            largeDataStorage.storeLargeData(playlistItem,key);
            outState.putString("playlistItem", key);
        } else {
            if(album !=null){
                LargeDataStorage largeDataStorage = miracleApp.getLargeDataStorage();
                String key = largeDataStorage.createUniqueKey();
                largeDataStorage.storeLargeData(album,key);
                outState.putString("album", key);
            }
        }

        super.onSaveInstanceState(outState);
    }
}
