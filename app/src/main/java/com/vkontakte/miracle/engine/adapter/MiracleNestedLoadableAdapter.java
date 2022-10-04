package com.vkontakte.miracle.engine.adapter;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.getHorizontalFabrics;

import android.util.ArrayMap;

import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;

public abstract class MiracleNestedLoadableAdapter extends MiracleAsyncLoadAdapter {
    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap(){
        return getHorizontalFabrics();
    }
}
