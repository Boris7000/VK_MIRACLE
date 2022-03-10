package com.vkontakte.miracle.adapter.audio.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.AudioDialog;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.dialog.audio.GoToArtistDialog;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.model.audio.view.AudioItemView;
import com.vkontakte.miracle.fragment.audio.FragmentPlaylist;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogArtist;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.player.AudioPlayerData;

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
            audioItemView.setOnClickListener(view -> getMiracleApp().getPlayerServiceController().
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
                    getMiracleApp().getPlayerServiceController().
                            setPlayNext(new AudioPlayerData(itemDataHolder));
                }

                @Override
                public void addToPlaylist() {

                }

                @Override
                public void goToAlbum() {
                    FragmentPlaylist fragmentPlaylist = new FragmentPlaylist();
                    fragmentPlaylist.setAlbum(audioItem.getAlbum());
                    getMiracleActivity().addFragment(fragmentPlaylist);
                }

                @Override
                public void goToArtist() {

                    if(audioItem.getArtists().size()==1){
                        FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
                        fragmentCatalogArtist.setArtistId(audioItem.getArtists().get(0));
                        getMiracleActivity().addFragment(fragmentCatalogArtist);
                    } else {
                        GoToArtistDialog goToArtistDialog = new GoToArtistDialog(view.getContext(), audioItem.getArtists());
                        goToArtistDialog.setDialogActionListener(artist -> {
                            FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
                            fragmentCatalogArtist.setArtistId(artist);
                            getMiracleActivity().addFragment(fragmentCatalogArtist);
                        });
                        goToArtistDialog.show(view.getContext());
                    }
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
