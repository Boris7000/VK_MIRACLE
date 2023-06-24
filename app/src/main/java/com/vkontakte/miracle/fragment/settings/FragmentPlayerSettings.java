package com.vkontakte.miracle.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.slider.Slider;
import com.miracle.engine.fragment.base.templates.BaseListFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;
import com.vkontakte.miracle.service.player.AudioPlayerSettingsUtil;

public class FragmentPlayerSettings extends BaseListFragment {

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        AudioPlayerSettingsUtil audioPlayerSettingsUtil = new AudioPlayerSettingsUtil(rootView.getContext());


        SwitchCompat wallpaperSwitch = rootView.findViewById(R.id.playerWallpaperSwitch);
        wallpaperSwitch.setChecked(audioPlayerSettingsUtil.getChangeWallpapers());
        wallpaperSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                audioPlayerSettingsUtil.storeChangeWallpapers(isChecked));

        Slider playbackReturnSlider = rootView.findViewById(R.id.playerPlaybackReturnSlider);
        playbackReturnSlider.setValue(audioPlayerSettingsUtil.getReturnPlaybackTime());
        playbackReturnSlider.addOnChangeListener((slider, value, fromUser) -> {
            if(fromUser){
                audioPlayerSettingsUtil.storeReturnPlaybackTime((int) value);
            }
        });

        Slider playbackStatisticsSlider = rootView.findViewById(R.id.playerPlaybackStatisticsSlider);
        playbackStatisticsSlider.setValue(audioPlayerSettingsUtil.getPlaybackStatisticsPercent()*100f);
        playbackStatisticsSlider.addOnChangeListener((slider, value, fromUser) -> {
            if(fromUser){
                float newValue = value/100f;
                audioPlayerSettingsUtil.storePlaybackStatisticsPercent(newValue);
                AudioPlayerServiceController.get().changePlaybackStatisticsPercent(newValue);
            }
        });
    }

    @Override
    public int requestTitleTextResId() {
        return R.string.player;
    }

    @Override
    public void inflateContent(@NonNull LayoutInflater inflater, @NonNull LinearLayout container) {
        inflater.inflate(R.layout.fragment_content_settings_audio_player, container, true);
    }


}
