package com.vkontakte.miracle.player;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.AudioWrapContainer;

import java.util.ArrayList;

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
    private boolean hasError = false;

    public AudioPlayerData(ItemDataHolder itemDataHolder){
        DataItemWrap<?,?> itemWrap = (DataItemWrap<?,?>) itemDataHolder;
        container = (AudioWrapContainer) itemWrap.getHolder();
        currentItem = (AudioItem) itemWrap.getItem();
        items = new ArrayList<>(container.getAudioItems());
        currentItemIndex = items.indexOf(itemWrap);
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


