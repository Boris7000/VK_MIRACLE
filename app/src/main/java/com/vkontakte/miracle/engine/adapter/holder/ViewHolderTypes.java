package com.vkontakte.miracle.engine.adapter.holder;

import android.util.ArrayMap;

import com.vkontakte.miracle.adapter.audio.holders.AudioViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.PlaylistHorizontalViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.ArtistBannerViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.CatalogUserViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.GroupHorizontalViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.GroupViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.HeaderViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.PlaylistViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.ProfileViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.SeparatorViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.SliderViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.TripleStackedSliderViewHolder;
import com.vkontakte.miracle.adapter.catalog.holders.TripleStackedViewHolder;
import com.vkontakte.miracle.adapter.messages.holders.ConversationViewHolder;
import com.vkontakte.miracle.adapter.photos.holders.HorizontalListPhotoAlbumViewHolder;
import com.vkontakte.miracle.adapter.photos.holders.PhotoAlbumViewHolder;
import com.vkontakte.miracle.adapter.photos.holders.StackedPhotoViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.GroupLargeViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.PostViewHolder;
import com.vkontakte.miracle.adapter.wall.holders.ProfileLargeViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.loading.LoadingViewHolder;

public class ViewHolderTypes {

    public static final int TYPE_LOADING = -2;
    public static final int TYPE_ERROR = -1;
    public static final int TYPE_CONVERSATION = 0;
    public static final int TYPE_STACKED_PHOTO = 3;
    public static final int TYPE_PHOTO_ALBUM_HORIZONTAL_LIST = 4;
    public static final int TYPE_PHOTO_ALBUM = 5;
    public static final int TYPE_MESSAGE_IN = 6;
    public static final int TYPE_MESSAGE_OUT = 7;
    public static final int TYPE_GROUP = 8;
    public static final int TYPE_PROFILE = 9;
    public static final int TYPE_CATALOG_HEADER = 10;
    public static final int TYPE_CATALOG_SEPARATOR = 11;
    public static final int TYPE_CATALOG_USER = 12;
    public static final int TYPE_CATALOG_SLIDER = 13;
    public static final int TYPE_AUDIO = 14;
    public static final int TYPE_PLAYLIST = 15;
    public static final int TYPE_POST = 16;
    public static final int TYPE_CATALOG_TRIPLE_STACKED_SLIDER = 17;
    public static final int TYPE_TRIPLE_STACKED = 18;
    public static final int TYPE_PLAYLIST_DESCRIPTION = 19;
    public static final int TYPE_ARTIST_BANNER = 20;

    public static ArrayMap<Integer, ViewHolderFabric> getCatalogFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_CONVERSATION, new ConversationViewHolder.Fabric());
        arrayMap.put(TYPE_GROUP, new GroupViewHolder.Fabric());
        arrayMap.put(TYPE_PROFILE, new ProfileViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_HEADER, new HeaderViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_SEPARATOR, new SeparatorViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_USER, new CatalogUserViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_SLIDER, new SliderViewHolder.Fabric());
        arrayMap.put(TYPE_AUDIO, new AudioViewHolder.Fabric());
        arrayMap.put(TYPE_PLAYLIST, new PlaylistViewHolder.Fabric());
        arrayMap.put(TYPE_CATALOG_TRIPLE_STACKED_SLIDER, new TripleStackedSliderViewHolder.Fabric());
        arrayMap.put(TYPE_ARTIST_BANNER, new ArtistBannerViewHolder.Fabric());
        return arrayMap;
    }

    public static ArrayMap<Integer, ViewHolderFabric> getCatalogSliderFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_GROUP, new GroupHorizontalViewHolder.Fabric());
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.FabricHorizontal());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_PHOTO_ALBUM, new PhotoAlbumViewHolder.Fabric());
        arrayMap.put(TYPE_PLAYLIST, new PlaylistHorizontalViewHolder.Fabric());
        arrayMap.put(TYPE_TRIPLE_STACKED, new TripleStackedViewHolder.Fabric());
        return arrayMap;
    }

    public static ArrayMap<Integer, ViewHolderFabric> getPhotoFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_STACKED_PHOTO, new StackedPhotoViewHolder.Fabric());
        arrayMap.put(TYPE_PHOTO_ALBUM_HORIZONTAL_LIST, new HorizontalListPhotoAlbumViewHolder.Fabric());
        return arrayMap;
    }

    public static ArrayMap<Integer, ViewHolderFabric> getWallFabrics(){
        ArrayMap<Integer, ViewHolderFabric> arrayMap = new ArrayMap<>();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_POST, new PostViewHolder.Fabric());
        arrayMap.put(TYPE_PROFILE, new ProfileLargeViewHolder.Fabric());
        arrayMap.put(TYPE_GROUP, new GroupLargeViewHolder.Fabric());
        return arrayMap;
    }

}
