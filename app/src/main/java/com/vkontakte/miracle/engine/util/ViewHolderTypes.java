package com.vkontakte.miracle.engine.util;

import static com.miracle.engine.adapter.holder.ViewHolderTypes.getVerticalFabrics;

import android.util.ArrayMap;

import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.adapter.photos.holders.HorizontalListPhotoAlbumViewHolder;
import com.vkontakte.miracle.adapter.photos.holders.StackedPhotosViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.GroupLargeViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.PostViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.ProfileLargeViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.StoriesViewHolder;

public class ViewHolderTypes {

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

    public static final int TYPE_PHOTO = 12;
    public static final int TYPE_WRAPPED_PHOTO = 13;
    public static final int TYPE_STACKED_PHOTOS = 14;
    public static final int TYPE_PHOTO_ALBUM_HORIZONTAL_LIST = 15;
    public static final int TYPE_PHOTO_ALBUM = 16;

    public static final int TYPE_POST = 17;
    public static final int TYPE_STORIES = 18;
    public static final int TYPE_PROFILE = 19;

    public static final int TYPE_CATALOG_HEADER = 20;
    public static final int TYPE_CATALOG_SEPARATOR = 21;
    public static final int TYPE_CATALOG_SLIDER = 22;
    public static final int TYPE_CATALOG_BANNER_SLIDER = 23;
    public static final int TYPE_CATALOG_TRIPLE_STACKED_SLIDER = 24;
    public static final int TYPE_CATALOG_CATEGORIES_LIST = 25;
    public static final int TYPE_CATALOG_LINK = 26;
    public static final int TYPE_CATALOG_BANNER = 27;
    public static final int TYPE_CATALOG_HEADER_EXTENDED = 28;
    public static final int TYPE_CATALOG_SUGGESTION = 29;
    public static final int TYPE_TRIPLE_STACKED = 30;
    public static final int TYPE_PLAYLIST_DESCRIPTION = 31;
    public static final int TYPE_ARTIST_BANNER = 32;
    public static final int TYPE_HORIZONTAL_BUTTONS = 33;
    public static final int TYPE_PLAYLIST_RECOMMENDATION = 34;
    public static final int TYPE_WRAPPED_PLAYLIST_RECOMMENDATION = 35;
    public static final int TYPE_BUTTON_PLAY_SHUFFLED = 36;
    public static final int TYPE_BUTTON_OPEN_SECTION = 37;
    public static final int TYPE_BUTTON_CREATE_PLAYLIST = 38;
    public static final int TYPE_BUTTON_PLAY = 39;
    public static final int TYPE_WALL_COUNTERS = 40;
    public static final int TYPE_WALL_COUNTER = 41;
    public static final int TYPE_PLAYLIST_CREATION = 42;

    public static ArrayMap<Integer, ViewHolderFabric> getPhotoFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = getVerticalFabrics();
        arrayMap.put(TYPE_STACKED_PHOTOS, new StackedPhotosViewHolder.Fabric());
        arrayMap.put(TYPE_PHOTO_ALBUM_HORIZONTAL_LIST, new HorizontalListPhotoAlbumViewHolder.Fabric());
        return arrayMap;
    }

    public static ArrayMap<Integer, ViewHolderFabric> getWallFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = getVerticalFabrics();
        arrayMap.put(TYPE_POST, new PostViewHolder.Fabric());
        arrayMap.put(TYPE_STORIES, new StoriesViewHolder.Fabric());
        arrayMap.put(TYPE_PROFILE, new ProfileLargeViewHolder.Fabric());
        arrayMap.put(TYPE_GROUP, new GroupLargeViewHolder.Fabric());
        return arrayMap;
    }

}
