package com.vkontakte.miracle.adapter.catalog.holders.buttons;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogClickableViewHolder;
import com.vkontakte.miracle.engine.util.NavigationUtil;

public class CatalogButtonCreatePlaylistViewHolder extends CatalogClickableViewHolder {

    public CatalogButtonCreatePlaylistViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onClick(View v) {
        NavigationUtil.goPlaylistCreation(v.getContext());
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogButtonCreatePlaylistViewHolder(
                    inflater.inflate(R.layout.view_catalog_button_create_playlist, viewGroup, false));
        }
    }
}
