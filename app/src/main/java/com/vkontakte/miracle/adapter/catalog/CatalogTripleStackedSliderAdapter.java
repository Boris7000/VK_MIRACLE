package com.vkontakte.miracle.adapter.catalog;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_CATALOG_LINK;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_AUDIO;

import android.util.ArrayMap;

import androidx.recyclerview.widget.DiffUtil;

import com.miracle.engine.adapter.MiracleNestedInstantLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogLinkViewHolder;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.catalog.CatalogBlock;

import java.util.ArrayList;

public class CatalogTripleStackedSliderAdapter extends MiracleNestedInstantLoadAdapter {

    private CatalogBlock catalogBlock;

    public CatalogTripleStackedSliderAdapter(CatalogBlock catalogBlock){
        this.catalogBlock = catalogBlock;
    }

    public void setNewCatalogBlock(CatalogBlock catalogBlock){
        this.catalogBlock = catalogBlock;
    }

    @Override
    public void onLoading() throws Exception {

        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        ArrayList<ItemDataHolder> newHolders = catalogBlock.getItems();
        if(holders.isEmpty()){
            holders.addAll(newHolders);
            setAddedCount(newHolders.size());
        } else {
            if(newHolders.isEmpty()){
                int oldSize = holders.size();
                holders.clear();
                setAddedCount(-oldSize);
                setFinallyLoaded(true);
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
        arrayMap.put(TYPE_WRAPPED_AUDIO, new WrappedAudioViewHolder.FabricTripleStacked());
        arrayMap.put(TYPE_CATALOG_LINK, new CatalogLinkViewHolder.FabricTripleStacked());
        return arrayMap;
    }

}
