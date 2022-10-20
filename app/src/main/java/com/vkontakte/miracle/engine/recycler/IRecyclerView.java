package com.vkontakte.miracle.engine.recycler;

public interface IRecyclerView {

    MiracleViewRecycler getRecycledViewPool();

    void setRecycledViewPool(MiracleViewRecycler recycledViewPool);

}
