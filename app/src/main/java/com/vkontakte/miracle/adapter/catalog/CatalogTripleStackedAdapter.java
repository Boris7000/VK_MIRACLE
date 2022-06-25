package com.vkontakte.miracle.adapter.catalog;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;

import android.util.ArrayMap;

import androidx.recyclerview.widget.DiffUtil;

import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleUniversalAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.catalog.CatalogTripleStack;

import java.util.ArrayList;

public class CatalogTripleStackedAdapter extends MiracleUniversalAdapter {

    public CatalogTripleStackedAdapter(ArrayList<ItemDataHolder> holders){
        getItemDataHolders().addAll(holders);
        setAddedCount(holders.size());
    }

    public CatalogTripleStackedAdapter(CatalogTripleStack catalogTripleStack){
        ArrayList<ItemDataHolder> holders = catalogTripleStack.getItems();
        getItemDataHolders().addAll(holders);
        setAddedCount(holders.size());
    }


    public void setNewItemDataHolders(ArrayList<ItemDataHolder> newHolders){
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
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
            getRecyclerView().scrollToPosition(0);
        } else {
            final int oldSize = holders.size();
            holders.clear();
            setHasData(false);
            notifyItemRangeRemoved(0, oldSize);
        }
    }

    @Override
    public void load(){
        super.load();
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_WRAPPED_AUDIO, new WrappedAudioViewHolder.FabricTripleStacked());
        return arrayMap;
    }
}
