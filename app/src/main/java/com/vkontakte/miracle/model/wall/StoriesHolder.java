package com.vkontakte.miracle.model.wall;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_STORIES;

import com.miracle.engine.adapter.holder.ItemDataHolder;

public class StoriesHolder implements ItemDataHolder {

    @Override
    public int getViewHolderType() {
        return TYPE_STORIES;
    }
}
