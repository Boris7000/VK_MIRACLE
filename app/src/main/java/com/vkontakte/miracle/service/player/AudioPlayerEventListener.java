package com.vkontakte.miracle.service.player;

public abstract class AudioPlayerEventListener {

    public void onDurationChange(AudioPlayerState audioPlayerState){}

    public void onPlaybackPositionChange(AudioPlayerState audioPlayerState){}

    public void onBufferChange(AudioPlayerState audioPlayerState){}

    public void onPlayWhenReadyChange(AudioPlayerState audioPlayerState){}

    public void onStateChange(AudioPlayerState audioPlayerState){}

    public void onRepeatModeChange(AudioPlayerState audioPlayerState){}

    public void onMediaItemChange(AudioPlayerMedia audioPlayerMedia){}

    public void onMediaChange(AudioPlayerMedia audioPlayerMedia){}

    public void onPlayerBind(AudioPlayerState audioPlayerState){}

    public void onPlayerClose(){}

}
