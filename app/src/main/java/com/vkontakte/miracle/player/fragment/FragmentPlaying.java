package com.vkontakte.miracle.player.fragment;

import android.os.Bundle;
import android.os.Trace;
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
import com.vkontakte.miracle.player.PlayerServiceController;

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
                //rootView.post(() -> setAdapter(playingAdapter));
            } else {
                FragmentPlaying.this.playerData = playerData;
                playingAdapter.setNewItemDataHolders(playerData);
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
        rootView.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        return windowInsets;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        miracleActivity = getMiracleActivity();
        rootView = inflater.inflate(R.layout.fragment_playing, container, false);
        setRecyclerView(rootView.findViewById(R.id.recyclerView));
        miracleActivity.addOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        PlayerServiceController.get().addOnPlayerEventListener(onPlayerEventListener);
        return rootView;
    }

    @Override
    public void onDestroy() {
        miracleActivity.removeOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        PlayerServiceController.get().removeOnPlayerEventListener(onPlayerEventListener);
        super.onDestroy();
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
