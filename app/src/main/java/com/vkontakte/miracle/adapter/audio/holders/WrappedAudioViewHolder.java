package com.vkontakte.miracle.adapter.audio.holders;

import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveAdd;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveAddToPlaylist;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveDelete;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveDownload;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveErase;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveFindArtist;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveGoToAlbum;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveGoToArtist;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveOnClick;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolvePlayNext;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.showAudioDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;

public class WrappedAudioViewHolder extends AudioViewHolder{

    private DataItemWrap<?,?> itemWrap;

    public WrappedAudioViewHolder(@NonNull View itemView) {
        this(itemView, R.drawable.audio_placeholder_image_mono_small);
    }

    public WrappedAudioViewHolder(@NonNull View itemView, int placeholderDrawableId) {
        super(itemView, placeholderDrawableId);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        itemWrap = (DataItemWrap<?,?>) itemDataHolder;
        Object item = itemWrap.getItem();
        if(item instanceof AudioItem){
            super.bind((AudioItem) item);
        }
    }

    @Override
    public void onClick(View view) {
        resolveOnClick(itemWrap);
    }

    @Override
    public boolean onLongClick(View view) {
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        showAudioDialog(audioItem, view.getContext(),
                new AudioDialogActionListener() {
                    @Override
                    public void add() {
                        resolveAdd(itemWrap);
                    }
                    @Override
                    public void delete() {
                        resolveDelete(itemWrap, getBindingMiracleAdapter());
                    }
                    @Override
                    public void playNext() {
                        resolvePlayNext(itemWrap);
                    }
                    @Override
                    public void addToPlaylist() {
                        resolveAddToPlaylist(itemWrap, getContext());
                    }
                    @Override
                    public void goToAlbum() {
                        resolveGoToAlbum(itemWrap, getContext());
                    }
                    @Override
                    public void goToArtist() {
                        resolveGoToArtist(itemWrap, getContext());
                    }
                    @Override
                    public void findArtist() {
                        resolveFindArtist(itemWrap, getContext());
                    }
                    @Override
                    public void download() {
                        resolveDownload(itemWrap);
                    }
                    @Override
                    public void erase() {
                        resolveErase(itemWrap, getBindingMiracleAdapter());
                    }
                });
        itemView.getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedAudioViewHolder(
                    inflater.inflate(R.layout.view_audio_item, viewGroup, false));
        }
    }

    public static class FabricTripleStacked implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedAudioViewHolder(
                    inflater.inflate(R.layout.view_audio_item_triple_stacked, viewGroup, false));
        }
    }

    public static class FabricPost implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedAudioViewHolder(
                    inflater.inflate(R.layout.view_audio_item_post, viewGroup, false));
        }
    }

    public static class FabricMessageIn implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedAudioViewHolder(
                    inflater.inflate(R.layout.view_audio_item_message_in, viewGroup, false));
        }
    }

    public static class FabricMessageOut implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedAudioViewHolder(
                    inflater.inflate(R.layout.view_audio_item_message_out, viewGroup, false),
                    R.drawable.audio_placeholder_image_colored_small);
        }
    }
}
