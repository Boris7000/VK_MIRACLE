package com.vkontakte.miracle.model.wall;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_STORIES;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

public class StoriesHolder implements ItemDataHolder {

    @Override
    public int getViewHolderType() {
        return TYPE_STORIES;
    }
}
