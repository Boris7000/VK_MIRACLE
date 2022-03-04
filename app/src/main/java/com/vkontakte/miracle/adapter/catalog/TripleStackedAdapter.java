package com.vkontakte.miracle.adapter.catalog;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_AUDIO;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_GROUP;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_LOADING;

import android.util.ArrayMap;

import com.vkontakte.miracle.adapter.audio.holders.AudioViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.GroupViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleNestedLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.loading.LoadingViewHolder;

import java.util.ArrayList;

public class TripleStackedAdapter extends MiracleNestedLoadableAdapter {

    public TripleStackedAdapter(ArrayList<ItemDataHolder> itemDataHolders){
        if(itemDataHolders.size()>0) {
            ArrayList<ItemDataHolder> holders = getItemDataHolders();

            holders.addAll(itemDataHolders);

            setAddedCount(holders.size());

            setFinallyLoaded(true);

            onComplete();
        }
    }

    @Override
    public void onLoading() throws Exception {
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_GROUP, new GroupViewHolder.Fabric());
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.FabricSlider());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_AUDIO, new AudioViewHolder.Fabric());
        return arrayMap;
    }
}
