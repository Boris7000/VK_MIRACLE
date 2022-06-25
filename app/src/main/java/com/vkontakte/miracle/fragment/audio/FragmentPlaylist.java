package com.vkontakte.miracle.fragment.audio;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.PlaylistAdapter;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Album;

public class FragmentPlaylist extends SimpleMiracleFragment {

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

        MiracleActivity miracleActivity = getMiracleActivity();

        View rootView = inflater.inflate(R.layout.fragment_with_recycleview, container, false);

        setAppBarLayout(rootView.findViewById(R.id.appbarlayout));
        setToolBar(getAppBarLayout().findViewById(R.id.toolbar));
        setAppbarClickToTop();
        setBackClick();
        setTitle(rootView.findViewById(R.id.title));
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        scrollAndElevate(getRecyclerView(),getAppBarLayout(), miracleActivity);
        setProgressBar(rootView.findViewById(R.id.progressCircle));

        if(savedInstanceState!=null&&!savedInstanceState.isEmpty()){
            String key = savedInstanceState.getString("playlistItem");
            if(key!=null){
                playlistItem = (PlaylistItem) LargeDataStorage.get().getLargeData(key);
                savedInstanceState.remove("playlistItem");
            } else {
                key = savedInstanceState.getString("album");
                if(key!=null){
                    album = (Album) LargeDataStorage.get().getLargeData(key);
                    savedInstanceState.remove("album");
                }
            }
        }

        if(playlistItem!=null){
            if(nullSavedAdapter(savedInstanceState)){
                setAdapter(new PlaylistAdapter(playlistItem));
            }
        } else {
            if(album!=null){
                if(nullSavedAdapter(savedInstanceState)){
                    setAdapter(new PlaylistAdapter(album));
                }
            }
        }

        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), this::reloadAdapter);

        setTitleText(miracleActivity.getString(R.string.playlist));

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if(playlistItem !=null){
            outState.putString("playlistItem", LargeDataStorage.get().storeLargeData(playlistItem));
        } else {
            if(album !=null){
                outState.putString("album", LargeDataStorage.get().storeLargeData(album));
            }
        }

        super.onSaveInstanceState(outState);
    }
}
