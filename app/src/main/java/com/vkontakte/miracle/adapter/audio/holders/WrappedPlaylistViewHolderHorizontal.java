package com.vkontakte.miracle.adapter.audio.holders;

import static com.vkontakte.miracle.adapter.audio.holders.actions.PlaylistItemActions.resolveDelete;
import static com.vkontakte.miracle.adapter.audio.holders.actions.PlaylistItemActions.resolveFollow;
import static com.vkontakte.miracle.adapter.audio.holders.actions.PlaylistItemActions.resolveGoToArtist;
import static com.vkontakte.miracle.adapter.audio.holders.actions.PlaylistItemActions.resolveGoToOwner;
import static com.vkontakte.miracle.adapter.audio.holders.actions.PlaylistItemActions.resolveOnClick;
import static com.vkontakte.miracle.adapter.audio.holders.actions.PlaylistItemActions.resolvePlayNext;
import static com.vkontakte.miracle.adapter.audio.holders.actions.PlaylistItemActions.showPlaylistDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.PlaylistItem;

public class WrappedPlaylistViewHolderHorizontal extends PlaylistViewHolderHorizontal{

    private DataItemWrap<?,?> itemWrap;

    public WrappedPlaylistViewHolderHorizontal(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        itemWrap = (DataItemWrap<?,?>) itemDataHolder;
        Object item = itemWrap.getItem();
        if(item instanceof PlaylistItem){
            super.bind((PlaylistItem) item);
        }
    }

    @Override
    public void onClick(View view) {
        resolveOnClick(itemWrap, getContext());
    }

    @Override
    public boolean onLongClick(View view) {
        PlaylistItem playlistItem = (PlaylistItem) itemWrap.getItem();
        showPlaylistDialog(playlistItem, view.getContext(),
                new PlaylistDialogActionListener() {
                    @Override
                    public void follow() {
                        resolveFollow(itemWrap);
                    }
                    @Override
                    public void delete() {
                        resolveDelete(itemWrap);
                    }
                    @Override
                    public void playNext() {
                        resolvePlayNext(itemWrap);
                    }
                    @Override
                    public void goToArtist() {
                        resolveGoToArtist(itemWrap, getContext());
                    }
                    @Override
                    public void goToOwner() {
                        resolveGoToOwner(itemWrap, getContext());
                    }
                });
        itemView.getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedPlaylistViewHolderHorizontal(
                    inflater.inflate(R.layout.view_playlist_item_horizontal, viewGroup, false));
        }
    }

}
