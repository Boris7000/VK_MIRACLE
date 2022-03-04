package com.vkontakte.miracle.adapter.audio;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_AUDIO;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_LOADING;

import android.util.ArrayMap;

import com.vkontakte.miracle.adapter.audio.holders.AudioViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.loading.LoadingViewHolder;
import com.vkontakte.miracle.player.AudioPlayerData;

import java.util.ArrayList;

public class PlayingAdapter extends MiracleLoadableAdapter {

    private AudioPlayerData playerData;

    public PlayingAdapter(AudioPlayerData playerData){
        this.playerData=playerData;
        setFinallyLoaded(true);
    }

    public void setItemDataHolders(AudioPlayerData playerData){

        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        if(this.playerData.equals(playerData)){
            if(holders.equals(playerData.getItems())){
                return;
            }
        }
        this.playerData=playerData;
        int previous = holders.size();
        holders.clear();
        holders.addAll(playerData.getItems());
        setAddedCount(holders.size() - previous);
        if(getAddedCount()!=0){
            notifyData();
        }
        notifyItemRangeChanged(0,previous);
    }

    @Override
    public void onLoading() throws Exception {
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        holders.clear();
        holders.addAll(playerData.getItems());
        setAddedCount(holders.size());
        onComplete();
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_AUDIO, new AudioViewHolder.FabricSheet());
        return arrayMap;
    }
}
