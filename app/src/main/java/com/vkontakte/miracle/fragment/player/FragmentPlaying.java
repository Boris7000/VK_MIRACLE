package com.vkontakte.miracle.fragment.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.PlayingAdapter;
import com.vkontakte.miracle.engine.fragment.tabs.NestedMiracleFragment;
import com.vkontakte.miracle.engine.fragment.tabs.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.player.AudioPlayerData;
import com.vkontakte.miracle.player.OnPlayerEventListener;

public class FragmentPlaying extends NestedMiracleFragment {
    private View rootView;
    private MiracleActivity miracleActivity;
    private AudioPlayerData playerData;
    private PlayingAdapter playingAdapter;
    private final OnPlayerEventListener onPlayerEventListener = new OnPlayerEventListener() {
        @Override
        public void onBufferChange(AudioPlayerData playerData) {

        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerData playerData) {

        }

        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            if(FragmentPlaying.this.playerData==null){
                FragmentPlaying.this.playerData=playerData;
                playingAdapter = new PlayingAdapter(playerData);
                setAdapter(playingAdapter);
            } else {
                FragmentPlaying.this.playerData = playerData;
                playingAdapter.setItemDataHolders(playerData);
            }
        }

        @Override
        public void onPlayWhenReadyChange(AudioPlayerData playerData, boolean animate) {

        }

        @Override
        public void onRepeatModeChange(AudioPlayerData playerData) {

        }

        @Override
        public void onPlayerClose() {

        }
    };
    private final OnApplyWindowInsetsListener onApplyWindowInsetsListener = (v, windowInsets) -> {
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        rootView.setPadding(0,insets.top,0,insets.bottom);
        return windowInsets;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        iniContext();
        miracleActivity = getMiracleActivity();
        rootView = inflater.inflate(R.layout.fragment_playing, container, false);
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        miracleActivity.addOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        return rootView;
    }

    @Override
    public void onDestroy() {
        miracleActivity.removeOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        getMiracleApp().getPlayerServiceController().removeOnPlayerEventListener(onPlayerEventListener);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMiracleApp().getPlayerServiceController().
                addOnPlayerEventListener(onPlayerEventListener);
    }

    public static class Fabric extends NestedMiracleFragmentFabric {
        public Fabric(){
            super("", -1);
        }
        @NonNull
        @Override
        public NestedMiracleFragment createFragment() {
            return new FragmentPlaying();
        }
    }
}
