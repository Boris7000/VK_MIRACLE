package com.vkontakte.miracle.adapter.photos.holders;

import android.view.View;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridItem;
import com.vkontakte.miracle.fragment.photos.PhotoViewerItem;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.photos.wraps.PhotoItemWC;

import java.util.ArrayList;

public class WrappedPhotoGridItemViewHolder extends PhotoGridItemViewHolder{

    private DataItemWrap<?,?> itemWrap;

    public WrappedPhotoGridItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(view -> resolveItemClickListener(itemWrap));
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        itemWrap = (DataItemWrap<?,?>) itemDataHolder;
        Object item = itemWrap.getItem();
        if(item instanceof PhotoGridItem){
            super.bind((PhotoGridItem) item);
        }
    }

    public static void resolveItemClickListener(DataItemWrap<?,?> itemWrap){
        PhotoGridItem photoGridItem = (PhotoGridItem) itemWrap.getItem();
        PhotoItemWC photoItemWC = (PhotoItemWC) itemWrap.getHolder();
        ArrayList<PhotoViewerItem> photoViewerItems = new ArrayList<>();
    }

}
