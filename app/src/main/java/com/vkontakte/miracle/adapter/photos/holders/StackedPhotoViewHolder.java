package com.vkontakte.miracle.adapter.photos.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoStackedView;

public class StackedPhotoViewHolder extends MiracleViewHolder {
    public StackedPhotoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        StackedPhotoItem stackedPhotoItem = (StackedPhotoItem) itemDataHolder;
        ((PhotoStackedView)itemView).setPhotos(stackedPhotoItem.getPhotoItems(),stackedPhotoItem.getRowLength());
    }
    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new StackedPhotoViewHolder(inflater.inflate(R.layout.view_stacked_photo_item, viewGroup, false));
        }
    }
}
