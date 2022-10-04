package com.vkontakte.miracle.model.audio;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_BUTTON_PLAY_SHUFFLED;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

public class PlaylistShuffleItem implements ItemDataHolder {

    private final PlaylistItem playlistItem;

    public PlaylistItem getPlaylistItem() {
        return playlistItem;
    }

    public PlaylistShuffleItem(PlaylistItem playlistItem) {
        this.playlistItem = playlistItem;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_BUTTON_PLAY_SHUFFLED;
    }
}
