package com.vkontakte.miracle.fragment.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.PlayingAdapter;
import com.vkontakte.miracle.engine.activity.MiracleActivity;
import com.vkontakte.miracle.engine.context.ContextExtractor;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.recycler.RecyclerFragment;
import com.vkontakte.miracle.service.player.AOnPlayerEventListener;
import com.vkontakte.miracle.service.player.AudioPlayerData;
import com.vkontakte.miracle.service.player.OnPlayerEventListener;
import com.vkontakte.miracle.service.player.PlayerServiceController;

public class FragmentPlaying extends RecyclerFragment {

    private View rootView;
    private PlayingAdapter playingAdapter;
    private final OnPlayerEventListener onPlayerEventListener = new AOnPlayerEventListener() {
        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            if(playingAdapter==null){
                playingAdapter = new PlayingAdapter(playerData);
                getRecyclerFragmentController().setRecyclerAdapter(playingAdapter);
            } else {
                playingAdapter.setNewAudioPlayerData(playerData);
                playingAdapter.load();
            }
            getRecyclerView().scrollToPosition(playerData.getCurrentItemIndex());
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
        
        rootView = super.onCreateView(inflater, container, savedInstanceState);

        MiracleActivity miracleActivity = ContextExtractor.extractMiracleActivity(getContext());
        if(miracleActivity!=null) {
            miracleActivity.addOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        }
        
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
        MiracleActivity miracleActivity = ContextExtractor.extractMiracleActivity(getContext());
        if(miracleActivity!=null) {
            miracleActivity.removeOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        }
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
