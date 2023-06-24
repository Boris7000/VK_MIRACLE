package com.vkontakte.miracle.adapter.wall;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WALL_COUNTER;

import android.util.ArrayMap;

import androidx.recyclerview.widget.DiffUtil;

import com.miracle.engine.adapter.MiracleInstantLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.adapter.wall.holders.WallCounterViewHolder;
import com.vkontakte.miracle.model.wall.fields.Counter;
import com.vkontakte.miracle.model.wall.fields.Counters;

import java.util.ArrayList;

public class CountersAdapter extends MiracleInstantLoadAdapter {

    private Counters counters;

    public CountersAdapter(Counters counters){
        this.counters = counters;
    }

    public void setNewCounters(Counters counters){
        this.counters = counters;
    }

    @Override
    public void onLoading() throws Exception {
        ArrayList<ItemDataHolder> holders = getItemDataHolders();
        ArrayList<ItemDataHolder> newHolders = counters.getCounters();

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
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                ItemDataHolder oldItem = holders.get(oldItemPosition);
                ItemDataHolder newItem = newHolders.get(newItemPosition);
                if (oldItem instanceof Counter && newItem instanceof Counter) {
                    Counter oldCounter = (Counter) oldItem;
                    Counter newCounter = (Counter) newItem;
                    return oldCounter.equalsContent(newCounter);
                }
                return false;
            }
        });
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_WALL_COUNTER, new WallCounterViewHolder.Fabric());
        return arrayMap;
    }


}
