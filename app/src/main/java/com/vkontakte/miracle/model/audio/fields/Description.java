package com.vkontakte.miracle.model.audio.fields;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PLAYLIST_DESCRIPTION;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

public class Description implements ItemDataHolder {
    private final String description;

    public String getDescription() {
        return description;
    }

    public Description(String description){
        this.description = description;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_PLAYLIST_DESCRIPTION;
    }
}
