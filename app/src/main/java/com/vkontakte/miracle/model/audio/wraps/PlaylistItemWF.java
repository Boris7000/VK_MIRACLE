package com.vkontakte.miracle.model.audio.wraps;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST;

import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.ItemDataWrapFabric;
import com.vkontakte.miracle.model.audio.PlaylistItem;

public class PlaylistItemWF implements ItemDataWrapFabric<PlaylistItem, PlaylistItemWC> {
    @Override
    public DataItemWrap<PlaylistItem, PlaylistItemWC> create(PlaylistItem item, PlaylistItemWC holder) {
        return new DataItemWrap<PlaylistItem, PlaylistItemWC>(item, holder) {
            @Override
            public int getViewHolderType() {
                return TYPE_WRAPPED_PLAYLIST;
            }
        };
    }
}
