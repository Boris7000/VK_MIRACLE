package com.vkontakte.miracle.adapter.audio.holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.DownloadedAdapter;
import com.vkontakte.miracle.dialog.audio.AudioDialog;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.engine.adapter.MiracleAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.executors.audio.AddAudio;
import com.vkontakte.miracle.executors.audio.DeleteAudio;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.AudioWrapContainer;
import com.vkontakte.miracle.service.downloads.audio.AudioDownloadServiceController;
import com.vkontakte.miracle.service.downloads.audio.AudioEraseServiceController;
import com.vkontakte.miracle.service.player.AudioPlayerData;
import com.vkontakte.miracle.service.player.PlayerServiceController;

import java.util.ArrayList;

public class WrappedAudioViewHolder extends AudioViewHolder{

    private DataItemWrap<?,?> itemWrap;

    public WrappedAudioViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(view -> resolveItemClickListener(itemWrap));
        itemView.setOnLongClickListener(view -> {
            AudioItem audioItem = (AudioItem) itemWrap.getItem();
            showAudioDialog(audioItem, view.getContext(),
                    new AudioDialogActionListener() {
                        @Override
                        public void add() {
                            resolveAdd(itemWrap);
                        }
                        @Override
                        public void delete() {
                            resolveDelete(itemWrap, getMiracleAdapter());
                        }
                        @Override
                        public void playNext() {
                            resolvePlayNext(itemWrap);
                        }
                        @Override
                        public void addToPlaylist() { }
                        @Override
                        public void goToAlbum() {
                            resolveGoToAlbum(itemWrap, getMiracleActivity());
                        }
                        @Override
                        public void goToArtist() {
                            resolveGoToArtist(itemWrap, getMiracleActivity());
                        }

                        @Override
                        public void findArtist() {
                            resolveFindArtist(itemWrap, getMiracleActivity());
                        }

                        @Override
                        public void download() {
                            resolveDownload(itemWrap);
                        }

                        @Override
                        public void erase() {
                            resolveErase(itemWrap, getMiracleAdapter());
                        }
                    });
            itemView.getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        });
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        itemWrap = (DataItemWrap<?,?>) itemDataHolder;
        Object item = itemWrap.getItem();
        if(item instanceof AudioItem){
            super.bind((AudioItem) item);
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedAudioViewHolder(inflater.inflate(R.layout.view_audio_item, viewGroup, false));
        }
    }

    public static class FabricSheet implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedAudioViewHolder(inflater.inflate(R.layout.view_audio_item_sheet, viewGroup, false));
        }
    }

    public static class FabricTripleStacked implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedAudioViewHolder(inflater.inflate(R.layout.view_audio_item_triple_stacked, viewGroup, false));
        }
    }

    public static void resolveAdd(DataItemWrap<?,?> itemWrap){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        new AddAudio(audioItem).start();
    }

    public static void resolveDelete(DataItemWrap<?,?> itemWrap, MiracleAdapter adapter){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        if(adapter!=null) {
            if (audioItem.getOriginalId() == null && audioItem.getOriginalOwnerId() == null) {
                ArrayList<ItemDataHolder> itemDataHolders = adapter.getItemDataHolders();
                int pos = itemDataHolders.indexOf(itemWrap);
                itemDataHolders.remove(itemWrap);
                ((AudioWrapContainer) itemWrap.getHolder()).getAudioItems().remove(itemWrap);
                adapter.notifyItemRemoved(pos);
            }
        }
        new DeleteAudio(audioItem).start();
    }

    public static void resolveDownload(DataItemWrap<?,?> itemWrap){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        AudioDownloadServiceController.get().addDownload(audioItem);
    }

    public static void resolveErase(DataItemWrap<?,?> itemWrap, RecyclerView.Adapter<?> adapter){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        if(adapter!=null) {
            if (adapter instanceof DownloadedAdapter) {
                DownloadedAdapter downloadedAdapter = (DownloadedAdapter) adapter;
                ArrayList<ItemDataHolder> itemDataHolders = downloadedAdapter.getItemDataHolders();
                int pos = itemDataHolders.indexOf(itemWrap);
                itemDataHolders.remove(itemWrap);
                ((AudioWrapContainer) itemWrap.getHolder()).getAudioItems().remove(itemWrap);
                downloadedAdapter.notifyItemRemoved(pos);
            }
        }
        AudioEraseServiceController.get().addErase(audioItem);
    }

    public static void resolvePlayNext(DataItemWrap<?,?> itemWrap){
        PlayerServiceController.get().setPlayNext(new AudioPlayerData(itemWrap));
    }

    public static void resolveItemClickListener(DataItemWrap<?,?> itemWrap){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        if(audioItem.isLicensed()) {
            PlayerServiceController.get().playNewAudio(new AudioPlayerData(itemWrap));
        }
    }

    public static void resolveGoToAlbum(DataItemWrap<?,?> itemWrap, MainActivity mainActivity){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        NavigationUtil.goToAlbum(audioItem, mainActivity);
    }

    public static void resolveGoToArtist(DataItemWrap<?,?> itemWrap, MainActivity mainActivity){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        NavigationUtil.goToArtist(audioItem, mainActivity);
    }

    public static void resolveFindArtist(DataItemWrap<?,?> itemWrap, MainActivity mainActivity){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        NavigationUtil.goToArtistSearch(audioItem.getArtist(), mainActivity);
    }

    public static void showAudioDialog(AudioItem audioItem, Context context,
                                       AudioDialogActionListener listener){
        AudioDialog audioDialog = new AudioDialog(context, audioItem);
        audioDialog.setDialogActionListener(listener);
        audioDialog.show(context);
    }

}
