package com.vkontakte.miracle.player;

public interface OnPlayerEventListener {

    void onBufferChange(AudioPlayerData playerData);

    void onPlaybackPositionChange(AudioPlayerData playerData);

    void onSongChange(AudioPlayerData playerData, boolean animate);

    void onPlayWhenReadyChange(AudioPlayerData playerData, boolean animate);

    void onRepeatModeChange(AudioPlayerData playerData);

    void onPlayerClose();

}
