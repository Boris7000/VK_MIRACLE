package com.vkontakte.miracle.fragment.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.PlayingAdapter;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.recycler.RecyclerFragment;
import com.vkontakte.miracle.service.player.AudioPlayerData;
import com.vkontakte.miracle.service.player.OnPlayerEventListener;
import com.vkontakte.miracle.service.player.PlayerServiceController;

public class FragmentPlaying extends RecyclerFragment {

    private View rootView;
    private MainActivity mainActivity;
    private AudioPlayerData playerData;
    private PlayingAdapter playingAdapter;
    private final OnPlayerEventListener onPlayerEventListener = new OnPlayerEventListener() {
        @Override
        public void onBufferChange(AudioPlayerData playerData) {}

        @Override
        public void onPlaybackPositionChange(AudioPlayerData playerData) {}

        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            if(FragmentPlaying.this.playerData==null){
                FragmentPlaying.this.playerData=playerData;
                playingAdapter = new PlayingAdapter(playerData);
                getRecyclerFragmentController().setRecyclerAdapter(playingAdapter);
            } else {
                FragmentPlaying.this.playerData = playerData;
                playingAdapter.setNewAudioPlayerData(playerData);
                playingAdapter.load();
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

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainActivity = getMiracleActivity();
        
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        
        mainActivity.addOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        
        PlayerServiceController.get().addOnPlayerEventListener(onPlayerEventListener);
        
        return rootView;
    }

    @Override
    public boolean saveRecyclerAdapterSate() {
        return false;
    }

    @Override
    public boolean autoSetRecyclerAdapter() {
        return false;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_playing, container, false);
    }

    @Override
    public void onDestroy() {
        mainActivity.removeOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        PlayerServiceController.get().removeOnPlayerEventListener(onPlayerEventListener);
        super.onDestroy();
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public MiracleFragment createFragment() {
            return new FragmentPlaying();
        }
    }
}
