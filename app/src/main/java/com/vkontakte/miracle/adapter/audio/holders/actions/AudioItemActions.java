package com.vkontakte.miracle.adapter.audio.holders.actions;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.adapter.MiracleAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.adapter.audio.DownloadedAdapter;
import com.vkontakte.miracle.dialog.audio.AudioDialog;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.executors.audio.AddAudio;
import com.vkontakte.miracle.executors.audio.DeleteAudio;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWC;
import com.vkontakte.miracle.service.downloads.audio.AudioDownloadServiceController;
import com.vkontakte.miracle.service.downloads.audio.AudioEraseServiceController;
import com.vkontakte.miracle.service.player.AudioPlayerMedia;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;

import java.util.ArrayList;

public class AudioItemActions {

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
                ((AudioItemWC) itemWrap.getHolder()).getAudioItems().remove(itemWrap);
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
                ((AudioItemWC) itemWrap.getHolder()).getAudioItems().remove(itemWrap);
                downloadedAdapter.notifyItemRemoved(pos);
            }
        }
        AudioEraseServiceController.get().addErase(audioItem);
    }

    public static void resolveAddToPlaylist(DataItemWrap<?,?> itemWrap, Context context){

    }

    public static void resolvePlayNext(DataItemWrap<?,?> itemWrap){
        AudioPlayerServiceController.get().setPlayNext(new AudioPlayerMedia(itemWrap));
    }

    public static void resolveGoToArtist(DataItemWrap<?,?> itemWrap, Context context){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        NavigationUtil.goToArtist(audioItem, context);
    }

    public static void resolveFindArtist(DataItemWrap<?,?> itemWrap, Context context){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        NavigationUtil.goToArtistSearch(audioItem, context);
    }

    public static void resolveGoToAlbum(DataItemWrap<?,?> itemWrap, Context context){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        NavigationUtil.goToAlbum(audioItem, context);
    }

    public static void resolveOnClick(DataItemWrap<?,?> itemWrap){
        AudioItem audioItem = (AudioItem) itemWrap.getItem();
        if(audioItem.isLicensed()) {
            AudioPlayerServiceController.get().playNewAudio(new AudioPlayerMedia(itemWrap));
        }
    }

    public static void showAudioDialog(AudioItem audioItem, Context context,
                                       AudioDialogActionListener listener){
        AudioDialog audioDialog = new AudioDialog(context, audioItem);
        audioDialog.setDialogActionListener(listener);
        audioDialog.show(context);
    }

}
