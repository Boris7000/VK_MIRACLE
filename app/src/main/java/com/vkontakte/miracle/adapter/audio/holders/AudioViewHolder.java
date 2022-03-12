package com.vkontakte.miracle.adapter.audio.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.AudioDialog;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.FragmentUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.view.AudioItemView;
import com.vkontakte.miracle.player.AudioPlayerData;
import com.vkontakte.miracle.player.PlayerServiceController;

public class AudioViewHolder extends MiracleViewHolder {

    private final AudioItemView audioItemView;

    public AudioViewHolder(@NonNull View itemView) {
        super(itemView);
        audioItemView = (AudioItemView) itemView;
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        AudioItem audioItem = (AudioItem) itemDataHolder;

        audioItemView.setValues(audioItem);

        if(audioItem.isLicensed()) {
            audioItemView.setOnClickListener(view -> PlayerServiceController.get().
                    playNewAudio(new AudioPlayerData(itemDataHolder)));
        } else {
            audioItemView.setOnClickListener(null);
        }

        audioItemView.setOnLongClickListener(view -> {
            AudioDialog audioDialog = new AudioDialog(getMiracleActivity(), audioItem,
                    getMiracleActivity().getUserItem());
            audioDialog.setDialogActionListener(new AudioDialogActionListener() {
                @Override
                public void add() {

                }

                @Override
                public void remove() {

                }

                @Override
                public void playNext() {
                    PlayerServiceController.get().setPlayNext(new AudioPlayerData(itemDataHolder));
                }

                @Override
                public void addToPlaylist() {

                }

                @Override
                public void goToAlbum() {
                    FragmentUtil.goToAlbum(audioItem,getMiracleActivity());
                }

                @Override
                public void goToArtist() {
                    FragmentUtil.goToArtist(audioItem,getMiracleActivity());
                }
            });
            audioDialog.show(view.getContext());
            itemView.getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        });

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new AudioViewHolder(inflater.inflate(R.layout.view_audio_item, viewGroup, false));
        }
    }

    public static class FabricSheet implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new AudioViewHolder(inflater.inflate(R.layout.view_audio_item_sheet, viewGroup, false));
        }
    }
}
