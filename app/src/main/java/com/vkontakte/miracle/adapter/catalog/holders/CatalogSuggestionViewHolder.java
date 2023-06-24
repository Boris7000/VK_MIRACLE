package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.fragment.search.ISearchFragment;
import com.miracle.widget.ExtendedMaterialButton;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.catalog.CatalogSuggestion;

public class CatalogSuggestionViewHolder extends CatalogClickableViewHolder{

    private final ExtendedMaterialButton button;
    private CatalogSuggestion catalogSuggestion;

    public CatalogSuggestionViewHolder(@NonNull View itemView) {
        super(itemView);
        button = (ExtendedMaterialButton) itemView;
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        catalogSuggestion = (CatalogSuggestion) itemDataHolder;
        button.setText(catalogSuggestion.getTitle());
    }

    @Override
    public void onClick(View v) {
        if (getBindingMiracleAdapter() != null) {
            Fragment fragment = getBindingMiracleAdapter().getFragment();
            if(fragment instanceof ISearchFragment){
                ISearchFragment iSearchFragment = (ISearchFragment) fragment;
                iSearchFragment.searchQuery(catalogSuggestion.getTitle());
            }
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogSuggestionViewHolder(
                    inflater.inflate(R.layout.catalog_suggestion, viewGroup, false));
        }
    }

    public static class FabricHorizontal implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new CatalogSuggestionViewHolder(
                    inflater.inflate(R.layout.catalog_suggestion_horizontal, viewGroup, false));
        }
    }
}
