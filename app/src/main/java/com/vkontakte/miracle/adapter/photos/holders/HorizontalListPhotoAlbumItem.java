package com.vkontakte.miracle.adapter.photos.holders;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;
import java.util.ArrayList;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PHOTO_ALBUM_HORIZONTAL_LIST;

public class HorizontalListPhotoAlbumItem implements ItemDataHolder {

    private final int count;
    private final ArrayList<PhotoAlbumItem> photoAlbumItems;

    public ArrayList<PhotoAlbumItem> getPhotoAlbumItems() {
        return photoAlbumItems;
    }

    public int getCount() {
        return count;
    }

    public HorizontalListPhotoAlbumItem(ArrayList<PhotoAlbumItem> photoAlbumItems, int count){
        this.photoAlbumItems = photoAlbumItems;
        this.count = count;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_PHOTO_ALBUM_HORIZONTAL_LIST;
    }
}
