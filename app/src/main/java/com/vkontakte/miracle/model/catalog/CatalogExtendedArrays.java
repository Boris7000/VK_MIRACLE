package com.vkontakte.miracle.model.catalog;

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

import com.vkontakte.miracle.model.audio.ArtistItem;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;

import org.json.JSONException;
import org.json.JSONObject;


public class CatalogExtendedArrays {

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

    public CatalogExtendedArrays(JSONObject response) throws JSONException {

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


}
