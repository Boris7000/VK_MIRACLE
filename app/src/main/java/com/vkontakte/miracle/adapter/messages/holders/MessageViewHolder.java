package com.vkontakte.miracle.adapter.messages.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.view.AudioListView;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridView;
import com.vkontakte.miracle.model.Attachments;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.messages.MessageItem;

public class MessageViewHolder extends MiracleViewHolder {

    private final TextView text;
    private final TextView time;
    private PhotoGridView photoGridView;
    private CardView photoGridViewHolder;
    private final ViewStub photosViewStub;
    private AudioListView audiosView;
    private final ViewStub audiosViewStub;

    private MessageItem messageItem;
    private ConversationItem conversationItem;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        text = itemView.findViewById(R.id.text);
        time = itemView.findViewById(R.id.time);
        photosViewStub = itemView.findViewById(R.id.photosViewStub);
        audiosViewStub = itemView.findViewById(R.id.audiosViewStub);
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

            if(!attachments.getAudios().isEmpty()){
                if(audiosView==null) {
                    if(photosViewStub!=null) {
                        audiosView = (AudioListView) audiosViewStub.inflate();
                    } else {
                        audiosView = itemView.findViewById(R.id.audiosView);
                    }
                }

                if(audiosView.getVisibility()!=VISIBLE) {
                    audiosView.setVisibility(VISIBLE);
                }
                audiosView.setItems(getMiracleActivity(),attachments.getAudios());
            }else {
                if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                    audiosView.setVisibility(GONE);
                }
            }

        } else {
            if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                photoGridViewHolder.setVisibility(GONE);
            }
            if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                audiosView.setVisibility(GONE);
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
