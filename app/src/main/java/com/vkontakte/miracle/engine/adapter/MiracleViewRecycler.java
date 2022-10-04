package com.vkontakte.miracle.engine.adapter;

import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorViewHolder;

import java.util.ArrayList;

public class MiracleViewRecycler {

    static final boolean DEBUG = false;
    private static final int DEFAULT_MAX_SCRAP = 5;

    static class ScrapData {
        final ArrayList<RecyclerView.ViewHolder> mScrapHeap = new ArrayList<>();
        int mMaxScrap = DEFAULT_MAX_SCRAP;
    }

    SparseArray<ScrapData> mScrap = new SparseArray<>();

    public void clear() {
        for (int i = 0; i < mScrap.size(); i++) {
            ScrapData data = mScrap.valueAt(i);
            data.mScrapHeap.clear();
        }
    }

    public void setMaxRecycledViews(int viewType, int max) {
        ScrapData scrapData = getScrapDataForType(viewType);
        scrapData.mMaxScrap = max;
        final ArrayList<RecyclerView.ViewHolder> scrapHeap = scrapData.mScrapHeap;
        while (scrapHeap.size() > max) {
            scrapHeap.remove(scrapHeap.size() - 1);
        }
    }

    public int getRecycledViewCount(int viewType) {
        return getScrapDataForType(viewType).mScrapHeap.size();
    }

    @Nullable
    public RecyclerView.ViewHolder getRecycledView(int viewType) {
        final ScrapData scrapData = mScrap.get(viewType);
        if (scrapData != null && !scrapData.mScrapHeap.isEmpty()) {
            final ArrayList<RecyclerView.ViewHolder> scrapHeap = scrapData.mScrapHeap;
            if(DEBUG) {
                Log.d("foekfoeofek", "type = " + viewType + " count = " + scrapHeap.size());
            }
            return scrapHeap.remove(0);
        }
        return null;
    }

    int size() {
        int count = 0;
        for (int i = 0; i < mScrap.size(); i++) {
            ArrayList<RecyclerView.ViewHolder> viewHolders = mScrap.valueAt(i).mScrapHeap;
            if (viewHolders != null) {
                count += viewHolders.size();
            }
        }
        return count;
    }

    public void putRecycledView(RecyclerView.ViewHolder scrap, int viewType) {
        final ArrayList<RecyclerView.ViewHolder> scrapHeap = getScrapDataForType(viewType).mScrapHeap;
        if (mScrap.get(viewType).mMaxScrap <= scrapHeap.size()) {
            return;
        }
        if (DEBUG && scrapHeap.contains(scrap)) {
            throw new IllegalArgumentException("this scrap item already exists");
        }
        scrapHeap.add(scrap);
        if(DEBUG) {
            Log.d("foekfoeofek", "type = " + viewType + " count = " + scrapHeap.size());
        }
    }

    private ScrapData getScrapDataForType(int viewType) {
        ScrapData scrapData = mScrap.get(viewType);
        if (scrapData == null) {
            scrapData = new ScrapData();
            mScrap.put(viewType, scrapData);
        }
        return scrapData;
    }

