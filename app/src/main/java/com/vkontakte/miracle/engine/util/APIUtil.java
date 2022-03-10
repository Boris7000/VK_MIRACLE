package com.vkontakte.miracle.engine.util;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.StringsUtil.stringFromArrayList;

import android.util.ArrayMap;

import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.audio.ArtistItem;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.catalog.CatalogUser;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Groups;
import com.vkontakte.miracle.network.methods.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class APIUtil {
    public static ArrayMap<String, Owner> loadOwners(ArrayList<String> ownerIds, String accessToken) throws Exception{

        ArrayMap<String,Owner> ownerArrayMap = new ArrayMap<>();
        ArrayList<String> profilesIds = new ArrayList<>();
        ArrayList<String> groupsIds = new ArrayList<>();

        for (String s:ownerIds) {
            if(s.charAt(0)=='-'){
                groupsIds.add(s.substring(1));
            } else {
                profilesIds.add(s);
            }
        }

        if(!groupsIds.isEmpty()) {
            Response<JSONObject> response = Groups.getWithMessageFields(stringFromArrayList(groupsIds, ","), accessToken).execute();
            JSONObject jo_response = validateBody(response);
            JSONArray groups = jo_response.getJSONObject("response").getJSONArray("groups");
            for(int i=0;i < groups.length(); i++){
                GroupItem groupItem = new GroupItem(groups.getJSONObject(i));
                ownerArrayMap.put(groupItem.getId(),new Owner(groupItem));
            }
        }

        if(!profilesIds.isEmpty()) {
            Response<JSONObject> response = Users.getWithMessageFields(stringFromArrayList(profilesIds, ","), accessToken).execute();
            JSONObject jo_response = validateBody(response);
            JSONArray profiles = jo_response.getJSONArray("response");

            for (int i = 0; i < profiles.length(); i++) {
                ProfileItem profileItem = new ProfileItem(profiles.getJSONObject(i));
                ownerArrayMap.put(profileItem.getId(), new Owner(profileItem));
            }
        }


        return ownerArrayMap;
    }

    public static ArrayMap<String, Owner> createOwnersMap(JSONObject jsonObject) throws JSONException {

        ArrayMap<String,Owner> ownerArrayMap = new ArrayMap<>();

        if(jsonObject.has("profiles")){
            JSONArray profiles = jsonObject.getJSONArray("profiles");
            for(int i=0;i < profiles.length(); i++){
                ProfileItem profileItem = new ProfileItem(profiles.getJSONObject(i));
                ownerArrayMap.put(profileItem.getId(),new Owner(profileItem));
            }
        }

        if(jsonObject.has("groups")){
            JSONArray groups = jsonObject.getJSONArray("groups");
            for(int i=0;i < groups.length(); i++){
                GroupItem groupItem = new GroupItem(groups.getJSONObject(i));
                ownerArrayMap.put(groupItem.getId(),new Owner(groupItem));
            }
        }
        return ownerArrayMap;
    }

    public static ArrayMap<String, ProfileItem> createProfilesMap(JSONArray profiles) throws JSONException {
        ArrayMap<String,ProfileItem> profilesMap = new ArrayMap<>();
        for(int i=0;i < profiles.length(); i++){
            ProfileItem profileItem = new ProfileItem(profiles.getJSONObject(i));
            profilesMap.put(profileItem.getId(),profileItem);
        }
        return profilesMap;
    }

    public static ArrayMap<String, GroupItem> createGroupsMap(JSONArray groups) throws JSONException {
        ArrayMap<String,GroupItem> groupsMap = new ArrayMap<>();
        for(int i=0;i < groups.length(); i++){
            GroupItem groupItem = new GroupItem(groups.getJSONObject(i));
            groupsMap.put(groupItem.getId(),groupItem);
        }
        return groupsMap;
    }

    public static ArrayMap<String, CatalogUser> createCatalogUsersMap(JSONArray catalogUsers, ArrayMap<String,ProfileItem> profilesMap) throws JSONException {
        ArrayMap<String,CatalogUser> catalogUsersMap = new ArrayMap<>();
        for(int i=0;i < catalogUsers.length(); i++){
            CatalogUser catalogUser = new CatalogUser(catalogUsers.getJSONObject(i), profilesMap);
            catalogUsersMap.put(catalogUser.getItemId(), catalogUser);
        }
        return catalogUsersMap;
    }

    public static ArrayMap<String, AudioItem> createAudiosMap(JSONArray audios) throws JSONException {
        ArrayMap<String, AudioItem> audiosMap = new ArrayMap<>();
        for(int i=0;i < audios.length(); i++){
            AudioItem audioItem = new AudioItem(audios.getJSONObject(i));
            audiosMap.put(audioItem.getOwnerId()+"_"+audioItem.getId(), audioItem);
        }
        return audiosMap;
    }

    public static ArrayMap<String, PlaylistItem> createPlaylistsMap(JSONArray playlists, ArrayMap<String,ProfileItem> profilesMap, ArrayMap<String,GroupItem> groupsMap) throws JSONException {
        ArrayMap<String, PlaylistItem> playlistsMap = new ArrayMap<>();
        for(int i=0;i < playlists.length(); i++){
            PlaylistItem playlistItem = new PlaylistItem(playlists.getJSONObject(i), profilesMap, groupsMap);
            playlistsMap.put(playlistItem.getOwnerId()+"_"+playlistItem.getId(), playlistItem);
        }
        return playlistsMap;
    }

    public static ArrayMap<String, ArtistItem> createArtistsMap(JSONArray artists) throws JSONException {
        ArrayMap<String,ArtistItem> artistsMap = new ArrayMap<>();
        for(int i=0;i < artists.length(); i++){
            ArtistItem artistItem = new ArtistItem(artists.getJSONObject(i));
            artistsMap.put(artistItem.getId(),artistItem);
        }
        return artistsMap;
    }
}
