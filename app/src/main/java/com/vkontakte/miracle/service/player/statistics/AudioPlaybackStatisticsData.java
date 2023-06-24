package com.vkontakte.miracle.service.player.statistics;

import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWC;
import com.vkontakte.miracle.service.player.AudioPlayerMedia;

public class AudioPlaybackStatisticsData {

    private final long startTime;
    private final String audioId;
    private final long duration;
    private long sendPlaybackPosition;
    private final String source;
    private final String playListId;

    private boolean sended;

    public AudioPlaybackStatisticsData(AudioPlayerMedia audioPlayerMedia){
        AudioItem audioItem = audioPlayerMedia.getCurrentAudioItem();
        audioId = audioItem.getOwnerId()+"_"+audioItem.getId();
        duration = audioItem.getDuration();

        AudioItemWC audioItemWC = audioPlayerMedia.getContainer();

        if(audioItemWC instanceof PlaylistItem){
            PlaylistItem playlistItem = (PlaylistItem) audioItemWC;
            source = "user_playlists";
            playListId = playlistItem.getOwnerId()+"_"+playlistItem.getId();
        } else {
            source = "my";
            playListId = null;
        }

        startTime = System.currentTimeMillis() / 1000;

    }

    public long getStartTime() {
        return startTime;
    }

    public String getAudioId() {
        return audioId;
    }

    public long getDuration() {
        return duration;
    }

    public String getSource() {
        return source;
    }

    public String getPlayListId() {
        return playListId;
    }

    public long getSendPlaybackPosition() {
        return sendPlaybackPosition;
    }

    public void setSendPlaybackPosition(long sendPlaybackPosition) {
        this.sendPlaybackPosition = sendPlaybackPosition;
    }

    public boolean wasSended() {
        return sended;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }
}
