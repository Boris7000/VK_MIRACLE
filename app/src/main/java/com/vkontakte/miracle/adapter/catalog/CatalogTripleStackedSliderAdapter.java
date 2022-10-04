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
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleInstantLoadAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogTripleStack;

import java.util.ArrayList;

public class CatalogTripleStackedSliderAdapter extends MiracleInstantLoadAdapter {

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
        ArrayList<ItemDataHolder> stacks = catalogBlock.getItems();
        ArrayList<ItemDataHolder> newHolders = new ArrayList<>();
        for(int i=0;i<stacks.size();){
            CatalogTripleStack catalogTripleStack = new CatalogTripleStack();
            for(int j=0;j<3&&i+j<stacks.size();j++){
                catalogTripleStack.addItem(stacks.get(i+j));
            }
            i+=catalogTripleStack.getItems().size();
            newHolders.add(catalogTripleStack);
        }

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
            CatalogTripleStackedAdapter catalogTripleStackedAdapter;
            if(adapter instanceof CatalogTripleStackedAdapter){
                catalogTripleStackedAdapter = ((CatalogTripleStackedAdapter) recyclerView.getAdapter());
                catalogTripleStackedAdapter.iniFromFragment(getMiracleFragment());
                catalogTripleStackedAdapter.setNewItemDataHolders(catalogTripleStack.getItems());
            } else {
                if(adapter instanceof MiracleAdapter){
                    MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
                    miracleAdapter.setRecyclerView(null);
                }
                catalogTripleStackedAdapter = new CatalogTripleStackedAdapter(catalogTripleStack.getItems());
                catalogTripleStackedAdapter.iniFromFragment(getMiracleFragment());
                catalogTripleStackedAdapter.setRecyclerView(recyclerView);
                catalogTripleStackedAdapter.ini();
                recyclerView.setAdapter(catalogTripleStackedAdapter);
            }
            catalogTripleStackedAdapter.load();

            if(changedType){
                if(!recyclerView.hasFixedSize()) {
                    recyclerView.setHasFixedSize(true);
                }
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
