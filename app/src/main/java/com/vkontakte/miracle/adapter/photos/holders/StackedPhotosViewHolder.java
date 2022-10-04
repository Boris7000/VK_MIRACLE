package com.vkontakte.miracle.adapter.photos.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PHOTO;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.MiracleViewRecycler;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoStackedView;

public class StackedPhotosViewHolder extends MiracleViewHolder {

    public StackedPhotosViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        super.bind(itemDataHolder);
        StackedPhotosItem stackedPhotosItem = (StackedPhotosItem) itemDataHolder;

        MiracleViewRecycler miracleViewRecycler =
                getMiracleAdapter().getMiracleViewRecycler(itemDataHolder.getViewHolderType());
        miracleViewRecycler.setMaxRecycledViews(TYPE_PHOTO, 15);

        PhotoStackedView photoStackedView = (PhotoStackedView) itemView;

        photoStackedView.setRecycledViewPool(miracleViewRecycler);

        if(!stackedPhotosItem.getPhotoItems().isEmpty()){
            if(photoStackedView.getVisibility()!=VISIBLE) {
                photoStackedView.setVisibility(VISIBLE);
            }
            photoStackedView.setPhotos(stackedPhotosItem.getPhotoItems(), stackedPhotosItem.getRowLength());
        } else {
            if(photoStackedView.getVisibility()!=GONE){
                photoStackedView.setPhotos(stackedPhotosItem.getPhotoItems(), stackedPhotosItem.getRowLength());
                photoStackedView.setVisibility(GONE);
            }
        }
    }
    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new StackedPhotosViewHolder(inflater.inflate(R.layout.view_stacked_photo_item, viewGroup, false));
        }
    }
}
