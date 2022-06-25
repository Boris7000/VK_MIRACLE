package com.vkontakte.miracle.engine.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;

public abstract class MiracleViewHolder extends RecyclerView.ViewHolder {

    private MiracleActivity miracleActivity;
    private MiracleAdapter miracleAdapter;

    public MiracleViewHolder(@NonNull View itemView) {
        super(itemView);
        Context context = itemView.getContext();
        if(context instanceof MiracleActivity) {
            miracleActivity = (MiracleActivity) context;
        }
    }

    public MiracleActivity getMiracleActivity() {
        return miracleActivity;
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
