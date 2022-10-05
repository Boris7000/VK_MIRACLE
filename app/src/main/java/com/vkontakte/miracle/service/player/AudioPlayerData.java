package com.vkontakte.miracle.service.player;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.AudioWrapContainer;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Catalog;
import com.vkontakte.miracle.network.methods.Execute;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class AudioPlayerData {

    private long duration;
    private long currentPosition = 0;
    private long bufferedPosition = 0;
    private int repeatMode;
    private boolean playWhenReady = true;
    private int currentItemIndex;
    private final AudioWrapContainer container;
    private final ArrayList<ItemDataHolder> items;
    private AudioItem currentItem;
    private DataItemWrap<?,?> currentItemWrap;
    private boolean hasError = false;

    public AudioPlayerData(ItemDataHolder itemDataHolder){
        currentItemWrap = (DataItemWrap<?,?>) itemDataHolder;
        container = (AudioWrapContainer) currentItemWrap.getHolder();
        currentItem = (AudioItem) currentItemWrap.getItem();
        items = new ArrayList<>(container.getAudioItems());
        currentItemIndex = items.indexOf(currentItemWrap);
        duration = currentItem.getDuration()*1000L;
    }

    public void reset(int currentItemIndex){
        DataItemWrap<?,?> itemWrap = (DataItemWrap<?,?>)items.get(currentItemIndex);
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        int directOrder = currentItemIndex>this.currentItemIndex?1:-1;
        while (!audioItem.isLicensed()&&currentItemIndex>-1&&currentItemIndex<items.size()){
            itemWrap = (DataItemWrap<?,?>)items.get(currentItemIndex+directOrder);
            audioItem = (AudioItem) itemWrap.getItem();
        }
        currentItemWrap = itemWrap;
        currentItem = audioItem;
        this.currentItemIndex = currentItemIndex;
        duration = currentItem.getDuration()*1000L;
        currentPosition = 0;
        bufferedPosition = 0;
        playWhenReady = true;
        hasError = false;
    }

    /////////////////////////////////////////////////////////

    public long getDuration() {
        return duration;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public long getBufferedPosition(){
        return bufferedPosition;
    }

    @Player.RepeatMode
    public int getRepeatMode() {
        return repeatMode;
    }

    public boolean getPlayWhenReady() {
        return playWhenReady;
    }

    public ArrayList<ItemDataHolder> getItems() {
        return items;
    }

    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    public AudioItem getCurrentItem() {
        return currentItem;
    }

    public DataItemWrap<?, ?> getCurrentItemWrap() {
        return currentItemWrap;
    }

    public AudioWrapContainer getContainer() {
        return container;
    }

    ////////////////////////////////////////////////////////

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setBufferedPosition(long bufferedPosition) {
        this.bufferedPosition = bufferedPosition;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public void loadMoreItems(OnAudioPlayerDataLoadingEnd listener){
        if(container instanceof PlaylistItem) {
            PlaylistItem playlistItem = (PlaylistItem) container;
            if(items.size()<playlistItem.getCount()){
                loadItemsForPlaylist(playlistItem, listener);
                return;
            }
        } else {
            if(container instanceof CatalogBlock){
                CatalogBlock catalogBlock = (CatalogBlock) container;
                if(!catalogBlock.getNextFrom().isEmpty()){
                    loadItemsForCatalog(catalogBlock, listener);
                    return;
                }
            }
        }
        listener.onLoadingEnd();
    }

    private void loadItemsForCatalog(CatalogBlock catalogBlock, OnAudioPlayerDataLoadingEnd listener){
        new AsyncExecutor<Boolean>() {
            @Override
            public Boolean inBackground() {
                try {
                    items.addAll(loadAudiosToCatalogBlock(catalogBlock));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            @Override
            public void onExecute(Boolean result) {
                if(result) {
                    listener.onLoadingEnd();
                }
            }
        }.start();
    }

    private void loadItemsForPlaylist(PlaylistItem playlistItem, OnAudioPlayerDataLoadingEnd listener){
        new AsyncExecutor<Boolean>() {
            @Override
            public Boolean inBackground() {
                try {
                    items.addAll(loadAudiosToPlaylist(playlistItem));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onExecute(Boolean result) {
                if(result) {
                    listener.onLoadingEnd();
                }
            }
        }.start();
    }

    @NonNull
    private static ArrayList<ItemDataHolder> loadAudiosToPlaylist(PlaylistItem playlistItem) throws Exception{
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
            return audioItems;
        }
        return new ArrayList<>();
    }

    @NonNull
    private static ArrayList<ItemDataHolder> loadAudiosToCatalogBlock(CatalogBlock catalogBlock) throws Exception{
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
            return itemDataHolders;
        }
        return new ArrayList<>();
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null){
            if(obj instanceof AudioPlayerData){
                AudioPlayerData audioPlayerData = (AudioPlayerData) obj;
                if(container!=null){
                    if(audioPlayerData.container!=null){
                        return container.equals(audioPlayerData.container);
                    }
                }
            }
        }
        return false;
    }

}