    public static RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType,
                                                      ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap,
                                                      LayoutInflater inflater) {
        ViewHolderFabric viewHolderFabric = viewHolderFabricMap.get(viewType);
        if(viewHolderFabric !=null){
            return viewHolderFabric.create(inflater, parent);
        }else {
            return new ErrorViewHolder.Fabric().create(inflater, parent);
        }
    }

    public static void resolveSingleTypeItems(ViewGroup viewGroup, ArrayList<ItemDataHolder> newItems,
                                              ArrayList<ItemDataHolder> oldItems, ArrayList<RecyclerView.ViewHolder> cache,
                                              MiracleViewRecycler recycledViewPool, ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap,
                                              LayoutInflater inflater){
        if (newItems.size() != cache.size()) {
            int k = newItems.size() - cache.size();
            if(k>0){
                boolean poolIsEmpty = false;
                for (int i = 0; i < k; i++) {
                    Log.d("eojfiejgoejgoejmg","3");
                    RecyclerView.ViewHolder viewHolder = null;
                    ItemDataHolder itemDataHolder = newItems.get(i);
                    if(!poolIsEmpty) {
                        viewHolder = recycledViewPool.getRecycledView(itemDataHolder.getViewHolderType());
                    }
                    if(viewHolder==null){
                        poolIsEmpty = true;
                        viewHolder = onCreateViewHolder(viewGroup, itemDataHolder.getViewHolderType(),
                                viewHolderFabricMap,inflater);
                    }
                    cache.add(viewHolder);
                    viewGroup.addView(viewHolder.itemView);
                }
            } else {
                for (int i = 0; i < -k; i++) {
                    RecyclerView.ViewHolder viewHolder = cache.get(0);
                    cache.remove(viewHolder);
                    viewGroup.removeView(viewHolder.itemView);
                    recycledViewPool.putRecycledView(viewHolder, oldItems.get(0).getViewHolderType());
                }
            }
        }
    }

    public static void resolveMultiTypeItems(ViewGroup viewGroup, ArrayList<ItemDataHolder> newItems,
                                              ArrayList<ItemDataHolder> oldItems, ArrayList<RecyclerView.ViewHolder> cache,
                                              MiracleViewRecycler recycledViewPool, ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap,
                                              LayoutInflater inflater){


        ArrayMap<Integer, Integer> typeRequirements = new ArrayMap<>();
        for(int i = 0; i < newItems.size(); i++){
            Integer type = newItems.get(0).getViewHolderType();
            Integer count = typeRequirements.get(type);
            if(count==null){
                typeRequirements.put(type, 1);
            } else {
                typeRequirements.remove(type);
                typeRequirements.put(type,count+1);
            }
        }

        int removedCount = 0;

        ArrayMap<Integer, ArrayList<RecyclerView.ViewHolder>> cachedViewHolders = new ArrayMap<>();

        for(int i = 0; i < oldItems.size(); i++){
            Integer type = oldItems.get(i).getViewHolderType();
            Integer count = typeRequirements.get(type);
            RecyclerView.ViewHolder viewHolder = cache.get(i-removedCount);
            if(count==null||count==0){
                cache.remove(viewHolder);
                viewGroup.removeView(viewHolder.itemView);
                recycledViewPool.putRecycledView(viewHolder, type);
                removedCount++;
            } else {
                ArrayList<RecyclerView.ViewHolder> viewHolders = cachedViewHolders.get(type);
                if (viewHolders == null){
                    cachedViewHolders.put(type, viewHolders = new ArrayList<>());
                }
                viewHolders.add(viewHolder);
                typeRequirements.remove(type);
                typeRequirements.put(type,count-1);
            }
        }

        for (int i = 0; i < newItems.size(); i++){
            ItemDataHolder itemDataHolder =  newItems.get(i);
            Integer type = itemDataHolder.getViewHolderType();
            ArrayList<RecyclerView.ViewHolder> viewHolders = cachedViewHolders.get(type);
            if (viewHolders != null && !viewHolders.isEmpty()){
                RecyclerView.ViewHolder viewHolder = viewHolders.get(0);
                viewHolders.remove(viewHolder);
                int oldPosition = cache.indexOf(viewHolder);
                if(oldPosition!=i){
                    cache.remove(viewHolder);
                    cache.add(i, viewHolder);
                    viewGroup.removeView(viewHolder.itemView);
                    viewGroup.addView(viewHolder.itemView, i);
                }
            } else {
                RecyclerView.ViewHolder viewHolder =
                        recycledViewPool.getRecycledView(itemDataHolder.getViewHolderType());
                if(viewHolder==null){
                    viewHolder = onCreateViewHolder(viewGroup, itemDataHolder.getViewHolderType(),
                            viewHolderFabricMap,inflater);
                }
                cache.add(i, viewHolder);
                viewGroup.addView(viewHolder.itemView, i);
            }
        }
    }
}
