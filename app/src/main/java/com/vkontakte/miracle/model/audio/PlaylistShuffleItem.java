package com.vkontakte.miracle.model.audio;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_BUTTON_PLAY_SHUFFLED;

import com.miracle.engine.adapter.holder.ItemDataHolder;

public class PlaylistShuffleItem implements ItemDataHolder {

    private final String playlistId;
    private final String ownerId;
    private final String accessKey;

    public PlaylistShuffleItem(String playlistId, String ownerId, String accessKey){
        this.playlistId = playlistId;
        this.ownerId = ownerId;
        this.accessKey = accessKey;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    @Override
    public int getViewHolderType() {
        return TYPE_BUTTON_PLAY_SHUFFLED;
    }

}
