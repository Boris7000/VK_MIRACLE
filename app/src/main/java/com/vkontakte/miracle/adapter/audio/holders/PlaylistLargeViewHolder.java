package com.vkontakte.miracle.adapter.audio.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToArtist;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToOwner;
import static com.vkontakte.miracle.engine.util.TimeUtil.getUpdatedDateString;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.widget.ExtendedMaterialButton;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.PlaylistDialog;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.util.StringsUtil;
import com.vkontakte.miracle.executors.audio.DeletePlaylist;
import com.vkontakte.miracle.executors.audio.FollowPlaylist;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.service.player.AudioPlayerMedia;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;

public class PlaylistLargeViewHolder extends MiracleViewHolder {
    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final ViewStub explicitStub;
    private ImageView explicit;
    private final TextView subtitle2;
    private final TextView subtitle3;
    private final ExtendedMaterialButton addButton;

    private PlaylistItem playlistItem;
    private final User user;

    public PlaylistLargeViewHolder(@NonNull View itemView) {
        super(itemView);
        user = StorageUtil.get().currentUser();
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        explicitStub = itemView.findViewById(R.id.explicitStub);
        subtitle2 = itemView.findViewById(R.id.subtitle2);
        subtitle3 = itemView.findViewById(R.id.subtitle3);
        addButton = itemView.findViewById(R.id.addButton);

        Button playButton = itemView.findViewById(R.id.playButton);
        Button optionsButton = itemView.findViewById(R.id.optionsButton);
        playButton.setOnClickListener(v ->
                AudioPlayerServiceController.get().
                        playNewAudio(new AudioPlayerMedia(playlistItem,0)));
        optionsButton.setOnClickListener(view -> {
            PlaylistDialog playlistDialog = new PlaylistDialog(view.getContext(), playlistItem);
            playlistDialog.setDialogActionListener(new PlaylistDialogActionListener() {
                @Override
                public void follow() {
                    resolveFollow();
                }

                @Override
                public void delete() {
                    resolveDelete();
                }

                @Override
                public void playNext() {
                    AudioPlayerServiceController.get().
                            setPlayNext(new AudioPlayerMedia(playlistItem,0));
                }

                @Override
                public void goToArtist() {
                    NavigationUtil.goToArtist(playlistItem, getContext());
                }

                @Override
                public void goToOwner() {
                    NavigationUtil.goToOwner(playlistItem, getContext());
                }
            });
            playlistDialog.show(itemView.getContext());
        });

        addButton.setOnClickListener(v -> {
            if(playlistItem.isFollowing()){
               resolveDelete();
            } else {
                resolveFollow();
            }
        });
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        Context context = itemView.getContext();

        playlistItem = (PlaylistItem) itemDataHolder;

        title.setText(playlistItem.getTitle());
        subtitle.setText(playlistItem.getSubtitle());

        if(playlistItem.getType()==1){
            if(!playlistItem.getArtists().isEmpty()){
                subtitle.setOnClickListener(v -> goToArtist(playlistItem, getContext()));
            }
        } else {
            subtitle.setOnClickListener(v -> goToOwner(playlistItem, getContext()));
        }

        Picasso.get().cancelRequest(imageView);

        if(playlistItem.getPhoto()!=null){
            Photo photo = playlistItem.getPhoto();
            if(photo.getPhoto270()!=null){
                Picasso.get().load(photo.getPhoto600()).into(imageView);
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
                subtitle2.setText(getUpdatedDateString(context, playlistItem.getUpdateTime()));
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
            subtitle3Text.append(StringsUtil.getPlaysDeclensions(context, playlistItem.getPlays()));
            subtitle3Text.append(" | ");
        }

        subtitle3Text.append(StringsUtil.getAudiosDeclensions(context, playlistItem.getCount()));
        subtitle3.setText(subtitle3Text.toString());

        if((playlistItem.getOriginal()==null&&playlistItem.getOwnerId().equals(user.getId()))
                ||(playlistItem.getOriginal()!=null&&playlistItem.getOriginal().getOwnerId().equals(user.getId()))){
            if(addButton.getVisibility()!=GONE) {
                addButton.setVisibility(GONE);
            }
        } else {
            if(addButton.getVisibility()!=VISIBLE) {
                addButton.setVisibility(VISIBLE);
            }
            switchAddButtonState(playlistItem.isFollowing(), false, true);
        }


    }

    private void resolveFollow(){
        switchAddButtonState(true,true,false);
        new FollowPlaylist(playlistItem){
            @Override
            public void onExecute(Boolean object) {
                super.onExecute(object);
                switchAddButtonState(playlistItem.isFollowing(), false, true);
            }
        }.start();
    }

    private void resolveDelete(){
        switchAddButtonState(false,true, false);
        new DeletePlaylist(playlistItem){
            @Override
            public void onExecute(Boolean object) {
                super.onExecute(object);
                switchAddButtonState(playlistItem.isFollowing(), false,true);
            }
        }.start();
    }

    private void switchAddButtonState(boolean added, boolean animate, boolean clickable){
        addButton.setToggled(animate);
        addButton.setClickable(clickable);
        addButton.setChecked(added);
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PlaylistLargeViewHolder(
                    inflater.inflate(R.layout.view_playlist_item_large, viewGroup, false));
        }
    }
}
