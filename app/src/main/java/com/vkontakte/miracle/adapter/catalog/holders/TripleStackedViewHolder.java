package com.vkontakte.miracle.adapter.catalog.holders;

import static com.vkontakte.miracle.engine.util.AdapterUtil.getVerticalLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.catalog.TripleStackedAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleNestedLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.catalog.other.CatalogTripleStack;

public class TripleStackedViewHolder extends MiracleViewHolder {

    private final RecyclerView recyclerView;

    public TripleStackedViewHolder(@NonNull View itemView, ViewGroup viewGroup) {
        super(itemView);
        recyclerView = ((RecyclerView)itemView);
        recyclerView.setLayoutManager(getVerticalLayoutManager(getMiracleActivity()));

        RecyclerView.Adapter<?> adapter = ((RecyclerView)viewGroup).getAdapter();
        if (adapter instanceof MiracleAdapter) {
            MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
            recyclerView.setRecycledViewPool(miracleAdapter.getRecycledViewPool());
        }
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        CatalogTripleStack catalogTripleStack = (CatalogTripleStack) itemDataHolder;
        TripleStackedAdapter tripleStackedAdapter
                = new TripleStackedAdapter(catalogTripleStack.getItemDataHolders());
        tripleStackedAdapter.iniFromActivity(getMiracleActivity());
        tripleStackedAdapter.setRecyclerView(recyclerView);
        tripleStackedAdapter.ini();
        if(recyclerView.getAdapter()!=null&&recyclerView.getAdapter()instanceof MiracleNestedLoadableAdapter){
            ((MiracleNestedLoadableAdapter)recyclerView.getAdapter()).setDetached(true);
        }
        recyclerView.setAdapter(tripleStackedAdapter);
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new TripleStackedViewHolder(
                    inflater.inflate(R.layout.view_triple_stacked_item,
                            viewGroup, false),viewGroup);
        }
    }

}
