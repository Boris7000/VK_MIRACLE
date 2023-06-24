package com.miracle.engine.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.adapter.MiracleAdapter;

public abstract class MiracleViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private RecyclerView.Adapter<?> adapter;
    private MiracleAdapter miracleAdapter;

    public MiracleViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
    }

    public Context getContext() {
        return context;
    }

    @Nullable
    public MiracleAdapter getBindingMiracleAdapter() {
        return miracleAdapter;
    }

    @CallSuper
    public void onBindToAdapter(){
        RecyclerView.Adapter<?> adapter = getBindingAdapter();
        if(adapter!=this.adapter){
            this.adapter = adapter;
            if (adapter instanceof MiracleAdapter) {
                miracleAdapter = (MiracleAdapter) adapter;
            } else {
                miracleAdapter = null;
            }
        }
    }

    public void bind(ItemDataHolder itemDataHolder){}

}
