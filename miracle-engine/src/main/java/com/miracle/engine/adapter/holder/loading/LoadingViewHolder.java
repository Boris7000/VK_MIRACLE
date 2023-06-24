package com.miracle.engine.adapter.holder.loading;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.R;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;

public class LoadingViewHolder extends MiracleViewHolder {

    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new LoadingViewHolder(inflater.inflate(R.layout.view_loading_item, viewGroup, false));
        }
    }

    public static class FabricHorizontal implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new LoadingViewHolder(inflater.inflate(R.layout.view_loading_item_horizontal, viewGroup, false));
        }
    }

}
