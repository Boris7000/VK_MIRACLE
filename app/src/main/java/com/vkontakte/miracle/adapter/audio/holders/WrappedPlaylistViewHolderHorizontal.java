package com.vkontakte.miracle.adapter.audio.holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.PlaylistDialog;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.executors.audio.DeletePlaylist;
import com.vkontakte.miracle.executors.audio.FollowPlaylist;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.service.player.PlayerServiceController;

public class WrappedPlaylistViewHolderHorizontal extends PlaylistViewHolderHorizontal{

    private DataItemWrap<?,?> itemWrap;

    public WrappedPlaylistViewHolderHorizontal(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(view -> NavigationUtil.goToPlaylist(itemWrap, getContext()));
        itemView.setOnLongClickListener(view -> {
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
                    PlayerServiceController.get().loadAndSetPlayNext(playlistItem);
                }

                @Override
                public void goToArtist() {
                    NavigationUtil.goToArtist(playlistItem, getContext());
                }

                @Override
                public void goToOwner() {
                    NavigationUtil.goToOwner(playlistItem, getContext());
                }
            });
            itemView.getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        });
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        itemWrap = (DataItemWrap<?,?>) itemDataHolder;
        Object item = itemWrap.getItem();
        if(item instanceof PlaylistItem){
            super.bind((PlaylistItem) item);
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedPlaylistViewHolderHorizontal(inflater.inflate(R.layout.view_playlist_item_horizontal, viewGroup, false));
        }
    }

    public static void resolveFollow(DataItemWrap<?,?> itemWrap){
        PlaylistItem playlistItem = (PlaylistItem) itemWrap.getItem();
        new FollowPlaylist(playlistItem).start();
    }

    public static void resolveDelete(DataItemWrap<?,?> itemWrap){
        PlaylistItem playlistItem = (PlaylistItem) itemWrap.getItem();
        new DeletePlaylist(playlistItem).start();
    }

    public static void showPlaylistDialog(PlaylistItem playlistItem, Context context,
                                          PlaylistDialogActionListener listener){
        PlaylistDialog playlistDialog = new PlaylistDialog(context, playlistItem);
        playlistDialog.setDialogActionListener(listener);
        playlistDialog.show(context);
    }

}
