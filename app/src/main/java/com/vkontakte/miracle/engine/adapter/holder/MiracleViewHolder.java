package com.vkontakte.miracle.engine.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.engine.adapter.MiracleAdapter;

public abstract class MiracleViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private MiracleAdapter miracleAdapter;

    public MiracleViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
    }

    public Context getContext() {
        return context;
    }

    public MiracleAdapter getMiracleAdapter() {
        return miracleAdapter;
    }

    public void bind(ItemDataHolder itemDataHolder){
        if(miracleAdapter==null) {
            RecyclerView.Adapter<?> adapter = getBindingAdapter();
            if (adapter instanceof MiracleAdapter) {
                miracleAdapter = (MiracleAdapter) adapter;
            }
        }
    }


}
