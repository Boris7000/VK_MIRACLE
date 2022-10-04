package com.vkontakte.miracle.service.player;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.executors.audio.LoadShuffledPlaylist;
import com.vkontakte.miracle.executors.catalog.LoadCatalogBlock;
import com.vkontakte.miracle.model.audio.AudioWrapContainer;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.PlaylistShuffleItem;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;
import com.vkontakte.miracle.network.methods.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;


//TODO тут намешано всего
public class PlayerServiceController {

    private AudioPlayerService audioPLayerService;
    private AudioPlayerData playerData;
    private final ArrayList<OnPlayerEventListener> onPlayerEventListeners = new ArrayList<>();

    private final OnPlayerEventListener onPlayerEventListener = new OnPlayerEventListener() {
        @Override
        public void onBufferChange(AudioPlayerData playerData) {
            PlayerServiceController.this.playerData = playerData;
            final OnPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onBufferChange(playerData);
            }
        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerData playerData) {
            PlayerServiceController.this.playerData = playerData;
            final OnPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onPlaybackPositionChange(playerData);
            }
        }

        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            PlayerServiceController.this.playerData = playerData;
            final OnPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onSongChange(playerData, animate);
            }
        }

        @Override
        public void onPlayWhenReadyChange(AudioPlayerData playerData, boolean animate) {
            PlayerServiceController.this.playerData = playerData;
            final OnPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onPlayWhenReadyChange(playerData, animate);
            }
        }

        @Override
        public void onRepeatModeChange(AudioPlayerData playerData) {
            PlayerServiceController.this.playerData = playerData;
            final OnPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onRepeatModeChange(playerData);
            }
        }

        @Override
        public void onPlayerClose() {
            playerData = null;
            audioPLayerService =null;
            final OnPlayerEventListener[] onPlayerEventListeners = convertListenersToArray();
            for (OnPlayerEventListener a : onPlayerEventListeners) {
                a.onPlayerClose();
            }
        }
    };

    private OnPlayerEventListener[] convertListenersToArray(){
        OnPlayerEventListener[] listeners = new OnPlayerEventListener[onPlayerEventListeners.size()];
        onPlayerEventListeners.toArray(listeners);
        return listeners;
    }

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

        AudioWrapContainer container = audioPlayerData.getContainer();

        Log.d("oifirjfijrifjrif", "PlayerServiceController");
        for (ItemDataHolder itdh: container.getAudioItems()) {
            ItemDataHolder newItem = (ItemDataHolder) ((DataItemWrap<?, ?>) itdh).getItem();
            AudioItem newAudioItem = (AudioItem) newItem;
            Log.d("oifirjfijrifjrif", newAudioItem.getTitle());
        }

        if(container instanceof PlaylistItem){
            PlaylistItem playlistItem = (PlaylistItem) container;
            if(playlistItem.getAudioItems().size()<25&&playlistItem.getAudioItems().size()<playlistItem.getCount()){
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

        AudioWrapContainer container = audioPlayerData.getContainer();
        if(container instanceof PlaylistItem){
            PlaylistItem playlistItem = (PlaylistItem) container;
            if(playlistItem.getAudioItems().size()<25&&playlistItem.getAudioItems().size()<playlistItem.getCount()){
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

    private PlaylistItem copyPlaylist(PlaylistItem sourcePlaylistItem){
        PlaylistItem playlistItem = new PlaylistItem(sourcePlaylistItem);
        playlistItem.copyItems(sourcePlaylistItem);
        return playlistItem;
    }

    private CatalogBlock copyCatalogBlock(CatalogBlock sourceCatalogBlock){
        CatalogBlock catalogBlock = new CatalogBlock(sourceCatalogBlock);
        catalogBlock.copyItems(sourceCatalogBlock);
        return catalogBlock;
    }

    private void loadAndPlayNewAudio(AudioPlayerData audioPlayerData){

        new AsyncExecutor<AudioPlayerData>() {
            @Override
            public AudioPlayerData inBackground() {
                try {
                    AudioWrapContainer container = audioPlayerData.getContainer();
                    if(container instanceof PlaylistItem) {
                        PlaylistItem playlistItem = copyPlaylist((PlaylistItem) container);
                        loadAudiosToPlaylist(playlistItem);
                        return new AudioPlayerData(playlistItem.getAudioItems().get(audioPlayerData.getCurrentItemIndex()));
                    } else {
                        if(container instanceof CatalogBlock){
                            CatalogBlock catalogBlock = copyCatalogBlock((CatalogBlock) container);
                            loadAudiosToCatalogBlock(catalogBlock);
                            return new AudioPlayerData(catalogBlock.getAudioItems().get(audioPlayerData.getCurrentItemIndex()));
                        }
                    }
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
                    AudioWrapContainer container = audioPlayerData.getContainer();
                    if(container instanceof PlaylistItem) {
                        PlaylistItem playlistItem = copyPlaylist((PlaylistItem) container);
                        loadAudiosToPlaylist(playlistItem);
                        return new AudioPlayerData(playlistItem.getAudioItems().get(audioPlayerData.getCurrentItemIndex()));
                    } else {
                        if(container instanceof CatalogBlock){
                            CatalogBlock catalogBlock = copyCatalogBlock((CatalogBlock) container);
                            loadAudiosToCatalogBlock(catalogBlock);
                            return new AudioPlayerData(catalogBlock.getAudioItems().get(audioPlayerData.getCurrentItemIndex()));
                        }
                    }
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

    public void loadAndSetPlayNext(PlaylistItem sourcePlaylistItem){

        if(sourcePlaylistItem.getAudioItems().size()<25&&sourcePlaylistItem.getAudioItems().size()<sourcePlaylistItem.getCount()) {
            new AsyncExecutor<AudioPlayerData>() {
                @Override
                public AudioPlayerData inBackground() {
                    try {
                        PlaylistItem playlistItem = copyPlaylist(sourcePlaylistItem);
                        loadAudiosToPlaylist(playlistItem);
                        if(!playlistItem.getAudioItems().isEmpty()) {
                            return new AudioPlayerData(playlistItem.getAudioItems().get(0));
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
            setPlayNext(new AudioPlayerData(sourcePlaylistItem.getAudioItems().get(0)));
        }
    }

    public void loadAndPlayNewAudio(PlaylistShuffleItem playlistShuffleItem){
        new LoadShuffledPlaylist(playlistShuffleItem.getPlaylistItem()){
            @Override
            public void onExecute(PlaylistItem playlistItem) {
                if(playlistItem!=null&&!playlistItem.getAudioItems().isEmpty()) {
                    playNewAudio(new AudioPlayerData(playlistItem.getAudioItems().get(0)));
                }
            }
        }.start();
    }

    public void loadAndPlayNewAudio(CatalogAction catalogAction){
        new LoadCatalogBlock(catalogAction.getBlockId()){
            @Override
            public void onExecute(CatalogBlock catalogBlock) {
                if(catalogBlock!=null&&!catalogBlock.getAudioItems().isEmpty()) {
                    playNewAudio(new AudioPlayerData(catalogBlock.getAudioItems().get(0)));
                }
            }
        }.start();
    }

    private void loadAudiosToPlaylist(PlaylistItem playlistItem) throws Exception{
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(profileItem!=null) {
            Response<JSONObject> response = Execute.getPlaylist(playlistItem.getOwnerId(),
                    playlistItem.getId(), false, playlistItem.getAudioItems().size(),
                    Math.min(25, playlistItem.getCount()), playlistItem.getAccessKey(),
                    profileItem.getAccessToken()).execute();

            JSONObject jo_response = validateBody(response);

            jo_response = jo_response.getJSONObject("response");

            JSONArray items = jo_response.getJSONArray("audios");

            ArrayList<ItemDataHolder> audioItems = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject jo_item = items.getJSONObject(i);
                AudioItem audioItem = new AudioItem(jo_item);
                DataItemWrap<AudioItem, AudioWrapContainer> dataItemWrap =
                        new DataItemWrap<AudioItem, AudioWrapContainer>(audioItem, playlistItem) {
                            @Override
                            public int getViewHolderType() {
                                return TYPE_WRAPPED_AUDIO;
                            }
                        };
                audioItems.add(dataItemWrap);
            }
            playlistItem.getAudioItems().addAll(audioItems);
        }
    }

    private void loadAudiosToCatalogBlock(CatalogBlock catalogBlock) throws Exception{
        ProfileItem profileItem = StorageUtil.get().currentUser();
        if(profileItem!=null) {
            Response<JSONObject> response = Catalog.getBlockItems(catalogBlock.getId(),
                    catalogBlock.getNextFrom(), profileItem.getAccessToken()).execute();

            JSONObject jo_response = validateBody(response).getJSONObject("response");
            JSONObject block = jo_response.getJSONObject("block");

            CatalogExtendedArrays catalogExtendedArrays = new CatalogExtendedArrays(jo_response);

            ArrayList<ItemDataHolder> itemDataHolders = catalogBlock.findItems(block, catalogExtendedArrays);

            catalogBlock.getItems().addAll(itemDataHolders);

            if(block.has("next_from")){
                catalogBlock.setNextFrom(block.getString("next_from"));
            } else {
                catalogBlock.setNextFrom("");
            }
        }
    }

    public void updateTheme(){
        if(audioPLayerService!=null){
            audioPLayerService.updateTheme();
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
