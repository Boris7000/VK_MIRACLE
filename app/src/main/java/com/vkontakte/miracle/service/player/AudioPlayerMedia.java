package com.vkontakte.miracle.service.player;

import androidx.annotation.Nullable;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWC;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWF;
import com.vkontakte.miracle.model.audio.wraps.LoadableAudioItemWC;
import com.vkontakte.miracle.service.player.loader.AudioItemWCLoader;

import java.util.ArrayList;

public class AudioPlayerMedia implements AudioItemWC{

    private final AudioItemWC container;
    private final AudioItemWCLoader loader;
    private final ArrayList<ItemDataHolder> wraps = new ArrayList<>();
    private final ArrayList<AudioItem> audioItems = new ArrayList<>();
    private int itemIndex = 0;

    public AudioPlayerMedia(AudioPlayerMedia audioPlayerMedia, int itemIndex){
        this.container = audioPlayerMedia.container;
        this.loader = audioPlayerMedia.loader;
        addItems(audioPlayerMedia.wraps);
        setItemIndex(itemIndex);
    }

    public AudioPlayerMedia(AudioItemWCLoader loader, ArrayList<ItemDataHolder> wraps){
        this.container = loader.getContainer();
        this.loader = loader;
        addItems(wraps);
        setItemIndex(itemIndex);
    }

    public AudioPlayerMedia(AudioItemWC container, int itemIndex){
        this.container = container;
        if (container instanceof LoadableAudioItemWC){
            loader = ((LoadableAudioItemWC) container).createAudioLoader();
        } else {
            loader = null;
        }
        addItems(container.getAudioItems());
        setItemIndex(itemIndex);
    }

    public AudioPlayerMedia(ItemDataHolder itemDataHolder){
        if(!(itemDataHolder instanceof DataItemWrap<?,?>))
        {
            throw new IllegalArgumentException("The itemDataHolder must implement DataItemWrap.");
        }
        DataItemWrap<?,?> wrap = (DataItemWrap<?,?>)itemDataHolder;
        if(!(wrap.getHolder()instanceof AudioItemWC)||!(wrap.getItem()instanceof AudioItem)){
            throw new IllegalArgumentException("The DataItemWrap.container must implement AudioItemWC and the DataItemWrap.item must implement AudioItem.");
        }

        container = (AudioItemWC) wrap.getHolder();

        if (container instanceof LoadableAudioItemWC){
            loader = ((LoadableAudioItemWC) container).createAudioLoader();
        } else {
            loader = null;
        }
        addItems(container.getAudioItems());
        setItemIndex(container.getAudioItems().indexOf(wrap));
    }

    public AudioItemWC getContainer() {
        return container;
    }

    public AudioItemWCLoader getLoader() {
        return loader;
    }

    public ArrayList<AudioItem> getUnwrappedAudioItems() {
        return audioItems;
    }

    @Override
    public ArrayList<ItemDataHolder> getAudioItems() {
        return wraps;
    }

    public AudioItem getCurrentAudioItem(){
        return audioItems.get(itemIndex);
    }

    public ItemDataHolder getCurrentItem(){
        return (DataItemWrap<?, ?>) wraps.get(itemIndex);
    }

    public DataItemWrap<?,?> getCurrentWrap(){
        return (DataItemWrap<?, ?>) wraps.get(itemIndex);
    }

    public void addItems(ArrayList<ItemDataHolder> wraps){
        AudioItemWF audioItemWF = new AudioItemWF();
        for (ItemDataHolder itemDataHolder: wraps) {
            DataItemWrap<?,?> itemWrap = (DataItemWrap<?,?>)itemDataHolder;
            Object object = itemWrap.getItem();
            if(object instanceof AudioItem){
                AudioItem audioItem = (AudioItem) object;
                audioItems.add(audioItem);
                this.wraps.add(audioItemWF.create((AudioItem) audioItem, this));
            }
        }
    }

    public void setItemIndex(int itemIndex) {
        if(audioItems.isEmpty()){
            this.itemIndex = itemIndex;
        } else {
            if(itemIndex>this.itemIndex){
                for (int i = itemIndex; i<wraps.size(); i++){
                    AudioItem audioItem = audioItems.get(i);
                    if(audioItem.isLicensed()){
                        this.itemIndex = i;
                        break;
                    }
                }
            } else {
                for (int i = itemIndex; i>-1; i--){
                    AudioItem audioItem = audioItems.get(i);
                    if(audioItem.isLicensed()){
                        this.itemIndex = i;
                        break;
                    }
                }
            }
        }
    }

    public void incrementIndex(){
        if(itemIndex+1>=audioItems.size()){
            setItemIndex(0);
        } else {
            setItemIndex(itemIndex+1);
        }
    }

    public void decrementIndex(){
        if(itemIndex<=0){
            setItemIndex(audioItems.size()-1);
        } else {
            setItemIndex(itemIndex-1);
        }
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public boolean equalsSource(@Nullable AudioPlayerMedia audioPlayerMedia){
        if(audioPlayerMedia!=null && audioPlayerMedia.container != null && container!=null) {
            return container.equals(audioPlayerMedia.container);
        }
        return false;
    }
}
