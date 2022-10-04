package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.button.TextViewButton;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.fragment.IMiracleFragment;
import com.vkontakte.miracle.engine.fragment.search.ISearchFragment;
import com.vkontakte.miracle.model.catalog.CatalogSuggestion;

public class CatalogSuggestionViewHolder extends MiracleViewHolder {

    private final TextViewButton textViewButton;
    private CatalogSuggestion catalogSuggestion;

    public CatalogSuggestionViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewButton = (TextViewButton) itemView;
        itemView.setOnClickListener(view -> {
            IMiracleFragment miracleFragment = getMiracleAdapter().getMiracleFragment();
            if(miracleFragment instanceof ISearchFragment){
                ISearchFragment iSearchFragment = (ISearchFragment) miracleFragment;
                iSearchFragment.setContextQuery(catalogSuggestion.getContext(), catalogSuggestion.getTitle());
            }
        });
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        super.bind(itemDataHolder);
        catalogSuggestion = (CatalogSuggestion) itemDataHolder;
        textViewButton.setText(catalogSuggestion.getTitle());
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogSuggestionViewHolder(inflater.inflate(R.layout.catalog_suggestion, viewGroup, false));
        }
    }

    public static class FabricHorizontal implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogSuggestionViewHolder(inflater.inflate(R.layout.catalog_suggestion_horizontal, viewGroup, false));
        }
    }

}
