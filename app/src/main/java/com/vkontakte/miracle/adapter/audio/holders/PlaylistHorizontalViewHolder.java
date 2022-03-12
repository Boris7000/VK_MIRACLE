package com.vkontakte.miracle.adapter.audio.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.FragmentUtil.goToPlaylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.dialog.audio.PlaylistDialog;
import com.vkontakte.miracle.dialog.audio.PlaylistDialogActionListener;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.FragmentUtil;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.player.PlayerServiceController;

public class PlaylistHorizontalViewHolder extends MiracleViewHolder {

    private final ImageView imageView;
    private final TextView title;
    private final TextView subtitle;
    private final ViewStub explicitStub;
    private final ProfileItem userItem;
    private ImageView explicit;

    public PlaylistHorizontalViewHolder(@NonNull View itemView) {
        super(itemView);
        userItem = getMiracleActivity().getUserItem();
        imageView = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        explicitStub = itemView.findViewById(R.id.explicitStub);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {
        PlaylistItem playlistItem = (PlaylistItem) itemDataHolder;
        title.setText(playlistItem.getTitle());
        subtitle.setText(playlistItem.getSubtitle());

        Picasso.get().cancelRequest(imageView);

        if(playlistItem.getPhoto()!=null){
            Photo photo = playlistItem.getPhoto();
            if(photo.getPhoto300()!=null){
                Picasso.get().load(photo.getPhoto300()).into(imageView);
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

        itemView.setOnClickListener(view -> goToPlaylist(playlistItem,getMiracleActivity()));

        itemView.setOnLongClickListener(view -> {
            PlaylistDialog playlistDialog = new PlaylistDialog(getMiracleActivity(),playlistItem,userItem);
            playlistDialog.setDialogActionListener(new PlaylistDialogActionListener() {
                @Override
                public void follow() {
                    playlistItem.setFollowing(true);
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
                    PlayerServiceController.get().loadAndSetPlayNext(playlistItem);
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
            itemView.getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        });

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PlaylistHorizontalViewHolder(inflater.inflate(R.layout.view_playlist_item_horizontal, viewGroup, false));
        }
    }

}
