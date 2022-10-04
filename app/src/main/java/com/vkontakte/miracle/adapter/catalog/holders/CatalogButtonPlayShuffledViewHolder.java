package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;

public class CatalogButtonPlayShuffledViewHolder extends CatalogButtonPlayViewHolder {

    public CatalogButtonPlayShuffledViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogButtonPlayShuffledViewHolder(inflater.inflate(R.layout.view_catalog_button_play_shuffled, viewGroup, false));
        }
    }
}
