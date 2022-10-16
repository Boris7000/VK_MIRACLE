package com.vkontakte.miracle.model.catalog.wraps;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST_RECOMMENDATION;

import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.ItemDataWrapFabric;
import com.vkontakte.miracle.model.audio.wraps.PlaylistItemWC;
import com.vkontakte.miracle.model.catalog.RecommendedPlaylist;

public class RecommendedPlaylistWF implements ItemDataWrapFabric<RecommendedPlaylist, PlaylistItemWC> {
    @Override
    public DataItemWrap<RecommendedPlaylist, PlaylistItemWC> create(RecommendedPlaylist item, PlaylistItemWC holder) {
        return new DataItemWrap<RecommendedPlaylist, PlaylistItemWC>(item, holder) {
            @Override
            public int getViewHolderType() {
                return TYPE_WRAPPED_PLAYLIST_RECOMMENDATION;
            }
        };
    }
}
