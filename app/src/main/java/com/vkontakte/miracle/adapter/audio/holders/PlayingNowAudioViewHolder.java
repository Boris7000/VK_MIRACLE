package com.vkontakte.miracle.adapter.audio.holders;

import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveAdd;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveAddToPlaylist;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveDelete;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveDownload;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveErase;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveFindArtist;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveGoToAlbum;
import static com.vkontakte.miracle.adapter.audio.holders.actions.AudioItemActions.resolveGoToArtist;
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
import com.vkontakte.miracle.service.player.AudioPlayerMedia;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;

public class PlayingNowAudioViewHolder extends AudioViewHolder {

    private DataItemWrap<?,?> itemWrap;

    public PlayingNowAudioViewHolder(@NonNull View itemView) {
        super(itemView, R.drawable.audio_placeholder_image_transluent_small);
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
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        if(audioItem.isLicensed()) {
            AudioPlayerMedia audioPlayerMedia = (AudioPlayerMedia) itemWrap.getHolder();
            AudioPlayerServiceController.get().skipTo(audioPlayerMedia.getAudioItems().indexOf(itemWrap));
        }
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
                        AudioPlayerMedia audioPlayerMedia = (AudioPlayerMedia) itemWrap.getHolder();
                        AudioPlayerServiceController.get().setPlayNext(
                                new AudioPlayerMedia(audioPlayerMedia,
                                        audioPlayerMedia.getAudioItems().indexOf(itemWrap)));
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
            return new PlayingNowAudioViewHolder(
                    inflater.inflate(R.layout.view_audio_item_sheet, viewGroup, false));
        }
    }
}
