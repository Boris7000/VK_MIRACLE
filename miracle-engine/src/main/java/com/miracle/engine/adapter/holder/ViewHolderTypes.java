package com.miracle.engine.adapter.holder;

import android.util.ArrayMap;

import com.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.miracle.engine.adapter.holder.loading.LoadingViewHolder;

public class ViewHolderTypes {

    public static final int TYPE_LOADING = -2;
    public static final int TYPE_ERROR = -1;

    public static ArrayMap<Integer, ViewHolderFabric> getVerticalFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        return arrayMap;
    }

    public static ArrayMap<Integer, ViewHolderFabric> getHorizontalFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.FabricHorizontal());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        return arrayMap;
    }


}
