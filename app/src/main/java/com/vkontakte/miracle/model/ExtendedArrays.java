package com.vkontakte.miracle.model;

import static com.vkontakte.miracle.engine.util.APIUtil.createArtistsMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createAudiosMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createCatalogBannersMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createCatalogSuggestionsMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createCatalogUsersMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createGroupsMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createLinksMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createPlaylistsMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createProfilesMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createRecommendedPlaylistsMap;

import android.util.ArrayMap;
import android.util.Log;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.audio.ArtistItem;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWF;
import com.vkontakte.miracle.model.audio.wraps.PlaylistItemWF;
import com.vkontakte.miracle.model.catalog.CatalogBanner;
import com.vkontakte.miracle.model.catalog.CatalogBlock;
import com.vkontakte.miracle.model.catalog.CatalogLink;
import com.vkontakte.miracle.model.catalog.CatalogSuggestion;
import com.vkontakte.miracle.model.catalog.CatalogUser;
import com.vkontakte.miracle.model.catalog.RecommendedPlaylist;
import com.vkontakte.miracle.model.catalog.wraps.CatalogUserWF;
import com.vkontakte.miracle.model.catalog.wraps.RecommendedPlaylistWF;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.groups.wraps.GroupItemWrapFabric;
import com.vkontakte.miracle.model.users.ProfileItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ExtendedArrays {

    private ArrayMap<String, ProfileItem> profilesMap;
    private ArrayMap<String, CatalogUser> catalogUsersMap;
    private ArrayMap<String, GroupItem> groupsMap;
    private ArrayMap<String, AudioItem> audiosMap;
    private ArrayMap<String, PlaylistItem> playlistsMap;
    private ArrayMap<String, RecommendedPlaylist> recommendedPlaylistsMap;
    private ArrayMap<String, ArtistItem> artistsMap;
    private ArrayMap<String, CatalogLink> linksMap;
    private ArrayMap<String, CatalogBanner> bannersMap;
    private ArrayMap<String, CatalogSuggestion> suggestionsMap;

    public ExtendedArrays(JSONObject response) throws JSONException {

        if(response.has("profiles")){
            profilesMap = createProfilesMap(response.getJSONArray("profiles"));
        }

        if(response.has("catalog_users")){
            catalogUsersMap = createCatalogUsersMap(response.getJSONArray("catalog_users"), profilesMap);
        }

        if(response.has("groups")){
            groupsMap = createGroupsMap(response.getJSONArray("groups"));
        }

        if(response.has("audios")){
            audiosMap = createAudiosMap(response.getJSONArray("audios"));
        }

        if(response.has("playlists")){
            playlistsMap = createPlaylistsMap(response.getJSONArray("playlists"), profilesMap, groupsMap);
        }

        if(response.has("recommended_playlists")){
            recommendedPlaylistsMap = createRecommendedPlaylistsMap(response.getJSONArray("recommended_playlists"),
                    playlistsMap, audiosMap);
        }

        if(response.has("artists")){
            artistsMap = createArtistsMap(response.getJSONArray("artists"));
        }

        if(response.has("links")){
            linksMap = createLinksMap(response.getJSONArray("links"));
        }

        if(response.has("catalog_banners")){
            bannersMap = createCatalogBannersMap(response.getJSONArray("catalog_banners"));
        }

        if(response.has("suggestions")){
            suggestionsMap = createCatalogSuggestionsMap(response.getJSONArray("suggestions"));
        }
    }

    public ArrayMap<String, ProfileItem> getProfilesMap() {
        return profilesMap;
    }

    public ArrayMap<String, CatalogUser> getCatalogUsersMap() {
        return catalogUsersMap;
    }

    public ArrayMap<String, GroupItem> getGroupsMap() {
        return groupsMap;
    }

    public ArrayMap<String, AudioItem> getAudiosMap() {
        return audiosMap;
    }

    public ArrayMap<String, PlaylistItem> getPlaylistsMap() {
        return playlistsMap;
    }

    public ArrayMap<String, RecommendedPlaylist> getRecommendedPlaylistsMap() {
        return recommendedPlaylistsMap;
    }

    public ArrayMap<String, ArtistItem> getArtistsMap() {
        return artistsMap;
    }

    public ArrayMap<String, CatalogLink> getLinksMap() {
        return linksMap;
    }

    public ArrayMap<String, CatalogBanner> getBannersMap() {
        return bannersMap;
    }

    public ArrayMap<String, CatalogSuggestion> getSuggestionsMap() {
        return suggestionsMap;
    }

    public ArrayList<ItemDataHolder> extractForBlock(CatalogBlock catalogBlock, JSONObject jsonObject) throws JSONException {

        ArrayList<ItemDataHolder> catalogBlockItemDataHolders = new ArrayList<>();
        switch (catalogBlock.getDataType()){
            default:{
                Log.d("unknown dataType",catalogBlock.getDataType());
                break;
            }
            case "groups_friends_likes":{
                JSONArray group_ids = jsonObject.getJSONArray("groups_likes_ids");
                ArrayMap<String, GroupItem> groupsMap = getGroupsMap();
                GroupItemWrapFabric groupItemWrapFabric = new GroupItemWrapFabric();
                for (int i = 0; i < group_ids.length(); i++) {
                    GroupItem groupItem = groupsMap.get("-"+group_ids.getString(i));
                    if(groupItem!=null){
                        catalogBlockItemDataHolders.add(groupItemWrapFabric.create(groupItem, catalogBlock));
                    }
                }
                break;
            }
            case "groups_items":{
                Log.d("pekpekfpef",jsonObject.toString());
                JSONArray groups = jsonObject.getJSONArray("group_items");
                ArrayMap<String, GroupItem> groupsMap = getGroupsMap();
                GroupItemWrapFabric groupItemWrapFabric = new GroupItemWrapFabric();
                for (int i = 0; i < groups.length(); i++) {
                    GroupItem groupItem = groupsMap.get("-"+groups.getJSONObject(i).getString("id"));
                    if(groupItem!=null){
                        catalogBlockItemDataHolders.add(groupItemWrapFabric.create(groupItem, catalogBlock));
                    }
                }
                break;
            }
            case "groups":{
                Log.d("pekpekfpef",jsonObject.toString());
                JSONArray group_ids = jsonObject.getJSONArray("group_ids");
                ArrayMap<String, GroupItem> groupsMap = getGroupsMap();
                GroupItemWrapFabric groupItemWrapFabric = new GroupItemWrapFabric();
                for (int i = 0; i < group_ids.length(); i++) {
                    GroupItem groupItem = groupsMap.get("-"+group_ids.getString(i));
                    if(groupItem!=null){
                        catalogBlockItemDataHolders.add(groupItemWrapFabric.create(groupItem, catalogBlock));
                    }
                }
                break;
            }
            case "catalog_users":{
                JSONArray catalog_users_ids = jsonObject.getJSONArray("catalog_users_ids");
                ArrayMap<String, CatalogUser> catalogUsersMap = getCatalogUsersMap();
                CatalogUserWF catalogUserWF = new CatalogUserWF();
                for (int i = 0; i < catalog_users_ids.length(); i++) {
                    CatalogUser catalogUser = catalogUsersMap.get(catalog_users_ids.getString(i));
                    if(catalogUser!=null){
                        catalogBlockItemDataHolders.add(catalogUserWF.create(catalogUser, catalogBlock));
                    }
                }
                break;
            }
            case "music_recommended_playlists": {
                JSONArray playlists_ids = jsonObject.getJSONArray("playlists_ids");
                ArrayMap<String, RecommendedPlaylist> map = getRecommendedPlaylistsMap();
                RecommendedPlaylistWF recommendedPlaylistWF = new RecommendedPlaylistWF();
                for (int i = 0; i < playlists_ids.length(); i++) {
                    RecommendedPlaylist recommendedPlaylist = map.get(playlists_ids.getString(i));
                    if(recommendedPlaylist!=null){
                        catalogBlockItemDataHolders.add(recommendedPlaylistWF.create(recommendedPlaylist, catalogBlock));
                    }


                }
                break;
            }
            case "music_audios": {
                JSONArray audios_ids = jsonObject.getJSONArray("audios_ids");
                ArrayMap<String, AudioItem> audiosMap = getAudiosMap();
                AudioItemWF audioItemWF = new AudioItemWF();
                for (int i = 0; i < audios_ids.length(); i++) {
                    AudioItem audioItem = audiosMap.get(audios_ids.getString(i));
                    if(audioItem!=null) {
                        catalogBlockItemDataHolders.add(audioItemWF.create(audioItem, catalogBlock));
                    }
                }
                break;
            }
            case "music_playlists": {
                JSONArray playlists_ids = jsonObject.getJSONArray("playlists_ids");
                ArrayMap<String, PlaylistItem> playlistsMap = getPlaylistsMap();
                PlaylistItemWF playlistItemWF = new PlaylistItemWF();
                for (int i = 0; i < playlists_ids.length(); i++) {
                    PlaylistItem playlistItem = playlistsMap.get(playlists_ids.getString(i));
                    if(playlistItem!=null){
                        catalogBlockItemDataHolders.add(playlistItemWF.create(playlistItem, catalogBlock));
                    }
                }
                break;
            }
            case "artist": {
                JSONArray artists_ids = jsonObject.getJSONArray("artists_ids");
                ArrayMap<String, ArtistItem> artistsMap = getArtistsMap();
                for (int i = 0; i < artists_ids.length(); i++) {
                    ArtistItem artistItem = artistsMap.get(artists_ids.getString(i));
                    if(artistItem!=null) {
                        catalogBlockItemDataHolders.add(artistItem);
                    }
                }
                break;
            }
            case "links": {
                Log.d("pekpekfpef",jsonObject.toString());
                JSONArray links_ids = jsonObject.getJSONArray("links_ids");
                ArrayMap<String, CatalogLink> linksMap = getLinksMap();
                for (int i = 0; i < links_ids.length(); i++) {
                    CatalogLink catalogLink = linksMap.get(links_ids.getString(i));
                    if(catalogLink!=null) {
                        catalogBlockItemDataHolders.add(catalogLink);
                    }
                }
                break;
            }
            case "catalog_banners": {
                JSONArray banner_ids = jsonObject.getJSONArray("catalog_banner_ids");
                ArrayMap<String, CatalogBanner> bannersMap = getBannersMap();
                for (int i = 0; i < banner_ids.length(); i++) {
                    CatalogBanner catalogBanner = bannersMap.get(banner_ids.getString(i));
                    if(catalogBanner!=null) {
                        catalogBlockItemDataHolders.add(catalogBanner);
                    }
                }
                break;
            }
            case "search_suggestions": {
                JSONArray suggestions_ids = jsonObject.getJSONArray("suggestions_ids");
                ArrayMap<String, CatalogSuggestion> suggestionsMap = getSuggestionsMap();
                for (int i = 0; i < suggestions_ids.length(); i++) {
                    CatalogSuggestion catalogSuggestion = suggestionsMap.get(suggestions_ids.getString(i));
                    if(catalogSuggestion!=null) {
                        catalogBlockItemDataHolders.add(catalogSuggestion);
                    }
                }
                break;
            }
        }
        return catalogBlockItemDataHolders;
    }

}
