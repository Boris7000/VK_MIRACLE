package com.vkontakte.miracle.adapter.messages.holders;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridView;
import com.vkontakte.miracle.model.Attachments;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.messages.MessageItem;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MessageViewHolder extends MiracleViewHolder {

    private final TextView text;
    private final TextView time;
    private PhotoGridView photoGridView;
    private CardView photoGridViewHolder;
    private final ViewStub photosViewStub;

    private MessageItem messageItem;
    private ConversationItem conversationItem;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        text = itemView.findViewById(R.id.text);
        time = itemView.findViewById(R.id.time);
        photosViewStub = itemView.findViewById(R.id.photosViewStub);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        messageItem = (MessageItem) itemDataHolder;

        time.setText(messageItem.getTime());

        if(messageItem.getText().isEmpty()){
            text.setText("");
            if(text.getVisibility()!=GONE){
                text.setVisibility(GONE);
            }
        } else {
            text.setText(messageItem.getText());
            if(text.getVisibility()!=VISIBLE){
                text.setVisibility(VISIBLE);
            }
        }

        if(messageItem.getAttachments()!=null){
            Attachments attachments = messageItem.getAttachments();
                if(!attachments.getMediaItems().isEmpty()){
                    if(photoGridView==null) {
                        if(photosViewStub!=null) {
                            photoGridViewHolder = (CardView) photosViewStub.inflate();
                        } else {
                            photoGridViewHolder = itemView.findViewById(R.id.photosView);
                        }
                        photoGridView = (PhotoGridView) photoGridViewHolder.getChildAt(0);
                    }
                    if(photoGridViewHolder.getVisibility()!=VISIBLE) {
                        photoGridViewHolder.setVisibility(VISIBLE);
                    }
                    photoGridView.setPhotos(attachments.getMediaItems());
                } else {
                    if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                        photoGridViewHolder.setVisibility(GONE);
                    }
                }
        } else {
            if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                photoGridViewHolder.setVisibility(GONE);
            }
        }
    }

    public void setConversationItem(ConversationItem conversationItem) {
        this.conversationItem = conversationItem;
    }

    public ConversationItem getConversationItem() {
        return conversationItem;
    }

    public MessageItem getMessageItem() {
        return messageItem;
    }
}
