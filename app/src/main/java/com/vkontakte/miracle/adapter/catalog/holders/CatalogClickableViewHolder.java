package com.vkontakte.miracle.adapter.catalog.holders;

import android.view.View;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.MiracleViewHolder;

public class CatalogClickableViewHolder extends MiracleViewHolder
        implements View.OnClickListener{

    public CatalogClickableViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {}

}
