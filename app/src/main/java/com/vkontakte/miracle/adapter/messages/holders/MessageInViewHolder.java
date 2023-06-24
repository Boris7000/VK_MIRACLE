package com.vkontakte.miracle.adapter.messages.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.adapter.photos.holders.PhotoGridItemViewHolder;

public class MessageInViewHolder extends MessageViewHolder {

    public MessageInViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public ViewHolderFabric getAudiosFabric(){
        return new WrappedAudioViewHolder.FabricMessageIn();
    }

    @Override
    public ViewHolderFabric getPhotosFabric() {
        return new PhotoGridItemViewHolder.FabricMessageIn();
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new MessageInViewHolder(
                    inflater.inflate(R.layout.view_message_item_in, viewGroup, false));
        }
    }
}