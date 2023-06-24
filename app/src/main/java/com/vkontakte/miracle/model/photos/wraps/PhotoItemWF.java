package com.vkontakte.miracle.model.photos.wraps;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_PHOTO;

import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.ItemDataWrapFabric;
import com.vkontakte.miracle.model.photos.PhotoItem;

public class PhotoItemWF implements ItemDataWrapFabric<PhotoItem, PhotoItemWC> {
    @Override
    public DataItemWrap<PhotoItem, PhotoItemWC> create(PhotoItem item, PhotoItemWC holder) {
        return new DataItemWrap<PhotoItem, PhotoItemWC>(item, holder) {
            @Override
            public int getViewHolderType() {
                return TYPE_WRAPPED_PHOTO;
            }
        };
    }
}
