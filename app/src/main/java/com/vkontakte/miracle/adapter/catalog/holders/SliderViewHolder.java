package com.vkontakte.miracle.adapter.catalog.holders;

import static com.vkontakte.miracle.engine.util.AdapterUtil.getHorizontalLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.catalog.CatalogSliderAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;
import com.vkontakte.miracle.engine.adapter.MiracleNestedLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.catalog.CatalogBlock;

public class SliderViewHolder extends MiracleViewHolder {

    private final RecyclerView recyclerView;

    public SliderViewHolder(@NonNull View itemView, ViewGroup viewGroup) {
        super(itemView);
        recyclerView = ((RecyclerView)itemView);
        recyclerView.setLayoutManager(getHorizontalLayoutManager(getMiracleActivity()));

        RecyclerView.Adapter<?> adapter = ((RecyclerView)viewGroup).getAdapter();
        if (adapter instanceof MiracleAdapter) {
            MiracleAdapter miracleAdapter = (MiracleAdapter) adapter;
            recyclerView.setRecycledViewPool(miracleAdapter.getRecycledViewPool());
        }
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        CatalogBlock catalogBlock = (CatalogBlock) itemDataHolder;

        CatalogSliderAdapter catalogSliderAdapter = new CatalogSliderAdapter(catalogBlock);
        catalogSliderAdapter.iniFromActivity(getMiracleActivity());
        catalogSliderAdapter.setRecyclerView(recyclerView);
        catalogSliderAdapter.ini();
        if(recyclerView.getAdapter()!=null&&recyclerView.getAdapter()instanceof MiracleNestedLoadableAdapter){
            ((MiracleNestedLoadableAdapter)recyclerView.getAdapter()).setDetached(true);
        }
        recyclerView.setAdapter(catalogSliderAdapter);
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new SliderViewHolder(inflater.inflate(R.layout.catalog_slider, viewGroup, false),viewGroup);
        }
    }


}
