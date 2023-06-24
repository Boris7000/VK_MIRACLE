package com.vkontakte.miracle.adapter.messages.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_PHOTO;

import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.miracle.engine.adapter.MiracleAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.recycler.MiracleViewRecycler;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.view.RecycleListView;
import com.vkontakte.miracle.engine.view.photoGridView.PhotoGridView;
import com.vkontakte.miracle.engine.view.textView.MiracleTextView;
import com.vkontakte.miracle.model.Attachments;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.messages.MessageItem;

public abstract class MessageViewHolder extends MiracleViewHolder {

    private final MiracleTextView text;
    private final TextView time;
    private PhotoGridView photoGridView;
    private CardView photoGridViewHolder;
    private final ViewStub photosViewStub;
    private RecycleListView audiosView;
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
                    inflatePhotos();
                }
                updatePhotosRecycler(itemDataHolder);
                if(photoGridViewHolder.getVisibility()!=VISIBLE) {
                    photoGridViewHolder.setVisibility(VISIBLE);
                }
                photoGridView.setItems(attachments.getMediaItems());
            } else {
                if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                    photoGridView.clearItems();
                    photoGridViewHolder.setVisibility(GONE);
                }
            }

            if(!attachments.getAudioItems().isEmpty()){
                if(audiosView==null) {
                    inflateAudios();
                }
                updateAudiosRecycler(itemDataHolder);
                if(audiosView.getVisibility()!=VISIBLE) {
                    audiosView.setVisibility(VISIBLE);
                }
                audiosView.setItems(attachments.getAudioItems(), false);
            }else {
                if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                    audiosView.clearItems();
                    audiosView.setVisibility(GONE);
                }
            }

        } else {
            if(photoGridViewHolder!=null&&photoGridViewHolder.getVisibility()!=GONE){
                photoGridView.clearItems();
                photoGridViewHolder.setVisibility(GONE);
            }
            if(audiosView!=null&&audiosView.getVisibility()!=GONE){
                audiosView.clearItems();
                audiosView.setVisibility(GONE);
            }
        }
    }

    public abstract ViewHolderFabric getAudiosFabric();

    public abstract ViewHolderFabric getPhotosFabric();

    public void inflatePhotos(){
        if(photosViewStub!=null) {
            photoGridViewHolder = (CardView) photosViewStub.inflate();
        } else {
            photoGridViewHolder = itemView.findViewById(R.id.photosView);
        }
        photoGridView = (PhotoGridView) photoGridViewHolder.getChildAt(0);
        photoGridView.getViewHolderFabricMap().put(TYPE_WRAPPED_PHOTO, getPhotosFabric());
    }

    public void updatePhotosRecycler(ItemDataHolder itemDataHolder){
        if(photoGridView!=null) {
            MiracleAdapter miracleAdapter = getBindingMiracleAdapter();
            if(miracleAdapter!=null) {
                MiracleViewRecycler miracleViewRecycler =
                        miracleAdapter.getMiracleViewRecycler(itemDataHolder.getViewHolderType());
                if(photoGridView.getViewRecycler()!=miracleViewRecycler) {
                    miracleViewRecycler.setMaxRecycledViews(TYPE_WRAPPED_PHOTO, 15);
                    photoGridView.setViewRecycler(miracleViewRecycler);
                }
            }
        }
    }

    public void inflateAudios(){
        if(photosViewStub!=null) {
            audiosView = (RecycleListView) audiosViewStub.inflate();
        } else {
            audiosView = itemView.findViewById(R.id.audiosView);
        }
        audiosView.getViewHolderFabricMap().put(TYPE_WRAPPED_AUDIO, getAudiosFabric());
    }

    public void updateAudiosRecycler(ItemDataHolder itemDataHolder){
        if(audiosView!=null) {
            MiracleAdapter miracleAdapter = getBindingMiracleAdapter();
            if(miracleAdapter!=null) {
                MiracleViewRecycler miracleViewRecycler =
                        miracleAdapter.getMiracleViewRecycler(itemDataHolder.getViewHolderType());
                if(audiosView.getViewRecycler()!=miracleViewRecycler) {
                    miracleViewRecycler.setMaxRecycledViews(TYPE_WRAPPED_AUDIO, 15);
                    audiosView.setViewRecycler(miracleViewRecycler);
                }
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
