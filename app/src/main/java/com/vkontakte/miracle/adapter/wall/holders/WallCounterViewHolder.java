package com.vkontakte.miracle.adapter.wall.holders;

import static com.miracle.widget.ExtendedTextHelper.ICON_POS_LEFT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.widget.ExtendedMaterialButton;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.model.wall.fields.Counter;

import java.util.Locale;

public class WallCounterViewHolder extends MiracleViewHolder {

    private final ExtendedMaterialButton button;

    public WallCounterViewHolder(@NonNull View itemView) {
        super(itemView);
        button = (ExtendedMaterialButton) itemView;
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
                button.setOnClickListener(view -> {
                    NavigationUtil.goToOwnerMusic(counter.getOwnerId(), getContext());
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
                button.setOnClickListener(view -> NavigationUtil.goToOwnerPhotos(counter.getOwnerId(), getContext()));
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
                button.setOnClickListener(view -> NavigationUtil.goToOwnerFriends(counter.getOwnerId(), getContext()));
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
                button.setOnClickListener(view -> NavigationUtil.goToOwnerGroups(counter.getOwnerId(), getContext()));
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
            String string = String.format(Locale.getDefault(),
                    "%s | %d", context.getString(stringResource), counter.getCount());
            button.setText(string);
        } else {
            button.setText(String.valueOf(counter.getCount()));
        }

        button.setIconResource(imageResource, ICON_POS_LEFT);

    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WallCounterViewHolder(inflater.inflate(R.layout.view_wall_counter_button, viewGroup, false));
        }
    }

}
