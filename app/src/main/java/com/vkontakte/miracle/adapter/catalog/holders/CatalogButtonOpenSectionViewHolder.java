package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogSection;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;

public class CatalogButtonOpenSectionViewHolder extends MiracleViewHolder {

    private CatalogAction catalogAction;

    public CatalogButtonOpenSectionViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(view -> resolveItemClickListener(catalogAction, getMiracleActivity()));
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        catalogAction = (CatalogAction) itemDataHolder;
    }

    public static void resolveItemClickListener(CatalogAction catalogAction, MainActivity mainActivity){
        FragmentCatalogSection fragmentCatalogSection = new FragmentCatalogSection();
        fragmentCatalogSection.setCatalogSectionId(catalogAction.getSectionId());
        mainActivity.addFragment(fragmentCatalogSection);
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogButtonOpenSectionViewHolder(inflater.inflate(R.layout.view_catalog_button_open_section, viewGroup, false));
        }
    }
}
