package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.R;

public class SeparatorViewHolder extends MiracleViewHolder {

    public SeparatorViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new SeparatorViewHolder(
                    inflater.inflate(R.layout.catalog_separator, viewGroup, false));
        }
    }

}
