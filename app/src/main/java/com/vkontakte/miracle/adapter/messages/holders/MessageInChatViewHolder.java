package com.vkontakte.miracle.adapter.messages.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.adapter.photos.holders.PhotoGridItemViewHolder;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.messages.MessageItem;

public class MessageInChatViewHolder extends MessageViewHolder {

    private final TextView title;
    private final ImageView photo;

    public MessageInChatViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        photo = itemView.findViewById(R.id.photo);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        super.bind(itemDataHolder);

        MessageItem messageItem = (MessageItem) itemDataHolder;
        Owner owner = messageItem.getOwner();

        title.setText(owner.getNameWithInitials());

        Picasso.get().cancelRequest(photo);

        if(!owner.getPhoto100().isEmpty()) {
            Picasso.get().load(owner.getPhoto100()).into(photo);
        }
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
            return new MessageInChatViewHolder(
                    inflater.inflate(R.layout.view_message_item_in_chat, viewGroup, false));
        }
    }
}