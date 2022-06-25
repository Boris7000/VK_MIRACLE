package com.vkontakte.miracle.model.catalog;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ARTIST_BANNER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_HEADER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_SEPARATOR;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_SLIDER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_CATALOG_TRIPLE_STACKED_SLIDER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_HORIZONTAL_BUTTONS;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_LOADING;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_CATALOG_USER;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_GROUP;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_PLAYLIST;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.audio.AudioWrapContainer;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.audio.ArtistItem;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.PlaylistWrapContainer;
import com.vkontakte.miracle.model.catalog.fields.CatalogAction;
import com.vkontakte.miracle.model.catalog.fields.CatalogBadge;
import com.vkontakte.miracle.model.catalog.fields.CatalogLayout;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.groups.GroupWrapContainer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CatalogBlock implements ItemDataHolder, AudioWrapContainer, GroupWrapContainer,
        PlaylistWrapContainer, CatalogUserWrapContainer {

    private final String id;
    private final String dataType;
    private final String nextFrom;
    private final CatalogLayout layout;
    private CatalogBadge badge;
    private ArrayList<ItemDataHolder> actions;
    private final ArrayList<ItemDataHolder> items = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getDataType() {
        return dataType;
    }

    public String getNextFrom() {
        return nextFrom;
    }

    public CatalogLayout getLayout() {
        return layout;
    }

    public CatalogBadge getBadge() {
        return badge;
    }

    public ArrayList<ItemDataHolder> getActions() {
        return actions;
    }



    public CatalogBlock(JSONObject jsonObject, CatalogExtendedArrays catalogExtendedArrays) throws JSONException {

        id = jsonObject.getString("id");
        dataType = jsonObject.getString("data_type");

        items.addAll(findItems(jsonObject, catalogExtendedArrays));

        if(jsonObject.has("next_from")){
            nextFrom = jsonObject.getString("next_from");
        } else {
            nextFrom = "";
        }

        layout = new CatalogLayout(jsonObject.getJSONObject("layout"));

        if(jsonObject.has("badge")){
            badge = new CatalogBadge(jsonObject.getJSONObject("badge"));
        }

        if(jsonObject.has("actions")){
            actions = new ArrayList<>();
            JSONArray ja_actions = jsonObject.getJSONArray("actions");

            for (int j = 0; j < ja_actions.length(); j++) {
                JSONObject action = ja_actions.getJSONObject(j);
                actions.add(new CatalogAction(action));
            }
        }
    }

    public ArrayList<ItemDataHolder> findItems(JSONObject jsonObject, CatalogExtendedArrays catalogExtendedArrays) throws JSONException {

        ArrayList<ItemDataHolder> catalogBlockItemDataHolders = new ArrayList<>();

        switch (dataType){
            default:{
                Log.d("eifjiejfiej",jsonObject.toString());
                break;
            }
            case "groups_friends_likes":{
                JSONArray group_ids = jsonObject.getJSONArray("groups_likes_ids");
                ArrayMap<String, GroupItem> groupsMap = catalogExtendedArrays.getGroupsMap();
                for (int i = 0; i < group_ids.length(); i++) {
                    GroupItem groupItem = groupsMap.get("-"+group_ids.getString(i));
                    if(groupItem!=null){
                        DataItemWrap<GroupItem, GroupWrapContainer> dataItemWrap =
                                new DataItemWrap<GroupItem, GroupWrapContainer>(groupItem, this) {
                                    @Override
                                    public int getViewHolderType() {
                                        return TYPE_WRAPPED_GROUP;
                                    }
                                };
                        catalogBlockItemDataHolders.add(dataItemWrap);
                    }
                }
                break;
            }
            case "groups_items":{
                JSONArray groups = jsonObject.getJSONArray("group_items");
                ArrayMap<String, GroupItem> groupsMap = catalogExtendedArrays.getGroupsMap();
                for (int i = 0; i < groups.length(); i++) {
                    GroupItem groupItem = groupsMap.get("-"+groups.getJSONObject(i).getString("id"));
                    if(groupItem!=null){
                        DataItemWrap<GroupItem, GroupWrapContainer> dataItemWrap =
                                new DataItemWrap<GroupItem, GroupWrapContainer>(groupItem, this) {
                                    @Override
                                    public int getViewHolderType() {
                                        return TYPE_WRAPPED_GROUP;
                                    }
                                };
                        catalogBlockItemDataHolders.add(dataItemWrap);
                    }
                }
                break;
            }
            case "groups":{
                JSONArray group_ids = jsonObject.getJSONArray("group_ids");
                ArrayMap<String, GroupItem> groupsMap = catalogExtendedArrays.getGroupsMap();
                for (int i = 0; i < group_ids.length(); i++) {
                    GroupItem groupItem = groupsMap.get("-"+group_ids.getString(i));
                    if(groupItem!=null){
                        DataItemWrap<GroupItem, GroupWrapContainer> dataItemWrap =
                                new DataItemWrap<GroupItem, GroupWrapContainer>(groupItem, this) {
                                    @Override
                                    public int getViewHolderType() {
                                        return TYPE_WRAPPED_GROUP;
                                    }
                                };
                        catalogBlockItemDataHolders.add(dataItemWrap);
                    }
                }
                break;
            }
            case "catalog_users":{
                JSONArray catalog_users_ids = jsonObject.getJSONArray("catalog_users_ids");
                ArrayMap<String, CatalogUser> catalogUsersMap = catalogExtendedArrays.getCatalogUsersMap();
                for (int i = 0; i < catalog_users_ids.length(); i++) {
                    CatalogUser catalogUser = catalogUsersMap.get(catalog_users_ids.getString(i));
                    if(catalogUser!=null){
                        DataItemWrap<CatalogUser, GroupWrapContainer> dataItemWrap =
                                new DataItemWrap<CatalogUser, GroupWrapContainer>(catalogUser, this) {
                                    @Override
                                    public int getViewHolderType() {
                                        return TYPE_WRAPPED_CATALOG_USER;
                                    }
                                };
                        catalogBlockItemDataHolders.add(dataItemWrap);
                    }
                }
                break;
            }
            case "music_recommended_playlists": {
                JSONArray audios_ids = jsonObject.getJSONArray("audios_ids");
                ArrayMap<String, AudioItem> audiosMap = catalogExtendedArrays.getAudiosMap();
                ArrayMap<String, ArrayList<ItemDataHolder>> playlistAudios = new ArrayMap<>();

                for (int i = 0; i < audios_ids.length(); i++) {
                    String playlistId_audioId = audios_ids.getString(i);
                    String playlistId = playlistId_audioId.substring(0,playlistId_audioId.indexOf('_'));
                    ArrayList<ItemDataHolder> audios = playlistAudios.get(playlistId);
                    if(audios==null){
                        playlistAudios.put(playlistId, audios=new ArrayList<>());
                    }
                    AudioItem audioItem = audiosMap.get(playlistId_audioId);
                    if(audioItem!=null) {
                        audios.add(audioItem);
                    }
                }

                JSONArray playlists_ids = jsonObject.getJSONArray("playlists_ids");
                ArrayMap<String, PlaylistItem> playlistsMap = catalogExtendedArrays.getPlaylistsMap();
                for (int i = 0; i < playlists_ids.length(); i++) {
                    PlaylistItem playlistItem = playlistsMap.get(playlists_ids.getString(i));
                    if(playlistItem!=null){
                        ArrayList<ItemDataHolder> audios = playlistAudios.get(playlistItem.getOwnerId());
                        if(audios!=null) {
                            CatalogRecommendedPlaylist catalogRecommendedPlaylist = new CatalogRecommendedPlaylist(playlistItem, audios);
                            catalogBlockItemDataHolders.add(catalogRecommendedPlaylist);
                        }
                    }
                }
                break;
            }
            case "music_audios": {
                JSONArray audios_ids = jsonObject.getJSONArray("audios_ids");
                ArrayMap<String, AudioItem> audiosMap = catalogExtendedArrays.getAudiosMap();
                for (int i = 0; i < audios_ids.length(); i++) {
                    AudioItem audioItem = audiosMap.get(audios_ids.getString(i));
                    if(audioItem!=null) {
                        DataItemWrap<AudioItem, AudioWrapContainer> dataItemWrap =
                                new DataItemWrap<AudioItem, AudioWrapContainer>(audioItem, this) {
                            @Override
                            public int getViewHolderType() {
                                return TYPE_WRAPPED_AUDIO;
                            }
                        };
                        catalogBlockItemDataHolders.add(dataItemWrap);
                    }
                }
                break;
            }
            case "music_playlists": {
                JSONArray playlists_ids = jsonObject.getJSONArray("playlists_ids");
                ArrayMap<String, PlaylistItem> playlistsMap = catalogExtendedArrays.getPlaylistsMap();
                for (int i = 0; i < playlists_ids.length(); i++) {
                    PlaylistItem playlistItem = playlistsMap.get(playlists_ids.getString(i));
                    if(playlistItem!=null){
                        DataItemWrap<PlaylistItem, AudioWrapContainer> dataItemWrap =
                                new DataItemWrap<PlaylistItem, AudioWrapContainer>(playlistItem, this) {
                                    @Override
                                    public int getViewHolderType() {
                                        return TYPE_WRAPPED_PLAYLIST;
                                    }
                                };
                        catalogBlockItemDataHolders.add(dataItemWrap);
                    }
                }
                break;
            }
            case "artist": {
                JSONArray artists_ids = jsonObject.getJSONArray("artists_ids");
                ArrayMap<String, ArtistItem> artistsMap = catalogExtendedArrays.getArtistsMap();
                for (int i = 0; i < artists_ids.length(); i++) {
                    ArtistItem artistItem = artistsMap.get(artists_ids.getString(i));
                    if(artistItem!=null) {
                        catalogBlockItemDataHolders.add(artistItem);
                    }
                }
                break;
            }
        }
        return catalogBlockItemDataHolders;
    }

    @Override
    public int getViewHolderType() {
        switch (layout.getName()){
            default:{
                return TYPE_ERROR;
            }
            case "header":{
                return TYPE_CATALOG_HEADER;
            }
            case "separator":{
                return TYPE_CATALOG_SEPARATOR;
            }
            case "slider":
            case "large_slider":
            case "music_chart_large_slider":{
                return TYPE_CATALOG_SLIDER;
            }
            case "horizontal_buttons":{
                return TYPE_HORIZONTAL_BUTTONS;
            }
            case "triple_stacked_slider":
            case "music_chart_triple_stacked_slider":{
                return TYPE_CATALOG_TRIPLE_STACKED_SLIDER;
            }
            case "banner":{
                switch (dataType){
                    default:{
                        return TYPE_ERROR;
                    }
                    case "artist":{
                        return TYPE_ARTIST_BANNER;
                    }
                }
            }
        }
    }

    public ArrayList<ItemDataHolder> getItems() {
        return items;
    }

    @Override
    public ArrayList<ItemDataHolder> getAudioItems() {
        return items;
    }

    @Override
    public ArrayList<ItemDataHolder> getGroupItems() {
        return items;
    }

    @Override
    public ArrayList<ItemDataHolder> getPlaylistItems() {
        return items;
    }

    @Override
    public ArrayList<ItemDataHolder> getCatalogUserItems() {
        return items;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null){
            if(obj instanceof CatalogBlock){
                CatalogBlock catalogBlock = (CatalogBlock) obj;
                return id.equals(catalogBlock.id);
            }
        }
        return false;
    }

}
