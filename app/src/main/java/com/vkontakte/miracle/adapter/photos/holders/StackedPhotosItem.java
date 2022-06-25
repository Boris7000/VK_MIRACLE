package com.vkontakte.miracle.adapter.photos.holders;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_STACKED_PHOTOS;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

import java.util.ArrayList;

public class StackedPhotosItem implements ItemDataHolder {

    private final ArrayList<ItemDataHolder> photoItems;
    private final int rowLength;
    public ArrayList<ItemDataHolder> getPhotoItems() {
        return photoItems;
    }

    public int getRowLength() {
        return rowLength;
    }

    public StackedPhotosItem(ArrayList<ItemDataHolder> photoItems, int rowLength){
        this.photoItems = photoItems;
        this.rowLength = rowLength;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_STACKED_PHOTOS;
    }
}
