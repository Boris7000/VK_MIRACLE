package com.vkontakte.miracle.engine.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;

public abstract class MiracleViewHolder extends RecyclerView.ViewHolder {

    private MainActivity mainActivity;
    private MiracleAdapter miracleAdapter;

    public MiracleViewHolder(@NonNull View itemView) {
        super(itemView);
        Context context = itemView.getContext();
        if(context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        }
    }

    public MainActivity getMiracleActivity() {
        return mainActivity;
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
