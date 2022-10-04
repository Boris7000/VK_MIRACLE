package com.vkontakte.miracle.adapter.wall.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;

public class StoriesViewHolder extends MiracleViewHolder {
    public StoriesViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new StoriesViewHolder(inflater.inflate(R.layout.view_stories_holder, viewGroup, false));
        }
    }

}
