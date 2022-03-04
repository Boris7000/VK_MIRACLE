package com.vkontakte.miracle.adapter.audio.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.AudioDialog;
import com.vkontakte.miracle.dialog.audio.AudioDialogActionListener;
import com.vkontakte.miracle.dialog.audio.GoToArtistDialog;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.fragment.audio.FragmentPlaylist;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogArtist;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.player.AudioPlayerData;

public class AudioViewHolder extends MiracleViewHolder {

    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final TextView duration;
    private final ViewStub explicitStub;

    private ImageView explicit;

    public AudioViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        explicitStub = itemView.findViewById(R.id.explicitStub);
        duration = itemView.findViewById(R.id.duration);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        AudioItem audioItem = (AudioItem) itemDataHolder;
        title.setText(audioItem.getTitle());
        subtitle.setText(audioItem.getArtist());
        duration.setText(audioItem.getDurationString());

        Picasso.get().cancelRequest(imageView);

        if(audioItem.getAlbum()!=null){
            Album album = audioItem.getAlbum();
            if(album.getThumb()!=null){
                Photo thumb = album.getThumb();
                if(thumb.getPhoto135()!=null){
                    Picasso.get().load(thumb.getPhoto135()).noFade().into(imageView);
                } else {
                    imageView.setImageDrawable(null);
                }
            } else {
                imageView.setImageDrawable(null);
            }
        } else {
            imageView.setImageDrawable(null);
        }

        itemView.setOnClickListener(view -> getMiracleApp().getPlayerServiceController().
                playNewAudio(new AudioPlayerData(itemDataHolder)));

        itemView.setOnLongClickListener(view -> {
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

        if(audioItem.isExplicit()){
            if(explicit==null) {
                if(explicitStub!=null) {
                    explicit = (ImageView) explicitStub.inflate();
                } else {
                    explicit = itemView.findViewById(R.id.explicit);
                }
            }
            if(explicit.getVisibility()!=VISIBLE) {
                explicit.setVisibility(VISIBLE);
            }
        } else {
            if(explicit!=null&&explicit.getVisibility()!=GONE){
                explicit.setVisibility(GONE);
            }
        }

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
