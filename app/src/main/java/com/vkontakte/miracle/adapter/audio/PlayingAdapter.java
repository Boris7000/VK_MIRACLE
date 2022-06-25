package com.vkontakte.miracle.adapter.audio;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_LOADING;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;

import android.util.ArrayMap;
import android.util.Log;

import androidx.recyclerview.widget.DiffUtil;

import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleUniversalAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.loading.LoadingViewHolder;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.player.AudioPlayerData;

import java.util.ArrayList;

public class PlayingAdapter extends MiracleUniversalAdapter {

    private AudioPlayerData playerData;

    public PlayingAdapter(AudioPlayerData playerData){
        this.playerData=playerData;
    }

    public void setNewItemDataHolders(AudioPlayerData playerData){
        this.playerData=playerData;
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        ArrayList<ItemDataHolder> newHolders = playerData.getItems();
        if(newHolders.size()>0) {
            final int oldSize = holders.size();
            final int newSize = newHolders.size();
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldSize;
                }

                @Override
                public int getNewListSize() {
                    return newSize;
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    ItemDataHolder oldItem = holders.get(oldItemPosition);
                    ItemDataHolder newItem = newHolders.get(newItemPosition);
                    if (oldItem instanceof DataItemWrap && newItem instanceof DataItemWrap) {
                        oldItem = (ItemDataHolder) ((DataItemWrap<?, ?>) oldItem).getItem();
                        newItem = (ItemDataHolder) ((DataItemWrap<?, ?>) newItem).getItem();
                    }
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ItemDataHolder oldItem = holders.get(oldItemPosition);
                    ItemDataHolder newItem = newHolders.get(newItemPosition);
                    if (oldItem instanceof DataItemWrap && newItem instanceof DataItemWrap) {
                        oldItem = (ItemDataHolder) ((DataItemWrap<?, ?>) oldItem).getItem();
                        newItem = (ItemDataHolder) ((DataItemWrap<?, ?>) newItem).getItem();
                    }
                    if (oldItem instanceof AudioItem && newItem instanceof AudioItem) {
                        AudioItem oldAudioItem = (AudioItem) oldItem;
                        AudioItem newAudioItem = (AudioItem) newItem;
                        return oldAudioItem.equalsContent(newAudioItem);
                    }
                    return false;
                }
            });
            holders.clear();
            holders.addAll(newHolders);
            if (!hasData()) {
                setHasData(true);
            }
            setItemDataHolders(holders, result);
            getRecyclerView().scrollToPosition(playerData.getCurrentItemIndex());
        } else {
            final int oldSize = holders.size();
            holders.clear();
            setHasData(false);
            notifyItemRangeRemoved(0, oldSize);
        }
    }

    @Override
    public void load(){
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        ArrayList<ItemDataHolder> newHolders = playerData.getItems();
        holders.clear();
        holders.addAll(newHolders);
        if (!hasData()) {
            setHasData(true);
        }
        setAddedCount(newHolders.size());
        setItemDataHolders(holders);
        super.load();
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_WRAPPED_AUDIO, new WrappedAudioViewHolder.FabricSheet());
        return arrayMap;
    }
}
