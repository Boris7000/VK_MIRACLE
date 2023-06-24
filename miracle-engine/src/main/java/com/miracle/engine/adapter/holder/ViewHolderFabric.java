package com.miracle.engine.adapter.holder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public interface ViewHolderFabric {
    RecyclerView.ViewHolder create(LayoutInflater inflater, ViewGroup viewGroup);
}
