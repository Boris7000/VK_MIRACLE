package com.miracle.engine.adapter;

import static com.miracle.engine.adapter.holder.ViewHolderTypes.getHorizontalFabrics;

import android.util.ArrayMap;

import com.miracle.engine.adapter.holder.ViewHolderFabric;

public abstract class MiracleNestedInstantLoadAdapter extends MiracleInstantLoadAdapter {
    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap(){
        return getHorizontalFabrics();
    }
}
