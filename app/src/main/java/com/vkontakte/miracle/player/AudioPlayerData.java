package com.vkontakte.miracle.player;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.catalog.CatalogBlock;

import java.util.ArrayList;

public class AudioPlayerData {

    private long duration;
    private long currentPosition = 0;
    private long bufferedPosition = 0;
    private int repeatMode;
    private boolean playWhenReady = true;
    private ArrayList<ItemDataHolder> items;
    private int currentItemIndex;
    private AudioItem currentItem;
    private CatalogBlock catalogBlock;
    private PlaylistItem playlistItem;
    private boolean hasError = false;

    public AudioPlayerData(ItemDataHolder itemDataHolder){
        currentItem = (AudioItem) itemDataHolder;
        duration = currentItem.getDuration()*1000L;
        if(currentItem.getCatalogBlock()!=null) {
            catalogBlock = currentItem.getCatalogBlock();
            items = new ArrayList<>(catalogBlock.getItems());
            currentItemIndex = items.indexOf(itemDataHolder);
        } else {
            if(currentItem.getPlaylistItem()!=null){
                playlistItem = currentItem.getPlaylistItem();
                items = new ArrayList<>(playlistItem.getItems());
                currentItemIndex = items.indexOf(itemDataHolder);
            }
        }
    }

    public void reset(int currentItemIndex){
        currentItem = (AudioItem) items.get(currentItemIndex);
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

    public PlaylistItem getPlaylistItem() {
        return playlistItem;
    }

    public CatalogBlock getCatalogBlock() {
        return catalogBlock;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null){
            if(obj instanceof AudioPlayerData){
                AudioPlayerData audioPlayerData = (AudioPlayerData) obj;
                if(catalogBlock!=null){
                    if(audioPlayerData.catalogBlock!=null){
                        return catalogBlock.equals(audioPlayerData.catalogBlock);
                    }
                } else {
                    if(playlistItem!=null){
                        if(audioPlayerData.playlistItem!=null){
                            return playlistItem.equals(audioPlayerData.playlistItem);
                        }
                    }
                }
            }
        }
        return false;
    }


}


