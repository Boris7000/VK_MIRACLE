package com.vkontakte.miracle.adapter.wall.holders;

import static com.vkontakte.miracle.engine.util.NavigationUtil.goToOwnerFriends;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToOwnerGroups;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToOwnerMusic;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToOwnerPhotos;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToUserMusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.button.TextViewButton;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.wall.fields.Counter;

public class WallCounterViewHolder extends MiracleViewHolder {

    private final TextViewButton textViewButton;

    public WallCounterViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewButton = (TextViewButton) itemView;
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        Counter counter = (Counter) itemDataHolder;


        int imageResource = 0;
        int stringResource = 0;

        switch (counter.getDataType()){
            case "audios":{
                imageResource = R.drawable.ic_vk_music_outline_28;
                stringResource = R.string.audios;
                itemView.setOnClickListener(view -> {
                    goToOwnerMusic(counter.getOwnerId(), getMiracleActivity());
                });
                break;
            }
            case "videos":{
                imageResource = R.drawable.ic_vk_videos_outline_28;
                stringResource = R.string.videos;
                break;
            }
            case "photos":{
                imageResource = R.drawable.ic_photos_28;
                stringResource = R.string.photos;
                itemView.setOnClickListener(view -> goToOwnerPhotos(counter.getOwnerId(), getMiracleActivity()));
                break;
            }
            case "gifts":{
                imageResource = R.drawable.ic_gift_28;
                stringResource = R.string.gifts;
                break;
            }
            case "friends":{
                imageResource = R.drawable.ic_friends_28;
                stringResource = R.string.friends;
                itemView.setOnClickListener(view -> goToOwnerFriends(counter.getOwnerId(), getMiracleActivity()));
                break;
            }
            case "subscriptions":{
                imageResource = R.drawable.ic_followings_28;
                stringResource = R.string.peoples;
                break;
            }
            case "groups":{
                imageResource = R.drawable.ic_users_28;
                stringResource = R.string.groups;
                itemView.setOnClickListener(view -> goToOwnerGroups(counter.getOwnerId(), getMiracleActivity()));
                break;
            }
            case "pages":{
                imageResource = R.drawable.ic_groups_28;
                stringResource = R.string.subscriptions;
                break;
            }
            case "followers":{
                imageResource = R.drawable.ic_followers_28;
                stringResource = R.string.followers;
                break;
            }
            case "topics":{
                break;
            }
            case "articles":{
                break;
            }
        }

        Context context = itemView.getContext();
        if(stringResource>0){
            textViewButton.setText(context.getString(stringResource)+" | "+ counter.getCount());
        } else {
            textViewButton.setText(String.valueOf(counter.getCount()));
        }


        if(imageResource>0) {
            textViewButton.setIconStartImageResource(imageResource);
        } else {
            textViewButton.setIconStart(null);
        }

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WallCounterViewHolder(inflater.inflate(R.layout.view_wall_counter_button, viewGroup, false));
        }
    }

}
