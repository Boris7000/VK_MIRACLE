package com.vkontakte.miracle.adapter.audio;

import static com.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;
import static com.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_LOADING;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_AUDIO;

import android.util.ArrayMap;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.adapter.MiracleInstantLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.miracle.engine.adapter.holder.loading.LoadingViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.PlayingNowAudioViewHolder;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.service.player.AudioPlayerMedia;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;

import java.util.ArrayList;

public class PlayingNowAdapter extends MiracleInstantLoadAdapter {

    private AudioPlayerMedia audioPlayerMedia;

    public PlayingNowAdapter(AudioPlayerMedia audioPlayerMedia){
        this.audioPlayerMedia=audioPlayerMedia;
    }

    public void setNewAudioPlayerData(AudioPlayerMedia audioPlayerMedia){
        this.audioPlayerMedia=audioPlayerMedia;
    }

    @Override
    public void onLoading(){
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        ArrayList<ItemDataHolder> newHolders = audioPlayerMedia.getAudioItems();

        if(holders.isEmpty()){
            holders.addAll(newHolders);
            setAddedCount(newHolders.size());
        } else {
            if(newHolders.isEmpty()) {
                int oldSize = holders.size();
                holders.clear();
                setAddedCount(-oldSize);
            } else {
                DiffUtil.DiffResult diffResult = calculateDifference(holders, newHolders);
                holders.clear();
                holders.addAll(newHolders);
                setDiffResult(diffResult);
            }
        }
        setFinallyLoaded(true);
    }

    private DiffUtil.DiffResult calculateDifference(ArrayList<ItemDataHolder> holders,
                                                    ArrayList<ItemDataHolder> newHolders){
        final int oldSize = holders.size();
        final int newSize = newHolders.size();
        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
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
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_WRAPPED_AUDIO, new PlayingNowAudioViewHolder.Fabric());
        return arrayMap;
    }


    @CallSuper
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int red = Math.min(6, getItemCount());
        if (position>=getItemCount()-red && attached() && !prohibitScrollLoad()) {
            AudioPlayerServiceController.get().loadMoreAudio();
        }
    }
}
