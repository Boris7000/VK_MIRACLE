package com.vkontakte.miracle.adapter.messages.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.messages.MessageItem;

public class MessageOutViewHolder extends MessageViewHolder {

    private final ViewStub readIconStub;
    private ImageView readIcon;

    public MessageOutViewHolder(@NonNull View itemView) {
        super(itemView);
        readIconStub = itemView.findViewById(R.id.readIconStub);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        super.bind(itemDataHolder);

        MessageItem messageItem = (MessageItem) itemDataHolder;

        ConversationItem conversationItem = getConversationItem();

        if(Integer.parseInt(conversationItem.getOutRead())<Integer.parseInt(messageItem.getId())){
            showReadIcon();
        } else {
            hideReadIcon();
        }

    }

    public void showReadIcon(){
        if(readIcon==null) {
            if(readIconStub!=null) {
                readIcon = (ImageView) readIconStub.inflate();
            } else {
                readIcon = itemView.findViewById(R.id.readIcon);
            }
        }
        if(readIcon.getVisibility()!=VISIBLE) {
            readIcon.setVisibility(VISIBLE);
        }
    }

    public void hideReadIcon(){
        if(readIcon!=null&&readIcon.getVisibility()!=GONE){
            readIcon.setVisibility(GONE);
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new MessageOutViewHolder(inflater.inflate(R.layout.view_message_item_out, viewGroup, false));
        }
    }
}
