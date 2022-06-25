package com.vkontakte.miracle.adapter.messages.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PHOTO_ITEM;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;

import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.MiracleViewRecycler;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.view.AudioListView;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridView;
import com.vkontakte.miracle.engine.view.textView.MiracleTextView;
import com.vkontakte.miracle.model.Attachments;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.messages.MessageItem;

import java.util.ArrayList;

public class MessageViewHolder extends MiracleViewHolder {

    private final MiracleTextView text;
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
        super.bind(itemDataHolder);
        messageItem = (MessageItem) itemDataHolder;

        time.setText(messageItem.getTime(), TextView.BufferType.SPANNABLE);

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
                    MiracleViewRecycler miracleViewRecycler =
                            getMiracleAdapter().getMiracleViewRecycler(itemDataHolder.getViewHolderType());
                    miracleViewRecycler.setMaxRecycledViews(TYPE_PHOTO_ITEM, 15);
                    photoGridView.setRecycledViewPool(miracleViewRecycler);
                }
                if(photoGridViewHolder.getVisibility()!=VISIBLE) {
                    photoGridViewHolder.setVisibility(VISIBLE);
                }
                photoGridView.setPhotos(attachments.getMediaItems());
            } else {
                if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                    photoGridView.setPhotos(attachments.getMediaItems());
                    photoGridViewHolder.setVisibility(GONE);
                }
            }

            if(!attachments.getAudioItems().isEmpty()){
                if(audiosView==null) {
                    if(photosViewStub!=null) {
                        audiosView = (AudioListView) audiosViewStub.inflate();
                    } else {
                        audiosView = itemView.findViewById(R.id.audiosView);
                    }
                    MiracleViewRecycler miracleViewRecycler =
                            getMiracleAdapter().getMiracleViewRecycler(itemDataHolder.getViewHolderType());
                    miracleViewRecycler.setMaxRecycledViews(TYPE_WRAPPED_AUDIO, 15);
                    audiosView.setRecycledViewPool(miracleViewRecycler);
                }

                if(audiosView.getVisibility()!=VISIBLE) {
                    audiosView.setVisibility(VISIBLE);
                }
                audiosView.setItems(attachments.getAudioItems());
            }else {
                if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                    audiosView.setItems(attachments.getAudioItems());
                    audiosView.setVisibility(GONE);
                }
            }

        } else {
            if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                photoGridView.setPhotos(new ArrayList<>());
                photoGridViewHolder.setVisibility(GONE);
            }
            if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                audiosView.setItems(new ArrayList<>());
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
