package com.vkontakte.miracle.service.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import androidx.media3.common.Player.RepeatMode;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.engine.util.StatisticsController;
import com.vkontakte.miracle.executors.audio.SendPlaybackStatistics;
import com.vkontakte.miracle.service.player.loader.AudioItemWCLoader;
import com.vkontakte.miracle.service.player.loader.AudioLoaderExecutor;
import com.vkontakte.miracle.service.player.statistics.AudioPlaybackStatisticsData;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class AudioPlayerServiceController {

    private AudioPlayerService audioPLayerService;

    private AudioPlayerState audioPlayerState;
    private AudioPlayerMedia audioPlayerMedia;
    private AudioPlaybackStatisticsData statisticsData;

    private float playbackStatisticsPercent = AudioPlayerSettingsUtil.get().getPlaybackStatisticsPercent();

    //Threads
    private AudioLoaderExecutor audioLoader;

    private static AudioPlayerServiceController instance;

    public AudioPlayerServiceController(){
        instance = this;
    }

    public static AudioPlayerServiceController getInstance(){
        return new AudioPlayerServiceController();
    }

    public static AudioPlayerServiceController get(){
        if (null == instance){
            instance = AudioPlayerServiceController.getInstance();
        }
        return instance;
    }

    private final AudioPlayerEventListener audioPlayerEventListener = new AudioPlayerEventListener() {
        @Override
        public void onDurationChange(AudioPlayerState audioPlayerState) {
            AudioPlayerServiceController.this.audioPlayerState = audioPlayerState;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onDurationChange(audioPlayerState);
            }
        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerState audioPlayerState) {
            AudioPlayerServiceController.this.audioPlayerState = audioPlayerState;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onPlaybackPositionChange(audioPlayerState);
            }

            if(statisticsData!=null&&!statisticsData.wasSended()){
                if(audioPlayerState.getPlaybackPosition()>=statisticsData.getSendPlaybackPosition()){
                    statisticsData.setSended(true);
                    StatisticsController.get().sendStatistics(new SendPlaybackStatistics(statisticsData));
                }
            }
        }

        @Override
        public void onBufferChange(AudioPlayerState audioPlayerState) {
            AudioPlayerServiceController.this.audioPlayerState = audioPlayerState;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onBufferChange(audioPlayerState);
            }
        }

        @Override
        public void onStateChange(AudioPlayerState audioPlayerState) {
            AudioPlayerServiceController.this.audioPlayerState = audioPlayerState;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onStateChange(audioPlayerState);
            }
        }

        @Override
        public void onPlayWhenReadyChange(AudioPlayerState audioPlayerState) {
            AudioPlayerServiceController.this.audioPlayerState = audioPlayerState;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onPlayWhenReadyChange(audioPlayerState);
            }
        }

        @Override
        public void onRepeatModeChange(AudioPlayerState audioPlayerState) {
            AudioPlayerServiceController.this.audioPlayerState = audioPlayerState;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onRepeatModeChange(audioPlayerState);
            }
        }

        @Override
        public void onMediaItemChange(AudioPlayerMedia audioPlayerMedia) {
            AudioPlayerServiceController.this.audioPlayerMedia = audioPlayerMedia;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onMediaItemChange(audioPlayerMedia);
            }

            if(audioPlayerMedia.getItemIndex()>=audioPlayerMedia.getAudioItems().size()-3){
               loadMoreAudio();
            }

            statisticsData = new AudioPlaybackStatisticsData(audioPlayerMedia);
            statisticsData.setSendPlaybackPosition((long)(playbackStatisticsPercent*(1000*statisticsData.getDuration())));
        }

        @Override
        public void onMediaChange(AudioPlayerMedia audioPlayerMedia) {
            AudioPlayerServiceController.this.audioPlayerMedia = audioPlayerMedia;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onMediaChange(audioPlayerMedia);
            }
        }

        @Override
        public void onPlayerBind(AudioPlayerState audioPlayerState) {
            AudioPlayerServiceController.this.audioPlayerState = audioPlayerState;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onPlayerBind(audioPlayerState);
            }
        }

        @Override
        public void onPlayerClose() {
            AudioPlayerServiceController.this.audioPlayerState = null;
            AudioPlayerServiceController.this.audioPlayerMedia = null;
            final AudioPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (AudioPlayerEventListener a : onPlayerEventListeners) {
                a.onPlayerClose();
            }
        }
    };

    @NonNull
    private final LinkedHashSet<AudioPlayerEventListener> audioPlayerEventListeners = new LinkedHashSet<>();

    private AudioPlayerEventListener[] convertListenersToArray(){
        AudioPlayerEventListener[] listeners = new AudioPlayerEventListener[audioPlayerEventListeners.size()];
        audioPlayerEventListeners.toArray(listeners);
        return listeners;
    }

    public void addOnPlayerEventListener(AudioPlayerEventListener audioPlayerEventListener){
        audioPlayerEventListeners.add(audioPlayerEventListener);
        if(audioPlayerMedia!=null){
            audioPlayerEventListener.onMediaChange(audioPlayerMedia);
            audioPlayerEventListener.onMediaItemChange(audioPlayerMedia);
        }
        if(audioPlayerState!=null){
            audioPlayerEventListener.onPlayerBind(audioPlayerState);
        }
    }

    public void removeOnPlayerEventListener(AudioPlayerEventListener audioPlayerEventListener){
        audioPlayerEventListeners.remove(audioPlayerEventListener);
    }

    public void clearAudioPlayerEventListeners() {
        audioPlayerEventListeners.clear();
    }

    ///////////////////////////////////////////////////////////////////////////

    public void play() {
        if(audioPLayerService!=null){
            audioPLayerService.play();
        }
    }

    public void pause() {
        if(audioPLayerService!=null){
            audioPLayerService.pause();
        }
    }

    public void skipToNext() {
        if(audioPLayerService!=null){
            audioPLayerService.skipToNext();
        }
    }

    public void skipToPrevious() {
        if(audioPLayerService!=null){
            audioPLayerService.skipToPrevious();
        }
    }

    public void skipTo(int pos){
        if(audioPLayerService!=null){
            audioPLayerService.skipTo(pos);
        }
    }

    public void seekTo(long pos) {
        if(audioPLayerService!=null){
            audioPLayerService.seekTo(pos);
        }
    }

    public void seekToPercent(float percent) {
        if(audioPLayerService!=null){
            audioPLayerService.seekToPercent(percent);
        }
    }

    public void stop() {
        if(audioPLayerService!=null){
            audioPLayerService.stop();
        }
    }

    public void changeRepeatMode(@RepeatMode int repeatMode) {
        if(audioPLayerService!=null){
            audioPLayerService.changeRepeatMode(repeatMode);
        }
    }

    public void changePlaybackStatisticsPercent(@FloatRange(from = 0f, to = 1f) float playbackStatisticsPercent){
        this.playbackStatisticsPercent = playbackStatisticsPercent;
        if(statisticsData!=null){
            statisticsData.setSendPlaybackPosition((long)(playbackStatisticsPercent*(1000*statisticsData.getDuration())));
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public void loadMoreAudio(){

        final AudioItemWCLoader loader = audioPlayerMedia.getLoader();

        if(loader!=null && loader.canLoadMore()) {
            if(audioLoader==null||audioLoader.workIsDone()){
                audioLoader = new AudioLoaderExecutor(loader){
                    @Override
                    public void onExecute(ArrayList<ItemDataHolder> result) {
                        if(result!=null&&!isInterrupted()){
                            audioPlayerMedia.addItems(result);
                            audioPlayerEventListener.onMediaChange(audioPlayerMedia);
                        }
                    }
                };
                audioLoader.start();
            }
        }
    }

    private void startAndPlay(AudioPlayerMedia audioPlayerMedia){

        MainApp mainApp = MainApp.getInstance();

        Intent playerIntent = new Intent(mainApp, AudioPlayerService.class);

        //Check is service is active
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mainApp.startForegroundService(playerIntent);
        } else {
            mainApp.startService(playerIntent);
        }

        mainApp.bindService(playerIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                audioPLayerService = ((AudioPlayerServiceBinder) binder).getService();
                if(audioPLayerService!=null){
                    audioPLayerService.addAudioPlayerEventListener(audioPlayerEventListener);
                    audioPLayerService.playNewMedia(audioPlayerMedia);
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                audioPLayerService.clearAudioPlayerEventListeners();
                audioPLayerService = null;
            }

        }, Context.BIND_ABOVE_CLIENT);
    }

    private void playOrStart(AudioPlayerMedia audioPlayerMedia){
        if(audioPLayerService !=null) {
            audioPLayerService.playNewMedia(audioPlayerMedia);
        } else {
            startAndPlay(audioPlayerMedia);
        }
    }

    private void setNextOrStart(AudioPlayerMedia audioPlayerMedia){
        if(audioPLayerService !=null) {
            audioPLayerService.playNextMedia(audioPlayerMedia);
        } else {
            startAndPlay(audioPlayerMedia);
        }
    }

    public void playNewAudio(AudioPlayerMedia audioPlayerMedia){

        final AudioItemWCLoader loader = audioPlayerMedia.getLoader();

        if(audioPlayerMedia.getAudioItems().size()<12
                && loader!=null
                && loader.canLoadMore()){

            if(audioLoader!=null){
                audioLoader.interrupt();
            }

            audioLoader = new AudioLoaderExecutor(loader) {
                @Override
                public void onExecute(ArrayList<ItemDataHolder> result) {
                    if(result!=null&&!isInterrupted()){
                        audioPlayerMedia.addItems(result);
                        playOrStart(audioPlayerMedia);
                    }
                }
            };

            audioLoader.start();
        } else {
            playOrStart(audioPlayerMedia);
        }
    }

    public void playNewAudio(AudioItemWCLoader loader){

        if(audioLoader!=null){
            audioLoader.interrupt();
        }

        audioLoader = new AudioLoaderExecutor(loader) {
            @Override
            public void onExecute(ArrayList<ItemDataHolder> result) {
                if(result!=null&&!isInterrupted()){
                    AudioPlayerMedia audioPlayerMedia =
                            new AudioPlayerMedia(getLoader(),result);
                    playOrStart(audioPlayerMedia);
                }
            }
        };

        audioLoader.start();
    }

    public void setPlayNext(AudioPlayerMedia audioPlayerMedia){

        final AudioItemWCLoader loader = audioPlayerMedia.getLoader();

        if(audioPlayerMedia.getAudioItems().size()<12
                && loader!=null
                && loader.canLoadMore()){

            if(audioLoader!=null){
                audioLoader.interrupt();
            }


            audioLoader = new AudioLoaderExecutor(loader) {
                @Override
                public void onExecute(ArrayList<ItemDataHolder> result) {
                    if(result!=null&&!isInterrupted()){
                        audioPlayerMedia.addItems(result);
                        setNextOrStart(audioPlayerMedia);
                    }
                }
            };

            audioLoader.start();

        } else {
            setNextOrStart(audioPlayerMedia);
        }
    }

    public void updateTheme(){
        if(audioPLayerService!=null){
            audioPLayerService.updateTheme();
        }
    }

}
