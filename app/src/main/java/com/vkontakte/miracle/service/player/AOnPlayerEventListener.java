package com.vkontakte.miracle.service.player;

public abstract class AOnPlayerEventListener implements OnPlayerEventListener{
    @Override
    public void onBufferChange(AudioPlayerData playerData) {

    }

    @Override
    public void onPlaybackPositionChange(AudioPlayerData playerData) {

    }

    @Override
    public void onSongChange(AudioPlayerData playerData, boolean animate) {

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
}
