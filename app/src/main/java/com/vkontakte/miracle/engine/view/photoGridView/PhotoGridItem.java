package com.vkontakte.miracle.engine.view.photoGridView;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

public class PhotoGridItem implements ItemDataHolder {
    public ItemDataHolder itemDataHolder;
    public PhotoGridPosition gridPosition;
    public ItemDataHolder getItemDataHolder() {
        return itemDataHolder;
    }

    public PhotoGridPosition getGridPosition() {
        return gridPosition;
    }

    @Override
    public int getViewHolderType() {
        return itemDataHolder.getViewHolderType();
    }
}
