package com.vkontakte.miracle.adapter.audio.holders;

import static com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolderHorizontal.resolveDelete;
import static com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolderHorizontal.resolveFollow;
import static com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolderHorizontal.resolveItemClickListener;
import static com.vkontakte.miracle.adapter.audio.holders.WrappedPlaylistViewHolderHorizontal.showPlaylistDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.AudioDialog;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.service.player.PlayerServiceController;

public class WrappedPlaylistViewHolder extends PlaylistViewHolder{

    private DataItemWrap<?,?> itemWrap;

    public WrappedPlaylistViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(view -> resolveItemClickListener(itemWrap, getMiracleActivity()));
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
                            NavigationUtil.goToArtist(playlistItem, getMiracleActivity());
                        }

                        @Override
                        public void goToOwner() {
                            NavigationUtil.goToOwner(playlistItem, getMiracleActivity());
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
            return new WrappedPlaylistViewHolder(inflater.inflate(R.layout.view_playlist_item_vertical, viewGroup, false));
        }
    }

}
