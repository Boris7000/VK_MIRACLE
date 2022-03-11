package com.vkontakte.miracle.adapter.audio.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.FragmentUtil.goToArtist;
import static com.vkontakte.miracle.engine.util.FragmentUtil.goToOwner;
import static com.vkontakte.miracle.engine.util.TimeUtil.getUpdatedDateString;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.PlaylistDialog;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.FragmentUtil;
import com.vkontakte.miracle.engine.util.StringsUtil;
import com.vkontakte.miracle.engine.view.MiracleButton;
import com.vkontakte.miracle.engine.view.switchIcon.SwitchIconViewV2;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.model.users.ProfileItem;
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
    private final SwitchIconViewV2 addButton;
    private final ProfileItem userItem;

    private PlaylistItem playlistItem;

    public PlaylistLargeViewHolder(@NonNull View itemView) {
        super(itemView);
        userItem = getMiracleActivity().getUserItem();
        playButton = itemView.findViewById(R.id.playButton);
        optionsButton = itemView.findViewById(R.id.optionsButton);
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        explicitStub = itemView.findViewById(R.id.explicitStub);
        subtitle2 = itemView.findViewById(R.id.subtitle2);
        subtitle3 = itemView.findViewById(R.id.subtitle3);
        addButton = itemView.findViewById(R.id.addButton);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        playlistItem = (PlaylistItem) itemDataHolder;

        title.setText(playlistItem.getTitle());
        subtitle.setText(playlistItem.getSubtitle());


        if(playlistItem.getType()==1){
            subtitle.setOnClickListener(v -> goToArtist(playlistItem,getMiracleActivity()));
        } else {
            subtitle.setOnClickListener(v -> goToOwner(playlistItem,getMiracleActivity()));
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

        if((playlistItem.getOriginal()==null&&playlistItem.getOwnerId().equals(userItem.getId()))
                ||(playlistItem.getOriginal()!=null&&playlistItem.getOriginal().getOwnerId().equals(userItem.getId()))){
            if(addButton.getVisibility()!=GONE) {
                addButton.setVisibility(GONE);
            }
        } else {
            if(addButton.getVisibility()!=VISIBLE) {
                addButton.setVisibility(VISIBLE);
            }
            switchAddButtonState(playlistItem.isFollowing());
        }

        addButton.setOnClickListener(v -> {
            playlistItem.setFollowing(!playlistItem.isFollowing());
            switchAddButtonState(playlistItem.isFollowing(), true);
            new AsyncExecutor<Integer>(){
                @Override
                public Integer inBackground() {
                    try {
                        if(playlistItem.isFollowing()){
                            playlistItem.follow(userItem);
                        } else {
                            playlistItem.delete(userItem);
                        }
                        return 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return -1;
                }
                @Override
                public void onExecute(Integer object) { }
            }.start();

        });

        optionsButton.setOnClickListener(view -> {
            PlaylistDialog playlistDialog = new PlaylistDialog(getMiracleActivity(), playlistItem, userItem);
            playlistDialog.setDialogActionListener(new PlaylistDialogActionListener() {
                @Override
                public void follow() {
                    playlistItem.setFollowing(true);
                    switchAddButtonState(true, true);
                    new AsyncExecutor<Integer>(){
                        @Override
                        public Integer inBackground() {
                            try {
                                playlistItem.follow(userItem);
                                return 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return -1;
                        }
                        @Override
                        public void onExecute(Integer object) { }
                    }.start();
                }

                @Override
                public void delete() {
                    playlistItem.setFollowing(false);
                    switchAddButtonState(false, true);
                    new AsyncExecutor<Integer>(){
                        @Override
                        public Integer inBackground() {
                            try {
                                playlistItem.delete(userItem);
                                return 1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return -1;
                        }
                        @Override
                        public void onExecute(Integer object) { }
                    }.start();
                }

                @Override
                public void playNext() {
                    getMiracleApp().getPlayerServiceController().loadAndSetPlayNext(playlistItem);
                }

                @Override
                public void goToArtist() {
                    FragmentUtil.goToArtist(playlistItem,getMiracleActivity());
                }

                @Override
                public void goToOwner() {
                    FragmentUtil.goToOwner(playlistItem,getMiracleActivity());
                }
            });
            playlistDialog.show(view.getContext());
        });

    }

    private void switchAddButtonState(boolean added){
        switchAddButtonState(added, false);
    }

    private void switchAddButtonState(boolean added, boolean animate){
        if(added){
            addButton.setImageDrawable(ResourcesCompat.getDrawable(getMiracleApp().getResources(),
                    R.drawable.ic_done_28, getMiracleApp().getTheme()));
            addButton.setIconEnabled(true,animate);
        } else {
            addButton.setIconEnabled(false,animate);
            addButton.setImageDrawable(ResourcesCompat.getDrawable(getMiracleApp().getResources(),
                    R.drawable.ic_add_28, getMiracleApp().getTheme()));
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PlaylistLargeViewHolder(inflater.inflate(R.layout.view_playlist_item_large, viewGroup, false));
        }
    }
}
