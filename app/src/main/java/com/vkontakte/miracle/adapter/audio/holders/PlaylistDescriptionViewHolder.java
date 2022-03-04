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
import com.vkontakte.miracle.model.audio.fields.Description;

public class PlaylistDescriptionViewHolder extends MiracleViewHolder {

    private final TextView descriptionText;

    public PlaylistDescriptionViewHolder(@NonNull View itemView) {
        super(itemView);
        descriptionText = itemView.findViewById(R.id.description);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        Description description = (Description) itemDataHolder;
        descriptionText.setText(description.getDescription());
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PlaylistDescriptionViewHolder(inflater.inflate(R.layout.view_playlist_description_item, viewGroup, false));
        }
    }

}
