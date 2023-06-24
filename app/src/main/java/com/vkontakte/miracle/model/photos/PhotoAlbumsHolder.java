package com.vkontakte.miracle.model.photos;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_PHOTO_ALBUM_HORIZONTAL_LIST;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.photos.PhotoAlbumItem;

import java.util.ArrayList;

public class PhotoAlbumsHolder implements ItemDataHolder {

    private final int count;
    private final ArrayList<PhotoAlbumItem> photoAlbumItems;

    public ArrayList<PhotoAlbumItem> getPhotoAlbumItems() {
        return photoAlbumItems;
    }

    public int getCount() {
        return count;
    }

    public PhotoAlbumsHolder(ArrayList<PhotoAlbumItem> photoAlbumItems, int count){
        this.photoAlbumItems = photoAlbumItems;
        this.count = count;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_PHOTO_ALBUM_HORIZONTAL_LIST;
    }
}
