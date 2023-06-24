package com.vkontakte.miracle.adapter.audio.holders.actions;

import android.content.Context;

import com.vkontakte.miracle.dialog.audio.PlaylistDialog;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.executors.audio.DeletePlaylist;
import com.vkontakte.miracle.executors.audio.FollowPlaylist;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.service.player.AudioPlayerMedia;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;

public class PlaylistItemActions {

    public static void resolveFollow(DataItemWrap<?,?> itemWrap){
        PlaylistItem playlistItem = (PlaylistItem) itemWrap.getItem();
        new FollowPlaylist(playlistItem).start();
    }

    public static void resolveDelete(DataItemWrap<?,?> itemWrap){
        PlaylistItem playlistItem = (PlaylistItem) itemWrap.getItem();
        new DeletePlaylist(playlistItem).start();
    }

    public static void resolvePlayNext(DataItemWrap<?,?> itemWrap){
        PlaylistItem playlistItem = (PlaylistItem) itemWrap.getItem();
        AudioPlayerServiceController.get().setPlayNext(new AudioPlayerMedia(playlistItem, 0));
    }

    public static void resolveGoToArtist(DataItemWrap<?,?> itemWrap, Context context){
        PlaylistItem playlistItem = (PlaylistItem) itemWrap.getItem();
        NavigationUtil.goToArtist(playlistItem, context);
    }

    public static void resolveGoToOwner(DataItemWrap<?,?> itemWrap, Context context){
        PlaylistItem playlistItem = (PlaylistItem) itemWrap.getItem();
        NavigationUtil.goToOwner(playlistItem, context);
    }

    public static void resolveOnClick(DataItemWrap<?,?> itemWrap, Context context){
        PlaylistItem playlistItem = (PlaylistItem) itemWrap.getItem();
        NavigationUtil.goToPlaylist(playlistItem, context);
    }

    public static void showPlaylistDialog(PlaylistItem playlistItem, Context context,
                                          PlaylistDialogActionListener listener){
        PlaylistDialog playlistDialog = new PlaylistDialog(context, playlistItem);
        playlistDialog.setDialogActionListener(listener);
        playlistDialog.show(context);
    }

}
