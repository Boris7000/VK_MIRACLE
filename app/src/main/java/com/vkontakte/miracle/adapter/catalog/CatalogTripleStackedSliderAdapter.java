package com.vkontakte.miracle.adapter.catalog;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_TRIPLE_STACKED;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.AdapterUtil.getVerticalLayoutManager;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.MiracleUniversalAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogTripleStack;

import java.util.ArrayList;

public class CatalogTripleStackedSliderAdapter extends MiracleUniversalAdapter {

    public CatalogTripleStackedSliderAdapter(CatalogBlock catalogBlock){
        ArrayList<ItemDataHolder> holders = catalogBlock.getItems();
        ArrayList<ItemDataHolder> newHolders = new ArrayList<>();
        for(int i=0;i<holders.size();){
            CatalogTripleStack catalogTripleStack = new CatalogTripleStack();
            for(int j=0;j<3&&i+j<holders.size();j++){
                catalogTripleStack.addItem(holders.get(i+j));
            }
            i+=catalogTripleStack.getItems().size();
            newHolders.add(catalogTripleStack);
        }
        getItemDataHolders().addAll(newHolders);
        setAddedCount(newHolders.size());
    }

    public void setItemDataHolders(CatalogBlock catalogBlock){
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        ArrayList<ItemDataHolder> catalogBlockItems = catalogBlock.getItems();
        ArrayList<ItemDataHolder> newHolders = new ArrayList<>();
        for(int i=0;i<catalogBlockItems.size();){
            CatalogTripleStack catalogTripleStack = new CatalogTripleStack();
            for(int j=0;j<3&&i+j<catalogBlockItems.size();j++){
                catalogTripleStack.addItem(catalogBlockItems.get(i+j));
            }
            i+=catalogTripleStack.getItems().size();
            newHolders.add(catalogTripleStack);
        }
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
                    if (oldItem instanceof CatalogTripleStack && newItem instanceof CatalogTripleStack) {
                        CatalogTripleStack oldCatalogTripleStack = (CatalogTripleStack) oldItem;
                        CatalogTripleStack newCatalogTripleStack = (CatalogTripleStack) newItem;
                        if(oldCatalogTripleStack.getViewHolderType()==newCatalogTripleStack.getViewHolderType()){
                            final int oldSize1 = oldCatalogTripleStack.getSize();
                            final int newSize1 = newCatalogTripleStack.getSize();
                            if(oldSize1==newSize1){
                                ArrayList<ItemDataHolder> oldStack = oldCatalogTripleStack.getItems();
                                ArrayList<ItemDataHolder> newStack = newCatalogTripleStack.getItems();
                                return oldStack.equals(newStack);
                            }
                        }
                    }
                    return false;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ItemDataHolder oldItem = holders.get(oldItemPosition);
                    ItemDataHolder newItem = newHolders.get(newItemPosition);
                    if (oldItem instanceof CatalogTripleStack && newItem instanceof CatalogTripleStack) {
                        CatalogTripleStack oldCatalogTripleStack = (CatalogTripleStack) oldItem;
                        CatalogTripleStack newCatalogTripleStack = (CatalogTripleStack) newItem;
                        if(oldCatalogTripleStack.getViewHolderType()==newCatalogTripleStack.getViewHolderType()){
                            final int oldSize1 = oldCatalogTripleStack.getSize();
                            final int newSize1 = newCatalogTripleStack.getSize();
                            if(oldSize1==newSize1){
                                ArrayList<ItemDataHolder> oldStack = oldCatalogTripleStack.getItems();
                                ArrayList<ItemDataHolder> newStack = newCatalogTripleStack.getItems();
                                for (int i = 0; i<oldSize1; i++){
                                    ItemDataHolder oldItem1 = oldStack.get(i);
                                    ItemDataHolder newItem1 = newStack.get(i);
                                    if (oldItem1 instanceof DataItemWrap && newItem1 instanceof DataItemWrap) {
                                        oldItem1 = (ItemDataHolder) ((DataItemWrap<?, ?>) oldItem1).getItem();
                                        newItem1 = (ItemDataHolder) ((DataItemWrap<?, ?>) newItem1).getItem();
                                    }
                                    if (oldItem1 instanceof AudioItem && newItem1 instanceof AudioItem) {
                                        AudioItem oldAudioItem = (AudioItem) oldItem1;
                                        AudioItem newAudioItem = (AudioItem) newItem1;
                                        if(!oldAudioItem.equalsContent(newAudioItem)){
                                            return false;
                                        }
                                    }
                                }
                                return true;
                            }
                        }
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
        arrayMap.put(TYPE_TRIPLE_STACKED, new TripleStackedViewHolderFabric());
        return arrayMap;
    }

    public class TripleStackedViewHolder extends MiracleViewHolder {

        private final RecyclerView recyclerView;
        private CatalogTripleStack catalogTripleStack;

        public TripleStackedViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = ((RecyclerView)itemView);
            recyclerView.setLayoutManager(getVerticalLayoutManager(itemView.getContext()));
            RecyclerView.RecycledViewPool recycledViewPool =
                    getNestedRecycledViewPool(TYPE_TRIPLE_STACKED);
            recycledViewPool.setMaxRecycledViews(TYPE_WRAPPED_AUDIO, 15);
            recyclerView.setRecycledViewPool(recycledViewPool);
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {
            super.bind(itemDataHolder);
            CatalogTripleStack newCatalogTripleStack = (CatalogTripleStack) itemDataHolder;
            boolean changedType = true;
            if(catalogTripleStack!=null){
                changedType = catalogTripleStack.getViewHolderType()!=newCatalogTripleStack.getViewHolderType();
            }
            catalogTripleStack = newCatalogTripleStack;

            if(changedType){
                if(recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(false);
                }
            }

            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            if(adapter instanceof CatalogTripleStackedAdapter){
                ((CatalogTripleStackedAdapter)recyclerView.getAdapter()).setNewItemDataHolders(catalogTripleStack.getItems());
            } else {
                CatalogTripleStackedAdapter catalogTripleStackedAdapter = new CatalogTripleStackedAdapter(catalogTripleStack);
                catalogTripleStackedAdapter.iniFromActivity(getMiracleActivity());
                catalogTripleStackedAdapter.setRecyclerView(recyclerView);
                catalogTripleStackedAdapter.ini();
                recyclerView.setAdapter(catalogTripleStackedAdapter);
                catalogTripleStackedAdapter.load();
            }

            if(changedType){
                recyclerView.setHasFixedSize(true);
            }
        }
    }

    public class TripleStackedViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new TripleStackedViewHolder(inflater.inflate(R.layout.view_triple_stacked_item,
                    viewGroup, false));
        }
    }

}
