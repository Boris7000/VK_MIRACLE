package com.vkontakte.miracle.adapter.audio.holders;

import android.view.View;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.view.AudioItemView;

public class AudioViewHolder extends MiracleViewHolder {

    private final AudioItemView audioItemView;

    public AudioViewHolder(@NonNull View itemView) {
        super(itemView);
        audioItemView = (AudioItemView) itemView;
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        AudioItem audioItem = (AudioItem) itemDataHolder;
        audioItemView.setValues(audioItem);
        super.bind(audioItem);
    }

}
