package com.vkontakte.miracle.adapter.catalog.holders.buttons;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogClickableViewHolder;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;
import com.vkontakte.miracle.service.player.loader.CatalogBlockLoader;

public class CatalogButtonPlayViewHolder extends CatalogClickableViewHolder {

    private CatalogAction catalogAction;

    public CatalogButtonPlayViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        catalogAction = (CatalogAction) itemDataHolder;
    }

    @Override
    public void onClick(View v) {
        AudioPlayerServiceController.get().
                playNewAudio(new CatalogBlockLoader(catalogAction.getBlockId()));
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogButtonPlayViewHolder(
                    inflater.inflate(R.layout.view_catalog_button_play, viewGroup, false));
        }
    }
}
