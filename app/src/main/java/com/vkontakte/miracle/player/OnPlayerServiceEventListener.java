package com.vkontakte.miracle.player;

interface OnPlayerServiceEventListener {

    void onBufferChange(long bufferPosition);

    void onPlaybackPositionChange(long playbackPosition, long duration);

    void onSongChange(AudioPlayerData playerData, boolean animate);

    void onPlayWhenReadyChange(boolean playWhenReady, boolean animate);

    void onRepeatModeChange(int repeatMode);

    void onPlayerClose();

}
