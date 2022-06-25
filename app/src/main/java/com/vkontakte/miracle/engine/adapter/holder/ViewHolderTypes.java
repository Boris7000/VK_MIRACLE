package com.vkontakte.miracle.engine.adapter.holder;

import android.util.ArrayMap;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.photos.holders.HorizontalListPhotoAlbumViewHolder;
import com.vkontakte.miracle.adapter.photos.holders.StackedPhotosViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.GroupLargeViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.PostViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.ProfileLargeViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.loading.LoadingViewHolder;

public class ViewHolderTypes {

    public static final int TYPE_LOADING = -2;
    public static final int TYPE_ERROR = -1;

    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_WRAPPED_AUDIO = 2;
    public static final int TYPE_PLAYLIST = 3;
    public static final int TYPE_WRAPPED_PLAYLIST = 4;
    public static final int TYPE_GROUP = 5;
    public static final int TYPE_WRAPPED_GROUP = 6;
    public static final int TYPE_CATALOG_USER = 7;
    public static final int TYPE_WRAPPED_CATALOG_USER = 8;

    public static final int TYPE_CONVERSATION = 9;
    public static final int TYPE_MESSAGE_IN = 10;
    public static final int TYPE_MESSAGE_OUT = 11;

    public static final int TYPE_PHOTO_ITEM = 12;
    public static final int TYPE_STACKED_PHOTOS = 13;
    public static final int TYPE_PHOTO_ALBUM_HORIZONTAL_LIST = 14;
    public static final int TYPE_PHOTO_ALBUM = 15;

    public static final int TYPE_POST = 16;
    public static final int TYPE_PROFILE = 17;

    public static final int TYPE_CATALOG_HEADER = 18;
    public static final int TYPE_CATALOG_SEPARATOR = 19;
    public static final int TYPE_CATALOG_SLIDER = 20;
    public static final int TYPE_CATALOG_TRIPLE_STACKED_SLIDER = 21;
    public static final int TYPE_TRIPLE_STACKED = 22;
    public static final int TYPE_PLAYLIST_DESCRIPTION = 23;
    public static final int TYPE_ARTIST_BANNER = 24;
    public static final int TYPE_HORIZONTAL_BUTTONS = 25;
    public static final int TYPE_PLAYLIST_RECOMMENDATION = 26;
    public static final int TYPE_BUTTON_PLAY_SHUFFLED = 27;
    public static final int TYPE_BUTTON_OPEN_SECTION = 28;
    public static final int TYPE_BUTTON_CREATE_PLAYLIST = 29;

    public static ArrayMap<Integer, ViewHolderFabric> getVerticalFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        return arrayMap;
    }

    public static ArrayMap<Integer, ViewHolderFabric> getHorizontalFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.FabricHorizontal());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        return arrayMap;
    }

    public static ArrayMap<Integer, ViewHolderFabric> getPhotoFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = getVerticalFabrics();
        arrayMap.put(TYPE_STACKED_PHOTOS, new StackedPhotosViewHolder.Fabric());
        arrayMap.put(TYPE_PHOTO_ALBUM_HORIZONTAL_LIST, new HorizontalListPhotoAlbumViewHolder.Fabric());
        return arrayMap;
    }

    public static ArrayMap<Integer, ViewHolderFabric> getWallFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = getVerticalFabrics();
        arrayMap.put(TYPE_POST, new PostViewHolder.Fabric());
        arrayMap.put(TYPE_PROFILE, new ProfileLargeViewHolder.Fabric());
        arrayMap.put(TYPE_GROUP, new GroupLargeViewHolder.Fabric());
        return arrayMap;
    }

}
