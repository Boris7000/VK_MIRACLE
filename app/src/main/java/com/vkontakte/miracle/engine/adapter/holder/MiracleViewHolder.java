package com.vkontakte.miracle.engine.adapter.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;

public abstract class MiracleViewHolder extends RecyclerView.ViewHolder {

    private final MiracleActivity miracleActivity;
    private final MiracleApp miracleApp;

    public MiracleViewHolder(@NonNull View itemView) {
        super(itemView);
        miracleActivity = (MiracleActivity) itemView.getContext();
        miracleApp = miracleActivity.getMiracleApp();
    }

    public MiracleActivity getMiracleActivity() {
        return miracleActivity;
    }

    public MiracleApp getMiracleApp() {
        return miracleApp;
    }

    public abstract void bind(ItemDataHolder itemDataHolder);

}
