package com.vkontakte.miracle.adapter.audio.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.vkontakte.miracle.engine.util.TimeUtil.getUpdatedDateString;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.GoToArtistDialog;
import com.vkontakte.miracle.dialog.audio.PlaylistDialog;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.StringsUtil;
import com.vkontakte.miracle.engine.view.MiracleButton;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogArtist;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.player.AudioPlayerData;

public class PlaylistLargeViewHolder extends PlaylistHorizontalViewHolder {
    private final MiracleButton playButton;
    private final MiracleButton optionsButton;
    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final ViewStub explicitStub;
    private ImageView explicit;
    private final TextView subtitle2;
    private final TextView subtitle3;

    public PlaylistLargeViewHolder(@NonNull View itemView) {
        super(itemView);
        playButton = itemView.findViewById(R.id.playButton);
        optionsButton = itemView.findViewById(R.id.optionsButton);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        explicitStub = itemView.findViewById(R.id.explicitStub);
        subtitle2 = itemView.findViewById(R.id.subtitle2);
        subtitle3 = itemView.findViewById(R.id.subtitle3);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        PlaylistItem playlistItem = (PlaylistItem) itemDataHolder;

        title.setText(playlistItem.getTitle());
        subtitle.setText(playlistItem.getSubtitle());

        Picasso.get().cancelRequest(imageView);

        if(playlistItem.getPhoto()!=null){
            Photo photo = playlistItem.getPhoto();
            if(photo.getPhoto270()!=null){
                Picasso.get().load(photo.getPhoto600()).noFade().into(imageView);
            }
        } else {
            imageView.setImageDrawable(null);
        }

        if(playlistItem.isExplicit()){
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

        switch (playlistItem.getType()){
            case 0:{
                subtitle2.setText(getUpdatedDateString(playlistItem.getUpdateTime(),getMiracleApp()));
                break;
            }
            case 1:{
                String yearAndGenre = playlistItem.getYear();

                if (!playlistItem.getGenresString().isEmpty()) {
                    if (yearAndGenre.isEmpty()) {
                        yearAndGenre = playlistItem.getGenresString();
                    } else {
                        yearAndGenre += " | " + playlistItem.getGenresString();
                    }
                }

                if (yearAndGenre.isEmpty()) {
                    if (subtitle2.getVisibility() != View.GONE) {
                        subtitle2.setVisibility(View.GONE);
                    }
                } else {
                    if (subtitle2.getVisibility() != View.VISIBLE) {
                        subtitle2.setVisibility(View.VISIBLE);
                    }
                    subtitle2.setText(yearAndGenre);
                }
                break;
            }
        }

        StringBuilder subtitle3Text = new StringBuilder();

        if (playlistItem.getPlays()>0) {
            subtitle3Text.append(StringsUtil.getPlaysDeclensions(playlistItem.getPlays(), getMiracleActivity()));
            subtitle3Text.append(" | ");
        }

        subtitle3Text.append(StringsUtil.getAudiosDeclensions(playlistItem.getCount(), getMiracleActivity()));
        subtitle3.setText(subtitle3Text.toString());

        playButton.setOnClickListener(view -> getMiracleApp().getPlayerServiceController().
                playNewAudio(new AudioPlayerData(playlistItem.getItems().get(0))));

        optionsButton.setOnClickListener(view -> {
            PlaylistDialog playlistDialog = new PlaylistDialog(getMiracleActivity(), playlistItem,
                    getMiracleActivity().getUserItem());
            playlistDialog.setDialogActionListener(new PlaylistDialogActionListener() {
                @Override
                public void add() {

                }

                @Override
                public void remove() {

                }

                @Override
                public void playNext() {
                    getMiracleApp().getPlayerServiceController().loadAndSetPlayNext(playlistItem);
                }

                @Override
                public void goToArtist() {

                    if(playlistItem.getArtists().size()==1){
                        FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
                        fragmentCatalogArtist.setArtistId(playlistItem.getArtists().get(0));
                        getMiracleActivity().addFragment(fragmentCatalogArtist);
                    } else {
                        GoToArtistDialog goToArtistDialog = new GoToArtistDialog(view.getContext(), playlistItem.getArtists());
                        goToArtistDialog.setDialogActionListener(artist -> {
                            FragmentCatalogArtist fragmentCatalogArtist = new FragmentCatalogArtist();
                            fragmentCatalogArtist.setArtistId(artist);
                            getMiracleActivity().addFragment(fragmentCatalogArtist);
                        });
                        goToArtistDialog.show(view.getContext());
                    }
                }
            });
            playlistDialog.show(view.getContext());
        });
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PlaylistLargeViewHolder(inflater.inflate(R.layout.view_playlist_item_large, viewGroup, false));
        }
    }
}
