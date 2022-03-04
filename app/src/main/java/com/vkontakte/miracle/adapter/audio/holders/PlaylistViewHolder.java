package com.vkontakte.miracle.adapter.audio.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.audio.PlaylistItem;

public class PlaylistViewHolder extends PlaylistHorizontalViewHolder {

    private final TextView subtitle2;

    public PlaylistViewHolder(@NonNull View itemView) {
        super(itemView);
        subtitle2 = itemView.findViewById(R.id.subtitle2);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        super.bind(itemDataHolder);
        PlaylistItem playlistItem = (PlaylistItem) itemDataHolder;

        if(playlistItem.getYear().isEmpty()){
            if(subtitle2.getVisibility()!=View.GONE){
                subtitle2.setVisibility(View.GONE);
            }
        } else {
            if(subtitle2.getVisibility()!=View.VISIBLE){
                subtitle2.setVisibility(View.VISIBLE);
            }
            subtitle2.setText(playlistItem.getYear());
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PlaylistViewHolder(inflater.inflate(R.layout.view_playlist_item_vertical, viewGroup, false));
        }
    }

}
