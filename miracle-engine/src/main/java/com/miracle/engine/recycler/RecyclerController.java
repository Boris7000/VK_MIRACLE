package com.miracle.engine.recycler;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.adapter.holder.error.ErrorViewHolder;

import java.util.ArrayList;

public class RecyclerController {

    private final LayoutInflater inflater;

    private final ArrayList<RecyclerView.ViewHolder> buffer = new ArrayList<>();

    private final ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap = new ArrayMap<>();

    private MiracleViewRecycler recycledViewPool = new MiracleViewRecycler();

    public RecyclerController(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public ArrayList<RecyclerView.ViewHolder> getBuffer() {
        return buffer;
    }

    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        return viewHolderFabricMap;
    }

    public MiracleViewRecycler getRecycledViewPool() {
        return recycledViewPool;
    }

    public void setRecycledViewPool(MiracleViewRecycler recycledViewPool) {
        this.recycledViewPool = recycledViewPool;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolderFabric viewHolderFabric = viewHolderFabricMap.get(viewType);
        if(viewHolderFabric !=null){
            return viewHolderFabric.create(inflater, parent);
        }else {
            return new ErrorViewHolder.Fabric().create(inflater, parent);
        }
    }

    public void loadAllViewsToPool(ViewGroup viewGroup, ArrayList<ItemDataHolder> oldItems){
        int k = oldItems.size();
        for (int i = 0; i < k; i++) {
            RecyclerView.ViewHolder viewHolder = buffer.get(0);
            buffer.remove(viewHolder);
            viewGroup.removeView(viewHolder.itemView);
            recycledViewPool.putRecycledView(viewHolder, oldItems.get(0).getViewHolderType());
        }
    }

    public void resolveSingleTypeItems(ViewGroup viewGroup, ArrayList<ItemDataHolder> newItems,
                                              ArrayList<ItemDataHolder> oldItems){
        if (newItems.size() != buffer.size()) {
            int k = newItems.size() - buffer.size();
            if(k>0){
                boolean poolIsEmpty = false;
                for (int i = 0; i < k; i++) {
                    RecyclerView.ViewHolder viewHolder = null;
                    ItemDataHolder itemDataHolder = newItems.get(i);
                    if(!poolIsEmpty) {
                        viewHolder = recycledViewPool.getRecycledView(itemDataHolder.getViewHolderType());
                    }
                    if(viewHolder==null){
                        poolIsEmpty = true;
                        viewHolder = onCreateViewHolder(viewGroup, itemDataHolder.getViewHolderType());
                    }
                    buffer.add(viewHolder);
                    viewGroup.addView(viewHolder.itemView);
                }
            } else {
                for (int i = 0; i < -k; i++) {
                    RecyclerView.ViewHolder viewHolder = buffer.get(0);
                    buffer.remove(viewHolder);
                    viewGroup.removeView(viewHolder.itemView);
                    recycledViewPool.putRecycledView(viewHolder, oldItems.get(0).getViewHolderType());
                }
            }
        }
    }

    public void resolveMultiTypeItems(ViewGroup viewGroup, ArrayList<ItemDataHolder> newItems,
                                             ArrayList<ItemDataHolder> oldItems){

        ArrayMap<Integer, Integer> typeRequirements = new ArrayMap<>();

        int k = newItems.size();

        for(int i = 0; i < k; i++){
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

        k = oldItems.size();

        for(int i = 0; i < k; i++){
            Integer type = oldItems.get(i).getViewHolderType();
            Integer count = typeRequirements.get(type);
            RecyclerView.ViewHolder viewHolder = buffer.get(i-removedCount);
            if(count==null||count==0){
                buffer.remove(viewHolder);
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

        k = newItems.size();

        for (int i = 0; i < k; i++){
            ItemDataHolder itemDataHolder =  newItems.get(i);
            Integer type = itemDataHolder.getViewHolderType();
            ArrayList<RecyclerView.ViewHolder> viewHolders = cachedViewHolders.get(type);
            if (viewHolders != null && !viewHolders.isEmpty()){
                RecyclerView.ViewHolder viewHolder = viewHolders.get(0);
                viewHolders.remove(viewHolder);
                int oldPosition = buffer.indexOf(viewHolder);
                if(oldPosition!=i){
                    buffer.remove(viewHolder);
                    buffer.add(i, viewHolder);
                    viewGroup.removeView(viewHolder.itemView);
                    viewGroup.addView(viewHolder.itemView, i);
                }
            } else {
                RecyclerView.ViewHolder viewHolder =
                        recycledViewPool.getRecycledView(itemDataHolder.getViewHolderType());
                if(viewHolder==null){
                    viewHolder = onCreateViewHolder(viewGroup, itemDataHolder.getViewHolderType());
                }
                buffer.add(i, viewHolder);
                viewGroup.addView(viewHolder.itemView, i);
            }
        }
    }
}
