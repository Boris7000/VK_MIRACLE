package com.vkontakte.miracle.service.player;


import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

public class AudioPlayerState {

    public final static int STATE_PLAYING = 0;
    public final static int STATE_PAUSED = 1;
    public final static int STATE_BUFFERING = 2;
    public final static int STATE_ERROR = 3;

    private int state = STATE_BUFFERING;
    private boolean playWhenReady = true;

    @Player.RepeatMode
    private int repeatMode = ExoPlayer.REPEAT_MODE_OFF;

    private long duration = 1;
    private long playbackPosition = 0;
    private long bufferedPosition = 0;


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean getPlayWhenReady() {
        return playWhenReady;
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
    }

    @Player.RepeatMode
    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(@Player.RepeatMode int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getPlaybackPosition() {
        return playbackPosition;
    }

    public void setPlaybackPosition(long playbackPosition) {
        this.playbackPosition = playbackPosition;
    }

    public long getBufferedPosition() {
        return bufferedPosition;
    }

    public void setBufferedPosition(long bufferedPosition) {
        this.bufferedPosition = bufferedPosition;
    }
}
