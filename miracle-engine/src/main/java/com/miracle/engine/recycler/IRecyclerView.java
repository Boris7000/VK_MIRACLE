package com.miracle.engine.recycler;

import android.util.ArrayMap;

import com.miracle.engine.adapter.holder.ViewHolderFabric;

public interface IRecyclerView {

    MiracleViewRecycler getViewRecycler();

    ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap();

    void setViewRecycler(MiracleViewRecycler recycledViewPool);

}
