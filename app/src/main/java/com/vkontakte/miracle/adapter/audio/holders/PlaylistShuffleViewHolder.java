package com.vkontakte.miracle.adapter.audio.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.audio.PlaylistShuffleItem;
import com.vkontakte.miracle.service.player.PlayerServiceController;

public class PlaylistShuffleViewHolder extends MiracleViewHolder {

    private PlaylistShuffleItem playlistShuffleItem;

    public PlaylistShuffleViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(view -> PlayerServiceController.get().loadAndPlayNewAudio(playlistShuffleItem));
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        playlistShuffleItem = (PlaylistShuffleItem) itemDataHolder;

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PlaylistShuffleViewHolder(inflater.inflate(R.layout.view_catalog_button_play_shuffled, viewGroup, false));
        }
    }
}
