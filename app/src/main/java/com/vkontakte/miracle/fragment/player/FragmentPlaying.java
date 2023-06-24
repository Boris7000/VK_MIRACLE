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
import androidx.fragment.app.Fragment;

import com.miracle.engine.activity.MiracleActivity;
import com.miracle.engine.context.ContextExtractor;
import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.recycler.templates.RecyclerFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.PlayingNowAdapter;
import com.vkontakte.miracle.service.player.AudioPlayerEventListener;
import com.vkontakte.miracle.service.player.AudioPlayerMedia;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;

public class FragmentPlaying extends RecyclerFragment {

    private View rootView;
    private PlayingNowAdapter playingNowAdapter;

    private final AudioPlayerEventListener audioPlayerEventListener = new AudioPlayerEventListener() {
        @Override
        public void onMediaItemChange(AudioPlayerMedia audioPlayerMedia) {
            getRecyclerView().scrollToPosition(audioPlayerMedia.getItemIndex());
        }

        @Override
        public void onMediaChange(AudioPlayerMedia audioPlayerMedia) {
            if(playingNowAdapter ==null){
                playingNowAdapter = new PlayingNowAdapter(audioPlayerMedia);
                getRecyclerFragmentController().setRecyclerAdapter(playingNowAdapter);
            } else {
                playingNowAdapter.setNewAudioPlayerData(audioPlayerMedia);
                playingNowAdapter.load();
            }
        }
    };

    private final OnApplyWindowInsetsListener onApplyWindowInsetsListener = (v, windowInsets) -> {
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        rootView.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        return windowInsets;
    };

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        this.rootView = rootView;

        MiracleActivity miracleActivity = ContextExtractor.extractMiracleActivity(getContext());
        if(miracleActivity!=null) {
            miracleActivity.addOnApplyWindowInsetsListener(onApplyWindowInsetsListener);
        }

        AudioPlayerServiceController.get().addOnPlayerEventListener(audioPlayerEventListener);
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
        AudioPlayerServiceController.get().removeOnPlayerEventListener(audioPlayerEventListener);
        super.onDestroy();
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public Fragment createFragment() {
            return new FragmentPlaying();
        }
    }
}
