package com.vkontakte.miracle.model.photos;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_STACKED_PHOTOS;

import com.miracle.engine.adapter.holder.ItemDataHolder;

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
