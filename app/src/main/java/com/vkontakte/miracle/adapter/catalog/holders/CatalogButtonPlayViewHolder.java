package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.service.player.PlayerServiceController;

public class CatalogButtonPlayViewHolder extends MiracleViewHolder {

    private CatalogAction catalogAction;

    public CatalogButtonPlayViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(view -> PlayerServiceController.get().loadAndPlayNewAudio(catalogAction));
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        catalogAction = (CatalogAction) itemDataHolder;

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogButtonPlayViewHolder(inflater.inflate(R.layout.view_catalog_button_play, viewGroup, false));
        }
    }
}
