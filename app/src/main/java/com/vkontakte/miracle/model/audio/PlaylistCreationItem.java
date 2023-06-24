package com.vkontakte.miracle.model.audio;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_PLAYLIST_CREATION;

import com.miracle.engine.adapter.holder.ItemDataHolder;

public class PlaylistCreationItem implements ItemDataHolder {
    @Override
    public int getViewHolderType() {
        return TYPE_PLAYLIST_CREATION;
    }
}
