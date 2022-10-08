package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;

public class CatalogButtonOpenSectionViewHolder extends MiracleViewHolder {

    private CatalogAction catalogAction;

    public CatalogButtonOpenSectionViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(view -> NavigationUtil.goToCatalogSection(catalogAction, getContext()));
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        catalogAction = (CatalogAction) itemDataHolder;
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogButtonOpenSectionViewHolder(inflater.inflate(R.layout.view_catalog_button_open_section, viewGroup, false));
        }
    }
}
