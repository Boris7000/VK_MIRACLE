package com.vkontakte.miracle.engine.util;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.StringsUtil.stringFromArrayList;

import android.util.ArrayMap;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.audio.ArtistItem;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.PlaylistItem;
import com.vkontakte.miracle.model.catalog.CatalogBanner;
import com.vkontakte.miracle.model.catalog.CatalogLink;
import com.vkontakte.miracle.model.catalog.CatalogSuggestion;
import com.vkontakte.miracle.model.catalog.CatalogUser;
import com.vkontakte.miracle.model.catalog.RecommendedPlaylist;
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

    //TODO разве это должно быть тут? может в другое место переписать

    public static ArrayMap<String, ProfileItem> createProfilesMap(JSONArray profiles) throws JSONException {
        ArrayMap<String,ProfileItem> profilesMap = new ArrayMap<>();
        for(int i=0;i < profiles.length(); i++){
            ProfileItem profileItem = new ProfileItem(profiles.getJSONObject(i));
            profilesMap.put(profileItem.getId(), profileItem);
        }
        return profilesMap;
    }

    public static ArrayMap<String, GroupItem> createGroupsMap(JSONArray groups) throws JSONException {
        ArrayMap<String,GroupItem> groupsMap = new ArrayMap<>();
        for(int i=0;i < groups.length(); i++){
            GroupItem groupItem = new GroupItem(groups.getJSONObject(i));
            groupsMap.put(groupItem.getId(), groupItem);
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

    public static ArrayMap<String, RecommendedPlaylist>
    createRecommendedPlaylistsMap(JSONArray recommendedPlaylists,
                                  ArrayMap<String,PlaylistItem> playlists,
                                  ArrayMap<String,AudioItem> audios) throws JSONException {

        ArrayMap<String, RecommendedPlaylist> playlistsMap = new ArrayMap<>();
        for(int i=0;i < recommendedPlaylists.length(); i++){
            JSONObject jsonObject = recommendedPlaylists.getJSONObject(i);
            String id = jsonObject.getString("owner_id")+"_"+jsonObject.getString("id");

            PlaylistItem playlistItem = playlists.get(id);
            RecommendedPlaylist recommendedPlaylist = new RecommendedPlaylist(jsonObject, playlistItem);

            JSONArray ja_audios = jsonObject.getJSONArray("audios");

            ArrayList<ItemDataHolder> audioWraps = new ArrayList<>();
            for (int j=0; j<ja_audios.length();j++){
                String ids = ja_audios.getString(j);
                DataItemWrap<ItemDataHolder, PlaylistItem> dataItemWrap =
                        new DataItemWrap<ItemDataHolder, PlaylistItem>(audios.get(ids), recommendedPlaylist) {
                            @Override
                            public int getViewHolderType() {
                                return TYPE_WRAPPED_AUDIO;
                            }
                        };
                audioWraps.add(dataItemWrap);
            }

            recommendedPlaylist.getAudioItems().addAll(audioWraps);

            playlistsMap.put(id, recommendedPlaylist);
        }
        return playlistsMap;
    }

    //TODO это вообще можно систематизировать

    public static ArrayMap<String, ArtistItem> createArtistsMap(JSONArray artists) throws JSONException {
        ArrayMap<String,ArtistItem> artistsMap = new ArrayMap<>();
        for(int i=0;i < artists.length(); i++){
            ArtistItem artistItem = new ArtistItem(artists.getJSONObject(i));
            artistsMap.put(artistItem.getId(),artistItem);
        }
        return artistsMap;
    }

    public static ArrayMap<String, CatalogLink> createLinksMap(JSONArray links) throws JSONException {
        ArrayMap<String, CatalogLink> linksMap = new ArrayMap<>();
        for(int i=0;i < links.length(); i++){
            CatalogLink catalogLink = new CatalogLink(links.getJSONObject(i));
            linksMap.put(catalogLink.getId(),catalogLink);
        }
        return linksMap;
    }

    public static ArrayMap<String, CatalogBanner> createCatalogBannersMap(JSONArray banners) throws JSONException {
        ArrayMap<String, CatalogBanner> bannersMap = new ArrayMap<>();
        for(int i=0;i < banners.length(); i++){
            CatalogBanner catalogBanner = new CatalogBanner(banners.getJSONObject(i));
            bannersMap.put(catalogBanner.getId(),catalogBanner);
        }
        return bannersMap;
    }

    public static ArrayMap<String, CatalogSuggestion> createCatalogSuggestionsMap(JSONArray banners) throws JSONException {
        ArrayMap<String, CatalogSuggestion> suggestionsMap = new ArrayMap<>();
        for(int i=0;i < banners.length(); i++){
            CatalogSuggestion catalogSuggestion = new CatalogSuggestion(banners.getJSONObject(i));
            suggestionsMap.put(catalogSuggestion.getId(),catalogSuggestion);
        }
        return suggestionsMap;
    }
}
