package com.vkontakte.miracle.model.catalog;

import static com.vkontakte.miracle.engine.util.APIUtil.createArtistsMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createAudiosMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createCatalogUsersMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createGroupsMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createPlaylistsMap;
import static com.vkontakte.miracle.engine.util.APIUtil.createProfilesMap;

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
    private ArrayMap<String, ArtistItem> artistsMap;

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

    public ArrayMap<String, ArtistItem> getArtistsMap() {
        return artistsMap;
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

        if(response.has("artists")){
            artistsMap = createArtistsMap(response.getJSONArray("artists"));
        }
    }


}
