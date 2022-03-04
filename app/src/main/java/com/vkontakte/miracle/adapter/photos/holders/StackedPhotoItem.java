package com.vkontakte.miracle.adapter.photos.holders;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.photos.PhotoItem;

import java.util.ArrayList;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_STACKED_PHOTO;

public class StackedPhotoItem implements ItemDataHolder {

    private final ArrayList<PhotoItem> photoItems;
    private final int rowLength;
    public ArrayList<PhotoItem> getPhotoItems() {
        return photoItems;
    }

    public int getRowLength() {
        return rowLength;
    }

    public StackedPhotoItem(ArrayList<PhotoItem> photoItems, int rowLength){
        this.photoItems = photoItems;
        this.rowLength = rowLength;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_STACKED_PHOTO;
    }
}
