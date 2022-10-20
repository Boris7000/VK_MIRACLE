package com.vkontakte.miracle.engine.view.photoGridView;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.photos.fields.Size;

public class PhotoGridItem implements ItemDataHolder {
    public MediaItem mediaItem;
    public PhotoGridPosition gridPosition;
    public Size temp;

    public MediaItem getMediaItem() {
        return mediaItem;
    }

    public PhotoGridPosition getGridPosition() {
        return gridPosition;
    }

    @Override
    public int getViewHolderType() {
        return mediaItem.getViewHolderType();
    }
}
