package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.catalog.CatalogLink;

public class CatalogLinkViewHolder extends CatalogLinkViewHolderHorizontal {

    private final TextView subtitle;

    public CatalogLinkViewHolder(@NonNull View itemView) {
        super(itemView);
        subtitle = itemView.findViewById(R.id.subtitle);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        super.bind(itemDataHolder);

        CatalogLink catalogLink = (CatalogLink) itemDataHolder;

        subtitle.setText(catalogLink.getSubtitle());
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogLinkViewHolder(inflater.inflate(R.layout.catalog_link_vertical, viewGroup, false));
        }
    }

}
