package com.vkontakte.miracle.player;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class PlayerServiceController {

    private AudioPlayerService audioPLayerService;
    private AudioPlayerData playerData;
    private final ArrayList<OnPlayerEventListener> onPlayerEventListeners = new ArrayList<>();

    private final OnPlayerEventListener onPlayerEventListener = new OnPlayerEventListener() {
        @Override
        public void onBufferChange(AudioPlayerData playerData) {
            PlayerServiceController.this.playerData = playerData;
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onBufferChange(playerData);
            }
        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerData playerData) {
            PlayerServiceController.this.playerData = playerData;
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onPlaybackPositionChange(playerData);
            }
        }

        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            PlayerServiceController.this.playerData = playerData;
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onSongChange(playerData, animate);
            }
        }

        @Override
        public void onPlayWhenReadyChange(AudioPlayerData playerData, boolean animate) {
            PlayerServiceController.this.playerData = playerData;
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onPlayWhenReadyChange(playerData, animate);
            }
        }

        @Override
        public void onRepeatModeChange(AudioPlayerData playerData) {
            PlayerServiceController.this.playerData = playerData;
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onRepeatModeChange(playerData);
            }
        }

        @Override
        public void onPlayerClose() {
            playerData = null;
            audioPLayerService =null;
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onPlayerClose();
            }
        }
    };

    private static PlayerServiceController instance;

    public PlayerServiceController(){
        instance = this;
    }

    public static PlayerServiceController getInstance(){
        return new PlayerServiceController();
    }

    public void addOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener){
        if(playerData !=null) {
            onPlayerEventListener.onSongChange(playerData, false);
            onPlayerEventListener.onPlaybackPositionChange(playerData);
            onPlayerEventListener.onRepeatModeChange(playerData);
            onPlayerEventListener.onBufferChange(playerData);
            onPlayerEventListener.onPlayWhenReadyChange(playerData, false);
        }
        onPlayerEventListeners.add(onPlayerEventListener);
    }

    public void removeOnPlayerEventListener(OnPlayerEventListener onPlayerEventListener){
        onPlayerEventListeners.remove(onPlayerEventListener);
    }

    public OnPlayerEventListener getOnPlayerEventListener() {
        return onPlayerEventListener;
    }

    private void startAndPlay(AudioPlayerData audioPlayerData){

        MiracleApp miracleApp = MiracleApp.getInstance();

        Intent playerIntent = new Intent(miracleApp, AudioPlayerService.class);

        //Check is service is active
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            miracleApp.startForegroundService(playerIntent);
        } else {
            miracleApp.startService(playerIntent);
        }

        miracleApp.bindService(playerIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                audioPLayerService = ((PlayerServiceBinder) binder).getService();
                if(audioPLayerService!=null){
                    audioPLayerService.playNewAudio(audioPlayerData);
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                audioPLayerService = null;
            }

        }, Context.BIND_ABOVE_CLIENT);
    }

    public void playNewAudio(AudioPlayerData audioPlayerData){

        if(audioPlayerData.getPlaylistItem()!=null){
            PlaylistItem playlistItem = audioPlayerData.getPlaylistItem();
            if(playlistItem.getItems().size()<25&&playlistItem.getItems().size()<playlistItem.getCount()){
                loadAndPlayNewAudio(audioPlayerData);
                return;
            }
        }

        if(audioPLayerService !=null) {
            audioPLayerService.playNewAudio(audioPlayerData);
        } else {
            startAndPlay(audioPlayerData);
        }
    }

    public void setPlayNext(AudioPlayerData audioPlayerData){

        if(audioPlayerData.getPlaylistItem()!=null){
            PlaylistItem playlistItem = audioPlayerData.getPlaylistItem();
            if(playlistItem.getItems().size()<25&&playlistItem.getItems().size()<playlistItem.getCount()){
                loadAndSetPlayNext(audioPlayerData);
                return;
            }
        }

        if(audioPLayerService!=null) {
            audioPLayerService.setPlayNext(audioPlayerData);
        } else {
            startAndPlay(audioPlayerData);
        }
    }

    private void loadAndPlayNewAudio(AudioPlayerData audioPlayerData){

        new AsyncExecutor<AudioPlayerData>() {
            @Override
            public AudioPlayerData inBackground() {
                try {
                    PlaylistItem playlistItem1 = new PlaylistItem(audioPlayerData.getPlaylistItem());
                    loadAudiosToPlaylist(playlistItem1);
                    return new AudioPlayerData(playlistItem1.getItems().get(audioPlayerData.getCurrentItemIndex()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return audioPlayerData;
            }

            @Override
            public void onExecute(AudioPlayerData audioPlayerData) {
                playNewAudio(audioPlayerData);
            }
        }.start();
    }

    private void loadAndSetPlayNext(AudioPlayerData audioPlayerData){

        new AsyncExecutor<AudioPlayerData>() {
            @Override
            public AudioPlayerData inBackground() {
                try {
                    PlaylistItem playlistItem1 = new PlaylistItem(audioPlayerData.getPlaylistItem());
                    loadAudiosToPlaylist(playlistItem1);
                    return new AudioPlayerData(playlistItem1.getItems().get(audioPlayerData.getCurrentItemIndex()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return audioPlayerData;
            }

            @Override
            public void onExecute(AudioPlayerData audioPlayerData) {
                setPlayNext(audioPlayerData);
            }
        }.start();
    }

    public void loadAndPlayNewAudio(PlaylistItem playlistItem){

        if(playlistItem.getItems().size()<25&&playlistItem.getItems().size()<playlistItem.getCount()) {
            new AsyncExecutor<AudioPlayerData>() {
                @Override
                public AudioPlayerData inBackground() {
                    try {
                        PlaylistItem playlistItem1 = new PlaylistItem(playlistItem);
                        loadAudiosToPlaylist(playlistItem1);
                        return new AudioPlayerData(playlistItem1.getItems().get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                @Override
                public void onExecute(AudioPlayerData audioPlayerData) {
                    playNewAudio(audioPlayerData);
                }
            }.start();
        } else {
            playNewAudio(new AudioPlayerData(playlistItem.getItems().get(0)));
        }
    }

    public void loadAndSetPlayNext(PlaylistItem playlistItem){

        if(playlistItem.getItems().size()<25&&playlistItem.getItems().size()<playlistItem.getCount()) {
            new AsyncExecutor<AudioPlayerData>() {
                @Override
                public AudioPlayerData inBackground() {
                    try {
                        PlaylistItem playlistItem1 = new PlaylistItem(playlistItem);
                        loadAudiosToPlaylist(playlistItem1);
                        if(!playlistItem1.getItems().isEmpty()) {
                            return new AudioPlayerData(playlistItem1.getItems().get(0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public void onExecute(AudioPlayerData audioPlayerData) {
                    if(audioPlayerData!=null) {
                        setPlayNext(audioPlayerData);
                    }
                }
            }.start();
        } else {
            setPlayNext(new AudioPlayerData(playlistItem.getItems().get(0)));
        }
    }

    private void loadAudiosToPlaylist(PlaylistItem playlistItem) throws Exception{
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(profileItem!=null) {
            Response<JSONObject> response = Execute.getPlaylist(playlistItem.getOwnerId(),
                    playlistItem.getId(), false, playlistItem.getItems().size(),
                    Math.min(25, playlistItem.getCount()), playlistItem.getAccessKey(),
                    profileItem.getAccessToken()).execute();
            JSONObject jo_response = validateBody(response).getJSONObject("response");

            JSONArray items = jo_response.getJSONArray("audios");

            ArrayList<ItemDataHolder> audioItems = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject jo_item = items.getJSONObject(i);
                AudioItem audioItem = new AudioItem(jo_item);
                audioItem.setPlaylistItem(playlistItem);
                audioItems.add(audioItem);
            }
            playlistItem.getItems().addAll(audioItems);
        }
    }

    public void actionPrevious(){
        if(audioPLayerService!=null){
            audioPLayerService.skipToPrevious();
        }
    }

    public void actionPause(){
        if(audioPLayerService!=null){
            audioPLayerService.pause();
        }
    }

    public void actionResume(){
        if(audioPLayerService!=null){
            audioPLayerService.resume();
        }
    }

    public void actionNext(){
        if(audioPLayerService!=null){
            audioPLayerService.skipToNext();
        }
    }

    public void actionStop(){
        if(audioPLayerService!=null){
            audioPLayerService.stopService();
        }
    }

    public void actionSeekTo(long seekPosition){
        if(audioPLayerService!=null){
            audioPLayerService.seekTo(seekPosition);
        }
    }

    public void actionSeekToPercent(float seekPercent){
        if(audioPLayerService!=null){
            audioPLayerService.seekToPercent(seekPercent);
        }
    }

    public void actionChangeRepeatMode(int repeatMode){
        if(audioPLayerService!=null){
            audioPLayerService.changeRepeatMode(repeatMode);
        }
    }

    public static PlayerServiceController get(){
        if (null == instance){
            instance = PlayerServiceController.getInstance();
        }
        return instance;
    }

}
